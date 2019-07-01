package pl.edu.agh.assetory.model;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTree {
    Category category;
    List<CategoryTree> subCategories;

    public CategoryTree(Category category, List<CategoryTree> subCategories) {
        this.category = category;
        this.subCategories = subCategories;
    }
}
