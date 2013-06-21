/**
* Copyright (c) 2012, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.reports.dashboard

import org.chai.memms.AbstractEntityController

/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class IndicatorController  extends AbstractEntityController {

    def indicatorService

    def list = {
        adaptParamsForList()
        def model = listModel(indicatorService.listIndicators(params))
        renderView(model, request.xhr)
    }

    def search = {
        adaptParamsForList()
        def model = listModel(indicatorService.searchIndicators(params)) << [q:params['q']]
        renderView(model, request.xhr)
    }

    def getAjaxData = {
        List<Indicator> indicators = indicatorService.listAllIndicators()
        render(contentType:"text/json") {
            elements = array {
                indicators.each { indicator ->
                    elem (
                        key: indicator.id,
                        value: indicator.name
                    )
                }
            }
        }
    }
    
    def renderView(def model, def ajax) {
        if(ajax) {
            renderAjax(model)
        } else {
            renderHtml(model)
        }
    }

    def renderHtml(def model) {
        render(view:"/entity/list", model:model << [
                template:"/indicator/indicatorList",
                listTop:"/indicator/listTop"
            ])
    }

    def renderAjax(def model) {
        render(contentType:"text/html") {
            results = [
                g.render(template:"/entity/indicator/indicatorList", model:model)
            ]
        }
    }

    def searchModel(def entities, def params) {
        return listModel(entities) << [q:params['q']]
    }

    def listModel(def entities) {
        return [
            entities: entities,
            entityCount: entities.totalCount,
            entityClass:getEntityClass(),
            code: getLabel()
        ]
    }

    def getModel(def entity) {
        [indicator: entity]
    }

    def bindParams(def entity) {
        entity.properties = params
    }

    def getTemplate() {
        return "/entity/indicator/createIndicator"
    }

    def getLabel() {
        return "indicator.label"
    }

    def getEntityClass() {
        return Indicator.class
    }

    def getEntity(def id) {
        return Indicator.get(id)
    }

    def createEntity() {
        return new Indicator()
    }
}
