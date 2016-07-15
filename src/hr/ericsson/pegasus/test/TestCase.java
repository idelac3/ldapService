package hr.ericsson.pegasus.test;

public abstract class TestCase {

	static final public int PASS = 0;
	static final public int FAIL = 1;
	static final public int ERROR = 2;

	private int verdict;

	private String name;

	public String getVerdictText() {
		switch (verdict) {
		case PASS:
			return "PASS";
		case FAIL:
			return "FAIL";
		default:
			return "ERROR";
		}
	}

	public int getVerdict() {
		return verdict;
	}

	/**
	 * Set verdict of test case.
	 * @param verdict PASS, FAIL or ERROR
	 */
	public void setVerdict(int verdict) {
		this.verdict = verdict;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
