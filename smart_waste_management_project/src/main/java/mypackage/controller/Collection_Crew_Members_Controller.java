
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Collection_Crew_Members;
import mypackage.service.Collection_Crew_Members_Service;

@RestController
@RequestMapping("/api/collection-crew-members")
public class Collection_Crew_Members_Controller {

    @Autowired
    private Collection_Crew_Members_Service crewService;

    // Add Crew Member
    @PostMapping
    public Collection_Crew_Members addCrewMember(
            @RequestBody Collection_Crew_Members crew) {

        return crewService.addCrewMember(crew);
    }

    // Get Crew Member By ID
    @GetMapping("/{id}")
    public Collection_Crew_Members getCrewMemberById(
            @PathVariable Long id) {

        return crewService.getCrewMemberById(id);
    }

    // Get All Crew Members
    @GetMapping
    public List<Collection_Crew_Members> getAllCrewMembers() {

        return crewService.getAllCrewMembers();
    }

    // Update Crew Member
    @PutMapping("/{id}")
    public Collection_Crew_Members updateCrewMember(
            @PathVariable Long id,
            @RequestBody Collection_Crew_Members crew) {

        return crewService.updateCrewMember(id, crew);
    }

    // Delete Crew Member
    @DeleteMapping("/{id}")
    public String deleteCrewMember(@PathVariable Long id) {

        return crewService.deleteCrewMember(id);
    }
}
