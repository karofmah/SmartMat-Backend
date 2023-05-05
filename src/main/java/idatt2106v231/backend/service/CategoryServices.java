package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.CategoryDto;

import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class to manage Category objects.
 */
@Service
public class CategoryServices {

    private final CategoryRepository catRepo;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the Category repository to use for database access.
     */
    @Autowired
    public CategoryServices(CategoryRepository catRepo) {
        this.catRepo = catRepo;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to save a new category to database.
     *
     * @param categoryDto the new category
     * @return true if the category is saved
     */
    public boolean saveCategory(CategoryDto categoryDto){
        try {
            Category cat = mapper.map(categoryDto, Category.class);
            catRepo.save(cat);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete a category from database.
     *
     * @param categoryId the category id
     * @return true if the category is deleted
     */
    public boolean deleteCategory(int categoryId){
        try {
            catRepo.deleteById(categoryId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to get a category.
     *
     * @param categoryId the category id
     * @return the category as a dto object
     */
    public CategoryDto getCategory(int categoryId){
        try {
            return mapper.map(catRepo.findById(categoryId).get(), CategoryDto.class);
        }
        catch (Exception e) {
            return null;
        }

    }

    /**
     * Method to get all categories
     *
     * @return all categories
     */
    public List<CategoryDto> getAllCategories(){
        try {
           return catRepo.findAll()
                    .stream()
                    .map(obj -> mapper.map(obj, CategoryDto.class))
                    .toList();
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to assert a category exists by id.
     *
     * @param categoryId the category id
     * @return true if the category exists
     */
    public boolean categoryNotExist(int categoryId){
        return !catRepo.existsCategoriesByCategoryId(categoryId);
    }

    /**
     * Method to assert a category exists by description
     *
     * @param description the category description
     * @return true if the category exists
     */
    public boolean categoryExist(String description){
        return catRepo.existsCategoriesByDescription(description);
    }
}