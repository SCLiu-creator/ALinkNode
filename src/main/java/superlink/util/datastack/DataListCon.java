package superlink.util.datastack;

public class DataListCon {

    public static DataListRW dataListRW;

    public static DataListRW getListRW(String u){
        if (dataListRW!=null){
            return dataListRW;
        }else {
            try {
                DataListRW dataListRW=new DataListRW();
                dataListRW.selectUser(u);
                DataListCon.dataListRW=dataListRW;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataListRW;
        }
    }
}
