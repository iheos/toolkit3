package gov.nist.hit.ds.simSupport.test.sims;

import gov.nist.hit.ds.errorRecording.ErrorRecorder;
import gov.nist.hit.ds.simSupport.engine.SimComponent;
import gov.nist.hit.ds.simSupport.engine.v2compatibility.MessageValidatorEngine;
import gov.nist.hit.ds.simSupport.test.datatypes.Foo;

public class FooMaker implements SimComponent {
	Foo foo;
	
	public Foo getFoo() {
		return foo;
	}


	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


	@Override
	public void setErrorRecorder(ErrorRecorder er) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void run(MessageValidatorEngine mve) {
		foo = new Foo("Fubar");
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
