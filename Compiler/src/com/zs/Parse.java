package com.zs;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

public class Parse {
	HashMap<String, String> predictmap;
	ArrayList<String> input_cache;
	ArrayList<Integer> input_cacheRow;
	ArrayList<String> deduce_str;
	JTextArea gra_output;
	int treeLength, n, temp, leftTop, top;
	DefaultMutableTreeNode tree[];
	// DefaultTableModel tbmodel_lex_result;

	public Parse(ArrayList<String> input_cache, ArrayList<Integer> lex_resultRow_stack, JTextArea gra_output) {
		predictmap = new HashMap<String, String>();
		this.input_cache = input_cache;
		this.input_cacheRow = lex_resultRow_stack;
		this.gra_output = gra_output;
		deduce_str = new ArrayList<String>();
		n = 0;
		treeLength = 10000;
		leftTop = 0;
		top = 0;
		tree = new DefaultMutableTreeNode[treeLength];
		getPredictMap();
	}

	// 句法分析
	public DefaultMutableTreeNode Parsing() {
		gra_output.setText("");
		// 初始符号压入栈
		deduce_str.add("S");
		setTreeNode(tree, n, "S(1)");
		n++;
		String right;
		String leftandinput;
		String process = "";

		while (deduce_str.size() > 0 && input_cache.size() > 0) {
			// 输入缓冲区与推导符号串第一个字符相等的话，删掉
			try {
				if (input_cache.get(0).equals(deduce_str.get(deduce_str.size() - 1))) {
					System.out.println(
							"-------------------------------------------------------------------------------------------------"
									+ deduce_str.get(deduce_str.size() - 1) + "-" + input_cache.get(0) + "跳过");

					setTreeNode(tree, n,
							deduce_str.get(deduce_str.size() - 1) + " (" + input_cacheRow.get(0) + ") " + n);

					tree[top].add(tree[n]);
					n++;
					top = leftTop;
					top++;
					leftTop = top;
					input_cache.remove(0);
					input_cacheRow.remove(0);
					deduce_str.remove(deduce_str.size() - 1);
					continue;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}

			// 匹配字符
			leftandinput = deduce_str.get(deduce_str.size() - 1) + "-" + input_cache.get(0);
			System.out.println(
					"-------------------------------------------------------------------------------------------------"
							+ leftandinput);
			// if(input_cache.get(0)==null)
			// {
			// leftandinput = leftandinput+"$";
			// }
			// 能够找到匹配的
			if ((right = predictmap.get(leftandinput)) != null) {

				// 输出产生式和推导过程
				process = "";
				for (int i = deduce_str.size() - 1; i > -1; i--) {
					process = process + deduce_str.get(i) + " ";
				}
				// tbmodel_lex_result
				// .addRow(new String[] { process,
				// deduce_str.get(deduce_str.size() - 1) + " -> " + right });

				// TODO

				System.out.println(
						String.format("%-60s\t", process) + deduce_str.get(deduce_str.size() - 1) + " -> " + right);
				String string[] = right.split(" ");

				for (int i = 0; i < string.length; i++) {
					if (!string[0].equals("$")) {
						setTreeNode(tree, n, string[i] + " (" + input_cacheRow.get(0) + ") " + n);
						tree[top].add(tree[n]);
						// System.out.print(string[i]+" ");

						if (i == 0) {
							temp = n;
						}
						if (i == string.length - 1) {
							leftTop = top;
							top = temp;
						}
						n++;
					} else if (string[0].equals("$")) {
						setTreeNode(tree, n, string[0] + " (" + input_cacheRow.get(0) + ") " + n);
						tree[top].add(tree[n]);
						n++;
						leftTop = top + 1;
						top++;
						break;
					}
				}

				System.out.println();
				// tree[n-1].add(tree[n]);

				// 删掉产生的字符，压入堆栈
				deduce_str.remove(deduce_str.size() - 1);
				if (right.equals("$")) {
					// 只弹不压
				} else {
					String[] arg = right.split(" ");
					for (int i = arg.length - 1; i > -1; i--) {
						// 反向压入堆栈
						deduce_str.add(arg[i]);
					}
				}

			}
			// 否则的话报错
			else {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + right);
				// 重新书写process
				process = "";
				for (int i = deduce_str.size() - 1; i > -1; i--) {
					process = process + deduce_str.get(i) + " ";
				}
				// tbmodel_lex_result.addRow(
				// new String[] { process, "ERROR! 无法识别的字符" + input_cache.get(0)
				// + "产生式" + leftandinput });

				// TODO
				System.out.println(String.format("%-60s\t", process) + "ERROR!  无法识别的字符:" + input_cache.get(0) + ",其产生式"
						+ leftandinput);
				gra_output.append("Error at Line [" + input_cacheRow.get(0) + "]:此行存在语法错误！\n");
				input_cache.remove(0);
				input_cacheRow.remove(0);
			}
		}

		// ---------------
		if (deduce_str.size() > 0 && input_cache.size() == 0) {
			String finalStr = deduce_str.toString().substring(1, deduce_str.toString().length() - 1);
			if (finalStr.equals("funcs")) {
				System.out.println(String.format("%-60s\t", finalStr) + "funcs -> $");
				System.out.println(
						"-------------------------------------------------------------------------------------------------"
								+ "funs-$");
				System.out.println(
						"-------------------------------------------------------------------------------------------------"
								+ "结束");
//				setTreeNode(tree, n, "$");
//				tree[top].add(tree[n]);n++;
			}

		}
		return tree[0];
	}

	// 获得预测分析表中的产生式以及对应的select集
	// 存储方式为键值对的形式
	public void getPredictMap() {
		String text_line;
		String left;
		String symbol;
		String right;
		try {
			// 初始化
			predictmap = new HashMap<String, String>();
			// 采用随机读取方式
			File file = new File("predictldy.txt");
			RandomAccessFile predictfile = new RandomAccessFile(file, "r");
			while ((text_line = predictfile.readLine()) != null) {
				left = text_line.split("#")[0];
				symbol = (text_line.split("#")[1]).split("->")[0].trim();
				right = (text_line.split("#")[1]).split("->")[1].trim();
				predictmap.put(left + "-" + symbol, right);

			}
			predictfile.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void setTreeNode(DefaultMutableTreeNode tree[], int n, String str) {
		if (n < tree.length) {
			tree[n] = new DefaultMutableTreeNode(str);
		}
	}
}