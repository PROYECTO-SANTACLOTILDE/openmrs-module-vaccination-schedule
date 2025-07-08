package org.openmrs.module.vaccinationschedule.api.dao;

import org.openmrs.Patient;
import org.openmrs.module.vaccinationschedule.PatientVaccinationSchedule;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.VaccinationScheduleRule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationScheduleDao {
    
    VaccinationSchedule getVaccinationSchedule(Integer scheduleId);
    
    VaccinationSchedule getVaccinationScheduleByUuid(String uuid);
    
    VaccinationSchedule getActiveScheduleByCountry(String countryCode);
    
    List<VaccinationSchedule> getAllSchedules(boolean includeRetired);
    
    List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired);
    
    VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule);
    
    void deleteVaccinationSchedule(VaccinationSchedule schedule);
    
    // Schedule Entry methods
    VaccinationScheduleEntry getScheduleEntry(Integer entryId);
    
    VaccinationScheduleEntry getScheduleEntryByUuid(String uuid);
    
    VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry);
    
    void deleteScheduleEntry(VaccinationScheduleEntry entry);
    
    List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule);
    
    // Schedule Rule methods
    VaccinationScheduleRule getScheduleRule(Integer ruleId);
    
    VaccinationScheduleRule getScheduleRuleByUuid(String uuid);
    
    VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule);
    
    void deleteScheduleRule(VaccinationScheduleRule rule);
    
    List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule);
    
    // Patient Schedule methods
    PatientVaccinationSchedule getPatientSchedule(Integer id);
    
    PatientVaccinationSchedule getPatientScheduleByUuid(String uuid);
    
    PatientVaccinationSchedule savePatientSchedule(PatientVaccinationSchedule patientSchedule);
    
    void deletePatientSchedule(PatientVaccinationSchedule patientSchedule);
    
    List<PatientVaccinationSchedule> getPatientSchedulesByPatient(Patient patient);
    
    List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule);
}