
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

    // ==========================================
    // 1. ADD NEW WASTE CATEGORY
    // ==========================================

    public WasteCategory saveWasteCategory(WasteCategory category) {

        if (category == null) {
            throw new IllegalArgumentException(
                "Waste category details are required."
            );
        }

        String categoryName = validateCategoryName(
            category.getCategoryName()
        );

        WasteCategory existingCategory =
            wasteCategoryRepository.findByCategoryNameIgnoreCase(
                categoryName
            );

        if (existingCategory != null &&
            !existingCategory.getCategoryId().equals(
                category.getCategoryId()
            )) {

            throw new IllegalArgumentException(
                "Waste category already exists."
            );
        }

        category.setCategoryName(categoryName);

        return wasteCategoryRepository.save(category);
    }

    // ==========================================
    // 2. GET ALL WASTE CATEGORIES
    // ==========================================

    public List<WasteCategory> getAllWasteCategories() {

        return wasteCategoryRepository.findAll();
    }

    // ==========================================
    // 3. GET WASTE CATEGORY BY ID
    // ==========================================

    public Optional<WasteCategory> getWasteCategoryById(
            Long categoryId) {

        return wasteCategoryRepository.findById(categoryId);
    }

    // ==========================================
    // 4. UPDATE WASTE CATEGORY
    // ==========================================

    public WasteCategory updateWasteCategory(
            Long categoryId,
            WasteCategory updatedCategory) {

        if (updatedCategory == null) {
            throw new IllegalArgumentException(
                "Waste category details are required."
            );
        }

        WasteCategory existingCategory =
            wasteCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Waste category not found."
                    )
                );

        String categoryName = validateCategoryName(
            updatedCategory.getCategoryName()
        );

        WasteCategory duplicateCategory =
            wasteCategoryRepository.findByCategoryNameIgnoreCase(
                categoryName
            );

        if (duplicateCategory != null &&
            !duplicateCategory.getCategoryId().equals(categoryId)) {

            throw new IllegalArgumentException(
                "Another waste category already uses this name."
            );
        }

        existingCategory.setCategoryName(categoryName);

        existingCategory.setRecyclable(
            updatedCategory.isRecyclable()
        );

        existingCategory.setHazardous(
            updatedCategory.isHazardous()
        );

        return wasteCategoryRepository.save(existingCategory);
    }

    // ==========================================
    // 5. VALIDATE CATEGORY NAME
    // ==========================================

    private String validateCategoryName(String categoryName) {

        if (categoryName == null ||
            categoryName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Waste category name is required."
            );
        }

        String cleanedName = categoryName.trim();

        if (cleanedName.length() > 100) {

            throw new IllegalArgumentException(
                "Category name must not exceed 100 characters."
            );
        }

        return cleanedName;
    }

    // ==========================================
    // 6. DELETE WASTE CATEGORY
    // Kept for existing controller compatibility.
    // We will not expose deletion in the Admin UI.
    // ==========================================

    public void deleteWasteCategory(Long categoryId) {

        wasteCategoryRepository.deleteById(categoryId);
    }
}
