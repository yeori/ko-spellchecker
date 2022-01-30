package github.yeori.dict;

/**
 *
 */
public enum WordUnit {
    IM, // idiom 관용구
    WD, // word  어휘
    PS, // phrase 구
    PV //  proverb 속담
    ;

    public static WordUnit parse(String unit) {
        if ("관용구".equals(unit)) {
            return IM;
        } else if ("어휘".equals(unit)) {
            return WD;
        } else if ("구".equals(unit)) {
            return PS;
        } else if ("속담".equals(unit)) {
            return PV;
        } else {
            throw new RuntimeException(String.format("invalid word unit: [%s]\n", unit));
        }
    }
}
