package gov.nist.hit.ds.eventLog.assertion;

public enum AssertionStatus {
	NONE, SUCCESS, WARNING, ERROR, FAULT;
	
	public AssertionStatus getMax(AssertionStatus as) {
		if (as.ordinal() > ordinal())
			return as;
		return this;
	}
	
	public boolean isError() {
		if (ordinal() >= ERROR.ordinal())
			return true;
		return false;
	}
	
}
