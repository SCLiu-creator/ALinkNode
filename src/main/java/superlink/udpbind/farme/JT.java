package superlink.udpbind.farme;

import javax.swing.*;
import java.awt.*;

public class JT{
    public JTabbedPane tabbedPane;

    public void jj(String s){
        JFrame jFrame=new JFrame(s);
        tabbedPane = new JTabbedPane();

        JPanel newfile=new JPanel();
        JPanel deletefile=new JPanel();
        JPanel sethashtable=new JPanel();
        JPanel lookfile=new JPanel();

        tabbedPane.addTab("查找", newfile);
        tabbedPane.addTab("删除", deletefile);
        tabbedPane.addTab("设置", sethashtable);
        tabbedPane.addTab("查看", lookfile);

        tabbedPane.setPreferredSize(new Dimension(430, 340));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        jFrame.add(tabbedPane);
        jFrame.setContentPane(tabbedPane);


        JPanel panell = new JPanel();
        panell.setLayout(new BoxLayout(panell, BoxLayout.Y_AXIS));
        // 创建若干个JButton对象，并添加到JPanel对象中
        JButton button1 = new JButton("请求节点");
        JButton button2 = new JButton("刷新列表");
        JButton button3 = new JButton("快速数据连接");
        JButton button4 = new JButton("稳定数据连接");
        panell.add(button1);
        panell.add(button2);
        panell.add(button3);
        panell.add(button4);
        jFrame.add(panell);
        button1.addActionListener(e->{
            JPanel panell1 = new JPanel();
            panell1.setLayout(new BoxLayout(panell1, BoxLayout.Y_AXIS));
            // 创建若干个JButton对象，并添加到JPanel对象中
            JButton b1utton1 = new JButton("请求节点");
            JButton b1utton2 = new JButton("刷新列表");
            JButton b1utton3 = new JButton("快速数据连接");
            JButton b1utton4 = new JButton("稳定数据连接");
            panell1.add(b1utton1);
            panell1.add(b1utton2);
            panell1.add(b1utton3);
            panell1.add(b1utton4);
            jFrame.add(panell1);
            jFrame.revalidate();
            jFrame.repaint();
        });

        jFrame.setBounds(100,100,1100,800);
        jFrame.setVisible(true);

    }

}
