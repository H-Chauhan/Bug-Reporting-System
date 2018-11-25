package threads;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import utils.Store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * This thread counts the no. of modifications in the files between 2 commits.
 * It also looks for buggy commits.
 */
public class ThreadMod implements Runnable {
    public Thread thread;
    private String startTag, endTag, path;
    public List<String> bugCommits = new ArrayList<>();

    public ThreadMod(String path, String te, String ts) {
        this.path = path;
        endTag = "refs/tags/" + te;
        startTag = "refs/tags/" + ts;
    }

    /*
     * This method parses git diff patch to count the LOC added, deleted or modified.
     */
    private void parsePatch(String name, String patch) {
        StringBuilder newstr = new StringBuilder();

        for (int i = 0; i < patch.length() - 1; i++) {
            if (patch.charAt(i) == '\n') {
                newstr.append(patch.charAt(i + 1));
            }
        }


        int a, d, m;
        a = d = m = 0;

        int i = 0;
        while (i < newstr.length()) {
            if (newstr.charAt(i) == '-') {
                int cm = 1;
                i++;

                //Count the number of '-'s.
                while (i < newstr.length() && newstr.charAt(i) == '-') {
                    i++;
                    cm++;
                }

                //Count the number of '+'s.
                int cp = 0;
                while (i < newstr.length() && newstr.charAt(i) == '+') {
                    i++;
                    cp++;
                }

                m += Math.min(cm, cp); //One '+' for every '-'.
                d += cm > cp ? cm - cp : 0; //More '-' than '+'.
                a += cm < cp ? cp - cm : 0; //More '+' than '-'.


            } else if (newstr.charAt(i) == '+') {
                int cp = 1;
                i++;

                //Count the number of '+'s.
                while (i < newstr.length() && newstr.charAt(i) == '+') {
                    i++;
                    cp++;
                }

                a += cp;
            } else {
                i++;
            }
        }

        if (!Store.hash.containsKey(name)) {
            return;
        }

        if (name.contains(".java")) {
            Store.hash.get(name).Added = a;
            Store.hash.get(name).Deleted = d;
            Store.hash.get(name).Modified = m;
        }
    }

    @Override
    public void run() {
        System.out.println("\nComparing Releases");
        File file = new File(path);
        RevWalk revwalk = null;
        try {
            Git git = Git.open(file);
            Repository repo = git.getRepository();
            revwalk = new RevWalk(repo);
            RevCommit oldcommit = revwalk.parseCommit(repo.getRef(startTag).getObjectId());
            RevCommit newcommit = revwalk.parseCommit(repo.getRef(endTag).getObjectId());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DiffFormatter diffFormatter = new DiffFormatter(baos);
            diffFormatter.setRepository(repo);
            for (DiffEntry entry : diffFormatter.scan(oldcommit, newcommit)) {
                diffFormatter.format(diffFormatter.toFileHeader(entry));
                String entrystring = baos.toString();
                baos.reset();

                String filename = entrystring.substring(entrystring.indexOf("b/") + 2,
                        entrystring.indexOf("\n"));
                if (filename.contains(".c") // Code files
                        || filename.contains(".cpp")
                        || filename.contains(".h")
                        || filename.contains(".cc")
                        || filename.contains(".java")) {
                    String patch = entrystring.substring(entrystring.indexOf("@@"));
                    if (!Store.hash.containsKey(filename)) {
						filename = filename.replace('/', '\\');
					}
					parsePatch(filename, patch);
                }

                Iterable<RevCommit> commits = git.log().addRange(oldcommit, newcommit).call();
                for (RevCommit commit : commits) {
                    String commitMessage = commit.getFullMessage().toLowerCase(); //To Remove Case-Sensitivity.
                    if (commitMessage.contains("fix")
                            || commitMessage.contains("bug")
                            || commitMessage.contains("issue")
                            || commitMessage.contains("defect")
                            || commitMessage.contains("error")) {
                        bugCommits.add(commit.getId().getName());
                    }
                }
            }
        } catch (IOException | GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (revwalk != null) {
                revwalk.dispose();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
}

