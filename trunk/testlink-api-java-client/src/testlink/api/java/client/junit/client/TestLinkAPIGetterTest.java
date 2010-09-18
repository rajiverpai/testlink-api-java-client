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
package testlink.api.java.client.junit.client;


import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkAPIHelper;
import testlink.api.java.client.TestLinkAPIResults;
import testlink.api.java.client.junit.constants.TestConst;


/**
 * 
 * Test used to verify the TestLink API Java implementation.
 * 
 * If you are going to use these tests it is recommended that 
 * you do it against a test installation of the TestLink system
 * especially the database since these test will create and
 * delete entries in the database.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkAPIGetterTest implements TestLinkAPIConst,
	TestConst
{	
	private static VersionListTestRunner runner;
		
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		runner =  new VersionListTestRunner();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{}

	/**
	 * Test method about
	 */
	@Test
	public void testAbout()
	{
		String method = "testAbout()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.about();
				if ( results == null ) {
					throw new Exception("Unable to run about method. Version=" + version);
				}
				printResults("testAbout (" + version + ") ", results);
			}
	};

	// Run test and record failure if it happens
	RunExceptionResults results = runner.runTest(method, test);
	if ( results == null || results.containsFailure()) {
		fail("Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
	}
	}
	
	/**
	 * Test method pint
	 */
	@Test
	public void testPing()
	{
		String method = "testPing()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.ping();
				if ( results == null ) {
					throw new Exception("Unable to run ping method. Version=" + version);
				}
				printResults("testPing (Version=" + version + ") ", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getProjects
	 */
	@Test
	public void testGetProjects()
	{
		String method = "testGetProjects()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
		
				TestLinkAPIResults results = api.getProjects();
				if ( results == null ) {
					throw new Exception("Unable to run getProject() method.");
				}
				printResults("testGetProjects (Version=" + version + "): ", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getFirstLevelTestSuitesForTestProject
	 */
	@Test
	public void testGetFirstLevelTestSuitesForTestProject()
	{
		String method = "testGetFirstLevelTestSuitesForTestProject()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getFirstLevelTestSuitesForTestProject(
					JUNIT_STATIC_PROJECT);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getFirstLevelTestSuitesForTestProject() method. Version="
							+ version);
				}
				printResults(
					"testGetFirstLevelTestSuitesForTestProject (Version=" + version + "): ",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getProjectTestPlans
	 */
	@Test
	public void testGetProjectTestPlans()
	{
		String method = "testGetProjectTestPlans()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
		
				TestLinkAPIResults results = api.getProjectTestPlans(JUNIT_STATIC_PROJECT);
				if ( results == null ) {
					throw new Exception("Unable to run getProjectTestPlans() method.");
				}
				printResults("testGetProjectTestPlans (Version=" + version + "): ",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getBuildsForTestPlan
	 */
	@Test
	public void testGetBuildsForTestPlan()
	{
		String method = "testGetBuildsForTestPlan()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
		
				TestLinkAPIResults results = api.getBuildsForTestPlan(JUNIT_STATIC_PROJECT,
					JUNIT_STATIC_TEST_PLAN);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getBuildsForTestPlan() method. Version=" + version);
				}
				printResults("testGetBuildsForTestPlan (Version=" + version + "): ",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLatestBuildForTestPlan
	 */
	@Test
	public void testGetLatestBuildForTestPlan()
	{
		String method = "testGetLatestBuildForTestPlan()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getLatestBuildForTestPlan(
					JUNIT_STATIC_PROJECT, JUNIT_STATIC_TEST_PLAN);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getLatestBuildForTestPlan() method.");
				}
				printResults("testGetLatestBuildForTestPlans (Version=" + version + "): ",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getTestSuitesForTestPlan
	 */
	@Test
	public void testGetTestSuitesForTestPlan()
	{
		String method = "testGetTestSuitesForTestPlan()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getTestSuitesForTestPlan(
					JUNIT_STATIC_PROJECT, JUNIT_STATIC_TEST_PLAN);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getTestSuitesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestSuitesForTestPlan", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getTestCaseIDByName
	 */
	@Test
	public void testGetTestCaseIDByName()
	{
		String method = "testGetTestCaseIDByName()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getTestCaseIDByName(JUNIT_STATIC_CASE);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getTestCaseIDByName() method. Version=" + version);
				}
				printResults("testGetTestCaseIDByName (Version=" + version + "): ",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLastExecutionResult
	 */
	@Test
	public void testGetLastExecutionResult()
	{
		String method = "testGetLastExecutionResult()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getLastExecutionResult(JUNIT_STATIC_PROJECT,
					JUNIT_STATIC_TEST_PLAN, JUNIT_STATIC_CASE);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getLastExecutionResult() method. Version=" + version);
				}
				printResults("testGetLastExecutionResult (Version=" + version + "):",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getCasesForTestPlan(projectName, planName)
	 */
	@Test
	public void testGetTestCasesByPlanName()
	{
		String method = "testGetTestCasesByPlanName()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				TestLinkAPIResults results = api.getCasesForTestPlan(JUNIT_STATIC_PROJECT,
					JUNIT_STATIC_TEST_PLAN);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getCasesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestCasesByPlanName (Version=" + version + "):",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLastExecutionResult
	 */
	@Test
	public void testGetTestCasesManual()
	{
		String method = "testGetTestCasesManual()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				Integer projectID = TestLinkAPIHelper.getProjectID(api, JUNIT_STATIC_PROJECT);
				if ( projectID == null ) {
					throw new TestLinkAPIException(
						"Could not get project identifier for " + JUNIT_STATIC_PROJECT
						+ ". Version=" + version);
				}
				Integer planID = TestLinkAPIHelper.getPlanID(api, projectID,
					JUNIT_STATIC_TEST_PLAN);
				if ( planID == null ) {
					throw new TestLinkAPIException(
						"Could not get plan identifier for " + JUNIT_STATIC_TEST_PLAN + ". Version="
						+ version);
				}
				TestLinkAPIResults results = api.getCasesForTestPlan(planID, null, null,
					null, null, null, null, TESTCASE_EXECUTION_TYPE_MANUAL);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getCasesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestCasesPassedManual (Version=" + version + "):",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLastExecutionResult
	 */
	@Test
	public void testGetTestCasesAuto()
	{
		String method = "testGetTestCasesAuto()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				Integer projectID = TestLinkAPIHelper.getProjectID(api, JUNIT_STATIC_PROJECT);
				if ( projectID == null ) {
					throw new TestLinkAPIException(
						"Could not get project identifier for " + JUNIT_STATIC_PROJECT
						+ ". Version=" + version);
				}
				Integer planID = TestLinkAPIHelper.getPlanID(api, projectID,
					JUNIT_STATIC_TEST_PLAN);
				if ( planID == null ) {
					throw new TestLinkAPIException(
						"Could not get plan identifier for " + JUNIT_STATIC_TEST_PLAN);
				}
				TestLinkAPIResults results = api.getCasesForTestPlan(planID, null, null,
					null, null, null, null, TESTCASE_EXECUTION_TYPE_AUTO);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getCasesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestCasesPassedAuto (Version=" + version + "):",
					results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLastExecutionResult
	 */
	@Test
	public void testGetTestCasesFailed()
	{
		String method = "testGetTestCasesFailed()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				Integer projectID = TestLinkAPIHelper.getProjectID(api, JUNIT_STATIC_PROJECT);
				if ( projectID == null ) {
					throw new TestLinkAPIException(
						"Could not get project identifier for " + JUNIT_STATIC_PROJECT
						+ " Version=" + version);
				}
				Integer planID = TestLinkAPIHelper.getPlanID(api, projectID,
					JUNIT_STATIC_TEST_PLAN);
				if ( planID == null ) {
					throw new TestLinkAPIException(
						"Could not get plan identifier for " + JUNIT_STATIC_TEST_PLAN);
				}
				TestLinkAPIResults results = api.getCasesForTestPlan(planID, null, null,
					null, null, null, TEST_FAILED, null);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getCasesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestCasesFailed. (Version=" + version + "):", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method getLastExecutionResult
	 */
	@Test
	public void testGetTestCasesPassed()
	{
		String method = "testGetTestCasesPassed()";
		TestLinkTest test = new TestLinkTest() {
			public void runTest(String version, TestLinkAPIClient api) throws Exception {
				Integer projectID = TestLinkAPIHelper.getProjectID(api, JUNIT_STATIC_PROJECT);
				if ( projectID == null ) {
					throw new TestLinkAPIException(
						"Could not get project identifier for " + JUNIT_STATIC_PROJECT);
				}
				Integer planID = TestLinkAPIHelper.getPlanID(api, projectID,
					JUNIT_STATIC_TEST_PLAN);
				if ( planID == null ) {
					throw new TestLinkAPIException(
						"Could not get plan identifier for " + JUNIT_STATIC_TEST_PLAN + ". Version="
						+ version);
				}
				TestLinkAPIResults results = api.getCasesForTestPlan(planID, null, null,
					null, null, null, TEST_PASSED, null);
				if ( results == null ) {
					throw new Exception(
						"Unable to run getCasesForTestPlan() method. Version=" + version);
				}
				printResults("testGetTestCasesPassed. (Version=" + version + "):", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure()) {
			fail("Failed to run TestLink API " + method + " method. Version="
					+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	private void printResults(
		String method,
		TestLinkAPIResults results)
	{
		System.out.println(
			"\n----------------------------------------------------------------");
		System.out.println(method + " results: ");
		System.out.println(results.toString());
	}
	
}

