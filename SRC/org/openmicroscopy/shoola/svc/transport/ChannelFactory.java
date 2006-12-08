/*
 * org.openmicroscopy.shoola.svc.transport.ChannelFactory 
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.svc.transport;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * Helper class to create commnucation links.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ChannelFactory
{

	/**
	 * Creates a <code>HttpChannel</code> corresponding to the passed type.
	 * 
	 * @param type			The channel type.
	 * @param url			The server's url.
	 * @param connTimeout	The time before being disconnected.
	 * @return See above.
	 * @throws IllegalArgumentException If the specified type is not supported.
	 */
    public static HttpChannel getChannel(int type, String url, int connTimeout) 
		throws IllegalArgumentException
	{
		HttpChannel channel = null;
		
		//We only have one channel now, this switch is more of a stub for
		//future development.
		switch (type) {
			case HttpChannel.CONNECTION_PER_REQUEST:
				channel = new BasicChannel(url, connTimeout);
				break;
			case HttpChannel.DEFAULT:
				channel = new BasicChannel(url, connTimeout);
				break;
			default:
				throw new IllegalArgumentException(
				"Unrecognized channel type: "+type+".");
		}
		return channel;
	}
    
    
}
