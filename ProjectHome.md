Welcome to the new and improved TestLink API Java Client implementation. This project is an client that interfaces with the TestLink php API via XML-RPC. For additional information on the project please see the wiki entries for the project.

The Java Client currently supports.

  * Test project creation
  * Test suite creation
  * Test case creation
  * Test build creation
  * Adding test cases to a test plan
  * Test case reporting

**Important changes to the API from the old dbfacade-testlink-rpc-api**

Classes have moved and been renamed as follows:

  * ExecutableTestCase
    * Old: testlink.api.java.client.tc.autoexec.ExecutableTestCase
    * New: testlink.api.java.client.TestLinkTestCase
  * TestPlan
    * Old: testlink.api.java.client.tc.autoexec.TestPlan
    * New: testlink.api.java.client.TestLinkTestPlan
  * TestSuite
    * Old: testlink.api.java.client.tc.autoexec.TestSuite
    * New: testlink.api.java.client.TestLinkTestSuite
  * TestProject
    * Old: testlink.api.java.client.tc.autoexec.TestProject
    * New: testlink.api.java.client.TestLinkTestProject