package com.explain;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by 박지운 on 2018-10-24.
 */

public class NounExtracter {
    HashSet<String> nounSet;

    String testString;

    NounExtracter(HashSet<String> nounSet){
        this.nounSet = nounSet;
    }

    void testCase(){
        String inputString = "[날씨] 구름많고 쌀쌀한 한글날…내일 밤부터 경기북부 비 한글날인 내일 쾌청한 가을 하늘을 보기는 조금 어렵겠습니다. 전국에 구름이 많이 지나겠는데요. 공휴일을 맞이해서 야외활동 계획하시는 분들 참 많으실 겁니다. 한낮에도 20도 안팎으로 쌀쌀하니까요. 외출하신다면 옷차림을 잘하셔야겠습니다. 내일 밤에는 경기 북부지역에 비 소식이 들어 있습니다. 비는 모레 아침 사이 중부지방과 호남, 영남 내륙으로도 가끔 오겠는데요. 비의 양은 5~20mm로 많지는 않겠지만 중부지방에는 돌풍과 천둥, 번개가 동반될 수 있습니다. 안전 사고에 유의하셔야겠습니다.";
        //String inputString = "안녕하세요 구글입니다.";
        testString = inputString;
    }

    String removeOther(String input){
        String output = "";
        output = input.replaceAll("[^\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F\\s]", ""); //한글, 스페이스를 제외한 모든 스트링 삭제.
        return output;
    }

    ArrayList<String> stringPermutation(String input){
        ArrayList<String> output = new ArrayList<>();
        /* 한글자는 무시 */
        if(input.length() == 1) {
//            output.add(input);
        } else {
            for(int i = 0; i < input.length() - 1; i++){
                for(int j = input.length() - i; j > 1+i ; j--){
                    output.add(input.substring(i, i+j));
                }
            }
        }

        return output;
    }

    HashSet<String> getNoun (String input){
        HashSet<String> output = new HashSet<>();
        input = removeOther(input);
        String[] stringList = input.split(" ");

        for(int i = 0; i < stringList.length; i++) {
            ArrayList<String> elemental = stringPermutation(stringList[i]);

            for (int j = 0; j < elemental.size(); j++) {
                if (nounSet.contains(elemental.get(j))) {
                    output.add(elemental.get(j));
                    break;
                }
            }
        }
        return output;
    }
}
