package gov.nist.hit.ds.ebIT

import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.actorTransaction.AsyncType
import gov.nist.hit.ds.actorTransaction.TlsType
import gov.nist.hit.ds.dsSims.eb.transactionSupport.EbSendRequest
import gov.nist.hit.ds.dsSims.eb.transactionSupport.EbSendRequestDAO
import gov.nist.hit.ds.simServlet.api.SimApi
import gov.nist.hit.ds.simServlet.restClient.CreateSimRest
import gov.nist.hit.ds.simServlet.servlet.SimServlet
import gov.nist.hit.ds.simSupport.client.SimId
import gov.nist.hit.ds.simSupport.client.SimIdentifier
import gov.nist.hit.ds.simSupport.serializer.SimulatorDAO
import gov.nist.hit.ds.simSupport.simulator.SimHandle
import gov.nist.hit.ds.simSupport.simulator.SimSystemConfig
import spock.lang.Specification

/**
 *
 * Test sending PnR to Document Recipient sim. TLS is not used.
 *
 *
 *
 * Created by bmajur on 2/3/15.
 */
class DocSrcSendIT extends Specification {
    def userName = 'Tester'
    def clientSimName = 'DocSrcSendTest'
    def clientSimId = new SimId(clientSimName)

    def metadata = '''
<xdsb:ProvideAndRegisterDocumentSetRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
    <lcm:SubmitObjectsRequest xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0">
        <rim:RegistryObjectList xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
        </rim:RegistryObjectList>
    </lcm:SubmitObjectsRequest>
</xdsb:ProvideAndRegisterDocumentSetRequest>
'''
    def requestXml = """
<sendRequest>
    <simReference>${userName}/${clientSimName}</simReference>
    <transactionName>prb</transactionName>
    <tls value="false"/>
    <metadata>${metadata}</metadata>
    <extraHeaders><foo/><bar/></extraHeaders>
    <document id="Document01" mimeType="text/plain">doc content</document>
</sendRequest>
"""
    def clientSimConfig(noTlsEndpoint, tlsEndpoint) {
        """
<actor type='docsrc'>
    <environment name="NA2015"/>
    <transaction name='prb'>
        <!-- example endpoint value -->
        <endpoint value='${noTlsEndpoint}' />
        <settings>
            <!-- Checks applied to response only -->
            <boolean name='schemaCheck' value='false' />
            <boolean name='modelCheck' value='false' />
            <boolean name='codingCheck' value='false' />
            <boolean name='soapCheck' value='false' />
            <!-- on response -->
            <text name='msgCallback' value='' />
        </settings>
        <webService value='prb' />
    </transaction>
    <transaction name='prb'>
        <endpoint value='${tlsEndpoint}' />
        <settings>
            <boolean name='schemaCheck' value='false' />
            <boolean name='modelCheck' value='false' />
            <boolean name='codingCheck' value='false' />
            <boolean name='soapCheck' value='false' />
            <text name='msgCallback' value='' />
        </settings>
        <webService value='prb_TLS' />
    </transaction>
</actor>"""
    }
        def serverSimConfig(noTlsEndpoint) {
            """
<actor type='docrec'>
    <environment name="NA2015"/>
    <transaction name='prb'>
        <!-- example endpoint value -->
        <endpoint value='${noTlsEndpoint}' />
        <settings>
            <!-- Checks applied to response only -->
            <boolean name='schemaCheck' value='false' />
            <boolean name='modelCheck' value='false' />
            <boolean name='codingCheck' value='false' />
            <boolean name='soapCheck' value='false' />
            <!-- on response -->
            <text name='msgCallback' value='' />
        </settings>
        <webService value='prb' />
    </transaction>
    <transaction name='prb'>
        <endpoint value='https://localhost:8080/xdstools3/sim/unknown/target23/docrec/prb' />
        <settings>
            <boolean name='schemaCheck' value='false' />
            <boolean name='modelCheck' value='false' />
            <boolean name='codingCheck' value='false' />
            <boolean name='soapCheck' value='false' />
            <text name='msgCallback' value='' />
        </settings>
        <webService value='prb_TLS' />
    </transaction>
</actor>"""
    }

    def serverSimId = new SimId('DocRec1')
    def simIdent
    ActorTransactionTypeFactory factory = new ActorTransactionTypeFactory()

    def setup() {
        new SimServlet().init()
        SimApi.delete(userName, clientSimId)  // necessary to make sure create actually creates new, default is to keep old if present
        simIdent = new SimIdentifier(userName, clientSimId)
    }

    def 'Send'() {
        when: '''Engine is launched in Tomcat inside IT environment. Use the REST interface to
request creation of a Document Recipient sim. example.com is a dummy URL that will get overwritten
by server when actually creating sim.'''
        def serverSimConfigXml = CreateSimRest.run(serverSimConfig('http://example.com'), 'localhost', '9080', SimSystemConfig.service, userName, serverSimId.id)
        def serverSimConfig = SimulatorDAO.toModel(serverSimConfigXml)
        println "Server config: ${serverSimConfigXml}"
        def serverEndpoint = serverSimConfig.getEndpoint(factory.getTransactionTypeIfAvailable('prb'),TlsType.NOTLS, AsyncType.SYNC)
        def serverTlsEndpoint = serverSimConfig.getEndpoint(factory.getTransactionTypeIfAvailable('prb'),TlsType.TLS, AsyncType.SYNC)
        println "Server endpoint is ${serverEndpoint.value}"

        then:  'server should return real endpoint'
        serverEndpoint
        serverEndpoint.value.startsWith('http')
        !serverEndpoint.value.contains('example.com')

        when: 'create client locally - this should accept a simconfig representing the knonwn server'
        SimHandle simHandle1 = SimApi.createClient('docsrc', userName, clientSimId, clientSimConfig(serverEndpoint, serverTlsEndpoint))

        and: ''

        and: 'send transaction'
        EbSendRequest request = EbSendRequestDAO.toModel(requestXml)
        SimHandle simHandle = SimApi.send(simIdent, request)

        then: 'verify no fault'
        simHandle.event.fault == null
    }
}