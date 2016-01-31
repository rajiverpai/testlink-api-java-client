# Introduction #

The following is a listing of the methods the TestLink API Java Client supports or is scheduled to support.


# Details #

Automated Test Results Recording

|**Method**|**Supported**|**Development Status**|**Priority**|
|:---------|:------------|:---------------------|:-----------|
|tl.reportTCResult|Yes          |Completed             |-           |


Utility methods

|**Method**|**Supported**|**Development Status**|**Priority**|
|:---------|:------------|:---------------------|:-----------|
|tl.about  |Yes          |Completed             |-           |
|tl.ping   |Yes          |Completed             |-           |

Create Methods

|**Method**|**Supported**|**Development Status**|**Priority**|
|:---------|:------------|:---------------------|:-----------|
|tl.createTestProject|Yes          |Completed             |-           |
|tl.createTestSuite|Yes          |Completed             |-           |
|tl.createTestCase|Yes          |Completed             |-           |
|tl.createBuild|Yes          |Completed             |-           |

Getter Methods

|**Method**|**Supported**|**Development Status**|**Priority**|
|:---------|:------------|:---------------------|:-----------|
|tl.getProjects|Yes          |Completed             |-           |
|tl.getFirstLevelTestSuitesForTestProject|Yes          |Completed             |-           |
|tl.getProjectTestPlans|Yes          |Completed             |-           |
|tl.getBuildsForTestPlan|Yes          |Completed             |-           |
|tl.getLatestBuildForTestPlan|Yes          |Completed             |-           |
|tl.getTestSuitesForTestPlan|Yes          |Completed             |-           |
|tl.getTestCasesForTestPlan|Yes          |Implemented but API does not seem to be returning expected results for some of the many parameters passed into this method. For instance execution type works which is tested in PHP client sample but execution status does not seem to work. 'f' and 'p' return nothing. The PHP sample client does not seem to have tested this parameter.|-           |
|tl.getTestCaseIDByName|Yes          |Completed             |-           |
|tl.getTestCasesForTestSuite|Yes          |Completed             |-           |
|tl.getLastExecutionResult|Yes          |Completed             |-           |
|tl.getTestCaseCustomFieldDesignValue|No           |Not scheduled for support at this time|E           |
|tl.getTestCaseAttachments|No           |Not scheduled for support at this time|E           |

Setter Methods

|**Method**|**Supported**|**Development Status**|**Priority**|
|:---------|:------------|:---------------------|:-----------|
|tl.setTestMode|No           |Setting the mode to true causes the Java client to fail all test cases (not going to be supported)|F           |
|tl.addTestCaseToTestPlan|Yes          |Completed             |-           |
|tl.assignRequirements|No           |Not scheduled for support at this time|E           |