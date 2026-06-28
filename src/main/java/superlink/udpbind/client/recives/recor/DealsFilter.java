package superlink.udpbind.client.recives.recor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealsFilter {

    public static List<filter> filterList=new ArrayList<>();

    public static boolean process(String username,String s){
        if (filterList.size()==0){
            return true;
        }else {
            boolean b=true;
            for (filter filter:filterList){
                b=b&&filter.filtration(username,s);
                filter.handling(username,s);
            }
            return b;
        }
    }
    public static void addFilter(filter filter){
        filterList.add(filter);
    }

    public static void delFilter(filter filter){
        filterList.remove(filter);
    }


    public static class defaultFilter implements filter{

        Map<String ,List<runFilter>> map;

        public defaultFilter(){
            map=new HashMap<>();
        }

        @Override
        public boolean filtration(String n, String s) {
            List<runFilter> list=map.get(n);
            if (list==null){
                return true;
            }else {
                boolean b = true;
                for (runFilter rf:list){
                    b= b&rf.run();
                }
                return b;
            }
        }

        @Override
        public void handling(String n, String s) {

        }
    }

    @FunctionalInterface
    public static interface runFilter{

        public boolean run();

    }

}



