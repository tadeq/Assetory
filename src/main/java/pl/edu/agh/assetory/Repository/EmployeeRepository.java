package pl.edu.agh.assetory.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import pl.edu.agh.assetory.model.Employee;

@Repository
public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {

    /**
     * Method to fetch the employee details on the basis of designation by using Elastic-Search-Repository.
     * @param designation
     * @return
     */
    public List<Employee> findByDesignation(String designation);

}