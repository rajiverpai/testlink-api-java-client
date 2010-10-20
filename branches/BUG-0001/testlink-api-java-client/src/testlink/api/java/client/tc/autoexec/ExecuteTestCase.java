package testlink.api.java.client.tc.autoexec;


import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.server.RemoteClientExecutor;


public class ExecuteTestCase
{
	
	/**
	 * Process the request locally
	 * 
	 * @param tc
	 * @param te
	 * @param port
	 * @throws Exception
	 */
	public static void execute(
		TestLinkTestPlan tp,
		TestCase tc,
		TestCaseExecutor te) throws Exception
	{
		execute(tp, tc, te, null);
	}

	/**
	 * The method runs a test case. This method can be independently called
	 * and by passing a port it is considered a remote request. So it must not 
	 * allow dependencies of a non-remote nature to seep into the method.
	 * 
	 * @param tc
	 * @param te
	 * @param port
	 * @return
	 */
	public static void execute(
		TestLinkTestPlan tp,
		TestCase tc,
		TestCaseExecutor te,
		RemoteClientExecutor rte) throws Exception
	{
		try {
			if ( rte == null ) {
				te.execute(tc);
			} else {
				try {
					rte.execute(tc);
					te.setExecutionNotes(rte.getExecutionNotes());
					te.setExecutionResult(rte.getExecutionResult());
					te.setExecutionState(rte.getExecutionState());
				} catch ( Exception e ) {
					te.setExecutionResult(TestCaseExecutor.RESULT_FAILED);
					te.setExecutionState(TestCaseExecutor.STATE_BOMBED);
					throw e;
				}
			}
		} catch ( Exception e ) {
			te.setExecutionResult(TestCaseExecutor.RESULT_FAILED);
			te.setExecutionState(TestCaseExecutor.STATE_BOMBED);
			throw e;
		}
	}

}
