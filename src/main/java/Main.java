import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        double lat = 48.13842;
        double lon = 11.57729;
        double radius = 0.0001;

        Document xml = OSMWrapperAPI.getXML(lat, lon, radius);
        Map<String, OSMNode> nodes = OSMWrapperAPI.getNodes(xml);
        Map<String, OSMWay>  aways = OSMWrapperAPI.getWays(xml);

        System.out.println("-------------------aways-------------------");
        for(String key: aways.keySet()){
            System.out.println(aways.get(key));
        }

        System.out.println("-------------------Nodes-------------------");
        for(String key: nodes.keySet()){
            System.out.println(nodes.get(key));
        }
    }
}
