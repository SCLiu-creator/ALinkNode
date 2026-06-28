package superlink.udpbind.farme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Framefirst extends Frame implements ActionListener, WindowListener {

    public static Frame frame = new Frame("测试窗口");

    public Framefirst(String str){
    super(str);

}
//窗口（Frame）
    public void start1() {
        // TODO Auto-generated method stub
        Framefirst fr = new Framefirst("Hello"); //构造方法
        fr.setSize(480,720);  //设置Frame的大小
        fr.setBackground(Color.white); //设置Frame的背景色
        fr.setVisible(true); //设置Frame为可见，默认不可见
        fr.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    //Panel是一种透明的容器，既没有标题，也没有边框。它不能作为最外层的容器单独存在，首先必须先作为一个组件放置在其他容器中，然后在把它当做容器。
    public void Panel() {
        // TODO Auto-generated method stub
        Frame fr = frame;
        fr.setSize(240,240);
        fr.setBackground(Color.green);
        fr.setLayout(null); //取消默认的布局BorderLayout
        Panel pan = new Panel(); //创建面板
        pan.setSize(100,100);
        pan.setBackground(Color.yellow);
        fr.add(pan);
        fr.setVisible(true);
    }

    //布局管理器（LayoutManager）

//FlowLayout——流式布局管理器
//    组件从左到右、从上到下，一个挨一个的放在容器中。（Panel和Applet的默认容器布局）如果容器足够宽，
//    第一个组件先添加到容器中第一行的最左边，后续的组件依次添加到上一个组件的右边，如果当前行已放置不下该组件，则放置到下一行的最左边。

    public void star3() {
        // TODO Auto-generated method stub
        Frame frame = new Frame("FlowLayout"); //Frame默认的布局管理器为BorderLayout
        frame.setBounds(100, 100, 400, 300);
        frame.setLayout(new FlowLayout()); //设置布局管理器为FlowLayout

        //FlowLayout fl = new FlowLayout();
        //fl.setAlignment(FlowLayout.LEFT); //设置对齐方式
        ////也可以直接使用构造函数
        ////FlowLayout f1 = new FlowLayout(FlowLayout.LEFT,20,40); //三个参数，对齐方式（居左，横向间隔20像素，纵向间隔40像素）
        //frame.setLayout(fl);

        Button but1 = new Button("button1");
        Button but2 = new Button("button2");
        Button but3 = new Button("button3");
        Button but4 = new Button("button4");
        Button but5 = new Button("button5");

        but1.setBackground(Color.blue);
        but2.setBackground(Color.yellow);
        but3.setBackground(Color.red);
        but4.setBackground(Color.green);
        but5.setBackground(Color.pink);

        frame.add(but1);
        frame.add(but2);
        frame.add(but3);
        frame.add(but4);
        frame.add(but5);

        frame.setVisible(true);
    }

//CardLayout——卡片布局管理器

    public void CardLayout() throws MalformedURLException, URISyntaxException {
        JFrame f = new JFrame("asddfg");
        new Frame();
//      URL url = JFrame.class.getResource("8635b23a8459d8ab1ee278ad957efe_720w.jpg");
//        URI url1=new URI("C:\\Users\\liusc\\Desktop\\720w.jpg");
        //将图片变为图标
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\liushengchang-n\\Desktop\\v2-fa11a4281c14883ef6f7a59078775109_r.jpg");
        String[] names = { "第一张", "第二张", "第三张", "第四张", "第五张" };
        JPanel p1 = new JPanel(); //显示的面板
        CardLayout c = new CardLayout(); //卡片局部
        p1.setLayout(c); //面板布局使用卡片布局
        for (int i = 0; i < names.length; i++) {
            p1.add(names[i], new Button(names[i])); //设置面板的名字和组件
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(800,600,Image.SCALE_DEFAULT));
        JLabel label=new JLabel();
        label.setIcon(imageIcon);
        p1.add(label);
        WindowDemo windowDemo=new WindowDemo();
//        p1.add(windowDemo.windde());
        JPanel p = new JPanel(); //创建一个放按钮的面板
        // 控制显示上一张的按钮
        Button previous = new Button("上一张");
        //为按钮添加监听
        previous.addActionListener(e ->  {
                c.previous(p1);
        });
        // 控制显示下一张的按钮
        Button next = new Button("下一张");
        next.addActionListener(e -> {
                c.next(p1);
        });
        // 控制显示第一张的按钮
        Button first = new Button("第一张");
        first.addActionListener(e -> {
                c.first(p1);
        });
        // 控制显示最后一张的按钮
        Button last = new Button("最后一张");
        last.addActionListener(e -> {
                c.last(p1);
        });
        // 控制根据Card显示的按钮
        Button third = new Button("第三张");
        third.addActionListener(e -> {
                c.show(p1, "第三张");
        });
        p.add(previous);
        p.add(next);
        p.add(first);
        p.add(last);
        p.add(third);
        f.add(p1);
        f.add(p, BorderLayout.SOUTH);
        f.setSize(800,800);
        f.setVisible(true);
        f.pack(); //紧凑排列

    }

//GridBagLayout——网格包布局管理器
    private Frame f = new Frame("GridBagLayout");
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    private Button[] btns = new Button[10];

    private void addButton(Button btn) {
        gbl.setConstraints(btn, gbc);
        f.add(btn);
    }
    public void GridBagLayout() {
        for (int i = 0; i < 10; i++) { // 先初始化10个按钮
            btns[i] = new Button("button" + i);
        }
        f.setLayout(gbl); // 设定框架的布局模式
        //为了设置如果组件所在的区域比组件本身要大时的显示情况
        gbc.fill = GridBagConstraints.BOTH; // 使组件完全填满其显示区域
        //NONE：不调整组件大小。
        //HORIZONTAL：加宽组件，使它在水平方向上填满其显示区域，但是不改变高度。
        //VERTICAL：加高组件，使它在垂直方向上填满其显示区域，但是不改变宽度。
        //BOTH：使组件完全填满其显示区域。
        gbc.weighty = 1; // 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个,为1则只占一格
        // 第1行的4个按钮
        gbc.weightx = 1; // 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        addButton(btns[0]);
        addButton(btns[1]);
        addButton(btns[2]);
        gbc.gridwidth = GridBagConstraints.REMAINDER; // 该组件是该行的最后一个，第4个添加后就要换行了
        addButton(btns[3]);
        // 第2行1个按钮，仍然保持REMAINDER换行状态
        addButton(btns[4]);
        //第3行
        gbc.gridwidth = 2; //按钮分别横跨2格
        gbc.weightx = 1;  //该方法设置组件水平的拉伸幅度
        addButton(btns[5]);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addButton(btns[6]);
        // 按钮7纵跨2个格子，8、9一上一下
        gbc.gridheight = 2; //按钮7纵跨2格
        gbc.gridwidth = 1;  //横跨1格
        gbc.weightx = 1;    //该方法设置组件水平的拉伸幅度
        addButton(btns[7]); // 由于纵跨2格因此纵向伸缩比例不需要调整，默认为1*2格，比例刚好
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        addButton(btns[8]);
        addButton(btns[9]);
        f.pack();
        f.setVisible(true);
    }

    //组件（Component）
    //    awt组件库中还有很多比较常用的组件，如：按钮（Button）、复选框（Checkbox）、复选框组（CheckboxGroup）、下拉菜单（Choice）、
    //    单行文本输入框（TextField）、多行文本输入框（TextArea）、列表（List）、对话框（Dialog）、文件对话框（Filedialog）、
    //    菜单（Menu）、MenuBar、MenuItem、Canvas等；
    public void component() {
        // TODO Auto-generated method stub
        Frame frame = this.frame;
        frame.setBounds(100, 100, 600, 300);
        GridLayout gl = new GridLayout(4,2,5,5); //设置表格为3行两列排列，表格横向间距为5个像素，纵向间距为5个像素
        frame.setLayout(gl);
        //按钮组件
        Button but1 = new Button("测试按钮");
        Panel pn0 = new Panel();
        pn0.setLayout(new FlowLayout());
        pn0.add(but1);
        frame.add(pn0);

        //复选框组件
        Panel pn1 = new Panel();
        pn1.setLayout(new FlowLayout());
        pn1.add(new Checkbox("one",null,true));
        pn1.add(new Checkbox("two"));
        pn1.add(new Checkbox("three"));
        frame.add(pn1);

        //复选框组（单选）
        Panel pn2 = new Panel();
        CheckboxGroup cg = new CheckboxGroup();
        pn2.setLayout(new FlowLayout());
        pn2.add(new Checkbox("one",cg,true));
        pn2.add(new Checkbox("two",cg,false));
        pn2.add(new Checkbox("three",cg,false));
        frame.add(pn2);

        //下拉菜单
        Choice cC = new Choice();
        cC.add("red");
        cC.add("green");
        cC.add("yellow");
        frame.add(cC);

        //单行文本框
        Panel pn3 = new Panel();
        pn3.setLayout(new FlowLayout());
        TextField tf = new TextField("",30); //30列长度
        pn3.add(tf);
        frame.add(pn3);

        //多行文本框
        TextArea ta = new TextArea();
        frame.add(ta);

        //列表
        List ls = new List();
        ls.add("a");
        ls.add("b");
        ls.add("c");
        ls.add("d");
        frame.add(ls);
        frame.setVisible(true);
    }



    public void MenuDemo(){
        Frame f = this.frame;
        f.setBounds(0,0, 480, 720);
        //Menu无法直接添加到容器中，只能直接添加到菜单容器中
        MenuBar mb = new MenuBar(); //创建菜单容器
        f.setMenuBar(mb);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //添加菜单
        Menu m1 = new Menu("File");
        Menu m2 = new Menu("Edit");
        Menu m3 = new Menu("Help");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);

        //添加菜单项
        MenuItem mi1 = new MenuItem("Save");
        MenuItem mi2 = new MenuItem("Load");
        MenuItem mi3 = new MenuItem("Quit");
        mi3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        m1.add(mi1);
        m1.add(mi2);
        m1.addSeparator(); //添加分隔线
        m1.add(mi3);
        f.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }
    @Override
    public void windowOpened(WindowEvent e) {
    }
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
    @Override
    public void windowClosed(WindowEvent e) {
    }
    @Override
    public void windowIconified(WindowEvent e) {
    }
    @Override
    public void windowDeiconified(WindowEvent e) {
    }
    @Override
    public void windowActivated(WindowEvent e) {
    }
    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}