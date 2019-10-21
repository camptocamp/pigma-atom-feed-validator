import groovy.xml.DOMBuilder
import javax.xml.parsers.*

def atomUrl= "https://www.pigma.org/geonetwork/srv/fre/atom.predefined.service?uuid=b9a35aca-3a7e-41e5-a563-93f532fedca2"
def mdUrl = "https://www.pigma.org/geonetwork/srv/fre/xml.metadata.get?uuid=b9a35aca-3a7e-41e5-a563-93f532fedca2"

def tests = []

def checks = 0

try {
  atomUrl.toURL().text
  tests << ["serviceUrl.check", false, "URL de flux de service en erreur"]
} catch (def e) {
  tests << ["serviceUrl.check", true, "URL de flux de service en erreur"]
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
            tests << ["md-${currentMdUuid}.check", true, "métadonnée de données en erreur: ${currentMdUuid}"]
      }
      tests << ["md-${currentMdUuid}.check", false, "métadonnée de données en erreur: ${currentMdUuid}"]
    } catch (def e) {
      tests << ["md-${currentMdUuid}.check", false, "métadonnée de données en erreur: ${currentMdUuid}"]
    }
}

println """<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="pigma-atom-xml-feed-error" tests="${checks}">\n"""

 tests.each {
   def testName = it[0]
   def hasErrored = it[1]
   def msg = it[2]
   println """  <testcase classname="${testName}" name="${testName}">\n"""
   if (hasErrored) {
    println """    <error message="${msg}">${msg}</error>\n"""
  }
    println """  </testcase>\n"""
 }

 println """</testsuite>\n"""
