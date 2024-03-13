package it.qilab.sonarfixer.fixer;

import java.util.ArrayList;
import java.util.List;

public class MultipleViolationsLines {
	private List<ViolationLine> violations;
	
	public MultipleViolationsLines() {
        this.violations = new ArrayList<>();
    }
	public List<ViolationLine> getMultipleViolationsLines(){
		return this.violations;
	}
	
	public void addViolationLine(ViolationLine violationLine) {
        this.violations.add(violationLine);
	}
}

