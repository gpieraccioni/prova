package it.qilab.sonarfixer.view.repositorymanager;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;

public interface RepositoryManager {
	GitLabApi getGitLabApi();
	Repository getRepository();
	String getRepositoryFolderName();
	String getBranchName() throws IOException;
	Git getGit();
	String getLocalPath();
    void setTokenAuth(String tokenAuth);
    void setupGitRepositoryPaths(String baseUrl, String folderPath, String localPath);
    void setCommitMessage(String commitMessage);
    void setBranchName(String branchName);
    void setGitUsername(String username);
    boolean isRepositoryCloned();
    void cloneRepository() throws GitAPIException, IOException;
    void createAndCheckoutNewBranch() throws GitAPIException, IOException;
    void commitAndPushToCurrentBranch(String commitMessage) throws GitAPIException, IOException;
    void createMergeRequest(String title, String description) throws GitAPIException, IOException;
    public List<MergeRequest> getAllMergeRequests();
    void closeRepository();
    void closeMergeRequest(int mergeRequestId) throws GitLabApiException, IOException;
}
