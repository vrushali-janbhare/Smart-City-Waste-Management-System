
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Area;
import mypackage.repository.Area_Repository;

@Service
public class Area_Service {

    @Autowired
    private Area_Repository areaRepository;

    // Add new area
    public Area saveArea(Area area) {

        validateArea(area);

        Optional<Area> duplicate =
                areaRepository.findByWardNumberIgnoreCase(
                        area.getWardNumber());

        if (duplicate.isPresent()) {
            throw new IllegalArgumentException(
                    "Ward number already exists.");
        }

        Area newArea = new Area();

        newArea.setAreaName(area.getAreaName().trim());
        newArea.setWardNumber(area.getWardNumber().trim());
        newArea.setCity(area.getCity().trim());

        return areaRepository.save(newArea);
    }

    // Get all areas
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    // Get area by ID
    public Optional<Area> getAreaById(Long areaId) {
        return areaRepository.findById(areaId);
    }

    // Update existing area
    public Optional<Area> updateArea(
            Long areaId, Area updatedArea) {

        validateArea(updatedArea);

        Optional<Area> existing =
                areaRepository.findById(areaId);

        if (!existing.isPresent()) {
            return Optional.empty();
        }

        Optional<Area> duplicate =
                areaRepository.findByWardNumberIgnoreCase(
                        updatedArea.getWardNumber());

        if (duplicate.isPresent()
                && !duplicate.get().getAreaId().equals(areaId)) {

            throw new IllegalArgumentException(
                    "Ward number already exists.");
        }

        Area area = existing.get();

        area.setAreaName(updatedArea.getAreaName().trim());
        area.setWardNumber(updatedArea.getWardNumber().trim());
        area.setCity(updatedArea.getCity().trim());

        return Optional.of(areaRepository.save(area));
    }

    // Validate area fields
    private void validateArea(Area area) {

        if (area == null) {
            throw new IllegalArgumentException(
                    "Area details are required.");
        }

        if (area.getAreaName() == null
                || area.getAreaName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Area name is required.");
        }

        if (area.getAreaName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Area name must not exceed 100 characters.");
        }

        if (area.getWardNumber() == null
                || area.getWardNumber().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward number is required.");
        }

        if (area.getWardNumber().trim().length() > 20) {
            throw new IllegalArgumentException(
                    "Ward number must not exceed 20 characters.");
        }

        if (area.getCity() == null
                || area.getCity().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "City name is required.");
        }

        if (area.getCity().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "City name must not exceed 100 characters.");
        }
    }
}
