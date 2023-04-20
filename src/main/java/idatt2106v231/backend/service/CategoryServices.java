package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.CategoryDto;
import org.springframework.stereotype.Service;

@Service
public class CategoryServices {


    //@Autowired
    //private CategoryRepository categoryRepository;

    /**
     * Method to assert a category exists
     *
     * @param categoryId the categorys id
     */
    /*
    public boolean categoryExist(int categoryId){

        return categoryRepository.findById(categoryId).isPresent();
    }
*/

    /**
     * Method to save a new category to database
     *
     * @param categoryDto the new category
     */
    public void saveCategory(CategoryDto categoryDto){
    }

    /**
     * Method to delete a category from database
     *
     * @param categoryId the category id
     */
    public void deleteCategory(int categoryId){
    }

    /**
     * Method to get a category
     *
     * @param categoryId the category id
     */
    public void getCategory(int categoryId){
    }

    /**
     * Method to get all categories
     *
     */
    public void getAllCategories(){
    }
}
