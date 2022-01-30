package github.yeori.dicttool;

import github.yeori.dict.RelationType;

import java.util.Objects;

public class Id {
    Integer src;
    Integer dst;
    RelationType type;

    public Id(Integer src, Integer dst, RelationType type) {
        this.src = src;
        this.dst = dst;
        this.type = type;
    }

    Id reverse() {
        return new Id(dst, src, type);
    }

    Id reverse(RelationType rel) {
        return new Id(this.dst, this.src, rel);
    }
    @Override
    public String toString() {
        return "Id{" +
                "src=" + src +
                ", dst=" + dst +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id = (Id) o;
        return Objects.equals(src, id.src) && Objects.equals(dst, id.dst) && type == id.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst, type);
    }
}
