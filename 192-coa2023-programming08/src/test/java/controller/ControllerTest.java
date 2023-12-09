package controller;

import cpu.controller.Controller;
import memory.Memory;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ControllerTest {
    Controller controller = Controller.getController();
    Memory memory = Memory.getMemory();
    @Before
    public void init(){
        controller.reset();
        memory.reset();
    }
    @Test
    public void testAdd(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b10000000  // add r4,r2,r3
        };
        memory.write("00000000000000000000000000000000",12,data);
        for(int i = 0;i < 6;i++){
            controller.tick();
        }
        assertEquals("00000000000000000010000000000000",String.valueOf(controller.getGPR(4)));
    }

    @Test
    public void testLoadWord(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b10000000, // add r4,r2,r3
                (byte) 0b11000000, 0b01010000, 0b01000000,(byte)0b00000000  // lw r5,r4,0
        };
        memory.write("00000000000000000000000000000000",16,data);
        memory.write("00000000000000000010000000000000",4,new byte[]{0b00000000,0b00000000,0b00000000,(byte) 0b10000000});
        for(int i = 0;i < 8;i++){
            controller.tick();
        }
        assertEquals("00000000000000000000000010000000",String.valueOf(controller.getGPR(5)));
    }

    @Test
    public void testAddi(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11001000, 0b00110000, 0b00100000,      0b00000001, // addi r3,r2,1
        };
        memory.write("00000000000000000000000000000000",8,data);
        for(int i = 0; i < 4;i++){
            controller.tick();
        }
        assertEquals("00000000000000000001000000000001",String.valueOf(controller.getGPR(3)));
    }

    @Test
    public void testIndirect(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11011100, 0b00110000, 0b00000001,(byte)0b10000000, // addc r3,r3,r0
        };
        memory.write("00000000000000000000000000000000",8,data);
        memory.write("00000000000000000001000000000000",4,new byte[]{0b00000000,0b00000000,0b00000000,0b00000001});
        for(int i = 0; i < 4;i++){
            controller.tick();
        }
        assertEquals("00000000000000000000000000000001",String.valueOf(controller.getGPR(3)));
    }

    @Test
    public void testJump(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b10000000, // add r4,r2,r3
                (byte) 0b11100110, 0b01010000, 0b01000000,(byte)0b00000100, // jalr r5,r4,4
        };
        memory.write("00000000000000000000000000000000",16,data);
        memory.write("00000000000000000001000000000000",4,new byte[]{0b00000000,0b00000000,0b00000000,(byte) 0b10000000});
        for(int i = 0;i < 8;i++){
            controller.tick();
        }
        assertEquals("00000000000000000000000000010000",String.valueOf(controller.getGPR(5)));
        assertEquals("00000000000000000010000000000000",String.valueOf(controller.getGPR(4)));
    }

    @Test
    public void testInterrupt(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11001110, 0b00000000, 0b00000000,(byte)0b00000001, // ecall
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b10000000, // add r4,r2,r3
        };
        memory.write("00000000000000000000000000000000",16,data);
        for(int i = 0;i < 8;i++){
            controller.tick();
        }
        assertEquals("ecall ", controller.interruptController.console.toString());
        assertFalse(controller.interruptController.signal);
    }

    @Test
    public void testMixed(){
        controller.loadPC();
        byte[] data = {
                (byte) 0b11101100, 0b00110000, 0b00000000,      0b00000001, // lui r3,4096
                (byte) 0b11101100, 0b00100000, 0b00000000,      0b00000001, // lui r2,4096
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b00000000, // add r4,r2,r2
                (byte) 0b11000000, 0b01010000, 0b01000000,(byte)0b00000000, // lw r5,r4,0
                (byte) 0b11001110, 0b00000000, 0b00000000,(byte)0b00000001, // ecall
                (byte) 0b11001100, 0b01000000, 0b00100001,(byte)0b10000000, // add r4,r2,r2
        };
        memory.write("00000000000000000000000000000000",24,data);
        memory.write("00000000000000000001000000000000",4,new byte[]{0b00000000,0b00000000,0b00000000,(byte) 0b10000000});
        memory.write("00000000000000000010000000000000",4,new byte[]{0b00000000,0b00000000,0b00000000,(byte) 0b10000000});

        for(int i = 0;i < 12;i++){;
            controller.tick();
        }
        assertEquals("00000000000000000010000000000000",String.valueOf(controller.getGPR(4)));
        assertEquals("00000000000000000000000010000000",String.valueOf(controller.getGPR(5)));
        assertEquals("ecall ", controller.interruptController.console.toString());
        assertFalse(controller.interruptController.signal);
    }
}
