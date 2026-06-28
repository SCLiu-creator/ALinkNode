package superlink.udpbind.farme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;

public class WindowDemo1 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Window Demo"); // 创建一个窗口并设置标题
        frame.setSize(800, 600); // 设置窗口的大小
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口关闭时的操作

        JPanel panel = new JPanel(); // 创建一个面板
        panel.setBackground(Color.black);// 设置面板的背景颜色
        panel.add(new Button("aaaa"));
        frame.add(panel, BorderLayout.EAST); // 将面板添加到窗口的东部位置

        frame.setVisible(true); // 设置窗口可见
    }
}