
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.CollectionCrewMember;
import mypackage.repository.CollectionCrewMember_Repository;

@Service
public class CollectionCrewMember_Service {

    @Autowired
    private CollectionCrewMember_Repository collectionCrewMemberRepository;

    // Insert or update crew member
    public CollectionCrewMember saveCollectionCrewMember(CollectionCrewMember crewMember) {
        return collectionCrewMemberRepository.save(crewMember);
    }

    // Get all crew members
    public List<CollectionCrewMember> getAllCollectionCrewMembers() {
        return collectionCrewMemberRepository.findAll();
    }

    // Get crew member by ID
    public Optional<CollectionCrewMember> getCollectionCrewMemberById(Long crewMemberId) {
        return collectionCrewMemberRepository.findById(crewMemberId);
    }

    // Delete crew member by ID
    public void deleteCollectionCrewMember(Long crewMemberId) {
        collectionCrewMemberRepository.deleteById(crewMemberId);
    }
}
