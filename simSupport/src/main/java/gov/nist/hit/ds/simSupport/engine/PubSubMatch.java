package gov.nist.hit.ds.simSupport.engine;

import java.lang.reflect.Method;

/**
 * This is a data object that holds the result of a Publisher/Subscriber match. What the
 * SimEngine does can be thought of as matching producers to consumers or publishers 
 * to subscribers.  
 * @author bmajur
 *
 */
public class PubSubMatch {
	Method pubMethod;
	Object pubObject;
	Method subMethod;
	Object subObject;
	
	public String toString() {
		return new StringBuffer()
//				.append("Subscriber <")
				.append("..")
//				.append(subObject.getClass().getName())
				.append("<#")
				.append(subMethod.getName())
//				.append("> with parm type <")
//				.append(subMethod.getParameterTypes()[0])
				.append("> ")
				.append("from <")
				.append(pubObject.getClass().getName())
				.append("#")
				.append(pubMethod.getName())
				.append(">")
				.toString();
	}
	
	public Method getPubMethod() {
		return pubMethod;
	}
	public PubSubMatch setPubMethod(Method pubMethod) {
		this.pubMethod = pubMethod;
		return this;
	}
	public Object getPubObject() {
		return pubObject;
	}
	public PubSubMatch setPubObject(Object pubObject) {
		this.pubObject = pubObject;
		return this;
	}
	public Method getSubMethod() {
		return subMethod;
	}
	public PubSubMatch setSubMethod(Method subMethod) {
		this.subMethod = subMethod;
		return this;
	}
	public Object getSubObject() {
		return subObject;
	}
	public PubSubMatch setSubObject(Object subObject) {
		this.subObject = subObject;
		return this;
	}

}
