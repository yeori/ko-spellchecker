package github.yeori.dicttool;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PreProcessorTest {

    @Test
    void proc0() throws SQLException {
        PreProcessor p = new PreProcessor();
        p.proc0();
    }
}