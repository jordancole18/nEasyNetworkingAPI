package me.jordancole.easynetworking.net.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.jordancole.easynetworking.net.packets.DisconnectHandler;
import me.jordancole.easynetworking.net.packets.Packet;
import me.jordancole.easynetworking.net.packets.PacketHandler;
import me.jordancole.easynetworking.net.packets.packets.ClientDisconnectPacket;
import me.jordancole.easynetworking.net.packets.packets.ConnectionPacket;

/**
 * 
 * <h1>Client</h1>
 * 
 * <p>
 * This allows you to connect to a specific ip and port
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public class Client {

	// whether or not the client is running.
	private boolean running;
	// this is the ip the client is attempting to connect to.
	private String ip;
	// this is the port the client is attempting to use.
	private int port;
	// this is the Datagram socket used for UDP connections.
	private DatagramSocket socket;

	// this is the Socket used for TCP connections.
	private Socket connection;
	// this is the message being received by a socket.
	private String message;
	// this is the output stream for sending info
	private ObjectOutputStream output;
	// this is the input stream for receiving info.
	private ObjectInputStream input;
	// this is all of the packet handlers.
	private List<PacketHandler> packetHandlers = new ArrayList<PacketHandler>();
	// this is all of the disconnect handlers
	private List<DisconnectHandler> disconnectHandlers = new ArrayList<DisconnectHandler>();

	// this is the Unique Identifier for the client.
	private UUID clientID;

	/**
	 * 
	 * @param ip   - The ip to the server.
	 * @param port - The port to the server.
	 * 
	 */
	public Client(String ip, int port) {
		this.running = true;
		this.ip = ip;
		this.port = port;
		clientID = UUID.randomUUID();
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startUDP();
		startTCP();
	}

	/**
	 * 
	 * Sets up UDP connections.
	 * 
	 */
	private void startUDP() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {

					}
					handlePacket(new String(packet.getData()));
				}
			}

		}).start();

	}

	/**
	 * 
	 * Sets up TCP connections.
	 * 
	 */
	private void startTCP() {
		try {

		} catch (Exception e) {

		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// init the connection
					connection = new Socket(ip, port);

					// setting up the streams
					output = new ObjectOutputStream(connection.getOutputStream());
					// making sure the output is cleaned out.
					output.flush();
					input = new ObjectInputStream(connection.getInputStream());
					// connecting TCP and UDP together to the same Client and Server object.
					sendTCP(new ConnectionPacket(clientID));
					sendUDP(new ConnectionPacket(clientID));
					do {
						try {
							// reading the incoming messages.
							message = (String) input.readObject();

							handlePacket(message);
						} catch (ClassNotFoundException var9) {

						}
					} while (running);
				} catch (Exception e) {
					// something happend so the client has disconnected
					System.out.println("DISCONNECTING.");
					for (DisconnectHandler disconnectHandler : disconnectHandlers) {
						disconnectHandler.onServerDisconnect();
					}
					running = false;
				}
			}

		}).start();
	}

	/**
	 * 
	 * Sends data via the UDP protocol.
	 * 
	 * @param data - via UDP protocol.
	 * 
	 */
	private void sendUDP(byte[] data) {
		if (socket.isClosed() || running == false)
			return;
		DatagramPacket packet = null;
		try {
			// inits the DatagramPacket
			packet = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			// sends the datagram packet.
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Sends a Packet via the UDP protocol.
	 * 
	 * @param packet - The packet to be sent vita UDP protocol.
	 */
	public void sendUDP(Packet packet) {
		// sends and formats the packet for easy parsing for the server.
		sendUDP((packet.getID() + ";" + clientID.toString() + ";" + new String(packet.getData())).getBytes());
	}

	/**
	 * 
	 * Sends a message via the TCP protocol.
	 * 
	 * @param message - The message to be sent via TCP protocol.
	 */
	private void sendTCP(String message) {
		try {
			// writes the object to the output stream.
			this.output.writeObject(message);
			// flushes the output stream.
			this.output.flush();
		} catch (IOException var3) {

		}

	}

	/**
	 * 
	 * Sends a packet via the TCP protocol.
	 * 
	 * @param packet - The packet to be sent via TCP protocol.
	 */
	public void sendTCP(Packet packet) {

		// sends and formats the packet for easy parsing for the server.
		sendTCP(packet.getID() + ";" + clientID.toString() + ";" + new String(packet.getData()));
	}

	/**
	 * 
	 * Stops the client.
	 * 
	 */
	public void stopClient() {
		this.sendTCP(new ClientDisconnectPacket(clientID));
		this.running = false;
	}

	/**
	 * 
	 * Handles an incoming message from the server and parses the packet.
	 * 
	 * @param message - The message to be handled.
	 */
	private void handlePacket(String message) {
		System.out.println("FROM SERVER TO CLIENT: " + message);
		String[] data = message.split(";");
		int id = Integer.parseInt(data[0]);
		int n = data.length - 1;
		String[] newArray = new String[n];
		System.arraycopy(data, 1, newArray, 0, n);
		for (PacketHandler packetHandler : getPacketHandlers()) {
			packetHandler.handleClientPacket(id, newArray, this);
		}
	}

	/**
	 * 
	 * Add a packet handler to the client.
	 * 
	 * @param packetHandler - The PacketHandler to be added.
	 */
	public void addPacketHandler(PacketHandler packetHandler) {
		if (this.packetHandlers.contains(packetHandler)) {
			System.err.println("This packet handler is already added...");
			return;
		}
		this.packetHandlers.add(packetHandler);
	}

	/**
	 * 
	 * Add a disconnect handler to the client.
	 * 
	 * @param disconnectHandler - The DisconnectHandler to be added.
	 */
	public void addClientDisconnectHandler(DisconnectHandler disconnectHandler) {
		if (this.disconnectHandlers.contains(disconnectHandler)) {
			System.err.println("This packet handler is already added...");
			return;
		}
		disconnectHandlers.add(disconnectHandler);
	}

	/**
	 * 
	 * @return the Packet Handler.
	 * 
	 */
	public List<PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	/**
	 * 
	 * @return the client ID
	 * 
	 */
	public UUID getID() {
		return clientID;
	}

}
