package org.openmrs.module.vaccinationschedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;

public class VaccinationScheduleActivator implements ModuleActivator {
    
    protected Log log = LogFactory.getLog(getClass());
    
    @Override
    public void willRefreshContext() {
        log.info("Refreshing Vaccination Schedule Module");
    }
    
    @Override
    public void contextRefreshed() {
        log.info("Vaccination Schedule Module refreshed");
    }
    
    @Override
    public void willStart() {
        log.info("Starting Vaccination Schedule Module");
    }
    
    @Override
    public void started() {
        log.info("Vaccination Schedule Module started successfully");
        
        try {
            log.info("Vaccination Schedule Module: Initialization complete");
        } catch (Exception e) {
            log.error("Error during Vaccination Schedule Module startup", e);
            throw new RuntimeException("Failed to start Vaccination Schedule Module", e);
        }
    }
    
    @Override
    public void willStop() {
        log.info("Stopping Vaccination Schedule Module");
    }
    
    @Override
    public void stopped() {
        log.info("Vaccination Schedule Module stopped");
    }
}