package gov.nist.hit.ds.dsSims.validator
import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.eventLog.testSupport.EventAccess
import gov.nist.hit.ds.repository.api.RepositorySource
import gov.nist.hit.ds.repository.simple.Configuration
import gov.nist.hit.ds.simSupport.client.SimId
import gov.nist.hit.ds.simSupport.transaction.TransactionRunner
import gov.nist.hit.ds.simSupport.utilities.SimSupport
import gov.nist.hit.ds.simSupport.utilities.SimUtils
import spock.lang.Specification
/**
 * Created by bmajur on 7/30/14.
 */
class ValidatorOptionalTest extends Specification {
    def actorsTransactions = '''
<ActorsTransactions>
    <transaction displayName="Register" id="rb" code="rb" asyncCode="r.as"
       class="gov.nist.hit.ds.dsSims.reg.RegisterTransaction">
        <request action="urn:ihe:iti:2007:RegisterDocumentSet-b"/>
        <response action="urn:ihe:iti:2007:RegisterDocumentSet-bResponse"/>
        <params multiPart="false" soap="true"/>
    </transaction>
    <actor displayName="Document Registry" id="reg"
      class="">
        <transaction id="rb"/>
    </actor>
</ActorsTransactions>
'''

    File repoDataDir
    RepositorySource repoSource
    SimId simId
    def repoName = 'Sim'

    def setup() {
        SimSupport.initialize()
        new ActorTransactionTypeFactory().clear()
        new ActorTransactionTypeFactory().loadFromString(actorsTransactions)
        repoSource = Configuration.getRepositorySrc(RepositorySource.Access.RW_EXTERNAL)
        repoDataDir = Configuration.getRepositoriesDataDir(repoSource)
        simId = new SimId('123')
        SimUtils.create('reg', simId, repoName)
    }

    def 'Failing optional validation should not fail test'() {
        when:
        Closure closure = { simHandle ->
            new OptionalTestValidator(simHandle.event).asPeer().run()
        }
        def transRunner = new TransactionRunner('rb', simId, repoName, closure)
        def eventAccess = new EventAccess(simId.id, transRunner.simHandle.event)
        transRunner.runTest()

        then: 'Even though validation fails - it is optional so it does not fail the overall test'
        !transRunner.simHandle.event.hasErrors()
    }

}
