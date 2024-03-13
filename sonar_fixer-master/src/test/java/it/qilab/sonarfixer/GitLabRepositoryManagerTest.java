package it.qilab.sonarfixer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.MergeRequestApi;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junit.framework.Assert;
import it.qilab.sonarfixer.view.repositorymanager.GitLabRepositoryManager;

import org.junit.After;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.junit.AfterClass;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import it.qilab.sonarfixer.view.repositorymanager.*;
public class GitLabRepositoryManagerTest {
	private static final String TEST_BASE_URL = "https://gitlab.qilab.it";
    private static final String TEST_FOLDER_PATH = "test_group/sonartest_myvay_svc";
    private static String TEST_LOCAL_PATH;
    private static final String TEST_USERNAME = "l.biondi";
    private static final String TEST_TOKEN_AUTH = "glpat--qosFP_-1cFZTA-vYJx5";
    private static final String TEST_BRANCH_NAME = "master";
    
    private String testFolderName;
    private static RepositoryManager repositoryManager;
    private static File FOLDER_TEST;
    
    @BeforeClass
    public static void folderTest() throws Exception {
    	String desktopPath = System.getProperty("user.home") + "/Desktop";
    	String folderPath = desktopPath + "/Test_GitLabRepositoryManager";

        // Elimina la cartella se esiste
    	FOLDER_TEST = new File(folderPath);
        if (FOLDER_TEST.exists()) {
            try {
                FileUtils.deleteDirectory(FOLDER_TEST);
                System.out.println("Cartella eliminata con successo: " + folderPath);
            } catch (IOException e) {
                System.err.println("Impossibile eliminare la cartella: " + folderPath);
                e.printStackTrace();
            }
        }
        if (FOLDER_TEST.mkdir()) {
            System.out.println("Creata la cartella di Test: " + folderPath);
        } else {
            System.err.println("Impossibile creare la cartella: " + folderPath);
        }
        
        TEST_LOCAL_PATH = FOLDER_TEST.getAbsolutePath();
    }
    
    @After
    public void tearDown() throws Exception {
        // Chiudi il repository dopo ogni test
        if (repositoryManager != null) {
        	//closeAllTestMergeRequests();
            repositoryManager.closeRepository();
        }
    }
    /*
    private void closeAllTestMergeRequests() {
        if (repositoryManager != null) {
            List<MergeRequest> mergeRequests = repositoryManager.getAllMergeRequests();
            for (MergeRequest mergeRequest : mergeRequests) {
                if ("Test Merge Request".equals(mergeRequest.getTitle())) {
                    try {
                        // Utilizza la GitLabApi per fare una richiesta HTTP PATCH all'API di GitLab
                        repositoryManager.getGitLabApi().put("/projects/"
                                + mergeRequest.getProjectId() + "/merge_requests/"
                                + mergeRequest.getIid() + "/merge", null);

                        System.out.println("Chiusa la merge request: " + mergeRequest.getTitle());
                    } catch (GitLabApiException e) {
                        System.err.println("Impossibile chiudere la merge request: " + mergeRequest.getTitle());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
*/





    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        try {
            FileUtils.deleteDirectory(FOLDER_TEST);
            System.out.println("Cartella di test eliminata con successo: " + TEST_LOCAL_PATH);
        } catch (IOException e) {
            System.err.println("Impossibile eliminare la cartella di test: " + TEST_LOCAL_PATH);
            e.printStackTrace();
        }
    }
    
    @Before
    public void setUp() {
        repositoryManager = GitLabRepositoryManager.getInstance();
        repositoryManager.setGitUsername(TEST_USERNAME);
        repositoryManager.setTokenAuth(TEST_TOKEN_AUTH);
    }
    
    @Test
    public void testCloneAndPull() throws GitAPIException, IOException {
    	String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	    String folderNameWithTimestamp = "Test_" + timestamp;
	    String localPathWithTimestamp = TEST_LOCAL_PATH + "/"+ folderNameWithTimestamp;
        repositoryManager.setupGitRepositoryPaths(TEST_BASE_URL, TEST_FOLDER_PATH, localPathWithTimestamp);
        repositoryManager.cloneRepository();
        assertTrue("Test clone del repository fallito", repositoryManager.isRepositoryCloned());
    }
    
    @Test
    public void testCreateAndCheckoutNewBranch() throws GitAPIException, IOException {
    	String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	    String folderNameWithTimestamp = "Test_" + timestamp;
	    String localPathWithTimestamp = TEST_LOCAL_PATH + "/"+ folderNameWithTimestamp;
        repositoryManager.setupGitRepositoryPaths(TEST_BASE_URL, TEST_FOLDER_PATH, localPathWithTimestamp);
        // Clone the repository
        repositoryManager.cloneRepository();
        
        // Create and checkout a new branch
        repositoryManager.createAndCheckoutNewBranch();
        
        // Verifica che il nuovo branch sia stato creato correttamente
        Ref branchRef = repositoryManager.getRepository().findRef(repositoryManager.getBranchName());
        assertNotNull(branchRef); // Verifica che il riferimento al branch non sia nullo
        
        // Verifica che il branch attualmente attivo sia il nuovo branch
        Ref headRef = repositoryManager.getRepository().findRef("HEAD");
        assertEquals(branchRef.getObjectId(), headRef.getTarget().getObjectId());
    }
    
    @Test
    public void testCommitAndPushToCurrentBranch() throws GitAPIException, IOException {
    	String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	    String folderNameWithTimestamp = "Test_" + timestamp;
	    String localPathWithTimestamp = TEST_LOCAL_PATH + "/"+ folderNameWithTimestamp;
        repositoryManager.setupGitRepositoryPaths(TEST_BASE_URL, TEST_FOLDER_PATH, localPathWithTimestamp);
 
        repositoryManager.cloneRepository();
        
        repositoryManager.createAndCheckoutNewBranch();
        
        // crea un file di test nella cartella del repository locale
        String timestampFile = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String nameTestFile = "test_" + timestampFile + ".txt";
        File testFile = new File(repositoryManager.getLocalPath(), nameTestFile);
        try {
            if (testFile.createNewFile()) {
                System.out.println("File di test creato con successo.");
            } else {
                System.err.println("Impossibile creare il file di test.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Git git = repositoryManager.getGit();
        git.add().addFilepattern(nameTestFile).call();

        repositoryManager.commitAndPushToCurrentBranch("Test Commit Message");
        
        boolean filePushed = checkIfFileIsPushedToRemoteRepository(nameTestFile);
        assertTrue("Test di commit fallito", filePushed);
    }
    
    // Metodo per verificare se un file è stato pushato nel repository remoto
    private boolean checkIfFileIsPushedToRemoteRepository(String fileName) throws GitAPIException, IOException {
        Iterable<RevCommit> commits = repositoryManager.getGit().log().call();
        for (RevCommit commit : commits) {
            if (commit.getFullMessage().equals("Test Commit Message")) {
                TreeWalk treeWalk = TreeWalk.forPath(repositoryManager.getRepository(), fileName, commit.getTree());
                if (treeWalk != null) {
                    return true;
                }
            }
        }
        return false;
    }


    @Test
    public void testCreateMergeRequest() throws GitAPIException, IOException {
    	String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	    String folderNameWithTimestamp = "Test_" + timestamp;
	    String localPathWithTimestamp = TEST_LOCAL_PATH + "/"+ folderNameWithTimestamp;
        repositoryManager.setupGitRepositoryPaths(TEST_BASE_URL, TEST_FOLDER_PATH, localPathWithTimestamp);
        repositoryManager.setBranchName(TEST_BRANCH_NAME);
        repositoryManager.cloneRepository();
        repositoryManager.createAndCheckoutNewBranch();
        
        String timestampFile = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File testFile = new File(repositoryManager.getLocalPath(), "test_" + timestampFile + ".txt");
        try {
            if (testFile.createNewFile()) {
                System.out.println("File di test creato con successo.");
            } else {
                System.err.println("Impossibile creare il file di test.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Git git = repositoryManager.getGit();
        git.add().addFilepattern("test.txt").call();

        repositoryManager.commitAndPushToCurrentBranch("Test Commit Message");

        repositoryManager.createMergeRequest("Test Merge Request", "Description");
        
        List<MergeRequest> mergeRequests = repositoryManager.getAllMergeRequests();

        boolean mergeRequestCreated = false;
        for (MergeRequest mergeRequest : mergeRequests) {
            if ("Test Merge Request".equals(mergeRequest.getTitle())) {
                mergeRequestCreated = true;
                break;
            }
        }
        assertTrue("Test Merge Request fallito", mergeRequestCreated);
    }
      
}
