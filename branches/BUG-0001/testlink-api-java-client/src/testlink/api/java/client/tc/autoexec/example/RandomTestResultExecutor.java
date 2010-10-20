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
package testlink.api.java.client.tc.autoexec.example;


import java.util.Random;


import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.tc.autoexec.EmptyExecutor;


public class RandomTestResultExecutor extends EmptyExecutor
{
	
	/**
	 * Information about the results of the execution.
	 * 
	 * @return Information about the results of the execution.
	 */
	public String getExecutionNotes()
	{
		String note = "Random executor generated a test case result of ";
		if ( this.getExecutionResult() == RESULT_FAILED ) {
			note += " failed.";
		} else if ( this.getExecutionResult() == RESULT_PASSED ) {
			note += " passed.";
		} else {
			note += " blocked.";
		}
		return note;		
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
		try {
			Thread.sleep(1000);
		} catch ( Exception e ) {}
		Random rand = new Random();
		int v = rand.nextInt(10);
		if ( v < 3 ) {
			setExecutionResult(RESULT_FAILED);
		} else if ( v >= 3 && v < 9 ) {
			setExecutionResult(RESULT_PASSED);
		} else {
			setExecutionResult(RESULT_BLOCKED);
		}
		setExecutionState(STATE_COMPLETED);
	}

}
