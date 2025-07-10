package org.openmrs.module.vaccinationschedule.web.rest;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.model.VaccinationSchedule;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
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
        + "/vaccinationschedule", supportedClass = VaccinationSchedule.class, supportedOpenmrsVersions = { "2.6.*",
                "2.7.*" })
public class VaccinationScheduleRestController extends DelegatingCrudResource<VaccinationSchedule> {
	
	@Override
	public VaccinationSchedule getByUniqueId(String uniqueId) {
		if (uniqueId == null || uniqueId.trim().isEmpty()) {
			throw new IllegalArgumentException("Schedule UUID is required");
		}
		VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
		VaccinationSchedule schedule = service.getVaccinationScheduleByUuid(uniqueId.trim());
		if (schedule == null) {
			throw new IllegalArgumentException("Schedule not found");
		}
		return schedule;
	}
	
	@Override
	protected void delete(VaccinationSchedule delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate == null) {
			throw new IllegalArgumentException("Vaccination schedule is required");
		}
		VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
		service.retireVaccinationSchedule(delegate, reason);
	}
	
	@Override
	public VaccinationSchedule newDelegate() {
		return new VaccinationSchedule();
	}
	
	@Override
	public VaccinationSchedule save(VaccinationSchedule delegate) {
		if (delegate == null) {
			throw new IllegalArgumentException("Vaccination schedule data is required");
		}
		VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
		return service.saveVaccinationSchedule(delegate);
	}
	
	@Override
	public void purge(VaccinationSchedule delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Purge not supported for vaccination schedules");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("countryCode");
			description.addProperty("version");
			description.addProperty("display");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("countryCode");
			description.addProperty("version");
			description.addProperty("effectiveDate");
			description.addProperty("expiryDate");
			description.addProperty("sourceAuthority");
			description.addProperty("retired");
			description.addProperty("display");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("countryCode");
			description.addProperty("version");
			description.addProperty("effectiveDate");
			description.addProperty("expiryDate");
			description.addProperty("sourceAuthority");
			description.addProperty("retired");
			description.addProperty("retireReason");
			description.addProperty("entries", Representation.DEFAULT);
			description.addProperty("rules", Representation.DEFAULT);
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addProperty("description");
		description.addProperty("countryCode");
		description.addProperty("version");
		description.addRequiredProperty("effectiveDate");
		description.addProperty("expiryDate");
		description.addProperty("sourceAuthority");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
		boolean includeRetired = context.getIncludeAll();
		List<VaccinationSchedule> schedules = service.getAllSchedules(includeRetired);
		return new NeedsPaging<VaccinationSchedule>(schedules, context);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String countryCode = context.getRequest().getParameter("country");
		if (countryCode != null) {
			VaccinationScheduleService service = Context.getService(VaccinationScheduleService.class);
			List<VaccinationSchedule> schedules = service.getSchedulesByCountry(countryCode, context.getIncludeAll());
			return new NeedsPaging<VaccinationSchedule>(schedules, context);
		}
		return super.doSearch(context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants.VERSION_1;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(VaccinationSchedule schedule) {
		if (schedule.getName() == null) {
			return "";
		}
		return schedule.getName() + (schedule.getCountryCode() != null ? " (" + schedule.getCountryCode() + ")" : "");
	}
}
