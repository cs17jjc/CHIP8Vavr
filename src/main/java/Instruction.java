import DataTypes.SHORT;

import java.util.function.Function;

public class Instruction {
    String name;
    SHORT opcode;
    SHORT mask;
    Function<State,State> execute;

    public Instruction(String name, SHORT opcode, SHORT mask, Function<State,State> execute) {
        this.name = name;
        this.opcode = opcode;
        this.mask = mask;
        this.execute = execute;
    }
    public Instruction(String name, int opcode, int mask, Function<State,State> execute) {
        this.name = name;
        this.opcode = SHORT.of(opcode);
        this.mask = SHORT.of(mask);
        this.execute = execute;
    }

    boolean checkOpcode(SHORT inst){
        return inst.AND(mask).matches(opcode);
    }

    State execute(State state){
        return execute.apply(state);
    }

}
