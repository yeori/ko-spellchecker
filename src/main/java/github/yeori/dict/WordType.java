package github.yeori.dict;

public enum WordType {
    NN,
    FR, // 외래어
    MX, // 혼종어
    KO, // 고유어
    CN // 한자어
    ;

    public static WordType parse(String type) {
        if ("".equals(type)) {
            return NN;
        } else if ("외래어".equals(type)) {
            return FR;
        } else if ("혼종어".equals(type)) {
            return MX;
        } else if ("고유어".equals(type)) {
            return KO;
        } else if ("한자어".equals(type)) {
            return CN;
        } else {
            throw new RuntimeException(String.format("invalid word type: [%s]\n", type));
        }
    }
}
