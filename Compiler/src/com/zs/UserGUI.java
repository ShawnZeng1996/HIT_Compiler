package com.zs;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class UserGUI implements ActionListener {

	private JFrame frame;
	private JMenuBar main_menu_bar;
	private JMenu menu_file;
	private JMenu menu_run;
	private JMenuItem menu_clear;
	private JMenuItem file_open;
	private JMenuItem file_save;
	private JMenuItem exit;
	private JMenuItem run_lex;
	private JMenuItem run_gra;
	private JLabel lb_lex_result;
	private JLabel lb_gra_first;
	private JLabel lb_gra_follow;
	private JLabel lb_gra_predict;
	private JLabel lb_text_edit;
	private JLabel lb_gra_result;
	private JTextArea ta_input;
	private JTextArea gra_output;
	private JScrollPane scrollpane_input;
	private JTable tb_lex_result;
	private JTable tb_gra_first;
	private JTable tb_gra_follow;
	private JTable tb_gra_predict;
	private DefaultTableModel tbmodel_lex_result;
	private DefaultTableModel tbmodel_gra_first;
	private DefaultTableModel tbmodel_gra_follow;
	private DefaultTableModel tbmodel_gra_predict;
	private JScrollPane scrollpane_lex_result;
	private JScrollPane scrollpane_gra_result;
	private JScrollPane scrollpane_gra_first;
	private JScrollPane scrollpane_gra_follow;
	private JScrollPane scrollpane_gra_predict;
	private JScrollPane scrollpane_gra_tree;
	private String fileName;
	private String[] temp;
	private JTree gra_tree;
	private DefaultMutableTreeNode node;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserGUI window = new UserGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserGUI() {
		AnalyseList initA = new AnalyseList();
		temp = initA.getPredictHeader();
		initialize();
		@SuppressWarnings("unused")
		AnalyseList analyse = new AnalyseList(tbmodel_lex_result, tbmodel_gra_first, tbmodel_gra_follow,
				tbmodel_gra_predict);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Compiler");
		// frame.setBounds(100, 100, 450, 300);
		frame.setSize(1100, 755);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		main_menu_bar = new JMenuBar();
		menu_file = new JMenu("文件");
		menu_run = new JMenu("运行");

		file_open = new JMenuItem("打开");
		file_save = new JMenuItem("保存");
		exit = new JMenuItem("退出");
		file_open.addActionListener(this);
		file_save.addActionListener(this);
		exit.addActionListener(this);
		menu_file.add(file_open);
		menu_file.add(file_save);
		menu_file.add(exit);
		main_menu_bar.add(menu_file);
		menu_clear = new JMenuItem("清空内容");
		menu_clear.addActionListener(this);
		menu_run.add(menu_clear);
		run_lex = new JMenuItem("词法分析");
		run_lex.addActionListener(this);
		menu_run.add(run_lex);
		run_gra = new JMenuItem("语法分析");
		run_gra.addActionListener(this);
		menu_run.add(run_gra);
		main_menu_bar.add(menu_run);
		frame.setJMenuBar(main_menu_bar);
		frame.getContentPane().setLayout(null);

		lb_text_edit = new JLabel("文本编辑区:");
		frame.getContentPane().add(lb_text_edit);
		lb_text_edit.setBounds(10, 10, 70, 20);

		ta_input = new JTextArea();
		Font f = new Font(Font.MONOSPACED, Font.PLAIN, 13);
		ta_input.setFont(f);
		ta_input.setBackground(new Color(211, 211, 211));
		scrollpane_input = new JScrollPane(ta_input);
		frame.getContentPane().add(scrollpane_input);
		scrollpane_input.setBounds(10, 40, 400, 300);
		scrollpane_input.setRowHeaderView(new LineNumberHeaderView());

		lb_lex_result = new JLabel("词法分析结果:");
		frame.getContentPane().add(lb_lex_result);
		lb_lex_result.setBounds(450, 10, 90, 20);

		tbmodel_lex_result = new DefaultTableModel(null, new String[] { "单词", "token序列", "行数" });
		tb_lex_result = new JTable(tbmodel_lex_result);
		tb_lex_result.setEnabled(false);
		tbmodel_lex_result.setRowCount(1000);
		tb_lex_result.setBackground(new Color(203, 203, 203));
		scrollpane_lex_result = new JScrollPane(tb_lex_result);
		frame.getContentPane().add(scrollpane_lex_result);
		scrollpane_lex_result.setBounds(450, 40, 300, 300);

		lb_gra_result = new JLabel("语法分析结果：");
		frame.getContentPane().add(lb_gra_result);
		lb_gra_result.setBounds(790, 10, 100, 20);

		gra_output = new JTextArea();
		gra_output.setEditable(false);
		scrollpane_gra_result = new JScrollPane(gra_output);
		frame.getContentPane().add(scrollpane_gra_result);
		scrollpane_gra_result.setBounds(790, 40, 300, 100);
		gra_output.setBackground(new Color(211, 211, 211));

		lb_gra_first = new JLabel("FIRST集:");
		frame.getContentPane().add(lb_gra_first);
		lb_gra_first.setBounds(10, 350, 70, 20);

		tbmodel_gra_first = new DefaultTableModel(null, new String[] { "符号", "FIRST集" });
		tb_gra_first = new JTable(tbmodel_gra_first);
		tb_gra_first.setEnabled(false);
		tb_gra_first.setBackground(new Color(203, 203, 203));
		scrollpane_gra_first = new JScrollPane(tb_gra_first);
		frame.getContentPane().add(scrollpane_gra_first);
		scrollpane_gra_first.setBounds(10, 380, 200, 300);

		lb_gra_follow = new JLabel("FOLLOW集:");
		frame.getContentPane().add(lb_gra_follow);
		lb_gra_follow.setBounds(260, 350, 70, 20);

		tbmodel_gra_follow = new DefaultTableModel(null, new String[] { "非终结符", "FOLLOW集" });
		tb_gra_follow = new JTable(tbmodel_gra_follow);
		tb_gra_follow.setEnabled(false);
		tb_gra_follow.setBackground(new Color(203, 203, 203));
		scrollpane_gra_follow = new JScrollPane(tb_gra_follow);
		frame.getContentPane().add(scrollpane_gra_follow);
		scrollpane_gra_follow.setBounds(260, 380, 200, 300);

		lb_gra_predict = new JLabel("预测分析表:");
		frame.getContentPane().add(lb_gra_predict);
		lb_gra_predict.setBounds(500, 350, 70, 20);

		// 预测分析表
		tbmodel_gra_predict = new DefaultTableModel(null, temp);
		tb_gra_predict = new JTable(tbmodel_gra_predict);
		// TODO 测试用
		// tbmodel_gra_predict.addRow(temp);
		// Util.FitTableColumns(tb_gra_predict);
		tb_gra_predict.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		tb_gra_predict.getTableHeader().setReorderingAllowed(false);
		tb_gra_predict.setEnabled(false);
		tb_gra_predict.setBackground(new Color(203, 203, 203));

		scrollpane_gra_predict = new JScrollPane(tb_gra_predict);
		frame.getContentPane().add(scrollpane_gra_predict);
		scrollpane_gra_predict.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane_gra_predict.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane_gra_predict.setBounds(500, 380, 590, 300);

		node = new DefaultMutableTreeNode("Program");
		gra_tree = new JTree(node);
		scrollpane_gra_tree = new JScrollPane(gra_tree);
		gra_tree.setBackground(new Color(211, 211, 211));
		scrollpane_gra_tree.setBounds(790, 150, 300, 190);
		frame.getContentPane().add(scrollpane_gra_tree);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == file_open) {
			JFileChooser file_open_filechooser = new JFileChooser();
			file_open_filechooser.setCurrentDirectory(new File("."));
			file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = file_open_filechooser.showOpenDialog(frame.getContentPane());
			// 证明有选择
			if (result == JFileChooser.APPROVE_OPTION) {
				fileName = file_open_filechooser.getSelectedFile().getPath();
				// 读取文件，写到JTextArea里面
				Util.showFile(fileName, ta_input);
			}
		} else if (e.getSource() == file_save) {
			Util.saveFile(fileName, ta_input);
		} else if (e.getSource() == exit) {
			System.exit(1);
		} else if (e.getSource() == menu_clear) {
			Util.clearTable(tbmodel_lex_result, tb_lex_result);
			Util.clearTextArea(ta_input);
			Util.clearTextArea(gra_output);
		} else if (e.getSource() == run_lex) {
			Util.clearTable(tbmodel_lex_result, tb_lex_result);
			if (ta_input.getText().equals("")) {
				JOptionPane.showMessageDialog(frame.getContentPane(), "没有输入！", "提示", JOptionPane.ERROR_MESSAGE);
			} else {
				// 词法分析
				Lexer lexer = new Lexer(ta_input.getText(), tbmodel_lex_result);
				lexer.scannerAll();
			}
		} else if (e.getSource() == run_gra) {
			Util.clearTable(tbmodel_lex_result, tb_lex_result);
			if (ta_input.getText().equals("")) {
				JOptionPane.showMessageDialog(frame.getContentPane(), "没有输入！", "提示", JOptionPane.ERROR_MESSAGE);
			} else {
				// 词法分析
				Lexer lexer = new Lexer(ta_input.getText(), tbmodel_lex_result);
				lexer.scannerAll();
				// 获得结果的表
				ArrayList<String> lex_result_stack = lexer.get_Lex_Result();
				ArrayList<Integer> lex_resultRow_stack = lexer.get_Lex_ResultRow();
				ArrayList<HashMap<String, String>> lex_error_stack = lexer.get_Lex_Error();
				// 若是存在词法分析错误
				if (lex_error_stack.size() != 0) {
					JOptionPane.showMessageDialog(frame.getContentPane(), "词法分析阶段出现错误！", "提示",
							JOptionPane.ERROR_MESSAGE);
				} else {// 不存在词法分析错误
					// 语法分析
					Parse textParse = new Parse(lex_result_stack, lex_resultRow_stack, gra_output);
					System.out.println(lex_result_stack);
					DefaultMutableTreeNode nTreeNode = textParse.Parsing();
					node.add(nTreeNode);
					gra_tree.updateUI();
				}
			}
		}
	}

}
