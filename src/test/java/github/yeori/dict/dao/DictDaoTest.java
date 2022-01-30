package github.yeori.dict.dao;

import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.Driver;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DictDaoTest {

    @Test
    public void test_con() throws SQLException {
        DriverManager.registerDriver(new Driver());
        String url = "jdbc:mariadb://localhost:3306/dictdb";
        String user = "root";
        String pass = "1111";
        Connection con = DriverManager.getConnection(url, user, pass);
        con.close();
    }

    @Test
    public void test_ds() throws SQLException {
        MariaDbDataSource ds = new MariaDbDataSource("localhost", 3306, "dictdb");
        ds.setUser("root");
        ds.setPassword("1111");
        Connection con = ds.getConnection();
        con.close();
    }

    @Test
    public void test_init_dao() throws SQLException {
        MariaDbDataSource ds = new MariaDbDataSource("localhost", 3306, "dictdb");
        ds.setUser("root");
        ds.setPassword("1111");
        DictDao dao = new DictDao();
        dao.setDataSource(ds);
    }
}