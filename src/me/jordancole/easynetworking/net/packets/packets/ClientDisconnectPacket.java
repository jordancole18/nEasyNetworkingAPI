package me.jordancole.easynetworking.net.packets.packets;

import java.util.UUID;

import me.jordancole.easynetworking.net.packets.Packet;

/**
 * 
 * <h1>ClientDisconnectPacket</h1>
 * 
 * <p>
 * The ClientDisconnectPacket is a packet that is used for terminating the
 * connection between the Client and Server.
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public class ClientDisconnectPacket extends Packet {

	//the unique identifier of the client
	private UUID uuid;

	/**
	 * 
	 * Initializes the packet.
	 * 
	 * @param uuid - The Unique Identifier of the client.
	 */
	public ClientDisconnectPacket(UUID uuid) {
		super(2);
		this.uuid = uuid;
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
