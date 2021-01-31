import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

public class State {
    short programCounter;//Memory address of currently executing instruction

    public State(short programCounter, List<Short> registers, short index, byte delay, byte sound, List<Short> stack, List<Byte> memory) {
        this.programCounter = programCounter;
        this.registers = registers;
        this.index = index;
        this.delay = delay;
        this.sound = sound;
        this.stack = stack;
        this.memory = memory;
    }

    private List<Short> registers;//16 8-bit registers
    private short index;//Address register

    private byte delay;
    private byte sound;

    private List<Short> stack;//Stack pointers

    private List<Byte> memory;

    public short getProgramCounter() {
        return programCounter;
    }
    public void setProgramCounter(short programCounter) {
        this.programCounter = programCounter;
    }
    public List<Short> getRegisters() {
        return List.ofAll(registers);
    }
    public void setRegisters(List<Short> registers) {
        this.registers = registers;
    }
    public short getIndex() {
        return index;
    }
    public void setIndex(short index) {
        this.index = index;
    }
    public byte getDelay() {
        return delay;
    }
    public void setDelay(byte delay) {
        this.delay = delay;
    }
    public byte getSound() {
        return sound;
    }
    public void setSound(byte sound) {
        this.sound = sound;
    }
    public List<Short> getStack() {
        return List.ofAll(stack);
    }
    public void setStack(List<Short> stack) {
        this.stack = stack;
    }
    public List<Byte> getMemory() {
        return List.ofAll(memory);
    }
    public void setMemory(List<Byte> memory) {
        this.memory = memory;
    }

    public static State defaultState(){
        return new State((short) 0x0200,List.fill(16,0).map(i -> (short)0),(short)0,(byte)0,(byte)0,List.empty(),List.fill(4096,0).map(i -> (byte)0));
    }

    public State clone(){
        return new State(getProgramCounter(),getRegisters(),getIndex(),getDelay(),getSound(),getStack(),getMemory());
    }

    public short getInst(){
        byte upper = memory.get(programCounter);
        byte lower = memory.get(programCounter + 0x01);

        short inst = (short) (upper << 8);
        return (short) (inst + lower);
    }

    public State incrProgramCounter(){
        programCounter = (short) (programCounter + 2);
        return this;
    }

    public List<Byte> extractRegisterIndexes(){
        short registers = (short) ((getInst() & 0x0FF0) >> 4);
        return List.of((byte)((registers & 0xF0) >> 4),(byte)(registers & 0x0F));
    }

    public List<Short> extractRegisterValues(List<Byte> indexes){
        return registers.zipWithIndex().filter(t -> indexes.contains(t._2().byteValue())).map(Tuple2::_1);
    }

    public byte extractLSByte(){
        return (byte) (getInst() & 0x00FF);
    }

    public short extractLSShort(){
        return (short) (getInst() & 0x0FFF);
    }

    public State writeInstruction(short addr, short instruction){
        State nextState = clone();
        nextState.setMemory(getMemory()
                .update(addr, (byte) ((instruction & 0xFF00) >> 8))
                .update(addr + 1, (byte) (instruction & 0x00FF)));
        return nextState;
    }
}
