package DataTypes;

import junit.framework.TestCase;
import org.junit.Assert;

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
        Assert.assertEquals(0,BYTE.of(0).SUB(BYTE.of(0),false)._1().toInt());
        Assert.assertEquals(0,BYTE.of(1).SUB(BYTE.of(1),false)._1().toInt());
        Assert.assertEquals(0xFF,BYTE.of(0).SUB(BYTE.of(1),false)._1().toInt());
        Assert.assertEquals(false,BYTE.of(0).SUB(BYTE.of(1),false)._2());
        Assert.assertEquals(1,BYTE.of(1).SUB(BYTE.of(0),false)._1().toInt());
        Assert.assertEquals(true,BYTE.of(1).SUB(BYTE.of(0),false)._2());
        Assert.assertEquals(5,BYTE.of(10).SUB(BYTE.of(5),false)._1().toInt());
    }
}