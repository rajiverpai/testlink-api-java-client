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


import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.junit.constants.TestConst;
import testlink.api.java.client.tc.autoexec.example.RandomTestResultPrep;
import testlink.api.java.client.tc.autoexec.server.ExecutionServer;

public class RemoteServerTest {
	private static TestLinkAPIClient api;

	public static void main(String[] args) throws Exception
	{
		try {
			api = new TestLinkAPIClient(TestConst.userKey, TestConst.api182URL, true);
			api.ping();
			int port = 59168;
			System.out.println(port);
			RandomTestResultPrep prep = new RandomTestResultPrep();
			ExecutionServer server = new ExecutionServer(port, api, prep, null, null);
			server.start();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}		
}
