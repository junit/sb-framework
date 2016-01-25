package org.chinasb.common.utility;

import java.util.ArrayList;
import java.util.List;

public class Tools {

    public static List<String[]> delimiterString2Array(String delimiterString) {
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return null;
        }
        String[] ss = delimiterString.trim().split(Constants.ELEMENT_SPLIT);
        if ((ss != null) && (ss.length > 0)) {
            List<String[]> list = new ArrayList<String[]>();
            for (int i = 0; i < ss.length; i++) {
                list.add(ss[i].split(Constants.ATTRIBUTE_SPLIT));
            }
            return list;
        }
        return null;
    }

    public static int getRandomInteger(int maxValue) {
        int value = 0;
        if (maxValue > 0) {
            value = RndUtils.nextInt(maxValue);
        }
        return value;
    }
}
