package github.yeori;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void regex()
    {
        boolean m = "‘깎이다’의 방언".matches("^‘.+’의 방언");
        assertTrue(m);
    }

    @Test
    public void test_니은() throws UnsupportedEncodingException {
        byte [] a = {52, 49};
        String sa = new String(a, "utf-16le");
        byte [] b = {-85, 17};
        String sb = new String(b, "utf-16le");
        System.out.println(sa);
        System.out.println(sb);

        char nieun = '\u3134';
        String N = new String(new char[]{nieun});
        System.out.println(N);
        // 대하(utf-16le)
        byte [] c = {0, -77, 88, -43};

    }
}
