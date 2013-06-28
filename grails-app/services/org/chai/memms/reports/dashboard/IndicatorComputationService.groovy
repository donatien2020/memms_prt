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

import groovy.sql.Sql

import java.util.List
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.chai.location.CalculationLocation
import org.chai.location.DataLocation
import org.chai.location.Location
import org.joda.time.DateTime
import org.chai.location.CalculationLocation
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.location.LocationLevel

import java.sql.ResultSet

/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class IndicatorComputationService {
    static transactional = true
    def sessionFactory

    static final String DATA_LOCATION_TOKEN = "@DATA_LOCATION"
    static final String USER_DEFINED_VARIABLE_REGEX = "#[0-9a-zA-Z_]+"
    static final Pattern userDefinedVariablePattern = Pattern.compile(USER_DEFINED_VARIABLE_REGEX)

    // CONFIGURE THIS METHOD TO BE EXECUTED EVERY DAY
    public def computeCurrentReport() {
       
        // 1. GET CURRENT DATE
        DateTime now = DateTime.now()
        Date currentDate = now.toDate()

        // 2. REMOVE PREVIOUS REPORTS IN THE SAME MONTH
        
         GroupIndicatorValue.executeUpdate("delete from GroupIndicatorValue where month(generatedAt) = " + now.getMonthOfYear() + " and year(generatedAt) = " + now.getYear())
         IndicatorValue.executeUpdate("delete from IndicatorValue where month(computedAt) = " + now.getMonthOfYear() + " and year(computedAt) = " + now.getYear())
         LocationReport.executeUpdate("delete from LocationReport where month(generatedAt) = " + now.getMonthOfYear() + " and year(generatedAt) = " + now.getYear())
         MemmsReport.executeUpdate("delete from MemmsReport where month(generatedAt) = " + now.getMonthOfYear() + " and year(generatedAt) = " + now.getYear())

        // 3. SAVE NEW MEMMS REPORT
        MemmsReport memmsReport = new MemmsReport(generatedAt: currentDate).save()

        // 4. COMPUTE REPORT FOR ALL FACILITIES
        for(DataLocation facility : DataLocation.findAll()) {
          // computeLocationReport(currentDate, facility, memmsReport)
        }

        // 5. COMPUTE REPORTS FOR ALL MAJOR LOCATIONS(NATION, PROVINCES, DISTRICTS)
        
        LocationLevel nationalLevel = LocationLevel.findByCode('National')
        LocationLevel provinceLevel = LocationLevel.findByCode('Province')
        LocationLevel districtLevel = LocationLevel.findByCode('District')

        for(Location nation : Location.findAllByLevel(nationalLevel)) {
           
            computeLocationReport(currentDate, nation, memmsReport)
           
        }

        for(Location province : Location.findAllByLevel(provinceLevel)) {
           // computeLocationReport(currentDate, province, memmsReport)
        }
        for(Location district : Location.findAllByLevel(districtLevel)) {
            // computeLocationReport(currentDate, district, memmsReport)
        }
         
        
    }

    def computeLocationReport(Date currentDate, CalculationLocation location, MemmsReport memmsReport) {
        
       
     
        LocationReport locationReport = new LocationReport(generatedAt: currentDate, memmsReport: memmsReport, location:location).save()
        for(Indicator indicator: Indicator.findAllByActive(true)) {
            def compvalue = computeIndicatorForLocation(indicator, location)
            IndicatorValue indicatorValue=new IndicatorValue(computedAt: currentDate, locationReport: locationReport, indicator: indicator, computedValue:compvalue).save()
       
            
            if(indicatorValue!=null){
            Map<String,Double> map= groupComputeIndicatorForLocation(indicatorValue.indicator,location)
        
            if(map!=null)
            for (Map.Entry<String, String> entry : (Set)map.entrySet()) {
                  
                def groupIndicatorValue=new GroupIndicatorValue(generatedAt:currentDate,name:entry.getKey(),value:entry.getValue(),indicatorValue:indicatorValue).save()
          
                }
        }
    }
        
   
       
}

def computeIndicatorForLocation(Indicator indicator, CalculationLocation location) {
    if (location == null) {
        return 0.0
    }
    List<DataLocation> dataLocations = new ArrayList<DataLocation>()
    if (location instanceof Location) {
        dataLocations = location.getDataLocations(LocationLevel.findAll(), null)
    } else if (location instanceof DataLocation) {
        dataLocations.add(location)
        dataLocations.addAll(location.manages)
    } else {
        return 0.0
    }
    if(dataLocations.size() == DataLocation.count()) {
        return computeIndicatorForAllDataLocations(indicator)
    }
    return computeIndicatorForDataLocations(indicator, dataLocations)
}

def computeIndicatorForDataLocations(Indicator indicator, def dataLocations) {
    if(dataLocations == null)
    return 0.0
    String cond = "";
    int counter = 0
    for(DataLocation loc : dataLocations) {
        if(counter > 0) {
            cond += ", "
        }
        cond += "" + loc.id
        counter++
    }
    if(counter == 0) {
        return 0.0
    } else if(counter == 1) {
        return computeIndicatorWithDataLocationCondition(indicator, " = " + cond + " ")
    } else {
        return computeIndicatorWithDataLocationCondition(indicator, " in (" + cond + ") ")
    }
}

def computeIndicatorForAllDataLocations(Indicator indicator) {
    return computeIndicatorWithDataLocationCondition(indicator, " is not null ")
}

def computeIndicatorWithDataLocationCondition(Indicator indicator, String dataLocationCondition) {
    String mScript = indicator.queryScript
    Matcher m =  userDefinedVariablePattern.matcher(indicator.queryScript)
    while (m.find()) {
        String code = m.group()
        Double value = UserDefinedVariable.findByCode(code.replace("#", "")).currentValue
        mScript = mScript.replace(code, "" + value)
    }
    return computeScriptWithDataLocationCondition(mScript, indicator.sqlQuery, dataLocationCondition)
}

def computeScriptWithDataLocationCondition(String script, Boolean sql, String dataLocationCondition) {
    return computeScript(script.replace(DATA_LOCATION_TOKEN, dataLocationCondition), sql);
}

def computeScript(String script, Boolean sql) {
    if(sql) {
        return executeSQL(script)
    } else {
        return executeHQL(script)
    }
}

def executeHQL(String hql) {
    def ret = 0.0
    def res =  sessionFactory.getCurrentSession().createQuery(hql).list()[0]
    if(res != null) {
        ret = res
    }
    return ret
}

def executeSQL(String sql) {
    def ret = 0.0
    def res =  sessionFactory.getCurrentSession().createSQLQuery(sql).list()[0]
    if(res != null) {
        ret = res
    }
    return ret
}
    
def groupComputeIndicatorForLocation(Indicator indicator, CalculationLocation location) {
    if (location == null) {
        return 0.0
    }
    List<DataLocation> dataLocations = new ArrayList<DataLocation>()
    if (location instanceof Location) {
        dataLocations = location.getDataLocations(LocationLevel.findAll(), null)
    } else if (location instanceof DataLocation) {
        dataLocations.add(location)
        dataLocations.addAll(location.manages)
    } else {
        return 0.0
    }
    if(dataLocations.size() == DataLocation.count()) {
        return groupComputeIndicatorForAllDataLocations(indicator)
    }
    return groupComputeIndicatorForDataLocations(indicator, dataLocations)
}
    
def groupComputeIndicatorForDataLocations(Indicator indicator, def dataLocations) {
    if(dataLocations == null)
    return 0.0
    String cond = "";
    int counter = 0
    for(DataLocation loc : dataLocations) {
        if(counter > 0) {
            cond += ", "
        }
        cond += "" + loc.id
        counter++
    }
    if(counter == 0) {
        return 0.0
    } else if(counter == 1) {
        return groupComputeIndicatorWithDataLocationCondition(indicator, " = " + cond + " ")
    } else {
        return groupComputeIndicatorWithDataLocationCondition(indicator, " in (" + cond + ") ")
    }
}
    
    
    
    
    
def groupComputeIndicatorForAllDataLocations(Indicator indicator) {
    return groupComputeIndicatorWithDataLocationCondition(indicator, " is not null ")
}
def groupComputeIndicatorWithDataLocationCondition(Indicator indicator, String dataLocationCondition) {
    String mScript =""
    if(indicator.groupQueryScript!=null){
            
        mScript = indicator.groupQueryScript
        
        Matcher m =  userDefinedVariablePattern.matcher(indicator.groupQueryScript)
        while (m.find()) {
            String code = m.group()
            Double value = UserDefinedVariable.findByCode(code.replace("#", "")).currentValue
            mScript = mScript.replace(code, "" + value)
        }
       
        return groupComputeScriptWithDataLocationCondition(mScript, indicator.sqlQuery, dataLocationCondition)
    }
    return null
}
def groupComputeScriptWithDataLocationCondition(String script, Boolean sql, String dataLocationCondition) {
    return groupComputeScript(script.replace(DATA_LOCATION_TOKEN, dataLocationCondition), sql);
}
    
    
def groupComputeScript(String script, Boolean sql) {
   
    if(sql) {
        return groupExecuteSQL(script)
    } else {
        return groupExecuteHQL(script)
    }
}

def groupExecuteHQL(String hql) {
    Map<String, Double> map = new HashMap<String, Double>()
    if(hql!=null){
        def res =  sessionFactory.getCurrentSession().createQuery(hql).list()
       
        if(res[0] != null) {
            
            for(Object arr : res) {
                map.put((String)arr[0],(Double)arr[1])
            }
        }}
    return map
}
   
def groupExecuteSQL(String sql) {
    Map<String, Double> map = new HashMap<String, Double>()
    if(sql!=null){
        def res =  sessionFactory.getCurrentSession().createSQLQuery(sql).list()
       
        if(res[0] != null) {
            for(Object arr : res) {
                if(Double.parseDouble(arr[1]+""))
                map.put(arr[0],(Double)arr[1])
            }
        }
    }
    return map
        
        
        

}
}