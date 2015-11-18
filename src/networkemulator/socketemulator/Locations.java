package networkemulator.socketemulator;

/**
 * Created by bensoer on 07/11/15.
 */
public enum Locations {
    CLIENT("Client"), SERVER("Server");

    private String location;

    Locations(String location){
        this.location = location;
    }

    @Override
    public String toString(){
        return this.location;
    }
}
