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
package testlink.api.java.client.tc.autoexec.server;


/**
 * Monitors a server for messages and pops the messages from 
 * the socket and stores the results with the connection.
 * 
 * @author Daniel Padilla
 *
 */
public class RemoteClientConnectionMonitor extends Thread
{
	private RemoteClientConnection conn;
	
	public RemoteClientConnectionMonitor(
		RemoteClientConnection conn)
	{
		this.conn = conn;
	}
	
	public void run()
	{
		ExecutionProtocol ep = new ExecutionProtocol();
		try {
			String fromServer;
			while ( (fromServer = conn.popMessage()) != null ) {
				ep.processInput("monitor", fromServer);
				if ( ep.shutdown() ) {
					conn.cacheMessage(fromServer);
					conn.closeConnection();
					break;
				} else {
					conn.cacheMessage(fromServer);
				}
			}
		} catch ( Exception e ) {
			ExecutionProtocol.debug("Monitor failed with exeception. " + e.toString());
			conn.closeConnection();
		}
	}
}
