package github.yeori.dict.dao;

import github.yeori.dict.DictException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class Dao {
    public static Stmt pstmt(DataSource ds, String query, int size) {
        Connection con = open(ds);
        return new Stmt(con, query, size);

    }

    public static Connection open(DataSource ds) {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("FAIL TO OPEN CONNECTION");
        }
    }

    public static PreparedStatement stmt(Connection con, String query) {
        try {
            return con.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DictException("fail to create pstmt", query);
        }
    }

    public static void close(Connection con, PreparedStatement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
