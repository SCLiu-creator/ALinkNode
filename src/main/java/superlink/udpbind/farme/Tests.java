package superlink.udpbind.farme;

import superlink.udpbind.cloude.util.TendMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//包
public class Tests{
    //Target Graphics Address Info
    public static Dimension GetScreenBorderInfo= Toolkit.getDefaultToolkit().getScreenSize();//获取信息默认包
    public static int GetScreenWidth=(int)GetScreenBorderInfo.getWidth();//宽
    public static int GetScreenHeight=(int)GetScreenBorderInfo.getHeight();//高
    //获取屏幕大小信息
    public static boolean MousePressedJudge=false;
    private static final java.awt.Point GetToWindowsPositionInfo=new java.awt.Point();
    //储存鼠标点击前的位置

    public static void St(int i){
        String s=String.valueOf(i);//3217,3147
        System.out.println(s);//3178
    }

    public static void main(String[] SimpleWindowsMoveExec){
        int user=1;
        long t=System.currentTimeMillis();
        for (int i = 0; i < 1000*1000; i++) {
            St(i);
//            try {
//                user.hashCode();//26
//            }catch (Exception e){
////                e.getMessage();
//                System.out.println("1");//9716
//            }
//            if (user==null){
//                System.out.println("1");//3405
//            }else {
//                user.hashCode();//27
//            }
//            user.hashCode();//24;
//            user++;//50
            // int user++;//7
        }
        System.out.println((System.currentTimeMillis()-t));
        LinkedBlockingQueue linkedBlockingQueue=new LinkedBlockingQueue<>();
        Thread.currentThread().setName("linkedBlockingQueue");
        Thread linkedBlockingQueuet=new Thread(()->{
            Thread.currentThread().setName("linkedBlockingQueue0");
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }finally {
//                System.out.println("finally linkedBlockingQueue0");
//            }
            try {

                linkedBlockingQueue.poll(1,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("poll linkedBlockingQueue0");
            }
            System.out.println("linkedBlockingQueue0");
        });
        linkedBlockingQueuet.start();

        try {linkedBlockingQueuet.interrupt();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            linkedBlockingQueuet.stop();
            linkedBlockingQueue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StringBuilder a=new StringBuilder("aaaa");
        StringBuilder b=a.append("b");
        StringBuilder c=b.append("c");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(b);
        ImageIcon DefaultOfTestsObject=new ImageIcon("C:\\\\Users\\\\liushengchang-n\\\\Desktop\\\\udp.jpg");
        JLabel LoadGraphicsOfJLabel=new JLabel();
        LoadGraphicsOfJLabel.setLayout(null);
        LoadGraphicsOfJLabel.setBounds(100,100,DefaultOfTestsObject.getIconWidth()
                ,DefaultOfTestsObject.getIconHeight());
        //设置边界
        JFrame GraphicsMainWindows=new JFrame("aaa");
        GraphicsMainWindows.setSize(1000,100);
        GraphicsMainWindows.setUndecorated(true);
        //无原始装扮
        GraphicsMainWindows.setBackground(new Color(10,10,0, 0));
        //背景透明
        GraphicsMainWindows.setLayout(null);
        JButton button=new JButton("aaaa");
        GraphicsMainWindows.add(button);
        GraphicsMainWindows.setVisible(true);
        //设置自定义布局
        GraphicsMainWindows.setBounds(GetScreenWidth/2-DefaultOfTestsObject.getIconWidth()/2,
                GetScreenHeight/2-DefaultOfTestsObject.getIconHeight()/2,DefaultOfTestsObject.getIconWidth()
                ,DefaultOfTestsObject.getIconHeight());
        ///使图像总是位于最中间
        LoadGraphicsOfJLabel.setIcon(DefaultOfTestsObject);
        GraphicsMainWindows.getLayeredPane().add(LoadGraphicsOfJLabel,new Integer(Integer.MIN_VALUE));
        //接收布局放置图像
        GraphicsMainWindows.setVisible(true);
        GraphicsMainWindows.addMouseListener(new MouseAdapter() {
            //监听点击
            @Override
            public void mousePressed(MouseEvent MousePressedFrontOfPosition){
                GetToWindowsPositionInfo.x=MousePressedFrontOfPosition.getX();
                GetToWindowsPositionInfo.y=MousePressedFrontOfPosition.getY();
                MousePressedJudge=true;
                //获取点击前的鼠标信息
            }
            @Override
            public void mouseReleased(MouseEvent MouseReleasedFrontOfPosition){
                MousePressedJudge=false;
                //如果不属于点击状态
            }
        });
        GraphicsMainWindows.addMouseMotionListener(new MouseAdapter(){
            //监听窗口拖拽
            @Override
            public void mouseDragged(MouseEvent MouseMotionTimeOfPosition){
                java.awt.Point ObtainWindowsOfPosition=GraphicsMainWindows.getLocation();
                //获取的是窗口的位置
                if (MousePressedJudge==true){
                    //判定是拖拽
                    GraphicsMainWindows.setLocation(ObtainWindowsOfPosition.x+MouseMotionTimeOfPosition.getX()-GetToWindowsPositionInfo.x,
                            ObtainWindowsOfPosition.y+MouseMotionTimeOfPosition.getY()-GetToWindowsPositionInfo.y);
                    //算法:窗口位置+拖拽位置-点击前的位置,因为你点击的位置属于窗口位置所一要减
                }
            }
        });
    }


}

