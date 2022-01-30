package github.yeori.dict.dao;

import github.yeori.dict.DictException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stmt {

    final PreparedStatement stmt;
    private final Connection con;
    int idx = 1;

    int batchLimit = 1000;
    int batchIndex = 0;

    List<Rset> rsets = new ArrayList<>();
    public Stmt(Connection con, String query, int batchLimit) {
        this.con = con;
        this.stmt = Dao.stmt(con, query);
        this.batchLimit = batchLimit;
    }

    public Stmt setInt(Integer v) {
        int i = idx++;
        try {
            stmt.setInt(i, v);
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("fal to stmt.setInt(%d, %d)", i, v);
        }
    }

    public Stmt setString(String v) {
        int i = idx++;
        try {
            stmt.setString(i, v);
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("fal to stmt.setString(%d, %d)", i, v);
        }
    }

    public <T extends Enum<T>> Stmt setString(Enum<T> enm) {
        return setString(enm.name());
    }

    private void flushBatch() {
        try {
            int [] n = stmt.executeBatch();
            stmt.clearBatch();
            stmt.clearWarnings();
            batchIndex = 0;
            System.out.println("[FLUSHED] " + n.length);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("fal to stmt.executeBatch");
        }
    }

    public void batch() {
        try {
            stmt.addBatch();
            stmt.clearParameters();
            idx = 1;
            if (++batchIndex == batchLimit) {
                flushBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("fal to stmt.executeUpdate");
        }
    }

    public void close() {
        if (batchIndex > 0) {
            flushBatch();
        }
        Dao.close(con, stmt);

        for (Rset set: rsets) {
            set.close();
        }
        rsets.clear();
    }

    public <T extends Collection<V>, V> T select(T container, Function<Rset, V> fn) {
        try {
            ResultSet rs = stmt.executeQuery();
            Rset rset = new Rset(rs);
            rsets.add(rset);
            rset.visit(container, fn);
            return container;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("sql exception %s", e.getMessage());
        } finally {
            this.close();
        }
    }
}
