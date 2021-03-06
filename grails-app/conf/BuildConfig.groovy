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
import org.codehaus.groovy.grails.resolve.GrailsRepoResolver

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
//        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        mavenCentral()
        mavenLocal()
		mavenRepo "http://m2repo.spockframework.org/snapshots"
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
		
		/**
		* Configure our resolver.
		*/
		def libResolver = new GrailsRepoResolver(null, null);
		libResolver.addArtifactPattern("https://github.com/clintonhealthaccess/repository/raw/master/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]")
		libResolver.addIvyPattern("https://github.com/clintonhealthaccess/repository/raw/master/[organisation]/[module]/ivys/ivy-[revision].xml")
		libResolver.name = "github"
        //libResolver.settings = ivySettings
		resolver libResolver
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// because of GRAILS-6147, this dependency is in lib instead of here
		//compile group: "net.sf.json-lib", name: "json-lib", version: "2.4", classifier: "jdk15"
		
		compile 'net.sf.ezmorph:ezmorph:1.0.6'
		runtime 'mysql:mysql-connector-java:5.1.5'
		compile 'org.supercsv:SuperCSV:1.52'
    }

    plugins {
	    build ":tomcat:$grailsVersion"
    
        compile ":hibernate:$grailsVersion"
		compile ":mail:1.0"
        compile ":jquery:1.7.1"
        compile ":resources:1.2-RC1"	
		compile ":cached-resources:1.0"
		compile ":cache-headers:1.1.5"
		compile ":shiro:1.1.5"
		compile ":springcache:1.3.1"
		compile ":compass-sass:0.7"
		compile ":mail:1.0"
		compile ":i18n-fields:0.6.1-CHAI"
		compile ":yui-minify-resources:0.1.5"
		compile ":rabbitmq-tasks:0.5.3-SNAPSHOT"
		compile ":chai-locations:0.5.1-CHAI"
		compile ":build-info-tag:0.3.1"
		//compile ':cloud-foundry:1.2.3'
		test (":spock:0.6") {changing = false}
		//		test ":geb:0.7.1"
		compile ":csv:0.3.1"
                
    }
	
}
//Comment this when deploying
//grails.plugin.location.'locations' = "../location"
