package github.yeori.dict;

public class DictWords {
    public static DictWord create(
            Integer targetCode, Integer groupCode, Integer order,
            String wordUnit, String wordType,
            String pos,
            String senseType, String cate,
            // String region,
            String text,
            String hanja, String definition) {
        DictWord w = new DictWord();
        w.targetCode = targetCode;
        w.groupCode = groupCode;
        w.order = order;
        w.word = strip(text);
        w.hanja = hanja;
        w.definition = definition;
        w.wordUnit = WordUnit.parse(wordUnit.trim());
        w.wordType = WordType.parse(wordType.trim());
        w.pos = pos;
//        w.region = Region.parse(region);
        w.senseType = SenseType.parse(senseType);
        w.category = cate;
        return w;
    }

    private static String strip(String text) {
        return text
                .replaceAll("[\\-]", "")
                .replaceAll("[\\^]"," ");
    }
}
