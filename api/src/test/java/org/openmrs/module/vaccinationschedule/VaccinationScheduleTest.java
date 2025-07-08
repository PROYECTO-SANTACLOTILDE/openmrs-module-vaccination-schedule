package org.openmrs.module.vaccinationschedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VaccinationScheduleTest {
    
    private VaccinationSchedule schedule;
    private VaccinationScheduleEntry entry1;
    private VaccinationScheduleEntry entry2;
    
    @BeforeEach
    public void setUp() {
        schedule = new VaccinationSchedule();
        schedule.setName("Test Schedule");
        schedule.setCountryCode("TST");
        schedule.setVersion(1);
        schedule.setEffectiveDate(new Date());
        schedule.setRetired(false);
        
        Concept vaccineConcept = new Concept();
        ConceptName conceptName = new ConceptName("Test Vaccine", null);
        vaccineConcept.addName(conceptName);
        
        entry1 = new VaccinationScheduleEntry();
        entry1.setVaccinationSchedule(schedule);
        entry1.setVaccineConcept(vaccineConcept);
        entry1.setDoseNumber(1);
        entry1.setAgeInDaysMin(0);
        entry1.setAgeInDaysRecommended(1);
        entry1.setAgeInDaysMax(30);
        entry1.setVoided(false);
        
        entry2 = new VaccinationScheduleEntry();
        entry2.setVaccinationSchedule(schedule);
        entry2.setVaccineConcept(vaccineConcept);
        entry2.setDoseNumber(2);
        entry2.setAgeInDaysMin(60);
        entry2.setAgeInDaysRecommended(90);
        entry2.setAgeInDaysMax(120);
        entry2.setVoided(false);
        
        Set<VaccinationScheduleEntry> entries = new HashSet<>();
        entries.add(entry1);
        entries.add(entry2);
        schedule.setEntries(entries);
    }
    
    @Test
    public void testIsActive_WithValidSchedule_ShouldReturnTrue() {
        assertTrue(schedule.isActive());
    }
    
    @Test
    public void testIsActive_WithRetiredSchedule_ShouldReturnFalse() {
        schedule.setRetired(true);
        assertFalse(schedule.isActive());
    }
    
    @Test
    public void testIsActive_WithFutureEffectiveDate_ShouldReturnFalse() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000); // tomorrow
        schedule.setEffectiveDate(futureDate);
        assertFalse(schedule.isActive());
    }
    
    @Test
    public void testIsActive_WithPastExpiryDate_ShouldReturnFalse() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000); // yesterday
        schedule.setExpiryDate(pastDate);
        assertFalse(schedule.isActive());
    }
    
    @Test
    public void testGetEntriesByAge_WithInfantAge_ShouldReturnFirstEntry() {
        List<VaccinationScheduleEntry> result = schedule.getEntriesByAge(15);
        assertEquals(1, result.size());
        assertEquals(entry1, result.get(0));
    }
    
    @Test
    public void testGetEntriesByAge_WithChildAge_ShouldReturnSecondEntry() {
        List<VaccinationScheduleEntry> result = schedule.getEntriesByAge(90);
        assertEquals(1, result.size());
        assertEquals(entry2, result.get(0));
    }
    
    @Test
    public void testGetEntriesByAge_WithAdultAge_ShouldReturnEmpty() {
        List<VaccinationScheduleEntry> result = schedule.getEntriesByAge(365);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testGetActiveEntries_WithNonVoidedEntries_ShouldReturnAll() {
        List<VaccinationScheduleEntry> result = schedule.getActiveEntries();
        assertEquals(2, result.size());
    }
    
    @Test
    public void testGetActiveEntries_WithVoidedEntry_ShouldExcludeVoided() {
        entry1.setVoided(true);
        List<VaccinationScheduleEntry> result = schedule.getActiveEntries();
        assertEquals(1, result.size());
        assertEquals(entry2, result.get(0));
    }
    
    @Test
    public void testConstructor_WithParameters_ShouldSetProperties() {
        Date effectiveDate = new Date();
        VaccinationSchedule newSchedule = new VaccinationSchedule("New Schedule", "NEW", effectiveDate);
        
        assertEquals("New Schedule", newSchedule.getName());
        assertEquals("NEW", newSchedule.getCountryCode());
        assertEquals(effectiveDate, newSchedule.getEffectiveDate());
    }
    
    @Test
    public void testToString_ShouldContainKeyProperties() {
        String result = schedule.toString();
        
        assertTrue(result.contains("Test Schedule"));
        assertTrue(result.contains("TST"));
        assertTrue(result.contains("version=1"));
    }
}