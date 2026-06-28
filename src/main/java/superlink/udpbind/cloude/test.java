package superlink.udpbind.cloude;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
    {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            executor.invokeAll(null);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

//    SAXReader reader1 = new SAXReader();
//    Document doc1 = reader1.read(new File("path/to/first/xml/file.xml"));
//
//    SAXReader reader2 = new SAXReader();
//    Document doc2 = reader2.read(new File("path/to/second/xml/file.xml"));
//
//    //获取根节点并比较它们的标签名和属性。
//
//    Element root1 = doc1.getRootElement();
//    Element root2 = doc2.getRootElement();
//    public void tett(){
//        if(!root1.getQualifiedName().equals(root2.getQualifiedName()))
//        {
//            System.out.println("根节点标签名不同");
//        }
//        doc1.node(0);
//    }
//
//
//    public test() throws DocumentException {
//    }
//    //递归比较两个文档的元素和文本节点。在比较过程中，你可以使用以下方法来比较节点的属性：
//
//    private static boolean compareNodes(Node node1, Node node2) {
//        if (node1.isEqual(node2)) {
//            return true;
//        }
//        if (!compareAttributes(node1, node2)) {
//            return false;
//        }
//        // 比较子节点
//        if (!compareChildNodes(node1, node2)) {
//            return false;
//        }
//        return true;
//    }
//
//    //    实现比较属性的函数。该函数将遍历每个节点的属性，并进行比较。如果同级节点只在一个树上有，它将记录下是哪个节点。
////    java
//    private static boolean compareAttributes(Node node1, Node node2) {
//        NamedNodeMap attrs1 = node1.getAttributes();
//        NamedNodeMap attrs2 = node2.getAttributes();
//        if (attrs1.getLength() != attrs2.getLength()) {
//            System.out.println("属性数量不同");
//            return false;
//        }
//        for (int i = 0; i < attrs1.getLength(); i++) {
//            Node attr1 = attrs1.item(i);
//            Node attr2 = attrs2.getNamedItem(attr1.getNodeName());
//            if (attr2 == null || !attr1.getNodeValue().equals(attr2.getNodeValue())) {
//                System.out.println("属性不同: " + attr1.getNodeName());
//                return false;
//            }
//        }
//        return true;
//    }
//
//    //    实现比较子节点的函数。该函数将递归比较每个节点的子节点。如果发现同级节点只在一个树上有，它将记录下是哪个节点。
////    java
//    private static boolean compareChildNodes(Node node1, Node node2) {
//        if (node1.getNodeType() != node2.getNodeType()) {
//            System.out.println("节点类型不同");
//            return false;
//        }
//        if (node1.hasChildNodes() != node2.hasChildNodes()) {
//            System.out.println("节点是否有子节点不同");
//            return false;
//        }
//        if (node1.hasChildNodes()) {
//            NodeList children1 = node1.getChildNodes();
//            NodeList children2 = node2.getChildNodes();
//            if (children1.getLength() != children2.getLength()) {
//                System.out.println("子节点数量不同");
//                return false;
//            }
//            for (int i = 0; i < children1.getLength(); i++) {
//                Node child1 = children1.item(i);
//                Node child2 = children2.item(i);
//                if (!compareNodes(child1, child2)) { // 递归比较子节点
//                    return false;
//                }
//                if (child1 != child2 && child1 instanceof Element && child2 instanceof Element) { // 记录同级节点只在一个树上的情况
//                    System.out.println("同级节点只在一个树上有: " + ((Element) child1).getQualifiedName()); // 或者使用其他方式记录所需的信息，如添加到集合中或进行其他处理操作。这里仅输出信息供参考。
//                }
//            }
//        } else { // 没有子节点，只比较文本内容是否相同
//            if (!Objects.equals(node1.getText(), node2.getText())) {
//                System.out.println("文本内容不同");
//                return false;
//            }
//
//        }
//    }
}
