package superlink.udpbind.fileListen.watch;


import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface FileWatchedListener {
    void onCreated(WatchEvent<Path> watchEvent);

    void onDeleted(WatchEvent<Path> watchEvent);

    void onModified(WatchEvent<Path> watchEvent);

    void onOverflowed(WatchEvent<Path> watchEvent);
}

