package it.qilab.sonarfixer.model;

import java.util.List;

public class Current {
	private String id;
    private String type;
    private String componentId;
    private String componentKey;
    private String componentName;
    private String componentQualifier;
    private String analysisId;
    private String status;
    private String submittedAt;
    private String submitterLogin;
    private String startedAt;
    private String executedAt;
    private int executionTimeMs;
    private boolean hasScannerContext;
    private int warningCount;
    private List<String> warnings;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentQualifier() {
        return componentQualifier;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public String getStatus() {
        return status;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public String getSubmitterLogin() {
        return submitterLogin;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getExecutedAt() {
        return executedAt;
    }

    public int getExecutionTimeMs() {
        return executionTimeMs;
    }

    public boolean isHasScannerContext() {
        return hasScannerContext;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public List<String> getWarnings() {
        return warnings;
    }

}