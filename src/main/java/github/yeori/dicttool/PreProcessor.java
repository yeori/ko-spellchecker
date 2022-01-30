package github.yeori.dicttool;

import github.yeori.dict.RelationType;
import github.yeori.dict.DictWord;
import github.yeori.dict.DictWords;
import github.yeori.dict.dao.DictDao;
import github.yeori.dict.dao.RelationDao;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.*;

public class PreProcessor {

    private final DictDao dictDao;
    private final RelationDao relDao;
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

    public PreProcessor() throws SQLException {
        MariaDbDataSource ds = new MariaDbDataSource("localhost", 3306, "dictdb");
        ds.setUser("root");
        ds.setPassword("1111");

        dictDao = new DictDao();
        dictDao.setDataSource(ds);
        relDao = new RelationDao(ds, 2000);

    }


    /**
     * 기본 정보 테이블에 삽입
     */
    public void proc0() {

        String[] files = toFiles();
        DicParser parser = new DicParser();
        List<DictWord> words = new ArrayList<>(1000);
        int [] cnt = {0};
        for (String file : files) {
            parser.from(file).filter("channel > item", (item) -> {
                Integer groupCode = item.asInt("> group_code");
                Integer orderInGroup = item.asInt("> group_order");
                Integer targetCode = item.asInt("> target_code");

                String text = item.text("> wordInfo > word");
                String wordUnit = item.text("> wordInfo > word_unit");
                String wordType = item.text("> wordInfo > word_type");
                String pos = item.text("> senseInfo > pos");
                String senseType = item.text("> senseInfo > type");
                String cate = item.text("> senseInfo > cat_info");
                String definition = item.text("> senseInfo > definition");

                int p = file.lastIndexOf('/');
                String fname = file.substring(p+ 1);
                // 한자어
                String hanja = parseHanja(item, wordType);
                DictWord w = DictWords.create(targetCode,
                        groupCode,
                        orderInGroup, wordUnit, wordType, pos, senseType, cate, text, hanja, definition);
                w.setOrigin(fname);
//                words.add(w);
                words.add(w);
                if (words.size() == 2000) {
                    dictDao.insertWord(words);
                    cnt[0] += words.size();
                    System.out.println("# of words: " + cnt[0]);
                    words.clear();
                }
                return true;
            });
        }
        if(!words.isEmpty()) {
            dictDao.insertWord(words);
            cnt[0] += words.size();
            System.out.println("# of words: " + cnt[0]);
        }

//        words.forEach(w -> System.out.println(w));
    }

    private String[] toFiles() {
        String path = "./dict/opendict/202201/920710_@n.xml";
        String [] files = Arrays
                .stream(nums)
                .map(n -> path.replace("@n", n))
                .toArray(len -> new String[len]);
        return files;
    }

    /**
     * 비슷한 말 처리
     *
     * A가 B와 비슷한말일때 B가 A와 비슷한말은 DB에 넣지 않음
     * (A, B)를 (B,A)로 해석해야 함
     */
    public void procSYM() {
        Set<Integer> targetCodes = dictDao.listTargetCodes();
        System.out.println(targetCodes.size());

        String [] files = toFiles();
        DicParser parser = new DicParser();
        Set<Id> idSet = new HashSet<>();
//        Set<String> missing = new HashSet<>();
        relDao.prepareBatch(4000);
        for (String file : files) {
            System.out.println("[file]" + file);
            parser.from(file).filter("channel > item", (item) -> {
                Integer src = item.asInt("> target_code");
                item.filter("> senseInfo > relation_info", (rel) -> {
                    String word = rel.text("> word");
                    String type = rel.text("> type");
                    Integer dst = rel.asInt("> link_target_code");
                    if (!targetCodes.contains(dst)) {
//                        missing.add(String.format("%d -> %d(%s), %s at %s\n",
//                                src,
//                                dst, word, type, file));
                    } else if ("비슷한말".equals(type)) {
                        Id id = new Id(src, dst, RelationType.SYM);
                        if (!idSet.contains(id)) {
                            idSet.add(id);
                            idSet.add(id.reverse());
                            relDao.insertRelation(src, dst, RelationType.SYM);
                        }
                    }
                    return true;
                });
                return true;
            });
        }
        relDao.endBatch();

//        IO.flush("missing.txt", missing);
//        System.out.println("# of missing: " + missing.size());

    }

    public void procREF_ANM() {
        Set<Integer> targetCodes = dictDao.listTargetCodes();
        System.out.println(targetCodes.size());

        String [] files = toFiles();
        DicParser parser = new DicParser();
        Set<Id> idSet = new HashSet<>(300000);
        relDao.prepareBatch(4000);
        for (String file : files) {
            System.out.println("[file]" + file);
            parser.from(file).filter("channel > item", (item) -> {
                Integer src = item.asInt("> target_code");
                item.filter("> senseInfo > relation_info", (rel) -> {
                    String word = rel.text("> word");
                    String type = rel.text("> type");
                    Integer dst = rel.asInt("> link_target_code");
                    if (!targetCodes.contains(dst)) {
//                        missing.add(String.format("%d -> %d(%s), %s at %s\n",
//                                src,
//                                dst, word, type, file));
                    } else if ("참고 어휘".equals(type) || "반대말".equals(type)) {
                        RelationType relType = RelationType.parse(type);
                        Id id = new Id(src, dst, relType);
                        if (!idSet.contains(id)) {
                            // idSet.add(id);
                            idSet.add(id.reverse());
                            relDao.insertRelation(src, dst, relType);
                        }
                    }
                    return true;
                });
                return true;
            });
        }
        relDao.endBatch();

//        IO.flush("missing.txt", missing);
//        System.out.println("# of missing: " + missing.size());

    }

    /**
     * 상위어-하위어
     * 높임말-낮춤말
     *
     * 단방향 그래프로 모두 등록함
     * A가 B의 상위어(등록)
     * B가 A의 하위어(등록)
     */
    public void procHP_HNR_DRP() {
        Set<Integer> targetCodes = dictDao.listTargetCodes();
        System.out.println(targetCodes.size());

        String [] files = toFiles();
        DicParser parser = new DicParser();
        Set<Id> idSet = new HashSet<>(300000);
        relDao.prepareBatch(4000);
        for (String file : files) {
            System.out.println("[file]" + file);
            parser.from(file).filter("channel > item", (item) -> {
                Integer src = item.asInt("> target_code");
                item.filter("> senseInfo > relation_info", (rel) -> {
                    String word = rel.text("> word");
                    String type = rel.text("> type");
                    Integer dst = rel.asInt("> link_target_code");
                    if (!targetCodes.contains(dst)) {
//                        missing.add(String.format("%d -> %d(%s), %s at %s\n",
//                                src,
//                                dst, word, type, file));
                    } else {
                        RelationType [] rels = new RelationType[2];
                        if ("상위어".equals(type)) {
                            rels[0] = RelationType.HPE;
                            rels[1] = RelationType.HPO;
                        } else if ("하위어".equals(type)) {
                            rels[0] = RelationType.HPO;
                            rels[1] = RelationType.HPE;
                        } else if ("높임말".equals(type)) {
                            rels[0] = RelationType.HNR;
                            rels[1] = RelationType.DRP;
                        } else if ("낮춤말".equals(type)) {
                            rels[0] = RelationType.DRP;
                            rels[1] = RelationType.HNR;
                        }
                        if (rels[0] != null) {
                            Id id = new Id(src, dst, rels[0]);
                            if (!idSet.contains(id)) {
                                // idSet.add(id);
                                relDao.insertRelation(src, dst, rels[0]);
                                relDao.insertRelation(dst, src, rels[1]);
                                idSet.add(id);
                                idSet.add(id.reverse(rels[1]));
                            }
                        }
                    }
                    return true;
                });
                return true;
            });
        }
        relDao.endBatch();

//        IO.flush("missing.txt", missing);
//        System.out.println("# of missing: " + missing.size());

    }

    private String parseHanja(DictElem item, String wordType) {
        if ("한자어".equals(wordType)) {
            return item.text("> wordInfo > original_language_info > original_language");
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws SQLException {
        PreProcessor p = new PreProcessor();
//        p.proc0();
//        p.procSYM();
//        p.procREF_ANM();
//        p.procHP_HNR_DRP();
    }
}
