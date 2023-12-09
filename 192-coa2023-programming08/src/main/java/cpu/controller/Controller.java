package cpu.controller;

import cpu.alu.ALU;


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
        // TODO
    }

    /** 执行取指操作 */
    private void getInstruct(){
        // TODO
    }

    /** 执行间址操作 */
    private void findOperand(){
        // TODO
    }

    /** 执行周期 */
    private void operate(){
        // TODO
    }

    /** 执行中断操作 */
    private void interrupt(){
        // TODO
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
