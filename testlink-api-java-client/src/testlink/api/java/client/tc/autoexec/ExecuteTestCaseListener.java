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

/**
 * A listener interface that can be registered with the
 * ExecuteTestCase object.
 * 
 * @author Daniel Padilla
 *
 */
public interface ExecuteTestCaseListener {
	
	/**
	 * Called before execution
	 * 
	 * @param event
	 */
	public void executionStart(ExecuteTestCaseEvent event);

	/**
	 * Called if execution fails
	 * 
	 * @param event
	 */
	public void executionFailed(ExecuteTestCaseEvent event);
	
	/**
	 * Called if execution completes without an exception. This does not mean all test passed.
	 * 
	 * @param event
	 */
	public void executionSuccess(ExecuteTestCaseEvent event);
	
	/**
	 * Called after the test cases has been reset as part of 
	 * preparation to start execution.
	 * 
	 * @param event
	 */
	public void testCasesReset(ExecuteTestCaseEvent event);
	
	
	/**
	 * Called before the test case runs
	 * 
	 * @param event
	 */
	public void testCaseStart(ExecuteTestCaseEvent event);
	
	/**
	 * Called if a test case is found without a registered executor
	 * 
	 * @param event
	 */
	public void testCaseWithoutExecutor(ExecuteTestCaseEvent event);
	
	/**
	 * Called if report results to testlink api is enabled and it has
	 * and exception.
	 * 
	 * @param event
	 */
	public void testCaseReportResultsFailed(ExecuteTestCaseEvent event);
	
	/**
	 * Called at any time the test cases is being processed
	 * and has not reached completion and there is an execption.
	 * 
	 * @param event
	 */
	public void testCaseBombed(ExecuteTestCaseEvent event);
	
	/**
	 * Called when the test case has completed and the results
	 * have been registered.
	 * 
	 * @param event
	 */
	public void testCaseCompleted(ExecuteTestCaseEvent event);
}
