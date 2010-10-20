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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


/**
 * The opens a connection to a port. 
 * 
 * @author Daniel Padilla
 *
 */
public class RemoteClientConnection
{
	private Socket fSocket = null;
	private PrintWriter messageSend = null;
	private BufferedReader messageReceive = null;
	private int port = -1;
	private Map messageCache = new HashMap();
	private boolean cacheLocked = false;
	private Random randomizer = new Random(new Date().getTime());
	private boolean isClosed = false;
	private ArrayList listeners = new ArrayList();
	
	/**
	 * Restrict to the package.
	 * 
	 * @param port
	 */
	RemoteClientConnection(
		int port)
	{
		this.port = port;
	}
	
	public void addListener(
		RemoteClientListener listener)
	{
		listeners.add(listener);
	}
	
	public void openConnection() throws Exception
	{
		int retry = 0;
		while ( retry < 3 ) {
			try {
				try {
					fSocket = new Socket("localhost", port);
					messageSend = new PrintWriter(fSocket.getOutputStream(), true);
					messageReceive = new BufferedReader(
						new InputStreamReader(fSocket.getInputStream()));
					RemoteClientConnectionMonitor monitor = new RemoteClientConnectionMonitor(
						this);
					monitor.start();
					break;
				} catch ( UnknownHostException e ) {
					ExecutionProtocol.debug("Don't know about host: localhost.");
					shutdownNotify();
					throw e;
				} catch ( IOException e ) {
					ExecutionProtocol.debug(
						"Couldn't get I/O for the connection to: localhost. Port:" + port);
					shutdownNotify();
					throw e;
				} catch ( Exception e ) {
					ExecutionProtocol.debug("Couldn't get connection established. Port: " + port);
					shutdownNotify();
					throw e;
				}
			} catch ( Exception e ) {
				retry++;
				if ( retry >= 3) {
					throw e;
				}
				try {
					Thread.sleep(1500);
				} catch ( Exception ee ) {}
			}
		}
	}
		
	/**
	 * Close all the connections. No exception is thrown. The exceptions
	 * are ignored in case the other side is already closed.
	 */
	public void closeConnection()
	{
		try {
			messageSend.close();
		} catch ( Throwable e ) {}
			
		try {
			messageReceive.close();
		} catch ( Throwable e ) {}
		
		try {
			fSocket.close();
		} catch ( Throwable e ) {}
		
		isClosed = true;
		shutdownNotify();
	}
	
	/**
	 * Send a message to the server.
	 * 
	 * @param clientName
	 * @param message
	 */
	public void sendMessage(
		String clientName,
		String message)
	{
		String sendMsg = clientName + ExecutionProtocol.STR_CLIENT_SEPARATOR + message;
		messageSend.println(sendMsg);	
		for ( int i = 0; i < listeners.size(); i++ ) {
			RemoteClientListener listener = (RemoteClientListener) listeners.get(i);
			listener.sentMessage(sendMsg);
		}
	}
	
	/**
	 * Read a message from the cache for a specific client.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String receiveMessage(
		String client) throws Exception
	{
		if ( isClosed ) {
			throw new Exception("The connections is closed and cannot be accessed.");
		}
		
		sleepRandom();
		waitForCache();
		cacheLocked = true;
		try {
			Vector messages = (Vector) messageCache.get(client);
			if ( messages != null ) {
				if ( messages.size() != 0 ) {
					String fromServer = (String) messages.get(0);
					messages.remove(0);
					cacheLocked = false;
					return fromServer;
				}
			}
		} catch ( Throwable e ) {
			e.printStackTrace();
			cacheLocked = false;
			throw new Exception(e);
		}
		cacheLocked = false;
		return null;
	}
	
	/**
	 * Pop a message from the socket comming from the server.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String popMessage() throws Exception
	{
		String receive = messageReceive.readLine();
		return receive;
	}
	
	/**
	 * True if the connection is good
	 * @return
	 */
	public boolean isGood()
	{
		return (fSocket.isConnected() && !fSocket.isClosed() && !isClosed);
	}
	
	/**
	 * Return the port for this connection
	 * 
	 * @return
	 */
	public int getPort()
	{
		return port;
	}
	
	public void cacheMessage(
		String fromServer)
	{
		// No client is the target so no need to cache
		if ( fromServer.indexOf(ExecutionProtocol.STR_CLIENT_SEPARATOR) < 1 ) {
			return;
		}
		
		// Get the client identification information. Must have client target to cache.
		String client = fromServer.substring(0,
			fromServer.indexOf(ExecutionProtocol.STR_CLIENT_SEPARATOR));

		// Cache the data for the client
		waitForCache();
		cacheLocked = true;
		try {
			Vector messages = (Vector) messageCache.get(client);
			if ( messages == null ) {
				messages = new Vector();
				messageCache.put(client, messages);
			}
			messages.add(fromServer);
			for ( int i = 0; i < listeners.size(); i++ ) {
				RemoteClientListener listener = (RemoteClientListener) listeners.get(i);
				listener.receivedMessage(fromServer);
			}
		} catch ( Throwable e ) {
			e.printStackTrace();
			cacheLocked = false;
		}
		cacheLocked = false;
	}
	
	/*
	 * Private methods
	 */
	private void waitForCache()
	{
		try {
			while ( cacheLocked == true ) {
				sleepRandom();
			}
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Called by message receivers to try to keep consumers on different schedule.
	 */
	private void sleepRandom()
	{
		try {
			int sleep = randomizer.nextInt(97) + 1;
			Thread.sleep(sleep);
		} catch ( Throwable e ) {}
	}
	
	/*
	 * Let the listeners know about the shutdown
	 */
	private void shutdownNotify()
	{
		for ( int i = 0; i < listeners.size(); i++ ) {
			RemoteClientListener listener = (RemoteClientListener) listeners.get(i);
			listener.receivedShutdown();
		}
	}
}
