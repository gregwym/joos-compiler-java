package ca.uwaterloo.joos.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
import org.junit.Test;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.Preprocessor;
import ca.uwaterloo.joos.parser.LR1;
import ca.uwaterloo.joos.parser.LR1Parser;
import ca.uwaterloo.joos.parser.ParseTree;
import ca.uwaterloo.joos.scanner.DFA;
import ca.uwaterloo.joos.scanner.Scanner;
import ca.uwaterloo.joos.scanner.Token;

public class A1Test {

	@Test
	//public static void main(String[] args) {
	public void Test() 
	{
		try {
			System.setOut(new PrintStream(new File("output.txt")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//fail("Not yet implemented");
		File directory = new File("resources/testcases/a1");
		File[] testFileList = directory.listFiles();
			for (File testFile : testFileList)
			{
				
				System.out.println("currentFile"+testFile.getName());
				
			
				Main.getLogger().setLevel(Level.INFO);
	
				Main.getLogger().fine("DFA constructing");
	
				/* Scanning */
				// Construct a DFA from file
				DFA dfa = null;
				try 
				{
					dfa = new DFA(new File("resources/joos.dfa"));
				} 
				catch (Exception e) 
				{
					System.err.println("ERROR: Invalid DFA File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
					System.exit(-1);
				}
	
				Main.getLogger().fine("DFA constructed: " + dfa);
	
				// Construct a Scanner which use the DFA
				Scanner scanner = new Scanner(dfa);
				List<Token> tokens = null;
	
				// Scan the source codes into tokens
				try 
				{
					tokens = scanner.fileToTokens(testFile);
				} 
				catch (Exception e) 
				{
					System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
					e.printStackTrace();
					System.exit(42);
				}
	
				// Preprocess the tokens
				Preprocessor preprocessor = new Preprocessor();
				tokens = preprocessor.processTokens(tokens);
	
				/* Parsing */
				// Construct a LR1 from file
				LR1 lr1 = null;
	
				try 
				{
				    lr1 = new LR1(new File("resources/joos.lr1"));
				} 
				catch (Exception e) 
				{
					System.err.println("ERROR: Invalid LR1 File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
					e.printStackTrace();
					System.exit(-2);
				}
	
				LR1Parser lr1Parser = new LR1Parser(lr1);
				ParseTree parseTree = null;
				try 
				{
					parseTree = lr1Parser.parseTokens(tokens);
				} 
				catch (Exception e) 
				{
					System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
					e.printStackTrace();
					System.exit(42);
				}
	
				System.out.println(parseTree);
			}
	}
	public void extractError(File testFile)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(testFile));
			String line = br.readLine();
			if(line.contains("JOO1:"))
			{
				if(line.contains("PARSER_EXCEPTION"))
				{
					
				}
				else if(line.contains("PARSER_EXCEPTION"))
				{
					
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}


