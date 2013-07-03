/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chai.memms.reports.dashboard
import grails.test.mixin.TestFor
import grails.plugin.spock.UnitSpec;
import org.chai.memms.reports.dashboard.Indicator
import org.chai.memms.reports.dashboard.LocationReport
import org.chai.memms.reports.dashboard.IndicatorDescriptor
import org.chai.memms.reports.dashboard.IndicatorCategory
import org.chai.memms.reports.dashboard.Indicator
import org.chai.memms.reports.dashboard.IndicatorValue
import org.chai.memms.reports.dashboard.MemmsReport
/**
/**
 *
 * @author donatien,Antoine
 */
@TestFor(IndicatorValue)
class IndicatorValueSpec  extends UnitSpec {
	
	  def "indicator Valueis valid"() {
          setup:
          mockForConstraintsTests(LocationReport)
          mockDomain(LocationReport)
          mockDomain(MemmsReport)
          mockDomain(IndicatorCategory)
          mockDomain(Indicator)
          mockDomain(IndicatorValue)
         
          when:
          Date currentDate=new Date()
            def memmsReport=new MemmsReport(generatedAt:currentDate).save(failOnError: true, flush:true)
            memmsReport.validate()
           
            def locationReport = new LocationReport(generatedAt: currentDate, memmsReport: memmsReport, location:null).save()
          
             def categoryy=new IndicatorCategory(code:IndicatorDescriptor.PRIVENTIVE_MAINTENANCE,name_en:"PRIVENTIVE_MAINTENANCE",name_fr:"PRIVENTIVE_MAINTENANCE",redToYellowThreshold:60,yellowToGreenThreshold:80).save(failOnError: true, flush:true)
         
           println" category is :"+categoryy
          def indicator=new Indicator(category:categoryy, code:"SHARE_OPE_EQUIPMENT", name_en:"Share of operational equipment",name_fr:"Share of operational equipment",description_en:"Share of operational equipment",description_fr:"Share of operational equipment", formula_en:"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", formula_fr:"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})",unit:"%",redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, historyItems:8, queryScript:IndicatorDescriptor.SHARE_OPERATIONAL_SIMPLE_SLD7, groupName_en:"Type of Equipment", groupName_fr:"Type of Equipment", groupQueryScript:IndicatorDescriptor.SHARE_OPERATIONAL_GROUP_SLD7,sqlQuery:false, active:true).save(failOnError: true, flush:true)
	  
          indicator.validate()
           def indicatorValue=new IndicatorValue(computedAt: currentDate, locationReport: locationReport, indicator: indicator, computedValue:30.0).save()
       
            
        
             indicatorValue.validate()
             println" the created indicatorValue :"+indicatorValue

          then:
           indicatorValue!=null
           
          
          
          
           
          

   }
}

