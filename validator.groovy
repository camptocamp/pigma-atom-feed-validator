import groovy.xml.DOMBuilder
import javax.xml.parsers.*

def atomUrl= "https://www.pigma.org/geonetwork/srv/fre/atom.predefined.service?uuid=b9a35aca-3a7e-41e5-a563-93f532fedca2"
def mdUrl = "https://www.pigma.org/geonetwork/srv/fre/xml.metadata.get?uuid=b9a35aca-3a7e-41e5-a563-93f532fedca2"

def errors = []

def checks = 0

try {
  atomUrl.toURL().text
} catch (def e) {
  errors << "flux de service en erreur"
}

checks++

def mdNs = [
  'gmd'   : 'http://www.isotc211.org/2005/gmd',
  'srv'   : 'http://www.isotc211.org/2005/srv',
  'xlink' : 'http://www.w3.org/1999/xlink'
]

def doc = new XmlSlurper().parse(mdUrl).declareNamespace(mdNs)

doc.'gmd:identificationInfo'.'srv:SV_ServiceIdentification'.'srv:operatesOn'.each {
    def currentMdUrl = it.@'xlink:href'
    def currentMdUuid = it.@'uuidref'
    checks ++
    try {
      def currentMd = new XmlSlurper().parse(currentMdUrl as String).declareNamespace(mdNs)
      if (currentMd.'gmd:MD_Metadata' == "") {
            errors << "métadonnée de données en erreur: ${currentMdUuid}"
      }
    } catch (def e) {
      errors << "métadonnée de données en erreur: ${currentMdUuid}"
    }  
}

println errors

println """<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="pigma-atom-xml-feed-error">\n"""
  
 errors.each {
   println """  <testcase classname="${it}">
    <error message="${it}">${it}</error>
    </testcase>\n"""
 }
  
 println """</testsuite>\n"""