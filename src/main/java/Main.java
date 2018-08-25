import threads.ThreadBugs;
import threads.ThreadFiles;
import threads.ThreadMod;
import threads.ThreadTags;
import utils.CsvWriter;
import utils.DeleteXML;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {

        try {
            System.out.println("Enter local path of git repository: ");
            String path = br.readLine();
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("The entered path doen't exist.");
                return;
            }
            // Scan the repo for all the file names and put them in hash map and apply CCCC tool.
            ThreadFiles tf = new ThreadFiles(path);
            tf.start();
            tf.thread.join();
            // Get all the releases/tags of the git repo.
            ThreadTags tt = new ThreadTags(path);
            tt.start();
            tt.thread.join();
            //Print all the releases.
            System.out.println("\nReleases Found:");
            for (String tag : tt.tags) {
                System.out.println(tag);
            }
            System.out.println("\n");
            System.out.println("Enter the start release: ");
            String startTag = br.readLine();
            System.out.println("Enter the end release: ");
            String endTag = br.readLine();
            //Count the no. of modifications in between the selected tags and look for buggy commits.
            ThreadMod tm = null;
            if (tt.tags.size() >= 2) {
                tm = new ThreadMod(path, endTag, startTag);
                tm.start();
                tm.thread.join();
            }
            if (tm != null && tm.bugCommits.size() > 0) {
                System.out.println("\nFound bugs in " + tm.bugCommits.size() + " commits");
            }
            //Search for files changed in all the buggy commits one by one.
            if (tm != null) {
                for (String commit : tm.bugCommits) {
                    ThreadBugs tb = new ThreadBugs(path, commit);
                    tb.start();
                    tb.thread.join();
                }
            }
            CsvWriter.write(startTag + "-" + endTag);
            DeleteXML.delete();
            System.out.println("Done!");
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}