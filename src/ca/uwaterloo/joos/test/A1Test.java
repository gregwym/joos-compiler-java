package ca.uwaterloo.joos.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.parser.LR1Parser;
import ca.uwaterloo.joos.scanner.Scanner;

@SuppressWarnings("serial")
@RunWith(value = Parameterized.class)
public class A1Test {

	private static Main instance;
	private static class A1Exception extends Exception {}

	@BeforeClass
	public static void setUp() {
		instance = new Main();

		try {
			File outFile = new File("tmp/a1test.out");
			if (outFile.exists()) {
				outFile.delete();
			}
			System.setOut(new PrintStream(outFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		List<Object[]> data = new ArrayList<Object[]>();

		try {
			File directory = new File("testcases/a1");
			File[] testFileList = directory.listFiles();
			for (File testFile: testFileList) {
				data.add( new Object[] { testFile });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private File testFile;

	public A1Test(File testFile) {
		this.testFile = testFile;
	}

	@Test
	public void Test() {
		Exception fileException = extractFileError(testFile);
		Exception realException = null;

		try {
			instance.execute(testFile);
			if (fileException != null) {
				System.out.println("Expecting: " + fileException.getClass().getSimpleName() + "\t"
						+ "but got: NoException" + "\t\t"
						+ "[" + testFile.getName() + "]");
			}
		} catch (Exception e) {
			realException = e;
			if (fileException == null) {
				System.out.println("Expecting: NoException" + "\t"
						+ "but got: " + realException.getClass().getSimpleName() + "\t\t"
						+ "[" + testFile.getName() + "]");
			}
			else if (!realException.getClass().getSimpleName().equals(fileException.getClass().getSimpleName())) {
				System.out.println("Expecting: " + fileException.getClass().getSimpleName() + "\t"
						+ "but got: " + realException.getClass().getSimpleName() + "\t\t"
						+ "[" + testFile.getName() + "]");
			}
		}

		if(fileException != null && realException != null) {
			assertEquals(fileException.getClass().getSimpleName(), realException.getClass().getSimpleName());
		}
		else {
			assertSame(fileException, realException);
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
