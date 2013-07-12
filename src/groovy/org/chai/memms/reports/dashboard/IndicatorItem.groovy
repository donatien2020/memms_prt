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

import org.chai.memms.reports.dashboard.LocationReport;
import org.chai.memms.reports.dashboard.Indicator;
import org.chai.memms.reports.dashboard.IndicatorValue;
import org.chai.memms.reports.dashboard.MemmsReport;
import org.chai.memms.reports.dashboard.GroupIndicatorValue;
import org.chai.location.CalculationLocation
import org.chai.location.DataLocationType
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.location.LocationLevel
import java.util.*
import org.chai.memms.security.User
import org.apache.shiro.SecurityUtils
import java.util.Collections;


/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class IndicatorItem {
    String categoryCode
    Date dateTime
    String code
    String name
    String groupName
    String formula
    Double value
    String unit
    String color
    List<HistoricalValueItem> historicalValueItems
    List<ComparisonValueItem> highestComparisonValueItems
    List<ComparisonValueItem> higherComparisonValueItems
    List<ComparisonValueItem> lowerComparisonValueItems
    List<ComparisonValueItem> lowestComparisonValueItems
    List<GeographicalValueItem> geographicalValueItems
    Map<String, Double> valuesPerGroup
  
    public IndicatorItem(IndicatorValue iv) {
        this.categoryCode = iv.indicator.category.code
        this.dateTime = iv.computedAt
        this.code = iv.indicator.code
        this.name = iv.indicator.name
        this.formula = iv.indicator.formula
        this.value = iv.computedValue
        this.unit = iv.indicator.unit
        this.groupName=iv.indicator.groupName
        
        Double red = iv.indicator.redToYellowThreshold
        Double green =  iv.indicator.yellowToGreenThreshold
        if(red < green) {
            if(this.value < red) {
                this.color = "red"
            } else if(this.value < green) {
                this.color = "yellow"
            } else {
                this.color = "green"
            }
        } else {
            if(this.value > red) {
                this.color = "red"
            } else if(this.value > green) {
                this.color = "yellow"
            } else {
                this.color = "green"
            }
        }
        this.historicalValueItems = new ArrayList<HistoricalValueItem>()
       
        this.geographicalValueItems = new ArrayList<GeographicalValueItem>()
       
          
        this.highestComparisonValueItems= new ArrayList<ComparisonValueItem>()
        this.higherComparisonValueItems= new ArrayList<ComparisonValueItem>()
        this.lowerComparisonValueItems= new ArrayList<ComparisonValueItem>()
        this.lowestComparisonValueItems= new ArrayList<ComparisonValueItem>()
        this.valuesPerGroup=new HashMap<String,Double>()
        
       
       
        this.geographicalValueItems.add(new GeographicalValueItem(iv))
        
        
        
        //####adding historical values
          
        for(IndicatorValue indV :  getHistoricValueIems(iv)) {
            
            this.historicalValueItems.add(new HistoricalValueItem(indV))
        }
       
        
        //#### adding geographical values
       
       
        for(IndicatorValue indV : getGeographicalValueItems(iv)) {
            this.geographicalValueItems.add(new GeographicalValueItem(indV))
          //  println" values :"+this.geographicalValueItems
           
        }
       
        //###adding comparison value items
        
        getComparisonValueItems(iv)
        
       //## adding groupValues
     
        for(GroupIndicatorValue grV:iv.groupIndicatorValues){
            this.valuesPerGroup.put(grV.name,grV.value)
        }
        
       
      
         
    }

    /**
     *Get historical values for this indicators = for diferent location reports
     */
    public def getHistoricValueIems(IndicatorValue iv) {
        if(iv != null) {
            def locationReports=null
            if(iv.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.MONTHLY)){
              
                locationReports = LocationReport.findAll("from LocationReport as locationReport  where locationReport.location.id='"+iv.locationReport.location.id+"' order by locationReport.generatedAt desc limit "+iv.indicator.historyItems+"")
             
            }else if(iv.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.QUARTERLY)){
            
                locationReports = LocationReport.findAll("from LocationReport as locationReport where month(locationReport.generatedAt) in (3,6,9,12) and locationReport.location.id='"+iv.locationReport.location.id+"' order by locationReport.generatedAt desc limit "+iv.indicator.historyItems+"")
             
            }else if(iv.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.YEARLY)){
                
                locationReports = LocationReport.findAll("from LocationReport as locationReport where month(locationReport.generatedAt)=12 and locationReport.location.id='"+iv.locationReport.location.id+"' order by locationReport.generatedAt desc limit "+iv.indicator.historyItems+"")  
       
           
            }
            
           
            return IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,iv.indicator)
        }
        return null
    }
    
  
    public void getComparisonValueItems(IndicatorValue currentValue){
        List<IndicatorValue> invVs=new ArrayList<IndicatorValue>()
        if(currentValue!=null) {
            
            def  simmilarFacilitiesOrLocations=getSimmilarFacilitiesOrlocations(currentValue)
           
            def locationReports=LocationReport.findAllByMemmsReportAndLocationInList(currentValue.locationReport.memmsReport,simmilarFacilitiesOrLocations)
            invVs.addAll(IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,currentValue.indicator))
             
            sortComparisonValues(currentValue,invVs)
         
           
        }
        
       
    }
 
    public void sortComparisonValues(IndicatorValue currentValue,List<IndicatorValue> invVs){
        Collections.sort(invVs);
        if(invVs.size() > 0){
     
           
            List<IndicatorValue> listOfHighest=new ArrayList<IndicatorValue>()
            List<IndicatorValue> listOfLowest=new ArrayList<IndicatorValue>()
            Double red = currentValue.indicator.redToYellowThreshold
            Double green =  currentValue.indicator.yellowToGreenThreshold
            if(red < green) {
                //increasing
                
                for(IndicatorValue iv:invVs){
               
                    if(iv.computedValue <= currentValue.computedValue && iv.id!=currentValue.id){
            
                        listOfLowest.add(iv)
                    }else if(iv.computedValue >= currentValue.computedValue && iv.id!=currentValue.id){
                        listOfHighest.add(iv)
               
                    }else if(iv.id == currentValue.id){
                        for(def i = 1 ; i <= 2 ; i++){
                    
                            if(invVs.indexOf(iv)+i<invVs.size())
                            this.higherComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(iv)+i)))
                            if(invVs.indexOf(iv)-i>=0)
                            this.lowerComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(iv)-i)))
                 
                        }
             
                    }       
           
                     
                    
                }
            
               
                
            }else{
                    
                //decreasing 
                
                for(IndicatorValue iv:invVs){
                
                    if(iv.computedValue<=currentValue.computedValue  && iv.id!=currentValue.id){
                        listOfHighest.add(iv)
                       
                    }else if(iv.computedValue >= currentValue.computedValue  && iv.id!=currentValue.id){
                        listOfLowest.add(iv)    
                    }else if(iv.id == currentValue.id){
               
                        for(def i=1 ; i<=2 ; i++){
                            if(invVs.indexOf(iv)+i<invVs.size())
                            this.lowerComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(iv)+i)))
                            if(invVs.indexOf(iv)-i>=0)
                            this.higherComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(iv)-i)))
                           
                            
                        }
             
                    }       
      
                       
                    
                }
                
         
            }
                
            def lowestCounter=0
            def highestcounter=0
        
            for(IndicatorValue iv: listOfLowest.reverse()){
                if(lowestCounter < 3)
                this.lowestComparisonValueItems.add(new ComparisonValueItem(iv)) 
                lowestCounter++
            }
           
            for(IndicatorValue iv:listOfHighest.reverse()){
                if(highestcounter < 3)
                this.highestComparisonValueItems.add(new ComparisonValueItem(iv))
                highestcounter++
            }
     
        }
         this.higherComparisonValueItems.reverse()
         
    }
    
    /**
     *
     *Gets geographical values for the current report
     **/
    
    
    public def getGeographicalValueItems(IndicatorValue indicatorValue){
      
        if(indicatorValue!=null){
           
            def  geographicalLocations=getDataLocationsInLocation(indicatorValue.locationReport.location)
             
            def locationReports=LocationReport.findAllByMemmsReportAndLocationInList(indicatorValue.locationReport.memmsReport,geographicalLocations)
                   
            
            return IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,indicatorValue.indicator)
              
        }
        return null
        
    }
    
    /**
     *Gets  facilities or locations similar to the curent facility/location from this indicator value
     */
    public def getSimmilarFacilitiesOrlocations(IndicatorValue indicatorValue){
        def  simmilarFacilitiesOrLocations=null
       
        if(indicatorValue!=null){
            if(indicatorValue.locationReport.location instanceof DataLocation){
               
                simmilarFacilitiesOrLocations=DataLocation.findAllByType(indicatorValue.locationReport.location.type)
             
            }else if(indicatorValue.locationReport.location instanceof Location){
               
         
                simmilarFacilitiesOrLocations=Location.findAllByLevel(indicatorValue.locationReport.location.level)
               
               
            }
            
            
        }
    
       
        return simmilarFacilitiesOrLocations
    }
    
    
    public def getDataLocationsInLocation(CalculationLocation location) {
        List<DataLocation> dataLocations = new ArrayList<DataLocation>()
        if (location instanceof Location) {
            dataLocations = location.getDataLocations(LocationLevel.findAll(), null)
            dataLocations.addAll(location.getChildren(null))
          
        } else if (location instanceof DataLocation) {
            dataLocations.add(location)
            dataLocations.addAll(location.manages)
        }
        
        
        return dataLocations;
    }
   
    
}
