package DataTypes;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class SHORT {
    BYTE upper;
    BYTE lower;

    public SHORT(){
        this.upper = BYTE.of(0);
        this.lower = BYTE.of(0);
    }
    public SHORT(BYTE upper,BYTE lower){
        this.upper = upper;
        this.lower = lower;
    }

    public static SHORT of(int value){
        return new SHORT(BYTE.of(value >>> 8),BYTE.of(value));
    }

    public int toInt(){
        return (upper.toInt() << 8) + lower.toInt();
    }

    public SHORT AND(SHORT b){
        return new SHORT(upper.AND(b.upper),lower.AND(b.lower));
    }
    public SHORT OR(SHORT b){
        return new SHORT(upper.OR(b.upper),lower.OR(b.lower));
    }
    public SHORT XOR(SHORT b){
        return new SHORT(upper.XOR(b.upper),lower.XOR(b.lower));
    }
    public Tuple2<SHORT, Boolean> ADD(SHORT b){
        Tuple2<BYTE, Boolean> t = lower.ADD(b.lower, false);
        Tuple2<BYTE, Boolean> t2 = upper.ADD(b.upper, t._2());
        return Tuple.of(new SHORT(t2._1(),t._1()),t._2());
    }
    public Tuple2<SHORT, Boolean> SUB(SHORT b){
        Tuple2<BYTE, Boolean> t = lower.SUB(b.lower, true);
        Tuple2<BYTE, Boolean> t2 = upper.SUB(b.upper, t._2());
        return Tuple.of(new SHORT(t2._1(),t._1()),t2._2());
    }
    public BYTE getNibble(int i){
        switch (i){
            case 0:
                return upper.getNibble(true);
            case 1:
                return upper.getNibble(false);
            case 2:
                return lower.getNibble(true);
            case 3:
                return lower.getNibble(false);
            default:
                return null;
        }
    }
    public BYTE getUpper(){
        return upper.clone();
    }
    public BYTE getLower(){
        return lower.clone();
    }
    public boolean matches(SHORT b){
        return upper.matches(b.upper) & lower.matches(b.lower);
    }

}
