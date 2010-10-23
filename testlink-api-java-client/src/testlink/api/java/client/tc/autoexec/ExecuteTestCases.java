/*
 * Daniel R Padilla
 *
 * Copyright (c) 2009, Daniel R Padilla
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package testlink.api.java.client.tc.autoexec;


import java.util.ArrayList;


import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.server.RemoteClientExecutor;


public class ExecuteTestCases extends Thread
{
	private TestLinkAPIClient apiClient = null;
	private TestLinkTestPlan testPlan;
	private boolean hasTestRun = false;
	private boolean hasTestFailed = false;
	private boolean reportResultsToTestLink = true;
	private TestCase[] cases;
	private String manualExecutorClass;
	private ArrayList<Object> listeners = new ArrayList<Object>();
	private String buildName = null;
	private int total = 0;
	private int remain = 0;
	private TestCase tc;
	RemoteClientExecutor rte=null;
	
	/**
	 * Executes the tests for test cases in a test plan.
	 * 
	 * @param apiClient             Optional
	 * @param plan					Required
	 * @param manualExecutorClass	Required
	 */
	public ExecuteTestCases(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan,
		String manualExecutorClass)
	{
		this(apiClient, plan, null, manualExecutorClass);
	}
	
	/**
	 * Executes the tests for test cases in a test plan.
	 * 
	 * @param apiClient             Optional
	 * @param plan					Required
	 * @param buildName				Required
	 * @param manualExecutorClass	Required
	 */
	public ExecuteTestCases(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan,
		String buildName,
		String manualExecutorClass)
	{
		this(apiClient, plan, plan.getTestCases(), buildName, manualExecutorClass);
	}
	
	/**
	 * Executes the cases against the test plan.
	 * 
	 * @param apiClient            	Optional
	 * @param plan					Required
	 * @param cases					Required
	 * @param buildName				Required
	 * @param manualExecutorClass	Required
	 */
	public ExecuteTestCases(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan,
		TestCase[] cases,
		String buildName,
		String manualExecutorClass)
	{
		this.apiClient = apiClient;
		this.testPlan = plan;
		this.cases = cases;
		this.buildName = buildName;
		this.manualExecutorClass = manualExecutorClass;
		this.total = cases.length;
		this.remain = this.total;
	}
	
	public void setRemoteTestMode(RemoteClientExecutor rte) {
		this.rte = rte;
	}
		
	/**
	 * True if the test has run to completion
	 */
	public boolean hasTestRun()
	{
		return hasTestRun;
	}
	
	/**
	 * True if the all test passed
	 */
	public boolean hasTestPassed()
	{
		return !(hasTestFailed());
	}
	
	/**
	 * True if the a single test did not pass
	 */
	public boolean hasTestFailed()
	{
		return hasTestFailed;
	}

	/**
	 * Added an ExecuteTestCaseListener
	 * 
	 * @param listener
	 */
	public void addListener(
		ExecuteTestCaseListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Execute the test results and report results or
	 * execute the results in the background.
	 * 
	 * @param reportResults
	 * @param inBackground
	 */
	public void executeTestCases(
		boolean reportResultsToTestLink,
		boolean runInBackground)
	{
		this.reportResultsToTestLink = reportResultsToTestLink;
		if ( runInBackground ) {
			this.start();
		} else {
			run();
		}
	}
	
	/**
	 * Execution of the test cases. This class is not
	 * recommended that it be started with this method
	 * until all the variables have been assigned.
	 * 
	 * @param reportResults
	 */
	public void run()
	{
		hasTestRun = false;
		try {
			executionStart();
						
			if ( this.testPlan == null || this.cases == null ) {
				throw new TestLinkAPIException(
					"All the variables have not been set so tests cannot be executed.");
			}
			hasTestFailed = false;
			
			// Reset the test cases to ready or reset
			for ( int i = 0; i < cases.length; i++ ) {
				TestCase tc = cases[i];
				TestCaseExecutor te = tc.getExecutor();
				if ( te != null ) {
					if ( te.getExecutionState() != TestCaseExecutor.STATE_READY ) {
						te.setExecutionState(TestCaseExecutor.STATE_RESET);
					} 
					te.setExecutionResult(TestCaseExecutor.RESULT_UNKNOWN);
				}
			}
			testCasesReset();
			
			// Execute the tests
			for ( int i = 0; i < cases.length; i++ ) {
			
				tc = cases[i];
				TestCaseExecutor te = tc.getExecutor();
				
				// If no executor is registered or manual test then create executor
				if ( te == null || tc.isManualExec() ) {
					if ( tc.isAutoExec() ) {
						te = new EmptyExecutor();
						testCaseWithoutExecutor(tc);
					} else {
						if ( manualExecutorClass != null ) {
							try {
								te = (TestCaseExecutor) Class.forName(manualExecutorClass).newInstance();
							} catch ( Exception e ) {
								testCaseWithoutExecutor(tc);
							}
						}
						
						// If manual exec is still null make it empty
						if ( te == null ) {
							te = new EmptyExecutor();
							testCaseWithoutExecutor(tc);
						}
					}				
				}
				
				testCaseStart(tc);
				
				try {
					// Manual test cannot be run remotely since they
					// require local input for the results. In addition,
					// the executor instantiated here must be registered
					// as the executor for the test case.
					if ( tc.isManualExec() ) {
						tc.setExecutor(te);
						ExecuteTestCase.execute(testPlan, tc, te);
					} else {
						ExecuteTestCase.execute(testPlan, tc, te, rte);	
					}
					if ( te.getExecutionResult() != TestCaseExecutor.RESULT_PASSED ) {
						hasTestFailed = true;
					}			
				} catch ( Exception e ) {
					if ( te.getExecutionState() != TestCaseExecutor.STATE_BOMBED ) {
						te.setExecutionState(TestCaseExecutor.STATE_BOMBED);
					} 
					hasTestFailed = true;
					testCaseBombed(tc, te, e);
				}

				if ( reportResultsToTestLink && apiClient != null ) {
					try {
						TestCaseUtils.reportTestResult(apiClient, testPlan, tc, te,
							buildName);
					} catch ( Exception e ) {
						hasTestFailed = true;
						testCaseReportResultsFailed(tc, te, e);
					}
				}
			
				testCaseCompleted(tc, te);
				this.remain = this.total - (i + 1);
			}
		} catch ( Exception e ) {
			hasTestFailed = true;
			executionFailed(e);
			hasTestRun = true;
			return;
		}
		hasTestRun = true;
		executionSuccess();
	}
	
	/*
	 * Private methods
	 */
	
	/*
	 * Called when all the each test is reset before execution
	 * 
	 * @param event
	 */
	private void testCasesReset()
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASES_RESET;
		event.testPlan = testPlan;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCasesReset(event);
		}
	}
	
	/*
	 * Called before the test case runs
	 * 
	 * @param event
	 */
	private void testCaseStart(
		TestCase tc)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASE_START;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCaseStart(event);
		}
	}
	
	/*
	 * Called if a test case is found without a registered executor
	 * 
	 * @param event
	 */
	private void testCaseWithoutExecutor(
		TestCase tc)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASE_EXECUTOR_MISSING;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCaseWithoutExecutor(event);
		}
	}
		
	/*
	 * Called if report results to listener is enabled.
	 * 
	 * @param event
	 */
	private void testCaseReportResultsFailed(
		TestCase tc,
		TestCaseExecutor te,
		Exception e)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASE_REPORTING_FAILED;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.testExecutor = te;
		event.e = e;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCaseReportResultsFailed(event);
		}
	}
	
	/*
	 * Called at any time the test cases is being processed
	 * and has not reached completion and there is an execption.
	 * 
	 * @param event
	 */
	private void testCaseBombed(
		TestCase tc,
		TestCaseExecutor te,
		Exception e)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASE_BOMBED;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.testExecutor = te;
		event.e = e;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCaseBombed(event);
		}
	}
	
	/*
	 * Called when the test case has completed and the results
	 * have been registered.
	 * 
	 * @param event
	 */
	private void testCaseCompleted(
		TestCase tc,
		TestCaseExecutor te)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.TEST_CASE_COMPLETED;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.testExecutor = te;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.testCaseCompleted(event);
		}
	}
	
	/*
	 * Called before execution
	 * 
	 * @param event
	 */
	private void executionStart()
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.EXECUTION_START;
		event.testPlan = testPlan;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.executionStart(event);
		}
	}
	
	/*
	 * Called if test complete without exception.
	 * 
	 * @param event
	 */
	private void executionSuccess()
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.EXECUTION_SUCCESS;
		event.testPlan = testPlan;
		event.totalTest = total;
		event.remainingTest = remain;
		event.testCase = tc;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.executionSuccess(event);
		}
	}
	
	/*
	 * Called when the test case has completed and the results
	 * have been registered.
	 * 
	 * @param event
	 */
	private void executionFailed(
		Exception e)
	{
		ExecuteTestCaseEvent event = new ExecuteTestCaseEvent();
		event.eventType = ExecuteTestCaseEvent.EXECUTION_FAILED;
		event.testPlan = testPlan;
		event.testCase = tc;
		event.e = e;
		event.totalTest = total;
		event.remainingTest = remain;
		event.hasTestFailed = hasTestFailed;
		for ( int i = 0; i < listeners.size(); i++ ) {
			ExecuteTestCaseListener listener = (ExecuteTestCaseListener) listeners.get(i);
			listener.executionFailed(event);
		}
	}
	
}
