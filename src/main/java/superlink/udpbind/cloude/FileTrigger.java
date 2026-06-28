package superlink.udpbind.cloude;

import com.alibaba.fastjson2.annotation.JSONField;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.XmlParser;
import superlink.init.Initor;
import superlink.util.SHAutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.filemanage.scanpackage.FileScan.reCreateXmls;

public class FileTrigger {
    public static Map<String, CloudeSynContainer> containerMap=null;
    public CloudBin bin;//=CloudeSynContainer.Listbin;
    public Object lock=new Object();
    //包含绝对路径
    public List<String> pathlist=new ArrayList<>();
    public Document document;
    public String fileName;
    public String rootPath;//有路径符
    //该对象处理名称
    public String targetPath;//无/
    public String AbsolutePath;//无/
    public int id;
    public BlockingQueue<TargetFile> addque;
    public BlockingQueue<TargetFile> delque;
    public BlockingQueue<TargetFile> changque;

    public FileTrigger(String name){
        this.fileName= name;
        SAXReader reader=new SAXReader();
        String dirname=name;
        File file=new File(dirname);
        try {
            document=reader.read(file);
            Element root=document.getRootElement();
            List<Element> elements=root.elements();
            rootPath=root.attribute(0).getValue().replace("\\","/")+"/";
            rootPath=rootPath.replace("//","/");
            targetPath=elements.get(0).attribute(0).getValue();

        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }
        AbsolutePath=rootPath+targetPath;
        if (!Initor.sleepState){
            document=reCreateXmls(name,AbsolutePath);
        }

        List<String> cloudFilePath=new ArrayList<>();
        rootPath=XmlParser.parserXml(cloudFilePath,dirname);
        pathlist=cloudFilePath;
        addque=new LinkedBlockingQueue<>();
        delque=new LinkedBlockingQueue<>();
        changque=new LinkedBlockingQueue<>();
    }

    public void setBin(CloudBin b){
        this.bin=b;
    }

    /*根据绝对路径移除*/
    public void removeNode(String absolutepath){
        absolutepath=absolutepath.replace("\\","/")+"/";
        absolutepath=absolutepath.replace("//","/");
        String filepath=absolutepath.replace(rootPath,"");
        try {
            String[] strings=filepath.split("/");
            Element root=document.getRootElement();
            List<Element> rl=root.elements();
            AtomicReference<Element> e= new AtomicReference<>(root);
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }
            Element parent=e.get().getParent();
            if (parent.attribute(0).getValue().equals(strings[strings.length-2])){
                parent.remove(e.get());
            }
                   // .elements().remove(e.get());

        } catch (Exception e) {
            e.printStackTrace();
        }

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
        t.syb=0;
        delque.add(t);
    }

    /*根据相对路径移除*/
    public void removeNodeFlie(String absolutepath){
        absolutepath=absolutepath.replace("\\","/");
        absolutepath=absolutepath.replace("//","/");
        String filepath=absolutepath.replace(rootPath,"");
        try {
            String[] strings=filepath.split("/");
            AtomicReference<Element> e= new AtomicReference<>(document.getRootElement());
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }
            Element parent=e.get().getParent();
            if (!parent.attribute(0).getValue().equals(rootPath)){
                parent.remove(e.get());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
        t.syb=0;
        t.c=1;
        delque.add(t);
    }

    /*绝对路径*/
    public void addNode(String absolutepath){
        absolutepath=absolutepath.replace("\\","/");
        absolutepath=absolutepath.replace("//","/");
        File file=new File(absolutepath);
        String filepath=absolutepath.replace(rootPath,"");
        try {
            //String[] strings=filepath.split("\\\\");
            String[] strings=filepath.split("/");
            Element root=document.getRootElement();
            List<Element> rl=root.elements();
            AtomicReference<Element> e= new AtomicReference<>(rl.get(0));
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }
            Element element=e.get().getParent();
            XmlParser.writeXml(element,file);

        } catch (Exception e) {
            e.printStackTrace();
        }

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
//        t.hash= SHAutils.getShaFromFile(absolutepath,SHAutils.MD_5,false);
        t.syb=1;
//        t.len=new File(filepath).length();
        addque.add(t);
    }
    public void addNodeFile(String absolutepath){
        absolutepath=absolutepath.replace("\\","/");
        absolutepath=absolutepath.replace("//","/");
        String filepath=absolutepath.replace(rootPath,"");
        File file=new File(absolutepath);
        try {
            String[] strings=filepath.split("/");
            AtomicReference<Element> e= new AtomicReference<>((Element)document.getRootElement().elements().get(0));
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }
            Element element=e.get().addElement("f");
            element.addAttribute("f",file.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
        t.hash= SHAutils.getShaFromFile(absolutepath,SHAutils.MD_5,false);
        t.syb=1;
        t.c=1;
        t.len=new File(filepath).length();
        addque.add(t);
    }
    public void change(String absolutepath){
        absolutepath=absolutepath.replace("\\","/");
        absolutepath=absolutepath.replace("//","/");
        String filepath=absolutepath.replace(rootPath,"");

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
        t.syb=2;
        t.len=new File(filepath).length();
        changque.add(t);
    }

    public void changeFile(String absolutepath){
        absolutepath=absolutepath.replace("\\","/");
        absolutepath=absolutepath.replace("//","/");
        String filepath=absolutepath.replace(rootPath,"");

        TargetFile t=new TargetFile();
        t.root =rootPath;
        t.target=targetPath;
        t.path=filepath;
        t.syb=2;
        t.c=1;
        t.len=new File(filepath).length();
        changque.add(t);
    }

    public void save() throws IOException {
        XMLWriter  writer=new XMLWriter(new FileWriter(fileName));
        writer.write(document);
        writer.close();
    }
    public TargetFile deal0(){
        //todo
        return null;
    }
    public TargetFile deal1(){
        //todo
        return null;
    }
    public TargetFile deal2(){
        //todo
        return null;
    }


    public void create(String filepath){
        File file=new File(filepath);
        if (file.isFile()){

        }else {
            addNode(filepath);
        }

    }

//    public void change(String filepath){
//        File file=new File(filepath);
//
//    }

    public void delete(String filepath){
       removeNode(filepath);
    }

    public static class TargetFile{
        public String user;
        public int c;
        //0,delete;1,create;2,change;-1,moveto;-2 download
        public int syb;
        public long len;
        public String root;//不含target,无路径符
        public String target;//无路径符
        //不含rootpath，包含target path
        public String path;//含target
        public String hash;

        @JSONField(serialize = false,deserialize = false)
        private Integer hashCode;

        @Override
        public boolean equals(Object o) {
//            if (o == null || getClass() != o.getClass()) return false;
            TargetFile that = (TargetFile) o;
            return Objects.equals(hashCode(),that.hashCode());
        }
//            if (this.root.equals(targetFile.root)  && this.target.equals(targetFile.target) && this.path.equals(targetFile.path) && this.len==targetFile.len && this.syb==targetFile.syb){
//                return true;
//            }else {
//                return false;
//            }


        @Override
        public int hashCode(){
            if (hashCode==null){
                this.hashCode=Objects.hash(root , path , c , syb, len);
            }
//            return Objects.hashCode(root + path + c + syb + len);
            return hashCode;
        }

        @JSONField(serialize = false,deserialize = false)
        public String getNotTargetPath(){
            if (path==null){ return null; }
            return path.split(target+"/",2)[1];
        }
        @JSONField(serialize = false,deserialize = false)
        public String getFileName(){
            if (path==null){ return null; }
            return path.substring(path.lastIndexOf("/")+1);
        }
        @JSONField(serialize = false,deserialize = false)
        public String getATP(){
//            return new File(absolute+"/" +target+"/" +path);
            return root +path;
        }

        @Override
        public String toString() {
            return "TargetFile{" +
                    "syb='"+syb+'\'' +
                    "user='" + user + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }
    }



}

