package org.openmrs.module.vaccinationschedule;

import org.openmrs.Obs;
import org.openmrs.Patient;

import java.util.Date;

public class PatientVaccinationStatus {
    
    public enum Status {
        PENDING,
        DUE,
        OVERDUE,
        APPLIED,
        CONTRAINDICATED,
        SKIPPED
    }
    
    private Patient patient;
    private VaccinationScheduleEntry scheduleEntry;
    private Status status;
    private Date dueDate;
    private Date appliedDate;
    private Obs immunizationObs;
    private String contraindicationReason;
    
    public PatientVaccinationStatus() {
    }
    
    public PatientVaccinationStatus(Patient patient, VaccinationScheduleEntry scheduleEntry) {
        this.patient = patient;
        this.scheduleEntry = scheduleEntry;
        this.status = calculateStatus();
        this.dueDate = calculateDueDate();
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public VaccinationScheduleEntry getScheduleEntry() {
        return scheduleEntry;
    }
    
    public void setScheduleEntry(VaccinationScheduleEntry scheduleEntry) {
        this.scheduleEntry = scheduleEntry;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public Date getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public Obs getImmunizationObs() {
        return immunizationObs;
    }
    
    public void setImmunizationObs(Obs immunizationObs) {
        this.immunizationObs = immunizationObs;
        if (immunizationObs != null) {
            this.appliedDate = immunizationObs.getObsDatetime();
            this.status = Status.APPLIED;
        }
    }
    
    public String getContraindicationReason() {
        return contraindicationReason;
    }
    
    public void setContraindicationReason(String contraindicationReason) {
        this.contraindicationReason = contraindicationReason;
        if (contraindicationReason != null && !contraindicationReason.trim().isEmpty()) {
            this.status = Status.CONTRAINDICATED;
        }
    }
    
    private Status calculateStatus() {
        if (immunizationObs != null) {
            return Status.APPLIED;
        }
        
        if (contraindicationReason != null && !contraindicationReason.trim().isEmpty()) {
            return Status.CONTRAINDICATED;
        }
        
        if (patient.getBirthdate() == null || scheduleEntry == null) {
            return Status.PENDING;
        }
        
        Date now = new Date();
        int ageInDays = (int) ((now.getTime() - patient.getBirthdate().getTime()) / (24 * 60 * 60 * 1000));
        
        if (ageInDays < scheduleEntry.getAgeInDaysMin()) {
            return Status.PENDING;
        }
        
        if (scheduleEntry.getAgeInDaysMax() != null && ageInDays > scheduleEntry.getAgeInDaysMax()) {
            return Status.OVERDUE;
        }
        
        return Status.DUE;
    }
    
    private Date calculateDueDate() {
        if (patient.getBirthdate() == null || scheduleEntry == null) {
            return null;
        }
        
        long dueDateMillis = patient.getBirthdate().getTime() + 
                (scheduleEntry.getAgeInDaysRecommended() * 24 * 60 * 60 * 1000L);
        return new Date(dueDateMillis);
    }
    
    public boolean isDue() {
        return status == Status.DUE;
    }
    
    public boolean isOverdue() {
        return status == Status.OVERDUE;
    }
    
    public boolean isApplied() {
        return status == Status.APPLIED;
    }
    
    public boolean isContraindicated() {
        return status == Status.CONTRAINDICATED;
    }
    
    public boolean isPending() {
        return status == Status.PENDING;
    }
    
    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }
    
    public int getDaysOverdue() {
        if (!isOverdue() || dueDate == null) {
            return 0;
        }
        
        Date now = new Date();
        return (int) ((now.getTime() - dueDate.getTime()) / (24 * 60 * 60 * 1000));
    }
    
    public int getDaysUntilDue() {
        if (isDue() || isOverdue() || dueDate == null) {
            return 0;
        }
        
        Date now = new Date();
        return (int) ((dueDate.getTime() - now.getTime()) / (24 * 60 * 60 * 1000));
    }
    
    public String getVaccineName() {
        return scheduleEntry != null && scheduleEntry.getVaccineConcept() != null ?
                scheduleEntry.getVaccineConcept().getName().getName() : "";
    }
    
    public Integer getDoseNumber() {
        return scheduleEntry != null ? scheduleEntry.getDoseNumber() : null;
    }
    
    @Override
    public String toString() {
        return "PatientVaccinationStatus{" +
                "patient=" + (patient != null ? patient.getPatientId() : "null") +
                ", vaccineName='" + getVaccineName() + '\'' +
                ", doseNumber=" + getDoseNumber() +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", appliedDate=" + appliedDate +
                '}';
    }
}