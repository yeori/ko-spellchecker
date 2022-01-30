package github.yeori;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bitbucket.eunjeon.seunjeon.Morpheme;
import scala.Enumeration;

import java.util.Set;

@Getter
@Setter
@ToString
public class Feature {

    Tagging tag;
    String surface; // 아프
    Pos pos; // VA
    String type; // COMMON, INFLECT 등

    static Set<Pos> NOUNS = Set.of(Pos.NNG, Pos.NNP, Pos.NP, Pos.XR);
    static Set<Pos> VERB = Set.of(Pos.VV, Pos.VA);

    public Feature(String surface, Pos pos, String type) {
        this.surface = surface;
        this.pos = pos;
        this.type = type;
    }

    public static Feature parse (Morpheme m) {
        String surface = m.getSurface();
        Pos pos = Pos.valueOf(m.getFeatureHead());
        Enumeration.Value type = m.getMType(); // COMMON, INFLECT
        return new Feature(surface, pos, type.toString());
    }

    public boolean isNoun() {
        return NOUNS.contains(this.pos);
    }

    public boolean isVerb() {
        return VERB.contains(this.pos);
    }

    /**
     * 어근(XR)인지
     * @return
     */
    public boolean isStem() {
        return Pos.XR == this.pos;
    }
}
