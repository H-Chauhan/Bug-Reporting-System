package threads;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

/*
 * This thread gets all the releases/tags in the git repository.
 */
public class ThreadTags implements Runnable {
    public Thread thread;
    private String path;
    public List<String> tags = new ArrayList<>();

    public ThreadTags(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        System.out.println("\nGetting Releases");
        File file = new File(path);
        Git git;
        try {
            git = Git.open(file);
            List<Ref> refs = git.tagList().call();
            for(Ref ref: refs) {
                tags.add(ref.getName().substring(10));
            }
        } catch (IOException | GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread (this, path);
            thread.start ();
        }
    }
}
