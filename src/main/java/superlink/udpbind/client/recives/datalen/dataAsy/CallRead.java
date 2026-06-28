package superlink.udpbind.client.recives.datalen.dataAsy;

public interface CallRead {

    public int runTime(byte[] byteBuffer) throws Exception;

    boolean isOver();
}
