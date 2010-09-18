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
package testlink.api.java.client.tc.autoexec;


import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkTestPlan;


/**
 * An implementer of this interface is expected to do the following:
 * 
 * Required:
 * 
 * 1)  Assign the executors for all the automated test cases in the test plan.  
 * 
 * Optional:
 * 
 *  1) Add additional test cases that are not listed in the plan. Especially helpful
 *  when the list of test cases is maintained and added to using programatic means.
 *  
 *  2) Replace the test cases with different TestCase interface implementations. 
 *  A good example would be when the default ExecutableTestCase could be replaced
 *  with CustomTestCase which extends ExecutableTestCase. 
 *  
 *  3) etc..
 *  
 * @author Daniel Padilla
 *
 */
public interface TestPlanPrepare
{

	/**
	 * Optionally made available by callers to the interface
	 * 
	 * @param directory
	 */
	public void setExternalPath(String path);
	
	/**
	 * Optionally made available by callers to the interface
	 * 
	 * @param user
	 */
	public void setTCUser(String user);
	
	
	/**
	 * Make changes to the contents of the test plan and test cases.	 
	 * 
	 * @param plan
	 * @return A test plan which has had the executors set for each test case.
	 * 
	 */
	public TestLinkTestPlan adjust(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan) throws TestLinkAPIException;
}
