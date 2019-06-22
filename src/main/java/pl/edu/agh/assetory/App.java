package pl.edu.agh.assetory;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.service.AssetsService;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class App implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Autowired
    private AssetsService assetsService;

    public static void main(String[] args) {
        log.info("Starting app");
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //Mock asset
        assetsService.addAsset(new Asset("1", "Asset number one",
                ImmutableMap.<String, String>builder()
                        .put("function", "PREZES")
                        .put("nickname", "łysy Dżon")
                        .build()));
    }

}