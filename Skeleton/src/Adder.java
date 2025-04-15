public class Adder {
    public static void subtract(Word32 a, Word32 b, Word32 result) {

        b.not(b);

        Word32 one = new Word32();
        one.bits[31].assign(Bit.boolValues.TRUE);
        for(int i = 0;i< 31;i++){
            one.bits[i].assign(Bit.boolValues.FALSE);
        }

        Word32 two = new Word32();
        add(b,one,two);
        add (a,two,result);
    }

    public static void add(Word32 a, Word32 b, Word32 result) {

        Bit carryin = new Bit(false);

        // Sum = ((X xor Y) xor Cin)
        // Cout = (X and Y) or ((X xor Y) and Cin)
        for(int i =0;i<32;i++){

            Bit carryout = new Bit(false);
            Bit xorXY = new Bit(false);
            Bit andXY = new Bit(false);
            Bit andxorCin = new Bit(false);
            Bit xorAB = new Bit(false);
            Bit sum = new Bit(false);

           Bit.xor(a.bits[31 -i],b.bits[31- i],xorXY);
           Bit.xor(xorXY,carryin,sum);

              Bit.and(a.bits[31- i],b.bits[31-i],andXY);
              Bit.xor (a.bits[31-i],b.bits[31-i],xorAB);
              Bit.and(xorAB,carryin,andxorCin);
              Bit.or(andXY,andxorCin,carryout);

              carryin.assign(carryout.getValue());
              result.setBitN(31-i,sum);
        }
    }
}