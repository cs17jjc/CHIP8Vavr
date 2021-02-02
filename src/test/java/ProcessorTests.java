import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Test;

public class ProcessorTests {

    @Test
    public void testAddition(){

        State state = State.defaultState()
                .writeInstruction((short)0x0200,(short)0x600A)//0x0A -> reg 0
                .writeInstruction((short)0x0202,(short)0x700A)//0x0A + reg0 -> reg0
                .writeInstruction((short)0x0204,(short)0x6110)//0x10 -> reg1
                .writeInstruction((short)0x0206,(short)0x8014)//reg0 + reg1 -> reg0
                .writeInstruction((short)0x0208,(short)0x7201)//0x01 + reg2 -> reg2
                ;
        state.setRegisters(state.getRegisters().update(0x2, (short) 0xFFFF));
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(6)
                .toList();

        states.forEach(s -> s.print(true));

        Assert.assertEquals(0x000A, (short)states.get(1).getRegisters().get(0x0));
        Assert.assertEquals(0x0014, (short)states.get(2).getRegisters().get(0x0));
        Assert.assertEquals(0x0024, (short)states.get(4).getRegisters().get(0x0));

        Assert.assertEquals(0x0000, (short)states.get(5).getRegisters().get(0x2));
        Assert.assertEquals(0x0000, (short)states.get(4).getRegisters().get(0xF));
        Assert.assertEquals(0x0001, (short)states.get(5).getRegisters().get(0xF));

    }

    @Test
    public void testSubtraction(){
        State state = State.defaultState()
                .writeInstruction((short)0x0200,(short)0x600B)//0x0B -> reg0
                .writeInstruction((short)0x0202,(short)0x610A)//0x0A -> reg1
                .writeInstruction((short)0x0204,(short)0x8015)//reg0 - reg1 -> reg0
                .writeInstruction((short)0x0206,(short)0x600B)//0x0B -> reg0
                .writeInstruction((short)0x0208,(short)0x8017)//reg1 - reg0 -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(6)
                .toList();

        states.forEach(s -> s.print(true));

        Assert.assertEquals(0x0001, (short)states.get(3).getRegisters().get(0x0));
        Assert.assertEquals(0x0001, (short)states.get(3).getRegisters().get(0xF));

        Assert.assertEquals((short)0xFFFF, (short)states.get(5).getRegisters().get(0x1));
        Assert.assertEquals(0x0000, (short)states.get(5).getRegisters().get(0xF));

    }

    @Test
    public void testSNE(){

        State state = State.defaultState()
                .writeInstruction((short)0x0200,(short)0x600A)//0x0A -> reg0
                .writeInstruction((short)0x0202,(short)0x6100)//0x00 -> reg1
                .writeInstruction((short)0x0204,(short)0x9010)//SNE
                .writeInstruction((short)0x0206,(short)0x600B)//0x0B -> reg0
                .writeInstruction((short)0x0208,(short)0x600C)//0x0C -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        Assert.assertEquals(0x000C, (short)states.get(4).getRegisters().get(0x0));
    }

    @Test
    public void testLIADDR(){

        State state = State.defaultState()
                .writeInstruction((short)0x0200,(short)0xAF0F)//0x0F0F -> index
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(2)
                .toList();

        Assert.assertEquals(0x0000, (short)states.get(0).getIndex());
        Assert.assertEquals(0x0F0F, (short)states.get(1).getIndex());
    }

    @Test
    public void testJMPV0ADR(){
        State state = State.defaultState()
                .writeInstruction((short)0x0200,(short)0x1000)//JMP 0x0000
                .writeInstruction((short)0x0002,(short)0x6001)//0x0001 -> reg0
                .writeInstruction((short)0x0004,(short)0xB005)//JMP V0 addr
                .writeInstruction((short)0x0006,(short)0x600A)//0x000A -> reg0
                .writeInstruction((short)0x0008,(short)0x600B)//0x00B -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        Assert.assertEquals(0x000B, (short)states.get(4).getRegisters().get(0x0));
    }

}
