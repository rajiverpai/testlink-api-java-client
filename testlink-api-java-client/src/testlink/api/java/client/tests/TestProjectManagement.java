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
package testlink.api.java.client.tests;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 21/10/2010
 */
public class TestProjectManagement 
extends TestCase
{

	protected String devKey = "831e2e35461fc7dd381e0f5c762cddf5";
	protected String url = "http://localhost/testlink19/lib/api/xmlrpc.php";
	
	private TestLinkAPIClient client;
	
	private String projectName = "testlink-api-java-client-test-project";
	private String testCasePrefix = "tlj";
	private String description = "Test Project for testlink-api-java-client";
	
	private Integer createdProjectId = 0;
	
	@Before
	public void setUp()
	{
		client = new TestLinkAPIClient(devKey, url);
	}
	
	@Test
	public void testCreateSimpleProject()
	{
		try
		{
			createdProjectId = client.createTestProject(projectName, testCasePrefix, description, true, true, true, true, false, false);
			assertTrue( createdProjectId > 0);
		} catch (TestLinkAPIException e)
		{
			e.printStackTrace();
			fail("Failed to ping: " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteSimpleProject()
	{
		//assertTrue(createdProjectId > 0);
		
		
	}
	
}
