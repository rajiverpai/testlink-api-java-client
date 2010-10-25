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

import org.junit.Before;
import org.junit.Test;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.TestLinkAPIException;
import junit.framework.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 22/10/2010
 */
public class TestCreateTestCaseWithSteps 
extends TestCase
{

	protected String devKey = "de4d6af9582c63cf624910bb4c861939";
	protected String url = "http://localhost/testlink_cvs/lib/api/xmlrpc.php";
	
	private TestLinkAPIClient client;
	
	@Before
	public void setUp()
	{
		client = new TestLinkAPIClient(devKey, url);
	}
	
	@Test
	public void testCreateTestCaseWithSteps()
	{
		String authorLoginName = "admin";
		String projectName = "myproject";
		String suiteName = "mysuite";
		String testCaseName = "mytc2";
		String summary = "No Summary";
		String steps = "1, 2";
		String expectedResults = "1, 2";
		String importance = TestLinkAPIConst.MEDIUM;
		
		try
		{
			this.client.createTestCase(
					authorLoginName , 
					projectName, 
					suiteName, 
					testCaseName , 
					summary , 
					steps , 
					expectedResults , 
					importance );
		} catch (TestLinkAPIException e)
		{
			e.printStackTrace();
			fail("Failed to create test case with steps: " + e.getMessage());
		}
	}
	
}
