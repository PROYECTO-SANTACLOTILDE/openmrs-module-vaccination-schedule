package org.openmrs.module.vaccinationschedule;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "patient_vaccination_schedule")
public class PatientVaccinationSchedule extends BaseOpenmrsData {
    
    public enum Status {
        ACTIVE,
        SUSPENDED,
        COMPLETED,
        TRANSFERRED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @NotNull
    private VaccinationSchedule vaccinationSchedule;
    
    @Column(name = "assigned_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_user_id", nullable = false)
    @NotNull
    private User assignedByUser;
    
    @Column(name = "birth_date_at_assignment")
    @Temporal(TemporalType.DATE)
    private Date birthDateAtAssignment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private Status status = Status.ACTIVE;
    
    public PatientVaccinationSchedule() {
        super();
    }
    
    public PatientVaccinationSchedule(Patient patient, VaccinationSchedule schedule, 
                                    User assignedByUser) {
        this();
        this.patient = patient;
        this.vaccinationSchedule = schedule;
        this.assignedByUser = assignedByUser;
        this.assignedDate = new Date();
        this.birthDateAtAssignment = patient.getBirthdate();
    }
    
    @Override
    public Integer getId() {
        return id;
    }
    
    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public VaccinationSchedule getVaccinationSchedule() {
        return vaccinationSchedule;
    }
    
    public void setVaccinationSchedule(VaccinationSchedule vaccinationSchedule) {
        this.vaccinationSchedule = vaccinationSchedule;
    }
    
    public Date getAssignedDate() {
        return assignedDate;
    }
    
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }
    
    public User getAssignedByUser() {
        return assignedByUser;
    }
    
    public void setAssignedByUser(User assignedByUser) {
        this.assignedByUser = assignedByUser;
    }
    
    public Date getBirthDateAtAssignment() {
        return birthDateAtAssignment;
    }
    
    public void setBirthDateAtAssignment(Date birthDateAtAssignment) {
        this.birthDateAtAssignment = birthDateAtAssignment;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Status calculateCurrentStatus() {
        if (getVoided() || status == Status.SUSPENDED || status == Status.TRANSFERRED) {
            return status;
        }
        
        if (patient.getBirthdate() == null) {
            return Status.SUSPENDED;
        }
        
        Date now = new Date();
        int currentAgeInDays = (int) ((now.getTime() - patient.getBirthdate().getTime()) / (24 * 60 * 60 * 1000));
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        boolean hasOutstandingVaccinations = entries.stream()
                .anyMatch(entry -> entry.isValidForAge(currentAgeInDays));
        
        if (!hasOutstandingVaccinations) {
            return Status.COMPLETED;
        }
        
        return Status.ACTIVE;
    }
    
    public List<VaccinationScheduleEntry> getOverdueVaccinations() {
        if (patient.getBirthdate() == null) {
            return java.util.Collections.emptyList();
        }
        
        Date now = new Date();
        int currentAgeInDays = (int) ((now.getTime() - patient.getBirthdate().getTime()) / (24 * 60 * 60 * 1000));
        
        return vaccinationSchedule.getActiveEntries().stream()
                .filter(entry -> entry.getAgeInDaysMax() != null && 
                        currentAgeInDays > entry.getAgeInDaysMax())
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<VaccinationScheduleEntry> getUpcomingVaccinations() {
        if (patient.getBirthdate() == null) {
            return java.util.Collections.emptyList();
        }
        
        Date now = new Date();
        int currentAgeInDays = (int) ((now.getTime() - patient.getBirthdate().getTime()) / (24 * 60 * 60 * 1000));
        
        return vaccinationSchedule.getActiveEntries().stream()
                .filter(entry -> currentAgeInDays >= entry.getAgeInDaysMin() && 
                        (entry.getAgeInDaysMax() == null || currentAgeInDays <= entry.getAgeInDaysMax()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "PatientVaccinationSchedule{" +
                "id=" + id +
                ", patient=" + (patient != null ? patient.getPatientId() : "null") +
                ", vaccinationSchedule=" + (vaccinationSchedule != null ? vaccinationSchedule.getName() : "null") +
                ", assignedDate=" + assignedDate +
                ", status=" + status +
                '}';
    }
}