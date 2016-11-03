package com.zs;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.DefaultTableModel;

public class AnalyseList {

	// 成员变量,产生式集，终结符集，非终结符集
	ArrayList<Production> productions;
	ArrayList<String> terminals;
	ArrayList<String> nonterminals;
	HashMap<String, ArrayList<String>> firsts;
	HashMap<String, ArrayList<String>> follows;
	String left;
	ArrayList<String> right;

	public AnalyseList() {
		productions = new ArrayList<Production>();
		terminals = new ArrayList<String>();
		nonterminals = new ArrayList<String>();
		firsts = new HashMap<String, ArrayList<String>>();
		follows = new HashMap<String, ArrayList<String>>();
		setProductions();
		setNonTerminals();
		setTerminals();
	}

	public AnalyseList(DefaultTableModel tbmodel_lex_result, DefaultTableModel tbmodel_gra_first,
			DefaultTableModel tbmodel_gra_follow, DefaultTableModel tbmodel_gra_predict) {
		productions = new ArrayList<Production>();
		terminals = new ArrayList<String>();
		nonterminals = new ArrayList<String>();
		firsts = new HashMap<String, ArrayList<String>>();
		follows = new HashMap<String, ArrayList<String>>();
		setProductions();
		setNonTerminals();
		setTerminals();
		getFirst(tbmodel_gra_first);
		getFollow(tbmodel_gra_follow);
		getSelect();
		Predict();
		preTableShow(tbmodel_gra_predict);

	}

	// 从文件中读取产生式
	public void setProductions() {
		try {
			File file = new File("grammar.txt");
			RandomAccessFile randomfile = new RandomAccessFile(file, "r");
			String line;
			String left;
			String right;
			Production production;
			while ((line = randomfile.readLine()) != null) {

				left = line.split("->")[0].trim();
				right = line.split("->")[1].trim();
				production = new Production(left, right.split(" "));
				productions.add(production);
			}
			randomfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获得非终结符集
	public void setNonTerminals() {
		try {
			File file = new File("grammar.txt");
			RandomAccessFile randomfile = new RandomAccessFile(file, "r");
			String line;
			String left;
			while ((line = randomfile.readLine()) != null) {
				left = line.split("->")[0].trim();
				if (nonterminals.contains(left)) {
					continue;
				} else {
					nonterminals.add(left);
				}
			}
			randomfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获得终结符集,依赖于获得产生式函数
	public void setTerminals() {
		// 遍历所有的产生式
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			rights = productions.get(i).returnRights();
			// 从右侧寻找终结符
			for (int j = 0; j < rights.length; j++) {
				if (nonterminals.contains(rights[j]) || rights[j].equals("$")) {
					continue;
				} else {
					terminals.add(rights[j]);
				}
			}
		}
	}

	// 获取First集
	public void getFirst(DefaultTableModel tbmodel_gra_first) {
		// 终结符全部求出first集
		ArrayList<String> first;
		for (int i = 0; i < terminals.size(); i++) {
			first = new ArrayList<String>();
			first.add(terminals.get(i));
			firsts.put(terminals.get(i), first);
		}
		// 给所有非终结符注册一下
		for (int i = 0; i < nonterminals.size(); i++) {
			first = new ArrayList<String>();
			firsts.put(nonterminals.get(i), first);
		}

		boolean flag;
		while (true) {
			flag = true;
			String left;
			String right;
			String[] rights;
			for (int i = 0; i < productions.size(); i++) {
				left = productions.get(i).returnLeft();
				rights = productions.get(i).returnRights();
				for (int j = 0; j < rights.length; j++) {
					right = rights[j];
					// right是否存在，遇到空怎么办
					if (!right.equals("$")) {
						for (int l = 0; l < firsts.get(right).size(); l++) {
							if (firsts.get(left).contains(firsts.get(right).get(l))) {
								continue;
							} else {
								firsts.get(left).add(firsts.get(right).get(l));
								flag = false;
							}
						}
					}
					if (isCanBeNull(right)) {
						continue;
					} else {
						break;
					}
				}
			}
			if (flag == true) {
				break;
			}

		}
		// 非终结符的first集
		// Iterator iterator=firsts.entrySet().iterator();
		// while (iterator.hasNext()) {
		// HashMap.Entry entry=(HashMap.Entry) iterator.next();
		//
		// }
		for (HashMap.Entry<String, ArrayList<String>> entry : firsts.entrySet()) {
			left = entry.getKey();
			right = entry.getValue();
			tbmodel_gra_first.addRow(new String[] { left, trimFirstAndLastChar(right.toString()) });
		}
	}

	// 获得Follow集
	public void getFollow(DefaultTableModel tbmodel_gra_follow) {
		// 所有非终结符的follow集初始化一下
		ArrayList<String> follow;
		for (int i = 0; i < nonterminals.size(); i++) {
			follow = new ArrayList<String>();
			follows.put(nonterminals.get(i), follow);
		}
		// 将#加入到follow(S)中
		follows.get("S").add("#");
		boolean flag;
		boolean fab;
		while (true) {
			flag = true;
			// 循环
			for (int i = 0; i < productions.size(); i++) {
				String left;
				String right;
				String[] rights;
				rights = productions.get(i).returnRights();
				for (int j = 0; j < rights.length; j++) {
					right = rights[j];

					// 非终结符
					if (nonterminals.contains(right)) {
						fab = true;
						for (int k = j + 1; k < rights.length; k++) {

							// 查找first集
							for (int v = 0; v < firsts.get(rights[k]).size(); v++) {
								// 将后一个元素的first集加入到前一个元素的follow集中
								if (follows.get(right).contains(firsts.get(rights[k]).get(v))) {
									continue;
								} else {
									follows.get(right).add(firsts.get(rights[k]).get(v));
									flag = false;
								}
							}
							if (isCanBeNull(rights[k])) {
								continue;
							} else {
								fab = false;
								break;
							}
						}
						if (fab) {
							left = productions.get(i).returnLeft();
							for (int p = 0; p < follows.get(left).size(); p++) {
								if (follows.get(right).contains(follows.get(left).get(p))) {
									continue;
								} else {
									follows.get(right).add(follows.get(left).get(p));
									flag = false;
								}
							}
						}
					}

				}
			}
			if (flag == true) {
				break;
			}
		}

		// 清除follow集中的#
		String left;
		for (int j = 0; j < nonterminals.size(); j++) {
			left = nonterminals.get(j);
			for (int v = 0; v < follows.get(left).size(); v++) {
				if (follows.get(left).get(v).equals("#"))
					follows.get(left).remove(v);
			}
		}

		for (HashMap.Entry<String, ArrayList<String>> entry : follows.entrySet()) {
			left = entry.getKey();
			right = entry.getValue();
			tbmodel_gra_follow.addRow(new String[] { left, trimFirstAndLastChar(right.toString()) });
		}
	}

	// 获取Select集
	public void getSelect() {
		String left;
		String right;
		String[] rights;
		ArrayList<String> follow = new ArrayList<String>();
		ArrayList<String> first = new ArrayList<String>();

		for (int i = 0; i < productions.size(); i++) {
			left = productions.get(i).returnLeft();
			rights = productions.get(i).returnRights();
			if (rights[0].equals("$")) {
				// select(i) = follow(A)
				follow = follows.get(left);
				for (int j = 0; j < follow.size(); j++) {
					if (productions.get(i).select.contains(follow.get(j))) {
						continue;
					} else {
						productions.get(i).select.add(follow.get(j));
					}
				}
			} else {
				boolean flag = true;
				for (int j = 0; j < rights.length; j++) {
					right = rights[j];
					first = firsts.get(right);
					for (int v = 0; v < first.size(); v++) {
						if (productions.get(i).select.contains(first.get(v))) {
							continue;
						} else {
							productions.get(i).select.add(first.get(v));
						}
					}
					if (isCanBeNull(right)) {
						continue;
					} else {
						flag = false;
						break;
					}
				}
				if (flag) {
					follow = follows.get(left);
					for (int j = 0; j < follow.size(); j++) {
						if (productions.get(i).select.contains(follow.get(j))) {
							continue;
						} else {
							// 刚刚这里出现了一个问题，已经被解决啦
							productions.get(i).select.add(follow.get(j));
						}
					}
				}
			}
		}
	}

	// 生成产生式
	public void Predict() {
		Production production;
		String line;
		String[] rights;
		try {
			File file = new File("predictldy.txt");
			RandomAccessFile randomfile = new RandomAccessFile(file, "rw");
			for (int i = 0; i < productions.size(); i++) {
				production = productions.get(i);
				for (int j = 0; j < production.select.size(); j++) {
					line = production.returnLeft() + "#" + production.select.get(j) + " ->";
					rights = production.returnRights();
					for (int v = 0; v < rights.length; v++) {
						line = line + " " + rights[v];
					}
					line = line + "\n";
					// 写入文件
					randomfile.writeBytes(line);
				}
			}
			randomfile.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// 判断是否是终结符
	public boolean isTerminal(String symbol) {
		if (terminals.contains(symbol))
			return true;
		else {
			return false;
		}
	}

	// 判断是否产生$
	public boolean isCanBeNull(String symbol) {
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			// 找到产生式
			if (productions.get(i).returnLeft().equals(symbol)) {
				rights = productions.get(i).returnRights();
				if (rights[0].equals("$")) {
					return true;
				}
			}
		}
		return false;
	}

	// 去除首尾字符
	public static String trimFirstAndLastChar(String str) {
		str = str.substring(1, str.length() - 1);
		return str;
	}

	// 预测表生成
	public void preTableShow(DefaultTableModel tbmodel_gra_predict) {
		int rowCount = nonterminals.size();
		int colCount = tbmodel_gra_predict.getColumnCount();
		tbmodel_gra_predict.setRowCount(rowCount);
		tbmodel_gra_predict.setColumnCount(colCount);
		for (int i = 0; i < rowCount; i++) {
			tbmodel_gra_predict.setValueAt(nonterminals.get(i), i, 0);
		}

		String table[][] = new String[rowCount + 1][colCount];
		// tbmodel_gra_predict.setValueAt(rowCount + "," + colCount, rowCount -
		// 1, colCount - 1);

		table[0][0] = "?";
		for (int i = 1; i < rowCount + 1; i++) {
			table[i][0] = nonterminals.get(i - 1);
		}
		for (int i = 1; i < colCount; i++) {
			table[0][i] = tbmodel_gra_predict.getColumnName(i);
		}
		for (int i = 0; i < rowCount + 1; i++) {
			for (int j = 0; j < colCount; j++) {
				if (table[i][j] == null) {
					table[i][j] = "";
				}
				// System.out.print(table[i][j]);
			}
			// System.out.println();
		}

		Production production;
		String right;
		String[] rights;
		for (int i = 1; i < rowCount + 1; i++) {
			for (int j = 0; j < productions.size(); j++) {
				production = productions.get(j);
				if (table[i][0].equals(production.returnLeft())) {
					for (int j2 = 0; j2 < production.select.size(); j2++) {
						right = "";
						for (int k = 1; k < colCount; k++) {
							if ((table[i][0] + "#" + table[0][k])
									.equals(production.returnLeft() + "#" + production.select.get(j2))) {
								rights = production.returnRights();
								for (int v = 0; v < rights.length; v++) {
									right = right + " " + rights[v];
								}
								tbmodel_gra_predict.setValueAt(production.returnLeft() + " -> " + right, i - 1, k);
								// System.out.println(i+","+k);
							}
						}
					}
				}
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String[] getPredictHeader() {
		String temp[] = { "预测分析表" };
		String mdzz;
		String zz;
		for (int i = 0; i < terminals.size(); i++) {
			mdzz = terminals.get(i);
			temp = Arrays.copyOf(temp, temp.length + 1);
			temp[i + 1] = mdzz;
		}
		Set set = new TreeSet();
		for (int i = 0; i < temp.length; i++) {
			set.add(temp[i]);
		}
		temp = (String[]) set.toArray(new String[0]);
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].equals("预测分析表")) {
				zz = temp[0];
				temp[0] = "预测分析表";
				temp[i] = zz;
				break;
			}
		}
		return temp;
	}

}