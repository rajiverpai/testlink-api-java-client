# Introduction #

TestLinkAPIClient class is the principal interface to the TestLink API. An instance of the class can be used to make calls to most of the TestLink API xml-rpc methods. The list of supported methods can be found at http://code.google.com/p/dbfacade-testlink-rpc-api/wiki/TestLinkMethods.

In addition methods that make access to the API without the need to know internal TestLink database identfiers have been provided to shield the user from the need to access the TestLink database in order to use the API.


# Details #

<h2>Constructor Summary</h2>
  * <b><b>TestLinkAPIClient</b></b>(devKey, url)
> > Construct an instance with cache capabilities turned off.
  * <b><b>TestLinkAPIClient</b></b>(devKey, url, useCache)
> > Construct an instance and indicate if cache capabilities should be enabled or disabled.

<h2>Method Summary</h2>

  * <b>about</b>()
> > Get information about the TestLink API version.
  * <b>addTestCaseToTestPlan</b>( projectID,  planID,  testCaseVisibleID,  version,  execOrder,  urgency)
> > The method adds a test case from a project to the test plan.
  * <b>addTestCaseToTestPlan</b>( projectName,  planName,  testCaseName)
> > Appends that latest version of a test case to a test plan with a default level of urgency.
  * <b>addTestCaseToTestPlan</b>( projectName,  planName,  testCaseName,  execOrder,  urgency)
> > Appends that latest version of a test case to a test plan with a medium urgency.
  * <b>createBuild</b>( planID,  buildName,  buildNotes)
> > Create a new build under the provided test plan identifier.
  * <b>createBuild</b>( projectName,  planName,  buildName,  buildNotes)
> > Create a new build under the provided project name and test plan.
  * <b>createTestCase</b>( authorLoginName,  projectID,  suiteID,  caseName,  summary,  steps,  expectedResults,  order,  internalID,  checkDuplicatedName,  actionOnDuplicatedName,  executionType,  importance)
> > Create a new test case using all the variables that are provided by the TestLink API.
  * <b>createTestCase</b>( authorLoginName,  projectName,  suiteName,  testCaseName,  summary,  steps,  expectedResults,  importance)
> > Create a test case by project name and suite name.
  * <b>createTestProject</b>( projectName,  testCasePrefix,  description)
> > Create a new project in TestLink database.
  * <b>createTestSuite</b>( projectID,  suiteName,  description)
> > Create top level test suite under a specific project identifier
  * <b>createTestSuite</b>( projectID,  suiteName,  description,  parentID,  order,  check)
> > Create a test suite at any level using the project identifier and the parent suite identifier information.
  * <b>createTestSuite</b>( projectName,  suiteName,  description)
> > Create top level test suite under a specific project name
  * <b>getBuildsForTestPlan</b>( planID)
> > Get a list of builds for a test plan id
  * <b>getBuildsForTestPlan</b>( projectName,  planName)
> > Get the builds by project and plan name.
  * <b>getCasesForTestPlan</b>( testPlanID)
> > Get all the test cases associated with a test plan identifier.
  * <b>getCasesForTestPlan</b>( testPlanID,  testCaseID,  buildID,  keywordID,  executed,  assignedTo,  execStatus,  execType)
> > Get all the test cases associated with a test plan.
  * <b>getCasesForTestPlan</b>( projectName,  planName)
> > Gets all the test cases for a test plan by project name and plan name.
  * <b>getCasesForTestSuite</b>( testProjectID,  testSuiteID)
> > Get all the test cases for a project identifier and test suite identifier.
  * <b>getFirstLevelTestSuitesForTestProject</b>( projectID)
> > Get all the first level project test suites by project id
  * <b>getFirstLevelTestSuitesForTestProject</b>( projectName)
> > Get all the first level project test suites by project name
  * <b>getLastExecutionResult</b>( testPlanID,  testCaseID)
> > Get the last execution result by plan identifier and test case internal identifier.
  * <b>getLastExecutionResult</b>( projectName,  testPlanName,  testCaseNameOrVisibleID)
> > Get the last execution result by project, plan and test case name/visible id.
  * <b>getLatestBuildForTestPlan</b>( planID)
> > Get a latest build for a test plan id
  * <b>getLatestBuildForTestPlan</b>( projectName,  planName)
> > Get the latest build by project and plan name.
  * <b>getProjects</b>()
> > Get a list of all the existing test projects for the instantiated TestLink URL.
  * <b>getProjectTestPlans</b>( projectID)
> > Get the test plans for a project identifier.
  * <b>getProjectTestPlans</b>( projectName)
> > Get a list of all the existing test plans for a project by name.
  * <b>getTestCaseIDByName</b>( testCaseName)
> > Get a test case id by name
  * <b>getTestCaseIDByName</b>( testCaseName,  testProjectName,  testSuiteName)
> > Get test case by name.
  * <b>getTestSuitesForTestPlan</b>( testPlanID)
> > Get test suites for test test plan by plan identifier.
  * <b>getTestSuitesForTestPlan</b>( projectName,  planName)
> > Get test suites for a test plan by project and plan name.
  * <b>ping</b>()
> > Allows user to test if the XML-RPC TestLink API is responding.
  * <b>reportTestCaseResult</b>( testPlanID,  testCaseID,  buildID,  bugID,  guess,  execNotes,  testResultStatus)
> > This method supports the TestLink API set of parameters that can be used to report a test case result.
  * <b>reportTestCaseResult</b>( testPlanID,  testCaseID,  buildID,  execNotes,  testResultStatus)
> > Report a test execution result for a test case by test plan identifier and test case identifier for a specific build identifier.
  * <b>reportTestCaseResult</b>( projectName,  testPlanName,  testCaseNameOrVisibleID,  buildName,  execNotes,  testResultStatus)
> > Report a test execution result for a test case by test project name and test plan name for a specific build.