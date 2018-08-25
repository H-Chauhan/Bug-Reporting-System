package POJO;

/*
 * This class represents each class file (i.e. .java file) in the software repository and
 * its properties we are reporting in this project.
 */
public class ClassFile {
    private String Name;
    public int Added, Deleted, Modified; // Lines of code that are added, deleted and modified between 2 releases.
    public int Bugs; // No. of bugs discovered in the file.
    //Software metrics
    public int nom, loc, mvg, com;
    public int wmc, dit, noc, cbo;

    public ClassFile(String x) {
        Name = x;
        Added = Deleted = Modified = Bugs = 0;
        nom = loc = mvg = com = 0;
        wmc = dit = noc = cbo = 0;
    }

}
