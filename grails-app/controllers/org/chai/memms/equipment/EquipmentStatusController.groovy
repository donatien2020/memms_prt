package org.chai.memms.equipment

import org.chai.memms.AbstractEntityController;
import org.chai.memms.equipment.EquipmentStatus.Status;

class EquipmentStatusController extends AbstractEntityController{

    def getEntity(def id) {
		return EquipmentStatus.get(id);
	}
	def createEntity() {
		def entity = new EquipmentStatus();
		if(!params["equipment.id"]) entity.equipment = Equipment.get(params.int("equipment"))
		return entity;
	}

	def getTemplate() {
		return "/entity/equipmentStatus/createEquipmentStatus";
	}

	def getLabel() {
		return "equipment.status.label";
	}
	
	def getEntityClass() {
		return EquipmentStatus.class;
	}
	
	def bindParams(def entity) {
		entity.changedBy= getUser()
		if(!entity.id)
			entity.statusChangeDate=new Date()	
		bindData(entity,params:[excludes:["current"]])
		if(params["current"]=='on'){
			def status = EquipmentStatus.findByCurrentAndEquipment(true,entity.equipment)
			if(status) {
				status.current=false
				status.save()
			}
			entity.current = true
		}else{
			entity.current = false
		}
	}
	
	def getModel(def entity) {
		[
			status:entity
		]
	}
	
	def list={
		adaptParamsForList()
		def equipment = Equipment.get(params.int("equipment"))
		
		if (equipment == null) {
			response.sendError(404)
		}else{
			List<EquipmentStatus> equipmentStatus  = equipment.status.asList()
			def max = Math.min(params['offset']+params['max'], equipmentStatus.size())
			equipmentStatus = equipmentStatus.sort{a,b -> (a.current > b.current) ? -1 : 1}
		
			render(view:"/entity/list", model:[
				template: "equipmentStatus/equipmentStatusList",
				equipment: equipment,
				entities: equipmentStatus.subList(params['offset'], max),
				entityCount: equipmentStatus.size(),
				code: getLabel(),
				entityClass: getEntityClass()
				])
		}
	}
}
