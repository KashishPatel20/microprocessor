public class Word16 {

    Bit[] bits;
    public Word16() {
        bits = new Bit[16];
        for(int i =0; i<16 ; i++){
            bits[i]= new Bit(false);
        }
    }

    // create a word16 where each bit can be customized
    public Word16(Bit[] in) {
        this.bits = new Bit[16];

        for(int i =0; i<16 ; i++){
            bits[i] = in[i];
        }
    }

    public void copy(Word16 result) { // sets the values in "result" to be the same as the values in this instance; use "bit.assign"
        for (int i = 0; i<16; i++){
            result.bits[i].assign(this.bits[i].getValue());
        }
    }

    public void setBitN(int n, Bit source) { // sets the nth bit of this word to "source"

        bits[n].assign(source.getValue());
    }

    public void getBitN(int n, Bit result) { // sets result to be the same value as the nth bit of this word

        result.assign(bits[n].getValue());
    }

    public boolean equals(Word16 other) { // is other equal to this
        return equals(this,other);
    }

    public static boolean equals(Word16 a, Word16 b)
    {
        for(int i =0;i<16;i++){
            if(a.bits[i].getValue() != b.bits[i].getValue()){
                return false;
            }
        }
        return true;
    }

    public void and(Word16 other, Word16 result) {
        and(this,other,result);
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        for(int i =0;i<16;i++){
            Bit.and(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void or(Word16 other, Word16 result) {
        or(this, other,result);
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        for(int i =0;i<16;i++){
            Bit.or(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void xor(Word16 other, Word16 result) {
        xor(this,other,result);
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        for(int i =0;i<16;i++){
            Bit.xor(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void not( Word16 result) {
        not(this,result);
    }

    public static void not(Word16 a, Word16 result) {
        for(int i =0;i<16;i++){
            Bit.not(a.bits[i],result.bits[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Bit bit : bits) {
            sb.append(bit.toString());
            sb.append(",");
        }

        return sb.toString();
    }
}