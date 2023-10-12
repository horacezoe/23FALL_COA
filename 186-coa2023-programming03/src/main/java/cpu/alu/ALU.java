package cpu.alu;

import util.DataType;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {
//    /**
//     * 返回两个二进制整数的和
//     * dest + src
//     *
//     * @param src  32-bits
//     * @param dest 32-bits
//     * @return 32-bits
//     */
//    public DataType add(DataType src, DataType dest) {
//        int carryIn = 0;
//        StringBuilder res = new StringBuilder();
//        for (int i = 31;i >= 0;i--){
//            int x = src.toString().charAt(i) - '0';
//            int y = dest.toString().charAt(i) - '0';
//            int ans = carryIn + x + y;
//            if (ans == 0){
//                carryIn = 0;
//                res.append("0");
//            } else if (ans == 1) {
//                carryIn = 0;
//                res.append("1");
//            }else if (ans == 2){
//                carryIn = 1;
//                res.append("0");
//            }else {
//                carryIn = 1;
//                res.append("1");
//            }
//        }
//        return new DataType(res.reverse().toString());
//    }

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
        int scrSign = findFirstNum(srcStr,srcStr.charAt(0) - '0');
        int destSign = findFirstNum(destStr,destStr.charAt(0) - '0');
        int sign;
        if (scrSign != -1 && destSign != -1){
            sign = Math.min(scrSign,destSign);//即最小的符号位，最小的合理长度
        } else if (scrSign == -1) {
            sign = destSign;
        }else {
            sign = scrSign;
        }
        String srcTrue = srcStr.substring(sign - 1);
        String destTrue = destStr.substring(sign - 1);//sign - 1要留一个符号位
        int length = destTrue.length();//the initial length of result tobe expanded to 2*length
        for (int i = 0;i < length;i++){
            res.append("0");
        }
        res.append(srcTrue.toString());
        res.append("0");//noticing res.charat(2*len) - res.charat(2*len-1)

        String posiSrc = srcTrue;
        String negaSrc = Nega(srcTrue);
        StringBuilder Prezero = new StringBuilder();
        for (int i = 0;i < length;i++){
            Prezero.append("0");
        }

        String zero = Prezero.toString();for (int i = 0; i < length;i++){
            int judge = res.charAt(2*length) - res.charAt(2*length-1);
            if (judge == 0){
                res = ADD(res,zero);
            } else if (judge == -1) {
                res = ADD(res,negaSrc);
            }else {
                res = ADD(res,posiSrc);
            }
            res.insert(0,Character.toString(res.charAt(0)));
//            if (res.charAt(0) == '1'){
//                res.insert(0,"1");
//            }else {
//                res.insert(0,"0");
//            }
        }
        res.delete(2 * length, res.length());
        if (length <= 16){
            for (int i = 0;i<32 - 2*length;i++ ){
                res.insert(0,Integer.toString(res.charAt(0) - '0'));
            }
        }else {
            res.delete(32,res.length());
        }
        return new DataType(res.toString());
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
    public DataType div(DataType src, DataType dest) {
        //TODO
        return null;
    }
    public  int findFirstNum (String oppent,int num){
        if (num == 1){
            return oppent.indexOf("0");
        }else {
            return oppent.indexOf("1");
        }
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
