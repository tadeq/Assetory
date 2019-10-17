package pl.edu.agh.assetory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.client.ComputerInformation;
import pl.edu.agh.assetory.repository.ComputerInformationRepository;

import java.util.List;

@Service
public class ComputerInformationService {
    @Autowired
    @Qualifier("computerInformationRepository")
    private ComputerInformationRepository computerInformationRepository;

    public ComputerInformation saveInformation(ComputerInformation computerInformation) {
        return computerInformationRepository.save(computerInformation);
    }

    public Iterable<ComputerInformation> getAllReports() {
        return computerInformationRepository.findAll();
    }

    public List<ComputerInformation> getReportsForComputer(String computerId) {
        return computerInformationRepository.getComputerInformationsByComputerId(computerId);
    }
}
