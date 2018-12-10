package me.jordancole.easynetworking.net.packets;

import me.jordancole.easynetworking.net.server.ServerConnection;

/**
 * 
 * <h1>DisconnectHandler</h1>
 * 
 * <p>
 * The DisconnectHandler handles the terminations of a connection between the
 * Server and Client.
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public abstract class DisconnectHandler {

	/**
	 * 
	 * This is run when a client disconnects from a server. The Server receives
	 * this.
	 * 
	 * @param serverConnection - The Client that has disconnected Server Side.
	 */
	public abstract void onClientDisconnect(ServerConnection serverConnection);

	/**
	 * 
	 * This is run when the connection is has been terminated. The Client receives
	 * this.
	 * 
	 */
	public abstract void onServerDisconnect();

}
