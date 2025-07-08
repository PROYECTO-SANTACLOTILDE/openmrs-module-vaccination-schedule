package org.openmrs.module.vaccinationschedule;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Entity
@Table(name = "vaccination_schedule_entry")
public class VaccinationScheduleEntry extends BaseOpenmrsData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @NotNull
    private VaccinationSchedule vaccinationSchedule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_concept_id", nullable = false)
    @NotNull
    private Concept vaccineConcept;
    
    @Column(name = "dose_number", nullable = false)
    @Positive
    private Integer doseNumber;
    
    @Column(name = "age_in_days_min", nullable = false)
    private Integer ageInDaysMin;
    
    @Column(name = "age_in_days_recommended", nullable = false)
    private Integer ageInDaysRecommended;
    
    @Column(name = "age_in_days_max")
    private Integer ageInDaysMax;
    
    @Column(name = "interval_from_previous_days")
    private Integer intervalFromPreviousDays;
    
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_concept_id")
    private Concept routeConcept;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_concept_id")
    private Concept siteConcept;
    
    public VaccinationScheduleEntry() {
        super();
    }
    
    public VaccinationScheduleEntry(VaccinationSchedule schedule, Concept vaccineConcept, 
                                   Integer doseNumber, Integer ageInDaysMin, 
                                   Integer ageInDaysRecommended) {
        this();
        this.vaccinationSchedule = schedule;
        this.vaccineConcept = vaccineConcept;
        this.doseNumber = doseNumber;
        this.ageInDaysMin = ageInDaysMin;
        this.ageInDaysRecommended = ageInDaysRecommended;
    }
    
    @Override
    public Integer getId() {
        return entryId;
    }
    
    @Override
    public void setId(Integer id) {
        this.entryId = id;
    }
    
    public Integer getEntryId() {
        return entryId;
    }
    
    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }
    
    public VaccinationSchedule getVaccinationSchedule() {
        return vaccinationSchedule;
    }
    
    public void setVaccinationSchedule(VaccinationSchedule vaccinationSchedule) {
        this.vaccinationSchedule = vaccinationSchedule;
    }
    
    public Concept getVaccineConcept() {
        return vaccineConcept;
    }
    
    public void setVaccineConcept(Concept vaccineConcept) {
        this.vaccineConcept = vaccineConcept;
    }
    
    public Integer getDoseNumber() {
        return doseNumber;
    }
    
    public void setDoseNumber(Integer doseNumber) {
        this.doseNumber = doseNumber;
    }
    
    public Integer getAgeInDaysMin() {
        return ageInDaysMin;
    }
    
    public void setAgeInDaysMin(Integer ageInDaysMin) {
        this.ageInDaysMin = ageInDaysMin;
    }
    
    public Integer getAgeInDaysRecommended() {
        return ageInDaysRecommended;
    }
    
    public void setAgeInDaysRecommended(Integer ageInDaysRecommended) {
        this.ageInDaysRecommended = ageInDaysRecommended;
    }
    
    public Integer getAgeInDaysMax() {
        return ageInDaysMax;
    }
    
    public void setAgeInDaysMax(Integer ageInDaysMax) {
        this.ageInDaysMax = ageInDaysMax;
    }
    
    public Integer getIntervalFromPreviousDays() {
        return intervalFromPreviousDays;
    }
    
    public void setIntervalFromPreviousDays(Integer intervalFromPreviousDays) {
        this.intervalFromPreviousDays = intervalFromPreviousDays;
    }
    
    public Boolean getIsMandatory() {
        return isMandatory;
    }
    
    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Concept getRouteConcept() {
        return routeConcept;
    }
    
    public void setRouteConcept(Concept routeConcept) {
        this.routeConcept = routeConcept;
    }
    
    public Concept getSiteConcept() {
        return siteConcept;
    }
    
    public void setSiteConcept(Concept siteConcept) {
        this.siteConcept = siteConcept;
    }
    
    public boolean isValidForAge(int ageInDays) {
        if (ageInDays < 0) {
            return false;
        }
        
        if (ageInDaysMin == null) {
            return false;
        }
        
        if (ageInDays < ageInDaysMin) {
            return false;
        }
        
        if (ageInDaysMax != null && ageInDays > ageInDaysMax) {
            return false;
        }
        
        return true;
    }
    
    public VaccinationScheduleEntry getNextDose() {
        if (vaccinationSchedule == null || vaccineConcept == null || doseNumber == null) {
            return null;
        }
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        
        return entries.stream()
                .filter(entry -> entry != null && 
                        entry.getVaccineConcept() != null && 
                        entry.getVaccineConcept().equals(this.vaccineConcept))
                .filter(entry -> entry.getDoseNumber() != null && 
                        entry.getDoseNumber() == this.doseNumber + 1)
                .findFirst()
                .orElse(null);
    }
    
    public VaccinationScheduleEntry getPreviousDose() {
        if (vaccinationSchedule == null || vaccineConcept == null || doseNumber == null || doseNumber <= 1) {
            return null;
        }
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        
        return entries.stream()
                .filter(entry -> entry != null && 
                        entry.getVaccineConcept() != null && 
                        entry.getVaccineConcept().equals(this.vaccineConcept))
                .filter(entry -> entry.getDoseNumber() != null && 
                        entry.getDoseNumber() == this.doseNumber - 1)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return "VaccinationScheduleEntry{" +
                "entryId=" + entryId +
                ", vaccineConcept=" + (vaccineConcept != null ? vaccineConcept.getName() : "null") +
                ", doseNumber=" + doseNumber +
                ", ageInDaysMin=" + ageInDaysMin +
                ", ageInDaysRecommended=" + ageInDaysRecommended +
                ", ageInDaysMax=" + ageInDaysMax +
                '}';
    }
}