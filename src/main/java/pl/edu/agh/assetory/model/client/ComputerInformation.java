package pl.edu.agh.assetory.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Document(indexName = "assetory", type = "computerInfo")
public class ComputerInformation {
    private String reportId;
    private String computerId;
    private LocalDateTime dateTime;
    private Map<String, String> system;
    private Map<String, String> hardware;
    private List<SoftwareRecord> software;
}
