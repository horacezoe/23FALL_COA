package memory.disk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SchedulerTest {

    Scheduler scheduler = new Scheduler();

    int start1 = 128;
    int[] request1 = {50, 200};

    @Test
    public void FCFSTest1() {
        double result = scheduler.FCFS(start1, request1);
        assertEquals(114, result, 0.01);
    }

    @Test
    public void SSTFTest1() {
        double result = scheduler.SSTF(start1, request1);
        assertEquals(111, result, 0.01);
    }

    @Test
    public void SCANTest1() {
        double result = scheduler.SCAN(start1, request1, true);
        assertEquals(166, result, 0.01);
    }

    @Test
    public void SCANTest2() {
        double result = scheduler.SCAN(start1, request1, false);
        assertEquals(164, result, 0.01);
    }

    @Test
    public void SCANTest3(){
        double result = scheduler.SCAN(start1, new int[]{200},true);
        assertEquals(72,result,0.01);
    }

    @Test
    public void CSCANTest1() {
        double result = scheduler.CSCAN(start1, request1);
        assertEquals(216, result, 0.01);
    }

    @Test
    public void LOOKTest1() {
        double result = scheduler.LOOK(start1, request1,true);
        assertEquals(111, result, 0.01);
    }

    @Test
    public void LOOKTest2() {
        double result = scheduler.LOOK(start1, request1,false);
        assertEquals(114, result, 0.01);
    }
    @Test
    public void CLOOKTest1() {
        double result = scheduler.CLOOK(start1, request1);
        assertEquals(111, result, 0.01);
    }

}
