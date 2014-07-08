package gov.nist.hit.ds.simSupport.components;

import gov.nist.hit.ds.eventLog.Event;
import gov.nist.hit.ds.eventLog.assertion.AssertionGroup;
import gov.nist.hit.ds.simSupport.validationEngine.ValComponent;

public class BarMaker implements ValComponent {
	Bar bar;
	Event event;

	public Bar getBar() {
		return bar;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void setAssertionGroup(AssertionGroup er) {
	}

	@Override
	public void run() {
		bar = new Bar("Open");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setDescription(String description) {
	}

	@Override
	public void setEvent(Event event) {
		this.event = event;	
	}

	@Override
	public boolean showOutputInLogs() {
		return false;
	}
}
