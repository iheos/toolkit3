package gov.nist.hit.ds.simSupport.validationEngine.annotation

import gov.nist.hit.ds.soapSupport.FaultCode

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by bmajur on 8/20/14.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Fault {
    FaultCode code()
}