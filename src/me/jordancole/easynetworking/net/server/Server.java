package me.jordancole.easynetworking.net.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.jordancole.easynetworking.net.packets.DisconnectHandler;
import me.jordancole.easynetworking.net.packets.PacketHandler;

/**
 * 
 * <h1>Server</h1>
 * 
 * <p>
 * The Server class creates a server which clients can connect to
 * </p>
 * 
 * @author Jordan Cole
 * @version 1.0
 * @since 12/9/18
 *
 */
public class Server {

	// the datagram socket for udp connections.
	private DatagramSocket socket;
	// the server socket for hosting the tcp server.
	private ServerSocket serverSocket;
	// the socket for the tcp conenctions.
	private Socket acceptingSocket;
	// the port of the server
	private int port;
	// if the server is running or not
	private boolean running;
	// all of the connected clients aka ServerConnections
	private List<ServerConnection> connectedClients = new ArrayList<ServerConnection>();
	// a list of all PacketHandlers
	private List<PacketHandler> packetHandlers = new ArrayList<PacketHandler>();
	// a list of all DisconnectHandlers
	private List<DisconnectHandler> disconnectHandlers;

	/**
	 * 
	 * This initializes the UDP and TCP Servers.
	 * 
	 * @param port - The port the server is being hosted on.
	 */
	public Server(int port) {
		this.port = port;
		startUDP();
		startTCP();
	}

	/**
	 * 
	 * Starts the UDP server for awaiting connections.
	 * 
	 */
	private void startUDP() {
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String message = new String(packet.getData()).trim();

					System.out.println("IN UDP STAGE: " + message);

					String[] msgData = message.split(";");

					UUID id = UUID.fromString(msgData[0]);

					ServerConnection sc = null;

					for (ServerConnection connection : connectedClients) {
						if (connection.getID() != null) {
							if (connection.getID().toString().equalsIgnoreCase(id.toString())) {
								sc = connection;
							}
						}
					}

					if (sc != null) {
						sc.setDatagramPacket(packet);
						sc.handlePacket(message);
					} else {
						System.err.println("INVALID UDP SERVER CONNECTION!");
					}
				}
			}

		}).start();
	}

	/**
	 * 
	 * Starts the TCP server for awaiting connections.
	 * 
	 */
	public void startTCP() {
		final Server s = this;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(port, 10);
					while (true) {
						try {
							acceptingSocket = serverSocket.accept();
						} catch (IOException e) {
							System.out.println("I/O error: " + e);
						}
						ServerConnection thread = new ServerConnection(s, acceptingSocket);
						thread.start();
						connectedClients.add(thread);
					}
				} catch (IOException localIOException1) {
				}
			}
		});
		t.start();

	}

	/**
	 * 
	 * The latest datagram socket.
	 * 
	 * @return Datagram Socket
	 */
	public DatagramSocket getDatagramSocket() {
		return socket;
	}

	/**
	 * 
	 * Adds a packet handler.
	 * 
	 * @param packetHandler - The packet handler to be added.
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
	 * Adds a DisconnectHandler
	 * 
	 * @param disconnectHandler - The disconnect handler to be added.
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
	 * This returns a list of all packet handlers.
	 * 
	 * @return a list of packet handlers.
	 */
	public List<PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	/**
	 * 
	 * Disconnects a client from the server This is server a server sided
	 * disconnect.
	 * 
	 * @param serverConnection - The ServerConnection thats connection was
	 *                         terminated.
	 */
	public void disconnect(ServerConnection serverConnection) {
		try {
			if (this.connectedClients.contains(serverConnection)) {
				this.connectedClients.remove(serverConnection);
			}
			for (DisconnectHandler disconnectHandler : disconnectHandlers) {
				disconnectHandler.onClientDisconnect(serverConnection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
