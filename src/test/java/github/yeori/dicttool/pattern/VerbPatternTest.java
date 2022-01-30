package github.yeori.dicttool.pattern;

import github.yeori.KoSpellChecker;
import github.yeori.Tagging;
import org.junit.jupiter.api.Test;

import java.util.List;

class VerbPatternTest {

    @Test
    public void test_decomp() {
        KoSpellChecker spell = new KoSpellChecker();
        // 막연부지
//        String s ="뚜렷하지 못하고 어렴풋하여 알지 못함.";
        String s ="성질이 고분고분하지 못하고 거세어";
        // [고분고분,MAG(2)]), [하,VV(3)][지,EC(1)]), [못하,VX(1)][고,EC(1)])
        List<Tagging> tags = spell.tagging(s);
        System.out.println(tags);
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);
    }

    @Test
    public void test_에_대하여() {
        KoSpellChecker spell = new KoSpellChecker();
        String s;
        List<Tagging> tags;
        s = "슬픔에 대하여 이야기하다.";
        tags = spell.tagging(s);
        VerbPattern.verbEhDaeHaYeo(tags);
        System.out.println(tags);

        s = "우리의 미래에 대해 생각해보자.";
        tags = spell.tagging(s);
        VerbPattern.verbEhDaeHaYeo(tags);
        System.out.println(tags);

        s = "수필에 대한 10가지 노하우";
        tags = spell.tagging(s);
        VerbPattern.verbEhDaeHaYeo(tags);
        System.out.println(tags);
    }
    @Test
    public void test_기_위하여() {
        KoSpellChecker spell = new KoSpellChecker();
        String s;
        List<Tagging> tags;
        s = "상대편을 맞추기 위하여 노력하다";
        tags = spell.tagging(s);
        VerbPattern.verbGiWehada(tags);
        System.out.println(tags);

        s = "내일 놀러가기 위해서는 오늘 숙제를 끝내야 한다";
        tags = spell.tagging(s);
        VerbPattern.verbGiWehada(tags);
        System.out.println(tags);
    }
    @Test
    public void verb_jimot() {
        KoSpellChecker spell = new KoSpellChecker();
        // 요건이 갖추어지지 못하여 임시로 의논하여 결정하다.
        List<Tagging> tags = spell.tagging("요건이 갖추어지지 못하여 임시로 의논하여 결정하다.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);
        tags = spell.tagging("색상이 달라지지 못했다고 힘들다.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);

        tags = spell.tagging("물이 채워지지 못하니까 힘들다.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);

        tags = spell.tagging("속도가 빨라지지 못해서 힘들다.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);

        tags = spell.tagging("살을 빼고싶어하지 않아서 큰일이다.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);

        tags = spell.tagging("뚜렷하지 못하고 어렴풋하여 알지 못함.");
        VerbPattern.verbJiMot1(tags);
        System.out.println(tags);
    }

    /**
     * 먹지도 못하면서
     * 이루지도 못하면서
     * 이기지를 못하는데
     */
    @Test
    void verb_ji_mot2() {
        KoSpellChecker spell = new KoSpellChecker();
        List<Tagging> tags;

        tags = spell.tagging("떠나지도 못해서 음식을 잔뜩 샀다.");
        VerbPattern.verbMotHada(tags);
        System.out.println(tags);
    }

    @Test
    public void verb_an() {
        KoSpellChecker spell = new KoSpellChecker();
        // 남에게 인정을 베풀지 않는 인색한 짓으로 부자가 되다.
        List<Tagging> tags = spell.tagging("남에게 인정을 만들어 주고싶어 하지않는 인색한 짓으로 부자가 되다.");
        VerbPattern.verbJiAn(tags);
        System.out.println(tags);
        tags = spell.tagging("예뻐지고 싶어하지 않아하는 듯한 태도다.");
        VerbPattern.verbJiAn(tags);
        System.out.println(tags);

        tags = spell.tagging("살을 빼고싶어하지 않아서 큰일이다.");
        VerbPattern.verbJiAn(tags);
        System.out.println(tags);

        tags = spell.tagging("조건을 받아들이고 싶지 않거나 물리치고 싶어 한다.");
        VerbPattern.verbJiAn(tags);
        System.out.println(tags);
    }

}