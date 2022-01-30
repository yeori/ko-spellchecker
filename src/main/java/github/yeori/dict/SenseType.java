package github.yeori.dict;

public enum SenseType {

    DL, // dialect 방언
    NK, // north korean 북한말
    NM, // normal 일반어
    OK // old korean 옛말
    ;

    public static SenseType parse(String sense) {
        if ("방언".equals(sense)) {
            return DL;
        } else if ("북한어".equals(sense)) {
            return NK;
        } else if ("일반어".equals(sense)) {
            return NM;
        } else if ("옛말".equals(sense)) {
            return OK;
        } else {
            throw new RuntimeException(String.format("invalid sense type: [%s]\n", sense));
        }
    }
}
