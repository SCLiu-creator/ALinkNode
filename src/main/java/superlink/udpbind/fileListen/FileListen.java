package superlink.udpbind.fileListen;

import superlink.udpbind.cloude.FileTrigger;

public interface FileListen {
    public boolean isRun();
    public boolean Run();
    public void ReSetTime(long time);
    public boolean addListenDirRuning(FileTrigger fileTrigger);
    public boolean addListenDirStop(FileTrigger fileTrigger);
    public boolean removeListenDirRuning(FileTrigger fileTrigger);
    public void manualStop();
    public void clearMonitor();
    public void stop() throws Exception ;

    public void start() throws Exception ;
}
