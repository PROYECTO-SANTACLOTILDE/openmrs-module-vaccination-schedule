package org.openmrs.module.vaccinationschedule.exception;

import org.openmrs.api.APIException;

public class VaccinationScheduleException extends APIException {
	
	private static final long serialVersionUID = 1L;
	
	public VaccinationScheduleException() {
		super();
	}
	
	public VaccinationScheduleException(String message) {
		super(message);
	}
	
	public VaccinationScheduleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public VaccinationScheduleException(Throwable cause) {
		super(cause);
	}
}
