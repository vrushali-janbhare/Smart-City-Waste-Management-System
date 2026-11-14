
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Waste_Categories;
import mypackage.repository.Waste_Categories_Repository;

@Service
public class Waste_Categories_Service {

    @Autowired
    private Waste_Categories_Repository wasteCategoriesRepository;

    // Add Waste Category
    public Waste_Categories addWasteCategory(Waste_Categories category) {
        return wasteCategoriesRepository.save(category);
    }

    // Get All Waste Categories
    public List<Waste_Categories> getAllWasteCategories() {
        return wasteCategoriesRepository.findAll();
    }

    // Get Waste Category By ID
    public Waste_Categories getWasteCategoryById(Long id) {
        return wasteCategoriesRepository.findById(id).orElse(null);
    }

    // Update Waste Category
    public Waste_Categories updateWasteCategory(Long id, Waste_Categories category) {

        Waste_Categories existingCategory = wasteCategoriesRepository.findById(id).orElse(null);

        if (existingCategory != null) {

            existingCategory.setCategory_name(category.getCategory_name());
            existingCategory.setDescription(category.getDescription());
            existingCategory.setWaste_type(category.getWaste_type());
            existingCategory.setIs_recyclable(category.isIs_recyclable());

            return wasteCategoriesRepository.save(existingCategory);
        }

        return null;
    }

    // Delete Waste Category
    public void deleteWasteCategory(Long id) {
        wasteCategoriesRepository.deleteById(id);
    }
}
