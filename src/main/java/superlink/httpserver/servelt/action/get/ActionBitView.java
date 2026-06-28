package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSONObject;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.init.InitClass;
import superlink.util.Utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;

@WebController(name = "bitView")
public class ActionBitView  {

    @Api(def = "bitViewFile")
    public String openFileBit(@GetParm Map<String,Object> map){
        Integer s= (Integer) map.get("s");
        String path= (String) map.get("f");
        File file=new File(path);
        String s1= Utils.getBitString(file,s,1280);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("b",s1);
        long l=file.length()/1280;
        jsonObject.put("p",l);
        return jsonObject.toJSONString();
    }
    @Api(def = "bitEdit")
    public String editFileBit(@GetParm Map<String,Object> map){
        Integer s= (Integer) map.get("s");
        String path= (String) map.get("f");
        String aByte= (String) map.get("b");

        File file=new File(path);
        try {
            byte b=Byte.valueOf(aByte);
            RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
            randomAccessFile.seek(s);
//            int i=randomAccessFile.read();
//            i=randomAccessFile.read();
//            i=randomAccessFile.read();
            randomAccessFile.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "true";
    }
    @Api(def = "vPathList")
    public String vPathList1(@GetParm String path){
        File file=new File(path);
        JSONObject json=new JSONObject();
        File[] files=file.listFiles();
        for (File f:files){
            if (f.isDirectory()){
                json.put(f.getName(),"p");
            }else {
                json.put(f.getName(),"f");
            }
        }
        return json.toJSONString();
    }

    @Api(def = "openParentP")
    public String openParentP(@GetParm String path){
        JSONObject json=new JSONObject();
        File parent=null;
        try {
            parent=new File(path).getParentFile();
        }catch (Exception e) {
        }
        if (parent==null){
            for (File fs: InitClass.roots){
                json.put(fs.getName(),"p");
            }
            return json.toJSONString();
        }else {
            for (String s: InitClass.getRootPaths()){
                if (parent.getName().equals(new File(s).getPath())){
                    for (File fs: InitClass.roots){
                        json.put(fs.getName(),"p");
                    }
                    return json.toJSONString();
                }
            }
        }
        File[] files=parent.listFiles();
        for (File f:files){
            if (f.isDirectory()){
                json.put(f.getName(),"p");
            }else {
                json.put(f.getName(),"f");
            }
        }
        return json.toJSONString();
    }
}
