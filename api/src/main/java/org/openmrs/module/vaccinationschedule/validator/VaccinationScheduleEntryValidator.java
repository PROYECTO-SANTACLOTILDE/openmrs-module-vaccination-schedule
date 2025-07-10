package org.openmrs.module.vaccinationschedule.validator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleEntry;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { VaccinationScheduleEntry.class }, order = 50)
public class VaccinationScheduleEntryValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return VaccinationScheduleEntry.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		VaccinationScheduleEntry entry = (VaccinationScheduleEntry) target;
		
		if (entry == null) {
			errors.reject("error.general");
			return;
		}
		
		ValidationUtils.rejectIfEmpty(errors, "vaccinationSchedule", "vaccinationscheduleentry.schedule.required");
		ValidationUtils.rejectIfEmpty(errors, "vaccineConcept", "vaccinationscheduleentry.vaccineConcept.required");
		ValidationUtils.rejectIfEmpty(errors, "doseNumber", "vaccinationscheduleentry.doseNumber.required");
		ValidationUtils.rejectIfEmpty(errors, "ageInDaysMin", "vaccinationscheduleentry.ageInDaysMin.required");
		ValidationUtils.rejectIfEmpty(errors, "ageInDaysRecommended",
		    "vaccinationscheduleentry.ageInDaysRecommended.required");
		
		if (entry.getDoseNumber() != null && entry.getDoseNumber() < 1) {
			errors.rejectValue("doseNumber", "vaccinationscheduleentry.doseNumber.invalid");
		}
		
		if (entry.getAgeInDaysMin() != null && entry.getAgeInDaysMin() < 0) {
			errors.rejectValue("ageInDaysMin", "vaccinationscheduleentry.ageInDaysMin.invalid");
		}
		
		if (entry.getAgeInDaysRecommended() != null && entry.getAgeInDaysRecommended() < 0) {
			errors.rejectValue("ageInDaysRecommended", "vaccinationscheduleentry.ageInDaysRecommended.invalid");
		}
		
		if (entry.getAgeInDaysMax() != null && entry.getAgeInDaysMax() < 0) {
			errors.rejectValue("ageInDaysMax", "vaccinationscheduleentry.ageInDaysMax.invalid");
		}
		
		if (entry.getAgeInDaysMin() != null && entry.getAgeInDaysRecommended() != null) {
			if (entry.getAgeInDaysMin() > entry.getAgeInDaysRecommended()) {
				errors.rejectValue("ageInDaysRecommended", "vaccinationscheduleentry.ageInDaysRecommended.beforeMin");
			}
		}
		
		if (entry.getAgeInDaysRecommended() != null && entry.getAgeInDaysMax() != null) {
			if (entry.getAgeInDaysRecommended() > entry.getAgeInDaysMax()) {
				errors.rejectValue("ageInDaysMax", "vaccinationscheduleentry.ageInDaysMax.beforeRecommended");
			}
		}
		
		if (entry.getIntervalFromPreviousDays() != null && entry.getIntervalFromPreviousDays() < 0) {
			errors.rejectValue("intervalFromPreviousDays", "vaccinationscheduleentry.intervalFromPreviousDays.invalid");
		}
		
		if (entry.getSortOrder() != null && entry.getSortOrder() < 0) {
			errors.rejectValue("sortOrder", "vaccinationscheduleentry.sortOrder.invalid");
		}
	}
}
