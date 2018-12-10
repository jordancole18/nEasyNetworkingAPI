package me.jordancole.easynetworking.net.packets;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <h1>Packet</h1>
 * 
 * <p>
 * The Packet class provides a wrapper class for the sending of data between the
 * Server and the Client.
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public abstract class Packet {

	// a list of all packet classes.
	private static List<Class<?>> allPacketTypes = new ArrayList<Class<?>>();

	// the id of the packet
	private int id;

	/**
	 * 
	 * Initalizes the packet.
	 * 
	 * @param id - the id of the packet.
	 */
	public Packet(int id) {
		this.id = id;
	}

	/**
	 * 
	 * The {@link #getID()} packet returns the packets id.
	 * 
	 * @return the packet id
	 * 
	 */
	public int getID() {
		return id;
	}

	/**
	 * 
	 * Parses the data of a packet.
	 * 
	 * @param data - The data to be parsed.
	 */
	public abstract void parsePacket(String data);

	/**
	 * 
	 * This is the byte[] data of a message.
	 * 
	 * @return byte[]
	 */
	public abstract byte[] getData();

	/**
	 * 
	 * Registers a class as a packet.
	 * 
	 * @param packetClass - The class that extends Packet.
	 */
	public static void registerPacket(Class<?> packetClass) {
		allPacketTypes.add(packetClass);
	}

	/**
	 * 
	 * This gets a Packet based on the ID of it.
	 * 
	 * @param id - The id of the packet
	 * @return packet - The packet that was found. Can be null.
	 */
	public Packet getPacketType(int id) {

		for (Class<?> packet : allPacketTypes) {
			Packet p = (Packet) packet.cast(Packet.class);
			if (p.getID() == id)
				return p;
		}

		return null;
	}

}
