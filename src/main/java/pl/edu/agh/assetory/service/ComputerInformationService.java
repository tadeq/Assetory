package pl.edu.agh.assetory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pl.edu.agh.assetory.model.client.ComputerInformation;
import pl.edu.agh.assetory.repository.ComputerInformationRepository;

public class ComputerInformationService {
    @Autowired
    @Qualifier("computerInformationRepository")
    private ComputerInformationRepository computerInformationRepository;

    public ComputerInformation saveInformation (ComputerInformation computerInformation) {
        return computerInformationRepository.save(computerInformation);
    }
}
