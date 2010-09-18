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


import java.util.HashMap;
import java.util.Map;


public class RemoteConnectionManager
{
	private static Map connections = new HashMap();
	
	public static RemoteClientConnection getOrCreateConnection(
		int port)
	{
		RemoteClientConnection conn = (RemoteClientConnection) connections.get(port);
		if ( conn == null ) {
			conn = new RemoteClientConnection(port);
			try {
				conn.openConnection();
				connections.put(port, conn);
			} catch ( Exception e ) {
				e.printStackTrace();
			}	
		}
		return conn;
	}
	
	public static void closeClientConnection(
		int port)
	{
		RemoteClientConnection conn = (RemoteClientConnection) connections.get(port);
		if ( conn != null ) {
			conn.closeConnection();
		}
	}
	
}
