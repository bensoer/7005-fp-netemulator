#network-emulator
Network emulator is a simulator of a network connection and includes bit errors

#Setup

So far it is assumed that the client in this simulation will always go first. Half-duplex is supported so both must
be able to communicate but the client will always be first it will finish and then the server will have a turn


#Number Reference

##Packet Type
Going to use HTTP codes as a reference to make these numbers somewhat meaningful

* EOT (End of Transmission) = 101
* PUSH = 100
* ACK = 200

See PacketType enum for details.