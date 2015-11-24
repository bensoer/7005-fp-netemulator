package networkemulator.socketemulator;

/**
 * Created by bensoer on 06/11/15.
 *
 * PacketType is a constant enum to define all packet types used in the network-emulator program. They are assigned
 * values associated with closest matching HTTP codes for mere convenience and to give moderate self-definition. Throughout
 * code though they will be referred to by thier enum names which are associated with the appropriate packet types seen
 * in networks
 */
public enum PacketType {
    ACK(200),PUSH(100),EOT(101);

    private int value;


    PacketType(int value){
        this.value = value;
    }

    /**
     * toInt fetches the int value associated with the enum
     * @return the int associated with the enum. This will be a best suited HTTP code
     */
    public int toInt(){
        return this.value;
    }


}
