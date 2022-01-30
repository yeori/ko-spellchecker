package github.yeori.dict.dao;

import github.yeori.dict.*;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DictDao {

    DataSource ds;

    public void setDataSource(MariaDbDataSource ds) {
        this.ds = ds;
    }

    public void insertWord(List<DictWord> words) {
        String query = "INSERT INTO dicts (" +
                " target_code, " +
                " group_code, " +
                " group_order, " +
                " word_unit, " +
                " word_type, " +
                " word, " +
                " hanja, " +
                " definition, " +
                " pos, " +
                " sense_type, " +
                " origin " +
                ") VALUES (" +
                " ?, ? ,? ,?, ?, ?, ?, ?, ?, ?, ?);";
        // Connection con = Dao.open();
        Stmt stmt = Dao.pstmt(ds, query, words.size());
        for (DictWord word: words) {
            stmt
                .setInt(word.getTargetCode())
                .setInt(word.getGroupCode())
                .setInt(word.getOrder())
                    .setString(word.getWordUnit())
                    .setString(word.getWordType())
                    .setString(word.getWord())
                    .setString(word.getHanja())
                    .setString(word.getDefinition())
                    .setString(word.getPos())
                    .setString(word.getSenseType())
                    .setString(word.getOrigin());
            stmt.batch();
        }
        stmt.close();

    }

    private void close(Connection con) {

        try {
            con.close();
        } catch (SQLException e) {
            throw new DictException("FAIL TO CLOSE CONNECTION");
        }
    }

    /**
     * target_code만 모아서 반환
     * @return
     */
    public Set<Integer> listTargetCodes() {
        String query = "SELECT target_code FROM dicts";
//        Connection con = Dao.open();

        Stmt stmt = Dao.pstmt(ds, query, 1);
        Set<Integer> codes = new HashSet<>();
        return stmt.select(codes, (rset) ->
            rset.asInt("target_code"));
    }

    DictWord rsToWord(Rset rset) {
        Integer targetCode = rset.asInt("target_code", null);
        String definition = rset.string("definition", null);
        String sensType = rset.string("sense_type", null);
        DictWord w = new DictWord();
        w.setTargetCode(targetCode);
        w.setGroupCode(rset.asInt("group_code", null));
        w.setOrder(rset.asInt("group_order", null));

        String wordUnit = rset.string("word_unit", null);
        w.setWordUnit(wordUnit == null ? null : WordUnit.valueOf(wordUnit));

        String wordType = rset.string("word_type", null);
        w.setWordType(wordType == null ? null : WordType.valueOf(wordType));

        w.setWord(rset.string("word", null));
        w.setHanja(rset.string("hanja", null));
        w.setDefinition(definition);
        w.setPos(rset.string("pos", null));
        w.setSenseType(sensType == null ? null : SenseType.valueOf(sensType));
        w.setOrigin(rset.string("origin", null));
        return w;
    }

    static String columns = "seq, target_code, group_code, group_order, word_unit, word_type, " +
            " word, hanja, definition, def_parsed, pos, cate, sense_type, origin";
    static String min = "target_code, word ";
    public List<DictWord> findByRange(int targetStart, int size, String ... cols) {
        String colList = String.join(", ", cols).trim();
        if (colList.equals("*")) {
            colList = columns;
        }
        String query = "SELECT "+
                colList +
                " from dicts " +
                " where target_code >= ? AND target_code < ?";
        Stmt stmt = Dao.pstmt(ds, query, 1);
        List<DictWord> words = new ArrayList<>();
        stmt.setInt(targetStart).setInt(targetStart + size).select(words, (rset) -> rsToWord(rset));
        return words;
    }
}
