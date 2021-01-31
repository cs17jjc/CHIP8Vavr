import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class Processor {

    List<Instruction> instructionSet;

    private State RET(State state){
        return new State(state.getStack().head(),state.getRegisters(), state.getIndex(), state.getDelay(), state.getSound(), state.getStack().pop(), state.getMemory());
    }
    private State JP(State state){
        State nextState = state.clone();
        nextState.setProgramCounter(state.extractLSShort());
        return nextState;
    }
    private State CLR(State state){
        //Clear screen
        return state;
    }
    private State CALL(State state){
        State nextState = state.clone();
        nextState.setStack(state.getStack().push(state.programCounter));
        nextState.setProgramCounter(state.extractLSShort());
        return nextState;
    }

    private State SKIPEQUAL(State state){
        List<Short> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(values.get(0) == state.extractLSByte()){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }
    private State SKIPNOTEQUAL(State state){
        List<Short> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(values.get(0) != state.extractLSByte()){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }
    private State SKIPEQUALREG(State state){
        List<Short> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(values.get(0).byteValue() == values.get(1).byteValue()){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }

    private State LD(State state){
        State nextState = state.clone();
        short value = nextState.extractLSByte();
        nextState.setRegisters(state.getRegisters().update(state.extractRegisterIndexes().get(0),value));
        return nextState;
    }
    private State ADD(State state){
        State nextState = state.clone();
        byte registerAddress = state.extractRegisterIndexes().get(0);
        short value = nextState.extractLSByte();
        nextState.setRegisters(state.getRegisters().update(registerAddress, (short) (value + state.getRegisters().get(registerAddress))));
        return nextState;
    }

    private State LDREG(State state){
        List<Byte> regAddr = state.extractRegisterIndexes();
        State nextState = state.clone();
        nextState.setRegisters(state.getRegisters().update(regAddr.get(0),state.getRegisters().get(regAddr.get(1))));
        return nextState;
    }

    private State ORREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State ANDREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State XORREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State ADDREG(State state){
        State nextState = state.clone();
        List<Byte> regAddr = state.extractRegisterIndexes();
        List<Short> registers = nextState.getRegisters();
        short value = (short) (registers.get(regAddr.get(0)) + registers.get(regAddr.get(1)));
        nextState.setRegisters(registers.update(regAddr.get(0), value));
        return nextState;
    }
    private State SUBREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State SHRREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State SUBNREG(State state){
        State nextState = state.clone();

        return nextState;
    }
    private State SHLREG(State state){
        State nextState = state.clone();

        return nextState;
    }


    public Processor(){
        instructionSet = List.of(
                new Instruction("CLS",(short)0x00E0,(short)0xFFFF, this::CLR),
                new Instruction("RET",(short)0x00EE,(short)0xFFFF, this::RET),
                new Instruction("SYS addr",(short)0x0000,(short)0xF000, null),
                new Instruction("JP addr",(short)0x1000,(short)0xF0000, this::JP),
                new Instruction("CALL addr",(short)0x2000,(short)0xF000, this::CALL),
                new Instruction("SE Vx byte",(short)0x3000,(short)0xF000, this::SKIPEQUAL),
                new Instruction("SNE Vx byte",(short)0x4000,(short)0xF000, this::SKIPNOTEQUAL),
                new Instruction("SE Vx Vy",(short)0x5000,(short)0xF000, this::SKIPEQUALREG),
                new Instruction("LD Vx byte",(short)0x6000,(short)0xF000, this::LD),
                new Instruction("ADD Vx byte",(short)0x7000,(short)0xF000, this::ADD),
                new Instruction("LD Vx Vy",(short)0x8000,(short)0xF00F, this::LDREG),
                new Instruction("OR Vx Vy",(short)0x8001,(short)0xF00F, this::ORREG),
                new Instruction("AND Vx Vy",(short)0x8002,(short)0xF00F, this::ANDREG),
                new Instruction("XOR Vx Vy",(short)0x8003,(short)0xF00F, this::XORREG),
                new Instruction("ADD Vx Vy",(short)0x8004,(short)0xF00F, this::ADDREG),
                new Instruction("SYB Vx Vy",(short)0x8005,(short)0xF00F, this::SUBREG),
                new Instruction("SHR Vx Vy",(short)0x8006,(short)0xF00F, this::SHRREG),
                new Instruction("SUBN Vx Vy",(short)0x8007,(short)0xF00F, this::SUBNREG),
                new Instruction("SHL Vx Vy",(short)0x800E,(short)0xF00F, this::SHLREG),
                new Instruction("SNE Vx Vy",(short)0x9000,(short)0xF00F, null),
                new Instruction("LD I addr",(short)0xA000,(short)0xF000, null),
                new Instruction("JP V0 addr",(short)0xB000,(short)0xF000, null),
                new Instruction("RND Vx byte",(short)0xC000,(short)0xF000, null),
                new Instruction("DRW Vx Vy nibble",(short)0xD000,(short)0xF000, null),
                new Instruction("SKP Vx",(short)0xE09E,(short)0xF0FF, null),
                new Instruction("SKNP Vx",(short)0xE0A1,(short)0xF0FF, null),
                new Instruction("LD Vx DT",(short)0xF007,(short)0xF0FF, null),
                new Instruction("LD Vx k",(short)0xF00A,(short)0xF0FF, null),
                new Instruction("LD DT Vx",(short)0xF015,(short)0xF0FF, null),
                new Instruction("LD ST Vx",(short)0xF018,(short)0xF0FF, null),
                new Instruction("ADD I Vx",(short)0xF01E,(short)0xF0FF, null),
                new Instruction("LD F Vx",(short)0xF029,(short)0xF0FF, null),
                new Instruction("LD B Vx",(short)0xF033,(short)0xF0FF, null),
                new Instruction("LD [I] Vx",(short)0xF055,(short)0xF0FF, null),
                new Instruction("LD Vx [I]",(short)0xF065,(short)0xF0FF, null)
        );
    }

    State process(State state){
        short inst = state.getInst();
        Option<Instruction> ins = instructionSet.find(i -> i.checkOpcode(inst));
        return ins.get().execute(state).incrProgramCounter();
    }

}
