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
        return new SHORT(memory.get(programCounter.toInt()),memory.get(programCounter.toInt()+1));
    }

    public State incrProgramCounter(){
        programCounter = programCounter.ADD(SHORT.of(1))._1();
        return this;
    }

    public List<Integer> extractRegisterIndexes(){
        return List.of(getInst().getNibble(2),getInst().getNibble(1)).map(BYTE::toInt);
    }

    public List<SHORT> extractRegisterValues(List<Integer> indexes){
        return registers.zipWithIndex().filter(t -> indexes.contains(t._2())).map(Tuple2::_1);
    }

    public BYTE extractLSByte(){
        return getIndex().getLower();
    }

    public SHORT extractLSShort(){
        return new SHORT(getIndex().getNibble(3),extractLSByte());
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
            getRegisters().forEach(s -> System.out.printf("0x%04X ",s.toInt()));
            System.out.print("| ");
            getStack().forEach(s -> System.out.printf("0x%04X ",s.toInt()));
            System.out.println();
        }
        return this;
    }
}
