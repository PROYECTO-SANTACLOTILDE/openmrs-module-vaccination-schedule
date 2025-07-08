package org.openmrs.module.vaccinationschedule.web.rest;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.vaccinationschedule.PatientVaccinationStatus;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@SubResource(parent = PatientRestController.class, path = "vaccinationstatus",
             supportedClass = PatientVaccinationStatus.class, supportedOpenmrsVersions = {"2.6.*"})
public class PatientVaccinationStatusRestController extends DelegatingSubResource<PatientVaccinationStatus, Patient, PatientRestController> {
    
    @Override
    public PatientVaccinationStatus getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Get by unique ID not supported for vaccination status");
    }
    
    @Override
    protected void delete(PatientVaccinationStatus delegate, String reason, RequestContext context) throws ResponseException {
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
    protected PageableResult doGetAll(Patient parent, RequestContext context) throws ResponseException {
        String type = context.getRequest().getParameter("type");
        String daysParam = context.getRequest().getParameter("days");
        
        VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
        List<PatientVaccinationStatus> statuses;
        
        if ("due".equals(type)) {
            statuses = service.getDueVaccinations(parent, new Date());
        } else if ("overdue".equals(type)) {
            statuses = service.getOverdueVaccinations(parent, new Date());
        } else if ("upcoming".equals(type)) {
            int days = daysParam != null ? Integer.parseInt(daysParam) : 30;
            statuses = service.getUpcomingVaccinations(parent, days);
        } else {
            statuses = service.calculateVaccinationStatuses(parent);
        }
        
        return new NeedsPaging<PatientVaccinationStatus>(statuses, context);
    }
    
    @Override
    public Patient getParent(PatientVaccinationStatus instance) {
        return instance.getPatient();
    }
    
    @Override
    public void setParent(PatientVaccinationStatus instance, Patient parent) {
        instance.setPatient(parent);
    }
    
    @Override
    public String getResourceVersion() {
        return RestConstants.VERSION_1;
    }
}