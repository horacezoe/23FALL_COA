package util;

import java.util.Objects;

import static java.lang.Math.*;

public class Transformer {

    public static String intToBinary(String numStr) {
        int temp = Integer.parseInt(numStr);
        if (temp == 0) {
            return "00000000000000000000000000000000";
        }
        if (temp == (int) (-Math.pow(2, 31))) {
            return "10000000000000000000000000000000";
        }
        int mark = 0;//mark=0是正数，1是负数
        if (temp < 0) {
            temp = -temp;
            mark = 1;
        }
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < 32; i++) {
            if (temp % 2 == 1) {
                res.insert(0, 1);
            } else {
                res.insert(0, 0);
            }
            temp /= 2;
        }
        if (mark == 1){
            res = verseAndPlus(res);
        }
        return res.toString();
    }

    public static String binaryToInt(String binStr) {
        int res = 0;
        for (int i = 0;i<32;i++){
            int temp = Integer.parseInt(binStr.substring(i,i+1));
            res = res + temp * (int) pow(2, 31 - i);
        }
        if (binStr.substring(0,1).equals("0")){
            return String.valueOf(res);
        }else return String.valueOf(res + 1);
    }

    public static String decimalToNBCD(String decimalStr) {
        int num = Integer.parseInt(decimalStr);
        StringBuilder res = new StringBuilder("");
        if (decimalStr.charAt(0) == '-') {
            res.append("1101");
            decimalStr = decimalStr.substring(1);
        } else {
            res.append("1100");
        }
        int length = decimalStr.length();
        for (int i = 0; i < 7 - length; i++) {
            res.append("0000");
        }
        for (int i = 0; i < length; i++) {
            res.append(String.format("%4s",Integer.toString(Integer.parseInt(String.valueOf(decimalStr.charAt(i))))).replace(" ","0"));
        }
        return res.toString();
    }

    public static String NBCDToDecimal(String NBCDStr) {
        int mark = 0;//mark == 1 means positive
        String sign = NBCDStr.substring(0, 4);
        if (sign.equals("1100")) {
            mark = 1;
        }
        StringBuilder res = new StringBuilder("");
        if (mark == 0) {
            res.append("-");
        }
        for (int i = 1; i < 8; i++) {
            String temp = NBCDStr.substring(4 * i, 4 * i + 4);
            int whatever = Integer.parseUnsignedInt(temp);
            res.append(String.valueOf(whatever));
        }
        int count;
        if (mark == 0){
            count = res.indexOf("1",1);
            res.replace(1,count,"");
        }else {
            count = res.indexOf("1");
            res.replace(0,count,"");
        }
        return res.toString();
    }

    public static String floatToBinary(String floatStr) {
        float flo = Float.parseFloat(floatStr);
        if (flo == pow(2,-127)){
            return "00000000010000000000000000000000";
        }
        StringBuilder res = new StringBuilder("");
        int mark = 1;
        if (flo < 0){
            res.append("1");
            mark = 0;
            flo = -flo;
        }else {
            res.append("0");
        }
        if (Float.isNaN(flo)){
            return "NAN";
        }
        if (!Float.isFinite(flo)){
            if (mark == 0){
                return "-Inf";
            }else {
                return "+Inf";
            }
        }
        if (flo == 0.0){
            return "00000000000000000000000000000000";
        }else{
            int inte = (int) flo;
            double deci = (flo - 1.0 * inte);
            StringBuilder temp = new StringBuilder("");
            for (;inte > 0; ) {
                if (inte % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                inte /= 2;
            }
            temp.replace(0,1,"");
            int length = temp.length();
            int preE = 23 - length;
            for (int i = 0; i < preE; i ++){
                deci = deci * 2;
                if (deci >= 1.0){
                    deci = deci - 1.0;
                    temp.append("1");
                }else {
                    temp.append("0");
                }
            }
            int True_E = length +127;
            int count = 0;//这里可能有问题
            for (;True_E > 0; ) {
                count ++;
                if (True_E % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                True_E /= 2;
            }
            for (int i = 0; i < 8 - count;i++){
                temp.insert(0,"0");
            }
            res.append(temp.toString());
        }
        return res.toString();
    }

    public static String binaryToFloat(String binStr) {
        if (Objects.equals(binStr, "00000000000000000000000000000000")){
            return "+0.0";
        }else if (binStr.equals("10000000000000000000000000000000")){
            return "-0.0";
        }
        int mark = 1;//1 means positive
        if (binStr.charAt(0) == '-'){
            mark = 0;
        }
        String E = binStr.substring(1,9);
        String M = binStr.substring(9);
        if (E.equals("11111111")) {
            if (M.contains("1")) {
                return "NaN";
            } else {
                if (mark == 1){
                    return "+Inf";
                }else {
                    return "-Inf";
                }
           }
        }
        if (E.equals("00000000")){
            //极小时
            double f = 0.0;
            int fe = 1;
            for (char unit:M.toCharArray()) {
                f += Integer.parseInt(String.valueOf(unit)) / Math.pow(2, fe);
                fe++;
            }
            f = (f)*Math.pow(2, -126);
            f = (mark == 0) ? -f : f;
            return String.valueOf((float) f);
        }
        //规格化
        int valueE = 0;
        for (int i = 7;i >= 0;i--){
            valueE += Integer.parseInt(E.substring(7 - i, 8 - i)) * (int) pow(2,i);
        }
        int mlu = valueE - 127;
        int inte = (int) pow(2,mlu);
        String intePart = M.substring(0,mlu);
        for (char unit:intePart.toCharArray()){
            inte += Integer.parseInt(String.valueOf(unit)) * (int) pow(2,mlu - 1);
            mlu--;
        }
        String deciPart = M.substring(mlu);
        double deci = 0.0;
        int fe = 1;
        for (char unit:M.toCharArray()) {
            deci += Integer.parseInt(String.valueOf(unit)) / Math.pow(2, fe);
            fe++;
        }
        float res = (float) inte + (float) deci;
        return Float.toString(res);
    }

    public static StringBuilder verseAndPlus(StringBuilder oppendant) {
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < 32; i++) {
            if (oppendant.charAt(i) == '1') {
                res.append("0");
            } else {
                res.append("1");
            }
        }
        // above is verse
        //below is plus
        int pass = 1;
        for (int i = 31; i >= 0; i--) {
            int bit;
            if (res.charAt(i) == '1' ) {
                bit = 1;
            }else {
                bit = 0;
            }
            int sum = bit + pass;
            if (sum == 0) {
                res.replace(i, i + 1, "0");
                pass = 0;
            } else if (sum == 1) {
                res.replace(i, i + 1, "1");
                pass = 0;
            } else if (sum == 2) {
                res.replace(i, i + 1, "0");
                pass = 1;
            } else {
                res.replace(i, i + 1, "1");
                pass = 1;
            }
        }
        return res;
    }

}