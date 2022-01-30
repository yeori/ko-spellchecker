package github.yeori.dict;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Word {
    @EqualsAndHashCode.Include
    Integer seq;

    String word;
    String defs;
    private List<Integer> groups = new ArrayList<>(3);

    public Word(Integer seq, String word) {
        this.seq = seq;
        this.word = word;
    }

    public void addGroupCode(Integer groupCode) {
        this.groups.add(groupCode);
    }
}
