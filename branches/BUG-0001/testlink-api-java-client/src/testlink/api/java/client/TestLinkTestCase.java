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


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import testlink.api.java.client.tc.autoexec.TestCaseExecutor;


/**
 * Default implementation of the TestCase interface for test cases that
 * can be executed.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkTestCase implements TestCase
{
	private TestLinkTestProject testProject;
	private TestLinkTestSuite testSuite;
	private String testCaseName;
	private String testCaseVisibleID;
	private Integer testCaseID;
	private Integer execOrder = new Integer(5000);
	private Integer execType = new Integer(TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_MANUAL);
	private String testCaseSummary;
	private List<HashMap<String, Object>> testCaseSteps;
	private String testCaseExpectedResults;
	private String testCaseVersion;
	private String testCaseImportance = TestLinkAPIConst.MEDIUM;
	private Map<Object, Object> custom = new HashMap<Object, Object>();
	private TestCaseExecutor autoTestExecutor = null;
	private boolean isActive = true;
	
	/**
	 * Default empty constructor.
	 */
	public TestLinkTestCase()
	{}
	
	/**
	 * Make a copy of the test case and return it as a new instance.
	 * This is useful for extending the test case and replacing
	 * the test case that will run during automated running in a 
	 * test plan.
	 * 
	 * @param testCaseOther
	 */
	public TestLinkTestCase(
		TestLinkTestCase testCaseOther)
	{
		this.copy(testCaseOther);
	}
	
	/**
	 * Using the data returned from the TestLink API the test
	 * case is initialized.
	 * 
	 * @param testCase
	 */
	public void initNewCase(
		TestLinkTestProject projectInfo,
		TestLinkTestSuite suiteInfo,
		String caseName) throws TestLinkAPIException
	{
		if ( projectInfo == null ) {
			throw new TestLinkAPIException(
				"The test project information cannot be initialized to null.");
		}
		if ( suiteInfo == null ) {
			throw new TestLinkAPIException(
				"The test suite information cannot be initialized to null.");
		}
		this.testProject = projectInfo;
		this.testSuite = suiteInfo;
		this.testCaseName = caseName;
	}
	
	/**
	 * Using the data returned from the TestLink API the test
	 * case is initialized.
	 * 
	 * @param testCase
	 */
	@SuppressWarnings("unchecked")
	public void initExistingCase(
		TestLinkTestProject projectInfo,
		TestLinkTestSuite suiteInfo,
		Map<Object, Object> testCaseInfo) throws TestLinkAPIException
	{
		if ( projectInfo == null ) {
			throw new TestLinkAPIException(
				"The test project information cannot be initialized to null.");
		}
		if ( suiteInfo == null ) {
			throw new TestLinkAPIException(
				"The test suite information cannot be initialized to null.");
		}
		if ( testCaseInfo == null ) {
			throw new TestLinkAPIException(
				"The test suite information cannot be initialized to null.");
		}
		this.testProject = projectInfo;
		this.testSuite = suiteInfo;
		
		// Identifier
		Object value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_TC_INTERNAL_ID);
		if ( value != null ) {
			this.testCaseID = new Integer(value.toString());
		} else {
			value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_IDENTIFIER);
			if ( value != null ) {
				this.testCaseID = new Integer(value.toString());
			} else {
				throw new TestLinkAPIException(
					"The test case identifier cannot be null for existing test case.");
			}
		}
		
		// Name
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_NAME);
		if ( value != null ) {
			this.testCaseName = value.toString();
		} else {
			throw new TestLinkAPIException(
				"The test case name cannot be null for existing test case.");
		}
		
		// Execution Order
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_EXEC_ORDER);
		if ( value != null ) {
			this.execOrder = new Integer(value.toString());
		} else {
			value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_NODE_ORDER);
			if ( value != null ) {
				this.execOrder = new Integer(value.toString());
			} else {
				throw new TestLinkAPIException(
					"The test case exec order cannot be null for existing test case.");
			}
		}
		
		// Visible ID
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_TC_EXTERNAL_ID);
		if ( value != null ) {
			this.testCaseVisibleID = this.testProject.getTestCasePrefix() + '-'
				+ value.toString();
		} else {
			value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_TC_ALT_EXTERNAL_ID);
			if ( value != null ) {
				this.testCaseVisibleID = this.testProject.getTestCasePrefix() + '-'
				+ value.toString();
			} else {
				throw new TestLinkAPIException(
					"The test case external identifier cannot be null for existing test case.");
			}
		}
		
		// Summary
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_SUMMARY);
		if ( value != null ) {
			this.testCaseSummary = value.toString();
		}
		
		// Execution Type
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_EXEC_TYPE);
		if ( value != null ) {
			this.execType = new Integer(value.toString());
		} else {
			throw new TestLinkAPIException(
				"The test case execution type cannot be null for existing test case.");
		}
		
		// Is Active
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_ACTIVE); 
		if ( value != null ) {
			if ( new Integer(value.toString()).intValue() < 1 ) {
				isActive = false;
			}
		}
		
		// Steps
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_STEPS);
		if ( value != null ) {
			this.testCaseSteps = (List<HashMap<String, Object>>) value;
		}
		
		// Suite verification
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_TC_SUITE);
		if ( value != null ) {
			if ( !testSuite.getSuiteName().equals(value.toString()) ) {
				throw new TestLinkAPIException(
					"The pass test suite name " + testSuite.getSuiteName()
					+ " does not match the suite name " + value.toString()
					+ " for the test case.");
			}
		} else {
			throw new TestLinkAPIException(
				"The test case suite name cannot be null for existing test case.");
			
		}
		
		// TC Version
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_VERSION);
		if ( value != null ) {
			this.testCaseVersion = value.toString();
		}
		
		// Expected results
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_EXPECTED_RESULTS);  
		if ( value != null ) {
			this.testCaseExpectedResults = value.toString();
		}
		
		// Importance
		value = testCaseInfo.get(TestLinkAPIConst.API_RESULT_IMPORTANCE);
		if ( value != null ) {
			this.testCaseImportance = value.toString();
		}
		
	}
	
	/**
	 * Get the name of the project with which the test case is associated.
	 * 
	 * @return
	 */
	public String getProjectName()
	{
		return testProject.getProjectName();
	}
	
	/**
	 * Get the internal identifier of the project with which the test case is associated.
	 * 
	 * @return
	 */
	public Integer getProjectID()
	{
		return testProject.getProjectID();
	}
	
	/**
	 * Get the name of the test suite with which the test case is associated.
	 * 
	 * @return
	 */
	public String getSuiteName()
	{
		return testSuite.getSuiteName();
	}
	
	/**
	 * Get the internal identifier of the test suite with which the test case is associated.
	 * 
	 * @return
	 */
	public Integer getSuiteID()
	{
		return testSuite.getSuiteID();
	}
	
	/**
	 * Get the name of the test case.
	 * 
	 * @return
	 */
	public String getTestCaseName()
	{
		return testCaseName;
	}
	
	/**
	 * Set the name of the test case.
	 * 
	 * @param caseName
	 */
	public void setTestCaseName(
		String caseName)
	{
		this.testCaseName = caseName;
	}
	
	/**
	 * Get the test case's visible identifier in the web application.
	 * 
	 * @return
	 */
	public String getTestCaseVisibleID()
	{
		return testCaseVisibleID;
	}
	
	/**
	 * Get the test case's internal identifier.
	 * 
	 * @return
	 */
	public Integer getTestCaseInternalID()
	{
		return testCaseID;
	}
	
	/**
	 * Get the test case summary information.
	 * 
	 * @return
	 */
	public String getTestCaseSummary()
	{
		return testCaseSummary;
	}
	
	/**
	 * Set the test case summary information.
	 * 
	 * @param summary
	 */
	public void setTestCaseSummary(
		String summary)
	{
		this.testCaseSummary = summary;
	}
	
	/**
	 * Get the execution steps for the test case.
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getTestCaseSteps()
	{
		return testCaseSteps;
	}
	
	/**
	 * Set the execution steps for the test case.
	 * 
	 * @param steps
	 */
	public void setTestCaseSteps(
		List<HashMap<String, Object>> steps)
	{
		this.testCaseSteps = steps;
	}
	
	/**
	 * Get the test case execution expected results.
	 * 
	 * @return
	 */
	public String getTestCaseExpectedResults()
	{
		return testCaseExpectedResults;
	}
	
	/**
	 * Set the test case's expected results.
	 * 
	 * @param expectedResults
	 */
	public void setTestCaseExpectedResults(
		String expectedResults)
	{
		this.testCaseExpectedResults = expectedResults;
	}
	
	/**
	 * Get custom information.
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getTestCaseCustomFieldContents(
		String fieldName)
	{
		return (String) this.custom.get(fieldName);
	}
	
	/**
	 * Set custom information.
	 * 
	 * @param fieldName
	 * @param contents
	 */
	public void setTestCaseCustomFieldContents(
		String fieldName,
		String contents)
	{
		this.custom.put(fieldName, contents);
	}
	
	/**
	 * Get the test case execution order within a plan.
	 * 
	 * @return
	 */
	public int getExecOrder()
	{
		return execOrder.intValue();
	}

	/**
	 * Set the execution order for the test case.
	 * 
	 * @param order
	 */
	public void setExecOrder(
		int order)
	{
		this.execOrder = new Integer(order);
	}
	
	/**
	 * True if the test requires manual execution.
	 * 
	 * @return
	 */
	public boolean isManualExec()
	{
		return !(isAutoExec());
	}
	
	/**
	 * True if the test requires automated execution.
	 * 
	 * @return
	 */
	public boolean isAutoExec()
	{
		Integer auto = new Integer(TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_AUTO);
		return (execType.intValue() == auto.intValue());
	}
	
	/**
	 * Set test case to manual execution
	 */
	public void setExecTypeManual()
	{
		execType = new Integer(TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_MANUAL);
	}
	
	/**
	 * Set test case to automated execution
	 */
	public void setExecTypeAuto()
	{
		execType = new Integer(TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_AUTO);
	}
	
	/**
	 * Get the version for the test case
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return testCaseVersion;
	}
	
	/**
	 * True if the test case is of low importance
	 * 
	 * @return
	 */
	public boolean isLowImportance()
	{
		return(this.testCaseImportance.equals(TestLinkAPIConst.LOW));
	}
	
	/**
	 * True if the test case is of medium.
	 * 
	 * @return
	 */
	public boolean isMediumImportance()
	{
		return(this.testCaseImportance.equals(TestLinkAPIConst.MEDIUM));
	}
	
	/**
	 * True if the test case is of high importance.
	 * 
	 * @return
	 */
	public boolean isHighImportance()
	{
		return (!isLowImportance() && !isMediumImportance());
	}
	
	/**
	 * Set the test case to low importance
	 */
	public void setToLowImportance()
	{
		this.testCaseImportance = TestLinkAPIConst.LOW;
	}
	
	/**
	 * Set the test case to medium importance
	 */
	public void setToMediumImportance()
	{
		this.testCaseImportance = TestLinkAPIConst.MEDIUM;
	}
	
	/**
	 * Set the test case to high importance
	 */
	public void setToHighImportance()
	{
		this.testCaseImportance = TestLinkAPIConst.HIGH;
	}
	
	/**
	 * See test link documentation for the meaning of this flag
	 */
	public boolean isActive()
	{
		return isActive;
	}
	
	public void addToTestLink(
		TestLinkAPIClient apiClient,
		String loginUserName) throws TestLinkAPIException
	{
		if ( !doesCaseExist(apiClient, this) ) {
			createTestCase(apiClient, this, loginUserName);
		}
	}
	
	/**
	 * Get the test case's automated execution instance.
	 * @return
	 */
	public TestCaseExecutor getExecutor()
	{
		return autoTestExecutor;
	}
	
	/**
	 * Reqister the object that will be responsible for executing the test case automatically.
	 * 
	 * @param executor
	 */
	public void setExecutor(
		TestCaseExecutor executor)
	{
		this.autoTestExecutor = executor;
	}
	
	/*
	 * Private methods
	 */

	private void copy(
		TestLinkTestCase otherTC)
	{
		this.testProject = new TestLinkTestProject(otherTC.testProject);
		
		if ( otherTC.testSuite != null ) {
			this.testSuite = new TestLinkTestSuite(otherTC.testSuite);
		}

		if ( otherTC.testCaseID != null ) {
			this.testCaseID = new Integer(otherTC.testCaseID.intValue());
		}
		
		if ( otherTC.execOrder != null ) {
			this.execOrder = new Integer(otherTC.execOrder.intValue());
		}
		
		if ( otherTC.execType != null ) {
			this.execType = new Integer(otherTC.execType.intValue());
		}
		
		if ( otherTC.testCaseName != null ) {
			this.testCaseName = new String(otherTC.testCaseName);
		}
		
		if ( otherTC.testCaseVisibleID != null ) {
			this.testCaseVisibleID = new String(otherTC.testCaseVisibleID);
		}
		
		if ( otherTC.testCaseSummary != null ) {
			this.testCaseSummary = new String(otherTC.testCaseSummary);
		}
		
		if ( otherTC.testCaseSteps != null ) {
			this.testCaseSteps = otherTC.testCaseSteps;
		}
		
		if ( otherTC.testCaseExpectedResults != null ) {
			this.testCaseExpectedResults = new String(otherTC.testCaseExpectedResults);
		}
		
		if ( otherTC.testCaseVersion != null ) {
			this.testCaseVersion = new String(otherTC.testCaseVersion);
		}
		
		if ( otherTC.testCaseImportance != null ) {
			this.testCaseImportance = new String(otherTC.testCaseImportance);
		}
		
		Map<Object, Object> tmp = new HashMap<Object, Object>();
		Iterator<Object> keys = otherTC.custom.keySet().iterator();
		while ( keys.hasNext() ) {
			Object key = keys.next();
			Object value = otherTC.custom.get(key);
			if ( value instanceof String ) {
				tmp.put(key, new String(value.toString()));
			} else {
				tmp.put(key, value);
			}
		}
		this.custom = tmp;
		
		this.isActive = otherTC.isActive;
		
		this.autoTestExecutor = otherTC.autoTestExecutor;
		
	}

	/*
	 * Check to see if the test case exist.
	 */
	private boolean doesCaseExist(
		TestLinkAPIClient apiClient,
		TestCase tc)
	{
		if ( tc.getTestCaseInternalID() == null ) {
			return false;
		}
		
		if ( tc.getTestCaseInternalID().intValue() < 1 ) {
			return false;
		}
		
		try {
			Map<Object, Object> tcInfo = TestLinkAPIHelper.getTestCaseInfo(apiClient,
				testProject.getProjectID(), tc.getTestCaseInternalID());
			if ( tcInfo != null ) {
				return true;
			} else {
				return false;
			}
		} catch ( Exception e ) {
			return false;
		}
	}
	
	/*
	 * Create the test case if it does not exist
	 */
	private void createTestCase(
		TestLinkAPIClient apiClient,
		TestCase tc,
		String loginUserName)
	{
		try {
			String execType = TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_MANUAL;
			if ( tc.isAutoExec() ) {
				execType = TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_AUTO;
			}
			String importance = TestLinkAPIConst.MEDIUM;
			if ( tc.isLowImportance() ) {
				importance = TestLinkAPIConst.LOW;
			} else if ( tc.isHighImportance() ) {
				importance = TestLinkAPIConst.HIGH;
			}
			apiClient.createTestCase(loginUserName, testProject.getProjectID(),
				tc.getSuiteID(), tc.getTestCaseName(), tc.getTestCaseSummary(),
				tc.getTestCaseSteps(), tc.getTestCaseExpectedResults(), new Integer(tc.getExecOrder()),
				null, null, null, execType, importance);
		} catch ( Exception e ) {// TODO: Report back some kind of error
		}
		
	}
	
}
