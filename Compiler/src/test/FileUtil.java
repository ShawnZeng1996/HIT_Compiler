package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JTextArea;

public class FileUtil {

	public static void show(String path, JTextArea textArea) {
		try {
			FileReader fReader = new FileReader(path);// 需要读取的文件路径
			BufferedReader bReader = new BufferedReader(fReader);
			String theRule = "";
			String s = null;
			while ((s = bReader.readLine()) != null) {// 如果当前行不为空
				theRule = theRule + s + "\r\n";
			}
			textArea.append(theRule);
			bReader.close();// 关闭BufferReader流
			fReader.close();// 关闭文件流
		} catch (Exception e) {
			System.out.println("指定文件不存在");// 处理异常
		}
	}

	public static void read(String path, StringBuffer buffer) {
		try {
			FileReader fReader = new FileReader(path);// 需要读取的文件路径
			BufferedReader bReader = new BufferedReader(fReader);
			String s = null;
			while ((s = bReader.readLine()) != null) {// 如果当前行不为空
				buffer.append(s);
			}
			bReader.close();// 关闭BufferReader流
			fReader.close();// 关闭文件流
		} catch (Exception e) {
			System.out.println("指定文件不存在");// 处理异常
		}
	}

	public static void save(String path, JTextArea textArea) {
		String[] lineString = textArea.getText().split("\r\n");
		int length = lineString.length;
		int i;
		File file = new File(path);
		try {
			FileWriter fWriter = new FileWriter(file.getAbsolutePath());
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (i = 0; i < length; i++) {
				bWriter.write(lineString[i] + "\r\n");
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readFA(String path, JTextArea textArea) {
		try {
			FileReader fReader = new FileReader(path);// 需要读取的文件路径
			BufferedReader bReader = new BufferedReader(fReader);
			String theRule = "";
			String s = null;
//			String digitDFA; // 数字DFA
//			String stConDFA;
//			String noteDFA;
			while ((s = bReader.readLine()) != null) {// 如果当前行不为空
				theRule = theRule + s + "\r\n";
			}
			textArea.append(theRule);
			bReader.close();// 关闭BufferReader流
			fReader.close();// 关闭文件流
		} catch (Exception e) {
			System.out.println("指定文件不存在");// 处理异常
		}
	}
}
