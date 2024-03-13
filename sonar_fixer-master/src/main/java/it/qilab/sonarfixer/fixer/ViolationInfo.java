package it.qilab.sonarfixer.fixer;

/*
 * Classe che incapsula le informazioni utili su un campo 
 */
public class ViolationInfo implements Comparable<ViolationInfo> {
    private String message;
    private int line;
    private int startLine;
    private int endLine;

    public ViolationInfo(String message, int line, int startLine, int endLine) {
        this.message = message;
        this.line = line;
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    public int getLine() {
    	return this.line;
    }
    
    public String getMessage() {
    	return this.message;
    }
    
    public int getStartLine() {
    	return this.startLine;
    }
    
    public int getEndLine() {
    	return this.endLine;
    }


    @Override
    public int compareTo(ViolationInfo other) {
    	return Integer.compare(other.line, this.line);
    }
}
