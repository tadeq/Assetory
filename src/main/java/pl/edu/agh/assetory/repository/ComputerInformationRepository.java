package pl.edu.agh.assetory.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.edu.agh.assetory.model.client.ComputerInformation;

import java.util.List;

public interface ComputerInformationRepository extends ElasticsearchRepository<ComputerInformation, String> {
    List<ComputerInformation> getComputerInformationsByComputerId(String computerId);
}
