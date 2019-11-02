package pl.edu.agh.assetory.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ComputerInformation {
    public static final String COMPUTER_ID_FIELD = "computerId";

    private String id;
    private String computerId;
    private String dateTime;
    private Map<String, String> system;
    private Map<String, String> hardware;
    private Map<String, List<SoftwareRecord>> software;
}
