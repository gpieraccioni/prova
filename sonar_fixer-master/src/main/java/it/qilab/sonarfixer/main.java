package it.qilab.sonarfixer;

import it.qilab.sonarfixer.view.SonarFacade;
public class main {
    public static void main(String[] args) {
    	
        SonarFacade sonarFacade = new SonarFacade(args);
        
        sonarFacade.runScanAndFixIssues();
    }  
}