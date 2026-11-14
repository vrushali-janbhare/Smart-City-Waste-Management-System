package mypackage.controller;

import mypackage.model.Area;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mypackage.service.Area_Service;

@RestController
@RequestMapping("/api/area")
public class Area_Controller {

	@Autowired
	private Area_Service areaServices;
	
	@PostMapping
	public Area addArea(@RequestBody Area area) {
		return areaServices.addArea(area);
	}
	  @GetMapping("/{id}")
	    public Area getAreaById(@PathVariable Long id) {
	        return areaServices.getAreaById(id);
	    }

	    // Get All Areas
	    @GetMapping
	    public List<Area> getAllAreas() {
	        return areaServices.getAllAreas();
	    }

	    // Update Area
	    @PutMapping("/{id}")
	    public Area updateArea(@PathVariable Long id,
	                           @RequestBody Area area) {
	        return areaServices.updateArea(id, area);
	    }

	    // Delete Area
	    @DeleteMapping("/{id}")
	    public String deleteArea(@PathVariable Long id) {
	        areaServices.deleteArea(id);
	        return "Area deleted successfully!";
	    }
	}
	

