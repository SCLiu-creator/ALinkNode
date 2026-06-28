package superlink.httpserver.servelt.httptype;

public enum ContentType implements type{
  //  abstract String Type();

//    zip(){
//        public String Type() {
//            return "application/zip";
//        }
//    },
//    wrl(){
//        public String Type() {
//            return "model/vrml";
//        }
//    },
//    xlc(){
//        public String Type() {
//            return "application/vnd.ms-exce";
//        }
//    },
//    xls (){
//        public String Type() {
//            return "application/vnd.ms-excel";
//        }
//    };

    txt("text/plain"),
    xml("text/xml"),
    json("application/json"),
    js("text/javascript"), // 修正为正确的MIME类型
    html("text/html"),
    css("text/css"),
    png("image/png"),
    jpeg("image/jpeg"),
    jpg("image/jpeg"), // jpg和jpeg应该映射到同一个MIME类型
    webp("image/webp"),
    heic("image/heic"),
    mp3("audio/mpeg"), // 修正为正确的MIME类型
    wav("audio/wav"), // 修正为正确的MIME类型
    mp4("video/mp4"),
    zip("application/zip"),
    wrl("model/vrml"),
    xlc("application/vnd.ms-excel"),
    xls("application/vnd.ms-excel"),
    gif("image/gif"),
    bmp("image/bmp"),
    svg("image/svg+xml"),
    tiff("image/tiff"),
    psd("image/vnd.adobe.photoshop"),
    mov("video/quicktime"),
    avi("video/x-msvideo"),
    wmv("video/x-ms-wmv"),
    mkv("video/x-matroska"),
    flv("video/x-flv"),
    webm("video/webm"),
    ogg("audio/ogg"),
    flac("audio/flac"),
    m4a("audio/mp4"),
    wma("audio/x-ms-wma"),
    aac("audio/aac"),
    pdf("application/pdf"),
    doc("application/msword"),
    docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    ppt("application/vnd.ms-powerpoint"),
    pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
//    xls("application/vnd.ms-excel"),
    xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    empty(""),
    DEFAULT("application/octet-stream");
    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType != null ? mimeType.toLowerCase() : "DEFAULT";
    }

//    public String getType() {
//        return mimeType;
//    }

    @Override
    public String Type() {
        return mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * 安全获取ContentType（替代valueOf）
     * @param prefix 文件前缀/扩展名
     * @return 匹配的ContentType，找不到时返回DEFAULT
     */
    public static ContentType safeValueOf(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return DEFAULT;
        }

        try {
            // 处理带点的情况如 ".doc"
            String normalized = prefix.startsWith(".")
                    ? prefix.substring(1).toUpperCase()
                    : prefix.toUpperCase();

            return ContentType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
//    String string;
//
//    ContentType(String string) {
//        //this.string = string;
//    }

    //根据vip等级获取vip枚举
//    public static ContentType getInstance(String s) {
//        for (ContentType type : ContentType.values()) {
//            if (type.string == s) {
//                return type;
//            }
//        }
//        return null;
//    }

}
