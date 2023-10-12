package util;

public class Transformer {
    /**
     * Integer to BinaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public static String intToBinary(String numStr) {
        int num = Integer.parseInt(numStr);
        if (num == 0) return "00000000000000000000000000000000";  //0单独判读
        if (num == 0x80000000) return "10000000000000000000000000000000";
        boolean isNeg = false;
        if (num < 0) {  //负数转正数
            num = -num;
            isNeg = true;
        }
        StringBuilder temp = new StringBuilder();
        while (num > 0) {  //转为二进制
            if (num % 2 == 1) temp.append("1");
            else temp.append("0");
            num /= 2;
        }
        String ans = temp.reverse().toString();  //反转
        int len = ans.length();
        for (int i = 0; i < 32 - len; i++) ans = "0" + ans;
        if (isNeg) {  //如果是负数那么取反加一
            ans = oneAdder(negation(ans)).substring(1);
        }
        return ans;
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
     */
    private static String oneAdder(String operand) {
        int len = operand.length();
        StringBuffer temp = new StringBuffer(operand);
        temp = temp.reverse();
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

    /**
     * convert the string's 0 and 1.
     * e.g 00000 to 11111
     *
     * @param operand string to convert (by default, it is 32 bits long)
     * @return string after converting
     */
    private static String negation(String operand) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < operand.length(); i++) {
            result = operand.charAt(i) == '1' ? result.append("0") : result.append("1");
        }
        return result.toString();
    }

}
