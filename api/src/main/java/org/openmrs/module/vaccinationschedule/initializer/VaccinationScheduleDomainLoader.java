package org.openmrs.module.vaccinationschedule.initializer;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class VaccinationScheduleDomainLoader extends BaseLineProcessor<VaccinationSchedule> {
    
    public static final String DOMAIN_NAME = "vaccinationschedules";
    
    protected static final String HEADER_UUID = "uuid";
    protected static final String HEADER_NAME = "name";
    protected static final String HEADER_DESCRIPTION = "description";
    protected static final String HEADER_COUNTRY_CODE = "country_code";
    protected static final String HEADER_VERSION = "version";
    protected static final String HEADER_EFFECTIVE_DATE = "effective_date";
    protected static final String HEADER_EXPIRY_DATE = "expiry_date";
    protected static final String HEADER_SOURCE_AUTHORITY = "source_authority";
    
    @Autowired
    private VaccinationScheduleService vaccinationScheduleService;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public Domain getDomain() {
        return Domain.valueOf(DOMAIN_NAME.toUpperCase());
    }
    
    @Override
    public VaccinationSchedule bootstrap(CsvLine line) throws IllegalArgumentException {
        String uuid = line.get(HEADER_UUID);
        
        VaccinationSchedule schedule = null;
        if (StringUtils.isNotBlank(uuid)) {
            schedule = vaccinationScheduleService.getVaccinationScheduleByUuid(uuid);
        }
        
        if (schedule == null) {
            schedule = new VaccinationSchedule();
            if (StringUtils.isNotBlank(uuid)) {
                schedule.setUuid(uuid);
            }
        }
        
        return schedule;
    }
    
    @Override
    public VaccinationSchedule fill(VaccinationSchedule schedule, CsvLine line) throws IllegalArgumentException {
        
        schedule.setName(line.get(HEADER_NAME, true));
        schedule.setDescription(line.get(HEADER_DESCRIPTION));
        schedule.setCountryCode(line.get(HEADER_COUNTRY_CODE));
        schedule.setSourceAuthority(line.get(HEADER_SOURCE_AUTHORITY));
        
        String versionStr = line.get(HEADER_VERSION);
        if (StringUtils.isNotBlank(versionStr)) {
            try {
                schedule.setVersion(Integer.parseInt(versionStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid version number: " + versionStr, e);
            }
        }
        
        String effectiveDateStr = line.get(HEADER_EFFECTIVE_DATE, true);
        try {
            Date effectiveDate = dateFormat.parse(effectiveDateStr);
            schedule.setEffectiveDate(effectiveDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid effective date format: " + effectiveDateStr + 
                    ". Expected format: yyyy-MM-dd", e);
        }
        
        String expiryDateStr = line.get(HEADER_EXPIRY_DATE);
        if (StringUtils.isNotBlank(expiryDateStr)) {
            try {
                Date expiryDate = dateFormat.parse(expiryDateStr);
                schedule.setExpiryDate(expiryDate);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid expiry date format: " + expiryDateStr + 
                        ". Expected format: yyyy-MM-dd", e);
            }
        }
        
        return schedule;
    }
    
    @Override
    protected VaccinationSchedule save(VaccinationSchedule schedule) {
        return vaccinationScheduleService.saveVaccinationSchedule(schedule);
    }
}