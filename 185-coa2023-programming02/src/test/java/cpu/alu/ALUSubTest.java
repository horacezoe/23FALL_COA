package cpu.alu;

import org.junit.Test;
import util.DataType;

import static org.junit.Assert.assertEquals;

public class ALUSubTest {

    private final ALU alu = new ALU();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest1() {
        src = new DataType("11000000000000000000000000000000");
        dest = new DataType("11000000000000000000000000000000");
        result = alu.sub(src, dest);
        assertEquals("00000000000000000000000000000000", result.toString());
    }

}
