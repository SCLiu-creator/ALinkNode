package superlink.udpbind.client.recives.datalen.dataAsy;

public interface DataWriter {
    int getLen();

    boolean getState();

    void add(byte[] bytes, int pos, int len);
}
