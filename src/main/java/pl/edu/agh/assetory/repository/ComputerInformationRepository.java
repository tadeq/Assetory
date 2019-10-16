package pl.edu.agh.assetory.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.edu.agh.assetory.model.client.ComputerInformation;

public interface ComputerInformationRepository extends ElasticsearchRepository<ComputerInformation, String> {
}
