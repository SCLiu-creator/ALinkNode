package superlink.util;

import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {


    public void praseUdp(String reply){
        //处理string
        reply=reply.substring(1,reply.length()).substring(0,reply.length()-3);

        String[] strings=reply.split("\\}"+"\\,");
        Arrays.stream(strings).forEach(s -> {
            s=s+"}";
            JSONObject jsonObject = JSON.parseObject(s);
            String stringObject=jsonObject.toJSONString();
            User acpectObject=JSON.parseObject(jsonObject.toJSONString(),User.class);
//            if (acpectObject.username==userlocal.username && acpectObject.inport==userlocal.inport && acpectObject.inaddress==userlocal.inaddress){
//                userlocal.port=acpectObject.port;
//                userlocal.address=acpectObject.address;
//
//            }
//
//            userList.add(acpectObject);
//            if (acpectObject.request=true){
//                if (acpectObject.choose==1){
//                    TcpServerBind threadBind = new TcpServerBind(acpectObject.address,acpectObject.port);
//                    threadBind.run();
//                }else if (acpectObject.choose==2){
//
//                }
//            }


        });
    }

    public static boolean checkNull(String ... strings){
        for (String s:strings){
            if (s==null){
                return true;
            }else {
                s=s.toLowerCase();
                if(s.equals("null") || s.equals("undefined") || s.equals("")){
                    return true;
                }
            }
        }
        return false;
    }

    public static User getUser(String username){
        AtomicReference<User> user=null;

        UDPclient.userMap.keySet().forEach(s->{
            if (s.equals(username)){
                user.set(UDPclient.userMap.get(s));
            }
        });
        return user.get();
    }

    public static User copyUser(User user){
        User user1=new User();
        user1.nickName=user.nickName;
        user1.username=user.username;
        user1.port=user.port;
        user1.address=user.address;
        user1.choose=user.choose;
        user1.setAddress(user.getInaddress());
        user1.inport=user.inport;
        return user1;
    }
    public static User setUser(User user0,User user){
        user0.nickName=user.nickName;
        user0.username=user.username;
        user0.port=user.port;
        user0.address=user.address;
        user0.choose=user.choose;
        user0.setAddress(user.getInaddress());
        user0.inport=user.inport;
        user0.udpstate=user.udpstate;
        return user0;
    }

    public static UserRequest getUserRequestBind(UserRequest Request){
        UserRequest userRequest=new UserRequest();
        userRequest.username=Request.username;
        userRequest.toport=Request.toport;
        try {
            userRequest.toaddress= InetAddress.getByAddress(Request.toaddress.getAddress());
            userRequest.inaddress=Request.inaddress;
            userRequest.requestaddress=Request.requestaddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        userRequest.requestport=Request.requestport;
        userRequest.inport=Request.inport;
        return userRequest;
    }
    public static User RequestUsertoUser(UserRequest userRequest){
        User user=new User();
        user.address=userRequest.requestaddress;
        user.port=userRequest.requestport;
        user.username=userRequest.username;
        user.inaddress=userRequest.inaddress;
        user.inport=userRequest.inport;
        return user;
    }
    public static UserRequest UsertoUserRequest(User user){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.address;
        userRequest.toport=user.port;
        userRequest.requestport= UDPclient.userlocal.port;
        userRequest.requestaddress=UDPclient.userlocal.address;
        userRequest.username=UDPclient.userlocal.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport=UDPclient.userlocal.inport;
        return userRequest;
    }
    public static UserRequest UsertoUserRequestbind(User user){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.address;
        userRequest.toport=user.port;
        userRequest.requestport= UDPclient.userlocal.port;
        userRequest.requestaddress=UDPclient.userlocal.address;
        userRequest.username=user.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport=UDPclient.userlocal.inport;
        return userRequest;
    }
    public static UserRequest inUsertoUserRequest(User user){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.inaddress;
        userRequest.toport=user.inport;
        userRequest.requestport= UDPclient.userlocal.inport;
        userRequest.requestaddress=UDPclient.userlocal.inaddress;
        userRequest.username=UDPclient.userlocal.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport=UDPclient.userlocal.inport;
        return userRequest;
    }
    public static UserRequest UsertoUserRequest(User user,Integer choose){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.address;
        userRequest.toport=user.port;
        userRequest.requestport= UDPclient.userlocal.port;
        userRequest.requestaddress=UDPclient.userlocal.address;
        userRequest.username=UDPclient.userlocal.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport=UDPclient.userlocal.inport;
        userRequest.choose=choose;
        return userRequest;
    }
    public static UserRequest UsertoUserRequest(User user,boolean request){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.address;
        userRequest.toport=user.port;
        userRequest.requestport= UDPclient.userlocal.port;
        userRequest.requestaddress=UDPclient.userlocal.address;
        userRequest.username=UDPclient.userlocal.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport=UDPclient.userlocal.inport;
        userRequest.request=request;
        return userRequest;
    }
    public static UserRequest toUserRequest(UserRequest userRequest){
        UserRequest inuserRequest=new UserRequest();
        inuserRequest.toaddress=userRequest.requestaddress;
        inuserRequest.toport=userRequest.requestport;
        inuserRequest.requestport= userRequest.toport;
        inuserRequest.requestaddress=userRequest.toaddress;
        inuserRequest.username=UDPclient.userlocal.username;
        inuserRequest.inaddress=UDPclient.userlocal.inaddress;
        inuserRequest.inport=UDPclient.userlocal.inport;
        return inuserRequest;
    }

    public static DataRequest URtoDR(UserRequest userRequest,String path,int pagelen){
        DataRequest dataRequest=new DataRequest();
        File file=new File(path);
        int len=Math.toIntExact(file.length()/pagelen);
        if ((file.length()/pagelen) != 0){len=len+1;}
        dataRequest.requestname=userRequest.username;
        dataRequest.dir=path;
        dataRequest.page=len;
        dataRequest.filename=file.getName();
        return dataRequest;
    }
    public static DataRequest URtoDR(UserRequest userRequest,String path,int id,int pagelen){
        DataRequest dataRequest=new DataRequest();
        File file=new File(path);
        int len=Math.toIntExact(file.length()/pagelen);
        if ((file.length()/pagelen) != 0){len=len+1;}
        dataRequest.requestname=userRequest.username;
        dataRequest.dir=path;
        dataRequest.page=len;
        dataRequest.filename=file.getName();
        dataRequest.id=id;
        return dataRequest;
    }

    public static String toLF(String perx,String name,String path,int pagelen){
        DataRequest dataRequest=new DataRequest();
        File file=new File(path);
        int len=Math.toIntExact(file.length()/pagelen);
        if ((file.length()/pagelen) != 0){len=len+1;}
        dataRequest.requestname=name;
        dataRequest.dir=path;
        dataRequest.page=len;
        dataRequest.filename=file.getName();

        return perx+JSON.toJSONString(dataRequest);
    }
    public static String toLF(String perx,String name,String path,int id,int pagelen){
        DataRequest dataRequest=new DataRequest();
        File file=new File(path);
        int len=Math.toIntExact(file.length()/pagelen);
        if ((file.length()/pagelen) != 0){len=len+1;}
        dataRequest.requestname=name;
        dataRequest.dir=path;
        dataRequest.page=len;
        dataRequest.filename=file.getName();
        dataRequest.id=id;

        return perx+JSON.toJSONString(dataRequest);
    }
    public static String toLF(String name,String path,int id,int pagelen){
        DataRequest dataRequest=new DataRequest();
        File file=new File(path);
        int len=Math.toIntExact(file.length()/pagelen);
        if ((file.length()/pagelen) != 0){len=len+1;}
        dataRequest.requestname=name;
        dataRequest.dir=path;
        dataRequest.page=len;
        dataRequest.filename=file.getName();
        dataRequest.id=id;

        return JSON.toJSONString(dataRequest);
    }
    public static void changeport(int port){
        String filePath = "web/electron-quick-start/main.js"; // 替换为你的文件路径
        StringBuilder content = new StringBuilder();

        try {
            // 读取文件内容
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("mainWindow.loadURL")){
                    int star=line.lastIndexOf(":")+1;
                    int over=line.lastIndexOf("'");
                    String sp=line.substring(star,over);
                    line=line.replace(sp,String.valueOf(port));
                }
                content.append(line).append("\n");
//                Content += line + "\n"; // 添加换行符，以便正确处理多行内容
            }
            reader.close();

            // 查找和替换特定字符或字符串
//            String targetString = "old_string"; // 替换为你想要查找和替换的字符串
//            String replacementString = "new_string"; // 替换为你想要的新字符串
//            newContent = oldContent.replace(targetString, replacementString);

            // 写回文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getPic(String path){
        File file=new File(path);
        if (!file.exists()){
            file=new File(XmlParser.dir+path);
        }
        return file;
    }

    public enum SortMode {
        BY_NAME,          // 按文件名排序（区分大小写）
        BY_NAME_IGNORE_CASE, // 按文件名排序（不区分大小写）
        BY_MODIFIED_TIME, // 按最后修改时间排序
        NATURAL_ORDER     // 按自然顺序（File.compareTo()，通常等同于 BY_NAME）
    }

    public static File[] sortFiles(File[] files, SortMode mode) {
        if (files == null) {
            return new File[0];
        }

        return Arrays.stream(files)
                .sorted(getComparator(mode))
                .toArray(File[]::new);
    }

    // 自定义 Comparator，支持 "file10.jpg" 这样的文件名
    private static final Comparator<File> NATURAL_ORDER_COMPARATOR = new Comparator<File>() {
        private final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

        @Override
        public int compare(File a, File b) {
            String nameA = a.getName();
            String nameB = b.getName();
            return compareNatural(nameA, nameB);
        }

        private int compareNatural(String a, String b) {
            Matcher matcherA = NUMBER_PATTERN.matcher(a);
            Matcher matcherB = NUMBER_PATTERN.matcher(b);

            int posA = 0, posB = 0;
            while (matcherA.find(posA) && matcherB.find(posB)) {
                String numStrA = matcherA.group();
                String numStrB = matcherB.group();
                int numA = Integer.parseInt(numStrA);
                int numB = Integer.parseInt(numStrB);

                if (numA != numB) {
                    return Integer.compare(numA, numB);
                }

                posA = matcherA.end();
                posB = matcherB.end();
            }

            // 如果数字部分相同，按剩余部分字典序排序
            int remainingCompare = a.substring(posA).compareTo(b.substring(posB));
            if (remainingCompare != 0) {
                return remainingCompare;
            }

            // 如果所有数字部分相同，按文件名长度排序（可选）
            return Integer.compare(a.length(), b.length());
        }
    };
    private static Comparator<File> getComparator(SortMode mode) {
        switch (mode) {
            case BY_NAME:
                return Comparator.comparing(File::getName);
            case BY_NAME_IGNORE_CASE:
                return Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
            case BY_MODIFIED_TIME:
                return Comparator.comparingLong(File::lastModified);
            case NATURAL_ORDER:
                return NATURAL_ORDER_COMPARATOR; // 或直接使用 (a, b) -> a.compareTo(b)
            default:
                throw new IllegalArgumentException("Unsupported sort mode: " + mode);
        }
    }
//    public static Object[] mergeAndDeduplicate(Object[] files1, Object[] files2) {
//        // 使用 LinkedHashSet 保持插入顺序并去重
//        Set<Object> mergedSet = new LinkedHashSet<>();
//
//        // 添加第一个数组的所有元素
//        if (files1 != null) {
//            mergedSet.addAll(Arrays.asList(files1));
//        }
//
//        // 添加第二个数组的所有元素（自动去重）
//        if (files2 != null) {
//            mergedSet.addAll(Arrays.asList(files2));
//        }
//
//        // 转换回 File[]
//        return mergedSet.toArray(new Object[0]);
//    }
    public static <T> T[] mergeAndDeduplicate(T[] files1, T[] files2, T[] dummyArray) {
        Set<T> mergedSet = new LinkedHashSet<>();

        if (files1 != null) {
            mergedSet.addAll(Arrays.asList(files1));
        }

        if (files2 != null) {
            mergedSet.addAll(Arrays.asList(files2));
        }

        return mergedSet.toArray(dummyArray);
    }

    public static String esc(String s){
        return s.replace("\\","/");
    }

    public static String replace(String s){
        String regex="[\\/]+";
        regex="(\\/[^/]*)|(//)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        String result = matcher.replaceAll("/");
        return result;
    }

    private static boolean isSlash(char c) {
        return (c == '\\') || (c == '/');
    }

    private static boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }
    //使用
    public static String normalize2(String path) {
        int n = path.length();
        char slash = '/';
        char altSlash = '\\';
        char prev = 0;
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if (c == altSlash) {
                // 如果是反斜杠，并且前一个字符也是反斜杠，则替换为正斜杠
                if (prev == altSlash) {
                    sb.append(slash);
                    prev = 0; // 重置前一个字符为0，表示没有字符
                } else {
                    // 如果是反斜杠，并且前一个字符不是反斜杠，则替换为正斜杠
                    if (prev!=slash){
                        sb.append(slash);
                    }
                    prev = slash; // 更新前一个字符为正斜杠
                }
            } else if (c == slash) {
                // 如果是正斜杠，并且前一个字符也是正斜杠，则跳过
                if (prev == slash) {
                   // i++; // 跳过当前的正斜杠
                } else {
                    sb.append(slash);
                    prev = slash; // 更新前一个字符为正斜杠
                }
            } else {
                // 如果是其他字符，直接添加到sb中
                sb.append(c);
                prev = c; // 更新前一个字符
            }
        }
        // 处理最后一个字符
        if (prev == altSlash) {
            sb.append(slash);
        }
        return sb.toString();
    }
    public static String normalize1(String path) {
        int n = path.length();
        char slash = '/';
        char altSlash = '\\';
        char prev = 0;
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if (c == altSlash) {
                // 如果是反斜杠，并且前一个字符也是反斜杠，则替换为正斜杠
                if (prev == altSlash) {
                    sb.append(slash);
                    prev = 0; // 重置前一个字符为0，表示没有字符
                } else {
                    // 如果是反斜杠，并且前一个字符不是反斜杠，则保留反斜杠
                    sb.append(altSlash);
                    prev = altSlash; // 更新前一个字符为反斜杠
                }
            } else if (c == slash) {
                // 如果是正斜杠，并且前一个字符也是正斜杠，则跳过
                if (prev == slash) {
                    i++; // 跳过当前的正斜杠
                } else {
                    sb.append(slash);
                    prev = slash; // 更新前一个字符为正斜杠
                }
            } else {
                // 如果是其他字符，直接添加到sb中
                sb.append(c);
                prev = c; // 更新前一个字符
            }
        }
        // 处理最后一个字符
        if (prev == altSlash) {
            sb.append(slash);
        }
        return sb.toString();
    }
    public static String normalize(String path) {
        int n = path.length();
        char slash = '/';
        char altSlash = '\\';
        char prev = 0;
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if (c == altSlash)
                return normalize(path, n, (prev == slash) ? i - 1 : i);
            if ((c == slash) && (prev == slash) && (i > 1))
                return normalize(path, n, i - 1);
            if ((c == ':') && (i > 1))
                return normalize(path, n, 0);
            prev = c;
        }
        if (prev == slash) return normalize(path, n, n - 1);
        return path;
    }
    private static String normalize(String path, int len, int off) {
        if (len == 0) return path;
        if (off < 3) off = 0;   /* Avoid fencepost cases with UNC pathnames */
        int src;
        char slash = '/';
        StringBuffer sb = new StringBuffer(len);

        if (off == 0) {
            /* Complete normalization, including prefix */
            src = normalizePrefix(path, len, sb);
        } else {
            /* Partial normalization */
            src = off;
            sb.append(path.substring(0, off));
        }

        while (src < len) {
            char c = path.charAt(src++);
            if (isSlash(c)) {
                while ((src < len) && isSlash(path.charAt(src))) src++;
                if (src == len) {
                    /* Check for trailing separator */
                    int sn = sb.length();
                    if ((sn == 2) && (sb.charAt(1) == ':')) {
                        /* "z:\\" */
                        sb.append(slash);
                        break;
                    }
                    if (sn == 0) {
                        /* "\\" */
                        sb.append(slash);
                        break;
                    }
                    if ((sn == 1) && (isSlash(sb.charAt(0)))) {
                        sb.append(slash);
                        break;
                    }
                    break;
                } else {
                    sb.append(slash);
                }
            } else {
                sb.append(c);
            }
        }

        String rv = sb.toString();
        return rv;
    }
    private static int normalizePrefix(String path, int len, StringBuffer sb) {
        int src = 0;
        while ((src < len) && isSlash(path.charAt(src))) src++;
        char c;
        if ((len - src >= 2)
                && isLetter(c = path.charAt(src))
                && path.charAt(src + 1) == ':') {
            sb.append(c);
            sb.append(':');
            src += 2;
        } else {
            src = 0;
            if ((len >= 2) && isSlash(path.charAt(0)) && isSlash(path.charAt(1))) {
                src = 1;
                sb.append('/');
            }
        }
        return src;
    }

    public static String getPrex(String s){
        int i=s.lastIndexOf(".");
        if (i<0){
            return "";
        }
        return s.substring(i).toLowerCase();
    }
    public static String getPrexs(String s){
        int i=s.lastIndexOf(".");
        if (i<0){
            return "";
        }
        return s.substring(i+1).toLowerCase();
    }
    public static String getType(String s){
        int i=s.lastIndexOf(".");
        if (i<0){
            return ".d";
        }
        return s.substring(i+1).toLowerCase();
    }

    public static File createFile(String path,String filePath) throws IOException {
        File file=new File(filePath);
        String p=file.getParent();
        File parent=new File(p);
        if (!parent.exists()){
            parent.mkdirs();
        }
        if (!file.exists()){
            file.createNewFile();
        }
        return file;
    }

    public static boolean copyFileIfNotExists(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);

        return copyFileIfNotExists(sourceFile,targetFile);
    }

    public static boolean copyFileIfNotExists(File sourceFile, File targetFile) throws IOException {

        // 检查源文件是否存在
        if (!sourceFile.exists()) {
            throw new IOException("源文件不存在: " + sourceFile);
        }

        // 检查是否是同一个文件
        try {
            if (sourceFile.getCanonicalPath().equals(targetFile.getCanonicalPath())) {
                System.out.println("源文件和目标文件是同一个文件，跳过拷贝: " + sourceFile);
                return false;
            }
        } catch (IOException e) {
            throw new IOException("无法解析文件路径", e);
        }

        // 检查目标文件是否已存在
        if (targetFile.exists()) {
            System.out.println("目标文件已存在，跳过拷贝: " + targetFile);
            return false;
        }

        // 确保目标目录存在
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 执行拷贝
        try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
             FileChannel targetChannel = new FileOutputStream(targetFile).getChannel()) {
            sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        }

        System.out.println("文件拷贝成功: " + sourceFile + " -> " + targetFile);
        return true;
    }
    public static void getFiles(List list, File pathFile, int i){
        try {
            if (i<=0){
                return;
            }
            File[] files=pathFile.listFiles();
            for (File file:files){
                if (file.isDirectory()){
                    getFiles(list,file,i-1);
                }else {
                   list.add(file);
                }
            }
        }catch (Exception e){
            System.out.println("getFiles  "+e.getMessage());
            System.out.println(pathFile);
        }
    }
    public static void getFiles(List<File> list, File pathFile){
        try {
            File[] files=pathFile.listFiles();
            for (File file:files){
                if (file.isDirectory()){
                    getFiles(list,file);
                }else {
                    list.add(file);
                }
            }
        }catch (Exception e){
            System.out.println("getFiles  "+e.getMessage());
            System.out.println(pathFile);
        }
    }

    public static class IdGenerator {

        // 中国身份证地区编码
        private static final String[] REGION_CODES = {
                "110000", "120000", "130000", // 示例：北京、天津、河北
                // ... 这里应该包含所有省市的地区代码
        };

        public static String generateId() {
            // 随机选择一个地区代码
            String regionCode = REGION_CODES[new Random().nextInt(REGION_CODES.length)];
            // 随机生成出生日期
            String birthDate = generateBirthDate();
            // 随机生成顺序码
            String sequenceCode = String.format("%03d", new Random().nextInt(999));
            // 计算校验码
            String preCheckCode = regionCode + birthDate + sequenceCode;
            char checkCode = calculateCheckCode(preCheckCode);
            // 拼接完整的身份证号码
            return preCheckCode + checkCode;
        }

        private static String generateBirthDate() {
            Random random = new Random();
            int year = 1900 + random.nextInt(121); // 1900年到2020年之间
            int month = 1 + random.nextInt(12); // 1到12月
            int day;
            if (month == 2) {
                day = 1 + random.nextInt(28); // 2月通常28天
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 1 + random.nextInt(30); // 小月30天
            } else {
                day = 1 + random.nextInt(31); // 大月31天
            }
            return String.format("%04d%02d%02d", year, month, day);
        }

        private static char calculateCheckCode(String id17) {
            int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2}; // 加权因子
            char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'}; // 校验码值
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                sum += (id17.charAt(i) - '0') * weight[i];
            }
            return checkCodes[sum % 11];
        }
    }

}
