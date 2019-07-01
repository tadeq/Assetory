package pl.edu.agh.assetory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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

import java.math.BigDecimal;

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
    public void run(String... strings) {
        Category all = new Category("1", "All", Lists.newArrayList("Owner"));
        Category software = new Category("2", "Software", Lists.newArrayList("Expiration date"), all.getId());
        all.addSubcategory(software.getId());
        Category subSoftware = new Category("4", "SubSoftware", Lists.newArrayList("Expiration date2"), software.getId());
        software.addSubcategory(subSoftware.getId());
        Category hardware = new Category("3", "Hardware", Lists.newArrayList("Manufacturer"), all.getId());
        all.addSubcategory(hardware.getId());
        categoriesService.addCategory(all);
        categoriesService.addCategory(hardware);
        categoriesService.addCategory(subSoftware);
        categoriesService.addCategory(software);

        assetsService.addAsset(createSampleAsset());
    }

    private Asset createSampleAsset() {
        return new Asset("1",
                "Asset number one",
                "2",
                ImmutableMap.<String, String>builder()
                        .put("Owner", "PREZES")
                        .put("Expiration date", "23.06.2019")
                        .build(),
                "localisation1",
                "backup1",
                "license1",
                new BigDecimal(123),
                "owner1",
                "user1");
    }

}