package threads;

import POJO.ClassFile;
import utils.Store;

import java.io.File;
import java.io.IOException;

/*
 * This thread scans the local repository for file names.
 * This ignores file and folders like .*
 */
public class ThreadFiles implements Runnable {
    public Thread thread;
    private String path;

    public ThreadFiles(String str) {
        path = str;
    }

    private boolean isOSWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private String getCcccCommand() { return isOSWindows() ? "cccc" : "./cccc"; };

    private void listfiles(String dn) {
        File directory = new File(dn);
        //get all the files from a directory
        if (directory.exists()) {
            File[] fList = directory.listFiles();
            if (fList != null) {
                for (File file : fList) {
                    if (file.isFile()) {
                        String name = file.getAbsolutePath().substring(path.length() + 1); // path of file relative to root directory. Used for Git.
                        String filename = file.getAbsolutePath().substring(path.length() + 1); //exact name of file
                        if (filename.contains(".java")) {
    //                		System.out.println(name);
                            Store.hash.put(name, new ClassFile(name)); //Put file in hash map.
                            try {
                                String[] cmd = {getCcccCommand(), file.getAbsolutePath()};
                                Process p;
                                p = Runtime.getRuntime().exec(cmd);
                                // Any Error?
                                StreamGobbler errorGobbler = new
                                        StreamGobbler(p.getErrorStream(), "ERROR");
                                // Any Output?
                                StreamGobbler outputGobbler = new
                                        StreamGobbler(p.getInputStream(), "OUTPUT");
                                // kick them off
                                errorGobbler.start();
                                outputGobbler.start();
                                p.waitFor();
                                ThreadReadXML trx = new ThreadReadXML(name);
                                trx.start();
                                trx.thread.join();
                            } catch (IOException | InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else if (file.isDirectory()) {
                        listfiles(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Scanning");
        listfiles(path);
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, path);
            thread.start();
        }
    }
}
