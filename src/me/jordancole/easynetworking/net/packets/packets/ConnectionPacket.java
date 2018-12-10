package me.jordancole.easynetworking.net.packets.packets;

import java.util.UUID;

import me.jordancole.easynetworking.net.packets.Packet;

/**
 * 
 * <h1>ConnectionPacket</h1>
 * 
 * <p>
 * The ConnectionPacket is a packet that is used for initializing the connection
 * between the Client and Server.
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public class ConnectionPacket extends Packet {

	// the unique identifier of the client.
	private UUID uuid;

	/**
	 * 
	 * Initializes the packet.
	 * 
	 * @param id - The Unique Identifier of the client.
	 */
	public ConnectionPacket(UUID id) {
		super(1);
		this.uuid = id;
	}

	@Override
	public void parsePacket(String data) {
		String[] arrayData = data.split(";");
		uuid = UUID.fromString(arrayData[0]);
	}

	@Override
	public byte[] getData() {
		return (uuid.toString() + ";").getBytes();
	}

}
