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


/**
 * The exception is used to indicate the conditions under which a failure
 * occurred during a call to a TestLink API Java Client method.
 * 
 * @author Daniel Padilla
 *
 */
public class TestLinkAPIException extends Exception
{

	/**
	 * Create exception with a message.
	 * 
	 * @param msg
	 */
	public TestLinkAPIException(
		String msg)
	{
		super(msg);
	}

	/** 
	 * Create a nested exception with a new message.
	 * 
	 * @param msg
	 * @param e
	 */
	public TestLinkAPIException(
		String msg,
		Throwable e)
	{
		super(msg, e);
	}
}
