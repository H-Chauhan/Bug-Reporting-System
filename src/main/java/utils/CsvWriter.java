package utils;

import POJO.ClassFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class CsvWriter {

    public static void write(String filename) {
        File file = new File(System.getProperty("user.dir"), "Report-" + filename + ".csv");
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);

            StringBuilder sb = new StringBuilder();
            sb.append("Class");
            sb.append(',');
            sb.append("Bugs count");
            sb.append(',');
            sb.append("Added Lines");
            sb.append(',');
            sb.append("Deleted Lines");
            sb.append(',');
            sb.append("Modified lines");
            sb.append(',');
            sb.append("NOM");
            sb.append(',');
            sb.append("LOC");
            sb.append(',');
            sb.append("MVG");
            sb.append(',');
            sb.append("COM");
            sb.append(',');
            sb.append("WMC");
            sb.append(',');
            sb.append("DIT");
            sb.append(',');
            sb.append("NOC");
            sb.append(',');
            sb.append("CBO");
            sb.append('\n');

            for (Map.Entry<String, ClassFile> pair : Store.hash.entrySet()) {
                ClassFile cf = (ClassFile) pair.getValue();
                sb.append(pair.getKey());
                sb.append(',');
                sb.append(cf.Bugs);
                sb.append(',');
                sb.append(cf.Added);
                sb.append(',');
                sb.append(cf.Deleted);
                sb.append(',');
                sb.append(cf.Modified);
                sb.append(',');

                sb.append(cf.nom);
                sb.append(',');
                sb.append(cf.loc);
                sb.append(',');
                sb.append(cf.mvg);
                sb.append(',');
                sb.append(cf.com);
                sb.append(',');
                sb.append(cf.wmc);
                sb.append(',');
                sb.append(cf.dit);
                sb.append(',');
                sb.append(cf.noc);
                sb.append(',');
                sb.append(cf.cbo);
                sb.append("\n");
            }

            pw.write(sb.toString());
            pw.close();
            System.out.println("Report generated at: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

