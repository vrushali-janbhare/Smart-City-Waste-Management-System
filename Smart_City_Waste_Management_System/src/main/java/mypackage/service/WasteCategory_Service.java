
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.WasteCategory;
import mypackage.repository.WasteCategory_Repository;

@Service
public class WasteCategory_Service {

    @Autowired
    private WasteCategory_Repository wasteCategoryRepository;

    // Insert or update waste category
    public WasteCategory saveWasteCategory(WasteCategory category) {
        return wasteCategoryRepository.save(category);
    }

    // Get all waste categories
    public List<WasteCategory> getAllWasteCategories() {
        return wasteCategoryRepository.findAll();
    }

    // Get waste category by ID
    public Optional<WasteCategory> getWasteCategoryById(Long categoryId) {
        return wasteCategoryRepository.findById(categoryId);
    }

    // Delete waste category by ID
    public void deleteWasteCategory(Long categoryId) {
        wasteCategoryRepository.deleteById(categoryId);
    }
}
