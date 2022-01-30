package github.yeori.dicttool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DictIterator implements IDictElem{

    List<IDictElem> elems = new ArrayList<>();
    int index = 0;
    @Override
    public void filter(String pathSelector, Predicate<DictElem> callback) {
        for (IDictElem each : elems) {
            each.filter(pathSelector, callback);
        }
    }

    @Override
    public Integer asInt(String path) {
        throw new UnsupportedOperationException("asInt not supported in iterator");
    }

    @Override
    public String text(String path) {
        throw new UnsupportedOperationException("asInt not supported in iterator");
    }

    public void addElem(IDictElem elem) {
        this.elems.add(elem);
    }
}
