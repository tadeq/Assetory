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
import pl.edu.agh.assetory.service.ComputerInformationService;

import java.io.IOException;
import java.util.Random;

@SpringBootApplication()
@PropertySource("classpath:application.properties")
public class App implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Autowired
    private AssetsService assetsService;
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private ComputerInformationService computerInformationService;

    public static void main(String[] args) {
        log.info("Starting app");
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) {
//        try {
//            assetsService.putMappings();
//            categoriesService.putMappings();
//            computerInformationService.putMappings();
//            prepareTestStructure();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private String getRandomString(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    private void prepareTestStructure() throws IOException {
        String[] users = {"John", "Xavier", "Carlos", "Juan", "Joe", "Tony", "Albert", "Cleo", "Phil"};
        Category all = categoriesService.addCategory(Category.builder()
                .name("All")
                .addRequiredAttribute("Owner", AttributeType.text)
                .addRequiredAttribute("User", AttributeType.text)
                .addRequiredAttribute("Location", AttributeType.text)
                .addRequiredAttribute("Price", AttributeType.number)
                .build());

        Category hardware = categoriesService.addCategory(Category.builder()
                .parentCategoryId(all.getId())
                .name("Hardware")
                .addAttribute("Manufacturer", AttributeType.text)
                .build());

        Category computers = categoriesService.addCategory(Category.builder()
                .parentCategoryId(hardware.getId())
                .name("Computers")
                .addAttribute("Processor", AttributeType.text)
                .addAttribute("RAM(Gb)", AttributeType.number)
                .addAttribute("Hard disc(Gb)", AttributeType.number)
                .addAttribute("Graphics card", AttributeType.text)
                .build());

        Category laptops = categoriesService.addCategory(Category.builder()
                .parentCategoryId(computers.getId())
                .name("Laptops")
                .addAttribute("Display size", AttributeType.number)
                .build());

        Category desktops = categoriesService.addCategory(Category.builder()
                .parentCategoryId(computers.getId())
                .name("Desktops")
                .build());

        Category networkHardware = categoriesService.addCategory(Category.builder()
                .parentCategoryId(hardware.getId())
                .name("Network hardware")
                .build());
        Category routers = categoriesService.addCategory(Category.builder()
                .parentCategoryId(networkHardware.getId())
                .name("Routers")
                .build());
        Category networkCards = categoriesService.addCategory(Category.builder()
                .parentCategoryId(networkHardware.getId())
                .name("Network cards")
                .build());

        Category peripherals = categoriesService.addCategory(Category.builder()
                .parentCategoryId(hardware.getId())
                .name("Peripherals")
                .build());

        Category software = categoriesService.addCategory(Category.builder()
                .parentCategoryId(all.getId())
                .name("Software")
                .addAttribute("License Expiration Date", AttributeType.date)
                .addAttribute("Software version", AttributeType.text)
                .build());

        Category operatingSystems = categoriesService.addCategory(Category.builder()
                .parentCategoryId(software.getId())
                .name("Operating systems")
                .build());
        Category officeTools = categoriesService.addCategory(Category.builder()
                .parentCategoryId(software.getId())
                .name("Office tools")
                .build());
        Category antiviruses = categoriesService.addCategory(Category.builder()
                .parentCategoryId(software.getId())
                .name("Antiviruses")
                .build());
        String administrator = "Administrator";
        for (int i = 0; i < 5; i++) {
            String[] locations = {"Office", "Room", "Lab", "Meeting Room"};
            //hardware
            assetsService.addAsset(Asset.builder()
                    .categoryId(hardware.getId())
                    .name("iPhone 7" + " - xyz00" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(2500 - i * 100))
                    .addAttribute(AttributeType.text, "Manufacturer", "Apple")
                    .build());
            String[] processors = {"AMD", "AMD APU", "AMD Ryzen", "Intel Atom", "Intel Celeron", "Intel Core i3",
                    "Intel Core i5", "Intel Core i7", "Intel Core i9", "Intel Pentium", "Intel Xeon"};
            String[] ram = {"8", "16", "32"};
            String[] hdd = {"128", "512", "1024"};
            String[] graphics = {"GeForce GTX 1650", "Radeon Vega 11", "GeForce RTX 2070", "GeForce GTX 1050"};
            //laptops
            String[] displaySizes = {"15", "17"};
            final Asset laptop = Asset.builder()
                    .categoryId(laptops.getId())
                    .name("Lenovo IdeaPadx" + i * 100 + " - fcd00" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(4500 - i * 250))
                    .addAttribute(AttributeType.text, "Manufacturer", "Lenovo")
                    .addAttribute(AttributeType.text, "Processor", getRandomString(processors))
                    .addAttribute(AttributeType.number, "RAM(Gb)", getRandomString(ram))
                    .addAttribute(AttributeType.number, "Hard disc(Gb)", getRandomString(hdd))
                    .addAttribute(AttributeType.text, "Graphics card", getRandomString(graphics))
                    .addAttribute(AttributeType.number, "Display size", getRandomString(displaySizes))
                    .build();
            assetsService.addAsset(laptop);
            //desktops
            final Asset desktop = Asset.builder()
                    .categoryId(desktops.getId())
                    .name("Infinity g3" + 10 * (i + 1) + " - asd0" + i * 11)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(6200 - i * 250))
                    .addAttribute(AttributeType.text, "Processor", getRandomString(processors))
                    .addAttribute(AttributeType.number, "RAM(Gb)", getRandomString(ram))
                    .addAttribute(AttributeType.number, "Hard disc(Gb)", getRandomString(hdd))
                    .addAttribute(AttributeType.text, "Graphics card", getRandomString(graphics))
                    .addAttribute(AttributeType.text, "Manufacturer", "IBM")
                    .build();
            assetsService.addAsset(desktop);

            //network cards
            assetsService.addAsset(Asset.builder()
                    .categoryId(networkCards.getId())
                    .name("TP-Link TL-WN72" + i + "N" + " - wdi" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(620 - i * 25))
                    .addAttribute(AttributeType.text, "Manufacturer", "TP-LINK")
                    .build());
            //routers
            assetsService.addAsset(Asset.builder()
                    .categoryId(routers.getId())
                    .name("TP-Link Archer C20" + "-agd" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(120 - i * 5))
                    .addAttribute(AttributeType.text, "Manufacturer", "TP-LINK")
                    .build());
            //peripherals
            assetsService.addAsset(Asset.builder()
                    .categoryId(peripherals.getId())
                    .name("HP OfficeJet PRO 6970 AiO" + "-kks" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(240 - i * 5))
                    .addAttribute(AttributeType.text, "Manufacturer", "HP")
                    .build());

            String[] softwareVersions = {"1.2.4", "10.12.2", "1.1.4", "4.0", "10.0.12", "3.7", "2.13", "11.22.14"};
            String[] operatingSystemsNames = {"Windows 10", "Windows 8/8.1", "Windows 7", "Windows Vista", "Other", "macOS", "Linux"};
            //operating systems
            final Asset operatingSystem = Asset.builder()
                    .categoryId(operatingSystems.getId())
                    .name(getRandomString(operatingSystemsNames) + "-sys" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(450 - i * 20))
                    .addAttribute(AttributeType.date, "License Expiration Date", "2020-07-" + (i * 2 + 1))
                    .addAttribute(AttributeType.text, "Software version", getRandomString(softwareVersions))
                    .build();
            assetsService.addAsset(operatingSystem);

            String[] officeToolsNames = {"Microsoft Ultimate 2007", "Microsoft Ultimate 2016", "Libre Office"};
            //office tools
            final Asset officeTool = Asset.builder()
                    .categoryId(officeTools.getId())
                    .name(getRandomString(officeToolsNames) + "-qwe" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(250 - i * 20))
                    .addAttribute(AttributeType.date, "License Expiration Date", "2020-08-" + (i * 2 + 5))
                    .addAttribute(AttributeType.text, "Software version", getRandomString(softwareVersions))
                    .build();
            assetsService.addAsset(officeTool);

            String[] antivirusesNames = {"BitDefender 2020", "Norton", "Panda", "TotalAV", "BullGuard"};
            //antiviruses
            final Asset antivirus = Asset.builder()
                    .categoryId(antiviruses.getId())
                    .name(getRandomString(antivirusesNames) + "-vir" + i)
                    .addAttribute(AttributeType.text, "Owner", administrator)
                    .addAttribute(AttributeType.text, "User", getRandomString(users))
                    .addAttribute(AttributeType.text, "Location", getRandomString(locations) + " " + i)
                    .addAttribute(AttributeType.number, "Price", String.valueOf(370 - i * 10))
                    .addAttribute(AttributeType.date, "License Expiration Date", "2020-09-" + (i * 3 + 1))
                    .addAttribute(AttributeType.text, "Software version", getRandomString(softwareVersions))
                    .build();
            assetsService.addAsset(antivirus);

            //related assets - operating systems & antiviruses to computers\laptops
            assetsService.saveAsset(Asset.builder()
                    .from(laptop)
                    .addRelatedAssets(Lists.newArrayList(operatingSystem.getId(), officeTool.getId(), antivirus.getId())).build());
            assetsService.saveAsset(Asset.builder()
                    .from(desktop)
                    .addRelatedAssets(Lists.newArrayList(operatingSystem.getId(), officeTool.getId(), antivirus.getId())).build());
        }
    }

}