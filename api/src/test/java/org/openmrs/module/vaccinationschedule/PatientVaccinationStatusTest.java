package org.openmrs.module.vaccinationschedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.Patient;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PatientVaccinationStatusTest {
    
    private Patient patient;
    private VaccinationScheduleEntry scheduleEntry;
    private PatientVaccinationStatus status;
    
    @BeforeEach
    public void setUp() {
        patient = new Patient();
        
        Calendar birthCal = Calendar.getInstance();
        birthCal.add(Calendar.DAY_OF_YEAR, -30); // 30 days old
        patient.setBirthdate(birthCal.getTime());
        
        Concept vaccineConcept = new Concept();
        ConceptName conceptName = new ConceptName("Test Vaccine", null);
        vaccineConcept.addName(conceptName);
        
        scheduleEntry = new VaccinationScheduleEntry();
        scheduleEntry.setVaccineConcept(vaccineConcept);
        scheduleEntry.setDoseNumber(1);
        scheduleEntry.setAgeInDaysMin(0);
        scheduleEntry.setAgeInDaysRecommended(7);
        scheduleEntry.setAgeInDaysMax(60);
        
        status = new PatientVaccinationStatus(patient, scheduleEntry);
    }
    
    @Test
    public void testCalculateStatus_WithPatientInValidAgeRange_ShouldReturnDue() {
        assertEquals(PatientVaccinationStatus.Status.DUE, status.getStatus());
        assertTrue(status.isDue());
        assertFalse(status.isPending());
        assertFalse(status.isOverdue());
    }
    
    @Test
    public void testCalculateStatus_WithPatientTooYoung_ShouldReturnPending() {
        Calendar youngBirthCal = Calendar.getInstance();
        youngBirthCal.add(Calendar.DAY_OF_YEAR, 1); // future birth date (invalid but for testing)
        patient.setBirthdate(youngBirthCal.getTime());
        
        status = new PatientVaccinationStatus(patient, scheduleEntry);
        assertEquals(PatientVaccinationStatus.Status.PENDING, status.getStatus());
        assertTrue(status.isPending());
    }
    
    @Test
    public void testCalculateStatus_WithPatientTooOld_ShouldReturnOverdue() {
        Calendar oldBirthCal = Calendar.getInstance();
        oldBirthCal.add(Calendar.DAY_OF_YEAR, -90); // 90 days old
        patient.setBirthdate(oldBirthCal.getTime());
        
        status = new PatientVaccinationStatus(patient, scheduleEntry);
        assertEquals(PatientVaccinationStatus.Status.OVERDUE, status.getStatus());
        assertTrue(status.isOverdue());
    }
    
    @Test
    public void testSetImmunizationObs_ShouldChangeStatusToApplied() {
        Obs immunizationObs = new Obs();
        immunizationObs.setObsDatetime(new Date());
        
        status.setImmunizationObs(immunizationObs);
        
        assertEquals(PatientVaccinationStatus.Status.APPLIED, status.getStatus());
        assertTrue(status.isApplied());
        assertEquals(immunizationObs.getObsDatetime(), status.getAppliedDate());
    }
    
    @Test
    public void testSetContraindicationReason_ShouldChangeStatusToContraindicated() {
        String reason = "Allergy to vaccine components";
        status.setContraindicationReason(reason);
        
        assertEquals(PatientVaccinationStatus.Status.CONTRAINDICATED, status.getStatus());
        assertTrue(status.isContraindicated());
        assertEquals(reason, status.getContraindicationReason());
    }
    
    @Test
    public void testCalculateDueDate_ShouldReturnCorrectDate() {
        Date dueDate = status.getDueDate();
        assertNotNull(dueDate);
        
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(patient.getBirthdate());
        expectedCal.add(Calendar.DAY_OF_YEAR, scheduleEntry.getAgeInDaysRecommended());
        
        assertEquals(expectedCal.getTime(), dueDate);
    }
    
    @Test
    public void testGetDaysOverdue_WithOverdueVaccination_ShouldReturnPositiveNumber() {
        Calendar oldBirthCal = Calendar.getInstance();
        oldBirthCal.add(Calendar.DAY_OF_YEAR, -90); // 90 days old
        patient.setBirthdate(oldBirthCal.getTime());
        
        status = new PatientVaccinationStatus(patient, scheduleEntry);
        int daysOverdue = status.getDaysOverdue();
        
        assertTrue(daysOverdue > 0);
    }
    
    @Test
    public void testGetDaysUntilDue_WithPendingVaccination_ShouldReturnPositiveNumber() {
        Calendar youngBirthCal = Calendar.getInstance();
        youngBirthCal.add(Calendar.DAY_OF_YEAR, -1); // 1 day old
        patient.setBirthdate(youngBirthCal.getTime());
        
        status = new PatientVaccinationStatus(patient, scheduleEntry);
        int daysUntilDue = status.getDaysUntilDue();
        
        assertTrue(daysUntilDue > 0);
    }
    
    @Test
    public void testGetVaccineName_ShouldReturnConceptName() {
        String vaccineName = status.getVaccineName();
        assertEquals("Test Vaccine", vaccineName);
    }
    
    @Test
    public void testGetDoseNumber_ShouldReturnCorrectDose() {
        Integer doseNumber = status.getDoseNumber();
        assertEquals(Integer.valueOf(1), doseNumber);
    }
    
    @Test
    public void testToString_ShouldContainKeyInformation() {
        String result = status.toString();
        
        assertTrue(result.contains("Test Vaccine"));
        assertTrue(result.contains("doseNumber=1"));
        assertTrue(result.contains("status=DUE"));
    }
}