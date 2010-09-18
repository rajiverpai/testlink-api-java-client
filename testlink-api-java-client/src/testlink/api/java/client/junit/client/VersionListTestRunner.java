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
package testlink.api.java.client.junit.client;

import java.util.HashMap;
import java.util.Iterator;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.junit.constants.TestConst;

public class VersionListTestRunner implements TestLinkAPIConst, TestConst
{
	private HashMap<String,
	TestLinkAPIClient> apiList = new HashMap();

	/**
     * The constructor sets up all known
     * testable cases via hard code.
     */
	public VersionListTestRunner() {
		TestLinkAPIClient api = new TestLinkAPIClient(userKey, api182URL, true);
		apiList.put("TestLink1.8.2", api);
		api = new TestLinkAPIClient(userKey, api185URL, true);
		apiList.put("TestLink1.8.5", api);
	}
	
	
	public RunExceptionResults runTest(String method, TestLinkTest test) {
		RunExceptionResults results = new RunExceptionResults();
	    
	    // Run the test for all testable versions
		try {
		    TestLinkAPIClient api;
		    String version;
			Iterator versions = apiList.keySet().iterator();
			while ( versions.hasNext() ) {
				version = (String) versions.next();
				api = apiList.get(version);
				try {
					test.runTest(version, api);
					results.addResult(version, null);
				} catch (Exception e) {
					results.addResult(version, e);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
		
		// Print out result summary
		System.out.println("\n\n=======================================================");
		Iterator versions = results.getVersions();
		while ( versions.hasNext() ) {
			String version = (String) versions.next();
			Exception e = results.getResultException(version);
			if ( e != null ) {
				System.err.println(method + ": " + version + " (FAILED) => " + e.getMessage());
			} else {
				System.out.println(method + ": " + version + " (SUCCEDED)");
			}
		}
		System.out.println("=======================================================\n");
		return results;
	}
}
