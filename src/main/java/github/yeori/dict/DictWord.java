package github.yeori.dict;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DictWord {
    Integer targetCode;
    Integer groupCode;
    Integer order;

    String word;
    String definition;
    /**
     * wordType이 한자어 또는 혼종어인 경우 한자
     */
    String hanja;
    /**
     * 어휘: word값이 한단어
     * C관용구
     * W어휘 - word값이 한단어
     * P구 - 전문 용어의 경우 word값이 "가려진^쿨롱^포텐셜" 과 같은 형태
     * S속담
     *
     * P구:
     */
    WordUnit wordUnit; // word units:
    /**
     * [없음, 외래어, 고유어, 혼종어, 한자어]
     * N없음
     * F외래어
     * M혼종어 - 한자와 고유어가 같이 결합된 형태
     * K고유어
     * C한자어
     */
    WordType wordType; //

    /**
     * [없음,
     * 형용사,
     * 대·관,
     * 대·부,
     * 수사,
     * 명사,
     * 수·관·명,
     * 보조 형용사,
     * 명·부,
     * 관형사,
     * 수·관,
     * 대·감,
     * 어미,
     * 의명·조,
     * 의존 명사,
     * 접사,
     * 품사 없음,
     * 관·명,
     * 조사,
     * 동사,
     * 감탄사,
     * 구,
     * 대명사,
     * 부·감,
     * 보조 동사,
     * 관·감,
     * 동·형,
     * 감·명,
     * 부사
     * ]
     */
    String pos;

    /**
     * [방언, 북한어, 일반어, 옛말]
     */
    SenseType senseType;
    /**
     * senseType이 [방언]인 경우 지역명(제주, 경기, 등)
     *
     * [
     중국 요령성,
     중앙아시아,
     중국 길림성,
     중국 흑룡강성,
     전라, 충북, 평북, 함북,
     평안, 전북, 평남, 충남, 경기, 강원, 함경, 경상, 경북, 황해,
     전국,
     제주, 함남, 전남, 충청, 경남
     */
    Set<Region> region; // 한개가 아니다.
    /**
     * [,
     * 예체능 일반,
     * 기계, 천문, 민속, 해양, 전기·전자, 보건 일반, 인명, 동물, 천연자원, 의학,
     * 가톨릭, 자연 일반, 화학, 복식, 철학, 건설, 공예, 군사,
     * 지구, 행정, 문학, 재료, 미술, 광업, 교육, 역사, 매체,
     * 농업, 생명, 식품, 산업 일반, 수의, 연기, 심리, 수학, 임업,
     * 지명, 수산업, 불교, 음악, 종교 일반, 무용, 서비스업,
     * 언어, 사회 일반, 공학 일반, 약학, 고유명 일반, 정보·통신, 공업,
     * 한의, 물리, 식물, 인문 일반, 경제, 교통, 경영, 체육, 지리, 영상,
     * 책명, 정치, 법률, 복지, 환경, 기독교]
     */
    String category;
    /**
     * 파일 위치(xml파일이름)
     */
    String origin;

    public boolean isDialect() {
        return this.senseType == SenseType.DL;
    }

    public boolean anyPosOf(String ... poses) {
        for (int i = 0; i < poses.length; i++) {
            if (poses[i].equals(this.pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Word{" +
                "targetCode=" + targetCode +
                ", groupCode=" + groupCode +
                ", order=" + order +
                ", word='" + word + '\'' +
                ", wordUnit='" + wordUnit + '\'' +
                ", wordType='" + wordType + '\'' +
                ", pos='" + pos + '\'' +
                ", senseType='" + senseType + '\'' +
                ", region='" + region + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
