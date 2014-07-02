package gov.nist.hit.ds.simSupport.simGeneration
import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.actorTransaction.AsyncType
import gov.nist.hit.ds.actorTransaction.TlsType
import gov.nist.hit.ds.simSupport.client.ActorSimConfig
import gov.nist.hit.ds.simSupport.client.SimId
import gov.nist.hit.ds.simSupport.simulator.SimConfigFactory
import spock.lang.Specification
/**
 * Created by bmajur on 6/5/14.
 */
class CreateSimTest extends Specification {
    static String config = '''
<ActorsTransactions>
    <transaction displayName="Stored Query" id="sq" code="sq" asyncCode="sq.as">
        <request action="urn:ihe:iti:2007:RegistryStoredQuery"/>
        <response action="urn:ihe:iti:2007:RegistryStoredQueryResponse"/>
    </transaction>
    <transaction displayName="Register" id="rb" code="rb" asyncCode="r.as">
        <request action="urn:ihe:iti:2007:RegisterDocumentSet-b"/>
        <response action="urn:ihe:iti:2007:RegisterDocumentSet-bResponse"/>
    </transaction>
    <transaction displayName="Provide and Register" id="prb" code="prb" asyncCode="pr.as">
        <request action="urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b"/>
        <response action="urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse"/>
    </transaction>
    <transaction displayName="Update" id="update" code="update" asyncCode="update.as">
        <request action="urn:ihe:iti:2010:UpdateDocumentSet"/>
        <response action="urn:ihe:iti:2010:UpdateDocumentSetResponse"/>
    </transaction>
    <actor displayName="Document Registry" id="reg">
        <simFactoryClass class="gov.nist.hit.ds.registrySim.factories.DocumentRegistryActorFactory"/>
        <transaction id="rb"/>
        <transaction id="sq"/>
        <transaction id="update"/>
    </actor>
    <actor displayName="Document Repository" id="rep">
        <simFactoryClass class="gov.nist.hit.ds.registrySim.factory.DocumentRepositoryActorFactory"/>
        <transaction id="prb"/>
        <property name="repositoryUniqueId" value="1.2.3.4"/>
    </actor>
</ActorsTransactions>
'''
    void setup() {
        def factory = new ActorTransactionTypeFactory()
        factory.clear()
        factory.load(config)
    }

    def 'SimConfig should contain transaction configs'() {
        given:
        def server = 'localhost'
        def port = '8080'
        def base = '/xdstools3/sim'
        def simId = new SimId('123')
        def actorTypeName = 'reg'

        when:
        SimConfigFactory factory = new SimConfigFactory()
        ActorSimConfig actorSimConfig = factory.buildSim(server, port, base, simId, actorTypeName)

        then:
        actorSimConfig
        actorSimConfig.getActorType().getShortName() == actorTypeName
    }

    def 'SimConfig lookup should return correct endpoint'() {
        given:
        def server = 'localhost'
        def port = '8080'
        def base = '/xdstools3/sim'
        def simId = new SimId('123')
        def actorTypeName = 'reg'

        when:
        SimConfigFactory factory = new SimConfigFactory()
        ActorSimConfig actorSimConfig = factory.buildSim(server, port, base, simId, actorTypeName)
        String endpoint = actorSimConfig.getEndpoint(
                new ActorTransactionTypeFactory().getTransactionType("rb"),
                TlsType.TLS,
                AsyncType.SYNC)

        then:
        endpoint == 'https://localhost:8080/xdstools3/sim/123/reg/rb'
    }
}