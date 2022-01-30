package github.yeori.dict.dao;

import github.yeori.dict.DictException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Function;

public class Rset {
    private final ResultSet rs;

    public Rset(ResultSet rs) {
        this.rs = rs;
    }

    public Integer asInt(String column) {
        try {
            int v = this.rs.getInt(column);
            if (this.rs.wasNull()) {
                return null;
            } else {
                return v;
            }
        } catch (SQLException e) {
            throw new DictException("error when call rs.getInt(\"%s\")\n", column);
        }
    }

    public Integer asInt(String column, Integer defaultValue) {
        try {
            return asInt(column);
        } catch (DictException e) {
            return defaultValue;
        }
    }

    public String string(String column) {
        try {
            return this.rs.getString(column);
        } catch (SQLException e) {
            throw new DictException("error when call rs.getString(\"%s\")\n", column);
        }
    }
    public String string(String column, String defaultValue) {
        try {
            return string(column);
        } catch (DictException e) {
            return defaultValue;
        }
    }

    void close() {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T extends Collection<V>, V> void visit(
            T container, Function<Rset, V> fn) throws SQLException {
        while(rs.next()) {
            V v = fn.apply(this);
            if(v != null) {
                container.add(v);
            }
        }
    }

}
