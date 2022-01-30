package github.yeori.dicttool;

import github.yeori.dict.dao.RelationDao;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.DriverManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DicParserTest {

    DicParser parser;

    String [] nums = {
            "50000",
            "100000",
            "150000",
            "200000",
            "250000",
            "300000",
            "350000",
            "400000",
            "450000",
            "500000",
            "550000",
            "600000",
            "650000",
            "700000",
            "750000",
            "800000",
            "850000",
            "900000",
            "950000",
            "1000000",
            "1050000",
            "1100000",
            "1150000",
            "1151412",
    };
    String [] files = {
            "./dict//opendict/202201/920710_350000.xml",
            "./dict/opendict/202201/920710_350000.xml",
            "./dict/opendict/202201/920710_400000.xml",
    };

    @Test
    public void test_opendict_load() {
        parser = new DicParser();
        File f = new File(".");
        System.out.println(f.getAbsolutePath());
        Set<Integer> pk = new HashSet<>();

        parser.from(files)
                .filter("channel > item", (item) -> {
                    pk.add(item.asInt("> group_code"));
//            System.out.printf("[%6d][%6d][%6d] seq: %s\n",
//                    item.index,
//                    item.asInt("> group_code"),
//                    item.asInt("> target_code"),
//                    item.text("> link"));
                    return  true;
        });
        System.out.println("# of pk " + pk.size());
    }
    @Test
    public void parse_group_code_group_order() {

        parser = new DicParser();
        Map<Integer, List<Integer>> groups = new HashMap<>();
        parser.from(files)
                .filter("channel > item", (item) -> {
                    Integer groupCode = item.asInt("> group_code");
                    Integer orderInGroup = item.asInt("> group_order");
                    List<Integer> orders = groups.get(groupCode);
                    if (orders == null) {
                        orders = new ArrayList<>();
                        groups.put(groupCode, orders);
                    }
                    orders.add(orderInGroup);
                    return true;
                });
        groups.forEach((groupCode, orders) -> {
            System.out.printf("[%d] %s\n", groupCode, orders.toString());
        });
        System.out.println("# of groups: " + groups.size());
    }
    @Test
    public void parse_word_unit() {
        /*
         * item > wordInfo > word_unit
         *                 > word_type
         *
         */
        String path = "./dict//opendict/202201/920710_@n.xml";
        String [] files = Arrays
                .stream(nums)
                .map(n -> path.replace("@n", n))
                .toArray(len -> new String[len]);
        parser = new DicParser();
        Set<String> wordUnits = new HashSet<>();
        Set<String> wordTypes = new HashSet<>();
        Set<String> posSet = new HashSet<>();
        Set<String> senseTypes = new HashSet();
        Set<String> categories = new HashSet<>();
        Set<String> regions = new HashSet<>();
        Set<String> relations = new HashSet<>();
        for(String file : files) {
            System.out.printf("[%s]\n", file);
            parser.from(file)
                    .filter("channel > item", (item) -> {
                        String wordUnit = item.text("> wordInfo > word_unit");
                        String wordType = item.text("> wordInfo > word_type");
                        String pos = item.text("> senseInfo > pos");
                        String senseType = item.text("> senseInfo > type");
                        String cate = item.text("> senseInfo cat_info");
                        List<String> region = item.texts("> senseInfo > region_info > region");
                        List<String> relation = item.texts("> senseInfo > relation_info > type");
                        wordUnits.add(wordUnit);
                        wordTypes.add(wordType);
                        posSet.add(pos);
                        senseTypes.add(senseType);
                        categories.add(cate);
                        for (String s : region) {
                            regions.add(s.trim());
                        }
                        for (String s : region) {
                            relations.add(s.trim());
                        }
                        return true;
                    });
        }
        System.out.println("word units: " + wordUnits);
        System.out.println("word types: " + wordTypes);
        System.out.println("POS values: " + posSet);
        System.out.println("sense type: " + senseTypes);
        System.out.println("categories: " + categories);
        System.out.println("regions   : " + regions);
        System.out.println("relations : " + relations);
    }

    @Test
    void test_relation_info_type() {
        Set<String> relations = new HashSet<>();
        String path = "./dict//opendict/202201/920710_@n.xml";
        String [] files = Arrays
                .stream(nums)
                .map(n -> path.replace("@n", n))
                .toArray(len -> new String[len]);
        parser = new DicParser();
        for(String file : files) {
            System.out.printf("[%s]\n", file);
            parser.from(file)
                    .filter("channel > item", (item) -> {
                        List<String> rels = item.texts("> senseInfo > relation_info > type");
                        for (String s : rels) {
                            relations.add(s.trim());
                        }
                        return true;
                    });
        }
        System.out.println(relations);
    }

    static class Id {
        Integer src;
        Integer dst;

        public Id(Integer src, Integer dst) {
            this.src = src;
            this.dst = dst;
        }

        Id reverse() {
            return new Id(dst, src);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id target = (Id) o;
            return Objects.equals(src, target.src) && Objects.equals(dst, target.dst);
        }

        @Override
        public int hashCode() {
            return Objects.hash(src, dst);
        }
    }
    /**
     * A가 B의 비슷한 말로 출현할때,
     * B도 A의 비슷한 말로 출현하는지 확인
     */
    @Test
    void test_비슷한말_구조_확인() {
        Set<String> relations = new HashSet<>();
        String path = "./dict//opendict/202201/920710_@n.xml";
        String [] files = Arrays
                .stream(nums)
                .map(n -> path.replace("@n", n))
                .toArray(len -> new String[len]);
        Set<Id> idSet = new HashSet<>((int)(1160000*0.75)); // 75%

        parser = new DicParser();
        for(String file : files) {
            System.out.printf("[%s]\n", file);
            parser.from(file).filter("channel > item", (item) -> {
                Integer src = item.asInt("> target_code");
                item.filter("> senseInfo > relation_info", (rel) -> {
                    String type = rel.text("> type");
                    Integer dst = rel.asInt("> link_target_code");
                    if ("비슷한말".equals(type)) {
                        idSet.add(new Id(src, dst));
                    }
                    return true;
                });
                return true;
            });
        }
        Set<Id> missing = new HashSet<>(12000);
        for (Id id : idSet) {
            Id reversed = id.reverse();
            if(!idSet.contains(reversed)) {
                missing.add(reversed);
                System.out.printf("[NO MAPPING] %d -> %d (OK), but %d -> %d(NONE)\n",
                        id.src, id.dst, id.dst, id.src);
            }
        }
        System.out.println("# of synomyms: " + idSet.size());
        System.out.println("# of missing : " + missing.size());
    }

    @Test
    void test_strip() {
        String s = "가-감-자리";
        assertEquals("가감자리",
                s.replaceAll("[\\^\\-]", ""));
        assertEquals("가감자리",
                "가^감-자^리".replaceAll("[\\^\\-]", ""));
    }

}