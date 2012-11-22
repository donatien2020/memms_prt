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
package org.chai.memms

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.chai.location.LocationLevel;
import org.chai.memms.Maintenance;
import org.chai.memms.Maintenances;
import org.chai.memms.Inventory
import org.chai.memms.Inventories
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.PreventiveOrder;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation
import org.chai.location.Location

class MaintenanceService {

    static transactional = true
	def locationService
	def grailsApplication
	def equipmentService
	
	def getSkipLocationLevels() {
		List<LocationLevel> levels = []
		for (String skipLevel : grailsApplication.config.location.sector.skip.level) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	} 
	
	/**
	 * Searches for a Order that contains the search term
	 * Pass a null value for the criteria you want to be ignored in the search other than the search text
	 * NB workOrdersEquipment is named like this to avoid conflicting with the navigation property equipment
	 * @param text
	 * @param location
	 * @param workOrdersEquipment
	 * @param params
	 * @return
	 */
	def searchOrder(Class clazz, String text,DataLocation dataLocation,Equipment equipment,Map<String, String> params) {
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = clazz.createCriteria()
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(equipment)
				eq('equipment',equipment)
			if(dataLocation)
				equipment{eq('dataLocation',dataLocation)}
			or{
				ilike("description","%"+text+"%")
				equipment{
					or{
						ilike("code","%"+text+"%")
						ilike("serialNumber","%"+text+"%")
						ilike(dbFieldDescriptions,"%"+text+"%")
						type{
							or{
								ilike(dbFieldTypeNames,"%"+text+"%")
								ilike("code","%"+text+"%")
							}
						}
					}
				}
			}
		}
	}
	
	def getMaintenancesByLocation(Class clazz,def location,Set<DataLocationType> types,Map<String, String> params) {
		if(log.isDebugEnabled()) log.debug("getMaintenancesByLocation url params: "+params)
		List<Maintenance> maintenances = []
		Set<LocationLevel> skipLevels = getSkipLocationLevels()
		
		for(DataLocation dataLocation : location.collectDataLocations(skipLevels,types)){
			maintenances.add(new Maintenance(dataLocation:dataLocation,orderCount:this.getMaintenanceOrderByDataLocation(clazz,dataLocation,[:]).size()))
		}
		
		Maintenances maintenance = new Maintenances()
		//If user tries to access elements outside the range, return empty list
		//TODO this is ideally supposed to avoid trying to access out of range data, but could be a bite
		if(params.offset != null && params.max != null && params.offset > params.max) return maintenance
		//If user specifies the pagination params, use them. Else return the whole list
		if(params.offset != null && params.offset > 0  && params.max != null && params.max > 0) maintenance.maintenanceList = maintenances[(params.offset) .. ((params.offset + params.max) > maintenances.size() ? maintenances.size() - 1 : (params.offset + params.max))]
		else maintenance.maintenanceList = maintenances
		maintenance.totalCount = maintenances.size()
		return maintenance
	}
	
	def getMaintenanceOrderByDataLocationManages(Class clazz,def dataLocation,Map<String,String> params){
		List<Equipment> equipments = equipmentService.getEquipmentsByDataLocation(dataLocation,[:])
		equipments << equipmentService.getEquipmentsByDataLocationAndManages(dataLocation,[:]).asList()
		def criteria =  clazz.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			or{
				for(Equipment equipment: equipments)
					eq("equipment",equipment)
			}
		}
		
	}
	
	def getMaintenanceOrderByDataLocation(Class clazz,def dataLocation,Map<String,String> params){
		def criteria = clazz.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocation)
				equipment{ eq('dataLocation',dataLocation)}
		}
		
	}
	
	def getMaintenanceOrderByEquipment(Class clazz,def equipment,Map<String,String> params){
		def criteria = clazz.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				eq('equipment',equipment)
		}
		
	}
	
	def getMaintenanceOrderByCalculationLocation(Class clazz,def location,Map<String,String> params){
		def equipments = []
		def criteria = clazz.createCriteria();
		
		if(location instanceof DataLocation){
			equipments = equipmentService.getEquipmentsByDataLocation(location, [:])
		}else{
			def dataLocations = location.getDataLocations(null,null)
			for(DataLocation dataLocation: dataLocations)
				equipments.addAll(equipmentService.getEquipmentsByDataLocation(dataLocation, [:]))
		}
		
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			or{
				equipments.each { equipment ->
					eq("equipment",equipment)
				}
			}
		}
		
	}
}