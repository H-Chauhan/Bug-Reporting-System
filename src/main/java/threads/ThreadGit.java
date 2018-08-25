package threads;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/*
 * This thread clones the Git repository to local.
 */
class ThreadGit implements Runnable {
    private Thread thread;
    private String uri;
    private String path;

    public ThreadGit(String str1, String str2) {
        uri = str1;
        path = str2;
    }

    public void run() {
        System.out.println("Cloning");
        File file = new File(path);

        try {
            Git.cloneRepository()
                    .setURI(uri)
                    .setDirectory(file)
                    .setCloneAllBranches(true)
                    .setBranch("master")
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, uri);
            thread.start();
        }
    }

}

