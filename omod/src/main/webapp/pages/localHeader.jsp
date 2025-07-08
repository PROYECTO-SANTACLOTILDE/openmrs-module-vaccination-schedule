<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
    </li>
    <li>
        <a href="${pageContext.request.contextPath}/module/vaccinationschedule/vaccinationSchedule.form">
            <spring:message code="vaccinationschedule.manage" text="Manage Vaccination Schedules"/>
        </a>
    </li>
    <li>
        <a href="${pageContext.request.contextPath}/module/vaccinationschedule/patientSchedule.form">
            <spring:message code="vaccinationschedule.patientSchedules" text="Patient Schedules"/>
        </a>
    </li>
    <li>
        <a href="${pageContext.request.contextPath}/module/vaccinationschedule/reports.form">
            <spring:message code="vaccinationschedule.reports" text="Reports"/>
        </a>
    </li>
</ul>