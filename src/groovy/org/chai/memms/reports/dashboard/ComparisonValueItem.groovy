

package org.chai.memms.reports.dashboard
import org.chai.memms.reports.dashboard.IndicatorValue;
/**
 *
 * @authorAntoine, donatien,Pivot Access Ltd
 */
class ComparisonValueItem  implements Comparable<ComparisonValueItem>{
     
    @Override
    public int compareTo(Object obj){
    ComparisonValueItem comp = (ComparisonValueItem)obj;
    return this.value.compareTo(comp.value);
  }
    String facility
    Double value
    String unit
    String color
    
     public ComparisonValueItem(IndicatorValue iv) {
        
           
        this.value = iv.computedValue
        this.unit = iv.indicator.unit
        this.facility = iv.locationReport.location.names
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
        return "ComparisonValueItem[facility:" +facility + "value:"+value+"]"
    }
	
}

