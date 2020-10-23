package com.example.barrierfree;

import com.example.barrierfree.models.RequestAddr;

import java.util.ArrayList;

public class Constant {

    // 시도 요청값
    public static ArrayList<RequestAddr> sidoReqList = getSidoReqList();


    // 구군 요청값
    public static ArrayList<RequestAddr> gugunReqList = getGugunReqList();



    private static ArrayList<RequestAddr> getSidoReqList() {

        ArrayList<RequestAddr> list = new ArrayList<>();
        list.add(new RequestAddr("서울특별시", "", 11));
        list.add(new RequestAddr("부산광역시", "", 26));
        list.add(new RequestAddr("대구광역시", "", 27));
        list.add(new RequestAddr("인천광역시", "", 28));
        list.add(new RequestAddr("광주광역시", "", 29));
        list.add(new RequestAddr("대전광역시", "", 30));
        list.add(new RequestAddr("울산광역시", "", 31));
        list.add(new RequestAddr("세종특별자치시", "", 36));
        list.add(new RequestAddr("경기도", "", 41));
        list.add(new RequestAddr("강원도", "", 42));
        list.add(new RequestAddr("충청북도", "", 43));
        list.add(new RequestAddr("충청남도", "", 44));
        list.add(new RequestAddr("전라북도", "", 45));
        list.add(new RequestAddr("전라남도", "", 46));
        list.add(new RequestAddr("경상북도", "", 47));
        list.add(new RequestAddr("경상남도", "", 48));
        list.add(new RequestAddr("제주특별자치도", "", 50));

        return list;
    }

    private static ArrayList<RequestAddr> getGugunReqList() {

        ArrayList<RequestAddr> list = new ArrayList<>();
        list.add(new RequestAddr("서울특별시", "강남구", 680));
        list.add(new RequestAddr("서울특별시", "강동구", 740));
        list.add(new RequestAddr("서울특별시", "강북구", 305));
        list.add(new RequestAddr("서울특별시", "강서구", 500));
        list.add(new RequestAddr("서울특별시", "관악구", 620));
        list.add(new RequestAddr("서울특별시", "광진구", 215));
        list.add(new RequestAddr("서울특별시", "구로구", 530));
        list.add(new RequestAddr("서울특별시", "금천구", 545));
        list.add(new RequestAddr("서울특별시", "노원구", 350));
        list.add(new RequestAddr("서울특별시", "도봉구", 320));
        list.add(new RequestAddr("서울특별시", "동대문구", 230));
        list.add(new RequestAddr("서울특별시", "동작구", 590));
        list.add(new RequestAddr("서울특별시", "마포구", 440));
        list.add(new RequestAddr("서울특별시", "서대문구", 410));
        list.add(new RequestAddr("서울특별시", "서초구", 650));
        list.add(new RequestAddr("서울특별시", "성동구", 200));
        list.add(new RequestAddr("서울특별시", "성북구", 290));
        list.add(new RequestAddr("서울특별시", "송파구", 710));
        list.add(new RequestAddr("서울특별시", "양천구", 470));
        list.add(new RequestAddr("서울특별시", "영등포구", 560));
        list.add(new RequestAddr("서울특별시", "용산구", 170));
        list.add(new RequestAddr("서울특별시", "은평구", 380));
        list.add(new RequestAddr("서울특별시", "종로구", 110));
        list.add(new RequestAddr("서울특별시", "중구", 140));
        list.add(new RequestAddr("서울특별시", "중랑구", 260));

        list.add(new RequestAddr("부산광역시", "강서구", 440));
        list.add(new RequestAddr("부산광역시", "금정구", 410));
        list.add(new RequestAddr("부산광역시", "기장군", 710));
        list.add(new RequestAddr("부산광역시", "남구", 290));
        list.add(new RequestAddr("부산광역시", "동구", 170));
        list.add(new RequestAddr("부산광역시", "동래구", 260));
        list.add(new RequestAddr("부산광역시", "부산진구", 230));
        list.add(new RequestAddr("부산광역시", "북구", 320));
        list.add(new RequestAddr("부산광역시", "사상구", 530));
        list.add(new RequestAddr("부산광역시", "사하구", 380));
        list.add(new RequestAddr("부산광역시", "서구", 140));
        list.add(new RequestAddr("부산광역시", "수영구", 500));
        list.add(new RequestAddr("부산광역시", "연제구", 470));
        list.add(new RequestAddr("부산광역시", "영도구", 200));
        list.add(new RequestAddr("부산광역시", "중구", 110));
        list.add(new RequestAddr("부산광역시", "해운대구", 350));

        list.add(new RequestAddr("대구광역시", "남구", 200));
        list.add(new RequestAddr("대구광역시", "달서구", 290));
        list.add(new RequestAddr("대구광역시", "달성군", 710));
        list.add(new RequestAddr("대구광역시", "동구", 140));
        list.add(new RequestAddr("대구광역시", "북구", 230));
        list.add(new RequestAddr("대구광역시", "서구", 170));
        list.add(new RequestAddr("대구광역시", "수성구", 260));
        list.add(new RequestAddr("대구광역시", "중구", 110));


        list.add(new RequestAddr("인천광역시", "강화군", 710));
        list.add(new RequestAddr("인천광역시", "계양구", 245));
        list.add(new RequestAddr("인천광역시", "남구", 170));
        list.add(new RequestAddr("인천광역시", "남동구", 200));
        list.add(new RequestAddr("인천광역시", "동구", 140));
        list.add(new RequestAddr("인천광역시", "부평구", 237));
        list.add(new RequestAddr("인천광역시", "서구", 260));
        list.add(new RequestAddr("인천광역시", "연수구", 185));
        list.add(new RequestAddr("인천광역시", "옹진군", 720));
        list.add(new RequestAddr("인천광역시", "중구", 110));

        list.add(new RequestAddr("광주광역시", "광산구", 200));
        list.add(new RequestAddr("광주광역시", "남구", 155));
        list.add(new RequestAddr("광주광역시", "동구", 110));
        list.add(new RequestAddr("광주광역시", "북구", 170));
        list.add(new RequestAddr("광주광역시", "서구", 140));

        list.add(new RequestAddr("대전광역시", "대덕구", 230));
        list.add(new RequestAddr("대전광역시", "동구", 110));
        list.add(new RequestAddr("대전광역시", "서구", 170));
        list.add(new RequestAddr("대전광역시", "유성구", 200));
        list.add(new RequestAddr("대전광역시", "중구", 140));

        list.add(new RequestAddr("울산광역시", "남구", 140));
        list.add(new RequestAddr("울산광역시", "동구", 170));
        list.add(new RequestAddr("울산광역시", "북구", 200));
        list.add(new RequestAddr("울산광역시", "울주군", 710));
        list.add(new RequestAddr("울산광역시", "중구", 110));

        list.add(new RequestAddr("세종특별자치시", "세종특별자치시", 110));

        list.add(new RequestAddr("경기도", "가평군", 820));
        list.add(new RequestAddr("경기도", "고양시", 28));
        list.add(new RequestAddr("경기도", "과천시", 290));
        list.add(new RequestAddr("경기도", "광명시", 210));
        list.add(new RequestAddr("경기도", "광주시", 610));
        list.add(new RequestAddr("경기도", "구리시", 310));
        list.add(new RequestAddr("경기도", "군포시", 410));
        list.add(new RequestAddr("경기도", "김포시", 570));
        list.add(new RequestAddr("경기도", "남양주시", 360));
        list.add(new RequestAddr("경기도", "동두천시", 250));
        list.add(new RequestAddr("경기도", "부천시", 19));
        list.add(new RequestAddr("경기도", "성남시", 13));
        list.add(new RequestAddr("경기도", "수원시", 11));
        list.add(new RequestAddr("경기도", "시흥시", 390));
        list.add(new RequestAddr("경기도", "안산시", 270));
//        list.add(new RequestAddr("경기도", "안산시", 27)); // 문서에 안산시가 270과 27 코드가 2개가 존재하여 텍스트로 주소조회시 에러발생 가능성떄문에 임의로 주석처리
        list.add(new RequestAddr("경기도", "안성시", 550));
        list.add(new RequestAddr("경기도", "안양시", 17));
        list.add(new RequestAddr("경기도", "양주시", 630));
        list.add(new RequestAddr("경기도", "양평군", 830));
        list.add(new RequestAddr("경기도", "여주군", 730));
        list.add(new RequestAddr("경기도", "여주시", 670));
        list.add(new RequestAddr("경기도", "연천군", 800));
        list.add(new RequestAddr("경기도", "오산시", 370));
        list.add(new RequestAddr("경기도", "용인시", 46));
        list.add(new RequestAddr("경기도", "의왕시", 430));
        list.add(new RequestAddr("경기도", "의정부시", 150));
        list.add(new RequestAddr("경기도", "이천시", 500));
        list.add(new RequestAddr("경기도", "파주시", 480));
        list.add(new RequestAddr("경기도", "평택시", 220));
        list.add(new RequestAddr("경기도", "포천군", 810));
        list.add(new RequestAddr("경기도", "포천시", 650));
        list.add(new RequestAddr("경기도", "하남시", 450));
        list.add(new RequestAddr("경기도", "화성시", 590));


        list.add(new RequestAddr("강원도", "강릉시", 150));
        list.add(new RequestAddr("강원도", "고성군", 820));
        list.add(new RequestAddr("강원도", "동해시", 170));
        list.add(new RequestAddr("강원도", "삼척시", 230));
        list.add(new RequestAddr("강원도", "속초시", 210));
        list.add(new RequestAddr("강원도", "양구군", 800));
        list.add(new RequestAddr("강원도", "양양군", 830));
        list.add(new RequestAddr("강원도", "영월군", 750));
        list.add(new RequestAddr("강원도", "원주시", 130));
        list.add(new RequestAddr("강원도", "인제군", 810));
        list.add(new RequestAddr("강원도", "정선군", 770));
        list.add(new RequestAddr("강원도", "철원군", 780));
        list.add(new RequestAddr("강원도", "춘천시", 110));
        list.add(new RequestAddr("강원도", "태백시", 190));
        list.add(new RequestAddr("강원도", "평창군", 760));
        list.add(new RequestAddr("강원도", "홍천군", 720));
        list.add(new RequestAddr("강원도", "화천군", 790));
        list.add(new RequestAddr("강원도", "횡성군", 730));

        list.add(new RequestAddr("충청북도", "괴산군", 760));
        list.add(new RequestAddr("충청북도", "단양군", 800));
        list.add(new RequestAddr("충청북도", "보은군", 720));
        list.add(new RequestAddr("충청북도", "영동군", 740));
        list.add(new RequestAddr("충청북도", "옥천군", 730));
        list.add(new RequestAddr("충청북도", "음성군", 770));
        list.add(new RequestAddr("충청북도", "제천시", 150));
        list.add(new RequestAddr("충청북도", "증평군", 745));
        list.add(new RequestAddr("충청북도", "진천군", 750));
        list.add(new RequestAddr("충청북도", "청원군", 710));
        list.add(new RequestAddr("충청북도", "청주시", 11));
        list.add(new RequestAddr("충청북도", "충주시", 130));


        list.add(new RequestAddr("충청남도", "계룡시", 250));
        list.add(new RequestAddr("충청남도", "공주시", 150));
        list.add(new RequestAddr("충청남도", "금산군", 710));
        list.add(new RequestAddr("충청남도", "논산시", 230));
        list.add(new RequestAddr("충청남도", "당진군", 830));
        list.add(new RequestAddr("충청남도", "당진시", 270));
        list.add(new RequestAddr("충청남도", "보령시", 180));
        list.add(new RequestAddr("충청남도", "부여군", 760));
        list.add(new RequestAddr("충청남도", "서산시", 210));
        list.add(new RequestAddr("충청남도", "서천군", 770));
        list.add(new RequestAddr("충청남도", "아산시", 200));
        list.add(new RequestAddr("충청남도", "연기군", 730));
        list.add(new RequestAddr("충청남도", "예산군", 810));
        list.add(new RequestAddr("충청남도", "천안시", 13));
        list.add(new RequestAddr("충청남도", "청양군", 790));
        list.add(new RequestAddr("충청남도", "태안군", 825));
        list.add(new RequestAddr("충청남도", "홍성군", 800));

        list.add(new RequestAddr("전라북도", "고창군", 790));
        list.add(new RequestAddr("전라북도", "군산시", 130));
        list.add(new RequestAddr("전라북도", "김제시", 210));
        list.add(new RequestAddr("전라북도", "남원시", 190));
        list.add(new RequestAddr("전라북도", "무주군", 730));
        list.add(new RequestAddr("전라북도", "부안군", 800));
        list.add(new RequestAddr("전라북도", "순창군", 770));
        list.add(new RequestAddr("전라북도", "완주군", 710));
        list.add(new RequestAddr("전라북도", "익산시", 140));
        list.add(new RequestAddr("전라북도", "임실군", 750));
        list.add(new RequestAddr("전라북도", "장수군", 740));
        list.add(new RequestAddr("전라북도", "전주시", 11));
        list.add(new RequestAddr("전라북도", "정읍시", 180));
        list.add(new RequestAddr("전라북도", "진안군", 720));

        list.add(new RequestAddr("전라남도", "강진군", 810));
        list.add(new RequestAddr("전라남도", "고흥군", 770));
        list.add(new RequestAddr("전라남도", "곡성군", 720));
        list.add(new RequestAddr("전라남도", "광양시", 230));
        list.add(new RequestAddr("전라남도", "구례군", 730));
        list.add(new RequestAddr("전라남도", "나주시", 170));
        list.add(new RequestAddr("전라남도", "담양군", 710));
        list.add(new RequestAddr("전라남도", "목포시", 110));
        list.add(new RequestAddr("전라남도", "무안군", 840));
        list.add(new RequestAddr("전라남도", "보성군", 780));
        list.add(new RequestAddr("전라남도", "순천시", 150));
        list.add(new RequestAddr("전라남도", "신안군", 910));
        list.add(new RequestAddr("전라남도", "여수시", 130));
        list.add(new RequestAddr("전라남도", "영광군", 870));
        list.add(new RequestAddr("전라남도", "영암군", 830));
        list.add(new RequestAddr("전라남도", "완도군", 890));
        list.add(new RequestAddr("전라남도", "장성군", 880));
        list.add(new RequestAddr("전라남도", "장흥군", 800));
        list.add(new RequestAddr("전라남도", "진도군", 900));
        list.add(new RequestAddr("전라남도", "함평군", 860));
        list.add(new RequestAddr("전라남도", "해남군", 820));
        list.add(new RequestAddr("전라남도", "화순군", 790));

        list.add(new RequestAddr("경상북도", "경산시", 290));
        list.add(new RequestAddr("경상북도", "경주시", 130));
        list.add(new RequestAddr("경상북도", "고령군", 830));
        list.add(new RequestAddr("경상북도", "구미시", 190));
        list.add(new RequestAddr("경상북도", "군위군", 720));
        list.add(new RequestAddr("경상북도", "김천시", 150));
        list.add(new RequestAddr("경상북도", "문경시", 280));
        list.add(new RequestAddr("경상북도", "봉화군", 920));
        list.add(new RequestAddr("경상북도", "상주시", 250));
        list.add(new RequestAddr("경상북도", "성주군", 840));
        list.add(new RequestAddr("경상북도", "안동시", 170));
        list.add(new RequestAddr("경상북도", "영덕군", 770));
        list.add(new RequestAddr("경상북도", "영양군", 760));
        list.add(new RequestAddr("경상북도", "영주시", 210));
        list.add(new RequestAddr("경상북도", "영천시", 230));
        list.add(new RequestAddr("경상북도", "예천군", 900));
        list.add(new RequestAddr("경상북도", "울릉군", 940));
        list.add(new RequestAddr("경상북도", "울진군", 930));
        list.add(new RequestAddr("경상북도", "의성군", 730));
        list.add(new RequestAddr("경상북도", "청도군", 820));
        list.add(new RequestAddr("경상북도", "청송군", 750));
        list.add(new RequestAddr("경상북도", "칠곡군", 850));
        list.add(new RequestAddr("경상북도", "포항시", 11));



        list.add(new RequestAddr("경상남도", "거제시", 310));
        list.add(new RequestAddr("경상남도", "거창군", 880));
        list.add(new RequestAddr("경상남도", "고성군", 820));
        list.add(new RequestAddr("경상남도", "김해시", 250));
        list.add(new RequestAddr("경상남도", "남해군", 840));
        list.add(new RequestAddr("경상남도", "마산시", 160));
        list.add(new RequestAddr("경상남도", "밀양시", 270));
        list.add(new RequestAddr("경상남도", "사천시", 240));
        list.add(new RequestAddr("경상남도", "산청군", 860));
        list.add(new RequestAddr("경상남도", "양산시", 332));
//        list.add(new RequestAddr("경상남도", "양산시", 330)); // 여기서는 주소 텍스트로 구분하여 임의로 중복되는 양신 코드 하나를 주석처리했다
        list.add(new RequestAddr("경상남도", "의령군", 720));
        list.add(new RequestAddr("경상남도", "진주시", 170));
        list.add(new RequestAddr("경상남도", "진해시", 190));
        list.add(new RequestAddr("경상남도", "창녕군", 740));
        list.add(new RequestAddr("경상남도", "창원시", 12));
        list.add(new RequestAddr("경상남도", "통영시", 220));
        list.add(new RequestAddr("경상남도", "하동군", 850));
        list.add(new RequestAddr("경상남도", "함안군", 730));
        list.add(new RequestAddr("경상남도", "함양군", 870));
        list.add(new RequestAddr("경상남도", "합천군", 890));


        list.add(new RequestAddr("제주특별자치도", "서귀포시", 130));
        list.add(new RequestAddr("제주특별자치도", "제주시", 110));



        return list;
    }

}
