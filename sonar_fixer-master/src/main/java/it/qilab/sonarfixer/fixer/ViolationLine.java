package it.qilab.sonarfixer.fixer;

public class ViolationLine {
	private int line;
    private int startLine;
    private int endLine;
    
    public ViolationLine(int line, int startLine, int endLine) {
        this.line = line;
        this.startLine = startLine;
        this.endLine = endLine;
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
}
