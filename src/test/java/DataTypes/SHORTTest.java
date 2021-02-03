package DataTypes;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class SHORTTest extends TestCase {

    @Test
    public void testAddition(){
        Assert.assertEquals(0,SHORT.of(0).ADD(SHORT.of(0))._1().toInt());
        Assert.assertEquals(0xFFAA,SHORT.of(0xFF00).ADD(SHORT.of(0x00AA))._1().toInt());
        Assert.assertEquals(0x0100,SHORT.of(0x00FF).ADD(SHORT.of(0x0001))._1().toInt());
    }

    @Test
    public void testSubtraction(){
        Assert.assertEquals(0,SHORT.of(0).SUB(SHORT.of(0))._1().toInt());

        Assert.assertEquals(1,SHORT.of(1).SUB(SHORT.of(0))._1().toInt());
        Assert.assertEquals(true,SHORT.of(1).SUB(SHORT.of(0))._2());

        Assert.assertEquals(0xFFFF,SHORT.of(0).SUB(SHORT.of(1))._1().toInt());
        Assert.assertEquals(false,SHORT.of(0).SUB(SHORT.of(1))._2());

        Assert.assertEquals(0,SHORT.of(1).SUB(SHORT.of(1))._1().toInt());

        Assert.assertEquals(0x00FF,SHORT.of(0x0100).SUB(SHORT.of(0x0001))._1().toInt());
    }

}