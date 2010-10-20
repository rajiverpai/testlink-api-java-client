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


import java.util.ArrayList;
import java.util.Map;


/**
 * This class contains a collection of helper methods that can
 * be used to compliment the TestLink API available methods.
 * 
 */
public class TestLinkAPIHelper implements TestLinkAPIConst
{

	/**
	 * Get the project identifier by test project name.
	 * 
	 * @param apiClient
	 * @param projectName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getProjectID(
		TestLinkAPIClient apiClient,
		String projectName) throws TestLinkAPIException
	{
		Map data = getProjectInfo(apiClient, projectName);
		return getIdentifier(data);
	}
	
	/**
	 * Get the project information
	 * 
	 * @param apiClient
	 * @param projectName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Map getProjectInfo(
		TestLinkAPIClient apiClient,
		String projectName) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getProjects();
		for ( int i = 0; i < results.size(); i++ ) {
			Object data = results.getValueByName(i, API_RESULT_NAME);
			if ( data != null ) {
				if ( projectName.equals(data.toString()) ) {
					return results.getData(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the project info by project ID
	 * 
	 * @param apiClient
	 * @param projectID
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Map getProjectInfo(
		TestLinkAPIClient apiClient,
		Integer projectID) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getProjects();
		for ( int i = 0; i < results.size(); i++ ) {
			Object data = results.getValueByName(i, API_RESULT_IDENTIFIER);
			if ( data != null ) {
				Integer identifier = new Integer(data.toString());
				if ( projectID.compareTo(identifier) == 0 ) {
					return results.getData(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the suite identifier by test project name and test suite name
	 * 
	 * @param apiClient
	 * @param projectName
	 * @param suiteName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getSuiteID(
		TestLinkAPIClient apiClient, 
		String projectName, 
		String suiteName) throws TestLinkAPIException
	{
		Integer projectID = getProjectID(apiClient, projectName);
		return getSuiteID(apiClient, projectID, suiteName);
	}
	
	public static Map getSuiteInfo(
		TestLinkAPIClient apiClient, 
		String projectName, 
		String suiteName) throws TestLinkAPIException
	{
		Integer projectID = getProjectID(apiClient, projectName);
		return getSuiteInfo(apiClient, projectID, suiteName);
	}
	
	/**
	 * Get the suite identifier by test project id and test suite name
	 * 
	 * @param apiClient
	 * @param projectID
	 * @param suiteName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getSuiteID(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		String suiteName) throws TestLinkAPIException
	{
		Map data = getSuiteInfo(apiClient, projectID, suiteName);
		return getIdentifier(data);
	}
	
	/**
	 * Get the suite record information by test project id and test suite name
	 * @param apiClient
	 * @param projectID
	 * @param suiteName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Map getSuiteInfo(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		String suiteName) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getFirstLevelTestSuitesForTestProject(
			projectID);
		for ( int i = 0; i < results.size(); i++ ) {
			Object data = results.getValueByName(i, API_RESULT_NAME);
			if ( data != null ) {
				if ( suiteName.equals(data.toString()) ) {
					return results.getData(i);
				}
			}
		}
		return null;
	}
	
	public static String getCaseVisibleID(
		TestLinkAPIClient apiClient,
		String projectName,
		String caseName) throws TestLinkAPIException
	{
		Map projectInfo = getProjectInfo(apiClient, projectName);
		Integer projectID = getIdentifier(projectInfo);
		Integer caseID = getCaseIDByName(apiClient, projectID, caseName);
		Map caseInfo = getTestCaseInfo(apiClient, projectID, caseID);
		Object prefix = projectInfo.get(API_RESULT_PREFIX);
		Object externalID = caseInfo.get(API_RESULT_TC_EXTERNAL_ID);
		return prefix.toString() + '-' + externalID.toString();
	}
	
	public static Integer getTestCaseID(
		TestLinkAPIClient apiClient,
		Integer projectID,
		String testCaseNameOrVisibleID) throws TestLinkAPIException
	{
		Integer caseID = null;
		try {
			caseID = getCaseIDByName(apiClient, projectID, testCaseNameOrVisibleID);
			
		} catch ( Exception e ) {
			caseID = null;
		} 
		
		if ( caseID == null ) {
			try {
				caseID = TestLinkAPIHelper.getCaseIDByVisibleID(apiClient, projectID,
					testCaseNameOrVisibleID);
			} catch ( Exception ee ) {
				return null;
			}
		}
		return caseID;
	}
			
	/**
	 * Get the test case identifier for a case name within a project.
	 * 
	 * @param apiClient
	 * @param projectID
	 * @param caseName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getCaseIDByName(
		TestLinkAPIClient apiClient,
		Integer projectID,
		String caseName) throws TestLinkAPIException
	{
		ArrayList cases = new ArrayList();
		TestLinkAPIResults results = apiClient.getFirstLevelTestSuitesForTestProject(
			projectID);
		for ( int i = 0; i < results.size(); i++ ) {
			Object id = results.getValueByName(i, API_RESULT_IDENTIFIER);
			if ( id != null ) {
				addAllMatchingCases(apiClient, cases, projectID,
					new Integer(id.toString()), caseName, null, false);
			}
		}
		Map data = getLatestVersionCaseID(cases);
		return getIdentifier(data);
	}
	
	/**
	 * Get the a test case identifier by test project id, suite id and test case name.
	 * 
	 * @param apiClient
	 * @param projectID
	 * @param suiteID
	 * @param caseName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getCaseIDByName(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		Integer suiteID,
		String caseName) throws TestLinkAPIException
	{
		ArrayList cases = new ArrayList();
		addAllMatchingCases(apiClient, cases, projectID, suiteID, caseName, null, false);
		Map data = getLatestVersionCaseID(cases);
		return getIdentifier(data);
	}
	
	public static Integer getCaseIDByVisibleID(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		String caseName) throws TestLinkAPIException
	{
		ArrayList cases = new ArrayList();
		Map projectInfo = TestLinkAPIHelper.getProjectInfo(apiClient, projectID);
		if ( projectInfo == null ) {
			throw new TestLinkAPIException("The failed to get the project information.");
		}
		String prefix = (String) projectInfo.get("prefix");
		TestLinkAPIResults results = apiClient.getFirstLevelTestSuitesForTestProject(
			projectID);
		for ( int i = 0; i < results.size(); i++ ) {
			Object id = results.getValueByName(i, API_RESULT_IDENTIFIER);
			if ( id != null ) {
				addAllMatchingCases(apiClient, cases, projectID,
					new Integer(id.toString()), caseName, prefix, true);
			}
		}
		Map data = getLatestVersionCaseID(cases);
		return getIdentifier(data);
	}
		
	/**
	 * Find the matching test case information and add it
	 * to the array list passes as a parameter.
	 * 
	 * @param apiClient
	 * @param cases
	 * @param projectID
	 * @param suiteID
	 * @param casePattern		The name or visible ID of the test case
	 * @param useVisibleID
	 * @throws TestLinkAPIException
	 */
	private static void addAllMatchingCases(
		TestLinkAPIClient apiClient, 
		ArrayList cases,
		Integer projectID, 
		Integer suiteID,
		String casePattern,
		String prefix,
		boolean useVisibleID) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getCasesForTestSuite(projectID, suiteID);
		Object id = null;
		for ( int i = 0; i < results.size(); i++ ) {
			Map data = results.getData(i);
			if ( data != null ) {
				Object externalID = data.get(API_RESULT_TC_EXTERNAL_ID);
				Object name = data.get(API_RESULT_NAME);
				if ( externalID != null && name != null ) {
					String currentPattern = name.toString();
					if ( useVisibleID ) {
						currentPattern = prefix.toString() + '-' + externalID.toString();
					}				
					if ( casePattern.equalsIgnoreCase(currentPattern) ) {
						id = results.getValueByName(i, API_RESULT_IDENTIFIER);
						if ( id != null ) {
							cases.add(results.getData(i));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Find the matching test case information and add it
	 * to the array list passes as a parameter.
	 * 
	 * @param apiClient
	 * @param cases
	 * @param projectID
	 * @param suiteID
	 * @param caseName
	 * @throws TestLinkAPIException
	 */
	public static Map getTestCaseInfo(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		Integer testCaseID) throws TestLinkAPIException
	{
		TestLinkAPIResults suites = apiClient.getFirstLevelTestSuitesForTestProject(
			projectID);
		for ( int i = 0; i < suites.size(); i++ ) {
			Object id = suites.getValueByName(i, API_RESULT_IDENTIFIER);
			if ( id != null ) {
				Integer suiteID = new Integer(id.toString());
				TestLinkAPIResults cases = apiClient.getCasesForTestSuite(projectID,
					suiteID);
				for ( int c = 0; c < cases.size(); c++ ) {
					id = cases.getValueByName(c, API_RESULT_IDENTIFIER);
					Integer currentTestCase = new Integer(id.toString());
					if ( currentTestCase.compareTo(testCaseID) == 0 ) {
						return (Map) cases.getData(c);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the latest version for the test cases passed in the array.
	 * The method assumes these are the results of all cases within 
	 * a suite or project that matched a specific case name.
	 * 
	 * @param cases
	 * @return
	 */
	private static Map getLatestVersionCaseID(
		ArrayList cases)
	{
		int maxVersion = 0;
		
		// find the max version
		for ( int i = 0; i < cases.size(); i++ ) {
			Map data = (Map) cases.get(i);
			Object version = data.get("tcversion_id");
			if ( version != null ) {
				int cv = new Integer(version.toString()).intValue();
				if ( cv > maxVersion ) {
					maxVersion = cv;
				}
			}
		}
		
		// return the max version
		for ( int i = 0; i < cases.size(); i++ ) {
			Map data = (Map) cases.get(i);
			Object version = data.get("tcversion_id");
			if ( version != null ) {
				int cv = new Integer(version.toString()).intValue();
				if ( cv == maxVersion ) {
					return data;
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the a test plan identifier by test project identifier and test plan name.
	 * 
	 * @param apiClient
	 * @param projectID
	 * @param suiteID
	 * @param caseName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getPlanID(
		TestLinkAPIClient apiClient, 
		Integer projectID, 
		String planName) throws TestLinkAPIException
	{
		Object id = null;
		Integer planID = null;
		Map planInfo = getPlanInfo(apiClient, projectID, planName);
		if ( planInfo != null ) {
			id = planInfo.get(API_RESULT_IDENTIFIER);
			if ( id != null ) {
				planID = new Integer(id.toString());
			}
		}
		return planID;
	}
	
	/**
	 * Get the project information by project identifier and test plan name.
	 * 
	 * @param apiClient
	 * @param projectID
	 * @param planName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Map getPlanInfo(
		TestLinkAPIClient apiClient,
		Integer projectID,
		String planName) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getProjectTestPlans(projectID);
		
		for ( int i = 0; i < results.size(); i++ ) {
			Object data = results.getValueByName(i, API_RESULT_NAME);
			if ( data != null ) {
				if ( planName.equals(data.toString()) ) {
					return results.getData(i);
				}
			}
		}		
		return null;
	}
	
	/**
	 * Get the build identifier by test plan id.
	 * 
	 * @param apiClient
	 * @param projectName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Integer getBuildID(
		TestLinkAPIClient apiClient,
		Integer planID,
		String buildName) throws TestLinkAPIException
	{
		Map data = getBuildInfo(apiClient, planID, buildName);
		return getIdentifier(data);
	}
	
	/**
	 * Get the build information by plan id and build name
	 * 
	 * @param apiClient
	 * @param projectName
	 * @return
	 * @throws TestLinkAPIException
	 */
	public static Map getBuildInfo(
		TestLinkAPIClient apiClient,
		Integer planID,
		String buildName) throws TestLinkAPIException
	{
		TestLinkAPIResults results = apiClient.getBuildsForTestPlan(planID);
		for ( int i = 0; i < results.size(); i++ ) {
			Object data = results.getValueByName(i, API_RESULT_NAME);
			if ( data != null ) {
				if ( buildName.equals(data.toString()) ) {
					return results.getData(i);
				}
			}
		}
		return null;
	}
	
	/*
	 * Private methods
	 */
	private static Integer getIdentifier(
		Map data)
	{
		Integer identifier = null;
		Object id = null;
		if ( data == null ) {
			return identifier;
		}
		id = data.get(API_RESULT_IDENTIFIER);
		if ( id != null ) {
			identifier = new Integer(id.toString());
		}
		return identifier;
	}
}
