<table class="items">
  <thead>
    <tr>
      <th/>
  <g:sortableColumn property="code"  title="${message(code: 'indicator.category.label')}" params="[q:q]" />
  <g:sortableColumn property="name"  title="${message(code: 'indicator.name.label')}" params="[q:q]" />
  <g:sortableColumn property="code"  title="${message(code: 'indicator.formula.label')}" params="[q:q]" />
  <th><g:message code="indicator.redToYellowThreshold.label"/></th>
<th><g:message code="indicator.yellowToGreenThreshold.label"/></th>
</tr>
</thead>
<tbody>
<g:each in="${entities}" status="i" var="indicator">
  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
    <td>
      <ul class="horizontal">
        <li>
          <a href="${createLinkWithTargetURI(controller:'indicator', action:'edit', params:[id: indicator.id])}"  class="edit-button">
            <g:message code="default.link.edit.label" />
          </a>
        </li>
      </ul>
    </td>
    <td><g:stripHtml field="${indicator.category.name}" chars="20"/></td>
  <td><g:stripHtml field="${indicator.name}" chars="60"/></td>
  <td><g:stripHtml field="${indicator.formula}" chars="30"/></td>
  <g:if test="${indicator.unit=='%'}">
    <td><g:formatNumber number="${indicator.redToYellowThreshold}" format="0%"/></td>
    <td><g:formatNumber number="${indicator.yellowToGreenThreshold}" format="0%"/></td>
  </g:if>
  <g:if test="${indicator.unit!='%'}">
    <td><g:formatNumber number="${indicator.redToYellowThreshold}" type="number"/> ${indicator.unit}</td>
    <td><g:formatNumber number="${indicator.yellowToGreenThreshold}" type="number"/> ${indicator.unit}</td>
  </g:if>
  </tr>
</g:each>
</tbody>
</table>
<g:render template="/templates/pagination" />
