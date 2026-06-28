package superlink.udpbind.farme;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;

public class WindowDemo {
    public JFrame windde() {

        String json = "[\"apple\", \"banana\", \"orange\"]"; // 定义一个json字符串

        JsonReader reader = Json.createReader(new StringReader(json)); // 创建一个JsonReader对象

        JsonArray array = reader.readArray(); // 将json字符串转换为JsonArray对象

        reader.close(); // 关闭JsonReader对象

        String[] data = new String[array.size()]; // 创建一个字符串数组

        for (int i = 0; i < array.size(); i++) {
            data[i] = array.getString(i); // 将JsonArray中的每个元素赋值给字符串数组中对应的位置
        }

        JFrame frame = new JFrame("Window Demo");

        frame.setSize(800, 600); // 设置窗口的大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口关闭时的操作

        JPanel panel = new JPanel(); // 创建一个面板
        panel.setBackground(Color.WHITE); // 设置面板的背景颜色

        frame.add(panel, BorderLayout.EAST); // 将面板添加到窗口的东部位置

        frame.setVisible(true); // 设置窗口可见

        JList list = new JList(data); // 创建一个列表，并将字符串数组作为参数传递给它

        JScrollPane scrollPane = new JScrollPane(list); // 创建一个滚动面板，并将列表作为它的客户端

        frame.add(scrollPane, BorderLayout.EAST); // 将滚动面板添加到窗口的东部位置，而不是直接添加面板

        JButton button = new JButton("Confirm"); // 创建一个按钮，并设置它的文本

        frame.add(button, BorderLayout.SOUTH); // 将按钮添加到窗口的南部位置

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 如果选择已经确定
                int index = list.getSelectedIndex(); // 获取选中项的索引
                System.out.println("You selected: " + data[index]); // 打印选中项的内容
            }
        });

        button.addActionListener(e -> {
            System.out.println("You clicked confirm."); // 打印点击确认按钮的信息
            System.exit(0); // 退出程序
        });

       frame.setSize(300,200);
       frame.setLocationRelativeTo(null);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setVisible(true);
       return frame;
    }
}