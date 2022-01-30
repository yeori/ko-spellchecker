package github.yeori.dict.dao;

import github.yeori.dict.Word;

import javax.sql.DataSource;

/**
 * table words
 */
public class WordDao {
    public static ThreadLocal<Stmt> local = new ThreadLocal<>();
    final int batchLimit;
    DataSource ds;

    public WordDao(DataSource ds, int batchLimit) {
        this.ds = ds;
        this.batchLimit = batchLimit;
    }
    public Stmt prepareBatch(int batchLimit) {
        String query = "INSERT INTO words (seq, word) VALUES (?, ?)";
        Stmt stmt = Dao.pstmt(ds, query, batchLimit);
        local.set(stmt);
        return stmt;
    }
    public void insertWord(Word word) {
        Stmt stmt  = local.get();
        stmt.setInt(word.getSeq()).setString(word.getWord()).batch();
    }
    public void endBatch() {
        Stmt stmt  = local.get();
        local.remove();
        stmt.close();
    }

    public Stmt prepareGroupMappingBatch(int batchLimit) {
        String query= "INSERT INTO word_in_dict (word_ref, group_ref) VALUES (?, ?)";
        Stmt stmt = Dao.pstmt(ds, query, batchLimit);
        local.set(stmt);
        return stmt;
    }
    public void insertMapping(Word word, Integer groupCode) {
        Stmt stmt  = local.get();
        stmt.setInt(word.getSeq()).setInt(groupCode).batch();
    }
}
