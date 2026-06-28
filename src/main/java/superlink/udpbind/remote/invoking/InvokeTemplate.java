package superlink.udpbind.remote.invoking;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.filemanage.classprocess.AutoScan;
import superlink.httpserver.servelt.ProcessMapL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class InvokeTemplate {

//    public String startUser;

//    public int readerId;
//
//    public int writerId;
//    public InvokeTemplate(String startUser,int readerId){
//        this.startUser=startUser;
//        this.readerId=readerId;
//        this.writerId=readerId;
//    }
//    public InvokeTemplate(String startUser,int readerId,int writerId){
//        this.startUser=startUser;
//        this.readerId=readerId;
//        this.writerId=writerId;
//    }
    public int sort=0;
    
    public controllerTemp ctt;

    public reAssemableTemp rat;
    
    public Object[] objects;

    public Object RI(Object... para){
        Object obj = null;
        if (ctt!=null){
            ProcessMapL.Nettybean nettybean=null;

            nettybean=ProcessMapL.map.get(ctt.uri);
            if (nettybean==null){
                for (ProcessMapL.Nettybean n:ProcessMapL.list){
                    if (ctt.uri.contains(n.murl)){
                        nettybean=n;
                        JSONObject[] jsonObjects= (JSONObject[]) para;
                        Object[] objects=new Object[jsonObjects.length];
                        Type[] classes=nettybean.getClasses();
                        for (int i = 0; i < jsonObjects.length; i++) {
                            try {
                                objects[i]=JSON.parseObject(jsonObjects[i].toJSONString(),classes[i]);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        obj=  nettybean.re(objects);
                        break;
                    }
                }
            }else {
                JSONObject[] jsonObjects= (JSONObject[]) para;
                Object[] objects=new Object[jsonObjects.length];
                Type[] classes=nettybean.getClasses();
                for (int i = 0; i < jsonObjects.length; i++) {
                    try {
                        objects[i]=JSON.parseObject(jsonObjects[i].toJSONString(),classes[i]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                obj=  nettybean.re(para);
            }
        }
        if (rat!=null){
            AutoScan.ReAssemabling ara=AutoScan.reAssemablingMap.get(rat.beanName);
            Method method;
            if (para==null){
                Class[] cs=new Class[para.length];

                try {
                    method=ara.aClass.getMethod(rat.method);

                    JSONObject[] jsonObjects= (JSONObject[]) para;
                    Object[] objects=new Object[jsonObjects.length];
                    Type[] classes=method.getGenericParameterTypes();
                    for (int i = 0; i < jsonObjects.length; i++) {
                        try {
                            objects[i]=JSON.parseObject(jsonObjects[i].toJSONString(),classes[i]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                  
                     obj=method.invoke(ara.object,objects);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                
            }else {
                Class[] cs=new Class[para.length];
                try {
                    for (int i = 0; i < para.length; i++) {
                        cs[i]= para[i].getClass();
                    }
                    method=ara.aClass.getMethod(rat.method,cs);

                    JSONObject[] jsonObjects= (JSONObject[]) para;
                    Object[] objects=new Object[jsonObjects.length];
                    Type[] classes=method.getGenericParameterTypes();
                    for (int i = 0; i < jsonObjects.length; i++) {
                        try {
                            objects[i]=JSON.parseObject(jsonObjects[i].toJSONString(),classes[i]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    obj=method.invoke(ara.object,objects);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }
    
    public static class controllerTemp{
        public String uri;
        
    }
    public static class reAssemableTemp{
        public String beanName;
        public String method;
    }

    public static void main(String[] args) {
        String jsonString = "{\"objects\":[{\"type\": \"dog\", \"name\": \"Rex\", \"breed\": \"German Shepherd\"}, {\"type\": \"person\", \"name\": \"Alice\", \"age\": 30}]}";
        InvokeTemplate invokeTemplate= JSON.parseObject(jsonString,InvokeTemplate.class);

    }
}
