package cpu.nbcdu;

import org.junit.Test;
import util.DataType;
import util.Transformer;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class NBCDUSubTest {

    private final NBCDU nbcdu = new NBCDU();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest1() {
        src = new DataType("11001000000001110010011100000001");
        dest = new DataType("11000110011100100101010001100001");
        result = nbcdu.sub(src, dest);
        assertEquals("11010001001101000111001001000000", result.toString());
    }

    @Test
    public void SubTest2() {
        int src_value = new Random().nextInt() % 9999999;
        int dest_value = new Random().nextInt() % 9999999;
        src = new DataType(Transformer.decimalToNBCD(""+src_value));
        dest = new DataType(Transformer.decimalToNBCD(""+dest_value));
        result = nbcdu.sub(src, dest);
        DataType expect = new DataType(Transformer.decimalToNBCD(""+(dest_value - src_value)));
        System.out.println("dest - src");
        System.out.println("src_value is " + src_value);
        System.out.println("dest_value is " + dest_value);
        System.out.println("src is " + src.toString());
        System.out.println("dest is " + dest.toString());
        System.out.println("result is " + result.toString());
        System.out.println("expect is " + expect.toString());
        assertEquals(expect.toString(), result.toString());
    }

    @Test
    public void SubTest3() {
        src = new DataType("11010000000000000000000000000000");
        dest = new DataType("11000110011100100101010001100001");
        result = nbcdu.sub(src, dest);
        assertEquals("11000110011100100101010001100001", result.toString());
    }

    @Test
    public void SubTest4() {
        for (int i = 0;i<1000000000;i++){
            int src_value = new Random().nextInt() % (9999999+1);
            int dest_value = new Random().nextInt() % (9999999+1);
            src = new DataType(Transformer.decimalToNBCD(""+src_value));
            dest = new DataType(Transformer.decimalToNBCD(""+dest_value));
            result = nbcdu.sub(src, dest);
            DataType expect = new DataType(Transformer.decimalToNBCD(""+(dest_value - src_value)));
            System.out.println("dest - src");
            System.out.println("src_value is " + src_value);
            System.out.println("dest_value is " + dest_value);
            System.out.println("src is " + src.toString());
            System.out.println("dest is " + dest.toString());
            System.out.println("result is " + result.toString());
            System.out.println("expect is " + expect.toString());
            assertEquals(expect.toString(), result.toString());
        }
    }

    @Test
    public void SubTest5() {
        int src_value =  0;
        int dest_value = 9;
        src = new DataType(Transformer.decimalToNBCD(""+src_value));
        dest = new DataType(Transformer.decimalToNBCD(""+dest_value));
        result = nbcdu.sub(src, dest);
        DataType expect = new DataType(Transformer.decimalToNBCD(""+(dest_value - src_value)));
        System.out.println("dest - src");
        System.out.println("src_value is " + src_value);
        System.out.println("dest_value is " + dest_value);
        System.out.println("src is " + src.toString());
        System.out.println("dest is " + dest.toString());
        System.out.println("result is " + result.toString());
        System.out.println("expect is " + expect.toString());
        assertEquals(expect.toString(), result.toString());
    }
}
