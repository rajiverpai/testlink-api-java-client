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
package testlink.api.java.client.tc.autoexec;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import testlink.api.java.client.TestCase;


/**
 * A registry of test cases.
 * 
 * @author Daniel Padilla
 *
 */
public class TestCaseRegistry
{
	Comparator orderByExecOrder = new TestCaseExecOrderCompare();
	ArrayList caseList = new ArrayList();
	
	/**
	 * Removes all TestCases from this registry
	 */
	public void clear()
	{
		caseList.clear();
	}
 
	/**
	 * Check to see if the test case is contained in the registry by TestCase object.
	 * 
	 * @param testCase
	 * @return
	 */
	public boolean contains(
		TestCase testCase)
	{
		int idx = find(testCase);
		if ( idx < 0 ) {
			return false;
		}
		return true;
	}
    
	/**
	 * Check to see if the TestCase is contained in the registry by case name or visible identifier.
	 * 
	 * @param caseNameOrVisibleID
	 * @return
	 */
	public boolean contains(
		String caseNameOrVisibleID)
	{
		if ( find(caseNameOrVisibleID) > -1 ) {
			return true;
		}
		return false;
	}

	/**
	 * Check to see if the test cases is contained in the registry by internal identifier.
	 * 
	 * @param internalID
	 * @return
	 */
	public boolean contains(
		Integer internalID)
	{
		if ( find(internalID) > -1 ) {
			return true;
		}
		return false;
	}
    
	/**
	 * Get the TestCase by name for visible identifier.
	 * 
	 * @param caseNameOrVisibleID
	 * @return
	 */
	public TestCase get(
		String caseNameOrVisibleID)
	{
		int idx = find(caseNameOrVisibleID);
		return (TestCase) caseList.get(idx);
	}
        
	/**
	 * Get the TestCase by internal identifier.
	 * 
	 * @param internalID
	 * @return
	 */
	public TestCase get(
		Integer internalID)
	{
		int idx = find(internalID);
		return (TestCase) caseList.get(idx);
	}
	
	/**
	 * Get a TestCase by registry index.
	 * 
	 * @param index
	 * @return
	 */
	public TestCase get(
		int index)
	{
		return (TestCase) caseList.get(index);
	}
	    
	/**
	 * Check to see if the registry is empty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return caseList.isEmpty();
	}

	/**
	 * Add a test case to the registry
	 * 
	 * @param testCase
	 */
	public void put(
		TestCase testCase)
	{
		int idx = find(testCase);
		if ( idx < 0 ) {
			caseList.add(testCase);
		} else {
			caseList.set(idx, testCase);
		}
		Collections.sort(caseList, orderByExecOrder);
	}

	/**
	 * Remove a test case from the registery
	 * 
	 * @param testCase
	 */
	public void remove(
		TestCase testCase)
	{
		int idx = find(testCase);
		if ( idx > -1 ) {
			caseList.remove(idx);
		}
		Collections.sort(caseList, orderByExecOrder);
	}
    
	/**
	 * Get the the number of entries in the registry.
	 * 
	 * @return
	 */
	public int size()
	{
		return caseList.size();
	}
	
	public TestCase[] toArray() {
		int count = caseList.size();
		TestCase cases[] = new TestCase[count];
		for (int i=0; i < caseList.size(); i++) {
			TestCase tc = (TestCase) caseList.get(i);
			cases[i] = tc;
		}
		return cases;
	}

	/*
	 * Private Methods
	 */
    
	/*
	 * Returns the index if the record is found
	 */
	public int find(
		TestCase testCase)
	{
    	
		// Internal id is most reliable and takes priority
		int idx = find(testCase.getTestCaseInternalID());
    	
		// Now search by Visible ID and Name
		if ( idx < 0 ) {
			idx = find(testCase.getTestCaseVisibleID());
			if ( idx < 0 ) {
				idx = find(testCase.getTestCaseName());
			} 
		}
		return idx;
	}

	private int find(
		String caseNameOrVisibleID)
	{
    	
		// Visible ID takes priority over the name
		for ( int i = 0; i < caseList.size(); i++ ) {
			TestCase tcase = (TestCase) caseList.get(i);
			if ( isEqual(tcase.getTestCaseVisibleID(), caseNameOrVisibleID) ) {
				return i;
			}
		}
    	
		// Search by name
		for ( int i = 0; i < caseList.size(); i++ ) {
			TestCase tcase = (TestCase) caseList.get(i);
			if ( isEqual(tcase.getTestCaseName(), caseNameOrVisibleID) ) {
				return i;
			}
		}
		return -1;
	}
    
	/*
	 * Returns the index if the record is found
	 */
	public int find(
		Integer internalID)
	{
		for ( int i = 0; i < caseList.size(); i++ ) {
			TestCase tcase = (TestCase) caseList.get(i);
			if ( isEqual(tcase.getTestCaseInternalID(), internalID) ) {
				return i;
			}
		}
		return -1;
	}
    
	/*
	 * Check the objects for equal value. Undefined nulls result in false match.
	 */
	private boolean isEqual(
		Object o1,
		Object o2)
	{
		if ( o1 == null || o2 == null ) {
			return false;
		}
		return o1.equals(o2);
	}
    
}
