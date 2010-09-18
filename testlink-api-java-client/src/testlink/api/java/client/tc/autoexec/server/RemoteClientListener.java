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

public interface RemoteClientListener {
	
	/**
	 * Called when the client send a request to the server.
	 * 
	 * @param request
	 */
	public void sentMessage(String output);

	/**
	 * Called when the client receives a result from the server.
	 * 
	 * @param input
	 */
	public void receivedMessage(String input);
	
	/**
	 * Called when a shutdown is received from the server
	 * or the socket shows that it is closed.
	 * 
	 */
	public void receivedShutdown();
}
