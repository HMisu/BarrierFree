package com.example.barrierfree.models;

/**
 * 공공 데이터 api 호출 응답시 사용
 * */
public class ResponseAddr {

    private double la_crd = 0; // 다발지역지점 중심점의 위도
    private double lo_crd = 0; // 다발지역지점 중심점의 경도

    private String geom_json = ""; // 다발지역폴리곤 - 폴리곤 좌표 json 데이터
    private String sido_sgg_nm = ""; // 시도시군구명
    private String spot_nm = ""; // 다발지역의 위치
    private int occrrnc_cnt = 0; // 사고 발생 건수
    private int caslt_cnt = 0; // 사상자수
    private int dth_dnv_cnt = 0; // 사망자수
    private int se_dnv_cnt = 0; // 중상자수
    private int sl_dnv_cnt = 0; // 경상자수
    private int wnd_dnv_cnt = 0; // 부상신고자수


    public double getLo_crd() {
        return lo_crd;
    }

    public void setLo_crd(double lo_crd) {
        this.lo_crd = lo_crd;
    }

    public double getLa_crd() {
        return la_crd;
    }

    public void setLa_crd(double la_crd) {
        this.la_crd = la_crd;
    }

    public String getGeom_json() {
        return geom_json;
    }

    public void setGeom_json(String geom_json) {
        this.geom_json = geom_json;
    }

    public String getSido_sgg_nm() {
        return sido_sgg_nm;
    }

    public void setSido_sgg_nm(String sido_sgg_nm) {
        this.sido_sgg_nm = sido_sgg_nm;
    }

    public String getSpot_nm() {
        return spot_nm;
    }

    public void setSpot_nm(String spot_nm) {
        this.spot_nm = spot_nm;
    }

    public int getOccrrnc_cnt() {
        return occrrnc_cnt;
    }

    public void setOccrrnc_cnt(int occrrnc_cnt) {
        this.occrrnc_cnt = occrrnc_cnt;
    }

    public int getCaslt_cnt() {
        return caslt_cnt;
    }

    public void setCaslt_cnt(int caslt_cnt) {
        this.caslt_cnt = caslt_cnt;
    }

    public int getDth_dnv_cnt() {
        return dth_dnv_cnt;
    }

    public void setDth_dnv_cnt(int dth_dnv_cnt) {
        this.dth_dnv_cnt = dth_dnv_cnt;
    }

    public int getSe_dnv_cnt() {
        return se_dnv_cnt;
    }

    public void setSe_dnv_cnt(int se_dnv_cnt) {
        this.se_dnv_cnt = se_dnv_cnt;
    }

    public int getSl_dnv_cnt() {
        return sl_dnv_cnt;
    }

    public void setSl_dnv_cnt(int sl_dnv_cnt) {
        this.sl_dnv_cnt = sl_dnv_cnt;
    }

    public int getWnd_dnv_cnt() {
        return wnd_dnv_cnt;
    }

    public void setWnd_dnv_cnt(int wnd_dnv_cnt) {
        this.wnd_dnv_cnt = wnd_dnv_cnt;
    }

    // api 호출 결과 데이터 구조
//    {
//        "resultCode": "00",
//            "resultMsg": "NORMAL_CODE",
//            "items": {
//        "item": [
//        {
//            "afos_fid": 6467491,
//                "afos_id": "2018074",
//                "bjd_cd": "26110141",
//                "spot_cd": "26110001",
//                "sido_sgg_nm": "부산광역시 중구1",
//                "spot_nm": "부산광역시 중구 남포동6가(남포사거리 인근)",
//                "occrrnc_cnt": 33,
//                "caslt_cnt": 41,
//                "dth_dnv_cnt": 0,
//                "se_dnv_cnt": 12,
//                "sl_dnv_cnt": 25,
//                "wnd_dnv_cnt": 4,
//                "geom_json": "{\"type\":\"Polygon\",\"coordinates\":[[[129.02956208,35.09774709],[129.02953619,35.09753201],[129.02945951,35.09732519],[129.02933499,35.09713459],[129.02916741,35.09696752],[129.02896322,35.09683042],[129.02873026,35.09672854],[129.02847749,35.0966658],[129.02821461,35.09664461],[129.02795173,35.0966658],[129.02769895,35.09672854],[129.02746599,35.09683042],[129.0272618,35.09696752],[129.02709422,35.09713459],[129.0269697,35.09732519],[129.02689302,35.09753201],[129.02686713,35.09774709],[129.02689302,35.09796217],[129.0269697,35.09816898],[129.02709422,35.09835958],[129.0272618,35.09852664],[129.02746599,35.09866375],[129.02769895,35.09876563],[129.02795173,35.09882836],[129.02821461,35.09884954],[129.02847749,35.09882836],[129.02873026,35.09876563],[129.02896322,35.09866375],[129.02916741,35.09852664],[129.02933499,35.09835958],[129.02945951,35.09816898],[129.02953619,35.09796217],[129.02956208,35.09774709]]]}",
//                "lo_crd": "129.02821460632",
//                "la_crd": "35.097747086859"
//        },
//        {
//            "afos_fid": 6467492,
//                "afos_id": "2018074",
//                "bjd_cd": "26110126",
//                "spot_cd": "26110002",
//                "sido_sgg_nm": "부산광역시 중구2",
//                "spot_nm": "부산광역시 중구 부평동4가(보수사거리 인근)",
//                "occrrnc_cnt": 27,
//                "caslt_cnt": 32,
//                "dth_dnv_cnt": 0,
//                "se_dnv_cnt": 9,
//                "sl_dnv_cnt": 22,
//                "wnd_dnv_cnt": 1,
//                "geom_json": "{\"type\":\"Polygon\",\"coordinates\":[[[129.02665936,35.10311193],[129.02663347,35.10289686],[129.02655679,35.10269006],[129.02643227,35.10249947],[129.0262647,35.10233242],[129.0260605,35.10219532],[129.02582754,35.10209345],[129.02557477,35.10203071],[129.02531189,35.10200953],[129.02504901,35.10203071],[129.02479623,35.10209345],[129.02456327,35.10219532],[129.02435908,35.10233242],[129.02419151,35.10249947],[129.02406699,35.10269006],[129.02399031,35.10289686],[129.02396442,35.10311193],[129.02399031,35.103327],[129.02406699,35.1035338],[129.02419151,35.10372438],[129.02435908,35.10389144],[129.02456327,35.10402853],[129.02479623,35.1041304],[129.02504901,35.10419313],[129.02531189,35.10421432],[129.02557477,35.10419313],[129.02582754,35.1041304],[129.0260605,35.10402853],[129.0262647,35.10389144],[129.02643227,35.10372438],[129.02655679,35.1035338],[129.02663347,35.103327],[129.02665936,35.10311193]]]}",
//                "lo_crd": "129.025311888834",
//                "la_crd": "35.103111930472"
//        },
//        {
//            "afos_fid": 6467493,
//                "afos_id": "2018074",
//                "bjd_cd": "26110109",
//                "spot_cd": "26110003",
//                "sido_sgg_nm": "부산광역시 중구3",
//                "spot_nm": "부산광역시 중구 중앙동6가(부산우체국사거리 인근)",
//                "occrrnc_cnt": 25,
//                "caslt_cnt": 33,
//                "dth_dnv_cnt": 0,
//                "se_dnv_cnt": 11,
//                "sl_dnv_cnt": 19,
//                "wnd_dnv_cnt": 3,
//                "geom_json": "{\"type\":\"Polygon\",\"coordinates\":[[[129.03774974,35.1029404],[129.03772384,35.10272533],[129.03764717,35.10251853],[129.03752265,35.10232794],[129.03735507,35.10216088],[129.03715088,35.10202379],[129.03691792,35.10192191],[129.03666514,35.10185918],[129.03640226,35.101838],[129.03613938,35.10185918],[129.03588661,35.10192191],[129.03565365,35.10202379],[129.03544946,35.10216088],[129.03528188,35.10232794],[129.03515736,35.10251853],[129.03508068,35.10272533],[129.03505479,35.1029404],[129.03508068,35.10315546],[129.03515736,35.10336227],[129.03528188,35.10355285],[129.03544946,35.10371991],[129.03565365,35.103857],[129.03588661,35.10395887],[129.03613938,35.1040216],[129.03640226,35.10404279],[129.03666514,35.1040216],[129.03691792,35.10395887],[129.03715088,35.103857],[129.03735507,35.10371991],[129.03752265,35.10355285],[129.03764717,35.10336227],[129.03772384,35.10315546],[129.03774974,35.1029404]]]}",
//                "lo_crd": "129.036402262655",
//                "la_crd": "35.102940398282"
//        }
//        ]
//    },
//        "totalCount": 3,
//            "numOfRows": 3,
//            "pageNo": 1
//    }


}
