package org.openmrs.module.vaccinationschedule.model;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Patient;

public class VaccinationScheduleRule extends BaseOpenmrsData {
	
	public enum RuleType {
		CONTRAINDICATION,
		PREREQUISITE,
		SPACING_RULE,
		ALTERNATIVE
	}
	
	private Integer ruleId;
	
	private VaccinationSchedule vaccinationSchedule;
	
	private RuleType ruleType;
	
	private Concept vaccineConcept;
	
	private Concept conditionConcept;
	
	private String ruleExpression;
	
	private String description;
	
	public VaccinationScheduleRule() {
		super();
	}
	
	public VaccinationScheduleRule(VaccinationSchedule schedule, RuleType ruleType, Concept vaccineConcept,
	    String description) {
		this();
		if (schedule == null) {
			throw new IllegalArgumentException("VaccinationSchedule cannot be null");
		}
		if (ruleType == null) {
			throw new IllegalArgumentException("RuleType cannot be null");
		}
		
		this.vaccinationSchedule = schedule;
		this.ruleType = ruleType;
		this.vaccineConcept = vaccineConcept;
		this.description = description;
	}
	
	@Override
	public Integer getId() {
		return ruleId;
	}
	
	@Override
	public void setId(Integer id) {
		this.ruleId = id;
	}
	
	public Integer getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
	
	public VaccinationSchedule getVaccinationSchedule() {
		return vaccinationSchedule;
	}
	
	public void setVaccinationSchedule(VaccinationSchedule vaccinationSchedule) {
		this.vaccinationSchedule = vaccinationSchedule;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	public Concept getVaccineConcept() {
		return vaccineConcept;
	}
	
	public void setVaccineConcept(Concept vaccineConcept) {
		this.vaccineConcept = vaccineConcept;
	}
	
	public Concept getConditionConcept() {
		return conditionConcept;
	}
	
	public void setConditionConcept(Concept conditionConcept) {
		this.conditionConcept = conditionConcept;
	}
	
	public String getRuleExpression() {
		return ruleExpression;
	}
	
	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean evaluateRule(Patient patient, Date evaluationDate) {
		switch (ruleType) {
			case CONTRAINDICATION:
				return evaluateContraindication(patient, evaluationDate);
			case PREREQUISITE:
				return evaluatePrerequisite(patient, evaluationDate);
			case SPACING_RULE:
				return evaluateSpacingRule(patient, evaluationDate);
			case ALTERNATIVE:
				return evaluateAlternative(patient, evaluationDate);
			default:
				return false;
		}
	}
	
	private boolean evaluateContraindication(Patient patient, Date evaluationDate) {
		if (conditionConcept != null) {
			return hasActiveDiagnosis(patient, conditionConcept, evaluationDate);
		}
		if (ruleExpression != null) {
			return evaluateExpression(patient, ruleExpression, evaluationDate);
		}
		return false;
	}
	
	private boolean evaluatePrerequisite(Patient patient, Date evaluationDate) {
		if (conditionConcept != null) {
			return hasActiveDiagnosis(patient, conditionConcept, evaluationDate);
		}
		if (ruleExpression != null) {
			return evaluateExpression(patient, ruleExpression, evaluationDate);
		}
		return true;
	}
	
	private boolean evaluateSpacingRule(Patient patient, Date evaluationDate) {
		if (ruleExpression != null) {
			return evaluateExpression(patient, ruleExpression, evaluationDate);
		}
		return true;
	}
	
	private boolean evaluateAlternative(Patient patient, Date evaluationDate) {
		if (ruleExpression != null) {
			return evaluateExpression(patient, ruleExpression, evaluationDate);
		}
		return false;
	}
	
	private boolean hasActiveDiagnosis(Patient patient, Concept concept, Date evaluationDate) {
		return false;
	}
	
	private boolean evaluateExpression(Patient patient, String expression, Date evaluationDate) {
		return false;
	}
	
	public boolean isApplicable(VaccinationScheduleEntry entry) {
		if (entry == null) {
			return false;
		}
		
		if (vaccineConcept == null) {
			return true;
		}
		
		Concept entryConcept = entry.getVaccineConcept();
		if (entryConcept == null) {
			return false;
		}
		
		return vaccineConcept.equals(entryConcept);
	}
	
	@Override
	public String toString() {
		return "VaccinationScheduleRule{" + "ruleId=" + ruleId + ", ruleType=" + ruleType + ", vaccineConcept="
		        + (vaccineConcept != null && vaccineConcept.getName() != null ? vaccineConcept.getName().getName() : "null")
		        + ", conditionConcept="
		        + (conditionConcept != null && conditionConcept.getName() != null ? conditionConcept.getName().getName()
		                : "null")
		        + ", description='" + description + '\'' + '}';
	}
}
