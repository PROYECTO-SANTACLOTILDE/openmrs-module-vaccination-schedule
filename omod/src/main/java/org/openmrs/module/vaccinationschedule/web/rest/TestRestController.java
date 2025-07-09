package org.openmrs.module.vaccinationschedule.web.rest;

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
import org.openmrs.module.webservices.rest.web.RequestContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Test data class
class TestData {
    private String uuid;
    private String name;
    private String message;
    private String status;
    private Map<String, Object> details;

    public TestData() {
        this.details = new HashMap<>();
        this.details.put("moduleLoaded", true);
        this.details.put("timestamp", System.currentTimeMillis());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

@Resource(name = RestConstants.VERSION_1 + "/vaccinationtest", supportedClass = TestData.class, supportedOpenmrsVersions = {"2.6.*"})
public class TestRestController extends DelegatingCrudResource<TestData> {

    @Override
    public TestData getByUniqueId(String uniqueId) {
        TestData data = new TestData();
        data.setUuid(uniqueId);
        data.setName("Test Data " + uniqueId);
        data.setMessage("Module is working!");
        return data;
    }

    @Override
    protected void delete(TestData delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Delete not supported for test data");
    }

    @Override
    public TestData newDelegate() {
        return new TestData();
    }

    @Override
    public TestData save(TestData delegate) {
        delegate.setMessage("Saved: " + delegate.getName());
        return delegate;
    }

    @Override
    public void purge(TestData delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Purge not supported for test data");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addSelfLink();
            return description;
        } else if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("message");
            description.addProperty("status");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("message");
            description.addProperty("status");
            description.addProperty("details");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("name");
        description.addProperty("message");
        description.addProperty("status");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        TestData data1 = new TestData();
        data1.setUuid("test-1");
        data1.setName("Test 1");
        data1.setMessage("Vaccination Schedule Module is loaded!");
        data1.setStatus("active");

        TestData data2 = new TestData();
        data2.setUuid("test-2");
        data2.setName("Test 2");
        data2.setMessage("REST endpoints are working!");
        data2.setStatus("active");

        List<TestData> testData = Arrays.asList(data1, data2);
        return new NeedsPaging<TestData>(testData, context);
    }

    @Override
    public String getResourceVersion() {
        return RestConstants.VERSION_1;
    }
}