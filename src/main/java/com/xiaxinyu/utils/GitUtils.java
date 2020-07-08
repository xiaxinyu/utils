package com.xiaxinyu.utils;

import com.xiaxinyu.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Git Utils
 *
 * @author XIAXINYU3
 * @date 2019.7.2
 */
@Slf4j
public class GitUtils {
    /**
     * init local repository
     *
     * @param localDir source code directory
     * @return
     */
    public static Git initLocalRepository(File localDir) {
        long t1 = System.currentTimeMillis();
        Git git;
        try {
            log.info("Init local repository, localDir={}", localDir.getAbsolutePath());
            git = Git.init().setDirectory(localDir).call();
            log.info("Finish init local repository, localDir={}", localDir.getAbsolutePath());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
        long t2 = System.currentTimeMillis();
        log.info("initLocalRepository costs {} seconds", (t2 - t1) / 1000);
        return git;
    }

    /**
     * clone repository to local
     *
     * @param workDir     work directory
     * @param remoteUrl   remote url to clone
     * @param accessToken access token
     * @return the git instance of local repository
     */
    public static Git cloneRepository(String workDir, String remoteUrl, String accessToken) {
        Git git;
        File localDir = new File(workDir);
        deleteDirectory(localDir);
        try {
            log.info("Cloning repository to local, workDir={}, remoteUrl={}", workDir, remoteUrl);
            Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setBranch("")
                    .setCredentialsProvider(StringUtils.isEmpty(accessToken) ? null : new UsernamePasswordCredentialsProvider("", accessToken))
                    .setDirectory(localDir)
                    .call();
            FileUtils.deleteDirectory(new File(localDir.getAbsolutePath() + File.separator + ".git"));
            git = initLocalRepository(localDir);
            log.info("Finish clone repository to local, workDir={}, remoteUrl={}", workDir, remoteUrl);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
        return git;
    }

    /**
     * commit and push to remote repository
     *
     * @param git         local git
     * @param repoUrl     repository address
     * @param accessToken token access token
     */
    public static void commitAndPush(Git git, String repoUrl, String accessToken) {
        long t1 = System.currentTimeMillis();
        try {
            log.info("Committing and pushing local repository, repoUrl={}", repoUrl);
            String[] url = repoUrl.split("://");
            git.add().addFilepattern(".").call();
            git.add().setUpdate(true).addFilepattern(".").call();
            git.commit().setMessage("init").call();
            List<Ref> refs = git.branchList().call();
            PushCommand pushCommand = git.push();
            for (Ref ref : refs) {
                pushCommand.add(ref);
            }
            pushCommand.setRemote(repoUrl);
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                    "", accessToken));
            pushCommand.call();
            log.info("Finish committing and pushing local repository, repoUrl={}", repoUrl);
        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
        long t2 = System.currentTimeMillis();
        log.info("commitAndPush costs {} seconds", (t2 - t1) / 1000);
    }

    /**
     * delete assigned directory
     *
     * @param dir
     */
    public static void deleteDirectory(File dir) {
        if (dir.exists()) {
            try {
                log.info("Deleting directory, dir={}", dir);
                FileUtils.deleteDirectory(dir);
                log.info("Finish deleting directory, dir={}", dir);
            } catch (IOException e) {
                throw new ApplicationException(e);
            }
        } else {
            log.warn("Deleting directory doesn't exist. dir={}", dir.getAbsoluteFile());
        }
    }
}
