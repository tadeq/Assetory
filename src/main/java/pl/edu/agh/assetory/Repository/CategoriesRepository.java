package pl.edu.agh.assetory.Repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.edu.agh.assetory.model.Category;

import java.util.List;

public interface CategoriesRepository extends ElasticsearchRepository<Category, String> {
    List<Category> findCategoriesByName(String name);
}
