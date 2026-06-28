package superlink.udpbind.client.recives;

public interface RunTime {
    void process();

    public long getTime();
    public void setTime(long time);
    public int getTimes();
    public void setTimes(int times);
    public void decTimes();
}