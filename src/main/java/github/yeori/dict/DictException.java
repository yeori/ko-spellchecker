package github.yeori.dict;

public class DictException extends RuntimeException {
    public DictException(String format, Object ... params) {
        super(String.format(format, params));
    }
}
