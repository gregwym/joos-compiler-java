package ca.uwaterloo.joos.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class CodeGenerator extends SemanticsVisitor {
	public static final Logger logger = Main.getLogger(CodeGenerator.class);
	
	protected File asmFile = null;
	protected List<String> texts = null;
	protected List<String> data = null;

	public CodeGenerator(SymbolTable table) {
		super(table);
		logger.setLevel(Level.FINER);
		texts = new ArrayList<String>();
		data = new ArrayList<String>();
		
		this.texts.add("section .text");
		this.texts.add("");
		this.data.add("section .data");
		this.data.add("");
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		super.willVisit(node);
		
		if (node instanceof TypeDeclaration) {
			// Construct output file
			String filename = this.getCurrentScope().getName();
			filename = filename.replace('.', '/');
			filename = "./output/" + filename + ".s";
			logger.finer(filename);
			this.asmFile = new File(filename);
			
			// Place the runtime.s externs
			this.texts.add("extern __malloc");
			this.texts.add("extern __debexit");
			this.texts.add("extern __exception");
			this.texts.add("extern NATIVEjava.io.OutputStream.nativeWrite");
			this.texts.add("");
		} else if (node instanceof MethodDeclaration) {
			Modifiers modifiers = ((MethodDeclaration) node).getModifiers();
			if (!modifiers.containModifier(Modifier.NATIVE)) {
				// Define method labels
				String methodLabel = this.methodLable(this.getCurrentScope().getName());
				if(((MethodDeclaration) node).getName().getSimpleName().equals("test") && 
						modifiers.containModifier(Modifier.STATIC)) {
					methodLabel = "_start";
				} 
				
				this.texts.add("global " + methodLabel);
				this.texts.add(methodLabel + ":");
				
				// Preamble
				this.texts.add("push ebp\t\t\t; Preamble");
				this.texts.add("mov ebp, esp");
				
				// Allocate space for local variables
				// TODO
				
				
				// Push registers
	//			this.texts.add("push eax");
				this.texts.add("push ebx");
				this.texts.add("push ecx");
				this.texts.add("push edx");
			}
		}
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if (node instanceof FileUnit) {
			File dir = this.asmFile.getParentFile();
			if(dir != null) {
				dir.mkdirs();
			}
			this.asmFile.createNewFile();
			BufferedWriter asmWriter = new BufferedWriter(new FileWriter(this.asmFile));
			for(String line: this.texts) {
				asmWriter.write(line);
				asmWriter.newLine();
			}
			
			for(String line: this.data) {
				asmWriter.write(line);
				asmWriter.newLine();
			}
			asmWriter.close();
		} else if (node instanceof MethodDeclaration) {
			
			// Postamble
			// Pop registers
			this.texts.add("pop edx\t\t\t; Postamble");
			this.texts.add("pop ecx");
			this.texts.add("pop ebx");
//			this.texts.add("pop eax");
			
			// Deallocate space for local variables
			// TODO
			
			// Restore frame pointer
			this.texts.add("pop ebp");
			
			if(((MethodDeclaration) node).getName().getSimpleName().equals("test") && 
					((MethodDeclaration) node).getModifiers().containModifier(Modifier.STATIC)) {
				this.texts.add("call __debexit");
			} else {
				this.texts.add("ret");
			}
			this.texts.add("");
		}
		super.didVisit(node);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof FileUnit) {
			
		} 
		return true;
	}
	
	private String methodLable(String methodSignature) {
		String label = methodSignature.replaceAll("[(),]", "_");
		label = label.replaceAll("\\[\\]", "_ARRAY");
		return label;
	}
}
