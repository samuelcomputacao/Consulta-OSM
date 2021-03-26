

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class OSMNode {
    private String id;

    private String lat;

    private String lon;

    private String version;

    private Map<String, String> tags;

//    public OSMNode(String id, String latitude, String longitude, String version, Map<String, String> tags) {
//        this.id = id;
//        this.lat = latitude;
//        this.lon = longitude;
//        this.version = version;
//        this.tags = tags;
//    }
}
