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


/**
 * Intended to hold the information of an existing test suite in
 * the TestLink database. That is why the only way to instantiate
 * the class is using a Map to pass the suite information returned 
 * by the TestLink API.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkTestSuite {
	private String suiteName;
	private Integer suiteID;
	private boolean isOfflineVersion=false;

	/**
	 * Used to create offline dummy suite
	 */
	private TestLinkTestSuite() {
		suiteName = "Offline suite";
		suiteID = new Integer(-1);
		isOfflineVersion = true;
	}
	
	/**
	 * Returns a new copied instance of the test suite
	 * 
	 * @param other
	 */
	public TestLinkTestSuite(TestLinkTestSuite other) {
		
		if ( suiteName != null ) {
			this.suiteName = new String(other.suiteName);
		}
		
		if ( suiteID != null ) {
			this.suiteID = new Integer(other.suiteID.intValue());
		}
		
		this.isOfflineVersion=other.isOfflineVersion;
	}
	
	/**
	 * Constructs a TestSuite instance when provided with information
	 * about the the suite using a Map result from the TestLink API
	 * for a suite. 
	 * 
	 * @param suiteInfo
	 * @throws TestLinkAPIException
	 */
	public TestLinkTestSuite(
		Map<Object, Object> suiteInfo) throws TestLinkAPIException
	{
		if ( suiteInfo == null ) {
			throw new TestLinkAPIException("The TestSuite class object instance could not be created.");
		}
		
		// Suite Name
		Object value = suiteInfo.get(TestLinkAPIConst.API_RESULT_NAME);
		if ( value == null ) {
			throw new TestLinkAPIException(
				"The setter does not allow null values for suite name.");
		} else {
			this.suiteName = value.toString();
		}
		
		// Identifier
		value = suiteInfo.get(TestLinkAPIConst.API_RESULT_IDENTIFIER);
		if ( value == null ) {
			throw new TestLinkAPIException(
			"The setter does not allow null values for suite identifier.");
		} else {
			this.suiteID = new Integer(value.toString());
		}
		
	}
	
	/**
	 * Currently not supported (method stub).
	 * <p>
	 * Get the name of the suite with which the test case is associated.
	 * 
	 * @return
	 */
	public String getSuiteName()
	{
		return suiteName;
	}
	

	/**
	 * Currently not supported (method stub).
	 * <p>
	 * Get the internal identifier of the suite with which the test case is associated.
	 * 
	 * @return
	 */
	public Integer getSuiteID()
	{
		return suiteID;
	}
	
	/**
	 * True if it is the offline version of a suite
	 * 
	 * @return
	 */
	public boolean isOfflineVersion() {
		return isOfflineVersion;
	}
	
	/**
	 * Returns an offline dummy version of the TestSuite
	 * @return
	 */
	public static TestLinkTestSuite getOfflineTestSuite() {
		return new TestLinkTestSuite();
	}

}
