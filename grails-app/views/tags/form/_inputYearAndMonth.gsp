<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label>${message(code:'entity.expectedLifeTime.label')}</label>
	<div>
	<label for="${year}">${labelYear}</label>
	<input type="${type}" class="idle-field ${dateClass}" name="${year}" value="${fieldValue(bean:bean,field:field)}" ${active} ${readonly?'readonly="readonly"':''}></input>
	</div>
	<div>
	<label for="${month}">${labelMonth}</label>
	<input type="${type}" class="idle-field ${dateClass}" name="${month}" value="${fieldValue(bean:bean,field:field)}" ${active} ${readonly?'readonly="readonly"':''}></input>
	</div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>