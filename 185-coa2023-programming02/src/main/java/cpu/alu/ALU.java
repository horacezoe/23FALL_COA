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

}
