package org.openmrs.module.vaccinationschedule.web.rest;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationStatus;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1
        + "/vaccinationstatus", supportedClass = PatientVaccinationStatus.class, supportedOpenmrsVersions = { "2.6.*",
                "2.7.*" })
public class PatientVaccinationStatusResource extends DelegatingCrudResource<PatientVaccinationStatus> {
	
	@Override
	public PatientVaccinationStatus getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException("Get by unique ID not supported for vaccination status");
	}
	
	@Override
	protected void delete(PatientVaccinationStatus delegate, String reason, RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Delete not supported for vaccination status");
	}
	
	@Override
	public PatientVaccinationStatus newDelegate() {
		return new PatientVaccinationStatus();
	}
	
	@Override
	public PatientVaccinationStatus save(PatientVaccinationStatus delegate) {
		throw new ResourceDoesNotSupportOperationException("Save not supported for vaccination status - it's computed");
	}
	
	@Override
	public void purge(PatientVaccinationStatus delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Purge not supported for vaccination status");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("vaccineName");
			description.addProperty("doseNumber");
			description.addProperty("status");
			description.addProperty("dueDate");
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("vaccineName");
			description.addProperty("doseNumber");
			description.addProperty("status");
			description.addProperty("dueDate");
			description.addProperty("appliedDate");
			description.addProperty("contraindicationReason");
			description.addProperty("scheduleEntry", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("vaccineName");
			description.addProperty("doseNumber");
			description.addProperty("status");
			description.addProperty("dueDate");
			description.addProperty("appliedDate");
			description.addProperty("contraindicationReason");
			description.addProperty("scheduleEntry", Representation.DEFAULT);
			description.addProperty("immunizationObs", Representation.DEFAULT);
			return description;
		}
		return null;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid == null || patientUuid.trim().isEmpty()) {
			throw new IllegalArgumentException("Patient UUID is required");
		}
		
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatientByUuid(patientUuid.trim());
		if (patient == null) {
			throw new IllegalArgumentException("Patient not found");
		}
		
		String type = context.getRequest().getParameter("type");
		String daysParam = context.getRequest().getParameter("days");
		
		VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
		List<PatientVaccinationStatus> statuses;
		
		try {
			if ("due".equals(type)) {
				statuses = service.getDueVaccinations(patient, new Date());
			} else if ("overdue".equals(type)) {
				statuses = service.getOverdueVaccinations(patient, new Date());
			} else if ("upcoming".equals(type)) {
				int days = 30; // default
				if (daysParam != null && !daysParam.trim().isEmpty()) {
					try {
						days = Integer.parseInt(daysParam.trim());
						if (days < 1 || days > 365) {
							throw new IllegalArgumentException("Days parameter must be between 1 and 365");
						}
					}
					catch (NumberFormatException e) {
						throw new IllegalArgumentException("Invalid days parameter: must be a number");
					}
				}
				statuses = service.getUpcomingVaccinations(patient, days);
			} else {
				statuses = service.calculateVaccinationStatuses(patient);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Error retrieving vaccination status: " + e.getMessage(), e);
		}
		
		return new NeedsPaging<PatientVaccinationStatus>(statuses, context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants.VERSION_1;
	}
}
