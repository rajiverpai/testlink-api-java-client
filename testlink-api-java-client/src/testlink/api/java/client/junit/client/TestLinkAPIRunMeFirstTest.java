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

import java.util.Map;

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
 * This test performs a standard setup of the JUNIT project
 * plan used in Creator and Setter test. This does some creation
 * and getter testing so the most important pieces of testing
 * are done by this module in the first place.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkAPIRunMeFirstTest implements TestLinkAPIConst,
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
		runner = new VersionListTestRunner();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{}
	
	/**
	 * Print out version information
	 */
	@Test
	public void testAbout()
	{
		String method = "testAbout()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
				TestLinkAPIResults results = api.about();
				if ( results == null ) {
					throw new Exception("Unable to run about method. Version=" + version);
				}
				printResults("testAbout (" + version + ") ", results);
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Create the JUNIT_STATIC_PROJECT
	 */
	@Test
	public void createStandardTestProject()
	{
		String method = "createStandardTestProject()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
				
				boolean createProject = false;
				try {
					Map project = TestLinkAPIHelper.getProjectInfo(api,
						JUNIT_STATIC_PROJECT);
					if ( project == null ) {
						createProject = true;
					}
				} catch ( Exception e ) {
					createProject = true;
				}
			
				if ( createProject ) {
					Integer id = api.createTestProject(JUNIT_STATIC_PROJECT,
						JUNIT_STATIC_PREFIX, JUNIT_STATIC_PROJECT + " created by JUnit test.");
					if ( id == null ) {
						throw new Exception("Unable to create project.");
					}
				}
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Create the JUNIT_STATIC_SUITE
	 */
	@Test
	public void createStandardTestSuite()
	{
		String method = "createStandardTestSuite()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
				
				boolean createSuite = false;
				try {
					Map suite = TestLinkAPIHelper.getSuiteInfo(api, JUNIT_STATIC_PROJECT,
						JUNIT_STATIC_SUITE);
					if ( suite == null ) {
						createSuite = true;
					}
				} catch ( Exception e ) {
					createSuite = true;
				}
				
				if ( createSuite ) {
					Integer id = api.createTestSuite(JUNIT_STATIC_PROJECT,
						JUNIT_STATIC_SUITE, "This suite was created by a JUnit test.");
					if ( id == null ) {
						throw new Exception(
							"Failed to create a test suite for project " + JUNIT_STATIC_SUITE
							+ ". Version=" + version); 
					}
				}
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Create JUNIT_STANDARD_CASE
	 */
	@Test
	public void createStandardTestCase()
	{
		String method = "createStandardTestCase()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
			
				boolean createTestCase = false;
				try {
					TestLinkAPIResults testCase = api.getTestCaseIDByName(
						JUNIT_STATIC_CASE, JUNIT_STATIC_PROJECT, JUNIT_STATIC_SUITE);
					if ( testCase == null ) {
						createTestCase = true;
					}
				} catch ( Exception e ) {
					createTestCase = true;
				}
			
				if ( createTestCase ) {
					Integer id = api.createTestCase("admin", JUNIT_STATIC_PROJECT, 
						JUNIT_STATIC_SUITE, JUNIT_STATIC_CASE, "JUnit created summary.",
						"JUnit created steps.", "JUnit created expected results.", HIGH);
					if ( id == null || id.intValue() == 0 ) {
						throw new Exception(
							"Failed to create a test case for project " + JUNIT_STATIC_CASE
							+ ". Version=" + version); 
					}
				}
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Test method create test plan. 
	 * 
	 * TestLink does not offer a createTestPlan procedure at
	 * this time so it will need to be created manually. This
	 * procedure will test of the project, suite and cases so
	 * that the test plan can be setup.
	 * 
	 */
	@Test
	public void doesStandardTestPlanExist()
	{
		String method = "doesStandardTestPlanExist()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
				Integer projectID = TestLinkAPIHelper.getProjectID(api,
					JUNIT_STATIC_PROJECT);
				Integer planID = TestLinkAPIHelper.getPlanID(api, projectID,
					JUNIT_STATIC_TEST_PLAN);
				if ( planID == null || planID.intValue() == 0 ) {
					throw new TestLinkAPIException(
						"The test plan '" + JUNIT_STATIC_TEST_PLAN
						+ "' was not found. Version=" + version);
				}
			}
		
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
				+ ((results == null) ? null : results.getLatestFailedVersion()));
		}
	}
	
	/**
	 * Issue #1 in google code test
	 */
	@Test
	public void testAddTestCaseToTestPlan()
	{
		String method = "testAddTestCaseToTestPlan()";
		TestLinkTest test = new TestLinkTest()
		{
			public void runTest(
				String version,
				TestLinkAPIClient api) throws Exception
			{
				Integer id = api.createTestCase("admin", JUNIT_STATIC_PROJECT, 
					JUNIT_STATIC_SUITE, JUNIT_DYNAMIC_CASE, "JUnit created summary.",
					"JUnit created steps.", "JUnit created expected results.", HIGH);
				if ( id == null ) {
					throw new Exception(
						"Unable to add test case to test plan suite. Version=" + version);
				}
				TestLinkAPIResults added = api.addTestCaseToTestPlan(JUNIT_STATIC_PROJECT,
					JUNIT_STATIC_TEST_PLAN, JUNIT_DYNAMIC_CASE);
				if ( added == null ) {
					throw new Exception(
						"Unable to add test case to test plan. Version=" + version);
				}
			}
		};

		// Run test and record failure if it happens
		RunExceptionResults results = runner.runTest(method, test);
		if ( results == null || results.containsFailure() ) {
			fail(
				"Failed to run TestLink API " + method + " method. Version="
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

