package github.yeori.dict;

import java.util.HashMap;
import java.util.Map;

public enum RelationType {

    // 반대말, 하위어, 참고 어휘, 비슷한말, 방언, 준말, 낮춤말, 상위어, 옛말, 높임말, 본말
    /**
     * 동의어(비슷한말)
     */
    SYM,
    /**
     * 반대말
     */
    ANM,
    /**
     * 참고 어휘
     */
    REF,
    /**
     * 상위어(hypernym)
     */
    HPE,
    /**
     * 하위어(hyponym)
     */
    HPO,
    /**
     * 방언(dialect)
     */
    DLT,
    /**
     * 줄임말(abbreviation)
     */
    ABR,
    /**
     * 본말(original)
     */
    ORG,
    /**
     * 낮춤말(disprespectiful)
     */
    DRP,
    /**
     * 높임말(honorific)
     */
    HNR,
    /**
     * 옛말
     */
    OLD;

    private static Map<String, RelationType> ref = new HashMap<>(20);
    static {
         // [반대말, 하위어, 참고 어휘, 비슷한말, 방언, 준말, 낮춤말, 상위어, 옛말, 높임말, 본말]
        /*
        SYM,ANM, REF, HPE,HPO,DLT, ABR,ORG,DRP,HNR,OLD
         */
        ref.put("비슷한말", SYM);
        ref.put("참고 어휘", REF);
        ref.put("반대말", ANM);

        ref.put("상위어", HPE);
        ref.put("하위어", HPO);

        ref.put("높임말", HNR);
        ref.put("낮춤말", DRP);

        ref.put("본말", ORG);
        ref.put("방언", SYM);
        ref.put("준말", ABR);
        ref.put("옛말", OLD);

    }

    public static RelationType parse(String relName) {
        RelationType type = ref.get(relName.trim());
        if (type == null) {
            throw new DictException("no such relation type: [%s]\n", relName.trim());
        }
        return type;
    }

}
