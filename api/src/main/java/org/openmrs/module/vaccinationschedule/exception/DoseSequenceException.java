package org.openmrs.module.vaccinationschedule.exception;

public class DoseSequenceException extends VaccinationScheduleException {
    
    private static final long serialVersionUID = 1L;
    
    public DoseSequenceException() {
        super();
    }
    
    public DoseSequenceException(String message) {
        super(message);
    }
    
    public DoseSequenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DoseSequenceException(String vaccineName, int requestedDose, int lastCompletedDose) {
        super(String.format("Cannot administer dose %d of %s. Patient has only completed dose %d. " +
                "Doses must be administered in sequence.", requestedDose, vaccineName, lastCompletedDose));
    }
}