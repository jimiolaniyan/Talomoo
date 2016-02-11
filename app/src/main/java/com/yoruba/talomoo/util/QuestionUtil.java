package com.yoruba.talomoo.util;

/**
 * Created by gidimo on 11/02/2016.
 */
public class QuestionUtil {
    public int questionCount(int position){
        switch (position){
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 50;
        }
        return 10;
    }
}
