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
    public void testADDIVX(){
        State state = State.defaultState()
                .writeInstruction(0x0200,0x60FF)//0x00FF -> reg0
                .writeInstruction(0x0202,0xA001)//0x0001 -> index
                .writeInstruction(0x0204,0xF01E)//index + reg0 -> index
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(4)
                .toList();

        assertShortsInt(0x0001, states.get(2).getIndex());
        assertShortsInt(0x0100, states.get(3).getIndex());
    }

    @Test
    public void testLDBVX(){
        State state = State.defaultState()
                .writeInstruction(0x0200,0x60FF)//0x00FF -> reg0
                .writeInstruction(0x0202,0xA00F)//0x000F -> index
                .writeInstruction(0x0204,0xF033)//LDBVX
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(4)
                .toList();

        Assert.assertEquals(0x0002, states.get(3).getMemory().get(0X0F).toInt());
        Assert.assertEquals(0x0005, states.get(3).getMemory().get(0x10).toInt());
        Assert.assertEquals(0x0005, states.get(3).getMemory().get(0x11).toInt());
    }

    @Test
    public void testLDIVX(){
        State state = State.defaultState()
                .writeInstruction(0x0200,0x60FF)//0x00FF -> reg0
                .writeInstruction(0x0200,0x60AA)//0x00AA -> reg1
                .writeInstruction(0x0200,0x60C3)//0x00C3 -> reg2
                .writeInstruction(0x0202,0xA000)//0x0000 -> index
                .writeInstruction(0x0204,0xF255)//LDIVX
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();

        Assert.assertEquals(0x00FF, states.get(4).getMemory().get(0X00).toInt());
        Assert.assertEquals(0x00AA, states.get(4).getMemory().get(0x01).toInt());
        Assert.assertEquals(0x00C3, states.get(4).getMemory().get(0x02).toInt());
    }

}
