package superlink.udpbind.dataqueue;

public enum QueName {

    TYPE1(1),TYPE2(2);

    private int type;
    QueName(int y){
        type=y;
    }
    public int getType() {
        return type;
    }
}
