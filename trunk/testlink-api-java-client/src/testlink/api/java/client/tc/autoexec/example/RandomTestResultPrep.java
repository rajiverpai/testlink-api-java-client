package testlink.api.java.client.tc.autoexec.example;



import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.TestCaseExecutor;
import testlink.api.java.client.tc.autoexec.TestPlanPrepare;


public class RandomTestResultPrep implements TestPlanPrepare
{

	/**
	 * Optionally made available by callers to the interface
	 * 
	 * @param directory
	 */
	public void setExternalPath(
		String path)
	{}
	
	/**
	 * Optionally made available by callers to the interface
	 * 
	 * @param user
	 */
	public void setTCUser(
		String user)
	{}
	
	/**
	 * This is just a dummy test executor creator. This is not
	 * a good example since you do not want to run the test
	 * at creation. 
	 * 
	 * @param plan
	 * @return A test plan which has had the executors set for each test case.
	 * 
	 */
	public TestLinkTestPlan adjust(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan) throws TestLinkAPIException
	{
		TestCase[] cases = plan.getTestCases();
		for ( int i = 0; i < cases.length; i++ ) {
			TestCase tc = cases[i];
			TestCaseExecutor te = new RandomTestResultExecutor();
			tc.setExecutor(te);
		}
		return plan;
	}
	
}
