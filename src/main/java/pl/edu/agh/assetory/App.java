package pl.edu.agh.assetory;

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
import pl.edu.agh.assetory.model.attributes.AttributeType;
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
        prepareTestStructure();
    }

    private void prepareTestStructure() {
        Category categoryAll = categoriesService.addCategory(Category.builder()
                .name("all")
                .addAttribute("user", AttributeType.text)
                .addAttribute("location", AttributeType.text)
                .addAttribute("price", AttributeType.number)
                .build());
        Category hardware = categoriesService.addCategory(Category.builder()
                .parentCategoryId(categoryAll.getId())
                .name("hardware")
                .addAttribute("manufacturer", AttributeType.text)
                .build());
        Category software = categoriesService.addCategory(Category.builder()
                .parentCategoryId(categoryAll.getId())
                .name("software")
                .addAttribute("expirationDate", AttributeType.date)
                .build());
        categoriesService.updateCategory(Category.builder()
                .from(categoryAll)
                .addSubcategoryIds(Lists.newArrayList(hardware.getId(), software.getId()))
                .build());
        Asset computer = assetsService.addAsset(Asset.builder()
                .categoryId(hardware.getId())
                .name("Computer")
                .addAttribute(AttributeType.text, "user", "John")
                .addAttribute(AttributeType.text, "location", "office")
                .addAttribute(AttributeType.number, "price", "2500")
                .addAttribute(AttributeType.text, "manufacturer", "Lenovo")
                .build());
        Asset windows = assetsService.addAsset(Asset.builder()
                .categoryId(software.getId())
                .name("Windows")
                .addAttribute(AttributeType.text, "user", "John")
                .addAttribute(AttributeType.text, "location", "computer1")
                .addAttribute(AttributeType.number, "price", "100")
                .addAttribute(AttributeType.date, "expirationDate", "7-07-2020")
                .build());
        System.out.println(" ");
    }

}