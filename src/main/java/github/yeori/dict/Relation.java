package github.yeori.dict;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relation {

    RelationType type;
    DictWord dst;

}
