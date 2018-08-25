package utils;

import java.io.File;

public class DeleteXML {

    public static void delete() {
        // TODO Auto-generated method stub
        File file = new File(System.getProperty("user.dir"), ".cccc");
        if (!file.exists()) return;
        String[] entries = file.list();
        if (entries != null) {
            for (String s : entries) {
                File currentFile = new File(file.getPath(), s);
                currentFile.delete();
            }
        }
        file.delete();
    }


}
