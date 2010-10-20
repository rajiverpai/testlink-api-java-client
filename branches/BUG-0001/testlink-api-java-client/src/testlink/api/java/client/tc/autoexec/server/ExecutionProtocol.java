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
package testlink.api.java.client.tc.autoexec.server;




public class ExecutionProtocol
{
	// debug variable for unit testing
	private static boolean inDebugMode=false;
	
	// States
	private static final Integer ALIVE = new Integer(0);
	private static final Integer TC_REQUEST = new Integer(1);
	private static final Integer PREP_REQUEST = new Integer(2);
	private static final Integer TC_RESULT = new Integer(3);
	private static final Integer PREP_RESULT = new Integer(4);
	private static final Integer SHUTDOWN = new Integer(5);
	private Integer state = ALIVE;
    
	// Outputs
	public static final String STR_SHUTDOWN = "Shutdown";
	public static final String STR_PING = "Ping";
	public static final String STR_CLIENT_SEPARATOR = "@:#";
	
	public static final String STR_PLANPREP_REQUEST = "PPRequest:";
	public static final String STR_PLANPREP_RESULT = "PPResult:";
	public static final String STR_PLANPREP_PASSED = "PP_Passed:";
	public static final String STR_PLANPREP_FAILED = "PP_Failed:";
	public static final String STR_PLANPREP_DETAIL = "[PP_DETAIL]";
	
	public static final String STR_TC_REQUEST = "TCRequest:";
	public static final String STR_TC_RESULT = "TCResult:";
	
	public static final String STR_REQUEST_PROJECT_NAME = "[project]";
	public static final String STR_REQUEST_PLAN_NAME = "[plan]";
	public static final String STR_REQUEST_TC_EXEC = "[tc_execute]";
	
	public static final String STR_EXEC_PASSED = "[tc_exec_passed]";
	public static final String STR_EXEC_FAILED = "[tc_exec_failed]";
	public static final String STR_EXEC_BLOCKED = "[tc_exec_blocked]";
	
	public static final String STR_EXEC_BOMBED = "[tc_exec_bombed]";
	public static final String STR_EXEC_COMPLETED = "[tc_exec_completed]";
	public static final String STR_EXEC_NOTES = "[tc_exec_notes]";
    
    
	public String processInput(
		String who,
		String theInput)
	{
		String theOutput = null;
		
		debug("Receive Message(" + who + "): " + theInput);

		if ( theInput != null ) {
			theOutput = theInput;
			if ( theInput.contains(STR_PLANPREP_REQUEST) ) {
				state = PREP_REQUEST;
			} else if ( theInput.contains(STR_PLANPREP_RESULT) ) {
				state = PREP_RESULT;
			} else if ( theInput.contains(STR_TC_RESULT) ) {
				state = TC_RESULT;
			} else if ( theInput.contains(STR_TC_REQUEST) ) {
				state = TC_REQUEST;
			} else if ( theInput.contains(STR_SHUTDOWN) ) {
				state = SHUTDOWN;
			} else {
				state = ALIVE;
			}
		} else {
			state = ALIVE;
			theOutput="";
		}
        
		// Send back a shutdown request 
		if ( shutdown() == true ) {
			theOutput = STR_SHUTDOWN;
		} 
       
		return theOutput;
	}
    
	/**
	 * True is request is a shutdown request
	 * 
	 * @return
	 */
	public boolean shutdown()
	{
		return (state.intValue() == SHUTDOWN.intValue());
	}
	
	/**
	 * True if it is a test case execution request
	 * 
	 * @return
	 */
	public boolean isTCRequest()
	{
		return (state.intValue() == TC_REQUEST.intValue());
	}
    
	/**
	 * True if it is a test plan prepare request
	 * 
	 * @return
	 */
	public boolean isPrepRequest()
	{
		return (state.intValue() == PREP_REQUEST.intValue());
	}
    
	/**
	 * Added due to the asynchronous nature it was hard to walk using
	 * the eclipse debugger.
	 * 
	 * @param msg
	 */
	public static void debug(String msg) {
		if ( inDebugMode ) {
			System.out.println(msg);
		}
	}
}
