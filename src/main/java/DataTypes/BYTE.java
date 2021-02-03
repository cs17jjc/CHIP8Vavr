package DataTypes;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

public class BYTE {

    List<Boolean> bits;

    public BYTE(List<Boolean> bits){
        this.bits = bits;
    }

    public static BYTE of(int value){
        return new BYTE(List.range(0,8).map(i -> {
            int mask = 0x80 >>> i;
            return (value & mask) == mask;
        }));
    }

    public BYTE clone(){
        return new BYTE(List.ofAll(bits));
    }

    public Tuple2<BYTE,Boolean> ADD(BYTE b,boolean carry){
        List<Boolean> output = List.empty();
        for(int i = 7; i >= 0;i--){
            output = output.prepend((bits.get(i) ^ b.bits.get(i)) ^ carry);
            carry = (bits.get(i) & b.bits.get(i)) | ((bits.get(i) ^ b.bits.get(i)) & carry);
        }

        return Tuple.of(new BYTE(output),carry);
    }

    public Tuple2<BYTE,Boolean> SUB(BYTE b,boolean borrow){
        return ADD(b.invert(),borrow);
    }

    public BYTE invert(){
        return new BYTE(bits.map(b -> !b));
    }

    public BYTE AND(BYTE b){
        return new BYTE(bits.zip(b.bits).map(bb -> bb._1() & bb._2()));
    }
    public BYTE OR(BYTE b){
        return new BYTE(bits.zip(b.bits).map(bb -> bb._1() | bb._2()));
    }
    public BYTE XOR(BYTE b){
        return new BYTE(bits.zip(b.bits).map(bb -> bb._1() ^ bb._2()));
    }

    public boolean matches(BYTE b){
        return bits.zip(b.bits).map(t -> t._1() == t._2()).reduce((x,y) -> x & y);
    }

    public BYTE getNibble(boolean upper){
        if(upper){
            return new BYTE(bits.slice(0,4).leftPadTo(8,false));
        }
        return new BYTE(bits.slice(4,8).leftPadTo(8,false));
    }

    public int toInt(){
        return List.range(0,8).reverse().map(i -> Math.pow(2,i)).zip(bits).map(t -> t._2() ? t._1() : 0).sum().intValue();
    }

}
