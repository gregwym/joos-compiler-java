package ca.uwaterloo.joos.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
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


@SuppressWarnings("serial")
@RunWith(value = Parameterized.class)
public class A4Test {
	public class A3Exception extends Exception {

	}

	private static Main instance;

	@BeforeClass
	public static void setUp() {
		instance = new Main();

		try {
			File outFile = new File("tmp/a4test.out");
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
			File directory = new File("testcases/a4");

			File[] testFileList = directory.listFiles();
			for (File testFile : testFileList) {
				System.out.println(testFile);
				data.add(new Object[] { testFile });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private List<String> testFiles = new ArrayList<String>();
	private File testFileList;

	public A4Test(File testFileInput) {
		this.testFileList = testFileInput;
		if (!testFileInput.isFile()) {
			extractFiles(testFileInput);
		} else {
			this.testFiles.add(testFileInput.getPath());
		}
		extractFiles(new File("stdlib/4.0"));
	}

	@Test
	public void Test() {
		Exception fileException = null;
		if (testFileList.getName().contains("Je")) {
			fileException = new Exception();
		}

		Exception realException = null;

		try {
			String[] files = new String[this.testFiles.size()];
			this.testFiles.toArray(files);
			instance.execute(files);
//
//			List<AST> asts = new ArrayList<AST>();
//			for(String arg: files) {
//				asts.add(instance.constructAst(new File(arg)));
//			}
//			
//			SymbolTable table = instance.typeLinking(asts);
//			instance.nameLinking(asts, table);
//			instance.typeChecking(asts, table);
//			instance.staticChecking(asts, table);
			
			if (fileException != null) {
				System.out.println("Expecting: " + fileException.getClass().getSimpleName() + "\t"
						+ "but got: NoException" + "\t\t"
						+ "[" + testFileList.getName() + "]");
				fail("Fail to reject");
			}
		} catch (Exception e) {
			realException = e;
			if (fileException == null) {
				System.out.println("Expecting: NoException" + "\t" + "but got: " + realException.getClass().getSimpleName() + " " + realException.getMessage() + "\t\t" + "[" + testFileList.getName() + "]");
				fail("Fail to let it through");
			} else if (!realException.getClass().getSimpleName().equals(fileException.getClass().getSimpleName())) {
//				System.out.println("Expecting: " + fileException.getClass().getSimpleName() + "\t"
//				+ "but got: " + realException.getClass().getSimpleName() + "\t\t"
//				+ "[" + testFileList.getName() + "]");
			}
		}
		
//		if (fileException != null && realException != null) {
//			assertEquals(fileException.getClass().getSimpleName(), realException.getClass().getSimpleName());
//		} else {
//			assertSame(fileException, realException);
//		}
	}

	private void extractFiles(File file) {

		File folder = file;
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().endsWith(".java")) {
					testFiles.add(listOfFiles[i].getPath());
				}

			} else {
				extractFiles(listOfFiles[i]);
			}
		}

	}
}
