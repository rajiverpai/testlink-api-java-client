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
package testlink.api.java.client.junit.autoexec;


import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testlink.api.java.client.TestLinkTestCase;
import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.TestLinkAPIHelper;
import testlink.api.java.client.TestLinkTestProject;
import testlink.api.java.client.TestLinkTestSuite;
import testlink.api.java.client.junit.constants.TestConst;


/**
 * 
 * Test used to verify ExecutableTestCase implementation
 * 
 * @author Daniel Padilla
 *
 */
public class ExecutableTestCaseTest implements TestLinkAPIConst, TestConst
{	
	// The api instance
	private TestLinkAPIClient api;
	
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
		api = new TestLinkAPIClient(userKey, api182URL, true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{}
	
	/**
	 * Test ExecutableTestCase.initExistingCase() method
	 */
	@Test
	public void testTestPlanConstructor()
	{
		try {
			Map<Object, Object> projectInfo= TestLinkAPIHelper.getProjectInfo(api, JUNIT_STATIC_PROJECT);
			TestLinkTestProject project = new TestLinkTestProject(projectInfo);
			project.getProjectID();
		} catch ( Exception e ) {
			e.printStackTrace();
			fail("Failed to initialize the existing test case.");
		}
	}

	/**
	 * Test ExecutableTestCase.initExistingCase() method
	 */
	@Test
	public void testInitExistingCase()
	{
		try {
			// Get IDs
			Integer projectID = TestLinkAPIHelper.getProjectID(api, JUNIT_STATIC_PROJECT);
			Integer caseID = TestLinkAPIHelper.getCaseIDByName(api, projectID, JUNIT_STATIC_CASE);
			
			// Get information for the ids
			Map<Object, Object> testCaseInfo = TestLinkAPIHelper.getTestCaseInfo(api, projectID, caseID);
			Map<Object, Object> projectInfo= TestLinkAPIHelper.getProjectInfo(api, JUNIT_STATIC_PROJECT);
			Map<Object, Object> suiteInfo = TestLinkAPIHelper.getSuiteInfo(api, projectID, JUNIT_STATIC_SUITE);
			
			// Create the classes
			TestLinkTestProject testProject = new TestLinkTestProject(projectInfo);
			TestLinkTestSuite testSuite = new TestLinkTestSuite(suiteInfo);
			TestCase testCase = new TestLinkTestCase();
			
			// Test the initializer
			testCase.initExistingCase(testProject, testSuite, testCaseInfo);
			
		} catch ( Exception e ) {
			fail("Failed to initialize the existing test case.");
		}
	}	
	
}

