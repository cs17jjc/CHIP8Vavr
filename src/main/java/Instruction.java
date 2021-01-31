import java.util.function.Function;

public class Instruction {
    String name;
    short opcode;
    short mask;
    Function<State,State> execute;

    public Instruction(String name, short opcode, short mask, Function<State,State> execute) {
        this.name = name;
        this.opcode = opcode;
        this.mask = mask;
        this.execute = execute;
    }

    boolean checkOpcode(short inst){
        return (inst & mask) == opcode;
    }

    State execute(State state){
        return execute.apply(state);
    }

}
