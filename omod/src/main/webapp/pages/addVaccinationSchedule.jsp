<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Vaccination Schedules" otherwise="/login.htm" redirect="/module/vaccinationschedule/schedule/add.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="vaccinationschedule.add.title" text="Add Vaccination Schedule" /></h2>

<spring:hasBindErrors name="schedule">
    <spring:message code="fix.error"/>
    <div class="error">
        <c:forEach items="${errors.allErrors}" var="error">
            <spring:message code="${error.defaultMessage}" text="${error.defaultMessage}"/><br/>
        </c:forEach>
    </div>
</spring:hasBindErrors>

<form method="post" action="${pageContext.request.contextPath}/module/vaccinationschedule/schedule/add.form">
    <div class="box">
        <table>
            <tr>
                <td><spring:message code="vaccinationschedule.name" text="Name" />*</td>
                <td>
                    <spring:bind path="schedule.name">
                        <input type="text" name="name" value="${status.value}" size="50" maxlength="255" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.description" text="Description" /></td>
                <td>
                    <spring:bind path="schedule.description">
                        <textarea name="description" rows="3" cols="50" maxlength="1000">${status.value}</textarea>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.country" text="Country Code" /></td>
                <td>
                    <spring:bind path="schedule.countryCode">
                        <input type="text" name="countryCode" value="${status.value}" size="3" maxlength="3" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.version" text="Version" />*</td>
                <td>
                    <spring:bind path="schedule.version">
                        <input type="text" name="version" value="${status.value}" size="10" maxlength="20" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.effectiveDate" text="Effective Date" />*</td>
                <td>
                    <spring:bind path="schedule.effectiveDate">
                        <input type="text" name="effectiveDate" value="${status.value}" size="10" onclick="showCalendar(this)" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.expiryDate" text="Expiry Date" /></td>
                <td>
                    <spring:bind path="schedule.expiryDate">
                        <input type="text" name="expiryDate" value="${status.value}" size="10" onclick="showCalendar(this)" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="vaccinationschedule.sourceAuthority" text="Source Authority" /></td>
                <td>
                    <spring:bind path="schedule.sourceAuthority">
                        <input type="text" name="sourceAuthority" value="${status.value}" size="50" maxlength="255" />
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
        </table>
        
        <div style="margin-top: 10px;">
            <input type="submit" name="save" value="<spring:message code="general.save" text="Save" />" />
            <input type="button" value="<spring:message code="general.cancel" text="Cancel" />" 
                   onclick="window.location='${pageContext.request.contextPath}/module/vaccinationschedule/vaccinationSchedule.form'" />
        </div>
    </div>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>