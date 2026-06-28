package superlink.udpbind.client.recives;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

public class CorBlockCon {
    // 定义 Comparator（升序排序）
    public static Comparator<RunTime> timeComparator = Comparator.comparing(RunTime::getTime);
    public static ConcurrentSkipListSet<RunTime> set = new ConcurrentSkipListSet<RunTime>(timeComparator);

    public static LinkedBlockingQueue<RunTime> linkedBlockingQueue=new LinkedBlockingQueue<>();
    public static boolean runIng=true;

    public synchronized static RunTime findNearest(ConcurrentSkipListSet<RunTime> RunTimes, long targetTime) {
        if (RunTimes.isEmpty()) {
            return null;
        }

        // 自定义 Comparator 比较 time 字段
        Comparator<RunTime> comparator = Comparator.comparingLong(RunTime::getTime);
        runTime.setTime(targetTime);

        // 找到左右邻元素
        RunTime floor = RunTimes.floor(runTime);
        RunTime ceiling = RunTimes.ceiling(runTime);

        // 比较左右邻元素哪个更接近
        RunTime nearest = null;
        if (floor != null && ceiling != null) {
            double distFloor = Math.abs(floor.getTime() - targetTime);
            double distCeiling = Math.abs(ceiling.getTime() - targetTime);
            nearest = distFloor <= distCeiling ? floor : ceiling;
        } else if (floor != null) {
            nearest = floor; // 没有右邻（n 比所有元素大）
        } else if (ceiling != null) {
            nearest = ceiling; // 没有左邻（n 比所有元素小）
        }
        return nearest;
    }

    public static RunTime runTime=new RunTime() {

        long time=0;
        int times=1;
        @Override
        public void process() {

        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public void setTime(long t) {
            time=t;
        }

        @Override
        public int getTimes() {
            return times;
        }

        @Override
        public void setTimes(int times) {
            this.times=times;
        }

        @Override
        public void decTimes() {
            times=times-1;
        }
    };
}
