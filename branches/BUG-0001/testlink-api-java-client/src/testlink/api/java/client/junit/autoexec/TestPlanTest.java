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

import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testlink.api.java.client.TestCase;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.TestLinkTestProject;
import testlink.api.java.client.junit.constants.TestConst;
import testlink.api.java.client.tc.autoexec.EmptyExecutor;
import testlink.api.java.client.tc.autoexec.ExecuteTestCases;
import testlink.api.java.client.tc.autoexec.TestCaseExecutor;
import testlink.api.java.client.tc.autoexec.TestPlanLoader;


/**
 * 
 * Test used to verify ExecutableTestCase implementation
 * 
 * @author Daniel Padilla
 *
 */
public class TestPlanTest implements TestLinkAPIConst,
	TestConst
{	
	// The api instance
	private TestLinkAPIClient api;
	private static TestPlanLoader planLoader;
	
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
	 * Test TestPlanLoader
	 */
	@Test
	public void testTestProject()
	{
		try {
			TestLinkTestProject proj = new TestLinkTestProject(api, JUNIT_STATIC_PROJECT);
			System.out.println(proj.toString());
		} catch ( Exception e ) {
			e.printStackTrace();
			fail("Failed to create test project.");
		}
	}
		
	/**
	 * Test TestPlanLoader
	 */
	@Test
	public void testTestPlanLoader()
	{
		try {
			planLoader = new TestPlanLoader(JUNIT_STATIC_PROJECT, api);
			Map plans = planLoader.getPlans();
			System.out.println(planLoader.toString());
			System.out.println(plans.toString());
		} catch ( Exception e ) {
			e.printStackTrace();
			fail("Failed to load the test plans.");
		}
	}
	
	/**
	 * Test ToArray
	 */
	@Test
	public void testToArray()
	{
		try {
			int cnt = 0;
			Iterator ids = planLoader.getPlanIDs();
			while ( ids.hasNext() ) {
				Object id = ids.next();
				TestLinkTestPlan plan = planLoader.getPlan(id);
				TestCase[] cases = plan.getTestCases();
				for ( int i = 0; i < cases.length; i++ ) {
					TestCase tc = cases[i];
					String name = tc.getTestCaseName();
					System.out.println(name);
					cnt++;
				}
			}
			if ( cnt == 0 ) {
				fail("No test cases were printed and some were expected.");
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			fail("Make sure we can get an array of test cases.");
		}
	}
	
	/**
	 * Test test execution
	 */
	@Test
	public void testTestExecution()
	{
		try {
			int cnt = 0;
			Iterator ids = planLoader.getPlanIDs();
			while ( ids.hasNext() ) {
				Object id = ids.next();
				TestLinkTestPlan plan = planLoader.getPlan(id);
				TestCase[] cases = plan.getTestCases();
				for ( int i = 0; i < cases.length; i++ ) {
					TestCase tc = cases[i];
					tc.setExecutor(new EmptyExecutor(TestCaseExecutor.RESULT_PASSED));
					String name = tc.getTestCaseName();
					System.out.println(name);
					cnt++;
				}
				ExecuteTestCases execution = new ExecuteTestCases(api, plan,
					"testlink.api.java.client.junit.autoexec.PassExecutor");
				execution.executeTestCases(true, false);
				
				/**
				 * All the test should have passed
				 */
				if ( !execution.hasTestPassed() ) {
					throw new Exception("The tests all did not come back as passed.");
				}
				
				/**
				 * All the test should have passed
				 */
				if ( !execution.hasTestRun() ) {
					throw new Exception("The tests did not run.");
				}
			}
			if ( cnt == 0 ) {
				fail("No test cases were printed and some were expected.");
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			fail("Make sure we can get an array of test cases.");
		}
	}
	
}

