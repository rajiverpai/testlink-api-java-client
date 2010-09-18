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


/**
 * The TestCase interface is used to hold the data from an existing TestCase that
 * is acquired using the TestLink API or for creating a new Test Case in the 
 * TestLink database.
 * 
 * Therefore, the interface provides to initialization procedures. initExistingCase() and
 * and initNewCase(). The auto executor will expect all the data about the test case to
 * be initialized by one of these two methods.
 * 
 * @author Daniel Padilla
 *
 */
public interface TestCase
{
	
	/**
	 * Initialize a new case for an existing TestLink Test Project
	 */
	public void initNewCase(
		TestLinkTestProject testProject,
		TestLinkTestSuite testSuite,
		String caseName) throws TestLinkAPIException;
	
	/**
	 * Using the data returned from the TestLink API the test
	 * case is initialized.
	 * 
	 * @param testCase
	 */
	public void initExistingCase(
		TestLinkTestProject testProject,
		TestLinkTestSuite testSuite,
		Map testCaseInfo) throws TestLinkAPIException;
	
	/**
	 * Get the name of the project with which the test case is associated.
	 * 
	 * @return
	 */
	public String getProjectName();
	
	/**
	 * Get the internal identifier of the project with which the test case is associated.
	 * 
	 * @return
	 */
	public Integer getProjectID();
	
	/**
	 * Get the name of the test suite with which the test case is associated.
	 * 
	 * @return
	 */
	public String getSuiteName();
	
	/**
	 * Get the internal identifier of the test suite with which the test case is associated.
	 * 
	 * @return
	 */
	public Integer getSuiteID();
	
	/**
	 * 
	 * Get the name of the test case.
	 * 
	 * @return
	 */
	public String getTestCaseName();
	
	/**
	 * Set the name of the test case.
	 * 
	 * @param caseName
	 */
	public void setTestCaseName(
		String caseName);
	
	/**
	 * Get the test case's visible identifier in the web application.
	 * 
	 * @return
	 */
	public String getTestCaseVisibleID();
	
	/**
	 * Get the test case's internal identifier.
	 * 
	 * @return
	 */
	public Integer getTestCaseInternalID();
	
	/**
	 * Get the test case summary information.
	 * 
	 * @return
	 */
	public String getTestCaseSummary();
	
	/**
	 * Set the test case summary information.
	 * 
	 * @param summary
	 */
	public void setTestCaseSummary(
		String summary);
	
	/**
	 * Get the execution steps for the test case.
	 * 
	 * @return
	 */
	public String getTestCaseSteps();
	
	/**
	 * Set the execution steps for the test case.
	 * 
	 * @param steps
	 */
	public void setTestCaseSteps(
		String steps);
	
	/**
	 * Get the test case execution expected results.
	 * 
	 * @return
	 */
	public String getTestCaseExpectedResults();
	
	/**
	 * Set the test case's expected results.
	 * 
	 * @param expectedResults
	 */
	public void setTestCaseExpectedResults(
		String expectedResults);
	
	/**
	 * get custom information.
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getTestCaseCustomFieldContents(
		String fieldName);
	
	/**
	 * Set custom information.
	 * 
	 * @param fieldName
	 * @param contents
	 */
	public void setTestCaseCustomFieldContents(
		String fieldName,
		String contents);
	

	/**
	 * Return the test cases execution order
	 * 
	 * @return
	 */
	public int getExecOrder();
	
	/**
	 * Set the execution order for the test case.
	 * 
	 * @param order
	 */
	public void setExecOrder(int order);
	
	/**
	 * True if the test requires manual execution.
	 * 
	 * @return
	 */
	public boolean isManualExec();
	
	/**
	 * True if the test requires automated execution.
	 * 
	 * @return
	 */
	public boolean isAutoExec();
	
	/**
	 * Set test case to manual execution
	 */
	public void setExecTypeManual();
	
	/**
	 * Set test case to automated execution
	 */
	public void setExecTypeAuto();
	
	/**
	 * Get the version for the test case
	 * 
	 * @return
	 */
	public String getVersion();
	
	/**
	 * True if the test case is of low importance
	 * 
	 * @return
	 */
	public boolean isLowImportance();
	
	/**
	 * True if the test case is of medium.
	 * 
	 * @return
	 */
	public boolean isMediumImportance();
	
	/**
	 * True if the test case is of high importance.
	 * 
	 * @return
	 */
	public boolean isHighImportance();
	
	/**
	 * Set the test case to low importance
	 */
	public void setToLowImportance();
	
	/**
	 * Set the test case to medium importance
	 */
	public void setToMediumImportance();
	
	/**
	 * Set the test case to high importance
	 */
	public void setToHighImportance();
	
	/**
	 * See TestLink documenation for the meaning of this flag
	 * @return
	 */
	public boolean isActive();
	
	/**
	 * Add this test case to the TestLink Database if it does not exist
	 */
	public void addToTestLink(TestLinkAPIClient apiClient, String loginUserName) throws TestLinkAPIException;
	
	/**
	 * Get the test case's automated execution instance.
	 * @return
	 */
	public TestCaseExecutor getExecutor();
	
	/**
	 * Reqister the object that will be responsible for executing the test case automatically.
	 * 
	 * @param executor
	 */
	public void setExecutor(
		TestCaseExecutor executor);
	
}
