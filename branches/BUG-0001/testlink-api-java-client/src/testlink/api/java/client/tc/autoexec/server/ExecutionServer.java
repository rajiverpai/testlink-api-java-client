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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.ExecuteTestCase;
import testlink.api.java.client.tc.autoexec.TestCaseExecutor;
import testlink.api.java.client.tc.autoexec.TestPlanPrepare;


/**
 * The server implements a protocol for listening to request
 * for test plan preparation and test case execution.
 * 
 * @author Daniel Padilla
 *
 */
public class ExecutionServer
{
	private int port;
	private TestLinkAPIClient apiClient;
	private Map<Object, Object> testPlans = new HashMap<Object, Object>();
	private TestPlanPrepare prep;
	private String defaultTestCaseUser;
	private String externalDir;
	private PrintWriter messageSend;
	private BufferedReader messageReceive;
	private boolean isConnected = false;
	private ArrayList<Object> clients = new ArrayList<Object>();

	/**
	 * The execution server expects a test plan that has
	 * already been prepared.
	 * 
	 * @param port
	 * @param apiClient
	 * @param testPlan
	 */
	public ExecutionServer(
		int port,
		TestLinkAPIClient apiClient,
		TestPlanPrepare prep,
		String defaultTestCaseUser,
		String externalDir)
	{
		this.port = port;
		this.apiClient = apiClient;
		this.prep = prep;
		this.defaultTestCaseUser = defaultTestCaseUser;
		this.externalDir = externalDir;
	}
	
	/**
	 * Start the remote test execution server.
	 */
	public void start()
	{
		try {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
			} catch ( IOException e ) {
				System.err.println("Could not listen on port: " + port);
				System.exit(1);
			}

			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch ( IOException e ) {
				System.err.println("Accept failed.");
				System.exit(1);
			}

			messageSend = new PrintWriter(clientSocket.getOutputStream(), true);
			messageReceive = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
			String inputLine,
				outputLine;
			ExecutionProtocol ep = new ExecutionProtocol();

			// Process the first output to check things out
			outputLine = ep.processInput("server", null);
			messageSend.println(outputLine);
			ExecutionProtocol.debug(
				"The server is alive on locolhost port: " + port + ", output: " + outputLine);

			isConnected = true;
			while ( (inputLine = messageReceive.readLine()) != null ) {
				
				// Process input and setup a state
				outputLine = ep.processInput("server", inputLine);
				
				// Check for proper format
				if ( !inputLine.contains(ExecutionProtocol.STR_CLIENT_SEPARATOR) ) {
					sendMessage("No client", ExecutionProtocol.STR_PING);
					continue;
				}
				
				// Get the client information
				String client = inputLine.substring(0,
					inputLine.indexOf(ExecutionProtocol.STR_CLIENT_SEPARATOR));
				if ( !clients.contains(client) ) {
					clients.add(client);
				}
				
				// Strip client information and get the message
				inputLine = inputLine.substring(
					inputLine.indexOf(ExecutionProtocol.STR_CLIENT_SEPARATOR)
						+ ExecutionProtocol.STR_CLIENT_SEPARATOR.length());

				// After answer is sent then process the requests
				if ( ep.shutdown() ) {
					
					// Inform the clients of the shutdown
					for ( int i = 0; i < clients.size(); i++ ) {
						client = (String) clients.get(i);
						sendMessage(client, ExecutionProtocol.STR_SHUTDOWN);
					}
					
					// Output debug
					ExecutionProtocol.debug(
						"The TestLink test execution server on port " + port
						+ " is shutting down.");
					break;
				} else if ( ep.isTCRequest() 
					&& inputLine.contains(ExecutionProtocol.STR_REQUEST_PROJECT_NAME)
					&& inputLine.contains(ExecutionProtocol.STR_REQUEST_PLAN_NAME)
					&& inputLine.contains(ExecutionProtocol.STR_REQUEST_TC_EXEC) ) {
					outputLine = processTestCaseExecRequest(client, inputLine);
					ExecutionProtocol.debug(outputLine);
					sendMessage(client, outputLine);
				} else if ( ep.isPrepRequest() 
					&& inputLine.contains(ExecutionProtocol.STR_REQUEST_PROJECT_NAME)
					&& inputLine.contains(ExecutionProtocol.STR_REQUEST_PLAN_NAME) ) {
					outputLine = processTestPlanPrepRequest(client, inputLine);
					ExecutionProtocol.debug(outputLine);
					sendMessage(client, outputLine);
				} else {
					sendMessage(client, ExecutionProtocol.STR_PING);
					continue;
				}
			}
			isConnected = false;
			messageSend.close();
			messageReceive.close();
			clientSocket.close();
			serverSocket.close();
		} catch ( Exception e ) {
			// Inform the clients of the shutdown
			for ( int i = 0; i < clients.size(); i++ ) {
				String client = (String) clients.get(i);
				sendMessage(client, ExecutionProtocol.STR_SHUTDOWN);
			}
			
			isConnected = false;
			sendMessage("All clients: " + e.toString(), ExecutionProtocol.STR_SHUTDOWN);
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the message 
	 * 
	 * @param message
	 */
	public void sendMessage(
		String clientName,
		String message)
	{
		ExecutionProtocol.debug(
			"Send Message: " + clientName + ExecutionProtocol.STR_CLIENT_SEPARATOR + message);
		messageSend.println(clientName + ExecutionProtocol.STR_CLIENT_SEPARATOR + message);	
	}
	
	/**
	 * True if the server is up and running and connected to the port.
	 * 
	 * @return
	 */
	public boolean isConnected()
	{
		return isConnected;
	}
	
	/**
	 * Process the test plan prepare request from the client.
	 * 
	 * @param request
	 * @return
	 */
	public String processTestPlanPrepRequest(
		String clientID,
		String request)
	{
		// Default result
		String result = ExecutionProtocol.STR_PLANPREP_RESULT
			+ ExecutionProtocol.STR_PLANPREP_FAILED;
			
		TestLinkTestPlan testPlan = null;
		try {
			testPlan = createTestPlan(clientID, request);
		} catch ( Exception e ) {
			// Do not use Request: it will confuse the protocol
			return result + " Unable to create the needed plan. {Req: "
				+ request.replaceAll(ExecutionProtocol.STR_PLANPREP_REQUEST, "")
				+ ", Exception: " + e.toString() + "}";
		}
		String passed = ExecutionProtocol.STR_PLANPREP_RESULT
			+ ExecutionProtocol.STR_PLANPREP_PASSED;
		
		if ( testPlan != null ) {
			passed += ExecutionProtocol.STR_PLANPREP_DETAIL;
			TestCase[] cases = testPlan.getTestCases();
			for ( int i = 0; i < cases.length; i++ ) {
				TestCase tc = cases[i];
				if ( tc != null ) {
					TestCaseExecutor te = tc.getExecutor();
					if ( te != null && tc.getTestCaseInternalID() != null ) {
						passed += "," + tc.getTestCaseInternalID().toString();
					}
				}
			}
		}
		
		return passed;
	}
	
	/**
	 * Parses the client request and performs the request.
	 * 
	 * TODO: Process the request
	 * 
	 * @param request
	 */
	public String processTestCaseExecRequest(
		String clientID,
		String request)
	{
		// Default result
		String result = ExecutionProtocol.STR_TC_RESULT
			+ ExecutionProtocol.STR_EXEC_BOMBED + ExecutionProtocol.STR_EXEC_FAILED
			+ ExecutionProtocol.STR_EXEC_NOTES + "Unable to process test case request.";
		
		try {
			createTestPlan(clientID, request);
		} catch ( Exception e ) {
			// Do not use Request: it will confuse the protocol
			return result + " Unable to create the needed plan. {Req: "
				+ request.replaceAll(ExecutionProtocol.STR_TC_REQUEST, "") + ", Exception: "
				+ e.toString() + "}";
		}
		
		// Extract test case id
		try {
			int idx = request.indexOf(ExecutionProtocol.STR_REQUEST_TC_EXEC)
				+ ExecutionProtocol.STR_REQUEST_TC_EXEC.length();
			String strID = request.substring(idx);
			ExecutionProtocol.debug("Test case id " + strID);
			Integer internalID = new Integer(strID);

			TestLinkTestPlan testPlan = (TestLinkTestPlan) testPlans.get(clientID);
			TestCase tc = null;
			TestCase[] cases = testPlan.getTestCases();
			for ( int i = 0; i < cases.length; i++ ) {
				TestCase tmp = cases[i];
				if ( tmp.getTestCaseInternalID().intValue() == internalID.intValue() ) {
					tc = tmp;
					break;
				}
			}
	
			try {
				if ( tc != null ) {
					TestCaseExecutor te = tc.getExecutor();
					try {
						ExecuteTestCase.execute(testPlan, tc, te);
						result = ExecutionProtocol.STR_TC_RESULT;
						if ( te.getExecutionState() == TestCaseExecutor.STATE_BOMBED ) {
							result += ExecutionProtocol.STR_EXEC_BOMBED
								+ ExecutionProtocol.STR_EXEC_FAILED
								+ ExecutionProtocol.STR_EXEC_NOTES + te.getExecutionNotes();					
						} else {
							result += ExecutionProtocol.STR_EXEC_COMPLETED;
							if ( te.getExecutionResult() == TestCaseExecutor.RESULT_PASSED ) {
								result += ExecutionProtocol.STR_EXEC_PASSED;
							} else if ( te.getExecutionResult()
								== TestCaseExecutor.RESULT_BLOCKED ) {
								result += ExecutionProtocol.STR_EXEC_BLOCKED;
							} else {
								result += ExecutionProtocol.STR_EXEC_FAILED;
							}
							result += ExecutionProtocol.STR_EXEC_NOTES
								+ te.getExecutionNotes();
						}
					} catch ( Exception e ) {
						result = ExecutionProtocol.STR_TC_RESULT
							+ ExecutionProtocol.STR_EXEC_BOMBED
							+ ExecutionProtocol.STR_EXEC_FAILED
							+ ExecutionProtocol.STR_EXEC_NOTES
							+ "The test cases execution failed with an exeception. [Exception: "
							+ e.toString() + "], [TC:" + internalID + ", TE:" + te + "]";
					}
				} else {
					result = result
						+ " Failed while processing the test case. [Test Case ID: "
						+ internalID + "].";
				}
			} catch ( Throwable tt ) {
				return result + " Failed while trying to find the test case. Exception: "
					+ tt.toString();
			}
			
		} catch ( Throwable t ) {
			// we must return a result
			return result + " Failed while trying to find the test case. Exception: "
				+ t.toString();
		}
		return result;
	}
	
	/**
	 * Create the test plan instance for this server if it does not yet exist.
	 * 
	 * @param request
	 * @throws Exception
	 */
	public TestLinkTestPlan createTestPlan(
		String clientID,
		String request) throws Exception
	{
		TestLinkTestPlan testPlan = (TestLinkTestPlan) testPlans.get(clientID);
		int projectIdx = request.indexOf(ExecutionProtocol.STR_REQUEST_PROJECT_NAME);
		int planIdx = request.indexOf(ExecutionProtocol.STR_REQUEST_PLAN_NAME);
		int tcIdx = request.indexOf(ExecutionProtocol.STR_REQUEST_TC_EXEC);
		
		int start = projectIdx + ExecutionProtocol.STR_REQUEST_PROJECT_NAME.length();
		String projectName = request.substring(start, planIdx);
		
		start = planIdx + ExecutionProtocol.STR_REQUEST_PLAN_NAME.length();
		String planName = null;
		if ( tcIdx > planIdx ) {
			planName = request.substring(start, tcIdx);
		} else {
			planName = request.substring(start);
		}
		
		String caseInternalID = "";
		if ( tcIdx > 0 ) {
			start = tcIdx + ExecutionProtocol.STR_REQUEST_TC_EXEC.length();
			caseInternalID = request.substring(start);
		}
		
		ExecutionProtocol.debug(projectName + ", " + planName + ", " + caseInternalID);
		
		if ( testPlan == null || !(testPlan.getTestPlanName().equals(planName)) ) {
			testPlan = new TestLinkTestPlan(apiClient, projectName, planName);	
			testPlans.put(clientID, testPlan);
			if ( prep != null ) {
				prep.setExternalPath(externalDir);
				prep.setTCUser(defaultTestCaseUser);
				prep.adjust(apiClient, testPlan);
			}
		}
		
		return testPlan;
	}
	
	/**
	 * Returns an open port on local host or throws an exception
	 * 
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static int demandPort() throws TestLinkAPIException
	{
		int port = findFreePort();
		if ( port == -1 ) {
			throw new TestLinkAPIException(
				"Could not find a free port for the connection.");
		}
		return port;
	}
    
	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 *
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 * @since 3.0
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch ( IOException e ) {} finally {
			if ( socket != null ) {
				try {
					socket.close();
				} catch ( IOException e ) {}
			}
		}
		return -1;
	}
	
	/**
	 * Open a client connection to the server port and send a shutdown request.
	 * 
	 * @param port
	 */
	public static void sendServerShutdownRequest(
		int port) throws Exception
	{
		PrintWriter cOut = null;
		Socket cSocket = null;
		try {
			cSocket = new Socket("localhost", port);
			cOut = new PrintWriter(cSocket.getOutputStream(), true);
		} catch ( UnknownHostException e ) {
			System.err.println("Don't know about host: localhost.");
			throw e;
		} catch ( IOException e ) {
			System.err.println("Couldn't get I/O for the connection to: localhost.");
			throw e;
		} catch ( Exception e ) {
			System.err.println("Couldn't get connection established.");
			throw e;
		}
		cOut.println(ExecutionProtocol.STR_SHUTDOWN);	
		cOut.close();
		cSocket.close();
	}

}

