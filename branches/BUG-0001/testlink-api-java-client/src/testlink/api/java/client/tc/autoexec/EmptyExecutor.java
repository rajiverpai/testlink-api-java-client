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
import testlink.api.java.client.TestLinkAPIException;

/**
 * This class can be used to write custom excutors by
 * overriding the execute method.
 * 
 * It can also be used for testing since it will return
 * an execution of failed if the default constructor is
 * used or the result provided to the defaultResult
 * constructor.
 * 
 * 
 * @author Daniel Padilla
 *
 */
public class EmptyExecutor implements TestCaseExecutor
{
	private short testState = STATE_READY;
	private short testResult = RESULT_UNKNOWN; 
	private short defaultResult = RESULT_FAILED;
	private String notes="Empty executor generated to report executor missing from test case.";
	
	/**
	 * Constructor in which the execution of the test
	 * always returns failed.
	 * 
	 */
	public EmptyExecutor() {
		this(RESULT_FAILED);
	}
	
	/**
	 * Constructor in which the execution of the test
	 * always returns failed.
	 * 
	 */
	public EmptyExecutor(short defaultResult) {
		this.defaultResult = defaultResult;
	}
	
	/**
	 * Get the state of the execution
	 * 
	 * @return
	 */
	public short getExecutionState()
	{
		return testState;
	}
	
	/**
	 * Set the new state of the executor
	 *  
	 * @param newState
	 */
	public void setExecutionState(
		short newState)
	{
		this.testState = newState;
	}
	
	/**
	 * Return FAILED result state of the test case execution.
	 * 
	 * @return The result of the test case. Implementers should set the initial status to UNKNOWN.
	 */
	public short getExecutionResult()
	{
		return testResult;
	}

	/**
	 * Set the results of the test from an external source.
	 * 
	 * @param result
	 */
	public void setExecutionResult(
		short result)
	{
			testResult = result;
	}
	
	/**
	 * Information about the results of the execution.
	 * 
	 * @return Information about the results of the execution.
	 */
	public String getExecutionNotes()
	{
		return notes;
	}
	
	
	/**
	 * Set the execution notes
	 * 
	 * @param notes
	 */
	public void setExecutionNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Execute the test case that has been passed into the execute method.
	 * 
	 * @param testCase
	 * @throws TestLinkAPIException
	 */
	public void execute(
		TestCase testCase) throws TestLinkAPIException
	{
		setExecutionState(STATE_RUNNING);
		setExecutionResult(defaultResult);
		setExecutionState(STATE_COMPLETED);
	}

}
