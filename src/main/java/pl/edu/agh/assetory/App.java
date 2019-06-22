package pl.edu.agh.assetory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class App implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Autowired
    private AssetsService assetsService;
    @Autowired
    private CategoriesService categoriesService;

    public static void main(String[] args) {
        log.info("Starting app");
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //Mock asset
        categoriesService.addCategory(new Category("1", "All", "all", ImmutableList.of("Owner")));
        categoriesService.addCategory(new Category("2", "Software", "all.software", ImmutableList.of("Expiration date")));
        categoriesService.addCategory(new Category("3", "Hardware", "all.hardware", ImmutableList.of("Manufacturer")));
        assetsService.addAsset(new Asset("1", "Asset number one", "Software",
                ImmutableMap.<String, String>builder()
                        .put("Owner", "PREZES")
                        .put("Expiration date", "23.06.2019")
                        .build()));


    }

}