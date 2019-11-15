package hanze.lab.githubapitest.github;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Hanze on 2019/11/6.
 */
public class GithubConnector {

    private static Logger logger = LoggerFactory.getLogger(GithubConnector.class);

    public static void main(String[] args){
        File xmlFile = new File("src/main/resources/xml");
        try {
            pushFiles(xmlFile, "test commit via github api.","iHatebug/github-api-test","heads/master");
        }
        catch (Exception ex){
            logger.error(ex.toString());
        }
    }

    static public String pushFiles(File baseDirectory, String message,
                                   String repositoryName, String branchName) throws IOException {
        //connect to github repository
        GitHub github = GitHubBuilder.fromPropertyFile("src/main/resources/.github").build();
        GHRepository repo = github.getRepository(repositoryName);
        logger.info("repository name: " + repo.getFullName());

        //get current branch
        GHRef ref = repo.getRef(branchName);
        logger.info("ref:" + ref.getUrl());

        GHCommit latestCommit = repo.getCommit(ref.getObject().getSha());
        logger.info("last commit:" + latestCommit.getSHA1());

        GHTreeBuilder treeBuilder = repo.createTree().baseTree(latestCommit.getTree().getSha());

        treeBuilder = addFilesToTree(treeBuilder, baseDirectory, baseDirectory);

        GHTree tree = treeBuilder.create();

        logger.info("tree: " + tree.getSha());

        GHCommit commit = repo.createCommit()
                .parent(latestCommit.getSHA1())
                .tree(tree.getSha())
                .message(message)
                .create();
        ref.updateTo(commit.getSHA1());

        logger.info("Commit created with on branch " + branchName + " and SHA " + commit.getSHA1() + " and URL " + commit.getHtmlUrl());
        return commit.getSHA1();
    }

    private static GHTreeBuilder addFilesToTree(GHTreeBuilder treeBuilder, File baseDirectory, File currentDirectory) throws IOException {
        logger.info("start to add file...");
        for(File file : currentDirectory.listFiles()) {
            String relativePath = baseDirectory.toURI().relativize(file.toURI()).getPath();
            if(file.isFile()) {
                treeBuilder.textEntry(relativePath, FileUtils.readFileToString(file), false);
                logger.info("is file:" + file.getName());
            } else {
                logger.info("not file");
                addFilesToTree(treeBuilder, baseDirectory, file);
            }
        }
        return treeBuilder;
    }
}
