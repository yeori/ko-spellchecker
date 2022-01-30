package github.yeori.dicttool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class DicParser {

    public IDictElem from(String dictFilePath) {
        Document doc = open(new File(dictFilePath));
        return new DictElem(doc, 0, true);
    }

    public IDictElem from(String ... dictFilePathes) {
        DictIterator iterator = new DictIterator();
        for (String path : dictFilePathes) {
            IDictElem elem = from(path);
            iterator.addElem(elem);
        }
        return iterator;
    }

    private Document open(File xmlFile) {
        try {
            String xml = Files.readString(xmlFile.toPath(), Charset.forName("UTF-8"));
            return Jsoup.parse(xml, Parser.xmlParser());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("error", e);
        } catch (IOException e) {
            throw new RuntimeException("error", e);
        }
    }
}
