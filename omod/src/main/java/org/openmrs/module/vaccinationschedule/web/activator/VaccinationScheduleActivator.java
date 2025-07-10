package org.openmrs.module.vaccinationschedule.web.activator;

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
			// Verify REST controllers are loaded
			log.info("Vaccination Schedule Module: Checking REST controller registration...");
			
			// Check if webservices.rest module is available
			try {
				Class.forName("org.openmrs.module.webservices.rest.web.RestService");
				log.info("WebServices REST module is available");
			}
			catch (ClassNotFoundException e) {
				log.error("WebServices REST module not found - REST endpoints will not work", e);
			}
			
			// Check if our controllers are in classpath
			try {
				Class.forName("org.openmrs.module.vaccinationschedule.web.rest.VaccinationScheduleRestController");
				log.info("VaccinationScheduleRestController found in classpath");
			}
			catch (ClassNotFoundException e) {
				log.error("VaccinationScheduleRestController not found", e);
			}
			
			try {
				Class.forName("org.openmrs.module.vaccinationschedule.web.rest.PatientVaccinationStatusRestController");
				log.info("PatientVaccinationStatusRestController found in classpath");
			}
			catch (ClassNotFoundException e) {
				log.error("PatientVaccinationStatusRestController not found", e);
			}
			
			log.info("Vaccination Schedule Module: Initialization complete");
		}
		catch (Exception e) {
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
