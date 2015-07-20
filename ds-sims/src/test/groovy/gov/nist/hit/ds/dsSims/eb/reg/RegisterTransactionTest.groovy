package gov.nist.hit.ds.dsSims.eb.reg

import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.repository.api.RepositorySource
import gov.nist.hit.ds.repository.shared.ValidationLevel
import gov.nist.hit.ds.repository.simple.Configuration
import gov.nist.hit.ds.simSupport.simulator.SimIdentifier
import gov.nist.hit.ds.simSupport.endpoint.EndpointBuilder
import gov.nist.hit.ds.simSupport.simulator.SimHandle
import gov.nist.hit.ds.simSupport.transaction.TransactionRunner
import gov.nist.hit.ds.simSupport.utilities.SimEventAccess
import gov.nist.hit.ds.simSupport.utilities.SimSupport
import gov.nist.hit.ds.simSupport.utilities.SimUtils
import groovy.util.logging.Log4j
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by bmajur on 7/7/14.
 */

class RegisterTransactionTest extends Specification {
    private static Logger log = Logger.getLogger(RegisterTransactionTest);
    def actorsTransactions = '''
<ActorsTransactions>
    <transaction name="Register" id="rb" code="rb" asyncCode="r.as">
      <implClass value="gov.nist.hit.ds.dsSims.eb.transactions.RegisterTransaction"/>
        <request action="urn:ihe:iti:2007:RegisterDocumentSet-b"/>
        <response action="urn:ihe:iti:2007:RegisterDocumentSet-bResponse"/>
        <params multiPart="false" soap="true"/>
    </transaction>
    <actor name="Document Registry" id="reg"
      class="">
        <transaction id="rb"/>
    </actor>
</ActorsTransactions>
'''
    def header = '''
POST /axis2/services/registryBonedoc HTTP/1.1
Content-Type: multipart/related; charset=UTF-8; action="urn:ihe:iti:2007:RegisterDocumentSet-b"
User-Agent: Axis2
Host: localhost:9085'''
    def body = '''
<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope" xmlns:wsa="http://www.w3.org/2005/08/addressing">
  <soapenv:Header>
    <wsa:To>http://localhost:9085/axis2/services/registryBonedoc</wsa:To><wsa:MessageID>urn:uuid:1CF198BACD3697AB7D1203091097442</wsa:MessageID>
    <wsa:Action soapenv:mustUnderstand="true">urn:ihe:iti:2007:RegisterDocumentSet-b</wsa:Action>
  </soapenv:Header>
  <soapenv:Body>
    <lcm:SubmitObjectsRequest xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0">
      <rim:RegistryObjectList xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
        <rim:ExtrinsicObject id="Document01" mimeType="text/plain" objectType="urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1"/>
      </rim:RegistryObjectList>
    </lcm:SubmitObjectsRequest>
  </soapenv:Body>
</soapenv:Envelope>'''

    File repoDataDir
    def repoSource

    def initTest() {
        if (repoDataDir.exists()) {
            log.debug 'cleaning dataDir'
            FileUtils.cleanDirectory(repoDataDir)
        }
    }
    def setup() {
        SimSupport.initialize()
        new ActorTransactionTypeFactory().clear()
        new ActorTransactionTypeFactory().loadFromString(actorsTransactions)
        repoSource = Configuration.getRepositorySrc(RepositorySource.Access.RW_EXTERNAL)
        repoDataDir = Configuration.getRepositoriesDataDir(repoSource)
    }


    def 'Register Transaction should succeed'() {
        when: ''
        SimIdentifier simId = new SimIdentifier(SimUtils.defaultRepoName, 'RegisterTransactionTest')
        String endpoint = 'http://localhost:8080/tools/sim/user/123/reg/rb'
        SimHandle simHandle = SimUtils.recreate('reg', simId)
        simHandle.event.validationLevel = ValidationLevel.INFO
        simHandle.transactionType = new ActorTransactionTypeFactory().getTransactionType('rb')

        then:
        simHandle.open

        when:
        simHandle.event.inOut.reqHdr = header.trim()
        simHandle.event.inOut.reqBody = body.trim().bytes
        EndpointBuilder endpointBuilder = new EndpointBuilder()
        endpointBuilder.parse(endpoint)
//        def transRunner = new TransactionRunner(endpointBuilder, new SimSystemConfig().repoName)
        def transRunner = new TransactionRunner(simHandle)
        transRunner.acceptRequest()

        then: '''Sim should still be open'''
        simHandle.isOpen()

        when:
        SimUtils.close(simHandle)

        then:
        !simHandle.isOpen()

        when:
        def eventAccess = new SimEventAccess(simId, transRunner.simHandle.event)

        then:
        !transRunner.simHandle.event.hasFault()
        !eventAccess.faultFile().exists()

        then:
        eventAccess.assertionGroupFile('HttpHeaderValidator').exists()
        eventAccess.assertionGroupFile('SoapMessageValidator').exists()
        eventAccess.assertionGroupFile('SoapHeaderValidator').exists()
        eventAccess.assertionGroupFile('DSMetadataProcessing').exists()

    }
}
