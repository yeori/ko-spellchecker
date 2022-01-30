package github.yeori.dicttool;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IDictElem {
    void filter(String pathSelector, Predicate<DictElem> callback);

    Integer asInt(String path);

    String text(String path);
}
