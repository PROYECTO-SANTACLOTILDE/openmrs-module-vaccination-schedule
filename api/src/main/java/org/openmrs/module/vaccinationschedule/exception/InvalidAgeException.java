package org.openmrs.module.vaccinationschedule.exception;

public class InvalidAgeException extends VaccinationScheduleException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidAgeException() {
		super();
	}
	
	public InvalidAgeException(String message) {
		super(message);
	}
	
	public InvalidAgeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidAgeException(int ageInDays, int minAge, Integer maxAge) {
		super(String.format("Age %d days is invalid. Must be between %d and %s days.", ageInDays, minAge,
		    maxAge != null ? maxAge.toString() : "unlimited"));
	}
}
