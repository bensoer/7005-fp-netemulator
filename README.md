#network-emulator
Network emulator is a simulator of a network connection and includes bit errors and delays.


#quick start
The emulator is setup on a client-server type of system where there is a client-server relationship between the `Client`
and `Internet` modules, and another between the `Internet` and `Server` modules. This TCP socket implementation then
requires that to start up the emulator, the following files will be initialized in the following order:

1. server/Server.java
2. internet/Main.java
3. client/Client.java

Upon the Client.java file starting, the emulation will begin transferring data with the set settings passed as parameters
or in the `configuration.properties` file. Each module will not start if there are required parameters needing to be
passed at initialization. `configuration.properties` has been setup with default settings so that the emulator will
work right away.

#features & settings
The network emulator does a full transaction of a 300 paragraph file from the `Client` to the `Server` and then returns
the file back from the `Server` to the `Client`. The time and duration of this transaction is of course effected by
timer lengths, window size, bit error rates and delay time which are all configurable in various areas.

##internet
The internet module contains the core handling of the emulated network connection quality. The module takes 2 parameters
at startup to determine the Bit Error Rate as a percent and the Average Delay in milliseconds. This module then handles
incoming packets and based on the parameters determines the length to delay the packet, or whether to drop it completely.
Note that the internet will drop or delay ANY kind of packet regardless of type that is used in the simulation.

The internet system initializes through the `Main` java class which creates and starts the `Internet` java class. The
`Main` classes purpose is to souly parse out the required parameters before initializing the `Internet` module. The
`Internet` then creates 2 threads built on the `Client2Internet` and `Server2Internet` classes which each listen to
either the client (Client2Internet) or the server (Server2Internet) for incoming packets. It then uses the `InternetTools`
static class to determine whether to drop the packet, and how long to hold onto the packet (delaying it) before passing
it on to the appropriate opposite socket based on the `src` and `dst` attributes set in the packet.

The internet module can also be configured with parameters in the `configuration.properties` file located in the root
of the project. Here you can set the `listener` and `sender` ports aswell as the sender host. By default the internet
module is configured to function on localhost and assumes the `Server` is also located on localhost on port `7000`. Below
is a breakdown of all the settings that can be configured in the `configuration.properties` file:
###internet.connection.listener.port
Set the port number for the internet to listen on for connections from the `Client` module
###internet.connection.sender.port
Set the port number for the internet to send on for connections to the `Server` module
###internet.connection.sender.host
Set the host value for the internet to send data to. This should be the host that the `Server` module is located on

##server
The server module is the first to receive data in the simulation before receiving an `EOT` packet type which triggers it
to switch roles and send data back to the client. The server module initializes from the `Server` class where it creates
two threads based on the `ServerSocketListener` and `ServerSocketSender` classes. These classes as named listen for
incoming data from the client and then sends back an `ACK` packet for each until an `EOT` packet arrives at which point
the `ServerSocketSender` thread is initialized and sends back file data to the client

Configuration of the server can be set in the `configuration.properties` file. The following attributes in the 
configuration file will alter settings of the server:
###server.connection.port
Set the port number the `Server` module will listen on for connections
###server.connection.windowsize
Sets the size of the window for the `Server` module. This is used when the `EOT` packet type is recieved and all packets
have been recieved. The `Server` module will then switch roles and start sending data, at which point it uses the window
size to control how many packets are sent at 1 time
###server.connection.inittimeout
Sets the initial timeout for the timers when the `Server` module starts sending data back to the client.
###server.packet.maxsize
Sets the max size in characters of the packets data length that will be sent. This number is used to calculate how many
packets will be needed to cutup the data being sent

##client
The client module is the first to send data in the simulation before it will recieve data. As the client there is not
explicit transition other then it completes sending all of its data with an `EOT` packet, at which point the server then
begins sending data. The client is unaware of the transition as it is already listening for incoming data being the `ACK`
packets for the data it is sending.

The client module initializes from the `Client` java class where it creates a socket based on the `ClientSocketListener`
class to listen for returning `ACKS` from the `Server` and data sent back from the `Server` when the `Client` has completed
transferring all data. The `Client` class also handles parsing the file data and sending it to the `Server`

The client module can be configured in the `configurations.properties` file with the following settings:
###client.connection.port
Set the port number the client will connect to, to send its data
###client.connection.host
Set the host the client will connect to, to send its data
###client.connection.windowsize
Set the windowsize of the client for determining how many packets will be sent at one time
###client.connection.inittimeout
Set the initial timeout value used in the timers to decide when a retransmission is needed. Timers are set once the packet
is sent and then stopped when the ACK by the server is recieved
###client.packet.maxsize
Set the max packet size in characters of the packet data length that will be snet. This number is used to calculate how
many packets will be needed to cutup the data being sent






#number reference

##packet type
Going to use HTTP codes as a reference to make these numbers somewhat meaningful

* EOT (End of Transmission) = 101
* PUSH = 100
* ACK = 200

See PacketType enum for details.