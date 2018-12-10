# nEasyNetworkingAPI

This API allows basic Server and Client connections via the TCP and UDP protocols in Java.

## Usage
# Server
```java
import me.jordancole.easynetworking.net.packets.DisconnectHandler;
import me.jordancole.easynetworking.net.packets.PacketHandler;
import me.jordancole.easynetworking.net.server.Server;
import me.jordancole.easynetworking.net.server.ServerConnection;
Server s = new Server(8222);s.addPacketHandler(new PacketHandler(){

@Override
public void handleServerPacket(int id, String[] data, ServerConnection connection) {
	System.out.println("SERVER HANDLE PACKET");
	System.out.println("PACKET TYPE: " + id);
	System.out.println("PACKET DATA:");
	int num = 0;
	for (String s : data) {
		System.out.println("INDEX " + num + " - " + s);
		num++;
	}
}

@Override
	public void handleClientPacket(int id, String[] data, Client client) {

	}

});
s.addClientDisconnectHandler(new DisconnectHandler() {

	@Override
	public void onClientDisconnect(ServerConnection sc) {
		// this is ran when on the server side when a clients connection is terminated.
		System.out.println("CLIENT DISCONNECTED FROM SERVER.");
	}

	@Override
	public void onServerDisconnect() {
		// this is never ran if added to the Server.
	}
}
```

# Client
```java

Client c = new Client("localhost", 8222);
System.out.println("CLIENT ID: " + c.getID().toString());
c.addPacketHandler(new PacketHandler() {

	@Override
	public void handleClientPacket(int id, String[] data, Client client) {
		System.out.println("CLIENT HANDLE PACKET");
		System.out.println("PACKET TYPE: " + id);
		System.out.println("PACKET DATA:");
		int num = 0;
		for (String s : data) {
			System.out.println("INDEX " + num + " - " + s);
			num++;
		}
	}

	@Override
	public void handleServerPacket(int id, String[] data, ServerConnection connection) {

	}
});
c.addClientDisconnectHandler(new DisconnectHandler() {

	@Override
	public void onClientDisconnect(ServerConnection sc) {
		// this is never ran if added to the Client.
	}

	@Override
	public void onServerDisconnect() {
		// this is ran when the connection to the server is terminated.
		System.out.println("CONNECTION HAS BEEN LOST.");
	}
});

```

