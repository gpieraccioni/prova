package it.qilab.sonarfixer.model;

public class TextRange {
	private int startLine;
	private int endLine;
	private int startOffset;
	private int endOffset;
	
	public int getStartLine() {
		return this.startLine;
	}
	
	public int getEndLine() {
		return this.endLine;
	}
	
	public int getStartOffset() {
		return this.startOffset;
	}
	
	public int getEndOffset() {
		return this.endOffset;
	}
}
