/**
 * 
 */
package testlink.api.java.client.junit.autoexec;

import org.junit.Before;
import org.junit.Test;

import testlink.api.java.client.TestLinkAPIClient;
import junit.framework.TestCase;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class TestCustomField 
extends TestCase
{
	protected String devKey = "831e2e35461fc7dd381e0f5c762cddf5";
	protected String url = "http://localhost/testlink19/lib/api/xmlrpc.php";
	
	protected TestLinkAPIClient client;
	
	@Before
	public void setUp()
	{
		client = new TestLinkAPIClient(devKey, url);
	}
	
	@Test
	public void testRetrieveCustomField()
	{
		
	}

}
