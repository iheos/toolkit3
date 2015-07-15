package gov.nist.hit.ds.dsSims.eb.metadata

import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.dsSims.eb.generator.RimGenerator
import gov.nist.hit.ds.dsSims.eb.reg.DSMetadataProcessing
import gov.nist.hit.ds.repository.api.RepositorySource
import gov.nist.hit.ds.repository.simple.Configuration
import gov.nist.hit.ds.simSupport.simulator.SimIdentifier
import gov.nist.hit.ds.simSupport.transaction.TransactionRunner
import gov.nist.hit.ds.simSupport.utilities.SimEventAccess
import gov.nist.hit.ds.simSupport.utilities.SimSupport
import gov.nist.hit.ds.simSupport.utilities.SimUtils
import gov.nist.hit.ds.utilities.xml.Parse
import org.apache.axiom.om.OMElement
import spock.lang.Specification
/**
 * Created by bmajur on 7/27/14.
 */
class MetadataParserTestEx extends Specification {
    def actorsTransactions = '''
<ActorsTransactions>
    <transaction name="Register" id="rb" code="rb" asyncCode="r.as">
       <implClass value="RegisterTransaction"/>
        <request action="urn:ihe:iti:2007:RegisterDocumentSet-b"/>
        <response action="urn:ihe:iti:2007:RegisterDocumentSet-bResponse"/>
        <params multiPart="false" soap="true"/>
    </transaction>
    <actor name="Document Registry" id="reg">
      <implClass value=""/>
        <transaction id="rb"/>
    </actor>
</ActorsTransactions>
'''

    File repoDataDir
    RepositorySource repoSource
    SimIdentifier simId
    def repoName = 'Sim'

    def setup() {
        SimSupport.initialize()
        new ActorTransactionTypeFactory().clear()
        new ActorTransactionTypeFactory().loadFromString(actorsTransactions)
        repoSource = Configuration.getRepositorySrc(RepositorySource.Access.RW_EXTERNAL)
        repoDataDir = Configuration.getRepositoriesDataDir(repoSource)
        simId = new SimIdentifier(SimUtils.defaultRepoName, '123')
        SimUtils.create('reg', simId)
    }

    def 'SubmissionSet passes metadata parser'() {
        def id = 'SS1'
        def submissionSpec =     [    [
                type: 'SubmissionSet',
                'id': id,
                attributes: []
        ] ]
        def ssXml = new RimGenerator().toRimXml(submissionSpec,'SubmitObjectsRequest')

        when:
        OMElement xml = Parse.parse_xml_string(ssXml)
        Closure closure = { simHandle ->
            new DSMetadataProcessing(simHandle, xml).run()
        }
        def transRunner = new TransactionRunner('rb', simId, closure)
        transRunner.simHandle.event.addArtifact('Metadata', ssXml)
        def eventAccess = new SimEventAccess(simId, transRunner.simHandle.event)
        transRunner.runTest()

        then:
        !transRunner.simHandle.event.hasErrors()
        eventAccess.assertionGroupFile('DSMetadataProcessing').exists()
    }
}
