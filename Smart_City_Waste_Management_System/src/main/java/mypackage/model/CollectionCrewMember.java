
package mypackage.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;


import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name = "collection_crew_members")
public class CollectionCrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_member_id")
    private Long crewMemberId;

    /// Many crew members can belong to one schedule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CollectionSchedule schedule;

    // One worker can be assigned to many crew records
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Worker worker;


    // Getters and Setters

    public Long getCrewMemberId() {
        return crewMemberId;
    }

    public void setCrewMemberId(Long crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public CollectionSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(CollectionSchedule schedule) {
        this.schedule = schedule;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
}
