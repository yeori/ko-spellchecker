package github.yeori.dicttool;

import github.yeori.KoSpellChecker;
import github.yeori.Tagging;
import github.yeori.dict.DictWord;
import github.yeori.dict.Word;
import github.yeori.dict.dao.DictDao;
import github.yeori.dict.dao.Stmt;
import github.yeori.dict.dao.WordDao;
import github.yeori.dicttool.pattern.VerbPattern;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * table words에 단어별 definition을 normalize함
 */
public class DictNormalizer {
    final DictDao dictDao;
    final WordDao wordDao;


    public DictNormalizer(DictDao dictDao, WordDao wordDao) {
        this.dictDao = dictDao;
        this.wordDao = wordDao;
    }

    public void normalize(int targetStart, int size) {
        KoSpellChecker spell = new KoSpellChecker();
        StringBuilder sb = new StringBuilder();
        List<DictWord> words = dictDao.findByRange(targetStart, size);
        words.forEach(w -> {

            String def = nomailizeDef(w.getDefinition());
            def = stripDialect(def);

            sb.append(w.getWord() + ": "+ def).append('\n');
            List<Tagging> tags = spell.tagging(def);
//            Stream<Feature> fts = tags
//                    .stream()
//                    .flatMap(t -> t.getFeatures().stream());

//            List<Feature> features = null;
//            if (w.anyPosOf("명사", "관·명", "대명사")) {
//                // 명사만 뽑아냄
//                tags.forEach(tag -> tag.filterFeatures(ft -> ft.getPos().level > 2 && ft.isNoun()));
//            } else if (w.anyPosOf("동사", "형용사")) {
//                // 동사만 뽑아냄
//                verbPattern(tags);
//
//            }
            verbPattern(tags);
            for (Tagging tag : tags) {
                tag.filterFeatures(ft -> ft.getPos().level <= 1);
            }
            tags = tags.stream().filter(tag -> !tag.isEmpty()).collect(Collectors.toList());
//            List<Feature> features = tags
//                    .stream()
//                    .flatMap(t -> t.getFeatures().stream())
//                    .filter(ft -> ft.getPos().level >= 3)
//                    .collect(Collectors.toList());
            for (Tagging tag :tags) {
                sb.append(tag).append(tag.toWord()).append('\n');
            }
            sb.append('\n');
            // sb.append(features.size() + ", " + features).append('\n');
        });
        System.out.println(sb.toString());
    }

    public void verbPattern(List<Tagging> tags) {
        VerbPattern.verbJiMot1(tags);
        VerbPattern.verbJiAn(tags);
        VerbPattern.verbMotHada(tags);
        VerbPattern.verbGiWehada(tags);
        VerbPattern.verbEhDaeHaYeo(tags);

        // 하다, 있다 등은 맨 나중에 처리해야함
        VerbPattern.verbStopWord(tags);
    }

    private String nomailizeDef(String def) {
        return def.replaceAll("ㆍ", ",")
                .replaceAll("\\(.+\\)", "");
    }

    /**
     * ‘~~~’의 {준말|방언|본말|북한|음역어}
     * @param def
     * @return
     */
    private String stripDialect(String def) {
        char a = '‘';
        char b = '’';
        int ia = def.indexOf(a);
        if (ia < 0) {
            return def;
        }
        int ib = def.indexOf(b, ia+1);
        if (ib < 0) {
            return def;
        }
        return def.substring(ia+1, ib);
    }

    private void fillMapping() {

    }

    /**
     * dicts 테이블의 모든 단어를 가져와서 words테이블에 기록. 동음 이의어를 하나로 합쳐서 words에 기록함
     *
     * [dicts]                [words]
     *   |                       |
     *   +--*-[word_in_dict]-*---+
     */
    private void fillWordMap() {
        int len = 1610910;
        System.out.println("=== loading.....===");
        List<DictWord> dicts = dictDao.findByRange(1, len, "group_code", "word");
        System.out.println("===start classifications===");
        Map<String, Word> wordMap =new HashMap<>(600000);
        DictWord dict;
        int seq = 1;
        for (int i = 0; i < dicts.size(); i++) {
            dict = dicts.get(i);
            Word word = wordMap.get(dict.getWord());
            if (word == null) {
                word = new Word(seq++, dict.getWord());
                wordMap.put(dict.getWord(), word);
            }
            word.addGroupCode(dict.getGroupCode());
//            groupCodes.add(dict.getGroupCode());
        }
        /*
        System.out.println("===insert words===");
        wordDao.prepareBatch(10000);
        for (String wname : wordMap.keySet()) {
            //System.out.println(word + ": " + wordMap.get(word));
            Word word = wordMap.get(wname);
            wordDao.insertWord(word);
        }
        wordDao.endBatch();
        */

        System.out.println("===insert groups===");
        wordDao.prepareGroupMappingBatch(20000);
        Set<Integer> groupCodes = new TreeSet<>();
        for (String wname : wordMap.keySet()) {
            Word word = wordMap.get(wname);
            groupCodes.clear();
            groupCodes.addAll(word.getGroups());
            for (Integer groupCode : groupCodes) {
                wordDao.insertMapping(word, groupCode);
            }
        }
        wordDao.endBatch();
    }

    public static void main(String[] args) throws SQLException {
//        CompressedAnalyzer.parseJava("안녕");
        MariaDbDataSource ds = new MariaDbDataSource("localhost", 3306, "dictdb");
        ds.setUser("root");
        ds.setPassword("1111");

        DictDao dictDao = new DictDao();
        dictDao.setDataSource(ds);

        WordDao wordDao =new WordDao(ds, 5000);
        DictNormalizer dn = new DictNormalizer(dictDao, wordDao);
        dn.fillWordMap();
        // dn.normalize(275900, 200);
    }
}
