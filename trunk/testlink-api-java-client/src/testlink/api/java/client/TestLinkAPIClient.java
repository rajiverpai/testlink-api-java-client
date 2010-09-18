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
package testlink.api.java.client;


import java.net.URL;
import java.util.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


/**
 * The TestLinkAPIClient class is the principal interface
 * to the TestLink API. An instance of the class can be used 
 * to make calls to most of the TestLink API xml-rpc methods. 
 * The list of supported methods can be found at
 * http://code.google.com/p/dbfacade-testlink-rpc-api/wiki/TestLinkMethods.
 * <p>
 * In addition methods that make access to the API without the
 * need to know internal TestLink database identfiers have been
 * provided to shield the user from the need to access the TestLink
 * database in order to use the API.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkAPIClient implements TestLinkAPIConst
{

	/* Cache Variables 
	 * 
	 * The way the client has been written it accesses
	 * the API many times for the same information so
	 * the following is a way to cache the information
	 * if the user of the api chooses to cache.
	 */
	public boolean isConnected=false;
	public String connectErrorMsg="";
	boolean useCache = false;
	Map cacheList = new HashMap();
	
	/* API Initialization variables */
	
	/**
	 * The value in the TestLink database user record for API access. 
	 * For TestLink version 1.8.2 this value had to be set manually using a database update statement.
	 * See the TestLink API documentation for more information.
	 * <p>
	 * Example:
	 * <p>
	 * update users set script_key = 'AnyStringYouWant' where id=2
	 * <p>
	 * DEV_KEY = "AnyStringYouWant"
	 */
	public String DEV_KEY; 
	
	/**
	 * The TestLink API URL. See the TestLink API documentation for more information.
	 * <p>
	 * Example: http://localhost/testlink/lib/api/xmlrpc.php
	 */
	public String SERVER_URL; 
	
	/**
	 * Construct an instance with cache capabilities turned off.
	 * 
	 * @param devKey
	 * @param url
	 */
	public TestLinkAPIClient(
		String devKey,
		String url)
	{
		DEV_KEY = devKey;
		SERVER_URL = url;
		check();
	}
	
	/**
	 * Construct an instance and indicate if cache capabilities should be enabled or disabled. 
	 * <p>
	 * If the cache is enabled during instantiation then the instance will
	 * ignore all external changes made either manually or by other instances
	 * of this class to the TestLink database. Therefore, results will only 
	 * be up to date with the first query performed by the instance unless 
	 * changes are made by the instance. If a change is made to the TestLink
	 * database by the instance then the cache is reset.
	 * 
	 * The cache is very helpful when performing executions of test during which
	 * test plans are known to not be changing.
	 * 
	 * @param devKey
	 * @param url
	 * @param useCache
	 */
	public TestLinkAPIClient(
		String devKey,
		String url,
		boolean useCache)
	{
		DEV_KEY = devKey;
		SERVER_URL = url;
		this.useCache = useCache;
		check();
	}
	
	private void check() {
		try {
			about();
			isConnected = true;
		} catch (Exception e) {
			isConnected=false;
			connectErrorMsg = e.getMessage();
		}
	}
	
	/**
	 * Get information about the TestLink API version.
	 * 
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults about() throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		return execXmlRpcMethodWithCache(API_METHOD_ABOUT, params, null);
	}
	
	/**
	 * Allows user to test if the XML-RPC TestLink API is responding.
	 * 
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults ping() throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		return execXmlRpcMethodWithCache(API_METHOD_PING, params, null);
	}

	/**
	 * Report a test execution result for a test case by test
	 * project name and test plan name for a specific build.
	 * <p>
	 * If the build is left as null then the system is allowed 
	 * to guess on the latest build for the test case.
	 * 
	 * @param projectName				Required
	 * @param testPlanName				Required
	 * @param testCaseNameOrVisibleID	Required (Test Case Name or ID on web-page tree)
	 * @param buildName					Optional
	 * @param testResultStatus			Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults reportTestCaseResult(
		String projectName,
		String testPlanName,
		String testCaseNameOrVisibleID,
		String buildName,
		String execNotes,
		String testResultStatus) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found and the test case "
				+ testCaseNameOrVisibleID + " could not be accessed to report a test result.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, testPlanName);
		if ( planID == null ) {
			throw new TestLinkAPIException(
				"The plan " + testPlanName + " was not found and the test case "
				+ testCaseNameOrVisibleID + " could not be accessed to report a test result.");
		}
		Integer caseID = TestLinkAPIHelper.getTestCaseID(this, projectID,
			testCaseNameOrVisibleID);
		if ( caseID == null ) {
			throw new TestLinkAPIException(
				"The test case identifier " + caseID + " was not found and the test case "
				+ testCaseNameOrVisibleID
				+ " could not be accessed to report a test result to test plan "
				+ testPlanName + ".");
		}
		
		Integer buildID = null;
		if ( buildName != null ) {
			buildID = TestLinkAPIHelper.getBuildID(this, planID, buildName);
			if ( buildID == null ) {
				throw new TestLinkAPIException(
					"The build name " + buildName + " was not found in test plan "
					+ testPlanName + " and the test result for test case "
					+ testCaseNameOrVisibleID + " could not be recorded.");
			}
		}
		
		TestLinkAPIResults results = reportTestCaseResult(planID, caseID, buildID,
			execNotes, testResultStatus);
		return results;
	}
	
	/**
	 * Report a test execution result for a test case by test
	 * plan identifier and test case identifier for a specific 
	 * build identifier.
	 * 
	 * If the build identifier is not provided then the system
	 * is allowed to guess on the latest build for the test case.
	 * 
	 * @param testPlanID				Required
	 * @param testCaseID				Required
	 * @param buildID					Optional
	 * @param testResultStatus			Required 
	 */ 
	public TestLinkAPIResults reportTestCaseResult(
		Integer testPlanID,
		Integer testCaseID,
		Integer buildID,
		String execNotes,
		String  testResultStatus) throws TestLinkAPIException
	{ 
		Boolean guess = new Boolean(true);
		if ( buildID != null ) {
			guess = new Boolean(false);
		}
		TestLinkAPIResults results = reportTestCaseResult(testPlanID, testCaseID, buildID,
			null, guess, execNotes, testResultStatus);
		return results;
	}
	
	/**
	 * This method supports the TestLink API set of parameters
	 * that can be used to report a test case result. 
	 * 
	 * @param testPlanID			Required
	 * @param testCaseID			Required
	 * @param buildID				Optional
	 * @param bugID					Optional
	 * @param execNotes				Optional
	 * @param testResultStatus		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults reportTestCaseResult(
		Integer testPlanID,
		Integer testCaseID,
		Integer buildID,
		Integer bugID,
		Boolean guess,
		String  execNotes,
		String  testResultStatus) throws TestLinkAPIException
	{ 
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, testPlanID);
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_ID, testCaseID);
		setParam(params, OPTIONAL, API_PARAM_BUILD_ID, buildID);
		setParam(params, OPTIONAL, API_PARAM_BUG_ID, bugID);
		setParam(params, OPTIONAL, API_PARAM_GUESS, guess);
		setParam(params, OPTIONAL, API_PARAM_NOTES, execNotes);
		setParam(params, REQUIRED, API_PARAM_STATUS, testResultStatus);
		return execXmlRpcMethodWithCache(API_METHOD_REPORT_TEST_RESULT, params, null);
	}
	
	/**
	 * Create a new project in TestLink database.
	 * 
	 * @param projectName		Required
	 * @param testCasePrefix	Required
	 * @param description		Required
	 * @return The identifier for the created test project.
	 * @throws TestLinkAPIException
	 */
	public Integer createTestProject(
		String projectName,
		String testCasePrefix,
		String description) throws TestLinkAPIException
	{ 
		initCache();
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_NAME, projectName);
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_PREFIX, testCasePrefix);
		setParam(params, REQUIRED, API_PARAM_NOTES, description);
		TestLinkAPIResults results = executeXmlRpcMethod(API_METHOD_CREATE_PROJECT, params);
		return getCreatedRecordIdentifier(results, API_RESULT_IDENTIFIER);
	}
	
	/**
	 * Create top level test suite under a specific project name
	 * 
	 * @param projectName		Required
	 * @param suiteName			Required
	 * @param description		Required
	 * @throws TestLinkAPIException
	 */
	public Integer createTestSuite(
		String projectName,
		String suiteName,
		String description) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID != null ) {
			return createTestSuite(projectID, suiteName, description);
		} else {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found and the test suite "
				+ suiteName + " could not be created.");
		}
	}
	
	/**
	 * Create top level test suite under a specific project identifier
	 * 
	 * @param projectID		Required
	 * @param suiteName		Required
	 * @param description	Required
	 * @return The identifier for the created test suite.
	 * @throws TestLinkAPIException
	 */
	public Integer createTestSuite(
		Integer projectID,
		String suiteName,
		String description) throws TestLinkAPIException
	{
		return createTestSuite(projectID, suiteName, description, null, null, null);
	}
	
	/**
	 * 
	 * Create a test suite at any level using the project identifier and
	 * the parent suite identifier information.
	 * 
	 * @param projectID		Required
	 * @param suiteName		Required
	 * @param description	Required
	 * @param parentID		Optional
	 * @param order			Optional
	 * @param check			Optional
	 * @return The identifier for the created test suite.
	 * @throws TestLinkAPIException
	 */
	public Integer createTestSuite(
		Integer projectID, 
		String suiteName,
		String description,
		Integer parentID,
		Integer order,
		Boolean check) throws TestLinkAPIException
	{
		initCache();
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID.toString());
		setParam(params, REQUIRED, API_PARAM_TEST_SUITE_NAME, suiteName);
		setParam(params, REQUIRED, API_PARAM_DETAILS, description);
		setParam(params, OPTIONAL, API_PARAM_PARENT_ID, parentID);
		setParam(params, OPTIONAL, API_PARAM_ORDER, order);
		setParam(params, OPTIONAL, API_PARAM_CHECK_DUP_NAMES, check);
		TestLinkAPIResults results = executeXmlRpcMethod(API_METHOD_CREATE_SUITE, params);
		return getCreatedRecordIdentifier(results, API_RESULT_IDENTIFIER);
	}
	
	/**
	 * Create a test case by project name and suite name. 
	 * 
	 * @param projectName		Required
	 * @param suiteName			Required
	 * @param testCaseName		Required
	 * @param summary			Required
	 * @param steps				Required
	 * @param expectedResults	Required
	 * @param importance		Optional
	 * @return The internal identifier for the created test case.
	 */
	public Integer createTestCase(
		String authorLoginName,
		String projectName, 
		String suiteName,
		String testCaseName,
		String summary,
		String steps,
		String expectedResults,
		String importance) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		Integer suiteID = TestLinkAPIHelper.getSuiteID(this, projectName, suiteName);
		return createTestCase(authorLoginName, projectID, suiteID, testCaseName, summary,
			steps, expectedResults, null, null, null, null, null, importance);
	}
	
	/**
	 * Create a new test case using all the variables that are provided by
	 * the TestLink API. For more information on the parameters refer to the 
	 * TestLink API documentation.
	 * 
	 * @param projectID					Required
	 * @param suiteID					Required
	 * @param caseName					Required
	 * @param summary					Required
	 * @param steps						Required
	 * @param expectedResults			Required
	 * @param order						Optional
	 * @param internalID				Optional
	 * @param checkDuplicatedName		Optional
	 * @param actionOnDuplicatedName	Optional
	 * @param executionType				Optional
	 * @param importance				Optional
	 * @return The internal identifier created test case
	 * @throws TestLinkAPIException
	 */
	public Integer createTestCase(
		String authorLoginName,
		Integer projectID,
		Integer suiteID,
		String caseName,
		String summary,
		String steps,
		String expectedResults,
		Integer order,
		Integer internalID,
		Boolean checkDuplicatedName,                        
		String actionOnDuplicatedName,
		String executionType,
		String importance) throws TestLinkAPIException
	{
		initCache();
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_AUTHOR_LOGIN, authorLoginName);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID);
		setParam(params, REQUIRED, API_PARAM_TEST_SUITE_ID, suiteID);
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_NAME, caseName);
		setParam(params, REQUIRED, API_PARAM_SUMMARY, summary);
		setParam(params, REQUIRED, API_PARAM_STEPS, steps);
		setParam(params, REQUIRED, API_PARAM_EXPECTED_RESULTS, expectedResults);
		setParam(params, OPTIONAL, API_PARAM_ORDER, order);
		setParam(params, OPTIONAL, API_PARAM_INTERNAL_ID, internalID);
		setParam(params, OPTIONAL, API_PARAM_CHECK_DUP_NAMES, checkDuplicatedName);
		setParam(params, OPTIONAL, API_PARAM_ACTION_DUP_NAME, actionOnDuplicatedName);
		setParam(params, OPTIONAL, API_PARAM_EXEC_TYPE, executionType);
		setParam(params, OPTIONAL, API_PARAM_IMPORTANCE, importance);
		executeXmlRpcMethod(API_METHOD_CREATE_TEST_CASE, params);
		// The id returned in results is the id within
		// the project we want the actual test case id
		return TestLinkAPIHelper.getCaseIDByName(this, projectID, suiteID, caseName);
	}
	
	/*
	 * This method is not supported by the TestLink API. It is
	 * needed so that JUnit test could be run without human
	 * intervention but right now manual creation is the only
	 * way to get to all the other test.
	 * 
	 * 
	 * @param projectName
	 * @param planName
	 * @param description
	 * @throws TestLinkAPIException
	 *
	 public Integer createTestPlan(
	 String projectName,
	 String planName,
	 String description) throws TestLinkAPIException
	 {
	 Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
	 if ( projectID != null ) {
	 return createTestPlan(projectID, planName, description);
	 } else {
	 throw new TestLinkAPIException("The project " + projectName + " was not found and the test plan " + planName + " could not be created.");
	 }
	 }
	 */
	
	/*
	 * This method is not supported by the TestLink API. It is
	 * needed so that JUnit test could be run without human
	 * intervention but right now manual creation is the only
	 * way to get to all the other test.
	 * 
	 * @param projectName
	 * @param planName
	 * @param description
	 * @throws TestLinkAPIException
	 *
	 public Integer createTestPlan(
	 Integer projectID,
	 String planName,
	 String description) throws TestLinkAPIException
	 {
	 initCache();
	 Hashtable params = new Hashtable();				
	 setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
	 setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID.toString());
	 setParam(params, REQUIRED, "testplanname", planName); // not supported by TestLink
	 setParam(params, REQUIRED, API_PARAM_NOTES, description);
	 TestLinkAPIResults results = executeRpcMethod(API_METHOD_CREATE_TEST_PLAN, params);
	 return getCreatedRecordIdentifier(results, API_RESULT_IDENTIFIER);
	 }
	 */


	/**
	 * Create a new build under the provided project name and test plan.
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @param buildName			Required
	 * @param buildNotes		Required
	 * @return The identifier for the build that was created.
	 * @throws TestLinkAPIException
	 */
	public Integer createBuild(
		String projectName,
		String planName,
		String buildName,
		String buildNotes) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found and the build " + buildName
				+ " could not be created.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID != null ) {
			return createBuild(planID, buildName, buildNotes);
		} else {
			throw new TestLinkAPIException(
				"The plan " + planName + " was not found and the build " + buildName
				+ " could not be created.");
		}
	}
	
	/**
	 * Create a new build under the provided test plan identifier.
	 * 
	 * @param planID		Required
	 * @param buildName		Required
	 * @param buildNotes	Required
	 * @return	The identifier for the build that was created.
	 * @throws TestLinkAPIException
	 */
	public Integer createBuild(
		Integer planID,
		String buildName,
		String buildNotes) throws TestLinkAPIException
	{
		initCache();
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, planID);
		setParam(params, REQUIRED, API_PARAM_BUILD_NAME, buildName);
		setParam(params, REQUIRED, API_PARAM_BUILD_NOTES, buildNotes);
		TestLinkAPIResults results = executeXmlRpcMethod(API_METHOD_CREATE_BUILD, params);
		return getCreatedRecordIdentifier(results, API_RESULT_IDENTIFIER);
	}
	
	/**
	 * Appends that latest version of a test case to a test 
	 * plan with a default level of urgency. Can only handle
	 * test cases associated with first level suites.
	 * 
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @param testCaseName		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults addTestCaseToTestPlan(
		String projectName,
		String planName,
		String testCaseName) throws TestLinkAPIException
	{
		int maxNode = 0;
		TestLinkAPIResults cases = getCasesForTestPlan(projectName, planName);
		for ( int i = 0; i < cases.size(); i++ ) {
			Map data = cases.getData(i);
			Object node = data.get("execution_order");
			if ( node != null ) {
				try {
					Integer cn = new Integer(node.toString());
					if ( cn.intValue() > maxNode ) {
						maxNode = cn.intValue();
					}
				} catch ( Exception e ) {}
			}
		}
		maxNode++;
		TestLinkAPIResults results = addTestCaseToTestPlan(projectName, planName,
			testCaseName, new Integer(maxNode), null);	
		return results;
	}
	
	/**
	 * Appends that latest version of a test case to a test plan with
	 * a medium urgency. Can only handle test in first level suites.
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @param testCaseName		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults addTestCaseToTestPlan(
		String projectName,
		String planName,
		String testCaseName,
		Integer execOrder,
		String urgency) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found and the test case "
				+ testCaseName + " could not be appended.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID == null ) {
			throw new TestLinkAPIException(
				"The plan " + planName + " was not found and the test case " + testCaseName
				+ " could not be appended.");
		}
		Integer caseID = TestLinkAPIHelper.getCaseIDByName(this, projectID, testCaseName);
		if ( caseID == null ) {
			throw new TestLinkAPIException(
				"The test case " + testCaseName + " was not found and the test case"
				+ " could not be appended to test plan " + planName + ".");
		}
		Map caseInfo = TestLinkAPIHelper.getTestCaseInfo(this, projectID, caseID);
		if ( caseInfo == null ) {
			throw new TestLinkAPIException(
				"The test case identifier " + caseID + " was not found and the test case "
				+ testCaseName + " could not be appended to test plan " + planName + ".");
		}
		Map projectInfo = TestLinkAPIHelper.getProjectInfo(this, projectName);
		if ( projectInfo == null ) {
			throw new TestLinkAPIException(
				"The project information for " + projectName
				+ " was not found and the test case " + testCaseName
				+ " could not be appended to test plan " + planName + ".");
		}
		Object prefix = projectInfo.get("prefix");
		Object externalID = caseInfo.get(API_RESULT_TC_EXTERNAL_ID);
		String visibleTestCaseID = prefix.toString() + '-' + externalID.toString();
		Object version = caseInfo.get(API_PARAM_VERSION);
		TestLinkAPIResults results = addTestCaseToTestPlan(projectID, planID,
				caseID, visibleTestCaseID, new Integer(version.toString()), execOrder, urgency);	
		return results;
	}
	
	/**
	 * The method adds a test case from a project to the test plan.
	 * 
	 * @param projectID			Required
	 * @param planID			Required
	 * @param testCaseVisibleID	Required
	 * @param version			Required
	 * @param ExecOrder			Optional
	 * @param Urgency			Optional
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults addTestCaseToTestPlan(
		Integer projectID,
		Integer planID,
		Integer testCaseID,
		String testCaseVisibleID,
		Integer version,
		Integer execOrder,
		String urgency) throws TestLinkAPIException
	{
		initCache();
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, planID);
		
		if ( testCaseID != null ) {
			setParam(params, OPTIONAL, API_PARAM_TEST_CASE_ID, testCaseID);
		}
		
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_ID_EXTERNAL, testCaseVisibleID);
		setParam(params, REQUIRED, API_PARAM_VERSION, version);
		setParam(params, OPTIONAL, API_PARAM_URGENCY, urgency);
		setParam(params, OPTIONAL, API_PARAM_EXEC_ORDER, execOrder);
		TestLinkAPIResults results = executeXmlRpcMethod(API_METHOD_ADD_TEST_CASE_TO_PLAN,
			params);
		if ( results.size() < 1 ) {
			throw new TestLinkAPIException(
				"Could not add test case " + testCaseVisibleID + " to test plan id " + planID);
		}
		Map data = results.getData(0);
		if ( hasError(data) ) {
			throw new TestLinkAPIException(
				"Could not add test case " + testCaseVisibleID + " to test plan id " + planID
				+ ". Results Message: [" + data.toString() + "]");
		}
		return results;
	}
				
	/**
	 * Get a list of all the existing test projects for the instantiated TestLink URL.
	 * 
	 * @return The results from the TestLink API as a list of Map entries
	 */
	public TestLinkAPIResults getProjects() throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		return execXmlRpcMethodWithCache(API_METHOD_GET_PROJECTS, params, "projects");
	}
	
	/**
	 * Get a list of all the existing test plans for a project by name.
	 * 
	 * @param projectName	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getProjectTestPlans(
		String projectName) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		return getProjectTestPlans(projectID);
	}
	
	/**
	 * Get the test plans for a project identifier.
	 * 
	 * @param projectID		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getProjectTestPlans(
		Integer projectID) throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID);		
		return execXmlRpcMethodWithCache(API_METHOD_GET_PROJECT_TEST_PLANS, params,
			projectID);
	}
	
	/**
	 * Get the builds by project and plan name.
	 * 
	 * @param projectName	Required
	 * @param planName		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getBuildsForTestPlan(
		String projectName,
		String planName) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID == null ) {
			throw new TestLinkAPIException("The plan " + planName + " was not found.");
		}
		return getBuildsForTestPlan(planID);
	}
		
	/**
	 * Get a list of builds for a test plan id
	 * 
	 * @param planID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getBuildsForTestPlan(
		Integer planID) throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, planID);			
		return execXmlRpcMethodWithCache(API_METHOD_GET_BUILDS_FOR_PLAN, params, planID);
	}
	
	/**
	 * Get the latest build by project and plan name.
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getLatestBuildForTestPlan(
		String projectName,
		String planName) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID == null ) {
			throw new TestLinkAPIException("The plan " + planName + " was not found.");
		}
		return getLatestBuildForTestPlan(planID);
	}
		
	/**
	 * Get a latest build for a test plan id
	 * 
	 * @param planID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getLatestBuildForTestPlan(
		Integer planID) throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, planID);			
		return execXmlRpcMethodWithCache(API_METHOD_GET_LATEST_BUILD_FOR_PLAN, params,
			planID);
	}
	
	/**
	 * Get all the first level project test suites by project name
	 * 
	 * @param projectName	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getFirstLevelTestSuitesForTestProject(
		String projectName) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		return getFirstLevelTestSuitesForTestProject(projectID);
	}
	
	/**
	 * Get all the first level project test suites by project id
	 * 
	 * @param projectID		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getFirstLevelTestSuitesForTestProject(
		Integer projectID) throws TestLinkAPIException
	{
		Hashtable params = new Hashtable();	
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, projectID);
		return execXmlRpcMethodWithCache(API_METHOD_GET_FIRST_LEVEL_SUITES_FOR_PROJECT,
			params, projectID);
	}
	
	/**
	 * Get a test case id by name
	 * 
	 * @param testCaseName		Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getTestCaseIDByName(
		String testCaseName) throws TestLinkAPIException
	{ 
		return this.getTestCaseIDByName(testCaseName, null, null);
	}
	
	/**
	 * Get test case by name. As an option the results can be
	 * restricted to a project and and test suite name.
	 * 
	 * @param testCaseName 		Required
	 * @param testProjectName	Optional
	 * @param testSuiteName		Optional
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */ 
	public TestLinkAPIResults getTestCaseIDByName(
		String testCaseName,
		String testProjectName,
		String testSuiteName) throws TestLinkAPIException
	{ 
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_NAME, testCaseName);
		setParam(params, OPTIONAL, API_PARAM_TEST_PROJECT_NAME, testProjectName);
		setParam(params, OPTIONAL, API_PARAM_TEST_SUITE_NAME, testSuiteName);
		return execXmlRpcMethodWithCache(API_METHOD_GET_TEST_CASE_IDS_BY_NAME, params,
			null);
	}
	
	/**
	 * Get all the test cases for a project identifier and test suite identifier.
	 * 
	 * @param int testProjectID		Required
	 * @param int testSuiteID		Required
	 * @return The results from the TestLink API as a list of Map entries
	 */ 
	public TestLinkAPIResults getCasesForTestSuite(
		Integer testProjectID,
		Integer testSuiteID) throws TestLinkAPIException
	{ 
		String key = testProjectID.toString() + "-" + testSuiteID.toString();
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PROJECT_ID, testProjectID);
		setParam(params, REQUIRED, API_PARAM_TEST_SUITE_ID, testSuiteID);
		setParam(params, REQUIRED, API_PARAM_DEPTH_FLAG, new Boolean(true));
		setParam(params, REQUIRED, API_PARAM_DETAILS, "full");
		return execXmlRpcMethodWithCache(API_METHOD_GET_TEST_CASES_FOR_SUITE, params, key);
	}
	
	/**
	 * Get test suites for a test plan by project and plan name.
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getTestSuitesForTestPlan(
		String projectName,
		String planName
		) throws TestLinkAPIException
	{ 
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"Could not get project identifier for " + projectName);
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID == null ) {
			throw new TestLinkAPIException("Could not get plan identifier for " + planName);
		}
		return getTestSuitesForTestPlan(planID);
	}
	
	/**
	 * Get test suites for test test plan by plan identifier.
	 * 
	 * @param testPlanID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getTestSuitesForTestPlan(
		Integer testPlanID
		) throws TestLinkAPIException
	{ 
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, testPlanID);
		return execXmlRpcMethodWithCache(API_METHOD_GET_SUITES_FOR_PLAN, params,
			testPlanID);
	}
	
	/**
	 * Get the last execution result by project, plan and test case name/visible id.
	 * 
	 * @param projectName				Required
	 * @param testPlanName				Required
	 * @param testCaseNameOrVisibleID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getLastExecutionResult(
		String projectName,
		String testPlanName,
		String testCaseNameOrVisibleID) throws TestLinkAPIException
	{
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"The project " + projectName + " was not found.");
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, testPlanName);
		if ( planID == null ) {
			throw new TestLinkAPIException("The plan " + testPlanName + " was not found.");
		}
		Integer caseID = TestLinkAPIHelper.getTestCaseID(this, projectID,
			testCaseNameOrVisibleID);
		if ( caseID == null ) {
			throw new TestLinkAPIException(
				"The test case " + testCaseNameOrVisibleID
				+ " was not found and the test case"
				+ " could not be accessed to report a test result against plan "
				+ testPlanName + ".");
		}

		return getLastExecutionResult(planID, caseID);
	}
	
	/**
	 * Get the last execution result by plan identifier and test case internal identifier.
	 * 
	 * @param testPlanID	Required
	 * @param testCaseID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getLastExecutionResult(
		Integer testPlanID,
		Integer testCaseID) throws TestLinkAPIException
	{
		Hashtable params = new Hashtable(); 
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, testPlanID);
		setParam(params, REQUIRED, API_PARAM_TEST_CASE_ID, testCaseID);
		return execXmlRpcMethodWithCache(API_METHOD_LAST_EXECUTION_RESULT, params,
			testPlanID);
	}
	
	/**
	 * Gets all the test cases for a test plan by project name and plan name.
	 * 
	 * @param projectName		Required
	 * @param planName			Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getCasesForTestPlan(
		String projectName,
		String planName
		) throws TestLinkAPIException
	{ 
		Integer projectID = TestLinkAPIHelper.getProjectID(this, projectName);
		if ( projectID == null ) {
			throw new TestLinkAPIException(
				"Could not get project identifier for " + projectName);
		}
		Integer planID = TestLinkAPIHelper.getPlanID(this, projectID, planName);
		if ( planID == null ) {
			throw new TestLinkAPIException("Could not get plan identifier for " + planName);
		}
		return getCasesForTestPlan(planID);
	}
	
	/**
	 * Get all the test cases associated with a test plan identifier.
	 * 
	 * @param testPlanID	Required
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getCasesForTestPlan(
		Integer testPlanID
		) throws TestLinkAPIException
	{ 
		return getCasesForTestPlan(testPlanID, null, null, null, null, null, null, null);
	}
	
	/**
	 * Get all the test cases associated with a test plan.
	 * 
	 * @param testPlanID		Required
	 * @param testCaseID		Optional
	 * @param buildID			Optional
	 * @param keywordID			Optional
	 * @param executed			Optional
	 * @param assignedTo		Optional
	 * @param execStatus		Optional
	 * @param execType			Optional
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	public TestLinkAPIResults getCasesForTestPlan(
		Integer testPlanID,
		Integer testCaseID,
		Integer buildID,
		Integer keywordID,
		String executed,
		String assignedTo,
		String execStatus,
		String execType
		) throws TestLinkAPIException
	{ 
		
		// setup the key
		String key = "Plan:" + testPlanID.toString();
		
		key += "Case:";
		if ( testCaseID != null ) {
			key += testCaseID.toString();
		}
		
		key += "Build:";
		if ( buildID != null ) {
			key += buildID.toString();
		}
		
		key += "KW:";
		if ( keywordID != null ) {
			key += keywordID.toString();
		}
		
		key += "Exec:";
		if ( executed != null ) {
			key += executed;
		}
		
		key += "Assign:";
		if ( assignedTo != null ) {
			key += assignedTo;
		}
		
		key += "ES:";
		if ( execStatus != null ) {
			key += execStatus;
		}
		
		key += "ET:";
		if ( execType != null ) {
			key += execType;
		}
				
		// Setup hash parameters
		Hashtable params = new Hashtable();				
		setParam(params, REQUIRED, API_PARAM_DEV_KEY, DEV_KEY);
		setParam(params, REQUIRED, API_PARAM_TEST_PLAN_ID, testPlanID);
		setParam(params, OPTIONAL, API_PARAM_TEST_CASE_ID, testCaseID);
		setParam(params, OPTIONAL, API_PARAM_BUILD_ID, buildID);
		setParam(params, OPTIONAL, API_PARAM_KEY_WORD_ID, keywordID);
		setParam(params, OPTIONAL, API_PARAM_EXECUTED, executed);
		setParam(params, OPTIONAL, API_PARAM_ASSIGNED_TO, assignedTo);
		setParam(params, OPTIONAL, API_PARAM_EXECUTE_STATUS, execStatus);
		setParam(params, OPTIONAL, API_PARAM_EXEC_TYPE, execType);

		return execXmlRpcMethodWithCache(API_METHOD_GET_TEST_CASES_FOR_PLAN, params, key);
	}
	
	/* =========================================== */
	
	/* Private Methods                */
	
	/* =========================================== */
	
	/*
	 * Initialize the cache
	 */
	private void initCache()
	{
		cacheList = new HashMap();
	}
	
	/*
	 * Executes the XML-RPC method but checks for cached results first. Also, if
	 * an error exists in the results then the method throws an exception.
	 * 
	 * @param method		The xml-rpc method name
	 * @param params		The parameters for the method
	 * @param cacheKey		If caching is desired this must not be null
	 * @return The results from the TestLink API as a list of Map entries
	 * @throws TestLinkAPIException
	 */
	private TestLinkAPIResults execXmlRpcMethodWithCache(
		String method,
		Hashtable params,
		Object cacheKey) throws TestLinkAPIException
	{
		boolean isCached = true;
	
		if ( method == null ) {
			throw new TestLinkAPIException(
				"A method must be provided for caching a xml-rpc calls to work.");
		}
	
		// Check the hash
		Map cache = (Map) cacheList.get(method);
		if ( cache == null || useCache == false || cacheKey == null ) {
			cache = new HashMap();
			cacheList.put(method, cache);
			isCached = false;
		} else if ( !cache.containsKey(cacheKey) ) {
			isCached = false;
		}
	
		TestLinkAPIResults results = null;
		if ( isCached == false ) {
			results = executeXmlRpcMethod(method, params);
			if ( cacheKey != null ) {
				cache.put(cacheKey, results);
			}
		} else {
			results = (TestLinkAPIResults) cache.get(cacheKey);
		}
	
		if ( results == null ) {
			throw new TestLinkAPIException(
				"\nThe xml-rpc call to TestLink API method " + method
				+ " failed because the results were null.");
		}
		if ( hasError(results) ) {
			throw new TestLinkAPIException(
				"\nThe xml-rpc call to TestLink API method " + method + " failed.\n"
				+ results);
		}
		return results;
	}

	/*
	 * Private method used to make xml-rpc method calls
	 * to the TestLink api URL.
	 */
	
	private TestLinkAPIResults executeXmlRpcMethod(
		String method,
		Hashtable executionData) throws TestLinkAPIException
	{
		TestLinkAPIResults results = new TestLinkAPIResults();
		ArrayList params = new ArrayList();
		XmlRpcClient rpcClient = getRpcClient();
		int unknownResultTypeCnt = 0;
		
		try {
			params.add(executionData);
			Object[] rawResults = (Object[]) rpcClient.execute(method, params);			
			for ( int i = 0; i < rawResults.length; i++ ) {
				Object result = (Map) rawResults[i];
				if ( result instanceof Map ) {
					Map item = (Map) result;
					results.add(item);
				} else {
					unknownResultTypeCnt++;
					HashMap data = new HashMap();
					data.put(getUnknownKey(unknownResultTypeCnt), result);
					results.add(data);
				}
			}
		} catch ( Exception e ) {
			
			// Try without casting to an Object[] list since XML-RPC officially returns
			// an Object but TestLink API is known to return Object[] list and cast is
			// tried first and then if that does not work we go with Object.
			try {
				Object single = rpcClient.execute(method, params);	
				if ( single instanceof Map ) {
					results.add((Map) single);
				} else {
					if ( single != null ) {
						// The api returns a length 0 string for empty list
						if ( single.toString().length() > 0 ) {
							unknownResultTypeCnt++;
							HashMap data = new HashMap();
							data.put(getUnknownKey(unknownResultTypeCnt), single);
							results.add(data);
						}
					}
				}
			} catch ( Exception ee ) {
				String msg = "The call to the xml-rpc client failed." +
				              "\nURL: " + SERVER_URL
				              + "\nMethod: " + method + "\nParameters:";
				Iterator paramNames = executionData.keySet().iterator();
				while ( paramNames.hasNext() ) {
					Object paramName = paramNames.next();
					msg = msg + "\n    " + paramName + "='" + executionData.get(paramName) + "'";
				}
				ee.printStackTrace();
				throw new TestLinkAPIException(msg, e);
			}
		}
		return results;
	}
	
	private static String getUnknownKey(
		int cnt)
	{
		String key = new Integer(cnt).toString();
		return "RESULT_" + key;
	}
	
	/*
	 * Create the rpc client to an xml rpc request can be made.
	 */
	private XmlRpcClient getRpcClient() throws TestLinkAPIException
	{
		XmlRpcClient rpcClient = null;
		XmlRpcClientConfigImpl config;
		
		try {
			config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(SERVER_URL));
			rpcClient = new XmlRpcClient();
			rpcClient.setConfig(config);
		} catch ( Exception e ) {
			throw new TestLinkAPIException("Unable to create a XML-RPC client.", e);
		}
		
		return rpcClient;		
	}
	
	/*
	 * Assign the parameter
	 */
	private void setParam(
		Hashtable params, 
		boolean isRequired, 
		String paramName, 
		Object value) throws TestLinkAPIException
	{
		// If the value is required and it is null return exception
		if ( isRequired && value == null ) {
			throw new TestLinkAPIException(
				"The required parameter " + paramName + " was not provided by the caller.");
		}
		
		// If the value is not required and it is null then return without assignment
		if ( !isRequired && value == null ) {
			return;
		}
		
		// Set the parameter for the XML-RPC call
		try {
			Integer intTypeValue = new Integer(value.toString());
			params.put(paramName, intTypeValue);
		} catch ( Exception e ) {
			params.put(paramName, value.toString());
		}
	}
	
	private Integer getCreatedRecordIdentifier(
		TestLinkAPIResults results,
		String idKey) throws TestLinkAPIException
	{
		Integer newID = null;
		if ( results.size() == 1 ) {
			Object id = results.getValueByName(0, idKey);
			if ( id != null ) {
				newID = new Integer(id.toString());
			} else {
				Map msg = results.getData(0);
				String failMsg = "Create failed since the identifier for new record was not retrieved.\nAPI Returned Data: ["
					+ msg.toString() + "]";
				throw new TestLinkAPIException(failMsg);
			}
		}
		return newID;
	}
	
	/*
	 * There seems to be no standard message coming from the 
	 * TestLink API to indicate there is an error. So this is 
	 * a kludge trying to figure out if the message is an error
	 * since it contains both a message and code.
	 * 
	 * Maybe it is the XML-RPC.
	 * 
	 */
	
	private boolean hasError(
		TestLinkAPIResults results)
	{
		if ( results == null ) {
			return true;
		}
		if ( results.size() > 0 ) {
			return hasError(results.getData(0));
		} else {
			return false;
		}
	}
	
	private boolean hasError(
		Map data)
	{
		String message = (String) data.get(API_RESULT_MESSAGE);
		Object code = data.get(API_RESULT_CODE);
		if ( message != null && code != null ) {
			if ( !message.toLowerCase().contains("success") ) {
				return true;
			}
		}
		return false;
	}
}

