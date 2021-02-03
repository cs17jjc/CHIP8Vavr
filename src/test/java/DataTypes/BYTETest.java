package DataTypes;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class BYTETest extends TestCase {

    public void testADD() {
        Assert.assertEquals(0,BYTE.of(0).ADD(BYTE.of(0),false)._1().toInt());
        Assert.assertEquals(1,BYTE.of(0).ADD(BYTE.of(0),true)._1().toInt());
        Assert.assertEquals(2,BYTE.of(1).ADD(BYTE.of(1),false)._1().toInt());
        Assert.assertEquals(255,BYTE.of(127).ADD(BYTE.of(128),false)._1().toInt());
        Assert.assertEquals(0,BYTE.of(255).ADD(BYTE.of(1),false)._1().toInt());
        Assert.assertEquals(true,BYTE.of(255).ADD(BYTE.of(1),false)._2());
    }

    public void testSUB() {
        Assert.assertEquals(0,BYTE.of(0).SUB(BYTE.of(0),true)._1().toInt());
        Assert.assertEquals(0,BYTE.of(1).SUB(BYTE.of(1),true)._1().toInt());
        Assert.assertEquals(0xFF,BYTE.of(0).SUB(BYTE.of(1),true)._1().toInt());
        Assert.assertEquals(false,BYTE.of(0).SUB(BYTE.of(1),true)._2());
        Assert.assertEquals(1,BYTE.of(1).SUB(BYTE.of(0),true)._1().toInt());
        Assert.assertEquals(true,BYTE.of(1).SUB(BYTE.of(0),true)._2());
        Assert.assertEquals(5,BYTE.of(10).SUB(BYTE.of(5),true)._1().toInt());
    }

    @Test
    public void testGetNibble(){
        BYTE b = BYTE.of(0xFF);
        Assert.assertEquals(0xF,b.getNibble(true).toInt());
        Assert.assertEquals(0xF,b.getNibble(false).toInt());
        b = BYTE.of(0x01);
        Assert.assertEquals(0x0,b.getNibble(true).toInt());
        Assert.assertEquals(0x1,b.getNibble(false).toInt());
    }

}