package threads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import utils.Store;

/*
 * This thread counts the number of bugs in files by reading the files changed
 * in a commit.
 */
public class ThreadBugs implements Runnable {
    public Thread thread;
    private String path, commitID;

    public ThreadBugs(String path, String id) {
        this.path = path;
        this.commitID = id;
    }

    @Override
    public void run() {
        File file = new File(path);
        RevWalk revwalk;
        Git git;
        try {
            git = Git.open(file);
            Repository repo = git.getRepository();
            revwalk = new RevWalk(repo);
            ObjectId commitObjID = ObjectId.fromString(commitID);
            RevCommit commit = revwalk.parseCommit(commitObjID);
            RevCommit parent = null;
            if (commit.getParent(0) != null) {
                parent = revwalk.parseCommit(commit.getParent(0).getId());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DiffFormatter diffFormatter = new DiffFormatter(baos);
            diffFormatter.setRepository(repo);
            for (DiffEntry entry : diffFormatter.scan(parent, commit)) {
                diffFormatter.format(diffFormatter.toFileHeader(entry));
                String entrystring = baos.toString();
                baos.reset();
                String filename = entrystring.substring(entrystring.indexOf("b/") + 2,
                        entrystring.indexOf("\n"));
                if (!Store.hash.containsKey(filename)) {
                    filename = filename.replace('/', '\\');
                }
                if (filename.contains(".java")) {
                    if (!Store.hash.containsKey(filename)) {
                        return;
                    }
                    Store.hash.get(filename).Bugs = Store.hash.get(filename).Bugs + 1;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
}

