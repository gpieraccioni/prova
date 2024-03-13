package it.qilab.sonarfixer;

import static junit.framework.TestCase.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import it.qilab.sonarfixer.view.SonarFacade;

public class SonarFacedeTest {
	private SonarFacade sonarFacade;

    @Before
    public void setUp() {
        String[] args = {"sonartest_myvay_svc","test_group/myvay_svc_sonartest"};
        sonarFacade = new SonarFacade(args);
    }

    @Test
    public void testRunScanAndFixIssues() {
        sonarFacade.runScanAndFixIssues();
    }
    
    @Test
    public void testRunWithCustomRules() {
        String[] args = {"sonartest_myvay_svc", "test_group/myvay_svc_sonartest", "-r='java:S1068:true,java:S1118:true'"};
        sonarFacade = new SonarFacade(args);
        sonarFacade.runScanAndFixIssues();
    }
}
