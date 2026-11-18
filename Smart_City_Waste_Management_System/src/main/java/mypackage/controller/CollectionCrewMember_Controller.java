
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.CollectionCrewMember;
import mypackage.service.CollectionCrewMember_Service;

//@RestController
//@RequestMapping("/api/collection-crew-members")
public class CollectionCrewMember_Controller {

    @Autowired
    private CollectionCrewMember_Service collectionCrewMemberService;

    // Insert new collection crew member
    @PostMapping
    public CollectionCrewMember saveCollectionCrewMember(
            @RequestBody CollectionCrewMember collectionCrewMember) {

        return collectionCrewMemberService.saveCollectionCrewMember(
                collectionCrewMember);
    }

    // Get all collection crew members
    @GetMapping
    public List<CollectionCrewMember> getAllCollectionCrewMembers() {
        return collectionCrewMemberService.getAllCollectionCrewMembers();
    }

    // Get collection crew member by ID
    @GetMapping("/{crewMemberId}")
    public ResponseEntity<CollectionCrewMember> getCollectionCrewMemberById(
            @PathVariable Long crewMemberId) {

        return collectionCrewMemberService.getCollectionCrewMemberById(crewMemberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update collection crew member by ID
    @PutMapping("/{crewMemberId}")
    public ResponseEntity<CollectionCrewMember> updateCollectionCrewMember(
            @PathVariable Long crewMemberId,
            @RequestBody CollectionCrewMember collectionCrewMember) {

        return collectionCrewMemberService.getCollectionCrewMemberById(crewMemberId)
                .map(existingMember -> {
                    collectionCrewMember.setCrewMemberId(crewMemberId);
                    return ResponseEntity.ok(
                            collectionCrewMemberService.saveCollectionCrewMember(
                                    collectionCrewMember));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete collection crew member by ID
    @DeleteMapping("/{crewMemberId}")
    public ResponseEntity<Void> deleteCollectionCrewMember(
            @PathVariable Long crewMemberId) {

        return collectionCrewMemberService.getCollectionCrewMemberById(crewMemberId)
                .map(existingMember -> {
                    collectionCrewMemberService.deleteCollectionCrewMember(crewMemberId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
