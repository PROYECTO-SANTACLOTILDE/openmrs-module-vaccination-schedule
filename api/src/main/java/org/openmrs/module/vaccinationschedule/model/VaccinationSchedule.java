package org.openmrs.module.vaccinationschedule.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.BaseOpenmrsMetadata;

public class VaccinationSchedule extends BaseOpenmrsMetadata {
	
	private Integer scheduleId;
	
	private String name;
	
	private String description;
	
	private String countryCode;
	
	private Integer version = 1;
	
	private Date effectiveDate;
	
	private Date expiryDate;
	
	private String sourceAuthority;
	
	private Set<VaccinationScheduleEntry> entries;
	
	private Set<VaccinationScheduleRule> rules;
	
	public VaccinationSchedule() {
		super();
	}
	
	public VaccinationSchedule(String name, String countryCode, Date effectiveDate) {
		this();
		this.name = name;
		this.countryCode = countryCode;
		this.effectiveDate = effectiveDate;
	}
	
	@Override
	public Integer getId() {
		return scheduleId;
	}
	
	@Override
	public void setId(Integer id) {
		this.scheduleId = id;
	}
	
	public Integer getScheduleId() {
		return scheduleId;
	}
	
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public String getSourceAuthority() {
		return sourceAuthority;
	}
	
	public void setSourceAuthority(String sourceAuthority) {
		this.sourceAuthority = sourceAuthority;
	}
	
	public Set<VaccinationScheduleEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(Set<VaccinationScheduleEntry> entries) {
		this.entries = entries;
	}
	
	public Set<VaccinationScheduleRule> getRules() {
		return rules;
	}
	
	public void setRules(Set<VaccinationScheduleRule> rules) {
		this.rules = rules;
	}
	
	public boolean isActive() {
		Date now = new Date();
		if (getRetired()) {
			return false;
		}
		if (effectiveDate != null && effectiveDate.after(now)) {
			return false;
		}
		if (expiryDate != null && expiryDate.before(now)) {
			return false;
		}
		return true;
	}
	
	public List<VaccinationScheduleEntry> getEntriesByAge(int ageInDays) {
		if (entries == null) {
			return new java.util.ArrayList<>();
		}
		return entries.stream().filter(entry -> !entry.getVoided() && entry.isValidForAge(ageInDays))
		        .collect(java.util.stream.Collectors.toList());
	}
	
	public List<VaccinationScheduleEntry> getActiveEntries() {
		if (entries == null) {
			return new java.util.ArrayList<>();
		}
		return entries.stream().filter(entry -> !entry.getVoided()).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public String toString() {
		return "VaccinationSchedule{" + "scheduleId=" + scheduleId + ", name='" + name + '\'' + ", countryCode='"
		        + countryCode + '\'' + ", version=" + version + ", effectiveDate=" + effectiveDate + ", expiryDate="
		        + expiryDate + '}';
	}
}
