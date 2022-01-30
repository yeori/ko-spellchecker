package github.yeori.dicttool;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DictElem implements IDictElem {

    public final Integer index;

    // Document doc;

    Element src;
    public final boolean isRoot;
    public DictElem(Element src, Integer idx, boolean isRoot) {
        this.src = src;
        this.index = idx;
        this.isRoot = isRoot;
    }

    @Override
    public void filter(String pathSelector, Predicate<DictElem> callback) {
        Elements elements = src.select(pathSelector);
        for (int i = 0; i < elements.size(); i++) {
            boolean keepOn = callback.test(new DictElem(elements.get(i), i, false));

            if (!keepOn) {
                break;
            }

        }
    }

    @Override
    public Integer asInt(String path) {
        String value = text(path);
        return toInt(value);
    }

    private Integer toInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        }catch (Exception e) {
            throw new RuntimeException("cannot parse to int : [" + s + "]");
        }
    }

    @Override
    public String text(String path) {
        String value = src.select(path).text();
        return value;
    }
    public List<String> texts(String path) {
        Elements elems = src.select(path);
        return elems.textNodes()
                .stream()
                .map(n -> n.text())
                .collect(Collectors.toList());
    }

//    public List<Integer> ints(String path) {
//        Elements elems = src.select(path);
//        return elems.textNodes()
//                .stream()
//                .map(n -> n.text())
//                .collect(Collectors.toList());
//    }
}
