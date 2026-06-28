package superlink.httpserver;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import superlink.httpserver.servelt.action.GetParm;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessMap {
    public static Map<String, Map<String, Nettybean>> mapMap=new HashMap<>();
    public static List< NettyIntercepter> interceptorList=new ArrayList<>();
    public static Map<String, NettyDeal> dealMap=new HashMap<>();

    public static class Nettybean<V> {

        Object action;
        Method method;
        Type[] classes;
        Type returnType;

        public Object getAction() {
            return action;
        }

        public Nettybean setAction(Object action) {
            this.action = action;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public Nettybean setMethod(Method method) {
            this.method = method;
            classes=method.getGenericParameterTypes();
            return this;
        }

        public Type getReturnType() {
            return returnType;
        }

        public Nettybean setReturnType(Type returnType) {
            this.returnType = returnType;
            return this;
        }

        public V re(Object... objects){

            Object[] objectss=new Object[classes.length];
            int i=0;
            for (Type c:classes){
                if (ChannelHandlerContext.class.equals(c)){
                    objectss[i]=objects[0];i++;continue;}
                if (FullHttpRequest.class.equals(c)){
                    objectss[i]=objects[1];i++;continue;}
                if (String.class.equals(c)){
                    objectss[i]=((FullHttpRequest) objects[1]).uri();
                }
                FullHttpRequest fullHttpRequest= (FullHttpRequest) objects[1];
                String s=null;
                Annotation[] annotations=method.getParameterAnnotations()[i];
                boolean b=false;


                for (Annotation a:annotations){
                    if (a instanceof GetParm){
                        b=true;
                    }
                }
                if (b){
                    try {

                        s= URLDecoder.decode(fullHttpRequest.uri());
                        String[] strings=s.split("\\?",2);
                        if (strings.length<2){
                            //((FullHttpRequest) objects[1]).uri()
                            objectss[i]=null;
                            i++;
                            continue;
                        } else {
                            s=strings[1];
                        }
                    }catch (Exception e){
                        continue;
                    }
                }else {
                    s=fullHttpRequest.content().toString(io.netty.util.CharsetUtil.UTF_8);
                }
//                if ( List.class.equals(c)){
//                    Type tp=(Type)c;
//                    ParameterizedType parameterizedType=(ParameterizedType)tp;
//                    Type[] types=parameterizedType.getActualTypeArguments();
//                    List list=JSON.parseArray(s, types);
//                    objectss[i]=list;
//                    continue;
//                }
//                if (Map.class.equals(c)){
//                    Type tp=(Type)c;
//                    ParameterizedType parameterizedType=(ParameterizedType)tp;
//                    Type typep=String.class;
//                    Map map=JSON.parseObject(s,new TypeReference<Map<Object,Object>>(){});
//                    objectss[i]=map;
//                    continue;
//                }


                if ((Type)c instanceof ParameterizedType){
                    Type[] types=((ParameterizedType)c).getActualTypeArguments();
                    if (types.length==1){
                        //List list=JSON.parseArray(s, types);
                        List list=(List) JSON.parse(s);
                        if (types[0] instanceof ParameterizedType) {
                            Type[] types1 = ((ParameterizedType) types[0]).getActualTypeArguments();
                            if (types1.length == 1) {
                                List list1 = new ArrayList();
                                list.forEach(l -> {
                                    list1.add(JSON.parseArray(((JSONObject) l).toJSONString(), types1));
                                });
                                list=list1;
                            } else if (types1.length == 2) {
                                List list1 = new ArrayList();
                                list.forEach(l -> {
                                    Map map = (Map) JSON.parse(((JSONObject) l).toJSONString());
                                    map.forEach((k, v) -> {
                                        Class ct=v.getClass();
                                        Constructor[] constructors=ct.getConstructors();
                                        if (v instanceof JSONArray) {
                                            JSONArray jsonArray = (JSONArray) v;
                                            Object tb = JSON.parseObject(jsonArray.toJSONString(),types1[1]);
                                            map.put(k, tb);
                                        }else if (v instanceof JSONObject) {
                                            JSONObject jsonObject = (JSONObject) v;
                                            Object tb = JSON.parseObject(jsonObject.toJSONString(), types1[1]);
                                            map.put(k, tb);
                                        }

                                    });
                                    list1 .add(map);
                                });
                                list=list1;

                            } else {
                                if (list.get(0) instanceof JSONObject) {
                                    List list1 = new ArrayList();
                                    list.forEach(l -> {
                                        list1.add(JSON.parseObject(((JSONObject) l).toJSONString(), types[0]));
                                    });
                                    list=list1;
                                }
                            }

//                            objectss[i] = list;
                        }
                        objectss[i]=list;
//                        continue;
                    }else if (types.length==2){
                        Map map= (Map) JSON.parse(s);

                        map.forEach((k,v)->{
                            if (v instanceof JSONObject){
                                JSONObject jsonObject= (JSONObject) v;
                                Object tb=JSON.parseObject(jsonObject.toJSONString(),types[1]);
                                map.put(k,tb);
                            }else if (v instanceof JSONArray){
                                JSONArray jsonArray=(JSONArray) v;
                                Object tb=JSON.parseObject(jsonArray.toJSONString(),types[1]);
                                map.put(k,tb);
                            }


                        });
                        objectss[i]=map;
//                        continue;
                    }else if (types.length==3){
                        Map map= (Map) JSON.parse(s);
                        map.forEach((k,v)->{
                            JSONObject object= (JSONObject) v;
                            Object tb=JSON.parseObject(object.toJSONString(),types[1]);
                            map.put(k,tb);
                        });
                        objectss[i]=map;
//                        continue;
                    }
//                    i++;
                }else {


                    Class a= null;
                    try {
                        a = Class.forName(c.getTypeName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        Class<?>[] classes=a.getInterfaces();
                        for (Class cla:classes){
                            if (cla.equals(Comparable.class)){
                                if (a.equals(String.class) ){
                                    objectss[i]=s;

                                }else {
                                    if ("".equals(s)){s="0";}
                                    Method method=a.getMethod("valueOf",String.class);
                                    Object o=method.invoke(null,s);
                                    objectss[i]=o;
//                            objectss[i]=Integer.valueOf(s);
                                }
                                break;
                            }
                        }
                        if (objectss[i]==null){
                            Object o=JSON.parseObject(s,a);
                            objectss[i]=o;
                        }


//                        if (a.equals(String.class) ){
//                            objectss[i]=s;
//                        }else if (a.equals(Integer.class) ){
//                            objectss[i]=Integer.valueOf(s);
//                        }else if (a.equals(Boolean.class) ){
//                            objectss[i]=Boolean.valueOf(s);
//                        }else if (a.equals(Long.class) ){
//                            objectss[i]=Long.valueOf(s);
//                        }else if (a.equals(Byte.class) ){
//                            objectss[i]=Byte.valueOf(s);
//                        }
//                        Object o= JSON.parseObject(s,a);
//                        objectss[i]=o;
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                i++;
            }
            try {
                return (V)method.invoke(action,objectss);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                System.out.println(((FullHttpRequest)objectss[1]).getUri());
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }catch (Error e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static class NettyIntercepter<V> {
        Object intercepter;
        Method method;
        Type[] classes;
        Type returnclass;

        public Object getIntercepter() {
            return intercepter;
        }

        public NettyIntercepter setIntercepter(Object intercepter) {
            this.intercepter = intercepter;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public NettyIntercepter setMethod(Method method) {
            this.method = method;
            classes=method.getGenericParameterTypes();
            return this;
        }

        public Type getReturnclass() {
            return returnclass;
        }

        public NettyIntercepter setReturnclass(Type returnclass) {
            this.returnclass = returnclass;
            return this;
        }

        public Object re(ChannelHandlerContext ctx, FullHttpRequest msg) {
            Object o=null;
            try {
                  o=method.invoke(intercepter, msg);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return o;
        }
    }

    public static class NettyDeal<V> {
        Object dealer;
        Method method;
        Type[] classes;
        Type returnclass;

        public Object getDealer() {
            return dealer;
        }

        public NettyDeal setDealer(Object dealer) {
            this.dealer = dealer;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public NettyDeal setMethod(Method method) {
            this.method = method;
            classes=method.getGenericParameterTypes();
            return this;
        }

        public Type getReturnclass() {
            return returnclass;
        }

        public NettyDeal setReturnclass(Type returnclass) {
            this.returnclass = returnclass;
            return this;
        }

        public Object re(Object ... objects) {
            Object o=null;
            try {
                o=method.invoke(dealer,objects);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return o;
        }
    }
}
