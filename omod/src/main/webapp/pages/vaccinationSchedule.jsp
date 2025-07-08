<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Vaccination Schedules" otherwise="/login.htm" redirect="/module/vaccinationschedule/vaccinationSchedule.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="vaccinationschedule.title" text="Vaccination Schedule Management" /></h2>

<div class="boxHeader">
    <spring:message code="vaccinationschedule.list.title" text="Vaccination Schedules" />
</div>

<div class="box">
    <div style="margin-bottom: 10px;">
        <a href="${pageContext.request.contextPath}/module/vaccinationschedule/schedule/add.form" class="button">
            <spring:message code="vaccinationschedule.add.title" text="Add New Vaccination Schedule" />
        </a>
    </div>
    
    <div id="scheduleList">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th><spring:message code="vaccinationschedule.name" text="Name" /></th>
                    <th><spring:message code="vaccinationschedule.country" text="Country" /></th>
                    <th><spring:message code="vaccinationschedule.version" text="Version" /></th>
                    <th><spring:message code="vaccinationschedule.effectiveDate" text="Effective Date" /></th>
                    <th><spring:message code="general.actions" text="Actions" /></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="schedule" items="${schedules}">
                    <tr>
                        <td>${schedule.name}</td>
                        <td>${schedule.countryCode}</td>
                        <td>${schedule.version}</td>
                        <td><openmrs:formatDate date="${schedule.effectiveDate}" type="medium" /></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/module/vaccinationschedule/schedule/edit.form?scheduleId=${schedule.scheduleId}">
                                <spring:message code="general.edit" text="Edit" />
                            </a>
                            |
                            <a href="${pageContext.request.contextPath}/module/vaccinationschedule/schedule/view.form?scheduleId=${schedule.scheduleId}">
                                <spring:message code="general.view" text="View" />
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>