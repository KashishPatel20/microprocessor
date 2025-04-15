public class TestConverter {
    public static void fromInt(int value, Word32 result) {

        for (int i = 0; i<32; i++){
            result.bits[i].assign(Bit.boolValues.FALSE);
        }
        int newvalue = value;
        if (value < 0   ) {
            value = (value * -1) - 1;
        }
        int i = 0;
        while (value != 0) {

        int bitvalue = value % 2;

        if (bitvalue == 1) {
            result.bits[31 - i].assign(Bit.boolValues.TRUE);
        } else {
            result.bits[31 - i].assign(Bit.boolValues.FALSE);
        }
        value = value / 2;
        i++;
    }
        if(newvalue < 0){
            result.not(result);
        }
    }

    public static int toInt(Word32 value) {


        int result = 0;
        boolean isneagtive = (value.bits[0].getValue() == Bit.boolValues.TRUE);
        if (isneagtive) {
            for (int i = 0; i < 32; i++) {
                if (value.bits[i].getValue() == Bit.boolValues.FALSE) {
                    result += (int) Math.pow(2,31-i);
                }
            }
            result = -(result + 1);
        } else {
            for (int i = 0; i < 32; i++) {
                if (value.bits[i].getValue() == Bit.boolValues.TRUE) {
                    result += (int) Math.pow(2,31-i);
                }
            }
        }
        return result;
    }
//    public static void main(String args[]){
//        int num = -65;
//        Word32 value = new Word32();
//        fromInt(num,value);
//        System.out.println(value);
//
//        System.out.println(toInt(value));
//
//    }
}