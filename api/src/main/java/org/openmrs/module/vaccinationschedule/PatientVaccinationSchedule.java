package org.openmrs.module.vaccinationschedule;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.User;

import java.util.Date;
import java.util.List;

public class PatientVaccinationSchedule extends BaseOpenmrsData {
    
    public enum Status {
        ACTIVE,
        SUSPENDED,
        COMPLETED,
        TRANSFERRED
    }
    
    private Integer id;
    
    private Patient patient;
    
    private VaccinationSchedule vaccinationSchedule;
    
    private Date assignedDate;
    
    private User assignedByUser;
    
    private Date birthDateAtAssignment;
    
    private Status status = Status.ACTIVE;
    
    public PatientVaccinationSchedule() {
        super();
    }
    
    public PatientVaccinationSchedule(Patient patient, VaccinationSchedule schedule, 
                                    User assignedByUser) {
        this();
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        if (schedule == null) {
            throw new IllegalArgumentException("VaccinationSchedule cannot be null");
        }
        if (assignedByUser == null) {
            throw new IllegalArgumentException("AssignedByUser cannot be null");
        }
        
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
        
        if (patient == null || patient.getBirthdate() == null) {
            return Status.SUSPENDED;
        }
        
        if (vaccinationSchedule == null) {
            return Status.SUSPENDED;
        }
        
        Date now = new Date();
        long ageInMillis = now.getTime() - patient.getBirthdate().getTime();
        if (ageInMillis < 0) {
            return Status.SUSPENDED; // Future birthdate
        }
        
        int currentAgeInDays = (int) (ageInMillis / (24 * 60 * 60 * 1000));
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        if (entries == null || entries.isEmpty()) {
            return Status.COMPLETED;
        }
        
        boolean hasOutstandingVaccinations = entries.stream()
                .anyMatch(entry -> entry != null && entry.isValidForAge(currentAgeInDays));
        
        if (!hasOutstandingVaccinations) {
            return Status.COMPLETED;
        }
        
        return Status.ACTIVE;
    }
    
    public List<VaccinationScheduleEntry> getOverdueVaccinations() {
        if (patient == null || patient.getBirthdate() == null || vaccinationSchedule == null) {
            return java.util.Collections.emptyList();
        }
        
        Date now = new Date();
        long ageInMillis = now.getTime() - patient.getBirthdate().getTime();
        if (ageInMillis < 0) {
            return java.util.Collections.emptyList(); // Future birthdate
        }
        
        int currentAgeInDays = (int) (ageInMillis / (24 * 60 * 60 * 1000));
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        if (entries == null) {
            return java.util.Collections.emptyList();
        }
        
        return entries.stream()
                .filter(entry -> entry != null && entry.getAgeInDaysMax() != null && 
                        currentAgeInDays > entry.getAgeInDaysMax())
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<VaccinationScheduleEntry> getUpcomingVaccinations() {
        if (patient == null || patient.getBirthdate() == null || vaccinationSchedule == null) {
            return java.util.Collections.emptyList();
        }
        
        Date now = new Date();
        long ageInMillis = now.getTime() - patient.getBirthdate().getTime();
        if (ageInMillis < 0) {
            return java.util.Collections.emptyList(); // Future birthdate
        }
        
        int currentAgeInDays = (int) (ageInMillis / (24 * 60 * 60 * 1000));
        
        List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
        if (entries == null) {
            return java.util.Collections.emptyList();
        }
        
        return entries.stream()
                .filter(entry -> entry != null && 
                        entry.getAgeInDaysMin() != null && 
                        currentAgeInDays >= entry.getAgeInDaysMin() && 
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