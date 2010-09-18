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

import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkTestPlan;

/**
 * The event created during test case execution. This event is
 * passed to ExecuteTestCaseListeners.
 * 
 * @author Daniel Padilla
 *
 */
public class ExecuteTestCaseEvent {
	/**
	 * Indicates that the test case is about to be tested.
	 */
	public static final short TEST_CASE_START=0;
	
	/**
	 * Indicates that the test case has not had an executor registered.
	 */
	public static final short TEST_CASE_EXECUTOR_MISSING=1;
	
	/**
	 * Indicates that the test case result reporting to test link failed.
	 */
	public static final short TEST_CASE_REPORTING_FAILED=2;
	
	
	/**
	 * Indicates that the test case failed with an exception.
	 */
	public static final short TEST_CASE_BOMBED=3;
	
	/**
	 * Indicates that the test case has been completed.
	 */
	public static final short TEST_CASE_COMPLETED=4;
	
	/**
	 * Indicates test cases could not be individually tested for even larger errors.
	 */
	public static final short EXECUTION_START=5;
	
	/**
	 * Indicates test cases could not be individually tested for even larger errors.
	 */
	public static final short EXECUTION_FAILED=6;
	
	/**
	 * Indicates test cases could not be individually tested for even larger errors.
	 */
	public static final short EXECUTION_SUCCESS=7;
	
	/**
	 * Indicates that the test case has been reset.
	 */
	public static final short TEST_CASES_RESET=8;
	
	/**
	 * The event type
	 */
	short eventType;
	
	/**
	 * The test plan being executed
	 */
	TestLinkTestPlan testPlan;
	
	/**
	 * The test case being tested.
	 */
	TestCase testCase;
	
	/**
	 * The executor used for the test. This is not always the
	 * same as the registered executor. If not executor is registered
	 * then the test is failed using an EmptyExecutor. If the test is
	 * classified as manual then the manual test handler executor is
	 * used for executing the test.
	 * 
	 */
	TestCaseExecutor testExecutor;
	
	/**
	 * Return the exception that was trapped in case of failure
	 */
	Exception e;
	
	/**
	 * Return the remaining tests that have not executed
	 */
	int remainingTest=0;
	
	/**
	 * Return the total cases that will/were tested.
	 */
	int totalTest=0;
	
	/**
	 * 
	 */
	boolean hasTestFailed = false;
	
	/**
	 * Returns the event type for this event
	 * 
	 * @return
	 */
	public short getEventType() {
		return eventType;
	}
	
	/**
	 * Returns the test plan associated with the event.
	 * 
	 * @return
	 */
	public TestLinkTestPlan getTestPlan() {
		return testPlan;
	}
	
	/**
	 * Returns the test case associated with the event.
	 * 
	 * @return
	 */
	public TestCase getTestCase() {
		return testCase;
	}
	
	/**
	 * Returns the test case executor associated with the event.
	 * 
	 * @return
	 */
	public TestCaseExecutor getExecutor() {
		return testExecutor;
	}
	
	/**
	 * Get failure if applicable
	 */
	public Exception getFaulure() {
		return e;
	}
	
	/**
	 * Get the total number of test cases that will/were tested.
	 * 
	 * @return
	 */
	public int getTotalCases() {
		return this.totalTest;
	}
	
	/**
	 * Get the total number of cases that remain untested.
	 * 
	 * @return
	 */
	public int getTotalRemainingCases() {
		return this.remainingTest;
	}
	
	/**
	 * Truee if all test so far have passed
	 * 
	 * @return
	 */
	public boolean getExecutionPassStatus() {
		return !(hasTestFailed);
	}
}
