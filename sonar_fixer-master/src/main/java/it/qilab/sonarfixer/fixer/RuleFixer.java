package it.qilab.sonarfixer.fixer;

import java.util.List;
import java.util.Map;

public interface RuleFixer {
	Map<String, List<Container_Action_ViolatedLine>> fixIssues();
	boolean getIssuesStatus();
}
