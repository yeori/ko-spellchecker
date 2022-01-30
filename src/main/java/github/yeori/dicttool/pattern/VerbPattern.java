package github.yeori.dicttool.pattern;

import github.yeori.Pos;
import github.yeori.Tagging;

import java.util.List;

public class VerbPattern {
    /**
     * <pre>
     *
     * ~지 못하다
     * ex)
     *          VA(3)부드럽, EC(1)지, VX(1)못하, EC(1)여
     * NNG(3)기억, XSV(1)하, EC(1)지, VX(1)못하
     * </pre>
     * @param tags
     */
    public static void verbJiMot1(List<Tagging> tags) {
        Tagging cur, prev;
        for (int i = 1; i < tags.size() ; i++) {
            prev = tags.get(i-1);
            cur = tags.get(i);
            if (!cur.startsWith(Pos.VX, "못하") &&
                    !cur.startsWith(Pos.MAG, "못", Pos.VV, "하") &&
                    !cur.startsWith(Pos.MAG, "못", Pos.XSV, "하") &&
                    !cur.startsWith(Pos.MAG, "못", Pos.XSA, "하")) {
                continue;
            }
            if (!prev.endsWith(Pos.EC, "지") && !prev.endsWith(Pos.EC, "지", Pos.JX, "*")) {
                continue;
            }
            int vtagIdx = i - 1;
            Tagging vTag ; // = tags.get(vtagIdx);
            while(vtagIdx >= 0 ) {
                vTag = tags.get(vtagIdx);
                if (vTag.endsWith(Pos.EC, "여")) {
                    break;
                }
                if (vTag.endsWith(Pos.EC, "*") && vTag.matchAt(vTag.size()-2, Pos.VV, Pos.VA, Pos.VX, Pos.XSA)) {
                    ;
                } else {
                    break;
                }
                vtagIdx--;
            }
            for(int k = vtagIdx+1; k<= i ;k++) {
                tags.get(k).disable();
            }
        }
    }

    /**
     * ~지를 못하다 [지,EC(1)][를,JX(1)], [못,MAG(2)], [하,VV(3)][ᆯ,ETM(1)],
     * ~지도 못하다 [지,EC(1)][도,JX(1)], [못,MAG(2)], [하,VV(3)][아서,EC(1)]
     * ~지는 못하다 [지,EC(1)][는,JX(1)]
     * @param tags
     */
    public static void verbMotHada(List<Tagging> tags) {
        Tagging cur, prev1, prev2;
        for (int i = 2; i < tags.size() ; i++) {
            // ... prev2, prev1, cur
            prev2 = tags.get(i-2);
            prev1 = tags.get(i-1);
            cur = tags.get(i);
            if (!prev1.is(Pos.MAG, "못") ) {
                continue;
            }
            if (!cur.startsWith(Pos.VV, "하")) {
                continue;
            }
            if (!prev2.endsWith(Pos.VV, "*", Pos.EC, "지", Pos.JX, "*") &&
                    !prev2.endsWith(Pos.VA, "*", Pos.EC, "지", Pos.JX, "*")) {
                continue;
            }
            for(int k = i - 2; k<= i ;k++) {
                tags.get(k).disable();
            }
        }
    }


    /**
     * ~~지 않다, ~~지 아니하다
     * @param tags
     */
    public static void verbJiAn(List<Tagging> tags) {

        Tagging cur, prev;
        for (int i = 1; i < tags.size() ; i++) {
            prev = tags.get(i-1);
            cur = tags.get(i);
            if (!cur.startsWith(Pos.VX, "않") && !cur.startsWith(Pos.VX, "아니하*")) {
                continue;
            }
            if (!prev.endsWith(Pos.EC, "지")) {
                continue;
            }

            int vtagIdx = i - 1;
            Tagging vTag ; // = tags.get(vtagIdx);
            while(vtagIdx >= 0 ) {
                vTag = tags.get(vtagIdx);
                if (vTag.endsWith(Pos.EC, "*") && vTag.matchAt(vTag.size()-2, Pos.VV, Pos.VA, Pos.VX)) {
                    ;
                } else {
                    break;
                }
                vtagIdx--;
            }
            for(int k = vtagIdx+1; k<= i ;k++) {
                tags.get(k).disable();
            }
        }
    }

    /**
     * 불용 표현
     * ~고 있다.
     * ~수 있다
     * ~되다
     *
     * @param tags
     */
    public static void verbStopWord(List<Tagging> tags) {
        for (int i = 0; i < tags.size(); i++) {
            Tagging t = tags.get(i);
            if (t.startsWith(Pos.VV, "있*")
                    || t.startsWith(Pos.VV, "하*")
                    || t.startsWith(Pos.VA, "있*")
                    || t.startsWith(Pos.VA, "없*")
                    || t.startsWith(Pos.VV, "되")) {
                t.disable();
            }
        }
    }

    /**
     * ~기 위하여, 위해서
     * [맞추,VV(3)][기,ETN(1)]), [위하,VV(3)][여,EC(1)])
     */
    public static void verbGiWehada(List<Tagging> tags) {
        Tagging cur, prev;

        for (int i = 1; i < tags.size() ; i++) {
            prev = tags.get(i-1);
            cur = tags.get(i);
            if (!cur.startsWith(Pos.VV, "위하")) {
                continue;
            }
            if (!prev.endsWith(Pos.ETN, "기")) {
                continue;
            }
            cur.disable();
        }
    }

    /**
     * 한글 'ㄴ' 조합형 코드표 참조
     * 소스코드에 "ㄴ"을 입력하면 hangle compatibility jamo에 있는 "ㄴ"의 코드값이 저장됨
     * seunjeon내부에서 사용하는 "ㄴ"과 바이트값이 다르다.
     * 아래와 같이 한글 조합형 코드표에 있는 "ㄴ"값을 직접 입력함
     */
    private static String NIEUN = new String(new char[]{'\u11ab'});
    /**
     * -에 대하여, ~에 대해서, ~에 대한
     * ~에 관하여, ~에 관해서, ~에 관한
     * @param tags
     */
    public static void verbEhDaeHaYeo(List<Tagging> tags) {
        Tagging cur, prev;
        for (int i = 1; i < tags.size() ; i++) {
            prev = tags.get(i-1);
            cur = tags.get(i);
            // 대해서: 대하/VV + 아/EC
            if (!cur.startsWith(Pos.VV, "대하", Pos.EC, "아")
                && !cur.startsWith(Pos.VV, "대하", Pos.EC, "여")
                    && !cur.startsWith(Pos.VV, "대하", Pos.ETM, NIEUN)
                    && !cur.startsWith(Pos.VV, "관하", Pos.EC, "아")
                    && !cur.startsWith(Pos.VV, "관하", Pos.EC, "여")
                    && !cur.startsWith(Pos.VV, "관하", Pos.ETM, NIEUN)) {
                continue;
            }

            if (!prev.endsWith(Pos.JKB, "에")) {
                continue;
            }
            cur.disable();
        }
    }
}
