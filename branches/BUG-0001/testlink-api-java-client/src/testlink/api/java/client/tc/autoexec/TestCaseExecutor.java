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


public interface TestCaseExecutor
{
	
	// Results
	public static final short RESULT_UNKNOWN = -1;
	public static final short RESULT_PASSED = 0;
	public static final short RESULT_FAILED = 1;
	public static final short RESULT_BLOCKED = 2;
	
	// States
	public static final short STATE_READY = 100;
	public static final short STATE_RUNNING = 101;
	public static final short STATE_BOMBED = 102;
	public static final short STATE_COMPLETED = 103;
	public static final short STATE_RESET = 104;
	
	/**
	 * Get the state of the execution
	 * 
	 * @return
	 */
	public short getExecutionState();
	
	/**
	 * Set the new state of the executor
	 *  
	 * @param newState
	 */
	public void setExecutionState(
		short newState);
	
	/**
	 * Return the result state of the test case execution.
	 * 
	 * @return The result of the test case. Implementers should set the initial status to UNKNOWN.
	 */
	public short getExecutionResult();
	
	/**
	 * Set the results of the test from an external source.
	 * 
	 * @param result
	 */
	public void setExecutionResult(
		short result);
	
	/**
	 * Information about the results of the execution.
	 * 
	 * @return Information about the results of the execution.
	 */
	public String getExecutionNotes();
	
	/**
	 * Set the execution notes
	 * 
	 * @param notes
	 */
	public void setExecutionNotes(String notes);
	
	/**
	 * Execute the test case that has been passed into the execute method.
	 * 
	 * @param testCase
	 * @throws TestLinkAPIException
	 */
	public void execute(
		TestCase testCase) throws TestLinkAPIException;
}
