
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

    // Insert or update area
    public Area saveArea(Area area) {
        return areaRepository.save(area);
    }

    // Get all areas
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    // Get area by ID
    public Optional<Area> getAreaById(Long areaId) {
        return areaRepository.findById(areaId);
    }

    // Delete area by ID
    public void deleteArea(Long areaId) {
        areaRepository.deleteById(areaId);
    }
}
