package me.jordancole.easynetworking.net.packets;

import me.jordancole.easynetworking.net.client.Client;
import me.jordancole.easynetworking.net.server.ServerConnection;

/**
 * 
 * <h1>Packet Handler</h1>
 * 
 * <p>
 * The PacketHandler class provides an easy way to handle incoming packets from
 * the client or server.
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public abstract class PacketHandler {

	/**
	 * 
	 * The packet handler for the client.
	 * 
	 * @param id     - The Unique Identifier of the client.
	 * @param data   - The data being handled.
	 * @param client - An instance of the Client.
	 */
	public abstract void handleClientPacket(int id, String[] data, Client client);

	/**
	 * 
	 * The packet handler for the server.
	 * 
	 * @param id         - The Unique Identifier of the Client.
	 * @param data       - The data being handled.
	 * @param connection - An instance of the Client on the Server Side via
	 *                   ServerConnection.
	 */
	public abstract void handleServerPacket(int id, String[] data, ServerConnection connection);

}
