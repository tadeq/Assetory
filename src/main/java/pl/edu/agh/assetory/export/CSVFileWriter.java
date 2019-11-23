package pl.edu.agh.assetory.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVFileWriter {
    public File createCSVFile(String filename, List<List<String>> data) throws FileNotFoundException {
        File csvFile = new File(filename);
        try (PrintWriter printWriter = new PrintWriter(csvFile)) {
            data.forEach(line -> printWriter.println(String.join(",", line)));
        }
        return csvFile;
    }
}