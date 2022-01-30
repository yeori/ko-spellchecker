package github.yeori.dict;

public enum Region {
    CYR, // [중국] 요령성
    CAA, // 중앙 아시아
    CJL, // [중국] 길림성
    CHL, // [중국] 흑룡강

    KHG, // 함경
    KHN, // 함경남도
    KHB, // 함경북도
    KPA, // 평안도
    KPB, // 평안북도
    KPN, // 평안남도

    KJL, // 전라
    KJB, // 전북
    KJN, // 전남
    KCC, // 충청도
    KCN, // 충남
    KCB, // 충복
    KGW, // 강원
    KGS, // 경상도
    KGB, // 경북
    KGN, // 경남
    KHH, // 황해도
    KJJ, // 제주
    KGG, // 경기
    KAL,
    NNN, // 없음
    ; // 전국

    public static Region parse(String region) {
        switch (region) {
            case "중국 요령성":
                return CYR;
            case "중앙아시아":
                return CAA;
            case "중국 길림성":
                return CJL;
            case "중국 흑룡강성":
                return CHL;
            case "전라":
                return KJL;
            case "충북":
                return KCB;
            case "평북":
                return KPB;
            case "함북":
                return KHB;
            case "평안":
                return KPA;
            case "전북":
                return KJB;
            case "평남":
                return KPN;
            case "충남":
                return KCN;
            case "경기":
                return KGG;
            case "강원":
                return KGW;
            case "함경":
                return KHG;
            case "경상":
                return KGS;
            case "경북":
                return KGB;
            case "황해":
                return KHH;
            case "전국":
                return KAL;
            case "제주":
                return KJJ;
            case "함남":
                return KHN;
            case "전남":
                return KJN;
            case "충청":
                return KCC;
            case "경남":
                return KGN;
            default:
                return NNN;
        }
    }
}
