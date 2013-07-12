/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chai.memms.reports.dashboard
import java.util.StringTokenizer;

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author Antoine, donatien,Pivot Access Ltd
 */
class GeographicalValueItem {
    
    String CORDINATE_REGEX_PARTERN = "\\[(.*?)\\]"
    Pattern cordinates = Pattern.compile(CORDINATE_REGEX_PARTERN)

    String dataLocation
    Double value
    String unit
    String color
    String longitude
    String latitude
    
    
    public GeographicalValueItem(IndicatorValue iv){
        
        this.value = iv.computedValue
        this.unit = iv.indicator.unit
        this.dataLocation = iv.locationReport.location.names
       
        
        if(iv.locationReport.location.coordinates!=null){
            
            Matcher match =  this.cordinates.matcher(iv.locationReport.location.coordinates.replace('[[[',""))
        
            def i=0
            while (match.find()) {
               
                if(i>0)
                break
                String[] token= match.group().replace("[","").replace("]","").split(",")
        
            if(token[0]!=null&&token[1]!=null){
                this.latitude=token[0]
                this.longitude=token[1]
               
                i++
            }
            }
        }
     
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
        
      
       
    }
    
 @Override
    public String toString() {
        return "GeographicalValueItem[dataLocation:" +this.dataLocation + "value:"+this.value+" Latitude :"+this.latitude+"Longitude"+this.longitude+ "]"
    }	
	
}

