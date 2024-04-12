package cpu.alu;

import util.DataType;
import util.IEEE754Float;

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
            for (int i = src.toString().length() - 1;i >= 0;i--){
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

    public String add(String src, String dest) {
        int carryIn = 0;
        StringBuilder res = new StringBuilder();
        for (int i = src.length() - 1; i >= 0; i--){
            int x = src.charAt(i) - '0';
            int y = dest.charAt(i) - '0';
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
        return res.reverse().toString();
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
            String scrStr = "0" + src.toString();
            String destStr = "0" + dest.toString();
            DataType oppendent = new DataType(Nega(scrStr));
            return  new DataType(add(new DataType(destStr),oppendent).toString().substring(1));
        }

    public String sub(String src, String dest) {
        String scrStr = "0" + src;
        String destStr = "0" + dest;
        int length = dest.length();
        if (length > src.length()){
            for (int i = 0; i < length - src.length();i++){
                scrStr = "0" + scrStr;
            }
        }
        return add(destStr,Nega(scrStr)).substring(1);
    }

        /**
         * 返回两个二进制整数的乘积(结果低位截取后32位)
         * dest * src
         * @param src  32-bits
         * @param dest 32-bits
         * @return 32-bits
         */
        public String mul(String src, String dest) {
            StringBuilder res = new StringBuilder();
            int length = src.length();
            for (int i = 0;i < length;i++){
                res.append("0");
            }
            res.append(dest);
            int carry;
            for (int i = 0; i < length;i++){
                carry = 0;
                if (res.charAt(2 * length - 1) == '1') {//problem here
                    for (int j = length - 1;j >= 0;j--){
                        int x1 = res.charAt(j) - '0';
                        int x2 = src.charAt(j) - '0';
                        int sum = carry + x2 + x1;
                        if (sum == 0) {
                            carry = 0;
                            res.replace(j,j+1,"0");
                        } else if (sum == 1) {
                            carry = 0;
                            res.replace(j,j+1,"1");
                        } else if (sum == 2) {
                            carry = 1;
                            res.replace(j,j+1,"0");
                        } else {
                            carry = 1;
                            res.replace(j,j+1,"1");
                        }
                    }
                }
                res.insert(0, carry);
            }
            return res.substring(0,2*length - 1);
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
        public String div(String src, String dest)  {
            int length = src.length();
            if (dest.equals("000000000000000000000000000") && !src.equals("000000000000000000000000000")){
                return "000000000000000000000000000";
            }else if (src.equals("000000000000000000000000000")) {
                throw new ArithmeticException();
            }

            StringBuilder remainder = new StringBuilder(dest);
            StringBuilder discuss = new StringBuilder();
            for (int i = 0; i < length; i++){
                remainder.append(0);
            }

            for (int i = 0;i < length; i++){
                String temp = remainder.substring(0,i + length);
                if (Integer.parseUnsignedInt(temp,2) - Integer.parseUnsignedInt(src,2) < 0){
                    discuss.append(0);
                }else {
                    temp = sub(src,temp);
                    remainder = remainder.replace(0,i + length,temp);
                    discuss.append(1);
                }
            }

            return discuss.toString();
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
