import DataTypes.BYTE;
import DataTypes.SHORT;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

public class State {

    public State(SHORT programCounter, List<SHORT> registers, SHORT index, BYTE delay, BYTE sound, List<SHORT> stack, List<BYTE> memory) {
        this.programCounter = programCounter;
        this.registers = registers;
        this.index = index;
        this.delay = delay;
        this.sound = sound;
        this.stack = stack;
        this.memory = memory;
    }

    SHORT programCounter;//Memory address of currently executing instruction

    private List<SHORT> registers;//16 8-bit registers
    private SHORT index;//Address register

    private BYTE delay;
    private BYTE sound;

    private List<SHORT> stack;//Stack pointers

    private List<BYTE> memory;

    public SHORT getProgramCounter() {
        return programCounter;
    }
    public void setProgramCounter(SHORT programCounter) {
        this.programCounter = programCounter;
    }
    public List<SHORT> getRegisters() {
        return List.ofAll(registers);
    }
    public void setRegisters(List<SHORT> registers) {
        this.registers = registers;
    }
    public SHORT getIndex() {
        return index;
    }
    public void setIndex(SHORT index) {
        this.index = index;
    }
    public BYTE getDelay() {
        return delay;
    }
    public void setDelay(BYTE delay) {
        this.delay = delay;
    }
    public BYTE getSound() {
        return sound;
    }
    public void setSound(BYTE sound) {
        this.sound = sound;
    }
    public List<SHORT> getStack() {
        return List.ofAll(stack);
    }
    public void setStack(List<SHORT> stack) {
        this.stack = stack;
    }
    public List<BYTE> getMemory() {
        return List.ofAll(memory);
    }
    public void setMemory(List<BYTE> memory) {
        this.memory = memory;
    }

    public static State defaultState(){
        return new State(SHORT.of( 0x0200),List.fill(16,0).map(i -> SHORT.of(0)),SHORT.of(0),BYTE.of(0),BYTE.of(0),List.empty(),List.fill(4096,0).map(i -> BYTE.of(0)));
    }

    public State clone(){
        return new State(getProgramCounter(),getRegisters(),getIndex(),getDelay(),getSound(),getStack(),getMemory());
    }

    public SHORT getInst(){
        BYTE up = memory.get(programCounter.toInt());
        BYTE low = memory.get(programCounter.ADD(SHORT.of(1))._1().toInt());
        return new SHORT(up,low);
    }

    public State incrProgramCounter(){
        State nextState = clone();
        nextState.setProgramCounter(getProgramCounter().ADD(SHORT.of(2))._1());
        return nextState;
    }

    public List<Integer> extractRegisterIndexes(){
        return List.of(getInst().getNibble(1).toInt(),getInst().getNibble(2).toInt());
    }

    public List<SHORT> extractRegisterValues(List<Integer> indexes){
        return registers.zipWithIndex().filter(t -> indexes.contains(t._2())).map(Tuple2::_1);
    }

    public BYTE extractLSByte(){
        return getInst().getLower();
    }

    public SHORT extractLSShort(){
        return new SHORT(getInst().getNibble(1),extractLSByte());
    }

    public State writeInstruction(int addr, int instruction){
        State nextState = clone();
        SHORT addrS = SHORT.of(addr);
        SHORT instS = SHORT.of(instruction);
        nextState.setMemory(getMemory()
                .update(addrS.toInt(), instS.getUpper())
                .update(addrS.toInt() + 1, instS.getLower()));
        return nextState;
    }

    public State print(boolean print){
        if(print){
            System.out.printf("0x%04X PC | ",getProgramCounter().toInt());
            System.out.printf("0x%04X INST | ",getInst().toInt());
            System.out.printf("0x%04X INDX | ",getIndex().toInt());
            getRegisters().forEach(s -> System.out.printf("0x%04X ",s.toInt()));
            System.out.print("| ");
            getStack().forEach(s -> System.out.printf("0x%04X ",s.toInt()));
            System.out.print("| ");
            getMemory().slice(0,32).forEach(s -> System.out.printf("0x%02X ",s.toInt()));
            System.out.println();
        }
        return this;
    }

    static class Builder{
        int counter;
        State state;

        public Builder(){
            this.state = defaultState();
            this.counter = state.programCounter.toInt();
        }

        Builder addInst(int instruction){
            state = state.writeInstruction(counter,instruction);
            counter += 2;
            return this;
        }

        Builder setCounter(int value){
            counter = value;
            return this;
        }

        State build(){
            return state;
        }

    }

}
