package pl.edu.agh.assetory.model.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SoftwareRecord {
    private String name;
    private String version;
    private String publisher;
    private String installDate;
}
