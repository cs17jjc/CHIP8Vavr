import DataTypes.BYTE;
import DataTypes.SHORT;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Random;

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
        List<SHORT> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(values.get(0).matches(new SHORT(BYTE.of(0),state.extractLSByte()))){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }
    private State SKIPNOTEQUAL(State state){
        List<SHORT> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(!values.get(0).matches(new SHORT(BYTE.of(0),state.extractLSByte()))){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }
    private State SKIPEQUALREG(State state){
        List<SHORT> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(values.get(0).matches(values.get(1))){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }

    private State LD(State state){
        State nextState = state.clone();
        SHORT value = new SHORT(BYTE.of(0),state.extractLSByte());
        nextState.setRegisters(state.getRegisters().update(state.extractRegisterIndexes().get(0),value));
        return nextState;
    }
    private State ADD(State state){
        State nextState = state.clone();
        int registerAddress = state.extractRegisterIndexes().get(0);

        Tuple2<SHORT,Boolean> result = new SHORT(BYTE.of(0),state.extractLSByte()).ADD(state.getRegisters().get(registerAddress));

        SHORT carryFlag = SHORT.of(0);
        if(result._2()){
            carryFlag = SHORT.of(1);
        }

        nextState.setRegisters(state.getRegisters()
                .update(0xF,carryFlag)
                .update(registerAddress, result._1()));
        return nextState;
    }

    private State LDREG(State state){
        List<Integer> regAddr = state.extractRegisterIndexes();
        State nextState = state.clone();
        nextState.setRegisters(state.getRegisters().update(regAddr.get(0),state.getRegisters().get(regAddr.get(1))));
        return nextState;
    }

    private State ORREG(State state){
        State nextState = state.clone();
        List<Integer> regAddr = state.extractRegisterIndexes();
        List<SHORT> registers = state.getRegisters();
        registers = registers.update(regAddr.get(0), registers.get(regAddr.get(0)).OR(registers.get(regAddr.get(1))));
        nextState.setRegisters(registers);
        return nextState;
    }
    private State ANDREG(State state){
        State nextState = state.clone();
        List<Integer> regAddr = state.extractRegisterIndexes();
        List<SHORT> registers = state.getRegisters();
        registers = registers.update(regAddr.get(0), registers.get(regAddr.get(0)).AND(registers.get(regAddr.get(1))));
        nextState.setRegisters(registers);
        return nextState;
    }
    private State XORREG(State state){
        State nextState = state.clone();
        List<Integer> regAddr = state.extractRegisterIndexes();
        List<SHORT> registers = state.getRegisters();
        registers = registers.update(regAddr.get(0), registers.get(regAddr.get(0)).XOR(registers.get(regAddr.get(1))));
        nextState.setRegisters(registers);
        return nextState;
    }
    private State ADDREG(State state){
        State nextState = state.clone();
        List<Integer> registerAddresses = state.extractRegisterIndexes();

        Tuple2<SHORT,Boolean> result = state.getRegisters().get(registerAddresses.get(0)).ADD(state.getRegisters().get(registerAddresses.get(1)));

        SHORT carryFlag = SHORT.of(0);
        if(result._2()){
            carryFlag = SHORT.of(1);
        }

        nextState.setRegisters(state.getRegisters()
                .update(0xF,carryFlag)
                .update(registerAddresses.get(0), result._1()));
        return nextState;
    }
    private State SUBREG(State state){
        State nextState = state.clone();
        List<Integer> registerAddresses = state.extractRegisterIndexes();

        Tuple2<SHORT,Boolean> result = state.getRegisters().get(registerAddresses.get(0)).SUB(state.getRegisters().get(registerAddresses.get(1)));

        SHORT carryFlag = SHORT.of(0);
        if(result._2()){
            carryFlag = SHORT.of(1);
        }

        nextState.setRegisters(state.getRegisters()
                .update(0xF,carryFlag)
                .update(registerAddresses.get(0), result._1()));
        return nextState;
    }
    private State SHRREG(State state){
        State nextState = state.clone();

        SHORT value = state.getRegisters().get(state.extractRegisterIndexes().get(0));

        nextState.setRegisters(state.getRegisters()
                .update(0xF,value.AND(SHORT.of(0x0001)))
                .update(state.extractRegisterIndexes().get(0), SHORT.of(value.toInt()>>>1) ));

        return nextState;
    }
    private State SUBNREG(State state){
        State nextState = state.clone();
        List<Integer> registerAddresses = state.extractRegisterIndexes().reverse();

        Tuple2<SHORT,Boolean> result = state.getRegisters().get(registerAddresses.get(0)).SUB(state.getRegisters().get(registerAddresses.get(1)));

        SHORT carryFlag = SHORT.of(0);
        if(result._2()){
            carryFlag = SHORT.of(1);
        }

        nextState.setRegisters(state.getRegisters()
                .update(0xF,carryFlag)
                .update(registerAddresses.get(0), result._1()));
        return nextState;
    }
    private State SHLREG(State state){
        State nextState = state.clone();

        SHORT value = state.getRegisters().get(state.extractRegisterIndexes().get(0));

        nextState.setRegisters(state.getRegisters()
                .update(0xF,value.AND(SHORT.of(0x8000)))
                .update(state.extractRegisterIndexes().get(0), SHORT.of(value.toInt()<<1) ));

        return nextState;
    }
    private State SNEREG(State state){
        List<SHORT> values = state.extractRegisterValues(state.extractRegisterIndexes());
        if(!values.get(0).matches(values.get(1))){
            return state.clone().incrProgramCounter();
        }
        return state.clone();
    }

    private State LDIADR(State state){
        SHORT addr = state.extractLSShort();
        State nextState = state.clone();
        nextState.setIndex(addr);
        return nextState;
    }

    private State JMPV0ADR(State state){
        State nextState = state.clone();
        nextState.setProgramCounter(state.extractLSShort().ADD(state.getRegisters().get(0))._1());
        return nextState;
    }

    private State RNDVXAND(State state){
        State nextState = state.clone();
        nextState.setRegisters(state.getRegisters().update(state.extractRegisterIndexes().get(0), new SHORT(BYTE.of(0),state.extractLSByte()).AND(SHORT.of(new Random().nextInt()))));
        return nextState;
    }

    private State LDVXDT(State state){
        State nextState = state.clone();
        nextState.setRegisters(state.getRegisters().update(state.extractRegisterIndexes().get(0), new SHORT(BYTE.of(0),state.getDelay())));
        return nextState;
    }
    private State LDDTVX(State state){
        State nextState = state.clone();
        nextState.setDelay(state.getRegisters().get(state.extractRegisterIndexes().get(0)).getLower());
        return nextState;
    }
    private State LDSTVX(State state){
        State nextState = state.clone();
        nextState.setSound(state.getRegisters().get(state.extractRegisterIndexes().get(0)).getLower());
        return nextState;
    }

    private State ADDIVX(State state) {
        State nextState = state.clone();
        nextState.setIndex(state.getIndex().ADD(state.extractRegisterValues(state.extractRegisterIndexes()).get(0))._1());
        return nextState;
    }

    private State LDBVX(State state){
        State nextState = state.clone();
        int index = state.getIndex().toInt();
        int value = state.getRegisters().get(state.extractRegisterIndexes().get(0)).toInt();
        int H = value/100;
        int T = (value / 10) - (H * 10);
        int D = value - (T * 10) - (H * 100);
        nextState.setMemory(state.getMemory().update(index,BYTE.of(H)).update(index+1,BYTE.of(T)).update(index+2,BYTE.of(D)));
        return nextState;
    }

    private State LDIVX(State state) {
        State nextState = state.clone();
        int lastRegIndex = state.extractRegisterIndexes().get(0);
        List<SHORT> regValues = List.range(0,lastRegIndex+1).map(i -> state.getRegisters().get(i));
        List<BYTE> mem = state.getMemory().slice(0,state.getIndex().toInt());
        mem = mem.appendAll(regValues.flatMap(s -> List.of(s.getUpper(),s.getLower())));
        mem = mem.appendAll(state.getMemory().slice(mem.length(),state.getMemory().length()-1));
        nextState.setMemory(mem);
        return nextState;
    }
    private State LDVXI(State state) {
        State nextState = state.clone();

        return nextState;
    }

    public Processor(){
        instructionSet = List.of(
                new Instruction("CLS",0x00E0,0xFFFF, this::CLR),
                new Instruction("RET",0x00EE,0xFFFF, this::RET),
                new Instruction("SYS addr",0x0000,0xF000, null),//Not used anymore
                new Instruction("JP addr",0x1000,0xF000, this::JP),
                new Instruction("CALL addr",0x2000,0xF000, this::CALL),
                new Instruction("SE Vx byte",0x3000,0xF000, this::SKIPEQUAL),
                new Instruction("SNE Vx byte",0x4000,0xF000, this::SKIPNOTEQUAL),
                new Instruction("SE Vx Vy",0x5000,0xF000, this::SKIPEQUALREG),
                new Instruction("LD Vx byte",0x6000,0xF000, this::LD),
                new Instruction("ADD Vx byte",0x7000,0xF000, this::ADD),
                new Instruction("LD Vx Vy",0x8000,0xF00F, this::LDREG),
                new Instruction("OR Vx Vy",0x8001,0xF00F, this::ORREG),
                new Instruction("AND Vx Vy",0x8002,0xF00F, this::ANDREG),
                new Instruction("XOR Vx Vy",0x8003,0xF00F, this::XORREG),
                new Instruction("ADD Vx Vy",0x8004,0xF00F, this::ADDREG),
                new Instruction("SYB Vx Vy",0x8005,0xF00F, this::SUBREG),
                new Instruction("SHR Vx Vy",0x8006,0xF00F, this::SHRREG),
                new Instruction("SUBN Vx Vy",0x8007,0xF00F, this::SUBNREG),
                new Instruction("SHL Vx Vy",0x800E,0xF00F, this::SHLREG),
                new Instruction("SNE Vx Vy",0x9000,0xF00F, this::SNEREG),
                new Instruction("LD I addr",0xA000,0xF000, this::LDIADR),
                new Instruction("JP V0 addr",0xB000,0xF000, this::JMPV0ADR),
                new Instruction("RND Vx byte",0xC000,0xF000, this::RNDVXAND),
                new Instruction("DRW Vx Vy nibble",0xD000,0xF000, null),//TODO: Implement screen
                new Instruction("SKP Vx",0xE09E,0xF0FF, null),//TODO: Implement keyboard
                new Instruction("SKNP Vx",0xE0A1,0xF0FF, null),//TODO: Implement keyboard
                new Instruction("LD Vx DT",0xF007,0xF0FF, this::LDVXDT),
                new Instruction("LD Vx k",0xF00A,0xF0FF, null),//TODO: Implement keyboard
                new Instruction("LD DT Vx",0xF015,0xF0FF, this::LDDTVX),
                new Instruction("LD ST Vx",0xF018,0xF0FF, this::LDSTVX),
                new Instruction("ADD I Vx",0xF01E,0xF0FF, this::ADDIVX),
                new Instruction("LD F Vx",0xF029,0xF0FF, null),//TODO: Implement keyboard
                new Instruction("LD B Vx",0xF033,0xF0FF, this::LDBVX),
                new Instruction("LD [I] Vx",0xF055,0xF0FF, this::LDIVX),
                new Instruction("LD Vx [I]",0xF065,0xF0FF, this::LDVXI)
        );
    }

    State process(State state){
        SHORT inst = state.getInst();
        Option<Instruction> ins = instructionSet.find(i -> i.checkOpcode(inst));
        return ins.get().execute(state).incrProgramCounter();
    }

}
