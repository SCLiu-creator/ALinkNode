package superlink.udpbind.controller;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public  class Invoke {

    public static ScheduledExecutorService scheduledExe= Executors.newSingleThreadScheduledExecutor();
    public static ReentrantLock reentrantLock=new ReentrantLock();

    public static void requestUser(String username){
        String s="RU"+username;
        byte[] bytes=s.getBytes();
        Senders.ServerSends(bytes);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // task to run goes here
                // 执行的输出的内容
                System.out.println("Hello, stranger");
            }
        };
        Timer timer = new Timer();
        // 定义开始等待时间  --- 等待 5 秒
        // 1000ms = 1s
        long delay = 5000;
        // 定义每次执行的间隔时间
        long intevalPeriod = 5 * 1000;
        // schedules the task to be run in an interval
        // 安排任务在一段时间内运行
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
        scheduledExe.scheduleAtFixedRate(()->{},1,10, TimeUnit.SECONDS);

    }

    public static FutureTask requestUserCall(String username){
        String s="RU"+username;
        byte[] bytes=s.getBytes();
        Senders.ServerSends(bytes);
        FutureTask<node> futureTask=new FutureTask<node>(new scheduledCall(username));
        futureTask.run();
        return futureTask;
    }

    public static class scheduledCall implements Callable {
        public scheduledCall(String username){
            this.name=username;
        }
        public String name;
        public int times=0;

        @Override
        public Object call() throws Exception {
            node node=new node();
            node.setName(name);
            while (times<7){
                User user = UDPclient.userMap.get(name);

                if (user!=null){
                    UserRequest userRequest=Controller.toUserquest(user);
                    Controller.requestNode(userRequest);
                    break;
                }
                times++;
                Thread.sleep(1000);
            }
            while (times<4){
                User user=UDPclient.bindUser.get(name);
                if (user!=null){
                    node.setB(true);
                    return node;
                }
                Thread.sleep(1000);
            }

            return node;
        }
    }

    public static class node{
        private String name;
        private boolean b=false;
        public int i=0;

        public String getName() {
            return name;
        }

        public node setName(String name) {
            this.name = name;
            return this;
        }

        public boolean getB() {
            return b;
        }

        public node setB(boolean b) {
            this.b = b;
            return this;
        }
    }

}
