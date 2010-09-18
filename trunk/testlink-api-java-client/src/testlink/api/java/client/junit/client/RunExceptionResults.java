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


public class RunExceptionResults
{
	private HashMap<String,
		Exception> results = new HashMap();
	private boolean hasFailure=false;
	private Exception latestFailure=null;
	private String latestFailedVersion;

	public void addResult(
		String version,
		Exception e)
	{
		if ( e != null ) {
			hasFailure = true;
			latestFailure=e;
			latestFailedVersion=version;
		}
		results.put(version, e);
	}

	public Iterator getVersions()
	{
		return results.keySet().iterator();
	}

	public Exception getResultException(
		String version)
	{
		return (Exception) results.get(version);
	}
	
	public boolean containsFailure() {
		return hasFailure;
	}
	
	public Exception getLatestFailure() {
		return latestFailure;
	}
	
	public String getLatestFailedVersion() {
		return latestFailedVersion;
	}
}
