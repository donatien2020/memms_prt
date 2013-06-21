package org.chai.memms.reports.dashboard

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import org.chai.memms.reports.dashboard.Indicator
import org.chai.memms.reports.dashboard.IndicatorValue
import org.chai.memms.reports.dashboard.IndicatorCategory
import org.chai.memms.reports.dashboard.IndicatorComputationService
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(IndicatorComputationService)
@TestMixin(GrailsUnitTestMixin)
@Mock([Indicator,IndicatorValue,IndicatorCategory])
class DashboardReportSpec{
IndicatorComputationService indicatorComputationService
//    void setUp() {
//        // Setup logic here
//    }
//
//    void tearDown() {
//        // Tear down logic here
//    }

    void testSomething() {
    
def i=0

for(Indicator ind:Indicator.findAll()){
  def result = service.computeScript(ind.queryScript,ind.sql)
   assertEquals true, Integer.parseInt(result)
   if(log.isDebugEnabled()) log.debug("testing number:${i}")
   i++
}
       
        
        
    }
}
