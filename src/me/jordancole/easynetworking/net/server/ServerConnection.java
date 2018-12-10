package me.jordancole.easynetworking.net.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

import me.jordancole.easynetworking.net.packets.Packet;
import me.jordancole.easynetworking.net.packets.PacketHandler;

/**
 * 
 * <h1>Server Connection</h1>
 * <p>
 * This is the connection that is handled by the server
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public class ServerConnection extends Thread {

	// server instance
	private Server server;
	// socket connection to the client
	private Socket socket;
	// the ip address of the client.
	private InetAddress ipAddress;
	// the port of the connection
	private int port;
	// the output stream for sending info.
	private ObjectOutputStream output;
	// the input stream for receiving info.
	private ObjectInputStream input;
	// the unique identifier of the connection
	private UUID uuid;
	// the latest UDP datagram packet received.
	private DatagramPacket latestDatagramPacket;

	/**
	 * 
	 * @see java.lang.Thread
	 * @param server - The server in which the ServerConnection is being made to.
	 * @param connected - The incoming Socket.
	 * 
	 */
	public ServerConnection(Server server, Socket connected) {
		this.server = server;
		this.socket = connected;
		this.ipAddress = socket.getInetAddress();
		this.port = socket.getPort();
	}

	/**
	 * 
	 * @see java.lang.Thread#run()
	 * 
	 */
	public void run() {
		String message = "";
		/*
		 * 
		 * checks if the input and output streams are null and if so initializes them.
		 * 
		 */
		if (this.output == null || this.input == null) {
			try {
				this.output = new ObjectOutputStream(this.socket.getOutputStream());
				this.output.flush();
				this.input = new ObjectInputStream(this.socket.getInputStream());
			} catch (IOException var10) {

			}
		}

		while (true) {

			do {
				try {

					if (ipAddress.isReachable(1000) == false) {
						System.out.println("CLIENT DISCONNECTED: " + uuid.toString());
						server.disconnect(this);
						this.interrupt();
						break;
					}

					if (this.input != null) {
						// gets the message from the input stream
						message = (String) this.input.readObject();
						try {
							handlePacket(message);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} catch (ClassNotFoundException var11) {

				} catch (IOException var12) {

				}
			} while (!message.equalsIgnoreCase("stopserverend"));

		}
	}

	/**
	 * 
	 * Handles the packet being received.
	 * 
	 * @param message - The message to be handled.
	 */
	public void handlePacket(String message) {
		System.out.println("HANDLE MESSAGE: " + message);

		String[] data = message.split(";");
		int id = Integer.parseInt(data[0]);
		int n = data.length - 1;

		if (id == 1) {
			this.uuid = UUID.fromString(data[1]);
		}

		String[] newArray = new String[n];
		System.arraycopy(data, 1, newArray, 0, n);
		for (PacketHandler packetHandler : server.getPacketHandlers()) {
			packetHandler.handleServerPacket(id, newArray, this);
		}
	}

	/**
	 * 
	 * @return the address of the connection
	 * 
	 */
	public InetAddress getAddress() {
		return ipAddress;
	}

	/**
	 * 
	 * @return the port of the connection
	 * 
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @return the object output stream
	 * 
	 */
	public ObjectOutputStream getOutputStream() {
		return output;
	}

	/**
	 * 
	 * @return the object input stream
	 * 
	 */
	public ObjectInputStream getInputStream() {
		return input;
	}

	/**
	 * 
	 * Sends a packet via the TCP protocol.
	 * 
	 * @param packet - The packet to be sent via the TCP protocol.
	 */
	public void sendTCP(Packet packet) {
		sendTCP(new String(packet.getData()));
	}

	/**
	 * 
	 * Sends a packet via the UDP protocol.
	 * 
	 * @param packet - The packet to be sent via the UDP protocol.
	 */
	public void sendUDP(Packet packet) {
		sendUDP(new String(packet.getData()));
	}

	/**
	 * 
	 * Sends a message via the TCP protocol.
	 * 
	 * @param message - The message to be sent via the TCP protocol.
	 */
	private void sendTCP(String message) {
		if (socket.isConnected() == false) {
			try {
				socket.close();
			} catch (IOException e) {
			}
			return;
		}
		try {
			this.output.writeObject(message);
			this.output.flush();
		} catch (IOException var3) {
		}

	}

	/**
	 * 
	 * Sends a message via the UDP protocol.
	 * 
	 * @param message - The message to be sent via the UDP protocol.
	 */
	private void sendUDP(String message) {

		if (latestDatagramPacket == null) {
			System.err.println("UDP NOT SETUP");
			return;
		}

		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
				latestDatagramPacket.getAddress(), latestDatagramPacket.getPort());
		try {
			server.getDatagramSocket().send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the unique identifier of the connection.
	 * 
	 */
	public UUID getID() {
		return uuid;
	}

	/**
	 * 
	 * Sets the latest UDP datagram packet.
	 * 
	 * @param packet - The latest packet received via UDP protocol.
	 */
	public void setDatagramPacket(DatagramPacket packet) {
		latestDatagramPacket = packet;
	}

	/**
	 * 
	 * @return the latest UDP datagram packet
	 * 
	 */
	public DatagramPacket getDatagramPacket() {
		return latestDatagramPacket;
	}

}
