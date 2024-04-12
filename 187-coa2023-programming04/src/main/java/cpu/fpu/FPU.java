package cpu.fpu;

import util.DataType;
import util.IEEE754Float;
import util.Transformer;
import cpu.alu.ALU;

import java.util.Objects;

import static util.Transformer.intToBinary;


/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用3位保护位进行计算
 */
public class FPU {

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_INF,IEEE754Float.P_ZERO,IEEE754Float.P_INF},
            {IEEE754Float.P_INF,IEEE754Float.N_ZERO,IEEE754Float.P_INF},
            {IEEE754Float.N_ZERO,IEEE754Float.P_INF,IEEE754Float.P_INF},
            {IEEE754Float.P_ZERO,IEEE754Float.P_INF,IEEE754Float.P_INF},
            {IEEE754Float.N_INF,IEEE754Float.P_ZERO,IEEE754Float.N_INF},
            {IEEE754Float.N_INF,IEEE754Float.N_ZERO,IEEE754Float.N_INF},
            {IEEE754Float.N_ZERO,IEEE754Float.N_INF,IEEE754Float.N_INF},
            {IEEE754Float.P_ZERO,IEEE754Float.N_INF,IEEE754Float.N_INF},
            {IEEE754Float.P_INF,IEEE754Float.P_INF,IEEE754Float.P_INF},
            {IEEE754Float.N_INF,IEEE754Float.N_INF,IEEE754Float.N_INF},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF,IEEE754Float.P_ZERO,IEEE754Float.P_INF},
            {IEEE754Float.P_INF,IEEE754Float.N_ZERO,IEEE754Float.P_INF},
            {IEEE754Float.N_ZERO,IEEE754Float.P_INF,IEEE754Float.N_INF},
            {IEEE754Float.P_ZERO,IEEE754Float.P_INF,IEEE754Float.N_INF},
            {IEEE754Float.N_INF,IEEE754Float.P_ZERO,IEEE754Float.N_INF},
            {IEEE754Float.N_INF,IEEE754Float.N_ZERO,IEEE754Float.N_INF},
            {IEEE754Float.N_ZERO,IEEE754Float.N_INF,IEEE754Float.P_INF},
            {IEEE754Float.P_ZERO,IEEE754Float.N_INF,IEEE754Float.P_INF},
            {IEEE754Float.N_INF,IEEE754Float.P_INF,IEEE754Float.N_INF},
            {IEEE754Float.P_INF,IEEE754Float.N_INF,IEEE754Float.P_INF},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };

    private final String[][] mulCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF,IEEE754Float.P_INF,IEEE754Float.P_INF},
            {IEEE754Float.P_INF,IEEE754Float.N_INF,IEEE754Float.N_INF},
            {IEEE754Float.N_INF,IEEE754Float.P_INF,IEEE754Float.N_INF},
            {IEEE754Float.N_INF,IEEE754Float.N_INF,IEEE754Float.P_INF}
    };

    private final String[][] divCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
    };

    /**
     * compute the float add of (dest + src)
     */
    public DataType add(DataType src, DataType dest) {
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        //判断NAN的没用
        if (cornerCheck(addCorner,src.toString(),dest.toString()) != null){
            return new DataType(Objects.requireNonNull(cornerCheck(addCorner, src.toString(), dest.toString())));
        }
        //处理边界情况 above

        DataType res;
        char sign_res;
        String exp_res;
        String mant_res;
        // 提取符号、阶码、尾数 below

        char sign_Src = src.toString().charAt(0);
        char sign_Dest = dest.toString().charAt(0);
        String exp_Src = src.toString().substring(1,9);
        String exp_Dest = dest.toString().substring(1,9);
        String mant_Src = src.toString().substring(9);
        String mant_Dest = dest.toString().substring(9);



        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("00000000")){
            return src;
        } else if (mant_Src.equals("00000000000000000000000") && exp_Src.equals("00000000")) {
            return dest;
        }
        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("11111111")){
            return dest;
        } else if (mant_Src.equals("00000000000000000000000") && exp_Src.equals("11111111")) {
            return src;
        }
        String operand_Src;
        String operand_Dest;
//        Boolean isSmall_Src = false;
//        Boolean isSmall_Dest = false;



        if(exp_Src.equals("00000000")){
            exp_Src = "00000001";
            operand_Src = "0" + mant_Src + "000";
//            isSmall_Src = true;
        }else {
            operand_Src = "1" + mant_Src + "000";
        }
        if (exp_Dest.equals("00000000")){
            exp_Dest = "00000001";
            operand_Dest = "0" + mant_Dest +"000";
//            isSmall_Dest = true;
        }else {
            operand_Dest = "1" + mant_Dest +"000";
        }
        if (Integer.parseInt(Transformer.binaryToInt(exp_Src)) >= Integer.parseInt(Transformer.binaryToInt(exp_Dest))){
            //src's exp is larger
            exp_res = exp_Src;
            while (!exp_Src.equals(exp_Dest)){
                exp_Dest = new ALU().ADD(new StringBuilder(exp_Dest),"00000001").toString();
                operand_Dest = rightShift(operand_Dest,1);
                if (Objects.equals(mant_Dest, "000000000000000000000000000")){
                    return src;
                }
            }
            //相加
        }else {
            exp_res = exp_Dest;
            //dest's exp is larger
            while (!exp_Src.equals(exp_Dest)){
                exp_Src = new ALU().ADD(new StringBuilder(exp_Src),"00000001").toString();
                operand_Src = rightShift(operand_Src,1);
                if (Objects.equals(mant_Src, "000000000000000000000000000")){
                    return dest;
                }
            }
            //相加
        }
        operand_Dest = "0" + "0" + operand_Dest;
        operand_Src = "0" +"0" +  operand_Src;
        if (sign_Dest == '1')operand_Dest = new ALU().Nega(operand_Dest);
        if (sign_Src == '1')operand_Src = new ALU().Nega(operand_Src);
        mant_res = new ALU().ADD(new StringBuilder(operand_Dest),operand_Src).toString();
        if (mant_res.charAt(0) == '0'){
            sign_res = '0';
        }else {
            sign_res = '1';
            mant_res = new ALU().Nega(mant_res);
        }
        mant_res = mant_res.substring(1);
        int index = mant_res.indexOf("1");
        if (index == 0){
            //判断是否发生进位，是否需要左移
            exp_res = new ALU().ADD(new StringBuilder(exp_res),"00000001").toString();
            if (exp_res.equals("00000000")){
                mant_res = "0" + mant_res;
            }
        }else {
            while (index != 1 && !exp_res.equals("00000000")){
                index--;
                exp_res = new ALU().ADD(new StringBuilder(exp_res),new ALU().Nega("00000001")).toString();
                mant_res = mant_res.substring(1) + "0";
            }
            if (index == 1 && !exp_res.equals("00000000")){
                mant_res = mant_res.substring(1) +  "0";
            }
        }
//        if (isSmall_Dest && isSmall_Src){
//            exp_res = new ALU().ADD(new StringBuilder(exp_res),new ALU().Nega("00000001")).toString();
//            mant_res = mant_res.substring(1) + "0";
//        }
        return new DataType(round(sign_res,exp_res,mant_res));
    }

    /**
     * compute the float add of (dest - src)
     */
    public DataType sub(DataType src, DataType dest) {
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        if (cornerCheck(subCorner,dest.toString(),src.toString()) != null){
            return new DataType(Objects.requireNonNull(cornerCheck(subCorner, dest.toString(), src.toString())));
        }
        DataType res;
        char sign_Src = src.toString().charAt(0);
        char sign_Dest = dest.toString().charAt(0);
        String exp_Src = src.toString().substring(1,9);
        String exp_Dest = dest.toString().substring(1,9);//阶码
        String mant_Src = src.toString().substring(9);//尾数
        String mant_Dest = dest.toString().substring(9);
        String new_src;
        if (sign_Src == '0'){
            new_src = "1" + exp_Src + mant_Src;
        }else {
            new_src = "0" + exp_Src + mant_Src;
        }


        return add(dest,new DataType(new_src));
    }

    /**
     * compute the float mul of (dest * src)
     */
    public DataType mul(DataType src,DataType dest){
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        if (cornerCheck(mulCorner,dest.toString(),src.toString()) != null){
            return new DataType(Objects.requireNonNull(cornerCheck(mulCorner, dest.toString(), src.toString())));
        }
        DataType res;
        char sign_Src = src.toString().charAt(0);
        char sign_Dest = dest.toString().charAt(0);
        String exp_Src = src.toString().substring(1,9);
        String exp_Dest = dest.toString().substring(1,9);
        String mant_Src = src.toString().substring(9);
        String mant_Dest = dest.toString().substring(9);

        char sign_res;
        if(sign_Src == sign_Dest){
            sign_res = '0';
        }else {
            sign_res = '1';
        }


        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("00000000")){
            return new DataType(sign_res + dest.toString().substring(1));
        } else if (mant_Src.equals("00000000000000000000000") && exp_Src.equals("00000000")) {
            return new DataType(sign_res + src.toString().substring(1));
        }
        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("11111111")){
            return new DataType(sign_res + dest.toString().substring(1));
        } else if (mant_Src.equals("00000000000000000000000") && exp_Src.equals("11111111")) {
            return new DataType(sign_res + src.toString().substring(1));
        }


        String operand_Src;
        String operand_Dest;
        if(exp_Src.equals("00000000")){
            exp_Src = "00000001";
            operand_Src = "0" + mant_Src + "000";
//            isSmall_Src = true;
        }else {
            operand_Src = "1" + mant_Src + "000";
        }
        if (exp_Dest.equals("00000000")){
            exp_Dest = "00000001";
            operand_Dest = "0" + mant_Dest +"000";
//            isSmall_Dest = true;
        }else {
            operand_Dest = "1" + mant_Dest +"000";
        }
        int value_exp_Src = Integer.parseUnsignedInt(exp_Src,2);
        int value_exp_Dest = Integer.parseUnsignedInt(exp_Dest,2);


        //模拟运算below
        String mant_res = new ALU().mul(operand_Src,operand_Dest);
        int value_exp_res = value_exp_Dest +value_exp_Src - 127;
        value_exp_res ++;


        //规格化舍入below
        // 1）运算后54位尾数的隐藏位为0且阶码大于0，
        // 此时应该不断将尾数左移并将阶码减少，
        // 直至尾数隐藏位恢复为1或阶码已经减为0
        while (mant_res.charAt(0) == '0' && value_exp_res > 0){
            mant_res = mant_res.substring(1) + "0";
            value_exp_res --;
        }
        //2)运算后阶码小于0且54位尾数的前27位不全为0
        // 此时应该不断将尾数右移并将阶码增加
        // 直至阶码增加至0或尾数的前27位已经移动至全0
        while (value_exp_res < 0 && !mant_res.substring(0,26).equals("000000000000000000000000000")){
            mant_res = rightShift(mant_res,1);
            value_exp_res++;
        }

        if (value_exp_res >= 255) {
            if (sign_res == '0') return new DataType(IEEE754Float.P_INF);
            else return new DataType(IEEE754Float.N_INF);
        } else if (value_exp_res < 0) {
            return new DataType(sign_res + "0000000000000000000000000000000");
        } else if(value_exp_res == 0) {
            mant_res = rightShift(mant_res,1);
        }

        return new DataType(round(sign_res,intToBinary(String.valueOf(value_exp_res)).substring(24),mant_res));
    }

    /**
     * compute the float mul of (dest / src)
     */
    public DataType div(DataType src,DataType dest){
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        if (cornerCheck(divCorner,dest.toString(),src.toString()) != null){
            return new DataType(Objects.requireNonNull(cornerCheck(divCorner, dest.toString(), src.toString())));
        }
        if (src.toString().substring(1).equals("0000000000000000000000000000000")){
            throw  new ArithmeticException();
        }
        DataType res;
        char sign_Src = src.toString().charAt(0);
        char sign_Dest = dest.toString().charAt(0);
        String exp_Src = src.toString().substring(1,9);
        String exp_Dest = dest.toString().substring(1,9);
        int value_exp_Src = Integer.parseUnsignedInt(exp_Src,2);
        int value_exp_Dest = Integer.parseUnsignedInt(exp_Dest,2);
        String mant_Src = src.toString().substring(9);
        String mant_Dest = dest.toString().substring(9);
        if (exp_Dest.equals("00000000")){
            mant_Dest = "0" + mant_Dest + "000";
        }else {
            mant_Dest = "1" + mant_Dest + "000";
        }
        if (exp_Src.equals("00000000")){
            mant_Src = "0" + mant_Src + "000";
        }else {
            mant_Src = "1" + mant_Src + "000";
        }

        char sign_res;
        if(sign_Src == sign_Dest){
            sign_res = '0';
        }else {
            sign_res = '1';
        }

        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("00000000")){
            return new DataType(sign_res + dest.toString().substring(1));
        }
        if (mant_Dest.equals("00000000000000000000000") && exp_Dest.equals("11111111")){
            return new DataType(sign_res + dest.toString().substring(1));
        } else if (mant_Src.equals("00000000000000000000000") && exp_Src.equals("11111111")) {
            return new DataType(sign_res + src.toString().substring(1));
        }

        String mant_res = new ALU().div(mant_Src,mant_Dest);
        int value_exp_res = value_exp_Dest - value_exp_Src + 127;

        while (mant_res.charAt(0) == '0' && value_exp_res > 0){
            mant_res = mant_res.substring(1) + "0";
            value_exp_res --;
        }
        //2)运算后阶码小于0且54位尾数的前27位不全为0
        // 此时应该不断将尾数右移并将阶码增加
        // 直至阶码增加至0或尾数的前27位已经移动至全0
        while (value_exp_res < 0 && !mant_res.substring(0,26).equals("000000000000000000000000000")){
            mant_res = rightShift(mant_res,1);
            value_exp_res++;
        }

        if (value_exp_res >= 255) {
            if (sign_res == '0') return new DataType(IEEE754Float.P_INF);
            else return new DataType(IEEE754Float.N_INF);
        } else if (value_exp_res < 0) {
            return new DataType(sign_res + "0000000000000000000000000000000");
        } else if(value_exp_res == 0) {
            mant_res = rightShift(mant_res,1);
        }

        return new DataType(round(sign_res,intToBinary(String.valueOf(value_exp_res)).substring(24),mant_res.substring(0,27)));
    }

    /**
     * check corner cases of mul and div
     *
     * @param cornerMatrix corner cases pre-stored
     * @param oprA first operand (String)
     * @param oprB second operand (String)
     * @return the result of the corner case (String)
     */
    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) && oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        return null;
    }

    /**
     * right shift a num without considering its sign using its string format
     *
     * @param operand to be moved
     * @param n       moving nums of bits
     * @return after moving
     */
    private String rightShift(String operand, int n) {
        StringBuilder result = new StringBuilder(operand);  //保证位数不变
        boolean sticky = false;
        for (int i = 0; i < n; i++) {
            sticky = sticky || result.toString().endsWith("1");
            result.insert(0, "0");
            result.deleteCharAt(result.length() - 1);
        }
        if (sticky) {
            result.replace(operand.length() - 1, operand.length(), "1");
        }
        return result.substring(0, operand.length());
    }

    /**
     * 对GRS保护位进行舍入
     *
     * @param sign    符号位
     * @param exp     阶码
     * @param sig_grs 带隐藏位和保护位的尾数
     * @return 舍入后的结果
     */
    private String round(char sign, String exp, String sig_grs) {
        int grs = Integer.parseInt(sig_grs.substring(24, 27), 2);
        if ((sig_grs.substring(27).contains("1")) && (grs % 2 == 0)) {
            grs++;
        }
        String sig = sig_grs.substring(0, 24); // 隐藏位+23位
        if (grs > 4) {
            sig = oneAdder(sig);
        } else if (grs == 4 && sig.endsWith("1")) {
            sig = oneAdder(sig);
        }

        if (Integer.parseInt(sig.substring(0, sig.length() - 23), 2) > 1) {
            sig = rightShift(sig, 1);
            exp = oneAdder(exp).substring(1);
        }
        if (exp.equals("11111111")) {
            return sign == '0' ? IEEE754Float.P_INF : IEEE754Float.N_INF;
        }

        return sign + exp + sig.substring(sig.length() - 23);
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carry to the next)
     *         and the remains means the result
     */
    private String oneAdder(String operand) {
        int len = operand.length();
        StringBuilder temp = new StringBuilder(operand);
        temp.reverse();
        int[] num = new int[len];
        for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char) ('0' + bit);  //显示转化为char
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
    }

}
