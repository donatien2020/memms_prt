<g:form class="search-form" url="[controller: controller, action: action]" method="GET">
	<!--Start-->
	<!--Note that the following hidden fields are being handled by ajax in form-utils-->
	<input type="text" name="q" value="${params.q}"/>
	<input type="hidden" name="dataLocation" value="${dataLocation?.id}"/>
	<input type="hidden" name="equipment" value="${equipment?.id}"/>
	<input type="hidden" name="type" value="${type?.id}"/>
	<input type="hidden" name="status" value="${status}"/>
	<!--End-->
	<button type="submit" class="medium"><g:message code="default.button.search.label"/></button>
</g:form>