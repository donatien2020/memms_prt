<%@ page import="org.grails.plugins.google.visualization.util.DateUtil" %>
<%@ page import="org.grails.plugins.google.visualization.data.Cell; org.grails.plugins.google.visualization.util.DateUtil" %>
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
                  <li><a id='historic_trend' class='active' href="#">Historic trend</a></li>
                  <li><a id='comparison' href="#">Comparison to other facilities</a></li>
                  <li><a id='geo_trend' href="#">Geographic trend</a></li>
                  <li><a id='info_facility' href="#">Information by ${indicatorItem.groupName}</a></li>
                </ul>
                <div id="historic_trend" class='toggled_tab'>
                
                   <g:if test="${indicatorItem.historyData()!=null}">
                 <gvisualization:scatterCoreChart elementId="historic_trend_chart_timeline_${indicatorItem.code}" title="Historical Trend" columns="${indicatorItem.historyColumns()}"  data="${indicatorItem.historyData()}" colors="${indicatorItem.historyColors()}"   hAxis="${new Expando(date: Date.parse('yyyy-MM-dd','2011-01-01'), title:"Dates")}" vAxis="${new Expando(title: 'Values', minValue: 0, maxValue: 15)}"  fontSize="${12}" width="${700}" />
                 <div id="historic_trend_chart_timeline_${indicatorItem.code}" style="width:700px; height:340px;"></div>
                   </g:if>
                </div>
                <div id="comparison">
                  <ul class="v-tabs-nested">
                    <g:each in="${indicatorItem.highestComparisonValueItems}" var="compItem">
                      <li class="v-tabs-row">
                        <span class="v-tabs-name">${compItem.facility}</span>
                        <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
                      <g:if test="${compItem.unit=='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
                      </g:if>
                      <g:if test="${compItem.unit!='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" type="number"/> ${compItem.unit}</span>
                      </g:if>
                      </li>
                    </g:each>
                    <li class="v-tabs-row">
                      <span class="v-tabs-name"> ... </span>
                    </li>
                    <g:each in="${indicatorItem.higherComparisonValueItems}" var="compItem">
                      <li class="v-tabs-row">
                        <span class="v-tabs-name">${compItem.facility}</span>
                        <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
                      <g:if test="${compItem.unit=='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
                      </g:if>
                      <g:if test="${compItem.unit!='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" type="number"/> ${compItem.unit}</span>
                      </g:if>
                      </li>
                    </g:each>
                    <li class="v-tabs-row">
                      <span class="v-tabs-name"> ... </span>
                    </li>
                    <g:each in="${indicatorItem.lowerComparisonValueItems}" var="compItem">
                      <li class="v-tabs-row">
                        <span class="v-tabs-name">${compItem.facility}</span>
                        <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
                      <g:if test="${compItem.unit=='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
                      </g:if>
                      <g:if test="${compItem.unit!='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" type="number"/> ${compItem.unit}</span>
                      </g:if>
                      </li>
                    </g:each>
                    <li class="v-tabs-row">
                      <span class="v-tabs-name"> ... </span>
                    </li>
                    <g:each in="${indicatorItem.lowestComparisonValueItems}" var="compItem">
                      <li class="v-tabs-row">
                        <span class="v-tabs-name">${compItem.facility}</span>
                        <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
                      <g:if test="${compItem.unit=='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
                      </g:if>
                      <g:if test="${compItem.unit!='%'}">
                        <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" type="number"/> ${compItem.unit}</span>
                      </g:if>
                      </li>
                    </g:each>
                  </ul>
                </div>
                <div id="geo_trend">
                  
                  
                   <g:if test="${indicatorItem.geoData()!=null}">
                   <gvisualization:map elementId="map_${indicatorItem.code}" 
                                       columns="${indicatorItem.geoColums()}" 
                                       data="${indicatorItem.geoData()}" 
                                       mapType="${'terrain'}" 
                                       zoomLevel="${2}" 
                                       useMapTypeControl="${true}" 
                                       showLine="${true}"
                                       showTip="${true}"
                                       /></g:if>
                 
                    <div id="map_${indicatorItem.code}" style="width: 700px; height: 400px"></div>
                   
                  
                </div>
                <div id="info_facility" style="width:auto; height: 400px; overflow: auto;">
                  <ul class="v-tabs-nested">
                    <g:each in="${indicatorItem.valuesPerGroup}" var="itemsMap">
                      <g:set var="itemKey" value="${itemsMap.key}" />
                      <g:set var="itemValue" value="${itemsMap.value}" />
                      <li class="v-tabs-row">
                        <span class="v-tabs-name">${itemKey}</span>
                        <span class="v-tabs-value">${itemValue} ${indicatorItem.unit}</span>
                      </li>
                    </g:each>
                  </ul>
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

