package superlink.udpbind.cloude.util;

import org.checkerframework.checker.units.qual.N;

import java.util.*;

public class TendMap {
    //key不含‘/’
    public Map<String,Node> map;
    public String name;
    //cloudePage:<loaclPath>
    public TendMap(){
        this.map=new HashMap<>();
    }
    //入参 localpath:<cloudpath>
    public static TendMap toTendMap(List<Map<String,List<String>>> mapList){
        TendMap tendMap=new TendMap();
        mapList.forEach(map->{
            map.forEach((k,v)->{
                v.forEach(s->{
                    Node node=tendMap.map.get(s);
                    if (node==null){
                        node=new Node();
                        tendMap.map.put(s,node);
                    }
                    node.add(k);
                });

            });
        });
        return tendMap;
    }

    public void put(String in,String to){
        Node node = map.get(in);
        if(node==null){
            node=new Node();
            map.put(in,node);
            node.in=in;
        }
        node.add(to);
    }
    public void put(String in,List list){
        Node node = map.get(in);
        if(node==null){
            node=new Node();
            map.put(in,node);
            node.in=in;
        }
        node.addAll(list);
    }
    public List<String> get(String in){
        Node n=map.get(in);
        if (n==null){
            return null;
        }
        return n.get();
    }
    public String[] getInList(){
        Set<String> list=new HashSet<>();
        map.forEach((k,v)->{
            list.addAll(v.to);
        });

        return (String[]) list.toArray();
    }

    //lsit.e 是absolutpath
    //node中路径不含‘/’
    public static class Node{
        public Node(){
            to=new ArrayList<>();
        }
        public String in;
        public List<String> to;

        public void add(String s){
            to.add(s);
        }
        public void add(List<String> list){
            for (String s:list){
                to.add(s);
            }
        }
        public void addAll(List<String> list){
            to.addAll(list);
        }
        public void remove(String s){
            to.remove(s);
        }
        public List<String> get(){
            return to;///(String[]) to.toArray();
        }

    }
}
