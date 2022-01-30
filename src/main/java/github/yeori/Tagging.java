package github.yeori;

import github.yeori.dict.DictException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
public class Tagging {

//    final String surface;
//    final String featureHead;
//    final String type; // INFLECT, COMMON, Compound
    final List<Feature> features;

    Pos mainPos;
    public Tagging() {
        this.features = new ArrayList<>();
    }
    /*
    public Tagging(String surface, String featureHead, String type, List<Feature> features) {
        this.surface = surface;
        this.featureHead = featureHead;
        this.type = type;
        this.features = features;
        this.mainPos = resolveMainPos();
        if (this.features.isEmpty()) {
            this.features.add(new Feature(surface, mainPos, type));
        }
    }
     */

//    public Pos resolveMainPos() {
//        int p = featureHead.indexOf('+');
//        String ft = p < 0 ? featureHead : featureHead.substring(0, p);
//        return Pos.valueOf(ft);
//    }
//    public boolean isVerbForm() {
//        Pos pos = this.features.get(0).pos;
//        return pos == Pos.VA || pos == Pos.VV;
//    }

    public void filterFeatures(Predicate<Feature> fn) {
        Feature ref = null;
        for (int i = features.size()-1; i >= 0 ; i--) {
            ref = this.features.get(i);
            if (fn.test(ref)) {
                ref.tag = null;
                this.features.remove(i);
            }
        }
    }

    public void addFeatures(List<Feature> features) {
        this.features.addAll(features);
        for(Feature ft: features) {
            ft.tag = this;
        }
    }

    public boolean isEmpty() {
        return this.features.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb  = new StringBuilder();

        for (Feature ft : features) {
            sb.append('[').append(ft.surface).append(',').append(ft.pos).append(']');
        }
        sb.append(')');
        return sb.toString();
    }

    private boolean checkSize(Object[] params) {
        if(params.length % 2 == 1) {
            throw new DictException("짝이 안맞음[POS, 'str', POS, 'str']");
        }
        if (params.length > this.features.size()*2) {
            return true;
        }
        return false;
    }

    String toRegex (String s) {
        if ("*".equals(s)) {
            return ".+";
        }
        return s.replaceAll("\\*+", ".*");
    }
    public boolean startsWith(Object ... params) {
        if (checkSize(params)) return false;
        Pos [] poses = new Pos[params.length/2];
        String [] patterns = new String[params.length/2];
        for (int i = 0; i < poses.length; i++) {
            poses[i] = (Pos) params[2*i];
            patterns[i] = (String) params[2*i+1];
        }
        Feature ref;

        for (int k= 0; k < poses.length; k++) {
            ref = features.get(k);
            if (ref.getPos() != poses[k]) {
                return false;
            }
            String regex = toRegex(patterns[k]);
            if (!ref.getSurface().matches(regex)) {
                return false;
            }
        }

        return true;
    }

    public boolean endsWith(Object ... params) {
        if (checkSize(params)) return false;

        Pos [] poses = new Pos[params.length/2];
        String [] patterns = new String[params.length/2];
        for (int i = 0; i < poses.length; i++) {
            poses[i] = (Pos) params[2*i];
            patterns[i] = (String) params[2*i+1];
        }

        // backward
        Feature ref;
        int offset = this.features.size() - poses.length;
        for (int k = poses.length-1; k >= 0; k--) {
            ref = features.get(offset+k);
            if (ref.getPos() != poses[k]) {
                return false;
            }
            String regex = toRegex(patterns[k]);
            if (!ref.getSurface().matches(regex)) {
                return false;
            }
        }
        return true;
    }

    public boolean is(Object ... params) {
        if (checkSize(params)) return false;
        if(params.length/2 != this.features.size()) {
            return false;
        }
        return this.startsWith(params);
    }

    public int size() {
        return this.features.size();
    }
    public boolean matchAt(int i, Pos ... poses) {
        if (i < 0 || i >= this.size()) {
            return false;
        }
        Feature ft = features.get(i);
        for (Pos pos: poses) {
            if(ft.getPos() == pos){
                return true;
            }
        }
        return false;
    }

    public void disableFeatures(int start, int end) {
        for (int i = end - 1 ; i >= start; i--) {
            this.features.remove(i);
        }
    }
    public void disableFeatures(int startIdx) {
        disableFeatures(startIdx, this.features.size());
    }

    public void disable() {
        disableFeatures(0, this.features.size());
    }

    public String toWord() {
        // 어려움을 당하여 몹시 괴롭다.
        Feature head = this.features.get(0);
        String word =  this.features.stream().map(ft -> ft.getSurface()).reduce("", (acc, s) -> acc + s);
        if (head.isVerb()) {
            return word + "다";
        } else if(head.isStem()) {
            return word + "하다";
        }
        else {
            return word;
        }
    }
}
