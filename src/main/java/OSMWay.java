import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class OSMWay {

    private final String id;

    private final ArrayList<String> refNodesIDs;

//    private LinkedHashMap<String, OSMNode> refNodes;

    private final Map<String, String> tags;

    private final String version;

//    @Override
//    public String toString() {
//        return "OSMWay [id=" + id + ", refNodesIDs=" + refNodesIDs + ", tags="
//                + tags + ", version=" + version + "]";
//    }
}