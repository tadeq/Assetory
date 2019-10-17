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
//        prepareTestStructure();
    }

    private void prepareTestStructure() {

        Category categoryAll = categoriesService.addCategory(Category.builder()
                .name("All")
                .addAttribute("User", AttributeType.text)
                .addAttribute("Location", AttributeType.text)
                .addAttribute("Price", AttributeType.number)
                .build());
        Category hardware = categoriesService.addCategory(Category.builder()
                .parentCategoryId(categoryAll.getId())
                .name("Hardware")
                .addAttribute("Manufacturer", AttributeType.text)
                .build());
        Category subhardware1 = categoriesService.addCategory(Category.builder()
                .parentCategoryId(hardware.getId())
                .name("Sub-Hardware-1")
                .addAttribute("Sub-Hardware-Attribute", AttributeType.text)
                .build());
        Category software = categoriesService.addCategory(Category.builder()
                .parentCategoryId(categoryAll.getId())
                .name("Software")
                .addAttribute("Expiration Date", AttributeType.date)
                .build());
        Category subsoftware1 = categoriesService.addCategory(Category.builder()
                .parentCategoryId(software.getId())
                .name("Sub-Software-1")
                .build());
        Category subsoftware2 = categoriesService.addCategory(Category.builder()
                .parentCategoryId(software.getId())
                .name("Sub-Software-2")
                .build());
        categoriesService.updateCategory(Category.builder()
                .from(categoryAll)
                .addSubcategoryIds(Lists.newArrayList(hardware.getId(), software.getId()))
                .build());
        categoriesService.updateCategory(Category.builder()
                .from(software)
                .addSubcategoryIds(Lists.newArrayList(subsoftware1.getId(), subsoftware2.getId()))
                .build());
        categoriesService.updateCategory(Category.builder()
                .from(hardware)
                .addSubcategoryIds(Lists.newArrayList(subhardware1.getId()))
                .build());
        Asset computer = assetsService.addAsset(Asset.builder()
                .categoryId(hardware.getId())
                .name("Computer")
                .addAttribute(AttributeType.text, "User", "John")
                .addAttribute(AttributeType.text, "Location", "Office")
                .addAttribute(AttributeType.number, "Price", "2500")
                .addAttribute(AttributeType.text, "Manufacturer", "lenovo")
                .build());
        Asset laptop = assetsService.addAsset(Asset.builder()
                .categoryId(hardware.getId())
                .name("Laptop")
                .addAttribute(AttributeType.text, "User", "Johnatan")
                .addAttribute(AttributeType.text, "Location", "Office2")
                .addAttribute(AttributeType.number, "Price", "100")
                .addAttribute(AttributeType.text, "Manufacturer", "lenovo")
                .build());
        Asset windows = assetsService.addAsset(Asset.builder()
                .categoryId(software.getId())
                .name("Windows")
                .addAttribute(AttributeType.text, "User", "John2")
                .addAttribute(AttributeType.text, "Location", "Computer1")
                .addAttribute(AttributeType.number, "Price", "100")
                .addAttribute(AttributeType.date, "Expiration Date", "7-07-2020")
                .build());
        Asset subwindows1 = assetsService.addAsset(Asset.builder()
                .categoryId(subsoftware1.getId())
                .name("Windows")
                .addAttribute(AttributeType.text, "User", "John")
                .addAttribute(AttributeType.text, "Location", "Computer2")
                .addAttribute(AttributeType.number, "Price", "2500")
                .addAttribute(AttributeType.date, "Expiration Date", "14-07-2020")
                .build());
        Asset subwindows2 = assetsService.addAsset(Asset.builder()
                .categoryId(subsoftware2.getId())
                .name("Windows")
                .addAttribute(AttributeType.text, "User", "Johnatan")
                .addAttribute(AttributeType.text, "Location", "Computer1")
                .addAttribute(AttributeType.number, "Price", "1700")
                .addAttribute(AttributeType.date, "Expiration Date", "14-07-2020")
                .build());
        for (int i = 0; i < 100; i++) {
            assetsService.addAsset(Asset.builder()
                    .categoryId(i % 2 == 0 ? hardware.getId() : subhardware1.getId())
                    .name("Computer " + i)
                    .addAttribute(AttributeType.text, "User", "John " + i % 3)
                    .addAttribute(AttributeType.text, "Location", "Office " + i % 5)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(i * 200))
                    .addAttribute(AttributeType.text, "Manufacturer", "lenovo " + i % 3)
                    .build());
        }
        System.out.println(" ");

    }

}