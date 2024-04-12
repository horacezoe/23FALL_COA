package cpu.alu;

import util.DataType;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 返回两个二进制整数的和
     * dest + src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType add(DataType src, DataType dest) {
        int carryIn = 0;
        StringBuilder res = new StringBuilder();
        for (int i = 31;i >= 0;i--){
            int x = src.toString().charAt(i) - '0';
            int y = dest.toString().charAt(i) - '0';
            int ans = carryIn + x + y;
            if (ans == 0){
                carryIn = 0;
                res.append("0");
            } else if (ans == 1) {
                carryIn = 0;
                res.append("1");
            }else if (ans == 2){
                carryIn = 1;
                res.append("0");
            }else {
                carryIn = 1;
                res.append("1");
            }
        }
        return new DataType(res.reverse().toString());
    }

    /**
     * 返回两个二进制整数的差
     * dest - src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType sub(DataType src, DataType dest) {
        StringBuilder newScr = new StringBuilder(src.toString());
        for (int i = 0;i < 32;i++){
            if (newScr.charAt(i) == '0'){
                newScr.replace(i,i+1,"1");
            }else {
                newScr.replace(i,i+1,"0");
            }
        }
        DataType oppendent = add(new DataType(newScr.toString()),new DataType("00000000000000000000000000000001"));
        return add(dest,oppendent);
    }

    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        StringBuilder res = new StringBuilder();//retrun first 32 len
        String srcStr = src.toString();
        String destStr = dest.toString();
        int length = 32;
        for (int i = 0;i < length;i++){
            res.append("0");
        }
        res.append(destStr.toString());
        res.append("0");//noticing res.charat(2*len) - res.charat(2*len-1)
        String posiSrc = srcStr;
        String negaSrc = Nega(srcStr);
        StringBuilder Prezero = new StringBuilder();
        for (int i = 0;i < length;i++){
            Prezero.append("0");
        }

        String zero = Prezero.toString();
        for (int i = 0; i < length;i++){
            int judge = res.charAt(2*length) - res.charAt(2*length-1);
            if (judge == 0){
                res = ADD(res,zero);
            } else if (judge == -1) {
                res = ADD(res,negaSrc);
            }else {
                res = ADD(res,posiSrc);
            }
            res.insert(0,Character.toString(res.charAt(0)));
        }
        return new DataType(res.toString().substring(32,64));
    }

    DataType remainderReg = new DataType("00000000000000000000000000000000");

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest)  {
        if (dest.toString().equals("00000000000000000000000000000000") && !src.toString().equals("00000000000000000000000000000000")){
            return new DataType("00000000000000000000000000000000");
        } else if (dest.toString().equals("00000000000000000000000000000000") && src.toString().equals("00000000000000000000000000000000")) {
            throw new ArithmeticException();
        } else if (src.toString().equals("00000000000000000000000000000000")) {
            throw new ArithmeticException();
        }

        StringBuilder discuss = new StringBuilder(dest.toString());
        StringBuilder remainder = new StringBuilder();
        for (int i = 0;i < 32;i++){
            remainder.append(dest.toString().charAt(0) - '0');
        }



        //恢复余数除法
        for (int i = 0;i < 32;i++){
            remainder.append(discuss.charAt(0));
            remainder.delete(0,1);
            discuss.delete(0,1);
            StringBuilder temp = new StringBuilder(remainder.toString());
            if (remainder.charAt(0) == '0' && src.toString().charAt(0) == '0'){
                remainder = ADD(remainder,Nega(src.toString()));
                boolean isZero = true;
                for (int j = 0;j < 32;j++){
                    if (remainder.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                for (int j = 0;j < 31 - i;j++){
                    if (discuss.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                if (remainder.charAt(0) == '0' || isZero){
                    discuss.append("1");
                }else {
                    discuss.append("0");
                    remainder = temp;
                }
            } else if (remainder.charAt(0) == '0' && src.toString().charAt(0) == '1') {
                remainder = ADD(remainder,src.toString());
                boolean isZero = true;
                for (int j = 0;j < 32;j++){
                    if (remainder.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                for (int j = 0;j < 31 - i;j++){
                    if (discuss.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                if (remainder.charAt(0) == '0' || isZero){
                    discuss.append("1");
                }else {
                    discuss.append("0");
                    remainder = temp;
                }
            } else if (remainder.charAt(0) == '1' && src.toString().charAt(0) == '0') {
                remainder = ADD(remainder,src.toString());
                boolean isZero = true;
                for (int j = 0;j < 32;j++){
                    if (remainder.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                for (int j = 0;j < 31 - i;j++){
                    if (discuss.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                if (remainder.charAt(0) == '1' || isZero){
                    discuss.append("1");
                }else {
                    discuss.append("0");
                    remainder = temp;
                }
            }else {
                remainder = ADD(remainder,Nega(src.toString()));
                boolean isZero = true;
                for (int j = 0;j < 32;j++){
                    if (remainder.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                for (int j = 0;j < 31 - i;j++){
                    if (discuss.charAt(j) == '1'){
                        isZero = false;
                    }
                }
                if (remainder.charAt(0) == '1' || isZero){
                    discuss.append("1");
                }else {
                    discuss.append("0");
                    remainder = temp;
                }
            }

        }
        if (src.toString().charAt(0) != dest.toString().charAt(0)){
            discuss = new StringBuilder(Nega(discuss.toString()));
        }







        remainderReg = new DataType(remainder.toString());
        return new DataType(discuss.toString());
    }

    public String Nega(String oppent){
        StringBuilder res = new StringBuilder();
        for (int i = 0;i < oppent.length();i++){
            if (oppent.charAt(i) == '0'){
                res.append("1");
            }else {
                res.append("0");
            }
        }
        StringBuilder dest = new StringBuilder();
        for (int i = 0;i < oppent.length() - 1;i++){
            dest.append("0");
        }
        dest.append("1");
        res = ADD(res,dest.toString());
        return res.toString();
    }

    /**
     *
     * @param src
     * @param dest
     * @return src + dest
     */
    public StringBuilder ADD(StringBuilder src,String dest){
        int length = dest.length();
        int carry = 0;
        for (int i = length - 1;i >= 0;i--){
            int x1 = src.charAt(i) - '0';
            int x2 = dest.charAt(i) - '0';
            int sum = carry + x2 + x1;
            if (sum == 0) {
                carry = 0;
                src.replace(i,i+1,"0");
            } else if (sum == 1) {
                carry = 0;
                src.replace(i,i+1,"1");
            } else if (sum == 2) {
                carry = 1;
                src.replace(i,i+1,"0");
            }else {
                carry = 1;
                src.replace(i,i+1,"1");
            }
        }
        return src;
    }
}
