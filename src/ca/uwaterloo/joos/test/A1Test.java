package ca.uwaterloo.joos.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.uwaterloo.joos.Main;

public class A1Test {
	
	private static Main instance;
	
	@BeforeClass
	public static void setUp() {
		instance = new Main();
	}

	@Test
	//public static void main(String[] args) {
	public void Test() 
	{
		try {
			File outFile = new File("tmp/output.txt");
			if(outFile.exists())
			{
				outFile.delete();
				outFile = new File("tmp/output.txt");
			}
			System.setOut(new PrintStream(outFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//fail("Not yet implemented");
		File directory = new File("resources/testcases/a1");
		File[] testFileList = directory.listFiles();
			for (File testFile : testFileList)
			{
				System.out.println("currentFile: "+testFile.getName());
				try {
					instance.execute(testFile);
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
					e.printStackTrace();
				}
			}
	}
	
}


