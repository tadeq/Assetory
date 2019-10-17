package pl.edu.agh.assetory.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(indexName = "assetory", type = "computerInfo")
public class ComputerInformation {
    private String id;
    private String computerId;
    private String dateTime;
    private Map<String, String> system;
    private Map<String, String> hardware;
    private List<SoftwareRecord> software;
}
