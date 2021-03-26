import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OSMWrapperAPI {

    private static final String OPENSTREETMAP_API_06 = "https://www.openstreetmap.org/api/0.6/";

    public static Document getXML(double lat, double lon, double vicinityRange) throws IOException, SAXException,
            ParserConfigurationException {

        DecimalFormat format = new DecimalFormat("##0.0000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH)); //$NON-NLS-1$
        String left = format.format(lon - vicinityRange);
        String bottom = format.format(lat - vicinityRange);
        String right = format.format(lon + vicinityRange);
        String top = format.format(lat + vicinityRange);

        String string = OPENSTREETMAP_API_06 + "map?bbox=" + left + "," + bottom + "," + right + ","
                + top;

//      String string = "https://api.openstreetmap.org/api/0.6/map?bbox=11.54,48.14,11.543,48.145";
        URL osm = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
        connection.setRequestMethod("GET");

        int status =  connection.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM) {
            String location = connection.getHeaderField("Location");
            URL newUrl = new URL(location);
            connection = (HttpURLConnection) newUrl.openConnection();
        }

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document tempDoc = docBuilder.parse(connection.getInputStream());

        return tempDoc;
    }

    public static Map<String, OSMWay> getMultipolygons(Document xmlDocument) {

        Map<String, OSMWay> osmWays = new HashMap<>();

        Node osmRoot = xmlDocument.getFirstChild();
        NodeList osmXMLNodes = osmRoot.getChildNodes();

        for (int i = 1; i < osmXMLNodes.getLength(); i++) {

            Node item = osmXMLNodes.item(i);
            if (item.getNodeName().equals("way")) {
                boolean hasBuildingTag = false;
                NodeList ndOrTagXMLNodes = item.getChildNodes();
                ArrayList<String> refNodesIDs = new ArrayList<>();
                Map<String, String> tags = new HashMap<>();
                for (int j = 1; j < ndOrTagXMLNodes.getLength(); j++) {
                    Node ndOrTagItem = ndOrTagXMLNodes.item(j);
                    NamedNodeMap ndOrTagAttributes = ndOrTagItem.getAttributes();
                    if (ndOrTagAttributes != null) {
                        if (ndOrTagItem.getNodeName().equals("tag")) {
                            tags.put(ndOrTagAttributes.getNamedItem("k").getNodeValue(), ndOrTagAttributes.getNamedItem("v")
                                    .getNodeValue());
                            if (ndOrTagAttributes.getNamedItem("k").getNodeValue().startsWith("building")) {
                                hasBuildingTag = true;
                            }
                        } else if (ndOrTagItem.getNodeName().equals("nd")) {
                            refNodesIDs.add(ndOrTagAttributes.getNamedItem("ref").getNodeValue());
                        }
                    }
                }
                NamedNodeMap attributes = item.getAttributes();
                Node namedItemID = attributes.getNamedItem("id");
                Node namedItemVersion = attributes.getNamedItem("version");

                String id = namedItemID.getNodeValue();
                String version = "0";
                if (namedItemVersion != null) {
                    version = namedItemVersion.getNodeValue();
                }

                if (hasBuildingTag) {
                    osmWays.put(id, new OSMWay(id, refNodesIDs, tags, version));
                }
            }

        }
        return osmWays;
    }

    public static Map<String, OSMNode> getNodes(Document xmlDocument) {

        HashMap<String, OSMNode> osmNodes = new HashMap<>();
        Node osmRoot = xmlDocument.getFirstChild();
        NodeList osmXMLNodes = osmRoot.getChildNodes();

        for (int i = 1; i < osmXMLNodes.getLength(); i++) {
            Node item = osmXMLNodes.item(i);
            if (item.getNodeName().equals("node")) {
                NodeList tagXMLNodes = item.getChildNodes();
                Map<String, String> tags = new HashMap<String, String>();
                for (int j = 1; j < tagXMLNodes.getLength(); j++) {
                    Node tagItem = tagXMLNodes.item(j);
                    NamedNodeMap tagAttributes = tagItem.getAttributes();
                    if (tagAttributes != null) {
                        tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
                                .getNodeValue());
                    }
                }
                NamedNodeMap attributes = item.getAttributes();
                Node namedItemID = attributes.getNamedItem("id");
                Node namedItemLat = attributes.getNamedItem("lat");
                Node namedItemLon = attributes.getNamedItem("lon");
                Node namedItemVersion = attributes.getNamedItem("version");

                String id = namedItemID.getNodeValue();
                String latitude = namedItemLat.getNodeValue();
                String longitude = namedItemLon.getNodeValue();
                String version = "0";
                if (namedItemVersion != null) {
                    version = namedItemVersion.getNodeValue();
                }
                osmNodes.put(id, new OSMNode(id, latitude, longitude, version, tags));
            }
        }
        return osmNodes;
    }

    public  static Map<String, OSMWay> getWays(Document xml) {
        Map<String, OSMWay> osmWaysInVicinity = OSMWrapperAPI.getMultipolygons(xml);
        return  osmWaysInVicinity;
    }
}