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
                .writeInstruction((short)0x0202,(short)0x700A)//0x0A -> reg 0
                .writeInstruction((short)0x0204,(short)0x6110)//0x10 -> reg 1
                .writeInstruction((short)0x0206,(short)0x8014)//reg1 + reg2 -> reg 1
                ;
        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .take(5)
                .toList();
        states.forEach(s -> System.out.println(String.format("0x%04X",s.programCounter) + " " + String.format("0x%04X",s.getInst()) + " " + String.format("0x%04X",s.getRegisters().get(0x0))));

        Assert.assertEquals(0x000A, (short)states.get(1).getRegisters().get(0x0));
        Assert.assertEquals(0x0014, (short)states.get(2).getRegisters().get(0x0));
        Assert.assertEquals(0x0024, (short)states.get(4).getRegisters().get(0x0));

    }

}
