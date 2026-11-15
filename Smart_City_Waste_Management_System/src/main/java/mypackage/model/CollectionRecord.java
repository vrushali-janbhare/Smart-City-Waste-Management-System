
package mypackage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "collection_records")
public class CollectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_record_id")
    private Long collectionRecordId;

    // Many collection records can belong to one schedule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CollectionSchedule schedule;

    // Many collection records can belong to one worker
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Worker worker;

    // Many collection records can belong to one waste category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private WasteCategory category;

    @Column(name = "quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    @Column(name = "collection_time", nullable = false)
    private LocalDateTime collectionTime;

    // Automatically set collection date and time before inserting
    @PrePersist
    public void setCollectionTimeAutomatically() {
        if (this.collectionTime == null) {
            this.collectionTime = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getCollectionRecordId() {
        return collectionRecordId;
    }

    public void setCollectionRecordId(Long collectionRecordId) {
        this.collectionRecordId = collectionRecordId;
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

    public WasteCategory getCategory() {
        return category;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    public BigDecimal getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(BigDecimal quantityKg) {
        this.quantityKg = quantityKg;
    }

    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(LocalDateTime collectionTime) {
        this.collectionTime = collectionTime;
    }
}
