package github.yeori;

import org.bitbucket.eunjeon.seunjeon.CompressedAnalyzer;
import org.bitbucket.eunjeon.seunjeon.Eojeol;
import org.bitbucket.eunjeon.seunjeon.LNode;
import org.bitbucket.eunjeon.seunjeon.Morpheme;
import scala.Enumeration;
import scala.collection.JavaConversions;
import scala.collection.mutable.WrappedArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class KoSpellChecker
{

    public static void main( String[] args )
    {
        String sentence = "사람이나 동물이 발을 땅에 대고 다리를 쭉 뻗으며 몸을 곧게 하다.";
//        String sentence = "장난감이 그만 망가졌다.";
//        String sentence = "매섭게 적을 몰아부쳐서";
//        String sentence = "팔이 아파 죽겠어";
//        String sentence = "큰 병이나 통 속에 다 차지 않은 액체가 자꾸 흔들리는 소리가 나다.";
        Iterable<LNode> res = CompressedAnalyzer.parseJava(sentence);
        res.forEach((node) -> {
            System.out.println("===");
            Tagging tag = new Tagging();
            tag.addFeatures(parseMorpheme(node.morpheme()));

        });

        // 어절 분석
//        for (Eojeol eojeol: CompressedAnalyzer.parseEojeolJava(sentence)) {
//            System.out.println("[" + eojeol + "]");
//            Eojeol e = eojeol.deCompound();
//            for (LNode node: e.nodesJava()) {
//                System.out.println(">>" +node);
//                Morpheme mpm = node.morpheme();
//                parseMorpheme(mpm, System.out);
//            }
//        }
    }

    /**
     *  NNG,   *, T,  팔,       *,  *,  *,                *
     *  VA+EC, *, F, 아파, Inflect, VA, EC, 아프/VA/*+아/EC/*
     * @param mpm
     * @return
     */
    private static List<Feature> parseMorpheme(Morpheme mpm) {
        Enumeration.Value type = mpm.getMType();
        String feat = mpm.getFeature();
        String surface = mpm.getSurface();
        WrappedArray<Enumeration.Value> poses = mpm.getPoses();
        // Seq<Morpheme> parts = mpm.deComposite();
        Collection<Morpheme> parts = JavaConversions.asJavaCollection(mpm.deComposite());
//        out.printf(">> %5s(%s) %5s %s\n", type, feat, surface, poses);
//        out.print("   ");


        List<Feature> feats = parts.stream().map((m) -> Feature.parse(m)).collect(Collectors.toList());
        if (feats.isEmpty()) {
            Feature ft = Feature.parse(mpm);
            feats.add(ft);
        }
        // tag.setFeatures(feats);
        return feats;

//        System.out.println(mpm.getFeatureHead() + ":" + tag);

    }

    public List<Tagging> tagging(String sentence) {
        // Iterable<LNode> res = CompressedAnalyzer.parseJava(sentence);
        Iterable<Eojeol> eojeols = CompressedAnalyzer.parseEojeolJava(sentence);
        List<Tagging> tags = new ArrayList<>();
        for (Eojeol ej : eojeols) {
            Tagging phrase = new Tagging();
            List<LNode> res = ej.nodesJava();
            res.forEach((node) -> {
                phrase.addFeatures(parseMorpheme(node.morpheme()));
            });
            tags.add(phrase);
        }
        return tags;
    }
}
