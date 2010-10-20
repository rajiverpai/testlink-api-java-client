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


import java.util.HashMap;
import java.util.Map;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.tc.autoexec.TestPlanPrepare;
import testlink.api.java.client.tc.autoexec.annotation.testplanprep.AnnotatedTestClassPreparator;

/**
 * Runs the ExecutionServer using the old class preparation
 * interface.
 * 
 * Th
 * 
 * @author Daniel Padilla
 *
 */
public class ExecutionRunner
{
	public static final String P_REPORT_RESULTS_AFTER_TEST = "-tlReportflag";
	public static final String P_DEFAULT_PROJECT_NAME = "-tlProject";
	public static final String P_DEV_KEY = "-tlDevKey";
	public static final String P_TESTLINK_URL = "-tlURL";
	public static final String P_TEST_CASE_CREATION_USER = "-tlUser";
	public static final String P_TESTLINK_TEST_CLASS = "-tlTestClass";
	public static final String P_TESTLINK_TEST_CLASS_TYPE = "-tlTestClassType";
	public static final String P_OPTIONAL_EXTERNAL_CONFIG_PATH = "-tlExternalPath";
	public static final String P_PORT = "-tlPort";
	public static final String CLASS_TYPE_ANNOTATION = "Test Class that uses TestLink annotation";
	public static final String CLASS_TYPE_PREP = "Test Plan Prepare Interface Implementer Class";

	/**
	 * The class has been changed to run with either a class
	 * that implements the Test Plan Prepare Interface or
	 * and class that uses TestLink annotation to perform
	 * tests.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(
		String[] args) throws Exception
	{
		try {
				
			// Get args
			Map argMap = getArgs(args);
			
			String strPort = (String) argMap.get(P_PORT);
			String devKey = (String) argMap.get(P_DEV_KEY);
			String url = (String) argMap.get(P_TESTLINK_URL) + "/lib/api/xmlrpc.php";
			String className = (String) argMap.get(P_TESTLINK_TEST_CLASS);
			String classType = (String) argMap.get(P_TESTLINK_TEST_CLASS_TYPE);
			String defaultTestCaseUser = (String) argMap.get(P_TEST_CASE_CREATION_USER);
			String externalDir = (String) argMap.get(P_OPTIONAL_EXTERNAL_CONFIG_PATH);
			
			System.out.println("Parameter " + P_PORT + "=" + strPort);
			System.out.println("Parameter " + P_DEV_KEY + "=" + devKey);
			System.out.println("Parameter " + P_TESTLINK_URL + "=" + url + "/lib/api/xmlrpc.php");
			System.out.println("Parameter " + P_TESTLINK_TEST_CLASS + "=" + className);
			System.out.println("Parameter " + P_TESTLINK_TEST_CLASS_TYPE + "=" + classType);
			System.out.println("Parameter " + P_TEST_CASE_CREATION_USER + "=" + defaultTestCaseUser);
			System.out.println("Parameter " + P_OPTIONAL_EXTERNAL_CONFIG_PATH + "=" + externalDir);
			
			int port = new Integer(strPort).intValue();
			TestLinkAPIClient apiClient = new TestLinkAPIClient(devKey, url);
			
			// The plan is to make sure both types of classes run the same way
			// all the logic in the annotated type class resides in the loader.
			TestPlanPrepare prep;
			if ( classType.equalsIgnoreCase(CLASS_TYPE_PREP) ) {
				prep = (TestPlanPrepare) Class.forName(className).newInstance();	
			} else {
				prep = new AnnotatedTestClassPreparator(className);
			}
					
			ExecutionServer server = new ExecutionServer(
					port,
					apiClient,
					prep,
					defaultTestCaseUser,
					externalDir);
			
			server.start();
				
		} catch ( Exception e ) {
			e.printStackTrace();
			ExecutionProtocol.debug("The launch failed due to an exception.");
		} 
	}
	
		
	public static Map getArgs(
		String[] args)
	{
		Map argMap = new HashMap();
		argMap.put(P_REPORT_RESULTS_AFTER_TEST, false);
		try {
			for ( int i = 0; i < args.length; i++ ) {
				if ( args[i].equals(P_DEFAULT_PROJECT_NAME) ) {
					i++;
					argMap.put(P_DEFAULT_PROJECT_NAME, args[i]);
				}
				if ( args[i].equals(P_TESTLINK_TEST_CLASS) ) {
					i++;
					argMap.put(P_TESTLINK_TEST_CLASS, args[i]);
				}
				if ( args[i].equals(P_TESTLINK_TEST_CLASS_TYPE) ) {
					i++;
					argMap.put(P_TESTLINK_TEST_CLASS_TYPE, args[i]);
				}
				if ( args[i].equals(P_DEV_KEY) ) {
					i++;
					argMap.put(P_DEV_KEY, args[i]);
				}
				if ( args[i].equals(P_TESTLINK_URL) ) {
					i++;
					argMap.put(P_TESTLINK_URL, args[i]);
				}
				if ( args[i].equals(P_REPORT_RESULTS_AFTER_TEST) ) {
					i++;
					argMap.put(P_REPORT_RESULTS_AFTER_TEST, flag(args[i]));
				}
				if ( args[i].equals(P_TEST_CASE_CREATION_USER) ) {
					i++;
					argMap.put(P_TEST_CASE_CREATION_USER, args[i]);
				}
				if ( args[i].equals(P_PORT) ) {
					i++;
					argMap.put(P_PORT, args[i]);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return argMap;
	}
	
	/*
	 * Private methods
	 */
		
	private static boolean flag(
		String arg)
	{
		if ( arg != null ) {
			if ( arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("Yes")
				|| arg.equalsIgnoreCase("Y") ) {
				return true;
			}
		}
		return false;
	}
}
