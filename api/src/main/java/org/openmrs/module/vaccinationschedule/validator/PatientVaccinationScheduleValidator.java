package org.openmrs.module.vaccinationschedule.validator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationSchedule;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { PatientVaccinationSchedule.class }, order = 50)
public class PatientVaccinationScheduleValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PatientVaccinationSchedule.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PatientVaccinationSchedule patientSchedule = (PatientVaccinationSchedule) target;
        
        if (patientSchedule == null) {
            errors.reject("error.general");
            return;
        }
        
        ValidationUtils.rejectIfEmpty(errors, "patient", "patientvaccinationschedule.patient.required");
        ValidationUtils.rejectIfEmpty(errors, "vaccinationSchedule", "patientvaccinationschedule.vaccinationSchedule.required");
        ValidationUtils.rejectIfEmpty(errors, "assignedByUser", "patientvaccinationschedule.assignedByUser.required");
        ValidationUtils.rejectIfEmpty(errors, "assignedDate", "patientvaccinationschedule.assignedDate.required");
        ValidationUtils.rejectIfEmpty(errors, "status", "patientvaccinationschedule.status.required");
        
        if (patientSchedule.getPatient() != null && patientSchedule.getPatient().getBirthdate() == null) {
            errors.rejectValue("patient", "patientvaccinationschedule.patient.birthdate.required");
        }
        
        if (patientSchedule.getVaccinationSchedule() != null && patientSchedule.getVaccinationSchedule().getRetired()) {
            errors.rejectValue("vaccinationSchedule", "patientvaccinationschedule.vaccinationSchedule.retired");
        }
        
        if (patientSchedule.getAssignedDate() != null && patientSchedule.getBirthDateAtAssignment() != null) {
            if (patientSchedule.getAssignedDate().before(patientSchedule.getBirthDateAtAssignment())) {
                errors.rejectValue("assignedDate", "patientvaccinationschedule.assignedDate.beforeBirthDate");
            }
        }
        
        if (patientSchedule.getPatient() != null && patientSchedule.getBirthDateAtAssignment() != null) {
            if (patientSchedule.getPatient().getBirthdate() != null && 
                !patientSchedule.getPatient().getBirthdate().equals(patientSchedule.getBirthDateAtAssignment())) {
                errors.rejectValue("birthDateAtAssignment", "patientvaccinationschedule.birthDateAtAssignment.mismatch");
            }
        }
    }
}