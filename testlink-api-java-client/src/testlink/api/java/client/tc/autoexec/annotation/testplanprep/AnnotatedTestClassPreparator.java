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
package testlink.api.java.client.tc.autoexec.annotation.testplanprep;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkTestPlan;
import testlink.api.java.client.tc.autoexec.TestPlanPrepare;

/**
 * This class prepares the TestPlan for the execution server and takes
 * as a parameter an annotated class
 * @author DPadilla
 *
 */
public class AnnotatedTestClassPreparator implements TestPlanPrepare {
	private String testClass;
	
	public AnnotatedTestClassPreparator(String testClass) {
		this.testClass = testClass;
	}
	
public void setExternalPath(String path) {
	
}

	public void setTCUser(String user) {
		
	}

	public TestLinkTestPlan adjust(
		TestLinkAPIClient apiClient,
		TestLinkTestPlan plan) throws TestLinkAPIException {
		parseTestClassAndCreateTestCaseExecutors(apiClient, plan);
		return plan;
	}
	
	/*
	 * This is what will do the brunt of the work of taking apart the
	 * TestClass with annotations and making executors for is test case 
	 * using the annotated class methods.
	 */
	private void parseTestClassAndCreateTestCaseExecutors(TestLinkAPIClient apiClient, TestLinkTestPlan plan) {
		
	}
}
