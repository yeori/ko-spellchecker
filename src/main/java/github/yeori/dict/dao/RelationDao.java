package github.yeori.dict.dao;

import github.yeori.dict.RelationType;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;

public class RelationDao {

    public static ThreadLocal<Stmt> local = new ThreadLocal<>();
    private final int batchLimit;
    DataSource ds;

    public RelationDao(MariaDbDataSource ds, int batchLimit) {
        this.ds = ds;
        this.batchLimit = batchLimit;
    }

    public Stmt prepareBatch(int batchLimit) {
        String query = "INSERT INTO word_relations ( " +
                " src_word, dst_word, rel_type " +
                ") VALUES ( " +
                " ?, ?, ?); ";
        Stmt stmt = Dao.pstmt(ds, query, batchLimit);
        local.set(stmt);
        return stmt;
    }

    public void insertRelation(Integer src, Integer dst, RelationType sym) {
        Stmt stmt = local.get();
        stmt.setInt(src).setInt(dst).setString(sym).batch();

    }

    public void endBatch() {
        Stmt stmt = local.get();
        stmt.close();
        local.remove();
    }
}
