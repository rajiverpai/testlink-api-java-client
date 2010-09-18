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


import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.EmptyExecutor;
import testlink.api.java.client.tc.autoexec.TestCaseExecutor;


public class RemoteClientExecutor extends EmptyExecutor
{
	private ExecutionProtocol ep = new ExecutionProtocol();
	private TestLinkTestPlan testPlan;
	private boolean isPreped = false;
	private String clientName = null;
	private RemoteClientConnection conn;

	/**
	 * Requires that the remote port be provided.
	 * 
	 * @param port
	 */
	public RemoteClientExecutor(
		int port,
		TestLinkTestPlan testPlan
		)
	{	
		this.testPlan = testPlan;
		this.clientName = this.toString();
		int retry = 0;
		while ( retry < 3 ) {
			try {
				retry++;
				Thread.sleep(500);
				conn = RemoteConnectionManager.getOrCreateConnection(port);
				sendMessage(ExecutionProtocol.STR_PING);
				ExecutionProtocol.debug("Opened connection to localhost port: " + port);
				break;
			} catch ( Exception e ) {
				retry++;
				if ( retry >= 3 ) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(500);
				} catch ( Exception se ) {}
				continue;
			}
		}
	}
	
	public  RemoteClientConnection getConnection() {
		return conn;
	}
	
	public void sendMessage(
		String message)
	{
		ExecutionProtocol.debug(
			"Send Message: " + clientName + ExecutionProtocol.STR_CLIENT_SEPARATOR + message);
		conn.sendMessage(clientName, message);	
	}
    
	/**
	 * Send the server a shutdown request from the client
	 */
	public void sendServerShutdownRequest()
	{
		sendMessage(ExecutionProtocol.STR_SHUTDOWN);	
	}
	
	/**
	 * Ask the server to prepare the test plan
	 */
	public void sendPlanPrepareRequest(TestCase[] cases)
	{
		isPreped = false;
		try {
			String request = ExecutionProtocol.STR_PLANPREP_REQUEST 
				+ ExecutionProtocol.STR_REQUEST_PROJECT_NAME
				+ testPlan.getProject().getProjectName()
				+ ExecutionProtocol.STR_REQUEST_PLAN_NAME + testPlan.getTestPlanName();
			sendMessage(request);
			
			// Wait for the response
			String fromServer = "";
			while ( true ) {
				
				fromServer = conn.receiveMessage(clientName);
				if ( fromServer == null ) {
					continue;
				}
				
				ep.processInput("client prep " + conn.getPort(), fromServer);
				
				if ( ep.shutdown() ) {
					ExecutionProtocol.debug("Shutdown request sent by server");
					break;
				}
				
				ExecutionProtocol.debug("Server: " + fromServer);
				if ( fromServer.contains(clientName)
					&& fromServer.contains(ExecutionProtocol.STR_PLANPREP_RESULT) ) {
					
					// Setup if it passed
					if ( fromServer.contains(ExecutionProtocol.STR_PLANPREP_PASSED) ) {
						isPreped = true;
					} else {
						isPreped = false;
					}
					
					// Check the server results for test case prep detail
					if ( isPreped && fromServer.contains(ExecutionProtocol.STR_PLANPREP_DETAIL) )
					{
						int idx = fromServer.indexOf(ExecutionProtocol.STR_PLANPREP_DETAIL) +
						ExecutionProtocol.STR_PLANPREP_DETAIL.length();
						String detail = fromServer.substring(idx);
						// Get the list of test cases with assigned executers
						// base on what is returned from the server.
						if ( detail.length() > 0 ) {
							String[] caseIDs = detail.split(",");
							for (int c=0; c < caseIDs.length; c++) {
								String caseID = caseIDs[c];
								if ( caseID != null && caseID.length() > 0) {
									// Match-up with local test case
									for (int rc=0; rc < cases.length; rc++) {
										TestCase tc = cases[rc];
										Integer tcid = tc.getTestCaseInternalID();
										try {
											Integer rtcid = new Integer(caseID);
											if ( tcid.equals(rtcid) ) {
												// Fill in a test case for UI display
												// this executor is not used.
												tc.setExecutor(new EmptyExecutor());
												break;
											}
										} catch (Exception e) {}
									}
								}
							}
						}
					}
					ExecutionProtocol.debug("Result from server: " + fromServer);
					break;
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * True if the plan was successfully prepared by the remote server.
	 * 
	 * @return
	 */
	public boolean isPreped()
	{
		return isPreped;
	}
	
	/**
	 * Make a request for remote test case execution.
	 */
	public void execute(
		TestCase tc)
	{
		String fromServer = null;
		try {
			
			// Check connection
			if ( !conn.isGood() ) {
				throw new Exception("The connection is no longer available.");
			}
			
			// Reset
			setExecutionResult(TestCaseExecutor.RESULT_UNKNOWN);
			setExecutionState(TestCaseExecutor.STATE_READY);
			setExecutionNotes("");
			
			// Build and send the request
			sendTestCaseRequest(tc);
			
			// Wait for the response
			ExecutionProtocol.debug("Waiting for server response"); 
			while ( true ) {
				
				fromServer = conn.receiveMessage(clientName);
				if ( fromServer == null ) {
					continue;
				}
				
				ep.processInput("client tc exec " + conn.getPort() + ", " + clientName,
					fromServer);
				if ( fromServer.contains(clientName) ) {
					ExecutionProtocol.debug("The data returned contains client name.");
				}
				if ( fromServer.contains(ExecutionProtocol.STR_TC_RESULT) ) {
					ExecutionProtocol.debug(
						"The message returned contains the result string.");
				} 
				if ( fromServer.contains(clientName)
					&& fromServer.contains(ExecutionProtocol.STR_TC_RESULT) ) {
					ExecutionProtocol.debug("Result from server: " + fromServer);
					break;
				} else {
					ExecutionProtocol.debug("Dispose from server: " + fromServer);
				}
			}
			
			// Process Result
			int beginIdx = fromServer.indexOf(ExecutionProtocol.STR_CLIENT_SEPARATOR)
				+ ExecutionProtocol.STR_CLIENT_SEPARATOR.length();
			if ( beginIdx > 0 ) {
				fromServer = fromServer.substring(beginIdx);
			}
			processTestCaseResult(fromServer);
			
		} catch ( Exception e ) {
			e.printStackTrace();
			ExecutionProtocol.debug("The message that failed. " + fromServer); 
			setExecutionResult(TestCaseExecutor.RESULT_FAILED);
			setExecutionState(TestCaseExecutor.STATE_BOMBED);
			setExecutionNotes(
				"Unable to complete the remote test request due to exception. "
					+ e.toString());
			ExecutionProtocol.debug(
				"Unable to complete the remote test request due to exception. "
					+ e.toString());
		} 
	}
	
	/**
	 * Send the request for remote test case execution
	 * 
	 * @param tc
	 */
	public void sendTestCaseRequest(
		TestCase tc) throws Exception
	{
		String request = ExecutionProtocol.STR_TC_REQUEST 
			+ ExecutionProtocol.STR_REQUEST_PROJECT_NAME + tc.getProjectName()
			+ ExecutionProtocol.STR_REQUEST_PLAN_NAME + testPlan.getTestPlanName()
			+ ExecutionProtocol.STR_REQUEST_TC_EXEC + tc.getTestCaseInternalID().toString();
		sendMessage(request);
	}
	
	/**
	 * Process the result back from the server
	 * 
	 * TODO: Validate that the result is for the correct test.
	 * Right now everything is serial so it should not be an
	 * issue but this could be a costly assumption.
	 * 
	 * @param result
	 */
	public void processTestCaseResult(
		String result)
	{
		
		// Set the return state
		if ( result.contains(ExecutionProtocol.STR_EXEC_BOMBED) ) {
			setExecutionState(STATE_BOMBED);
			setExecutionResult(RESULT_FAILED);
		} else if ( result.contains(ExecutionProtocol.STR_EXEC_PASSED) ) {
			setExecutionState(STATE_COMPLETED);
			setExecutionResult(RESULT_PASSED);
		} else if ( result.contains(ExecutionProtocol.STR_EXEC_BLOCKED) ) {
			setExecutionState(STATE_COMPLETED);
			setExecutionResult(RESULT_BLOCKED);
		} else {
			setExecutionState(STATE_COMPLETED);
			setExecutionResult(RESULT_FAILED);
		}
		
		// Notes are expected at the end of the string
	}
	
	/**
	 * Return the default client identifier plus project name.
	 */
	public final String toString()
	{
		String superToString = super.toString();
		return this.testPlan.getTestPlanName() + "@" + superToString;
	}
}
