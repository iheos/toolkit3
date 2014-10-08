package gov.nist.hit.ds.dsSims.validator
import gov.nist.hit.ds.eventLog.Event
import gov.nist.hit.ds.simSupport.validationEngine.ValComponentBase
import gov.nist.hit.ds.simSupport.validationEngine.annotation.Optional
import gov.nist.hit.ds.simSupport.validationEngine.annotation.Validation
/**
 * Created by bmajur on 7/30/14.
 */
class OptionalTestValidator extends ValComponentBase {
    OptionalTestValidator(Event event) {
        super(event)
    }

    @Optional
    @Validation(id="TV101", msg="A test Validation", ref="??")
    def val1() { fail('oops') }

}