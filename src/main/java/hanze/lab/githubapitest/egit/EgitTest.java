package hanze.lab.githubapitest.egit;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Hanze on 2019/11/16.
 */
public class EgitTest {

    private static final String owner = "iHatebug";
    private static final String repositoryName = "github-api-test";

    private static Logger logger = LoggerFactory.getLogger(EgitTest.class);

    public static void commit() throws IOException{

        GitHubClient client = new GitHubClient();
        client.setOAuth2Token("94e976169f156ed1bc7e0644614f7d0ecac1a954");

        RepositoryService service = new RepositoryService();
        Repository repository = service.getRepository(owner, repositoryName);
        logger.info("repository url: " + repository.getUrl());

        ContentsService contentsService = new ContentsService(client);
        List<RepositoryContents> repositoryContentsList = contentsService.getContents(repository);
        for(RepositoryContents repositoryContents:repositoryContentsList){
            logger.info(repositoryContents.getPath());
        }


    }

    public static void main(String[] args) throws IOException{
        commit();
    }
}
