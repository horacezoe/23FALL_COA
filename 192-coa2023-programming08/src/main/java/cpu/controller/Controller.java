package cpu.controller;

import cpu.alu.ALU;
import memory.Memory;
import util.DataType;
import util.Transformer;

import java.util.Arrays;


public class Controller {
    // general purpose register
    char[][] GPR = new char[32][32];
    // program counter
    char[] PC = new char[32];
    // instruction register
    char[] IR = new char[32];
    // memory address register
    char[] MAR = new char[32];
    // memory buffer register
    char[] MBR =  new char[32];
    char[] ICC = new char[2];

    // 单例模式
    private static final Controller controller = new Controller();

    private Controller(){
        //规定第0个寄存器为zero寄存器
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
    }

    public static Controller getController(){
        return controller;
    }

    public void reset(){
        PC = new char[32];
        IR = new char[32];
        MAR = new char[32];
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
        interruptController.reset();
    }

    public InterruptController interruptController = new InterruptController();
    public ALU alu = new ALU();

    public void tick(){
        String ICC_sign = new String(ICC);
        switch(ICC_sign){
            case "00"://取指
                getInstruct();

                break;
            case "01"://间址
                findOperand();

                break;
            case "10"://执行
                operate();

                break;
            case "11"://中断
                interrupt();

                break;
            default:
        }

        // TODO
    }

    /** 执行取指操作 */
    private void getInstruct(){
        // TODO
        MAR = PC;
        String data = byteToString(Memory.getMemory().read(new String(MAR),4));
        PC = Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt(new String(PC))) + 4)).toCharArray();
        MBR = data.toCharArray();
        IR = MBR;
        if (data.startsWith("1101110")){
            ICC = "01".toCharArray();
        }else if(data.startsWith("1100111")){
            ICC[0] = '1';
            ICC[1] = '1';
        }else{
            ICC[0] = '1';
            ICC[1] = '0';
        }
    }

    /**
     *
     * @param array byte数组
     * @return byte数组每一个bit对应位的二进制表示的String
     */
    public String byteToString(byte[] array){
        StringBuilder res = new StringBuilder();
        for (byte element : array) {
            for (int i = 7; i >= 0; i--) {
                // 逐位将每个字节转换为二进制表示，并追加到 StringBuilder
                res.append((element >> i) & 1);
            }
        }
        return res.toString();
    }

    /** 执行间址操作 */
    private void findOperand(){
        // TODO
//        将rs2中的内容加载到MAR中
//        根据MAR中的地址读出内存中对应数据存回rs2中
        String address = new String(MBR).substring(20,25);
        int pAddr = Integer.parseInt(Transformer.binaryToInt(address));
        String new_address = new String(GPR[pAddr]);
        MAR = new_address.toCharArray();
        String data = byteToString(Memory.getMemory().read(new_address,4));
        GPR[pAddr] = data.toCharArray();
        ICC[0] = '0';
        ICC[1] = '1';
    }

    /** 执行周期 */
    private void operate(){
        // TODO
        String OP = new String(IR);
        if (OP.startsWith("1100110") || OP.startsWith("1101110")){//add and addc
            int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
            int r1 = Integer.parseInt(Transformer.binaryToInt(OP.substring(15,20)));
            int r2 = Integer.parseInt(Transformer.binaryToInt(OP.substring(20,25)));
            String data_r1 = new String(GPR[r1]);
            String data_r2 = new String(GPR[r2]);
            String dest_data = new ALU().add(new DataType(data_r1),new DataType(data_r2)).toString();
            GPR[dest] = dest_data.toCharArray();
        } else if (OP.startsWith("1100100")) {//addi
            int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
            int r1 = Integer.parseInt(Transformer.binaryToInt(OP.substring(15,20)));
            String data_r1 = new String(GPR[r1]);
            String imm = OP.substring(20);
            String dest_info = Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt(data_r1)) + Integer.parseInt(Transformer.binaryToInt(imm))));
            GPR[dest] = dest_info.toCharArray();
        } else if (OP.startsWith("1100000")) {//lw
            int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
            int r1 = Integer.parseInt(Transformer.binaryToInt(OP.substring(15,20)));
            int offset = Integer.parseInt(Transformer.binaryToInt(OP.substring(20)));
            String data_r1 = new String(GPR[r1]);
            int data_r1_value = Integer.parseInt(Transformer.binaryToInt(data_r1));
            String pAddr = Transformer.intToBinary(String.valueOf(data_r1_value + offset));
            String data = byteToString(Memory.getMemory().read(pAddr,4));
            GPR[dest] = data.toCharArray();
        } else if (OP.startsWith("1110110")) {//lui
            int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
            String imm = OP.substring(12) + "000000000000";
            GPR[dest] = imm.toCharArray();
        } else if (OP.startsWith("1110011")) {///jalr
            int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
            int r1 = Integer.parseInt(Transformer.binaryToInt(OP.substring(15,20)));
            int offset = Integer.parseInt(Transformer.binaryToInt(OP.substring(20)));
            GPR[dest] = PC;
//            GPR[dest] = Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt(new String(PC))) + 4)).toCharArray();
            GPR[1] = GPR[dest];
            String pAddr = Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt(new String(GPR[r1])) + offset)));
            PC = pAddr.toCharArray();
        }else {//ecall

        }
        ICC[0] = '0';
        ICC[1] = '0';
    }

    /** 执行中断操作 */
    private void interrupt(){
        // TODO
        String OP = new String(IR);
        int dest = Integer.parseInt(Transformer.binaryToInt(OP.substring(7,12)));
        GPR[dest] = PC;
//            GPR[dest] = Transformer.intToBinary(String.valueOf(Integer.parseInt(Transformer.binaryToInt(new String(PC))) + 4)).toCharArray();
//        interruptController.signal = true;
        interruptController.handleInterrupt();
        ICC[0] = '0';
        ICC[1] = '0';
    }

    public class InterruptController{
        // 中断信号：是否发生中断
        public boolean signal;
        public StringBuffer console = new StringBuffer();
        /** 处理中断 */
        public void handleInterrupt(){
            console.append("ecall ");
        }
        public void reset(){
            signal = false;
            console = new StringBuffer();
        }
    }

    // 以下一系列的get方法用于检查寄存器中的内容进行测试，请勿修改

    // 假定代码程序存储在主存起始位置，忽略系统程序空间
    public void loadPC(){
        PC = GPR[0];
    }

    public char[] getRA() {
        //规定第1个寄存器为返回地址寄存器
        return GPR[1];
    }

    public char[] getGPR(int i) {
        return GPR[i];
    }
}
