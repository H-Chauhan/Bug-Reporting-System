package threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import utils.Store;

public class ThreadReadXML implements Runnable {
    private String filename;
    Thread thread;

    ThreadReadXML(String fname) {
        this.filename = fname;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            File fXmlFile = new File(System.getProperty("user.dir"), ".cccc/cccc.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            FileInputStream finputStream = new FileInputStream(fXmlFile);

            Reader reader = new InputStreamReader(finputStream, StandardCharsets.UTF_8);

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            Document doc = dBuilder.parse(is);

            //optional, but recommended.
            doc.getDocumentElement().normalize();

            if(!(doc.getElementsByTagName("project_summary").getLength() > 0)) return;
            Element projSummary = (Element)
                    doc.getElementsByTagName("project_summary").item(0);

            if(projSummary.getElementsByTagName("number_of_modules").getLength() <= 0
                    || projSummary.getElementsByTagName("lines_of_code").getLength() <= 0
                    || projSummary.getElementsByTagName("McCabes_cyclomatic_complexity").getLength() <= 0
                    || projSummary.getElementsByTagName("lines_of_comment").getLength() <= 0
            ) return;

            Element nom = (Element)
                    projSummary.getElementsByTagName("number_of_modules").item(0);

            Element loc = (Element)
                    projSummary.getElementsByTagName("lines_of_code").item(0);

            Element mvg = (Element)
                    projSummary.getElementsByTagName("McCabes_cyclomatic_complexity").item(0);

            Element com = (Element)
                    projSummary.getElementsByTagName("lines_of_comment").item(0);

            NodeList wmcList = doc.getElementsByTagName("weighted_methods_per_class_unity");
            NodeList ditList = doc.getElementsByTagName("depth_of_inheritance_tree");
            NodeList nocList = doc.getElementsByTagName("number_of_children");
            NodeList cboList = doc.getElementsByTagName("coupling_between_objects");

            if(wmcList.getLength() == 0
                    || ditList.getLength() == 0
                    || nocList.getLength() == 0
                    || cboList.getLength() == 0) return;

            int wmc = 0;
            for(int i = 0; i < wmcList.getLength(); i++) {
                Element wmce = (Element) wmcList.item(i);
                wmc += Integer.parseInt(wmce.getAttribute("value"));
            }

            int dit = Integer.MIN_VALUE;
            for(int i = 0; i < ditList.getLength(); i++) {
                Element dite = (Element) ditList.item(i);
                dit = Integer.parseInt(dite.getAttribute("value")) > dit ?
                        Integer.parseInt(dite.getAttribute("value")) : dit;
            }

            int noc = 0;
            for(int i = 0; i < nocList.getLength(); i++) {
                Element noce = (Element) nocList.item(i);
                noc = Integer.parseInt(noce.getAttribute("value"));
            }

            int cbo = Integer.MIN_VALUE;
            for(int i = 0; i < cboList.getLength(); i++) {
                Element cboe = (Element) cboList.item(i);
                cbo = Integer.parseInt(cboe.getAttribute("value")) > cbo ?
                        Integer.parseInt(cboe.getAttribute("value")) : cbo;
            }

            Store.hash.get(filename).nom = Integer.parseInt(nom.getAttribute("value"));
            Store.hash.get(filename).loc = Integer.parseInt(loc.getAttribute("value"));
            Store.hash.get(filename).mvg = Integer.parseInt(mvg.getAttribute("value"));
            Store.hash.get(filename).com = Integer.parseInt(com.getAttribute("value"));
            Store.hash.get(filename).wmc = wmc;
            Store.hash.get(filename).dit = dit;
            Store.hash.get(filename).noc = noc;
            Store.hash.get(filename).cbo = cbo;

        } catch(ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    void start() {
        if (thread == null)
        {
            thread = new Thread (this, filename);
            thread.start ();
        }
    }

}

