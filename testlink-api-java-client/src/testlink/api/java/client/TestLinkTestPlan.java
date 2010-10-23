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


import java.util.Map;

import testlink.api.java.client.tc.autoexec.TestCaseExecutor;
import testlink.api.java.client.tc.autoexec.TestCaseRegistry;


/**
 * The class is used to manage the execution of test cases
 * associated with a test plan.
 * <p>
 * The test plan manager has two modes of opertaion. There is the online mode 
 * and the offline mode. In order to enter online mode a valid DEV KEY and 
 * TestLink API url must be provided. See the TestLink documentation for more 
 * information about the dev key.
 * <p>
 * If the api is deemed to be unreachable no error is thrown. The test plan
 * manager just defaults into offline mode. The offline or online state
 * of a TestPlanManager instance is found by using isOffline() method.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkTestPlan
{
	private boolean isAPIReachable = false;
	private boolean isReportResultsOn = false;
	private TestLinkTestProject testProject;
	private String testPlanName = null;
	private Integer testPlanID = null;
	private TestLinkAPIClient apiClient;
	private TestCaseRegistry testCaseRegistry = new TestCaseRegistry();
	private String testCaseClass = "testlink.api.java.client.tc.autoexec.ExecutableTestCase";
	private boolean testCasesInitialized = false;
	private boolean isActive=true;
	private String description="";
	
	/**
	 * Creates an offline version of the project manager for
	 * running test offline.
	 */
	public TestLinkTestPlan()
	{
		isAPIReachable = false;
		isReportResultsOn = false;
		this.testProject = TestLinkTestProject.getOffLineProject();
		testPlanName = "Offline plan";
	}
	
	/**
	 * Creates an offline version of the project manager for
	 * running test offline.
	 */
	public TestLinkTestPlan(
		String projectName,
		String planName)
	{
		isAPIReachable = false;
		isReportResultsOn = false;
		this.testProject = TestLinkTestProject.getOffLineProject(projectName);
		testPlanName = planName;
	}
	
	/**
	 * When the TestPlanManager is instantiated then it retrieves all the
	 * test cases that are defined as automated test cases in the test plan.
	 * <p>
	 * Reporting of test results is turned on if the API is reachable. If the
	 * API is not reachable the system defaults to offline mode.
	 * 
	 * @param projectName
	 * @param planName
	 */
	public TestLinkTestPlan(
		TestLinkAPIClient apiClient,
		String projectName,
		String planName)
	{
		this.apiClient = apiClient;
		initTestPlan(this.apiClient, projectName, planName);
	}
	
	/**
	 * When the TestPlanManager is instantiated then it retrieves all the
	 * test cases that are defined as automated test cases in the test plan.
	 * <p>
	 * Reporting of test results is turned on if the API is reachable. If the
	 * API is not reachable the system defaults to offline mode.
	 * 
	 * @param projectName
	 * @param planName
	 */
	public TestLinkTestPlan(
		String projectName,
		String planName,
		String devKey,
		String urlToAPI)
	{
		initTestPlan(devKey, urlToAPI, projectName, planName);
	}
	
	/**
	 * When the TestPlanManager is instantiated then it retrieves all the
	 * test cases that are defined as automated test cases in the test plan.
	 * <p>
	 * The cases are instantiated using the class that is passed to the constructor.
	 * <p>
	 * Reporting of test results is turned on if the API is reachable. If the
	 * API is not reachable the system defaults to offline mode.
	 *
	 * @param projectName
	 * @param planName
	 * @param testCaseImplementionClass
	 */
	public TestLinkTestPlan(
		String projectName,
		String planName,
		String testCaseClass,
		String devKey,
		String urlToAPI)
	{
		initTestPlan(devKey, urlToAPI, projectName, planName);
		this.testCaseClass = testCaseClass;
	}
	
	/**
	 * Return the project for thsi plan.
	 * @return
	 */
	public TestLinkTestProject getProject() {
		return testProject;
	}
	
	/**
	 * Return the name of the test plan for this test plan manager.
	 * 
	 * @return
	 */
	public String getTestPlanName()
	{
		return testPlanName;
	}
	
	/**
	 * Can the project manager access the API
	 * 
	 * @return
	 */
	public boolean isOffline()
	{
		return !(isAPIReachable);
	}
	
	/**
	 * True if reporting of test result to the TestLink API is turned on. 
	 * 
	 * @return
	 */
	public boolean isReportResultsOn()
	{
		return isReportResultsOn;
	}
	
	/**
	 * Turn on report of test result to the TestLink API. 
	 */
	public void trunOnResultReporting()
	{
		isReportResultsOn = true;
	}
	
	/**
	 * Turn off report of test result to the TestLink API. 
	 */
	public void trunOffResultReporting()
	{
		isReportResultsOn = false;
	}

	/**
	 * Adds a test case to the a project test suite if the test case does
	 * not exist and then it it adds it to the test plan.
	 * 
	 * If the test case exists it replaces the test case class obejct instance
	 * with the new instance.
	 * 
	 * @param testCase
	 * @throws TestLinkAPIException
	 */
	public void putTestCase(
		TestCase testCase,
		String loginUserName) throws TestLinkAPIException
	{
		if ( !isOffline() ) {
			if ( isAPIReachable ) {
				initTestCases(testProject.getProjectName(), testPlanName);
			}
			testCase.addToTestLink(apiClient, loginUserName);
			if ( !isCasePartOfPlan(testCase) ) {
				addTestCaseToPlan(testCase);
			}
		}
		testCaseRegistry.put(testCase);
	}
	
	/**
	 * 
	 * @param caseNameOrVisibleID
	 * @param executor
	 * @throws TestLinkAPIException
	 */
	public void setTestCaseExecutor(
		String caseNameOrVisibleID,
		TestCaseExecutor executor) 
	{
		if ( isAPIReachable ) {
			initTestCases(testProject.getProjectName(), testPlanName);
		}
		TestCase testCase = testCaseRegistry.get(caseNameOrVisibleID);
		testCase.setExecutor(executor);
	}
	
	/**
	 * 
	 * @param caseID
	 * @param executor
	 * @throws TestLinkAPIException
	 */
	public void setTestCaseExecutor(
		Integer caseID,
		TestCaseExecutor executor) 
	{
		if ( isAPIReachable ) {
			initTestCases(testProject.getProjectName(), testPlanName);
		}
		TestCase testCase = testCaseRegistry.get(caseID);
		testCase.setExecutor(executor);
	}
	
	public TestCase[] getTestCases()
	{
		if ( isAPIReachable ) {
			initTestCases(testProject.getProjectName(), testPlanName);
		}
		return testCaseRegistry.toArray();
	}
	
	/**
	 * True if the test plan is active
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * The test plan description/notes
	 * 
	 * @return
	 */
	public String getPlanDescription() {
		return description;
	}

	/*
	 * Private methods section
	 */
	
	/*
	 * Check to see if the TestLINK API can be accessed and the project exists
	 */
	private void initTestPlan(
		String devKey,
		String url,
		String projectName,
		String planName) 
	{
		apiClient = new TestLinkAPIClient(devKey, url);
		initTestPlan(apiClient, projectName, planName);
	}
	
	private void initTestPlan(
		TestLinkAPIClient apiClient,
		String projectName,
		String planName)
	{
		
		// First ping
		try {
			apiClient.ping();
			isAPIReachable = true;
		} catch ( Exception e ) {
			isAPIReachable = false;
		}
		
		// Check if the test project exist
		Integer projectID = null;
		if ( isAPIReachable ) {
			try {
				projectID = TestLinkAPIHelper.getProjectID(apiClient, projectName);
				if ( projectID == null ) {
					throw new Exception("Could not find the project.");
				} 
				Map<Object, Object> projectInfo = TestLinkAPIHelper.getProjectInfo(apiClient, projectID);
				testProject = new TestLinkTestProject(projectInfo);
			} catch ( Exception e ) {
				isAPIReachable = false;
				createDummyOfflineInfo(projectName, planName);
			}
		}
		
		// Check if the test plan exists
		if ( isAPIReachable ) {
			try {
				testPlanID = TestLinkAPIHelper.getPlanID(apiClient, projectID, planName);
				if ( testPlanID == null ) {
					throw new Exception("Could not find the project.");
				} 
			} catch ( Exception e ) {
				isAPIReachable = false;
				createDummyOfflineInfo(projectName, planName);
			}
		}
		
		if ( isAPIReachable ) {
			try {
				Map<Object, Object> planInfo = TestLinkAPIHelper.getPlanInfo(apiClient, projectID, planName);
				if ( planInfo != null ) {
					Object value = planInfo.get(TestLinkAPIConst.API_RESULT_ACTIVE);
					if ( value != null ) {
						Integer active = new Integer(value.toString());
						if ( active.intValue() > 0 ) {
							isActive = true;
						} else {
							isActive = false;
						}
					}
					value = planInfo.get(TestLinkAPIConst.API_RESULT_NOTES);
					if ( value != null ) {
						description = value.toString();
					}
				} 
			} catch ( Exception e ) {} // Info not all that critical
		}
		
		if ( isAPIReachable ) {
			isReportResultsOn = true;
		}
		
		testPlanName = planName;
	}
	
	/*
	 * Method is called by the constructor and it finds all the test cases in a 
	 * test plan and then instantiates the classes using the initExistingCase()
	 * method. Once the case is instantiated it is stored in a list.
	 */
	private void initTestCases(
		String projectName,
		String planName)
	{
		if ( testCasesInitialized == false && isAPIReachable ) {
			try {
				TestLinkAPIResults caseList = apiClient.getCasesForTestPlan(testPlanID);
				for ( int i = 0; i < caseList.size(); i++ ) {
					Map<Object, Object> caseInfo = caseList.getData(i);
					TestCase tc = getTestCaseInstance(caseInfo);
					testCaseRegistry.put(tc);
				}
			} catch ( Exception e ) {
				isAPIReachable = false;
				createDummyOfflineInfo(projectName, planName);
			}
			testCasesInitialized = true;
		}
	}
	
	/*
	 * Create an instance of the requested test case class
	 */
	private TestCase getTestCaseInstance(
		Map<Object, Object> testCaseInfo) throws Exception
	{
		boolean isCreated = false;
		String tcSuiteName = (String) testCaseInfo.get(
			TestLinkAPIConst.API_RESULT_TC_SUITE);
		TestCase tc = (TestCase) Class.forName(testCaseClass).newInstance();
		TestLinkAPIResults suites = apiClient.getTestSuitesForTestPlan(testPlanID);
		for ( int i = 0; i < suites.size(); i++ ) {
			Map<Object, Object> suiteInfo = suites.getData(i);
			String pSuiteName = (String) suiteInfo.get(TestLinkAPIConst.API_RESULT_NAME);
			if ( tcSuiteName.equals(pSuiteName) ) {
				TestLinkTestSuite testSuite = new TestLinkTestSuite(suiteInfo);
				tc.initExistingCase(testProject, testSuite, testCaseInfo);
				isCreated = true;
			}
		}
		
		if ( isCreated ) {
			return tc;
		} else {
			throw new TestLinkAPIException(
				"Unable to find test suite name in the test plan " + tcSuiteName);
		}
	}
	
	/*
	 * Make sure we are offline with an offline project
	 */
	private void createDummyOfflineInfo(
		String projectName,
		String planName)
	{
		isAPIReachable = false;
		this.testProject = TestLinkTestProject.getOffLineProject(projectName);
		testPlanName = planName;
	}
	
	/*
	 * Check to see if the test plan is part of the test case
	 */
	private boolean isCasePartOfPlan(
		TestCase tc)
	{
		try {
			TestLinkAPIResults results = apiClient.getCasesForTestPlan(testPlanID);
			for ( int i = 0; i < results.size(); i++ ) {
				Map<Object, Object> caseInfo = results.getData(i);
				Object id = caseInfo.get(TestLinkAPIConst.API_RESULT_IDENTIFIER);
				if ( id != null ) {
					Integer caseID = new Integer(id.toString());
					if ( caseID.equals(tc.getTestCaseInternalID()) ) {
						return true;
					}
				}
			}
			return false;
		} catch ( Exception e ) {
			return false;
		}
		
	}
	
	/*
	 * Add test case to test plan
	 */
	private void addTestCaseToPlan(
		TestCase tc)
	{
		try {
			apiClient.addTestCaseToTestPlan(testProject.getProjectID(), testPlanID, null,
				tc.getTestCaseVisibleID(), null, null, null);
		} catch ( Exception e ) {}
	}
}
