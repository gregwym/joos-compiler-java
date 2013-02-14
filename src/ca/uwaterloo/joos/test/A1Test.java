package ca.uwaterloo.joos.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.parser.LR1Parser;
import ca.uwaterloo.joos.scanner.Scanner;

@SuppressWarnings("serial")
public class A1Test {

	private static Main instance;

	class A1Exception extends Exception {
	}

	@BeforeClass
	public static void setUp() {
		instance = new Main();
	}

	@Test
	public void Test() {
		try {
			File outFile = new File("tmp/a1test.out");
			if (outFile.exists()) {
				outFile.delete();
			}
			System.setOut(new PrintStream(outFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		File directory = new File("resources/testcases/a1");
		File[] testFileList = directory.listFiles();
		for (File testFile : testFileList) {
			Exception fileException = extractFileError(testFile);

			try {
				instance.execute(testFile);
				if (fileException != null) {
					System.out.println("ERROR: " + testFile.getName()
							+ "\nrealException: NoException" + " fileException: "
							+ fileException.getClass().getSimpleName() + "\n");
				}
			} catch (Exception realException) {
				if (fileException == null) {
					System.out.println("ERROR: " + testFile.getName()
							+ "\nrealException: " + realException.getClass().getSimpleName()
							+ " fileException: NoException\n");
				}
//				else if (!realException.getClass().getSimpleName().equals(fileException.getClass().getSimpleName())) {
//					System.out.println("WARNING: " + testFile.getName()
//							+ "\nrealException: "
//							+ realException.getClass().getSimpleName()
//							+ " fileException: "
//							+ fileException.getClass().getSimpleName()
//							+ "\n");
//				}
			}
		}
	}

	private Exception extractFileError(File testFile) {
		Exception fileExcpetion = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(testFile));
			String errorMessage = new String();

			while (errorMessage != null && !errorMessage.contains("JOOS1:")) {
				errorMessage = br.readLine();
			} 
			
			if(errorMessage != null) fileExcpetion = extractError(errorMessage);
			if (fileExcpetion == null && testFile.getName().contains("Je")) {
				fileExcpetion = new A1Exception();
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileExcpetion;
	}

	public Exception extractError(String errorMessage) {
		if (errorMessage.contains("LEXER_EXCEPTION")) {
			return new Scanner.ScanException("");
		} else if (errorMessage.contains("PARSER_EXCEPTION") || errorMessage.contains("SYNTAX_ERROR")) {
			return new LR1Parser.ParseException("");
		}

		return null;
	}
}
