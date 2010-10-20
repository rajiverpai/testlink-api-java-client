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

import java.util.Comparator;

import testlink.api.java.client.TestCase;

/**
 * The compartor determines the compare based on TestCase
 * execution order.
 * 
 * @author Daniel Padilla
 *
 */
public class TestCaseExecOrderCompare implements Comparator {
	public int compare(Object o1, Object o2) {
		
		// Both null
		if ( o1 == null && o2 == null ) {
			return 0;
		}
		
		// Null < value
		if ( o1 == null && o2 != null ) {
			return -1;
		}
		
		// value > null
		if ( o1 != null && o2 == null ) {
			return 1;
		}
		
		if ( !(o1 instanceof TestCase) ) {
			throw new ClassCastException("The object is not of type TestCase.");
		}
		
		if ( !(o2 instanceof TestCase) ) {
			throw new ClassCastException("The object is not of type TestCase.");
		}
		
		TestCase tc1 = (TestCase) o1;
		TestCase tc2 = (TestCase) o2;
		
		if ( tc1.getExecOrder() == tc2.getExecOrder() ) {
			if (  tc1.getTestCaseVisibleID() != null && tc2.getTestCaseVisibleID() != null ) {
				return tc1.getTestCaseVisibleID().compareTo(tc2.getTestCaseVisibleID());
			} else {
				return tc1.getTestCaseName().compareTo(tc2.getTestCaseName());
			}
		} else {
			Integer i1 = new Integer(tc1.getExecOrder());
			Integer i2 = new Integer(tc2.getExecOrder());
			return i1.compareTo(i2);
		}
	}
}
