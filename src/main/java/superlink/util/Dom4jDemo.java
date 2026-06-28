package superlink.util;

// 导入需要的包
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import superlink.filemanage.xmltool.XmlParser;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Dom4jDemo {

    public static void main(String[] args) throws Exception {
        // 创建一个SAXReader对象，用来读取XML文件
        SAXReader reader = new SAXReader();
        // 读取本地文件或远程URL，并返回一个Document对象
        Document document = reader.read(XmlParser.cloudefile+".xml");
        // Document document = reader.read(new URL("http://www.example.com/books.xml"));
        // 获取根元素
        Element root = document.getRootElement();
        // 打印根元素的名字
        System.out.println("Root element: " + root.getName());
        // 获取根元素下的所有子元素
        List<Element> elements = root.elements();
        // 遍历所有子元素
        for (Element element : elements) {
            // 打印子元素的名字和属性值
            System.out.println("Element: " + element.getName());
            List<Attribute> attributes = element.attributes();
            for (Attribute attribute : attributes) {
                System.out.println("Attribute: " + attribute.getName() + "=" + attribute.getValue());
            }
            // 打印子元素的文本内容
            System.out.println("Text: " + element.getText());
        }

        // 创建一个新的元素，并添加到根元素下
        Element newElement = DocumentHelper.createElement("book");
        newElement.addAttribute("userid", "1005");
        newElement.addAttribute("name", "Java Web Development");
        newElement.setText("Beijing");
        root.add(newElement);

        // 修改某个元素的属性值或文本内容
        Element firstElement = root.element("book");
        firstElement.attribute("name").setValue("Java Programming");
        firstElement.setText("Shanghai");

        // 删除某个元素的属性或子元素
        Element secondElement = (Element) root.elements().get(1);
        secondElement.remove(secondElement.attribute("name"));
        secondElement.remove(secondElement.element("author"));

        // 设置输出格式，如缩进，换行，编码等
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

        // 创建一个XMLWriter对象，用来将Document对象写入到文件或输出流中
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream("books.xml"), "UTF-8"), format);
        // 将Document对象写入到文件中
        writer.write(document);
        // 关闭XMLWriter对象
        writer.close();
    }
}
