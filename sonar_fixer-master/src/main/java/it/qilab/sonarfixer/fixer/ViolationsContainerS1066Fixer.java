package it.qilab.sonarfixer.fixer;

public class ViolationsContainerS1066Fixer {
	private int line;
    private int startLine;
    private int endLine;
    private int flowLine;

    public ViolationsContainerS1066Fixer(int line, int startLine, int endLine, int flowLine) {
        this.line = line;
        this.startLine = startLine;
        this.endLine = endLine;
        this.flowLine = flowLine;
    }
    
    public int getLine() {
    	return this.line;
    }
 
    public int getStartLine() {
    	return this.startLine;
    }
    
    public int getEndLine() {
    	return this.endLine;
    }
    
    public int getFlowLine() {
    	return this.flowLine; 
    }

}