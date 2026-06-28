package superlink.udpbind.farme;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LogANA extends JFrame {



    //背景图片bgImage
    private ImageIcon bgImage = new ImageIcon("C:\\Users\\liusc\\Desktop\\720w.jpg");//"C:\\Users\\liusc\\Desktop\\udp.jpg"
    //用于处理拖动事件，表示鼠标按下时的坐标，相对于JFrame
    int xOld = 0;
    int yOld = 0;

    private ImageIcon bt1mage = new ImageIcon("src/main/images/mini.png");
    private ImageIcon bt1mage_enable = new ImageIcon("src/main/images/mini_enable.png");
    private ImageIcon bt2mage = new ImageIcon("src/main/images/close.png");
    private ImageIcon bt2mage_enable = new ImageIcon("src/main/images/close_enable.png");

        public LogANA() {

                 getContentPane().setLayout(new BorderLayout());
                 this.setLocationRelativeTo(null);
                 this.setSize(bgImage.getIconWidth(), bgImage.getIconHeight());

                 //处理拖动事件---去掉默认边框后，不能拖动了，具体实现如下
                 this.addMouseListener(new MouseAdapter() {
             @Override
             public void mousePressed(MouseEvent e) {
                                 xOld = e.getX();//记录鼠标按下时的坐标
                                 yOld = e.getY();
                             }
         });

                 this.addMouseMotionListener(new MouseMotionAdapter() {
             @Override
             public void mouseDragged(MouseEvent e) {
                                 int xOnScreen = e.getXOnScreen();
                                 int yOnScreen = e.getYOnScreen();
                                 int xx = xOnScreen - xOld;
                                 int yy = yOnScreen - yOld;
                                 LogANA.this.setLocation(xx, yy);//设置拖拽后，窗口的位置
                             }
         });

                 JPanel mainPanel = new JPanel();
                 mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                 mainPanel.setSize(bgImage.getIconWidth(), 100);
                mainPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

                 //关闭按钮
                 JButton miniBtn = new JButton();
                 mainPanel.add(miniBtn);

                 //关闭按钮
                JButton closeBtn = new JButton();
                 mainPanel.add(closeBtn);

                 getContentPane().add(mainPanel, BorderLayout.CENTER);

                 closeBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                                 System.exit(0);
                             }
         });

                 miniBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                                setExtendedState(JFrame.ICONIFIED);//最小化窗体
                            }
         });

                 setUndecorated(true);
                 setLocationRelativeTo(null);
                 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             }

             public static void main(String[] args) {
                 LogANA j = new LogANA();
                 j.setVisible(true);
             }

         }