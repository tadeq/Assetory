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
        //Mock asset
        categoriesService.addCategory(new Category("1", "All", "all", Lists.newArrayList("Owner")));
        Category software = new Category("2", "Software", "all" + Category.PATH_SEPARATOR + "software", Lists.newArrayList("Expiration date"));
        Category subSoftware = new Category("4", "SubSoftware", "all" + Category.PATH_SEPARATOR + "software" + Category.PATH_SEPARATOR + "subsoftware", Lists.newArrayList("Expiration date2"));
        categoriesService.addCategory(subSoftware);
        categoriesService.addCategory(software);
        categoriesService.addCategory(new Category("3", "Hardware", "all" + Category.PATH_SEPARATOR + "hardware", Lists.newArrayList("Manufacturer")));
        assetsService.addAsset(new Asset("1", "Asset number one", "Software",
                ImmutableMap.<String, String>builder()
                        .put("Owner", "PREZES")
                        .put("Expiration date", "23.06.2019")
                        .build()));
        Iterable<Category> categoryList = categoriesService.getSuperCategories(software);
        categoryList.forEach(category -> log.info(category.getName()));
    }

}