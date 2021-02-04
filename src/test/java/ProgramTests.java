import DataTypes.BYTE;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.junit.Assert;
import org.junit.Test;

public class ProgramTests {

    @Test
    public void testFib(){
        /*

        A = 1
        B = 1

        loop:
        C = 0
        C = A + B
        A = B
        B = C

         */

        State state = new State.Builder()
                .addInst(0xA000)//0 -> index
                .addInst(0x6E01)//1 -> regE
                .addInst(0x6001)//1 -> reg0
                .addInst(0x6101)//1 -> reg1
                //Loop:
                .addInst(0xF055)//reg0 -> mem[index]
                .addInst(0xFE1E)//index = index + regE
                .addInst(0x6200)//0 -> reg2
                .addInst(0x8204)//reg2 = reg2 + reg0
                .addInst(0x8214)//reg2 = reg2 + reg1
                .addInst(0x8010)//reg0 = reg1
                .addInst(0x8120)//reg1 = reg2
                .addInst(0x1206)//jmp 0x0206

                .build();

        Processor processor = new Processor();

        List<State> states = Stream.iterate(state, processor::process)
                .map(s -> s.print(true))
                .takeUntil(s -> s.getIndex().toInt() == 10)
                .toList();
        List<Integer> fibExpected = List.of(1,1,2,3,5,8,13,21,34,55);
        List<Integer> fibActual = states.last().getMemory().slice(0,10).map(BYTE::toInt);
        fibActual.zip(fibExpected).forEach(t -> Assert.assertEquals(t._1(),t._2()));
    }

}
