package superlink.udpbind.farme;

import javax.swing.*;

import java.awt.*;

public class TAB extends JTabbedPane

{

    private static final long serialVersionUID = 1L;

    static final int WIDTH=600;

    static final int HEIGHT=400;

    public TAB()
    {
        JFrame f=new JFrame("JTabbedPane选项卡测试窗口");
        f.setSize(600,800);
        JPanel p=new JPanel();
        f.setContentPane(p);
        f.setVisible(true);
        setLayout(new BorderLayout());

        JPanel panel1 = new JPanel ();
        JPanel panel2 = new JPanel ();
        JPanel panel3 = new JPanel ();
        JPanel panel4 = new JPanel ();
        JPanel panel5 = new JPanel ();
        panel1.setLayout(new BorderLayout());
        addTab("Tab1", panel1);
        setEnabledAt(0,true);
        setTitleAt(0,"JTabbedPane面板1，文字测试");
        addTab ("Tab2", panel2);
        setEnabledAt (1, true);
        setTitleAt (1,"JTabbedPane面板2，文字测试");
        addTab ("Tab3", panel3);
        setEnabledAt (2, true);
        setTitleAt (2,"JTabbedPane面板3，文字测试");
        addTab ("Tab4", panel4);
        setEnabledAt(0,true);
        setTitleAt(3,"JTabbedPane面板4，文字测试");
        addTab ("Tab5", panel5);
        setEnabledAt(4,true);
        setTitleAt(4,"JTabbedPane面板5，文字测试");
        setPreferredSize (new Dimension (500,200));
        setTabPlacement (JTabbedPane.TOP);
        setTabLayoutPolicy (JTabbedPane.SCROLL_TAB_LAYOUT);
        p.add("Center",this);
        setVisible(true);
    }

    public static void main(String[] args) {

        long l=3831541035l;
        int i= (int) l;
        new TAB();

    }

}
