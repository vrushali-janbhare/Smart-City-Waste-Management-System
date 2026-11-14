
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Collection_Crew_Members;
import mypackage.repository.Collection_Crew_Members_Repository;

@Service
public class Collection_Crew_Members_Service {

    @Autowired
    private Collection_Crew_Members_Repository crewRepository;

    // Add Crew Member
    public Collection_Crew_Members addCrewMember(Collection_Crew_Members crew) {
        return crewRepository.save(crew);
    }

    // Get Crew Member By ID
    public Collection_Crew_Members getCrewMemberById(Long id) {
        return crewRepository.findById(id).orElse(null);
    }

    // Get All Crew Members
    public List<Collection_Crew_Members> getAllCrewMembers() {
        return crewRepository.findAll();
    }

    // Update Crew Member
    public Collection_Crew_Members updateCrewMember(Long id, Collection_Crew_Members crew) {

        Collection_Crew_Members existingCrew =
                crewRepository.findById(id).orElse(null);

        if (existingCrew != null) {

            existingCrew.setCollection_schedule(crew.getCollection_schedule());
            existingCrew.setWorkers(crew.getWorkers());
            existingCrew.setAssigned_at(crew.getAssigned_at());

            return crewRepository.save(existingCrew);
        }

        return null;
    }

    // Delete Crew Member
    public String deleteCrewMember(Long id) {

        crewRepository.deleteById(id);

        return "Crew Member Deleted Successfully";
    }
}
