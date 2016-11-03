package test;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class NewLexer extends JPanel {

	private JTextArea originalTxt;
	private JTextArea resultTxt;
	private JPanel buttonPanel;
	private JLabel label_1;
	private JLabel label_2;
	private JTextField textField_1;
	private JTextField textField_2;
	private JFileChooser chooser_1;
	private JFileChooser chooser_2;
	private JScrollPane btnPane;
	private JScrollPane originPane;
	private JScrollPane resultPane;
	private JSplitPane splitPane;
	private JSplitPane rightSplitPane;
	static final Dimension minimumSize = new Dimension(300, 200);
	private String readPath_1;// FA转换表存储路径
	private String readPath_2;// 测试1用例存储路径
	private StringBuffer buffer = new StringBuffer(); // 缓冲区
	// private char ch;
	// private int i;
	public static String code = "";
	public static Map<String, Integer> symbol = new HashMap<String, Integer>();// =new
																				// HashMap<String,int>;
	public static int symbol_pos = 0;
	private final String keyWords[] = { "abstract", "boolean", "break", "byte", "case", "catch", "char", "class",
			"continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "if",
			"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
			"protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw",
			"throws", "transient", "try", "void", "volatile", "while", "strictfp", "enum", "goto", "const", "assert" }; // 关键字数组
	// private final char operators[] = { '+', '-', '*', '/', '=', '>', '<',
	// '&', '|', '!' }; // 运算符数组
	// private final char separators[] = { ',', ';', '{', '}', '(', ')', '[',
	// ']', '_', ':', '.', '"', '\\' }; // 界符数组
	public static char operators[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~', '^', '!', '(', ')', '[', ']', '{',
			'}', '%', ';', ',', '#', '.' };
	public static String digitDFA[] = { "#", "#d.#e##", "###d###", "###de##", "#####-d", "######d", "######d" }; // 数字DFA
	public static String stConDFA[] = { "#\\d#", "##a#", "#\\d\"", "####" };
	public static String noteDFA[] = { "#", "##*##", "##c*#", "##c*/", "#####" };//注释DFA

	public NewLexer() {
		super(new GridLayout(1, 0));
		resultTxt = new JTextArea();
		originalTxt = new JTextArea();
		buildUpScrollGUI();
	}

	private void buildUpScrollGUI() {
		// Setup buttonPanel, which contains all buttons and
		// will be used in btnPane below
		setUpButtonPanel();
		btnPane = new JScrollPane(buttonPanel);
		btnPane.setMinimumSize(minimumSize);

		resultPane = new JScrollPane(resultTxt);
		originPane = new JScrollPane(originalTxt);

		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane.setLeftComponent(originPane);
		rightSplitPane.setRightComponent(resultPane);
		rightSplitPane.setDividerLocation(333);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(btnPane);
		splitPane.setRightComponent(rightSplitPane);

		splitPane.setDividerLocation(333);
		splitPane.setPreferredSize(new Dimension(1000, 350));

		add(splitPane);
		setSize(new Dimension(1000, 400));
		setVisible(true);
	}

	private void setUpButtonPanel() {
		label_1 = new JLabel("Please choose the FA table:");
		label_2 = new JLabel("Please choose the code:");
		textField_1 = new JTextField();
		textField_2 = new JTextField();
		textField_1.setColumns(10);
		textField_2.setColumns(10);
		textField_1.setEditable(false);
		textField_2.setEditable(false);
		resultTxt.setEditable(false);

		// Create buttons
		JButton chooseButton_1 = new JButton("Choose_1");
		JButton chooseButton_2 = new JButton("Choose_2");
		JButton saveButton = new JButton("Save");
		JButton exitButton = new JButton("Exit");
		JButton compileButton = new JButton("Compile");

		ButtonListener btnListener = new ButtonListener();
		chooseButton_1.addActionListener(btnListener);
		chooseButton_2.addActionListener(btnListener);
		exitButton.addActionListener(btnListener);
		saveButton.addActionListener(btnListener);
		compileButton.addActionListener(btnListener);

		buttonPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		buttonPanel.setLayout(gridbag);
		GridBagConstraints gbc = new GridBagConstraints();
		buttonPanel.add(label_1);
		buttonPanel.add(label_2);
		buttonPanel.add(textField_1);
		buttonPanel.add(textField_2);
		buttonPanel.add(chooseButton_1);
		buttonPanel.add(chooseButton_2);
		buttonPanel.add(exitButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(compileButton);

		gbc.insets.top = 5;
		gbc.insets.bottom = 5;
		gbc.insets.left = 5;
		gbc.insets.right = 5;

		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gridbag.setConstraints(label_1, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gridbag.setConstraints(textField_1, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gridbag.setConstraints(chooseButton_1, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gridbag.setConstraints(label_2, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gridbag.setConstraints(textField_2, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 3;
		gridbag.setConstraints(chooseButton_2, gbc);

		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gridbag.setConstraints(saveButton, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 4;
		gridbag.setConstraints(compileButton, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gridbag.setConstraints(exitButton, gbc);

	}

	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			chooser_1 = new JFileChooser();
			FileNameExtensionFilter filter_1 = new FileNameExtensionFilter("Text Files", "txt");
			chooser_1.setFileFilter(filter_1);
			chooser_2 = new JFileChooser();
			FileNameExtensionFilter filter_2 = new FileNameExtensionFilter("Text Files", "txt");
			chooser_2.setFileFilter(filter_2);
			// String searchResult = null;
			if (e.getActionCommand().equals("Compile")) {
				compiler(originalTxt, resultTxt);
			}
			if (e.getActionCommand().equals("Exit")) {
				System.exit(1);
			}
			if (e.getActionCommand().equals("Save")) {
				FileUtil.save(textField_2.getText(), originalTxt);
			}
			if (e.getActionCommand().equals("Choose_1")) {
				// i = 0;
				int index_1 = chooser_1.showOpenDialog(null);
				chooser_1.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser_1.setMultiSelectionEnabled(false);
				chooser_1.setAcceptAllFileFilterUsed(false);
				if (index_1 == JFileChooser.APPROVE_OPTION) {
					// 把获取到的文件的绝对路径显示在文本编辑框中
					textField_1.setText(chooser_1.getSelectedFile().getAbsolutePath());
					readPath_1 = textField_1.getText();
				}
				resultTxt.setText(null);
				FileUtil.show(readPath_1, resultTxt);
			}
			if (e.getActionCommand().equals("Choose_2")) {
				// i = 0;
				int index_2 = chooser_2.showOpenDialog(null);
				chooser_2.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser_2.setMultiSelectionEnabled(false);
				chooser_2.setAcceptAllFileFilterUsed(false);
				if (index_2 == JFileChooser.APPROVE_OPTION) {
					// 把获取到的文件的绝对路径显示在文本编辑框中
					textField_2.setText(chooser_2.getSelectedFile().getAbsolutePath());
					readPath_2 = textField_2.getText();
				}
				originalTxt.setText(null);
				FileUtil.show(readPath_2, originalTxt);
				FileUtil.read(readPath_2, buffer);
			}
		}
	} // End of class ButtonListener

	private void compiler(JTextArea originalTxt, JTextArea resultTxt) {
		resultTxt.setText(null);
		String[] lineString = originalTxt.getText().split("\n");
		int cnt = 0;
		while (cnt < lineString.length) {
			if (cnt == 0) {
				resultTxt.append("line " + (cnt + 1) + ":\r\n");
			} else {
				resultTxt.append("\r\nline " + (cnt + 1) + ":\r\n");
			}

			if (lineString[cnt].equals("") || lineString[cnt].equals(" ") || lineString[cnt].equals("  ")
					|| lineString[cnt].equals("\r\n")) {
				resultTxt.append("空行\r\n");
			} else {
				char[] strLine = lineString[cnt].toCharArray();
				for (int i = 0; i < strLine.length; i++) {
					char ch = strLine[i];
					String token = "";
					if (isAlpha(ch)) {// ************判断关键字和标识符************
						do {
							token += ch;
							i++;
							if (i >= strLine.length)
								break;
							ch = strLine[i];
						} while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));
						--i; // 指针回退
						if (isKeyWord(token.toString())) {// 是关键字
							resultTxt.append(String.format("%-15s\t<%s,__>\n", token, token));
						} else {// 是标识符
							if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) {
								symbol.put(token, symbol_pos);
								resultTxt.append(String.format("%-15s\t<标识符,%s>\n", token, token));
								symbol_pos++;
							} else {
								resultTxt.append(String.format("%-15s\t<标识符,%s>\n", token, token));
							}
						}
						token = "";
					} else if (isDigit(ch)) {// ************判断数字常量************
						int s = 1;
						Boolean isfloat = false;
						while (ch != '\0' && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-')) {
							if (ch == '.' || ch == 'e')
								isfloat = true;
							int k;
							for (k = 1; k <= 6; k++) {
								char tmpstr[] = digitDFA[s].toCharArray();
								if (ch != '#' && 1 == in_digitDFA(ch, tmpstr[k])) {
									token += ch;
									s = k;
									break;
								}
							}
							if (k > 6)
								break;
							i++;
							if (i >= strLine.length)
								break;
							ch = strLine[i];
						}
						// if(ch) --i; // 指针回退
						Boolean haveMistake = false;
						if (s == 2 || s == 4 || s == 5) {
							haveMistake = true;
						} else { // 1,3,6
							if (!isOp(ch) || ch == '.')
								haveMistake = true;
						}
						if (haveMistake) { // 错误处理
							while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') // 一直到“可分割”的字符结束
							{
								token += ch;
								i++;
								if (i >= strLine.length)
									break;
								ch = strLine[i];
							}
							resultTxt.append(String.format("%-15s\tERROR：请确保实常数输入正确\n", token));
						} else {
							if (isfloat) {
								resultTxt.append(String.format("%-15s\t<数字常量,%s>\n", token, token));
							} else {
								resultTxt.append(String.format("%-15s\t<数字常量,%s>\n", token, token));
							}
						}
						--i;
						token = "";
					} else if (ch == '\'') {// ************识别字符常量,类似处理字符串常量************
						int s = 0;
						Boolean haveMistake = false;
						String token1 = "";
						token1 += ch;
						while (s != 3) {
							i++;
							if (i >= strLine.length)
								break;
							ch = strLine[i];
							if (ch == '\0') {
								haveMistake = true;
								break;
							}
							for (int k = 0; k < 4; k++) {
								char tmpstr[] = stConDFA[s].toCharArray();
								if (in_sinStConDFA(ch, tmpstr[k])) {
									token1 += ch; // 为输出
									if (k == 2 && s == 1) {
										if (isEsSt(ch)) // 是转义字符
											token = token + '\\' + ch;
										else
											token += ch;
									} else if (k != 3 && k != 1)
										token += ch;
									s = k;
									break;
								}
							}
						}
						if (haveMistake) {
							resultTxt.append(String.format("%-15s\tERROR：字符常量引号不封闭\n", token1));
							--i;
						} else {
							if (token.length() == 1) {
								resultTxt.append(String.format("%-15s\t<字符常量,%s>\n", token1, token));
							} else if (token.length() == 2) {
								if (isEsSt(token.charAt(1)) && token.charAt(0) == '\\') {
									resultTxt.append(String.format("%-15s\t<字符常量,%s>\n", token1, token));
								}
							}
						}
						token = "";
					} else if (ch == '"') {// 处理字符串常量的
						String token1 = "";
						token1 += ch;
						int s = 0;
						Boolean haveMistake = false;
						while (s != 3) {
							i++;
							if (i >= strLine.length - 1) {
								haveMistake = true;
								break;
							}

							ch = strLine[i];
							if (ch == '\0') {
								haveMistake = true;
								break;
							}
							for (int k = 0; k < 4; k++) {
								char tmpstr[] = stConDFA[s].toCharArray();
								if (in_stConDFA(ch, tmpstr[k])) {
									token1 += ch;
									if (k == 2 && s == 1) {
										if (isEsSt(ch)) // 是转义字符
											token = token + '\\' + ch;
										else
											token += ch;
									} else if (k != 3 && k != 1)
										token += ch;
									s = k;
									break;
								}
							}
						}
						if (haveMistake) {
							resultTxt.append(String.format("%-15s\tERROR:字符串常量引号不封闭\n", token1));
							--i;
						} else {
							resultTxt.append(String.format("%-15s\t<字符串常量,%s>\n", token1, token));
						}
						token = "";
					} else if (isOp(ch)) {// 运算符，界符
						token += ch;
						if (isPlusEqu(ch)) // 后面可以用一个"="
						{
							i++;
							if (i >= strLine.length)
								break;
							ch = strLine[i];
							if (ch == '=')
								token += ch;
							else {
								if (isPlusSame(strLine[i - 1]) && ch == strLine[i - 1])
									token += ch; // 后面可以用一个和自己一样的
								else {
									--i;
								}
							}
						}
						resultTxt.append(String.format("%-15s\t<%s,__>\n", token, token));
						token = "";
					} else if (ch == '/') {// 注释+除号: 注释只要识别出来就好。
						token += ch;
						i++;
						if (i >= strLine.length)
							break;
						ch = strLine[i];

						if (ch != '*' && ch != '/') // 除号处理
						{
							if (ch == '=')
								token += ch; // /=
							else {
								--i; // 指针回退 // /
							}
							resultTxt.append(String.format("%-15s\t<%s,__>\n", token, token));
							token = "";
						} else {// 注释可能是‘//’也可能是‘/*’
							Boolean haveMistake = false;
							if (ch == '*') {
								token += ch; // ch == '*'
								int s = 2;

								while (s != 4) {
									i++;
									if (i >= strLine.length)
										break;
									ch = strLine[i]; // 注意判断溢出!
									if (ch == '\0') {
										haveMistake = true;
										break;
									}
									for (int k = 2; k <= 4; k++) {
										char tmpstr[] = noteDFA[s].toCharArray();
										if (1 == in_noteDFA(ch, tmpstr[k], s)) {
											token += ch;
											s = k;
											break;
										}
									}
								}
							} else if (ch == '/') // 这里就不用状态转移了...
							{
								int index = lineString[cnt].lastIndexOf("//");
								String tmpstr = lineString[cnt].substring(index);
								int tmpint = tmpstr.length();
								for (int k = 0; k < tmpint; k++) {
									i++;
								}
								token = tmpstr;
							}
							resultTxt.append(String.format("%-15s\t", token));
							if (haveMistake) {
								resultTxt.append("ERROR:注释没有封闭\n");
								--i;
							} else {
								resultTxt.append(String.format("(注释：%s)\n", token));
							}
							token = "";
						}
					} else {// 一些很奇怪的字符
						if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
							resultTxt.append(String.format("%-15c\t ERROR:存在不合法字符\n", ch));
						}
					}
				}
			}
			cnt++;
		}
	}

	// 是否为字母
	public boolean isAlpha(char ch) {
		return Character.isLetter(ch);
	}

	// 是否为数字
	public boolean isDigit(char ch) {
		return Character.isDigit(ch);
	}

	// 是否为关键字
	public boolean isKeyWord(String s) {
		for (int i = 0; i < keyWords.length; i++) {
			if (keyWords[i].equals(s))
				return true;
		}
		return false;
	}

	// 是否为分隔符
	// public boolean isSep(char ch) {
	// for (int i = 0; i < separators.length; i++) {
	// if (ch == separators[i])
	// return true;
	// }
	// return false;
	// }

	// 判断是否是运算符
	public Boolean isOp(char ch) {
		for (int i = 0; i < 22; i++)
			if (ch == operators[i]) {
				return true;
			}
		return false;
	}

	public int in_digitDFA(char ch, char dD) {
		if (dD == 'd') {
			if (isDigit(ch))
				return 1;
			else
				return 0;
		}
		return (ch == dD) ? 1 : 0;
	}

	public Boolean in_stConDFA(char ch, char key) {
		if (key == 'a')
			return true;
		if (key == '\\')
			return ch == key;
		if (key == '"')
			return ch == key;
		if (key == 'd')
			return ch != '\\' && ch != '"';
		return false;
	}

	public Boolean in_sinStConDFA(char ch, char key) {
		if (key == 'a')
			return true;
		if (key == '\\')
			return ch == key;
		if (key == '"')
			return ch == '\'';
		if (key == 'd')
			return ch != '\\' && ch != '\'';
		return false;
	}

	public Boolean isPlusEqu(char ch) // 运算符后可加等于
	{
		return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>' || ch == '<' || ch == '&'
				|| ch == '|' || ch == '^';
	}

	public Boolean isPlusSame(char ch) // 可以连续两个运算符一样
	{
		return ch == '+' || ch == '-' || ch == '&' || ch == '|';
	}

	public Boolean isEsSt(char ch) { // 转义字符
		return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't' || ch == 'v' || ch == '?'
				|| ch == '0';
	}

	public int in_noteDFA(char ch, char nD, int s) {
		if (s == 2) {
			if (nD == 'c') {
				if (ch != '*')
					return 1;
				else
					return 0;
			}
		}
		if (s == 3) {
			if (nD == 'c') {
				if (ch != '*' && ch != '/')
					return 1;
				else
					return 0;
			}
		}
		return (ch == nD) ? 1 : 0;
	}

	static public void main(String argv[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JFrame frame = new JFrame("Lexer By Shawn Zeng");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				NewLexer newLexer = new NewLexer();
				newLexer.setOpaque(true);
				frame.setContentPane(newLexer);
				frame.pack();
				frame.setVisible(true);
				frame.setLocationRelativeTo(null);
			}
		});
	}
}
