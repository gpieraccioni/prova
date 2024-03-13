package it.qilab.sonarfixer.view.repositorymanager;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab4j.api.models.MergeRequest;
import org.eclipse.jgit.lib.Ref;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;


public class GitLabRepositoryManager implements RepositoryManager{
    private static GitLabRepositoryManager instance;
    private String token_auth; 
    private String username; 
    private String remoteUrl; 
    private String localPath; 
    private String branchName;
    private String repositoryFolderName;
    private String folderPath;
    private String commitMessage;
    private Repository repository;
    private Git git;
    private boolean repositoryCloned = false;
    private GitLabApi gitLabApi;

    private GitLabRepositoryManager() {
       
    }

    public static GitLabRepositoryManager getInstance() {
        if (instance == null) {
            instance = new GitLabRepositoryManager();
        }
        return instance;
    }
    
    @Override
    public void setTokenAuth(String token_auth) {
    	this.token_auth = token_auth;
    }
    
    @Override
    public void setupGitRepositoryPaths(String baseUrl, String folderPath, String localPath) {
    	this.folderPath = folderPath;
    	this.remoteUrl = baseUrl + "/" + folderPath + ".git";
    	String[] partsOfRoute = folderPath.split("/");
    	this.repositoryFolderName = partsOfRoute[partsOfRoute.length - 1];
    	this.localPath = localPath + this.repositoryFolderName;
    	this.gitLabApi = new GitLabApi(baseUrl, this.token_auth);
    }
    
    @Override
    public GitLabApi getGitLabApi() {
        return this.gitLabApi;
    }
    
    @Override
    public Repository getRepository() {
        return this.repository;
    }
    
    @Override
    public Git getGit() {
    	return this.git;
    }
    
    @Override
    public String getLocalPath() {
    	return this.localPath;
    }
    
    @Override
    public String getRepositoryFolderName() {
    	return this.repositoryFolderName;
    }
    
    @Override
    public String getBranchName() throws IOException {
        if (this.repository != null) {
            return this.repository.getBranch();
        } else {
            return null; 
        }
    }
    
    @Override
    public void setCommitMessage(String new_commit) {
        this.commitMessage = new_commit;
    }
    
    @Override
    public void setBranchName(String branch) {
        this.branchName = branch;
    }
    
    @Override
    public void setGitUsername(String username) {
        this.username = username;
    }
    
    @Override
    public boolean isRepositoryCloned() {
        return this.repositoryCloned;
    }
    
    @Override
    public void cloneRepository() throws GitAPIException, IOException {
        this.repository = getCloneRepository();
        this.git = new Git(repository);
        this.repositoryCloned = true;
    }

    private Repository getCloneRepository() throws GitAPIException {
    	System.out.println("Cloning repository...");
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, token_auth);
        File localRepositoryDir = new File(localPath);

        if (localRepositoryDir.exists() && localRepositoryDir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(localRepositoryDir); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Repository repoCloned = Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(localRepositoryDir)
                .setCredentialsProvider(credentialsProvider)
                .call()
                .getRepository();

        System.out.println("Repository cloned successfully.");
        return repoCloned;
    }
    
    @Override
    public void createAndCheckoutNewBranch() throws GitAPIException, IOException {
    	if (this.repository != null) {
    		
            // Genera un timestamp per il nome del nuovo branch
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String newBranchName = "sonarqubefixer_test_" + timestamp;

            // Crea un nuovo branch basato sul branch corrente
            Ref newBranch = git.branchCreate()
                    .setName(newBranchName)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .call();

            // Esegui il checkout del nuovo branch
            git.checkout()
                    .setName(newBranch.getName())
                    .call();
   
    	} else {
            System.err.println("Il repository non è stato inizializzato correttamente.");
        }
    }
    
    private void addAllChanges(Git git) throws GitAPIException {
        git.add().addFilepattern(".").call();
    }
    
    @Override
    public void commitAndPushToCurrentBranch(String commitMessage) throws GitAPIException, IOException {
    	if (this.repository != null) {
            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, token_auth);
            // Ottenere il nome del branch corrente
            String currentBranch = repository.getBranch();
            
            addAllChanges(git);

            // Effettuare il commit sul branch corrente
            git.commit()
                    .setMessage(commitMessage)
                    .call();

            // Push delle modifiche al branch corrente
            git.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec("refs/heads/" + currentBranch + ":refs/heads/" + currentBranch))
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            System.out.println("Commit e push al branch corrente (" + currentBranch + ") effettuati con successo.");
    	} else {
            System.err.println("Il repository non è stato inizializzato correttamente.");
        }
    }
    
    @Override
    public void createMergeRequest(String title, String description) throws IOException {
    	if (this.repository != null) {
    		String currentBranch = repository.getBranch();
    		
	    	HttpClient httpClient = HttpClients.createDefault();
	
	        // Costruzione dell'URL per creare una merge request nel tuo repository GitLab
	        String gitLabApiUrl = "https://gitlab.qilab.it/api/v4";
	        String encodedFolderPath = encodeFolderPath(this.folderPath); // Codifica il percorso della cartella
	        String createMergeRequestUrl = gitLabApiUrl + "/projects/" + encodedFolderPath + "/merge_requests";
	        
	      
            String requestBody = String.format("{\"source_branch\":\"%s\",\"target_branch\":\"%s\",\"title\":\"%s\",\"description\":\"%s\"}",
                    currentBranch, this.branchName, title, description);
            
            HttpPost httpPost = new HttpPost(createMergeRequestUrl);
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.token_auth);
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setEntity(new StringEntity(requestBody));

            // Eseguzione della richiesta HTTP per creare la merge request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 201) {
                String responseContent = EntityUtils.toString(responseEntity);
                System.out.println("Merge request creata con successo: " + responseContent);
            } else {
                System.err.println("Errore durante la creazione della merge request. Codice di stato HTTP: " + response.getStatusLine().getStatusCode());
            }
        } else {
            System.err.println("Il repository non è stato inizializzato correttamente.");
        }
    }

    // Codifica il percorso della cartella
    private String encodeFolderPath(String folderPath) {
        try {
            return URLEncoder.encode(folderPath, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return folderPath; // Ritorna il percorso non codificato in caso di errore
        }
    }
    
    @Override
    public List<MergeRequest> getAllMergeRequests() {
        try {
            if (this.gitLabApi != null) {
                Project project = this.gitLabApi.getProjectApi().getProject(this.folderPath);
                MergeRequestFilter filter = new MergeRequestFilter();
                filter.setProjectId(project.getId());
                List<MergeRequest> mergeRequests = this.gitLabApi.getMergeRequestApi().getMergeRequests(filter);
                return mergeRequests;
            } else {
                System.err.println("GitLab API non inizializzata.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void closeRepository() {
        if (git != null) {
            git.close();
            System.out.println("Istanza Git chiusa correttamente.");
        }

        if (repository != null) {
            repository.close();
            System.out.println("Repository chiuso correttamente.");
        }
    }
    
    
    @Override
    public void closeMergeRequest(int mergeRequestId) throws GitLabApiException, IOException {
        /*if (this.gitLabApi != null) {
            MergeRequestApi mergeRequestApi = gitLabApi.getMergeRequestApi();
            mergeRequestApi.updateMergeRequest(projectId, mergeRequestId)
                    .withState(MergeRequestStateEvent.CLOSE)
                    .update();
            System.out.println("Merge request #" + mergeRequestId + " chiusa con successo.");
        } else {
            System.err.println("GitLab API non inizializzata.");
        }*/
    }

	
}
