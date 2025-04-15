public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {

        for(int i = 0;i<32;i++){
            result.bits[i].assign(Bit.boolValues.FALSE);
        }
        Word32 shiftA = new Word32();
        a.copy(shiftA);

        for(int i=31; i>=0;i--){
            if(b.bits[i].getValue() ==Bit.boolValues.TRUE){
                Adder.add(result,shiftA,result);
            }
        Shifter.LeftShift(shiftA,1,shiftA);
        }
    }
}
