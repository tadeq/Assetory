package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.client.ComputerInformation;
import pl.edu.agh.assetory.service.ComputerInformationService;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "info")
public class ComputerInfoController {
    private ComputerInformationService informationService;

    @Autowired
    ComputerInfoController(ComputerInformationService computerInformationService) {
        this.informationService = computerInformationService;
    }

    @PostMapping
    @ApiOperation(value = "saves new report from connected machine",
            response = ComputerInformation.class)
    public ResponseEntity<?> addComputerInformation(@RequestBody ComputerInformation info) throws IOException {
        return info.getId() == null ? ResponseEntity.badRequest().body("Report id was not provided")
                : ResponseEntity.ok(informationService.addComputerInformation(info));
    }

    @GetMapping
    @ApiOperation(value = "returns all gathered reports",
            response = ComputerInformation.class,
            responseContainer = "List")
    public ResponseEntity<?> getAllReports() throws IOException {
        return ResponseEntity.ok(informationService.getAllComputerInformation());
    }

    @GetMapping("/{computerId}")
    @ApiOperation(value = "returns reports for machine with given id",
            response = ComputerInformation.class,
            responseContainer = "List")
    public ResponseEntity<?> getReportsForComputer(@PathVariable String computerId) throws IOException {
        return ResponseEntity.ok(informationService.findByComputerId(computerId));
    }

    @GetMapping("/{computerId}/{date}")
    @ApiOperation(value = "returns report for given computer and date",
            response = ComputerInformation.class)
    public ResponseEntity<?> getReportForComputer(@PathVariable String computerId, @PathVariable String date) throws IOException {
        return informationService.findByComputerIdAndDate(computerId, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/registered/identifiers")
    @ApiOperation(value = "returns all registered computers identifiers",
            response = String.class,
            responseContainer = "List")
    public ResponseEntity<?> getComputersIdentifiers() throws IOException {
        return ResponseEntity.ok(informationService.getAllComputerInformation().stream()
                .map(ComputerInformation::getComputerId)
                .collect(Collectors.toSet()));
    }
}
