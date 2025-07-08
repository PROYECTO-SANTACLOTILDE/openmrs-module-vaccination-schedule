package org.openmrs.module.vaccinationschedule.initializer;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VaccinationScheduleEntryDomainLoader extends BaseLineProcessor<VaccinationScheduleEntry> {
    
    public static final String DOMAIN_NAME = "vaccinationscheduleentries";
    
    protected static final String HEADER_UUID = "uuid";
    protected static final String HEADER_SCHEDULE_UUID = "schedule_uuid";
    protected static final String HEADER_VACCINE_CONCEPT = "vaccine_concept";
    protected static final String HEADER_DOSE_NUMBER = "dose_number";
    protected static final String HEADER_AGE_MIN_DAYS = "age_min_days";
    protected static final String HEADER_AGE_RECOMMENDED_DAYS = "age_recommended_days";
    protected static final String HEADER_AGE_MAX_DAYS = "age_max_days";
    protected static final String HEADER_INTERVAL_DAYS = "interval_from_previous_days";
    protected static final String HEADER_IS_MANDATORY = "is_mandatory";
    protected static final String HEADER_SORT_ORDER = "sort_order";
    protected static final String HEADER_ROUTE_CONCEPT = "route_concept";
    protected static final String HEADER_SITE_CONCEPT = "site_concept";
    
    @Autowired
    @Qualifier("vaccinationschedule.VaccinationScheduleService")
    private VaccinationScheduleService vaccinationScheduleService;
    
    @Autowired
    private ConceptService conceptService;
    
    public Domain getDomain() {
        return Domain.valueOf(DOMAIN_NAME.toUpperCase());
    }
    
    public VaccinationScheduleEntry bootstrap(CsvLine line) throws IllegalArgumentException {
        String uuid = line.get(HEADER_UUID);
        
        VaccinationScheduleEntry entry = null;
        if (StringUtils.isNotBlank(uuid)) {
            entry = vaccinationScheduleService.getScheduleEntryByUuid(uuid);
        }
        
        if (entry == null) {
            entry = new VaccinationScheduleEntry();
            if (StringUtils.isNotBlank(uuid)) {
                entry.setUuid(uuid);
            }
        }
        
        return entry;
    }
    
    @Override
    public VaccinationScheduleEntry fill(VaccinationScheduleEntry entry, CsvLine line) throws IllegalArgumentException {
        
        String scheduleUuid = line.get(HEADER_SCHEDULE_UUID, true);
        VaccinationSchedule schedule = vaccinationScheduleService.getVaccinationScheduleByUuid(scheduleUuid);
        if (schedule == null) {
            throw new IllegalArgumentException("No vaccination schedule found with UUID: " + scheduleUuid);
        }
        entry.setVaccinationSchedule(schedule);
        
        String vaccineConceptRef = line.get(HEADER_VACCINE_CONCEPT, true);
        Concept vaccineConcept = conceptService.getConceptByUuid(vaccineConceptRef);
        if (vaccineConcept == null) {
            vaccineConcept = conceptService.getConcept(vaccineConceptRef);
        }
        if (vaccineConcept == null) {
            throw new IllegalArgumentException("No vaccine concept found: " + vaccineConceptRef);
        }
        entry.setVaccineConcept(vaccineConcept);
        
        String doseNumberStr = line.get(HEADER_DOSE_NUMBER, true);
        try {
            entry.setDoseNumber(Integer.parseInt(doseNumberStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid dose number: " + doseNumberStr, e);
        }
        
        String ageMinStr = line.get(HEADER_AGE_MIN_DAYS, true);
        try {
            entry.setAgeInDaysMin(Integer.parseInt(ageMinStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid minimum age: " + ageMinStr, e);
        }
        
        String ageRecommendedStr = line.get(HEADER_AGE_RECOMMENDED_DAYS, true);
        try {
            entry.setAgeInDaysRecommended(Integer.parseInt(ageRecommendedStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid recommended age: " + ageRecommendedStr, e);
        }
        
        String ageMaxStr = line.get(HEADER_AGE_MAX_DAYS);
        if (StringUtils.isNotBlank(ageMaxStr)) {
            try {
                entry.setAgeInDaysMax(Integer.parseInt(ageMaxStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid maximum age: " + ageMaxStr, e);
            }
        }
        
        String intervalStr = line.get(HEADER_INTERVAL_DAYS);
        if (StringUtils.isNotBlank(intervalStr)) {
            try {
                entry.setIntervalFromPreviousDays(Integer.parseInt(intervalStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid interval days: " + intervalStr, e);
            }
        }
        
        String mandatoryStr = line.get(HEADER_IS_MANDATORY);
        if (StringUtils.isNotBlank(mandatoryStr)) {
            entry.setIsMandatory(Boolean.parseBoolean(mandatoryStr));
        }
        
        String sortOrderStr = line.get(HEADER_SORT_ORDER);
        if (StringUtils.isNotBlank(sortOrderStr)) {
            try {
                entry.setSortOrder(Integer.parseInt(sortOrderStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid sort order: " + sortOrderStr, e);
            }
        }
        
        String routeConceptRef = line.get(HEADER_ROUTE_CONCEPT);
        if (StringUtils.isNotBlank(routeConceptRef)) {
            Concept routeConcept = conceptService.getConceptByUuid(routeConceptRef);
            if (routeConcept == null) {
                routeConcept = conceptService.getConcept(routeConceptRef);
            }
            entry.setRouteConcept(routeConcept);
        }
        
        String siteConceptRef = line.get(HEADER_SITE_CONCEPT);
        if (StringUtils.isNotBlank(siteConceptRef)) {
            Concept siteConcept = conceptService.getConceptByUuid(siteConceptRef);
            if (siteConcept == null) {
                siteConcept = conceptService.getConcept(siteConceptRef);
            }
            entry.setSiteConcept(siteConcept);
        }
        
        return entry;
    }
    
    protected VaccinationScheduleEntry save(VaccinationScheduleEntry entry) {
        return vaccinationScheduleService.saveScheduleEntry(entry);
    }
}