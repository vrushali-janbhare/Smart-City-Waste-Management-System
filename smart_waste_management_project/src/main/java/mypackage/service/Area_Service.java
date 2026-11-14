package mypackage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Area;
import mypackage.repository.Area_Repository;

import java.util.List;

@Service
public class Area_Service {

    @Autowired
    private Area_Repository areaRepository;

    // Add Area
    public Area addArea(Area area) {
        return areaRepository.save(area);
    }

    // Get All Areas
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    // Get Area By ID
    public Area getAreaById(Long id) {
        return areaRepository.findById(id).orElse(null);
    }

    // Update Area
    public Area updateArea(Long id, Area area) {
        Area existingArea = areaRepository.findById(id).orElse(null);

        if (existingArea != null) {
            existingArea.setArea_name(area.getArea_name());
            return areaRepository.save(existingArea);
        }

        return null;
    }

    // Delete Area
    public String deleteArea(Long id) {
        areaRepository.deleteById(id);
        return "Area Deleted Successfully";
    }
}