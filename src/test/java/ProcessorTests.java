import DataTypes.SHORT;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.junit.Assert;
import org.junit.Test;

public class ProcessorTests {

    public void assertShortsInt(int expected,SHORT actual){
        Assert.assertEquals(SHORT.of(expected).toInt(), actual.toInt());
    }

    @Test
    public void testAddition(){

        State state = State.defaultState()
                .writeInstruction(0x0200,0x600A)//0x0A -> reg 0
                .writeInstruction(0x0202,0x700A)//0x0A + reg0 -> reg0
                .writeInstruction(0x0204,0x6110)//0x10 -> reg1
                .writeInstruction(0x0206,0x8014)//reg0 + reg1 -> reg0
                .writeInstruction(0x0208,0x7201)//0x01 + reg2 -> reg2
                ;
        state.setRegisters(state.getRegisters().update(0x2, SHORT.of( 0xFFFF)));
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(6)
                .toList();

        states.forEach(s -> s.print(true));

        assertShortsInt(0x000A, states.get(1).getRegisters().get(0x0));
        assertShortsInt(0x0014, states.get(2).getRegisters().get(0x0));
        assertShortsInt(0x0024, states.get(4).getRegisters().get(0x0));

        assertShortsInt(0x0000, states.get(5).getRegisters().get(0x2));
        assertShortsInt(0x0000, states.get(4).getRegisters().get(0xF));
        assertShortsInt(0x0001, states.get(5).getRegisters().get(0xF));

    }

    @Test
    public void testSubtraction(){
        State state = State.defaultState()
                .writeInstruction(0x0200,0x600B)//0x0B -> reg0
                .writeInstruction(0x0202,0x610A)//0x0A -> reg1
                .writeInstruction(0x0204,0x8015)//reg0 - reg1 -> reg0
                .writeInstruction(0x0206,0x600B)//0x0B -> reg0
                .writeInstruction(0x0208,0x8017)//reg1 - reg0 -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(6)
                .toList();

        states.forEach(s -> s.print(true));

        assertShortsInt(0x0001, states.get(3).getRegisters().get(0x0));
        assertShortsInt(0x0001, states.get(3).getRegisters().get(0xF));

        assertShortsInt(0xFFFF, states.get(5).getRegisters().get(0x1));
        assertShortsInt(0x0000, states.get(5).getRegisters().get(0xF));

    }

    @Test
    public void testSNE(){

        State state = State.defaultState()
                .writeInstruction(0x0200,0x600A)//0x0A -> reg0
                .writeInstruction(0x0202,0x6100)//0x00 -> reg1
                .writeInstruction(0x0204,0x9010)//SNE
                .writeInstruction(0x0206,0x600B)//0x0B -> reg0
                .writeInstruction(0x0208,0x600C)//0x0C -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        assertShortsInt(0x000C, states.get(4).getRegisters().get(0x0));
    }

    @Test
    public void testLIADDR(){

        State state = State.defaultState()
                .writeInstruction(0x0200,0xAF0F)//0x0F0F -> index
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(2)
                .toList();

        assertShortsInt(0x0000, states.get(0).getIndex());
        assertShortsInt(0x0F0F, states.get(1).getIndex());
    }

    @Test
    public void testJMPV0ADR(){
        State state = State.defaultState()
                .writeInstruction(0x0200,0x1000)//JMP 0x0000
                .writeInstruction(0x0002,0x6001)//0x0001 -> reg0
                .writeInstruction(0x0004,0xB005)//JMP V0 addr
                .writeInstruction(0x0006,0x600A)//0x000A -> reg0
                .writeInstruction(0x0008,0x600B)//0x00B -> reg0
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        assertShortsInt(0x000B, states.get(4).getRegisters().get(0x0));
    }

    @Test
    public void test(){

        State state = State.defaultState()
                .writeInstruction(0x0200,0x1000)//JMP 0x0000
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        assertShortsInt(0x000B, states.get(4).getRegisters().get(0x0));

    }

}
