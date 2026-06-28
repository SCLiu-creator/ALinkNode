package superlink.udpbind.ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//ftp://anonymous:123@192.168.0.141:2121/D:\build
public class FtpServerdk {
    // 配置常量 - 替代magic number
    private static final int CONTROL_PORT = 2121;          // FTP控制连接端口
    private static final int DATA_PORT_RANGE_START = 50000; // 数据端口范围起始
    private static final int DATA_PORT_RANGE_END = 50100;   // 数据端口范围结束
    private static final String ROOT_DIR = "D:\\";       // 根目录
    private static final String WELCOME_MSG = "220 详细FTP服务器 v1.0"; // 欢迎消息
    private static final String ANONYMOUS_USER = "anonymous"; // 匿名用户名

    // 响应代码常量
    private static final String CODE_150 = "150 ";          //文件状态正常，准备打开数据连接

    private static final String CODE_200 = "200 ";          //命令成功执行
    private static final String CODE_213 = "215 ";          //返回文件长度
    private static final String CODE_215 = "215 ";          //系统类型响应
    private static final String CODE_220 = "220 ";         // 服务就绪
    private static final String CODE_221 = "221 ";         // 服务关闭控制连接
    private static final String CODE_226 = "226 ";         // 关闭数据连接，文件操作成功
    private static final String CODE_227 = "227 ";         // 进入被动模式
    private static final String CODE_230 = "230 ";         // 用户登录成功
    private static final String CODE_250 = "250 ";         // 请求文件操作完成
    private static final String CODE_257 = "257 ";         // 路径创建成功

    private static final String CODE_331 = "331 ";         // 需要用户密码
    private static final String CODE_350 = "350 ";         // 续传

    private static final String CODE_425 = "425 ";          //无法打开数据连接
    private static final String CODE_426 = "426" ;          //连接中断; 传输中止
    private static final String CODE_450 = "450 ";          //文件不可用
    private static final String CODE_451 = "451 ";          //处理请求时遇到本地错误

    private static final String CODE_500 = "500 ";         // 语法错误
    private static final String CODE_501 = "501 ";         // 参数语法错误
    private static final String CODE_503 = "503 ";         // 请先使用PASV或PORT命令
    private static final String CODE_502 = "502 ";         // 命令未实现
    private static final String CODE_530 = "530 ";         // 登录认证失败
    private static final String CODE_550 = "550 ";         // 请求操作未执行

    public static void main(String[] args) {
        // 创建根目录
        new File(ROOT_DIR).mkdirs();

        try (ServerSocket serverSocket = new ServerSocket(CONTROL_PORT)) {
            System.out.println("FTP服务器已启动，监听端口: " + CONTROL_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接: " + clientSocket.getInetAddress());
                new Thread(new FtpSession(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("服务器错误: " + e.getMessage());
        }
    }

    private static class FtpSession implements Runnable {

        private Socket controlSocket;          // 控制连接socket
        private Socket dataSocket;             // 数据连接socket
        private BufferedReader controlReader;  // 控制连接输入
        private PrintWriter controlWriter;     // 控制连接输出
        private File currentDir;               // 当前目录
        private String username;               // 登录用户名
        private boolean loggedIn = false;      // 登录状态
        private boolean passiveMode = false;   // 是否被动模式
        private int passivePort;               // 被动模式端口
        private String iso = "ISO-8859-1";

        private String encoding = "GBK"; // Windows中文系统默认编码
        // 添加传输模式字段
        private boolean isBinary = false;

        // 日期格式化
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm", Locale.US);

        public FtpSession(Socket socket) {
            this.controlSocket = socket;
            this.currentDir = new File(ROOT_DIR).getAbsoluteFile();
        }

        @Override
        public void run() {
            try {
                // 初始化I/O
                controlReader = new BufferedReader(
                        new InputStreamReader(controlSocket.getInputStream(),encoding));
                controlWriter = new PrintWriter(
                        new OutputStreamWriter(controlSocket.getOutputStream()), true);

                // 发送欢迎消息
                sendControlResponse(CODE_220 + WELCOME_MSG);

                // 主命令处理循环
                String command;
                while ((command = controlReader.readLine()) != null) {
                    System.out.println("收到命令: " + command);
                    processCommand(command.trim());
                }
            } catch (IOException e) {
                if (!controlSocket.isClosed()) {
                    System.err.println("客户端连接错误: " + e.getMessage());
                }
            } finally {
                closeConnections();
            }
        }



        private void processCommand(String command) throws IOException {
            String[] parts = command.split(" ");
            String cmd = parts[0].toUpperCase();
            String arg = parts.length > 1 ? command.substring(parts[0].length() + 1) : null;

            switch (cmd) {
                case "USER":
                    handleUser(arg);
                    break;
                case "FEAT":
                    handleFeat();
                    break;
                case "PASS":
                    handlePass(arg);
                    break;
                case "PWD":
                case "XPWD":
                    handlePwd();
                    break;
                case "CWD":
                case "XCWD":
                    handleCwd(arg);
                    break;
                case "CDUP":
                case "XCUP":
                    handleCdup();
                    break;
                case "LIST":
                    handleList(arg);
                    break;
                case "SIZE":
                    handleSize(arg);
                    break;
                case "REST":
                    handleRest(arg);
                    break;
                case "PASV":
                    handlePasv();
                    break;
                case "PORT":
                    handlePort(arg);
                    break;
                case "RETR":
                    handleRetr(arg);
                    break;
                case "STOR":
                    handleStor(arg);
                    break;
                case "RNFR":
                    handleRName(arg);
                    break;
                case "DELE":
                    handleDele(arg);
                    break;
                case "MKD":
                case "XMKD":
                    handleMkd(arg);
                    break;
                case "OPTS":
                    handleCode(arg);
                    break;
                case "RMD":
                case "XRMD":
                    handleRmd(arg);
                    break;
                case "QUIT":
                    handleQuit();
                    break;
                case "SYST":
                    sendControlResponse(CODE_215 + "WINDOWS Type: 11");
//                    sendControlResponse(CODE_215 + "UNIX Type: L8");
                    break;
                case "TYPE":
//                    sendControlResponse(CODE_200 + "Type set to " + (arg != null ? arg : "ASCII"));
                    if (arg != null) {
                        if (arg.equalsIgnoreCase("I")) {
                            isBinary = true;
                            sendControlResponse(CODE_200 + "Type set to binary");
                        } else if (arg.equalsIgnoreCase("A")) {
                            isBinary = false;
                            sendControlResponse(CODE_200 + "Type set to ASCII");
                        } else {
                            sendControlResponse(CODE_500 + "Unsupported type");
                        }
                    }
                    break;
                case "NOOP":
                    sendControlResponse(CODE_200 + "NOOP ok");
                    break;
                default:
                    sendControlResponse(CODE_502 + "命令未实现: " + cmd);
                    System.out.println("未处理的命令: " + command);
            }
        }

        private void handleUser(String username) {
            this.username = username;
            // 允许匿名登录
            if (ANONYMOUS_USER.equalsIgnoreCase(username)) {
                sendControlResponse(CODE_331 + "匿名登录，不需要密码");
                loggedIn = true;
            } else {
                sendControlResponse(CODE_331 + "需要用户密码");
            }
        }

        private void handleFeat() {
            // 构造功能列表（根据实际支持的功能调整）
            StringBuilder features = new StringBuilder();
            features.append("211-Features supported:\r\n");
            features.append(" MDTM\r\n");          // 文件修改时间
            features.append(" SIZE\r\n");          // 文件大小查询
            features.append(" REST STREAM\r\n");   // 断点续传
            features.append(" UTF8\r\n");          // UTF-8编码支持
            features.append("211 End\r\n");        // 结束标记

            // 发送响应
            sendControlResponse(features.toString());
        }

        private void handlePass(String password) {
            // 简单认证 - 实际项目中应该更安全
            if (ANONYMOUS_USER.equalsIgnoreCase(username) || "password".equals(password)) {
                loggedIn = true;
                sendControlResponse(CODE_230 + "用户 " + username + " 登录成功");
            } else {
                sendControlResponse(CODE_530 + "登录认证失败");
                loggedIn = false;
            }
        }

        private void handlePwd() {
            if (!checkLogin()) return;
            sendControlResponse(CODE_257 + "\"" + currentDir.getPath() + "\"");
        }

        private void handleCwd(String path) {
            if (!checkLogin()) return;

            if (path == null || path.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少路径参数");
                return;
            }

            try {
                File newDir;
                if (path.startsWith("/")) {
                    //去除第一个字符
                    path = path.substring(1);
                    Path path1= Paths.get(path);
                    if(path1.isAbsolute()){
                        newDir = new File(path).getAbsoluteFile();
                    }else {
                        newDir = new File(ROOT_DIR + path).getAbsoluteFile();
                    }

                    if(!newDir.exists()){
                        sendControlResponse(CODE_550 + "目录不存在");
                        return;
                    }

//                    newDir = new File(ROOT_DIR + path).getAbsoluteFile();
                } else {
                    Path path1= Paths.get(path);
                    if(path1.isAbsolute()){
                        newDir = new File(path).getAbsoluteFile();
                    }else {
                        newDir = new File(currentDir + path).getAbsoluteFile();
                    }
                }


                // 防止跳出根目录
                if (!newDir.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!newDir.exists() || !newDir.isDirectory()) {
                    sendControlResponse(CODE_550 + "目录不存在");
                    return;
                }

                currentDir = newDir;
                sendControlResponse(CODE_250 + "目录成功更改为 " + currentDir.getPath());
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "目录更改失败");
            }
        }

        private void handleCdup() {
            if (!checkLogin()) return;
            handleCwd("..");
        }

        private void handleList(String path) {
            if (!checkLogin()) return;
            if("-al".equals(path)){
                path=null;
            }
            try {
                // 如果没有指定路径，使用当前目录
                File dir = (path == null || path.isEmpty()) ? currentDir :
                        (path.startsWith("/") ? new File(ROOT_DIR + path) : new File(currentDir, path));

                if (!dir.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!dir.exists() || !dir.isDirectory()) {
                    sendControlResponse(CODE_550 + "目录不存在");
                    return;
                }

                // 建立数据连接
                if (!setupDataConnection()) {
                    return;
                }

                sendControlResponse(CODE_150 + "打开ASCII模式数据连接");

                // 发送文件列表
                PrintWriter dataWriter = new PrintWriter(
                        new OutputStreamWriter(dataSocket.getOutputStream(),"GBK"), true);

                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        // 模拟UNIX风格的列表: 权限 链接数 用户 组 大小 日期 名称
                        String permissions = file.isDirectory() ? "drwxr-xr-x" : "-rw-r--r--";
                        String size = String.format("%10d", file.length());
                        String date = dateFormat.format(new Date(file.lastModified()));
//                        dataWriter.println(permissions + " 1 owner group " + size + " " + date + " " + file.getName());

                        // 修改LIST输出格式为Windows兼容
                        String listing = String.format("%s 1 owner group %12d %s %s",
                                permissions,
                                file.length(),
                                new SimpleDateFormat("MMM dd yyyy").format(file.lastModified()),
                                file.getName());
                        dataWriter.println(permissions + " 1 owner group " + size + " " + date + " " + file.getName());

                    }
                }

                closeDataConnection();
                sendControlResponse(CODE_226 + "传输完成");
            } catch (IOException e) {
                sendControlResponse(CODE_450 + "文件不可用");
            }
        }

        private long restartOffset = 0;

        private void handleRest(String offset) {
            try {
                restartOffset = Long.parseLong(offset);
                sendControlResponse(CODE_350 + "重启偏移量设置为 " + restartOffset);
            } catch (NumberFormatException e) {
                sendControlResponse(CODE_501 + "无效偏移量");
            }
        }
        private void handleSize(String path) {
            if (!checkLogin()) return;
            if (path == null || path.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少路径参数");
                return;
            }

            try {
                File file = new File(currentDir, path);
                restartOffset = 0;
                // 安全检查：防止目录遍历
                if (!file.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!file.exists()) {
                    sendControlResponse(CODE_550 + "文件不存在");
                } else if (file.isDirectory()) {
                    sendControlResponse(CODE_550 + "路径是目录");
                } else if (!file.canRead()) {
                    sendControlResponse(CODE_550 + "文件不可读");
                } else {
                    String str = " 字节";
                    sendControlResponse(CODE_213 + file.length()); // 明确单位
                }
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "无法获取文件大小");
            }
        }

        private void handleRetr(String filename) {
            if (!checkLogin()) return;
            if (!checkDataConnection()) return;
            if (filename == null || filename.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少文件名");
                return;
            }

            try {
                File file = new File(currentDir, filename);

                // 安全检查 - 防止目录遍历
                if (!file.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!file.exists() || !file.isFile()) {
                    sendControlResponse(CODE_550 + "文件不存在");
                    return;
                }
                if (!file.canRead()) {
                    sendControlResponse(CODE_550 + "文件不可读");
                    return;
                }

                // 发送传输开始响应
                sendControlResponse(CODE_150 + (isBinary ? "打开二进制数据连接" : "打开ASCII模式数据连接"));

                // 使用 try-with-resources 自动管理流
                try {
                    if (!isBinary) {
                        // ASCII模式处理（文本文件，转换换行符）
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(file), encoding));
                             BufferedWriter writer = new BufferedWriter(
                                     new OutputStreamWriter(dataSocket.getOutputStream(), encoding))) {

                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.write(line);
                                writer.write("\r\n"); // 转换为Windows换行符
                                writer.flush();
                            }

                        }catch (Exception e){
                            e.printStackTrace();;
                        }
                    } else {
                        // 二进制模式处理（直接传输字节）
                        try (InputStream fileInput = new FileInputStream(file);
                             OutputStream dataOutput = dataSocket.getOutputStream()) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;

                            // 处理续传偏移量
                            if (restartOffset > 0) {
                                long skipped = fileInput.skip(restartOffset);
                                if (skipped != restartOffset) {
                                    sendControlResponse(CODE_550 + "无法定位到指定偏移量");
                                    return;
                                }
                            }

                            while ((bytesRead = fileInput.read(buffer)) != -1) {
                                try {
                                    dataOutput.write(buffer, 0, bytesRead);
                                    dataOutput.flush();
                                } catch (IOException e) {
                                    System.out.println("客户端中断传输: " + e.getMessage());
                                    break;
                                }
                            }
                        }
                    }

                    closeDataConnection();
                    sendControlResponse(CODE_226 + "文件传输完成");
                } catch (IOException e) {
                    System.err.println("文件传输失败: " + e.getMessage());
                    sendControlResponse(CODE_426 + "连接中断; 传输中止");
                    return;
                }

                // 无论成功与否，都重置重启偏移量
                restartOffset = 0;

            } catch (IOException e) {
                sendControlResponse(CODE_550 + "文件操作错误: " + e.getMessage());
                restartOffset = 0; // 发生错误时也重置偏移量
            }
        }
        private void handlePasv() {
            if (!checkLogin()) return;

            // 关闭任何现有的数据连接
            if (dataSocket != null && !dataSocket.isClosed()) {
                try {
                    dataSocket.close();
                } catch (IOException e) { /* 忽略 */ }
            }

            passiveMode = true;

            // 在指定范围内寻找可用端口
            for (int port = DATA_PORT_RANGE_START; port <= DATA_PORT_RANGE_END; port++) {
                try {
                    ServerSocket passiveSocket = new ServerSocket(port);
                    passivePort = port;

                    // 获取服务器IP地址
                    String ip = controlSocket.getLocalAddress().getHostAddress();
                    // 替换.为, (FTP PASV模式要求)
                    String pasvIp = ip.replace('.', ',');

                    int p1 = passivePort / 256;
                    int p2 = passivePort % 256;

                    sendControlResponse(CODE_227 + "进入被动模式 (" + pasvIp + "," + p1 + "," + p2 + ")");

                    // 等待客户端连接
                    dataSocket = passiveSocket.accept();
                    passiveSocket.close();
                    return;
                } catch (IOException e) {
                    // 端口不可用，继续尝试下一个
                }
            }
            sendControlResponse(CODE_451 + "无法找到可用端口");
        }

        private void handlePort(String arg) {
            if (!checkLogin()) return;

            // 关闭被动模式
            passiveMode = false;

            try {
                String[] parts = arg.split(",");
                if (parts.length != 6) {
                    sendControlResponse(CODE_501 + "PORT参数错误");
                    return;
                }

                // 解析IP和端口
                String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

                // 验证端口范围 (通常客户端使用>1023的端口)
                if (port < 1024 || port > 65535) {
                    sendControlResponse(CODE_501 + "无效端口号");
                    return;
                }

                // 尝试连接 (实际项目中应该使用线程池管理)
                new Thread(() -> {
                    try {
                        dataSocket = new Socket(ip, port);
                        sendControlResponse(CODE_200 + "PORT命令成功");
                    } catch (IOException e) {
                        sendControlResponse(CODE_425 + "无法建立数据连接");
                    }
                }).start();
            } catch (NumberFormatException e) {
                sendControlResponse(CODE_501 + "PORT参数格式错误");
            }
        }

        private void handleStor(String filename) {
            if (!checkLogin()) return;
            if (!checkDataConnection()) return;

            if (filename == null || filename.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少文件名");
                return;
            }

            try {
                File file = new File(currentDir, filename);

                // 安全检查
                if (!file.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                sendControlResponse(CODE_150 + "准备接收文件");

                // 接收文件
                try (OutputStream fileOutput = new FileOutputStream(file);
                     InputStream dataInput = dataSocket.getInputStream()) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = dataInput.read(buffer)) != -1) {
                        fileOutput.write(buffer, 0, bytesRead);
                    }
                }

                closeDataConnection();
                sendControlResponse(CODE_226 + "文件接收完成");
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "文件写入错误");
            }
        }

        private void handleDele(String filename) {
            if (!checkLogin()) return;

            if (filename == null || filename.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少文件名");
                return;
            }

            try {
                File file = new File(currentDir, filename);

                // 安全检查
                if (!file.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!file.exists()) {
                    sendControlResponse(CODE_550 + "文件不存在");
                    return;
                }

                if (!file.delete()) {
                    sendControlResponse(CODE_550 + "文件删除失败");
                    return;
                }

                sendControlResponse(CODE_250 + "文件删除成功");
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "文件操作错误");
            }
        }

        private void handleRName(String filename) {
            if (!checkLogin()) return;

            if (filename == null || filename.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少文件名");
                return;
            }

            try {
                File file = new File(currentDir, filename);


                sendControlResponse(CODE_250 + "文件删除成功");
            } catch (Exception e) {
                sendControlResponse(CODE_550 + "文件操作错误");
            }
        }

        private void handleMkd(String dirname) {
            if (!checkLogin()) return;

            if (dirname == null || dirname.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少目录名");
                return;
            }

            try {
                File dir = new File(currentDir, dirname);

                // 安全检查
                if (!dir.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (dir.exists()) {
                    sendControlResponse(CODE_550 + "目录已存在");
                    return;
                }

                if (!dir.mkdir()) {
                    sendControlResponse(CODE_550 + "目录创建失败");
                    return;
                }

                sendControlResponse(CODE_257 + "\"" + dir.getName() + "\" 目录创建成功");
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "目录操作错误");
            }
        }

        private void handleCode(String args) {
            if(args != null && args.equalsIgnoreCase("UTF8 ON")) {
                encoding = "UTF-8";
                sendControlResponse(CODE_200 + encoding+"\r\n");
            } else {
                sendControlResponse(CODE_200 + "Unsupported option");
            }
        }

        private void handleRmd(String dirname) {
            if (!checkLogin()) return;

            if (dirname == null || dirname.isEmpty()) {
                sendControlResponse(CODE_501 + "缺少目录名");
                return;
            }

            try {
                File dir = new File(currentDir, dirname);

                // 安全检查
                if (!dir.getCanonicalPath().startsWith(new File(ROOT_DIR).getCanonicalPath())) {
                    sendControlResponse(CODE_550 + "权限拒绝");
                    return;
                }

                if (!dir.exists() || !dir.isDirectory()) {
                    sendControlResponse(CODE_550 + "目录不存在");
                    return;
                }

                // 检查目录是否为空
                if (dir.list().length > 0) {
                    sendControlResponse(CODE_550 + "目录不为空");
                    return;
                }

                if (!dir.delete()) {
                    sendControlResponse(CODE_550 + "目录删除失败");
                    return;
                }

                sendControlResponse(CODE_250 + "目录删除成功");
            } catch (IOException e) {
                sendControlResponse(CODE_550 + "目录操作错误");
            }
        }

        private void handleQuit() {
            sendControlResponse(CODE_221 + "再见");
            try {
                controlSocket.close();
            } catch (IOException e) { /* 忽略 */ }
        }

        private boolean checkLogin() {
            if (!loggedIn) {
                sendControlResponse(CODE_530 + "请先登录");
            }
            return loggedIn;
        }

        private boolean setupDataConnection() throws IOException {
            if (passiveMode) {
                // 被动模式已经由PASV命令设置好
                return true;
            } else {
                // 主动模式 - 服务器连接客户端 (需要PORT命令先设置)
                // 简单实现中，我们假设客户端已经发送了PORT命令
                return dataSocket != null && !dataSocket.isClosed();
            }
        }
//        private boolean setupDataConnection() throws IOException {
//            if (passiveMode) {
//                // 被动模式：在 accept() 前设置超时
//                ServerSocket passiveSocket = new ServerSocket();
//                passiveSocket.setSoTimeout(30000); // 30秒超时
//                passiveSocket.bind(new InetSocketAddress(passivePort));
//                dataSocket = passiveSocket.accept();
//                passiveSocket.close();
//            } else {
//                // 主动模式：连接客户端时设置超时
//                dataSocket = new Socket();
//                dataSocket.setSoTimeout(30000); // 30秒超时
//                dataSocket.connect(new InetSocketAddress(controlSocket.getInetAddress(), passivePort % 256));
//            }
//
//            // 设置数据连接的读写超时
//            dataSocket.setSoTimeout(60000); // 60秒读写超时
//            return true;
//        }

        private boolean checkDataConnection() {
            if (dataSocket == null || dataSocket.isClosed()) {
                sendControlResponse(passiveMode ? CODE_425 : CODE_503 + "请先使用PASV或PORT命令");
                return false;
            }
            return true;
        }

        private void closeDataConnection() {
            try {
                if (dataSocket != null && !dataSocket.isClosed()) {
                    dataSocket.close();
                }
            } catch (IOException e) { /* 忽略 */ }
            dataSocket = null;
        }

        private void closeConnections() {
            closeDataConnection();
            try {
                if (controlSocket != null && !controlSocket.isClosed()) {
                    controlSocket.close();
                }
            } catch (IOException e) { /* 忽略 */ }
        }

        private void sendControlResponse(String response) {
            controlWriter.println(response);
        }
    }
}