package superlink.udpbind.cloude.show;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ShowBin implements Comparable{

    public ShowBin(String user,Document document){
        this.user=user;
        this.documentfile=document;
        this.time=System.currentTimeMillis();
        Attribute attribute=document.getRootElement().attribute("t");
        if (attribute==null){
            rootTime=0;
        }else {
            rootTime=Long.valueOf(attribute.getValue());
        }
    }
    long time;
    long rootTime;
    List<Document> documents;
    String user;
    public Document documentfile;
    List<Element> elements;

    public List<String> get(){
        List<Element> elements=documentfile.getRootElement().elements();
        List<String> list=new ArrayList<>();
        String s;
        for (Element element:elements){
            Attribute attribute=element.attribute("p");
            if(attribute==null){
                attribute=element.attribute("f");
            }
            try {
                s=attribute.getValue();
                list.add(s);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return list;
    }
    public List<String> get(int start,int len){
        List<Element> elements=documentfile.getRootElement().elements();
        int s=elements.size();
        List<String> list=new ArrayList<>();
        Element element;
        String string;
        for (int i = start; i < s && i<start+len; i++) {
            element=elements.get(i);
            string=element.attribute("p").getValue();
            list.add(string);
        }
        return list;
    }

    public List<String> getByTime(){
        List<Element> elements=getElementsByTime();
        List<String> list=new ArrayList<>();
        String s;
        for (Element element:elements){
            s=element.attribute("p").getValue();
            list.add(s);
        }
        return list;
    }

    public List<Element> getElementsByTime(){
        if (this.elements==null){
            List<Element> elements=documentfile.getRootElement().elements();
            List<Element> elementList=new ArrayList<>(elements.size());
            for (Element el:elements){
                elementList.add(el);
            }
            int s=elements.size();
            Element element=elements.get(0);
            Element element1;
            Element buf;
            String string;

            boolean tb=true;
            int j=elementList.size();
            while (tb && j>0){
                tb=false;
                element=elementList.get(0);
                for (int i = 1; i < elementList.size() ; i++) {
                    element1=elementList.get(i);
                    if (compareBig(element,element1)){
                        element=element1;
                    }else {
                        tb=true;
                        elementList.set(i-1,element1);
                        elementList.set(i,element);
                    }
                }
                if (tb==false){
                    break;
                }else {
                    tb=false;
                }
                element=elementList.get(elementList.size()-1);
                for (int i = elementList.size()-2; i >0 ; i--) {
                    element1=elementList.get(i);
                    if (compareSmall(element,element1)){
                        element=element1;
                    }else {
                        tb=true;
                        elementList.set(i+1,element1);
                        elementList.set(i,element);
                    }
                }
                j--;
            }
            this.elements=elementList;
        }
        return this.elements;
    }
    public List<String> getByTime(int start,int len){
        List<Element> elements=getElementsByTime();
        int s=elements.size();
        List<String> list=new ArrayList<>();
        Element element;
        Attribute attribute;
        String string;
        for (int i = start; i < s && i<start+len; i++) {
            element=elements.get(i);
            attribute=element.attribute("f");
            if (attribute==null){
                attribute=element.attribute("p");
            }
            string=attribute.getValue();
            list.add(string);
        }
        return list;
    }

    public List<Map<String,Object>> getBodyByTime(int start,int len){
        List<Element> elements=getElementsByTime();
        return getListMap(elements.get(0).getParent(),elements,start,len);
    }

    public List<Map<String,Object>> getEleList(Element elementParent, int start,int len){
        List<Element> elements=elementParent.elements();
        List<Map<String,Object>> list=new ArrayList<>();
        Element element;
        Attribute attribute;
        String string;
        String time=null;
        int s=elements.size();
        Long t=Long.MAX_VALUE;
        long rootTime = 0;
        if (elements.size()>0){
            Attribute attt=elementParent.attribute("T");
            if (attt!=null){
                rootTime=Long.valueOf(attt.getValue());
            }
        }else {
            return list;
        }
        for (int i = start; i < s && i<start+len; i++) {
            element=elements.get(i);
            String sz="f";
            attribute=element.attribute(sz);
            if (attribute==null){
                sz="p";
                attribute=element.attribute(sz);
            }
            string=attribute.getValue();
            attribute=element.attribute("t");
            if (attribute!=null){
                time=attribute.getValue();
                t=Long.valueOf(time);
//                if (sz.equals("f")){
//                    t=t+rootTime;
//                    t=t*1000;
//                }else {
//                    t=t*1000;
//                }
                t=t+rootTime;
                t=t*1000;

            }
            Timestamp dateFormat=new Timestamp(t);
            Map map=new HashMap(2,1);
            map.put("t",dateFormat.toString());
            map.put(sz,string);
            list.add(map);
        }
        return list;
    }

    public List<Map<String,Object>> getListMap(Element elementParent,List<Element> elements, int start,int len){
        List<Map<String,Object>> list=new ArrayList<>();
        Element element;
        Attribute attribute;
        String string;
        String time=null;
        int s=elements.size();
        Long t=Long.MAX_VALUE;
        long rootTime = 0;
        if (elements.size()>0){
            Attribute attt=elementParent.attribute("T");
            if (attt!=null){
                rootTime=Long.valueOf(attt.getValue());
            }
        }else {
            return list;
        }
        for (int i = start; i < s && i<start+len; i++) {
            element=elements.get(i);
            String sz="f";
            attribute=element.attribute(sz);
            if (attribute==null){
                sz="p";
                attribute=element.attribute(sz);
            }
            string=attribute.getValue();
            attribute=element.attribute("t");
            if (attribute!=null){
                time=attribute.getValue();
                t=Long.valueOf(time);
//                if (sz.equals("f")){
//                    t=t+rootTime;
//                    t=t*1000;
//                }else {
//                    t=t*1000;
//                }
                t=t+rootTime;
                t=t*1000;

            }
            Timestamp dateFormat=new Timestamp(t);
            Map map=new HashMap(2,1);
            map.put("t",dateFormat.toString());
            map.put(sz,string);
            list.add(map);
        }
        return list;
    }
    public Element getPathElement(List<String> strings, AtomicInteger integer){
        Element element =documentfile.getRootElement();
        List<Element> elements=null;
        Attribute attribute=null;
        for (String s:strings){
            elements=element.elements();
            if (elements.size()>0){

                for (Element ele:elements){
                    attribute=ele.attribute(0);
    //                    &&"p".equals(attribute.getName()
                    if (attribute.getValue().equals(s)){
                        element=ele;
                        integer.decrementAndGet();
                        break;
                    }
                }
            }else {
                return element;
            }
        }
        return element;
    }
    public StringBuilder getAbsolut(Element element){
        ArrayDeque<String> deque=new ArrayDeque();
        while (element.getParent()!=null){
            String p=element.attribute("p").getValue();
            deque.push(p);
            element=element.getParent();
        }
        StringBuilder stringBuilder=new StringBuilder();
        while (deque.size()>0){
            stringBuilder.append(deque.pollFirst()).append("/");
        }
        return stringBuilder;
    }

    public long getTime(Element element){
        Attribute attribute=element.attribute("t");
        if (attribute==null){
            return Long.MAX_VALUE;
        }
        return Long.valueOf(attribute.getValue());
    }
    public boolean compareBig(Element element0,Element element1){
        long l0=getTime(element0);
        long l1=getTime(element1);
        return l0>=l1;
    }
    public boolean compareSmall(Element element0,Element element1){
        long l0=getTime(element0);
        long l1=getTime(element1);
        return l0<=l1;
    }



    @Override
    public int compareTo(Object o) {
        return (int) (this.time-((ShowBin)o).time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShowBin showBin = (ShowBin) o;

        return time == showBin.time;
    }

    @Override
    public int hashCode() {
        return (int) (time ^ (time >>> 32));
    }
}
