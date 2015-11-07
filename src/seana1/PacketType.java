package seana1;

/**
 * Created by bensoer on 06/11/15.
 */
public enum PacketType {
    ACK(200),PUSH(100),EOT(101);

    private int value;

    PacketType(int value){
        this.value = value;
    }

    public int toInt(){
        return this.value;
    }


}
