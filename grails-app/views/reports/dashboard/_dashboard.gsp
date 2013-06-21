<div class="entity-list">
  <div class="heading1-bar"></div>
  <div id="list-grid" class="v-tabs">
    <ul id='top_tabs' class="v-tabs-nav left" >
      <g:each in="${categoryItems}" status="i" var="mapItem">
        <li ${(i==0)?'class="active"':''}>
          <span class="v-tabs-formula" style="background: ${mapItem.value.color}; z-index: 5000"> </span>
          <a id="${mapItem.key}">${mapItem.value.name}</a>
        </li>
      </g:each>
    </ul>
    <div class="v-tabs-content right">
      <a id="showhide" class="right" href="#" ><g:message code="default.reports.dashboard.filter.by.color.label"/></a>
      <ul class="v-tabs-filters">
        <li><input type="checkbox" name="green"/><label>Green</label></li>
        <li><input type="checkbox" name="yellow"/><label>Yellow</label></li>
        <li><input type="checkbox" name="red"/><label>Red</label></li>
      </ul>
      <div class="filters main">
        <script src="/memms/static/js/reports/dashboard_init.js" type="text/javascript"></script>
        <!-- Categories tabs -->
        <g:each in="${categoryItems}" status="categoryCount" var="mapItem">
          <g:set var="categoryCode" value="${mapItem.key}" />
          <g:set var="categoryItem" value="${mapItem.value}" />
          <div id="${categoryCode}" style="display: none">
            <ul class="v-tabs-list">
              <g:each in="${categoryItem.indicatorItems}" status="indicatorCount" var="indicatorItem">
                <li class="v-tabs-row">
                  <p>
                    <a class="v-tabs-name v-tabs-fold-toggle">
                      <span class="v-tabs-switch"><img src="${resource(dir:'images',file:'arrow.png')}"/></span> ${indicatorItem.name}
                    </a>
                    <span class="tooltip v-tabs-formula" style="background: ${indicatorItem.color}" original-title="${indicatorItem.formula}">${indicatorItem.formula}</span>
                <g:if test="${indicatorItem.unit=='%'}">
                  <span class="v-tabs-value"><g:formatNumber number="${indicatorItem.value}" format="0%"/></span>
                </g:if>
                <g:if test="${indicatorItem.unit!='%'}">
                  <span class="v-tabs-value"><g:formatNumber number="${indicatorItem.value}" type="number"/> ${indicatorItem.unit}</span>
                </g:if>
                </p>
                <div class="v-tabs-fold-container">
                  <ul class="v-tabs-nested-nav">
                    <li><a id='historic_trend' class='active' href="#">Historic Trend</a></li>
                    <li><a id='comparison' href="#">Comparison To Other Facilities</a></li>
                    <li><a id='geo_trend' href="#">Geographic Trend</a></li>
                    <li><a id='info_facility' href="#">Information By Facility</a></li>
                  </ul>
                  <div id="historic_trend" class='toggled_tab'>
                    <g:render template="/reports/dashboard/nested_tabs" />
                  </div>
                  <div id="comparison">
                    <g:render template="/reports/dashboard/nested_tabs" />
                  </div>
                  <div id="geo_trend">
                    <g:render template="/reports/dashboard/nested_tabs" />
                  </div>
                  <div id="info_facility">
                    <g:render template="/reports/dashboard/nested_tabs" />
                  </div>
                </div>
                </li>
              </g:each>
            </ul>
          </div>
        </g:each>
      </div>
    </div>
  </div>
</div>