package superlink.udpbind.farme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Demo extends JFrame {

    public Demo() {
        super("Demo");
        initUI();
    }
    public Openfile openfile=new Openfile();
    JFileChooser chooser;

    private void initUI() {
        chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        // 创建一个JPanel对象，并设置其布局为BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 创建若干个JButton对象，并添加到JPanel对象中
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        JButton button3 = new JButton("Button 3");
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);

        // 为每个JButton对象添加一个ActionListener对象，并在actionPerformed方法中编写交互内容
        button1.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 1");
        });

        button2.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 2");
        });

        button3.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 3");
            String string=openfile.name;
            int result =chooser.showOpenDialog(null);
            //if file selected,set it as icon of the label
            if(result==JFileChooser.APPROVE_OPTION)
            {
                string=chooser.getSelectedFile().getPath();
            System.out.println("打开文件"+string);}
        });

        // 创建一个JLabel对象，并设置其文本为你想要显示的内容
        JLabel label = new JLabel("This is a label");

        // 将JLabel对象添加到窗体的左上角，例如使用BorderLayout.NORTH
        add(label, BorderLayout.NORTH);

        // 将JPanel对象添加到窗体的左侧，例如使用BorderLayout.WEST
        add(panel, BorderLayout.WEST);

        // 设置窗体的属性
        setSize(300, 2000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        String s="";
        String[] ss=s.split("\\.");
        if (ss.length<=1){
            System.out.println("error");
        }
        Map map=new HashMap();
        map.put(1,"aa");
        map.put(2,2);
        map.entrySet().iterator().forEachRemaining(o -> {
            Map.Entry<Object,Object> m= (Map.Entry<Object, Object>) o;
            System.out.println(o.getClass()+o.toString());
            System.out.println(o+o.toString());
            System.out.println(m.getKey().getClass()+":"+m.getValue().getClass());

        });




        SwingUtilities.invokeLater(() -> {
            Class c=Thread.currentThread().getClass();
//            HttpThreadBind httpThreadBind=new HttpThreadBind(Utils.getLocalIp(),8880);
//            httpThreadBind.run();
          //  Demo demo = new Demo();
            JT j=new JT();
            j.jj("page");
            Framefirst framefirst = new Framefirst("aaa");
//            framefirst.start1();
//            framefirst.Panel();
//            try {
//                framefirst.CardLayout();
//            } catch (MalformedURLException | URISyntaxException e) {
//                e.printStackTrace();
//            }
//           framefirst.MenuDemo();
//           framefirst.star3();
             framefirst.component();
           //framefirst.GridBagLayout();

        });
    }
}
