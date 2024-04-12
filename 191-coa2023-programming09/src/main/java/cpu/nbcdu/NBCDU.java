package cpu.nbcdu;

import util.DataType;
import util.Transformer;

public class NBCDU {

    /**
     * @param src  A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest + src
     */
    DataType add(DataType src, DataType dest) {
        int length = src.toString().length();
        StringBuilder res = new StringBuilder(length);
        if (src.toString().substring(0,4).equals(dest.toString().substring(0,4))){
            res.append(Add(src.toString().substring(4),dest.toString().substring(4)));
            res.insert(0,src.toString().substring(0,4));
        } else if (src.toString().startsWith("1100")) {//src is positive
            int carry = 0;
            String dest_Src = "1101" + NegaWithAdd(dest.toString().substring(4));//反转
            for (int i = length - 1;i > 3;i-=4){
                String src_oppend = src.toString().substring(i-3,i+1);
                String dest_oppend = dest_Src.substring(i-3,i+1);
                String temp = binaryAdd(src_oppend,dest_oppend);
                if (carry == 1){
                    if (1 + Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                        temp = binaryAdd(temp,"0110");
                        carry = 1;
                    }else {
                        carry = 0;
                    }
                    temp = binaryAdd(temp,"0001");
                }else {
                    if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                        temp = binaryAdd(temp,"0110");
                        carry = 1;
                    }else {
                        carry = 0;
                    }
                }
//                if (carry == 1){
//                    temp = binaryAdd(temp,"0001");
//                }
//                if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
//                    temp = binaryAdd(temp,"0110");
//                    carry = 1;
//                }else {
//                    carry = 0;
//                }
                res.insert(0,temp);
            }
//            res.append(Add(src.toString().substring(4),Nega(dest.toString().substring(4))));
            if (carry == 1){
                res.insert(0,"1100");
            }else {
                res = new StringBuilder(NegaWithAdd(res.toString()));
//                res = new StringBuilder(NegaWithoutAdd(res.toString()));
//                res = new StringBuilder(Add(res.toString(), "0000000000000000000000000001"));
                res.insert(0,"1101");
            }
        }else {
            int carry = 0;
            String src_Src = "1101" + NegaWithAdd(src.toString().substring(4));//反转
            for (int i = length - 1;i > 3;i-=4){
                String src_oppend = src_Src.substring(i-3,i+1);
                String dest_oppend = dest.toString().substring(i-3,i+1);
                String temp = binaryAdd(src_oppend,dest_oppend);
                if (carry == 1){
                    if (1 + Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                        temp = binaryAdd(temp,"0110");
                        carry = 1;
                    }else {
                        carry = 0;
                    }
                    temp = binaryAdd(temp,"0001");
                }else {
                    if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                        temp = binaryAdd(temp,"0110");
                        carry = 1;
                    }else {
                        carry = 0;
                    }
                }
//                if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
//                    temp = binaryAdd(temp,"0110");
//                    carry = 1;
//                }else {
//                    carry = 0;
//                }
                res.insert(0,temp);
            }
//            res.append(Add(src.toString().substring(4),Nega(dest.toString().substring(4))));
            if (carry == 1){
                res.insert(0,"1100");
            }else {
//                res = new StringBuilder(NegaWithoutAdd(res.toString()));
//                res = new StringBuilder(Add(res.toString(), "0000000000000000000000000001"));
                res = new StringBuilder(NegaWithAdd(res.toString()));
                res.insert(0,"1101");
            }
        }

        return new DataType(res.toString());
    }

    /***
     *
     * @param src A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest - src
     */
    DataType sub(DataType src, DataType dest) {
        // TODO
        //src是0的时候要特判
        String sign;
        if (Transformer.NBCDToDecimal(src.toString()).equals("0")){
            return add(src,dest);
        }
        if (src.toString().startsWith("1100")){
            sign = "1101";
        }else {
            sign = "1100";
        }
        String new_src = sign + src.toString().substring(4);
        return add(new DataType(new_src),dest);
    }

    String binaryAdd(String src,String dest){
        StringBuilder res = new StringBuilder();
        int length = src.length();
        int carry = 0;
        for (int i = length - 1;i >= 0;i--){
            int src_value =  Integer.parseInt(String.valueOf(src.charAt(i)));
            int dest_value = Integer.parseInt(String.valueOf(dest.charAt(i)));
            int sum = carry + src_value + dest_value;
            if (sum == 0){
                res.insert(0,'0');
                carry = 0;
            } else if (sum == 1) {
                res.insert(0,'1');
                carry = 0;
            } else if (sum == 2) {
                res.insert(0,'0');
                carry = 1;
            }else {
                res.insert(0,'1');
                carry = 1;
            }
        }
        return res.toString();
    }

    /**
     *
     * @param oppend 不传入符号位
     * @return oppend 整体按位组取反再加1
     */
    String NegaWithAdd(String oppend){
        int length = oppend.length();
        StringBuilder res = new StringBuilder();
        for (int i = 0;i<length;i++){
            if (oppend.charAt(i) == '0'){
                res.append('1');
            }else {
                res.append('0');
            }
        }
        for (int i = length - 1;i >= 3;i-=4){
            String src_oppend = res.substring(i-3,i+1);
            String dest_oppend = "1010";
            String temp = binaryAdd(src_oppend,dest_oppend);
            res.replace(i-3,i+1,temp);
        }
        StringBuilder one = new StringBuilder("1");
        for (int i = 1;i < length;i++){
            one.insert(0, "0");
        }
        //注意取反加一时也可能会遇到表示超过十的情况
        StringBuilder new_res = new StringBuilder();
        int carry = 0;
        for (int i = length - 1;i >= 3;i-=4){
            String src_oppend = res.substring(i-3,i+1);
            String dest_oppend = one.substring(i-3,i+1);
            String temp = binaryAdd(src_oppend,dest_oppend);
            if (carry == 1){
                if (1 + Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                    temp = binaryAdd(temp,"0110");
                    carry = 1;
                }else {
                    carry = 0;
                }
                temp = binaryAdd(temp,"0001");
            }else {
                if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                    temp = binaryAdd(temp,"0110");
                    carry = 1;
                }else {
                    carry = 0;
                }
            }
            new_res.insert(0,temp);
        }
        return new_res.toString();
    }
    String NegaWithoutAdd(String oppend){
        int length = oppend.length();
        StringBuilder res = new StringBuilder();
        for (int i = 0;i<length;i++){
            if (oppend.charAt(i) == '0'){
                res.append('1');
            }else {
                res.append('0');
            }
        }
        for (int i = length - 1;i >= 3;i-=4){
            String src_oppend = res.substring(i-3,i+1);
            String dest_oppend = "1010";
            String temp = binaryAdd(src_oppend,dest_oppend);
            res.replace(i-3,i+1,temp);
        }

        return res.toString();
    }
    String Add(String src,String dest){
        StringBuilder res = new StringBuilder();
        int length = src.length();
        int carry = 0;
        for (int i = length - 1;i >= 3;i-=4){
            String src_oppend = src.substring(i-3,i+1);
            String dest_oppend = dest.substring(i-3,i+1);
            String temp = binaryAdd(src_oppend,dest_oppend);
            if (carry == 1){
                if (1 + Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                    temp = binaryAdd(temp,"0110");
                    carry = 1;
                }else {
                    carry = 0;
                }
                temp = binaryAdd(temp,"0001");
            }else {
                if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
                    temp = binaryAdd(temp,"0110");
                    carry = 1;
                }else {
                    carry = 0;
                }
            }
//            if (carry == 1){
//                temp = binaryAdd(temp,"0001");
//            }
//            if (Integer.parseInt(src_oppend,2) + Integer.parseInt(dest_oppend,2)>= 10){
//                temp = binaryAdd(temp,"0110");
//                carry = 1;
//            }else {
//                carry = 0;
//            }
            res.insert(0,temp);
        }
        return res.toString();
    }
}
