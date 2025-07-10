package org.openmrs.module.vaccinationschedule.model;

import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;

public class VaccinationScheduleEntry extends BaseOpenmrsData {
	
	private Integer entryId;
	
	private VaccinationSchedule vaccinationSchedule;
	
	private Concept vaccineConcept;
	
	private Integer doseNumber;
	
	private Integer ageInDaysMin;
	
	private Integer ageInDaysRecommended;
	
	private Integer ageInDaysMax;
	
	private Integer intervalFromPreviousDays;
	
	private Boolean isMandatory = true;
	
	private Integer sortOrder = 0;
	
	private Concept routeConcept;
	
	private Concept siteConcept;
	
	public VaccinationScheduleEntry() {
		super();
	}
	
	public VaccinationScheduleEntry(VaccinationSchedule schedule, Concept vaccineConcept, Integer doseNumber,
	    Integer ageInDaysMin, Integer ageInDaysRecommended) {
		this();
		this.vaccinationSchedule = schedule;
		this.vaccineConcept = vaccineConcept;
		this.doseNumber = doseNumber;
		this.ageInDaysMin = ageInDaysMin;
		this.ageInDaysRecommended = ageInDaysRecommended;
	}
	
	@Override
	public Integer getId() {
		return entryId;
	}
	
	@Override
	public void setId(Integer id) {
		this.entryId = id;
	}
	
	public Integer getEntryId() {
		return entryId;
	}
	
	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
	}
	
	public VaccinationSchedule getVaccinationSchedule() {
		return vaccinationSchedule;
	}
	
	public void setVaccinationSchedule(VaccinationSchedule vaccinationSchedule) {
		this.vaccinationSchedule = vaccinationSchedule;
	}
	
	public Concept getVaccineConcept() {
		return vaccineConcept;
	}
	
	public void setVaccineConcept(Concept vaccineConcept) {
		this.vaccineConcept = vaccineConcept;
	}
	
	public Integer getDoseNumber() {
		return doseNumber;
	}
	
	public void setDoseNumber(Integer doseNumber) {
		this.doseNumber = doseNumber;
	}
	
	public Integer getAgeInDaysMin() {
		return ageInDaysMin;
	}
	
	public void setAgeInDaysMin(Integer ageInDaysMin) {
		this.ageInDaysMin = ageInDaysMin;
	}
	
	public Integer getAgeInDaysRecommended() {
		return ageInDaysRecommended;
	}
	
	public void setAgeInDaysRecommended(Integer ageInDaysRecommended) {
		this.ageInDaysRecommended = ageInDaysRecommended;
	}
	
	public Integer getAgeInDaysMax() {
		return ageInDaysMax;
	}
	
	public void setAgeInDaysMax(Integer ageInDaysMax) {
		this.ageInDaysMax = ageInDaysMax;
	}
	
	public Integer getIntervalFromPreviousDays() {
		return intervalFromPreviousDays;
	}
	
	public void setIntervalFromPreviousDays(Integer intervalFromPreviousDays) {
		this.intervalFromPreviousDays = intervalFromPreviousDays;
	}
	
	public Boolean getIsMandatory() {
		return isMandatory;
	}
	
	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}
	
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public Concept getRouteConcept() {
		return routeConcept;
	}
	
	public void setRouteConcept(Concept routeConcept) {
		this.routeConcept = routeConcept;
	}
	
	public Concept getSiteConcept() {
		return siteConcept;
	}
	
	public void setSiteConcept(Concept siteConcept) {
		this.siteConcept = siteConcept;
	}
	
	public boolean isValidForAge(int ageInDays) {
		if (ageInDays < 0) {
			return false;
		}
		
		if (ageInDaysMin == null) {
			return false;
		}
		
		if (ageInDays < ageInDaysMin) {
			return false;
		}
		
		if (ageInDaysMax != null && ageInDays > ageInDaysMax) {
			return false;
		}
		
		return true;
	}
	
	public VaccinationScheduleEntry getNextDose() {
		if (vaccinationSchedule == null || vaccineConcept == null || doseNumber == null) {
			return null;
		}
		
		List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		
		return entries.stream()
		        .filter(entry -> entry != null && entry.getVaccineConcept() != null
		                && entry.getVaccineConcept().equals(this.vaccineConcept))
		        .filter(entry -> entry.getDoseNumber() != null && entry.getDoseNumber() == this.doseNumber + 1).findFirst()
		        .orElse(null);
	}
	
	public VaccinationScheduleEntry getPreviousDose() {
		if (vaccinationSchedule == null || vaccineConcept == null || doseNumber == null || doseNumber <= 1) {
			return null;
		}
		
		List<VaccinationScheduleEntry> entries = vaccinationSchedule.getActiveEntries();
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		
		return entries.stream()
		        .filter(entry -> entry != null && entry.getVaccineConcept() != null
		                && entry.getVaccineConcept().equals(this.vaccineConcept))
		        .filter(entry -> entry.getDoseNumber() != null && entry.getDoseNumber() == this.doseNumber - 1).findFirst()
		        .orElse(null);
	}
	
	@Override
	public String toString() {
		return "VaccinationScheduleEntry{" + "entryId=" + entryId + ", vaccineConcept="
		        + (vaccineConcept != null ? vaccineConcept.getName() : "null") + ", doseNumber=" + doseNumber
		        + ", ageInDaysMin=" + ageInDaysMin + ", ageInDaysRecommended=" + ageInDaysRecommended + ", ageInDaysMax="
		        + ageInDaysMax + '}';
	}
}
