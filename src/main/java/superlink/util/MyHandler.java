package superlink.util;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

// 导入需要的包
import java.io.File;
import javax.xml.parsers.SAXParserFactory;

// 定义一个自定义类继承DefaultHandler
public class MyHandler extends DefaultHandler {

    // 定义一些变量来存储解析过程中的信息
    private String currentTag; // 当前标签名
    private String currentText; // 当前标签内的文本内容
    private Book book; // 当前正在处理的Book对象
    private List<Book> books; // 存储所有Book对象的列表

    // 重写startDocument ()方法，在文档开始时初始化列表
    @Override
    public void startDocument() throws SAXException {
        books = new ArrayList<Book>();
    }

    // 重写endDocument ()方法，在文档结束时打印列表中的内容
    @Override
    public void endDocument() throws SAXException {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    // 重写startElement ()方法，在遇到开始标签时记录标签名，并根据标签名创建Book对象或获取属性值
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName; // 记录当前标签名
        if ("book".equals(qName)) { // 如果是book标签，就创建一个Book对象，并获取其id属性值
            book = new Book();
            book.setId(attributes.getValue("userid"));
        }
    }

    // 重写endElement ()方法，在遇到结束标签时根据标签名将Book对象添加到列表中，并清空当前标签名和文本内容
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("book".equals(qName)) { // 如果是book标签，就将Book对象添加到列表中
            books.add(book);
        } else if ("name".equals(qName)) { // 如果是name标签，就将文本内容设置为Book对象的name属性值
            book.setName(currentText);
        } else if ("author".equals(qName)) { // 如果是author标签，就将文本内容设置为Book对象的author属性值
            book.setAuthor(currentText);
        } else if ("year".equals(qName)) { // 如果是year标签，就将文本内容设置为Book对象的year属性值
            book.setYear(Integer.parseInt(currentText));
        } else if ("price".equals(qName)) { // 如果是price标签，就将文本内容设置为Book对象的price属性值
            book.setPrice(Double.parseDouble(currentText));
        }
        currentTag = null; // 清空当前标签名
        currentText = null; // 清空当前文本内容
    }

    // 重写characters ()方法，在遇到文本内容时记录文本内容
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentTag != null) { // 如果当前标签名不为空，就获取文本内容
            currentText = new String(ch, start, length);
        }
    }

    // 定义一个Book类，用来封装XML文件中的book元素的信息
    public static class Book {
        private String id; // id属性值
        private String name; // name元素的文本内容
        private String author; // author元素的文本内容
        private int year; // year元素的文本内容
        private double price; // price元素的文本内容

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        // 省略构造方法，getter和setter方法，toString ()方法
    }

    // 定义一个主方法，用来调用SAXParser的parse ()方法，开始解析XML文件
    public static void main(String[] args) throws Exception {
        // 创建一个SAXParserFactory对象，用来获取SAXParser实例
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 创建一个SAXParser对象，用来解析XML文件
        SAXParser parser = factory.newSAXParser();
        // 创建一个MyHandler对象，用来处理解析过程中的事件
        MyHandler handler = new MyHandler();
        // 调用SAXParser的parse ()方法，传入XML文件和MyHandler对象，开始解析
        parser.parse(new File("books.xml"), handler);
    }
}
