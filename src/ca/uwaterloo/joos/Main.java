/**
 *
 */
package ca.uwaterloo.joos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.checker.HierarchyChecker;
import ca.uwaterloo.joos.codegen.CodeGenerator;
import ca.uwaterloo.joos.codegen.IndexerVisitor;
import ca.uwaterloo.joos.parser.LR1;
import ca.uwaterloo.joos.parser.LR1Parser;
import ca.uwaterloo.joos.parser.ParseTree;
import ca.uwaterloo.joos.reachability.ReachabilityVisitor;
import ca.uwaterloo.joos.scanner.DFA;
import ca.uwaterloo.joos.scanner.Scanner;
import ca.uwaterloo.joos.scanner.Token;
import ca.uwaterloo.joos.symboltable.DeepDeclVisitor;
import ca.uwaterloo.joos.symboltable.ImportVisitor;
import ca.uwaterloo.joos.symboltable.NameLinker;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TopDeclVisitor;
import ca.uwaterloo.joos.symboltable.TypeChecker;
import ca.uwaterloo.joos.symboltable.TypeLinker;
import ca.uwaterloo.joos.weeder.Weeder;

/**
 * @author Greg Wang
 *
 */
public class Main {
	private static final Logger logger = Main.getLogger(Main.class);

	public static Logger getLogger(Class<?> cls) {
		return Logger.getLogger(cls.getSimpleName());
	}

	private final Scanner scanner;
	private final LR1Parser parser;
	private final Preprocessor preprocessor;
	private final Weeder weeder;
	private HierarchyChecker hierarchyChecker;
	public Main() {
		// Construct Preprocessor
		this.preprocessor = new Preprocessor();

		// Read a DFA from file
		DFA dfa = null;
		try {
			dfa = new DFA(new File("resources/joos.dfa"));
		} catch (Exception e) {
			System.err.println("ERROR: Invalid DFA File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			System.exit(-11);
		}
		// Construct Scanner
		this.scanner = new Scanner(dfa);

		// Read a LR1 from file
		LR1 lr1 = null;
		try {
			lr1 = new LR1(new File("resources/joos.lr1"));
		} catch (Exception e) {
			System.err.println("ERROR: Invalid LR1 File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(-12);
		}
		// Construct Parser
		this.parser = new LR1Parser(lr1);
		
		this.weeder = new Weeder();
	}

	public AST constructAst(File source) throws Exception {

		/* Scanning */
		// Construct a Scanner which use the DFA
		List<Token> tokens = null;

		// Scan the source codes into tokens
		tokens = this.scanner.fileToTokens(source);

		// Preprocess the tokens
		tokens = preprocessor.processTokens(tokens);

		/* Parsing */
		ParseTree parseTree = null;
		parseTree = this.parser.parseTokens(tokens);
		
//		if(!source.getPath().contains("stdlib")) {
//		System.out.println(parseTree);
//		}
		
		/* AST Constructing */
		AST ast = new AST(parseTree, source.getName());
		
//		if(!source.getPath().contains("stdlib")) {
//		ToStringVisitor visitor = new ToStringVisitor();
//		ast.getRoot().accept(visitor);
//		System.out.println(visitor.getString());
//		}
		
		/* AST Weeding */
		this.weeder.weedAst(ast);
		return ast;
	}
	
	public SymbolTable typeLinking(List<AST> asts) throws Exception {
		SymbolTable table = new SymbolTable();
		
		for (AST ast : asts) {
			ast.getRoot().accept(new TopDeclVisitor(table));
		}
		logger.info("Top Declarations constructed");

		for (AST ast: asts){
			ast.getRoot().accept(new ImportVisitor(table));
		}
		logger.info("Import Declarations added");

		for(AST ast: asts) {
			TypeLinker linker = new TypeLinker(table);
			ast.getRoot().accept(linker);
		}
		logger.info("Type Linking finished");
		
		for (AST ast: asts){
			ast.getRoot().accept(new DeepDeclVisitor(table));
		}
		logger.info("Deep Declaration constructed");
		
		for(AST ast: asts) {
			 hierarchyChecker = new HierarchyChecker(table);
			ast.getRoot().accept(hierarchyChecker);
			
		}
		
		logger.info("Hierarchy Checking finished");
		return table;
	}
	
	public void nameLinking(List<AST> asts, SymbolTable table) throws Exception {
		NameLinker linker = new NameLinker(table);
		
		for(AST ast: asts) {
			ast.getRoot().accept(linker);
		}
		logger.info("Name Linking finished");
	}
	
	public void typeChecking(List<AST> asts, SymbolTable table) throws Exception {
		TypeChecker checker = new TypeChecker(table);
		
		for(AST ast: asts) {
			ast.getRoot().accept(checker);
		}
		logger.info("Type Checking finished");
	}
	
	public void staticChecking(List<AST> asts, SymbolTable table) throws Exception {
		ReachabilityVisitor checker = new ReachabilityVisitor(table);
		
		for(AST ast: asts) {
			ast.getRoot().accept(checker);
		}
		logger.info("Static Checking finished");
	}
	
	public void generateCode(List<AST> asts, SymbolTable table) throws Exception {
		
		 //Sort out a list of static fields and assign index for each decl
		IndexerVisitor visitor = new IndexerVisitor(table);
		for(AST ast: asts) {
			ast.getRoot().accept(visitor);
		}
		
		CodeGenerator generator = new CodeGenerator(table);
		
		for(AST ast: asts) {
			ast.getRoot().accept(generator);
		}
		generator.writeStaticInit();
		generator.generateSubtypeTable();
		generator.copyNullAsm();
		logger.info("Code Generated");
	}
	
	public void execute(String[] args) throws Exception {
		logger.info("Processing: " + Arrays.asList(args));
		
		List<AST> asts = new ArrayList<AST>();
		for(String arg: args) {
			asts.add(this.constructAst(new File(arg)));
		}
		
		SymbolTable table = typeLinking(asts);
//		table.listScopes();
		
		nameLinking(asts, table);
		typeChecking(asts, table);
//		staticChecking(asts, table);
		generateCode(asts, table);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		if(args.length < 1) {
			System.exit(-1);
		}
		
		Main instance = new Main();

		try {
			instance.execute(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(42);
		}
	}
}
