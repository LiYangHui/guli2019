package com.neusoft.mangerweb.util;


import java.util.*;

public class Testaaa {

    public static void main(String[] args) {
        String s = "abccba";
        char[] str = s.toCharArray();
        boolean b = ddd(str, 0);
        System.out.println(b);
    }
    public static boolean ddd(char[] strArr,int i){
        if (i<strArr.length/2){
            if (strArr[i]==strArr[strArr.length-i-1]){
                return ddd(strArr,i+1);
            }else {

                return false;
            }
        }else {
            return true;
        }
    }
}
