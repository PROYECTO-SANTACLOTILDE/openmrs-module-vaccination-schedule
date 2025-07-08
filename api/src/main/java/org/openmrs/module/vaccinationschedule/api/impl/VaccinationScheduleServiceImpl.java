package org.openmrs.module.vaccinationschedule.api.impl;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.vaccinationschedule.*;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationScheduleServiceImpl extends BaseOpenmrsService implements VaccinationScheduleService {
    
    @Autowired
    private VaccinationScheduleDao vaccinationScheduleDao;
    
    @Override
    public VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule) {
        if (schedule == null) {
            throw new APIException("VaccinationSchedule cannot be null");
        }
        
        if (schedule.getName() == null || schedule.getName().trim().isEmpty()) {
            throw new APIException("VaccinationSchedule name is required");
        }
        
        if (schedule.getCountryCode() != null && schedule.getCountryCode().length() > 3) {
            throw new APIException("Country code cannot exceed 3 characters");
        }
        
        if (schedule.getEffectiveDate() == null) {
            throw new APIException("Effective date is required");
        }
        
        return vaccinationScheduleDao.saveVaccinationSchedule(schedule);
    }
    
    @Override
    public VaccinationSchedule getVaccinationSchedule(Integer scheduleId) {
        return vaccinationScheduleDao.getVaccinationSchedule(scheduleId);
    }
    
    @Override
    public VaccinationSchedule getVaccinationScheduleByUuid(String uuid) {
        return vaccinationScheduleDao.getVaccinationScheduleByUuid(uuid);
    }
    
    @Override
    public VaccinationSchedule getActiveScheduleByCountry(String countryCode) {
        return vaccinationScheduleDao.getActiveScheduleByCountry(countryCode);
    }
    
    @Override
    public List<VaccinationSchedule> getAllSchedules(boolean includeRetired) {
        return vaccinationScheduleDao.getAllSchedules(includeRetired);
    }
    
    @Override
    public List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired) {
        return vaccinationScheduleDao.getSchedulesByCountry(countryCode, includeRetired);
    }
    
    @Override
    public VaccinationSchedule retireVaccinationSchedule(VaccinationSchedule schedule, String reason) {
        schedule.setRetired(true);
        schedule.setRetireReason(reason);
        schedule.setRetiredBy(Context.getAuthenticatedUser());
        schedule.setDateRetired(new Date());
        return saveVaccinationSchedule(schedule);
    }
    
    @Override
    public VaccinationSchedule unretireVaccinationSchedule(VaccinationSchedule schedule) {
        schedule.setRetired(false);
        schedule.setRetireReason(null);
        schedule.setRetiredBy(null);
        schedule.setDateRetired(null);
        return saveVaccinationSchedule(schedule);
    }
    
    @Override
    public VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry) {
        if (entry == null) {
            throw new APIException("VaccinationScheduleEntry cannot be null");
        }
        
        if (entry.getVaccineConcept() == null) {
            throw new APIException("Vaccine concept is required");
        }
        
        if (entry.getDoseNumber() == null || entry.getDoseNumber() <= 0) {
            throw new APIException("Dose number must be positive");
        }
        
        if (entry.getAgeInDaysMin() == null || entry.getAgeInDaysRecommended() == null) {
            throw new APIException("Age in days (min and recommended) are required");
        }
        
        if (entry.getAgeInDaysMin() > entry.getAgeInDaysRecommended()) {
            throw new APIException("Minimum age cannot be greater than recommended age");
        }
        
        if (entry.getAgeInDaysMax() != null && entry.getAgeInDaysRecommended() > entry.getAgeInDaysMax()) {
            throw new APIException("Recommended age cannot be greater than maximum age");
        }
        
        return vaccinationScheduleDao.saveScheduleEntry(entry);
    }
    
    @Override
    public VaccinationScheduleEntry getScheduleEntry(Integer entryId) {
        return vaccinationScheduleDao.getScheduleEntry(entryId);
    }
    
    @Override
    public VaccinationScheduleEntry getScheduleEntryByUuid(String uuid) {
        return vaccinationScheduleDao.getScheduleEntryByUuid(uuid);
    }
    
    @Override
    public List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule) {
        if (schedule == null) {
            return new ArrayList<>();
        }
        return schedule.getActiveEntries();
    }
    
    @Override
    public List<VaccinationScheduleEntry> getEntriesForAge(VaccinationSchedule schedule, int ageInDays) {
        if (schedule == null) {
            return new ArrayList<>();
        }
        return schedule.getEntriesByAge(ageInDays);
    }
    
    @Override
    public VaccinationScheduleEntry voidScheduleEntry(VaccinationScheduleEntry entry, String reason) {
        entry.setVoided(true);
        entry.setVoidReason(reason);
        entry.setVoidedBy(Context.getAuthenticatedUser());
        entry.setDateVoided(new Date());
        return saveScheduleEntry(entry);
    }
    
    @Override
    public VaccinationScheduleEntry unvoidScheduleEntry(VaccinationScheduleEntry entry) {
        entry.setVoided(false);
        entry.setVoidReason(null);
        entry.setVoidedBy(null);
        entry.setDateVoided(null);
        return saveScheduleEntry(entry);
    }
    
    @Override
    public VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule) {
        return null;
    }
    
    @Override
    public VaccinationScheduleRule getScheduleRule(Integer ruleId) {
        return null;
    }
    
    @Override
    public VaccinationScheduleRule getScheduleRuleByUuid(String uuid) {
        return null;
    }
    
    @Override
    public List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule) {
        return new ArrayList<>();
    }
    
    @Override
    public VaccinationScheduleRule voidScheduleRule(VaccinationScheduleRule rule, String reason) {
        return null;
    }
    
    @Override
    public VaccinationScheduleRule unvoidScheduleRule(VaccinationScheduleRule rule) {
        return null;
    }
    
    @Override
    public PatientVaccinationSchedule assignScheduleToPatient(Patient patient, VaccinationSchedule schedule) {
        PatientVaccinationSchedule patientSchedule = new PatientVaccinationSchedule(
                patient, schedule, Context.getAuthenticatedUser());
        return patientSchedule;
    }
    
    @Override
    public PatientVaccinationSchedule getPatientSchedule(Patient patient) {
        return null;
    }
    
    @Override
    public PatientVaccinationSchedule getPatientScheduleByUuid(String uuid) {
        return null;
    }
    
    @Override
    public List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule) {
        return new ArrayList<>();
    }
    
    @Override
    public PatientVaccinationSchedule updatePatientScheduleStatus(PatientVaccinationSchedule patientSchedule, 
                                                                PatientVaccinationSchedule.Status status) {
        patientSchedule.setStatus(status);
        return patientSchedule;
    }
    
    @Override
    public PatientVaccinationSchedule voidPatientSchedule(PatientVaccinationSchedule patientSchedule, String reason) {
        patientSchedule.setVoided(true);
        patientSchedule.setVoidReason(reason);
        patientSchedule.setVoidedBy(Context.getAuthenticatedUser());
        patientSchedule.setDateVoided(new Date());
        return patientSchedule;
    }
    
    @Override
    public PatientVaccinationSchedule unvoidPatientSchedule(PatientVaccinationSchedule patientSchedule) {
        patientSchedule.setVoided(false);
        patientSchedule.setVoidReason(null);
        patientSchedule.setVoidedBy(null);
        patientSchedule.setDateVoided(null);
        return patientSchedule;
    }
    
    @Override
    public List<PatientVaccinationStatus> calculateVaccinationStatuses(Patient patient) {
        PatientVaccinationSchedule patientSchedule = getPatientSchedule(patient);
        if (patientSchedule == null) {
            return new ArrayList<>();
        }
        
        List<PatientVaccinationStatus> statuses = new ArrayList<>();
        List<VaccinationScheduleEntry> entries = patientSchedule.getVaccinationSchedule().getActiveEntries();
        
        for (VaccinationScheduleEntry entry : entries) {
            PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);
            
            if (isVaccineAdministered(patient, entry.getVaccineConcept(), entry.getDoseNumber())) {
                List<Obs> vaccineObs = getAdministeredVaccinesForConcept(patient, entry.getVaccineConcept());
                if (!vaccineObs.isEmpty()) {
                    status.setImmunizationObs(vaccineObs.get(0));
                }
            }
            
            statuses.add(status);
        }
        
        return statuses;
    }
    
    @Override
    public List<PatientVaccinationStatus> getDueVaccinations(Patient patient, Date asOfDate) {
        return calculateVaccinationStatuses(patient).stream()
                .filter(status -> status.getStatus() == PatientVaccinationStatus.Status.DUE)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PatientVaccinationStatus> getOverdueVaccinations(Patient patient, Date asOfDate) {
        return calculateVaccinationStatuses(patient).stream()
                .filter(status -> status.getStatus() == PatientVaccinationStatus.Status.OVERDUE)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PatientVaccinationStatus> getUpcomingVaccinations(Patient patient, int daysAhead) {
        Date futureDate = new Date(System.currentTimeMillis() + (daysAhead * 24 * 60 * 60 * 1000L));
        return calculateVaccinationStatuses(patient).stream()
                .filter(status -> status.getStatus() == PatientVaccinationStatus.Status.PENDING)
                .filter(status -> status.getDueDate() != null && status.getDueDate().before(futureDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PatientVaccinationStatus> getVaccinationCalendar(Patient patient, Date startDate, Date endDate) {
        return calculateVaccinationStatuses(patient).stream()
                .filter(status -> status.getDueDate() != null)
                .filter(status -> !status.getDueDate().before(startDate) && !status.getDueDate().after(endDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public void recordVaccineAdministration(Patient patient, VaccinationScheduleEntry entry, 
                                          Encounter encounter, Obs immunizationObs) {
        
    }
    
    @Override
    public boolean isVaccineAdministered(Patient patient, Concept vaccineConcept, Integer doseNumber) {
        List<Obs> vaccineObs = getAdministeredVaccinesForConcept(patient, vaccineConcept);
        return vaccineObs.size() >= doseNumber;
    }
    
    @Override
    public List<Obs> getAdministeredVaccines(Patient patient) {
        return new ArrayList<>();
    }
    
    @Override
    public List<Obs> getAdministeredVaccinesForConcept(Patient patient, Concept vaccineConcept) {
        return new ArrayList<>();
    }
    
    @Override
    public List<VaccinationScheduleRule> evaluateRulesForPatient(Patient patient, VaccinationScheduleEntry entry) {
        return new ArrayList<>();
    }
    
    @Override
    public boolean hasContraindications(Patient patient, VaccinationScheduleEntry entry) {
        return !getContraindications(patient, entry).isEmpty();
    }
    
    @Override
    public List<VaccinationScheduleRule> getContraindications(Patient patient, VaccinationScheduleEntry entry) {
        return evaluateRulesForPatient(patient, entry).stream()
                .filter(rule -> rule.getRuleType() == VaccinationScheduleRule.RuleType.CONTRAINDICATION)
                .filter(rule -> rule.evaluateRule(patient, new Date()))
                .collect(Collectors.toList());
    }
    
    @Override
    public PatientVaccinationStatus markVaccineAsContraindicated(Patient patient, VaccinationScheduleEntry entry, 
                                                               String reason) {
        PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);
        status.setContraindicationReason(reason);
        return status;
    }
    
    @Override
    public PatientVaccinationStatus markVaccineAsSkipped(Patient patient, VaccinationScheduleEntry entry, 
                                                       String reason) {
        PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);
        status.setStatus(PatientVaccinationStatus.Status.SKIPPED);
        return status;
    }
    
    @Override
    public List<PatientVaccinationStatus> getVaccinationHistory(Patient patient) {
        return calculateVaccinationStatuses(patient).stream()
                .filter(status -> status.getStatus() == PatientVaccinationStatus.Status.APPLIED)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateDoseSequence(Patient patient, VaccinationScheduleEntry entry) {
        if (entry.getDoseNumber() <= 1) {
            return true;
        }
        
        return isVaccineAdministered(patient, entry.getVaccineConcept(), entry.getDoseNumber() - 1);
    }
    
    @Override
    public List<VaccinationScheduleEntry> getNextRecommendedVaccinations(Patient patient, int maxResults) {
        return getDueVaccinations(patient, new Date()).stream()
                .limit(maxResults)
                .map(PatientVaccinationStatus::getScheduleEntry)
                .collect(Collectors.toList());
    }
}