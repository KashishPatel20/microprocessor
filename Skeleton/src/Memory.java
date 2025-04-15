public class Memory {
    public Word32 address= new Word32();
    public Word32 value = new Word32();

    private Word32[] dram= new Word32[1000];

    // This method is used to read the value from the memory
    public Memory() {
        for (int i = 0; i < dram.length; i++) {
            dram[i] = new Word32();
        }
    }
    public int addressAsInt() {
        int ans = 0;
        for (int k = 0; k < 32; k++) {  // Addresses are unsigned, use 32 bits
            Bit bit = address.bits[31 - k];
            if (bit.getValue() == Bit.boolValues.TRUE) {
                ans += Math.pow(2,k); // Use bit-shifting for efficiency
            }
        }
        return ans ;
    }

    public void read() {
        this.value = dram[addressAsInt()];
    }
    // This method is used to write the value to the memory
    public void write() {
        Word32 val = new Word32();
        Bit bool = new Bit(false);
        for (int i = 31; i >=0; i--) {
             value.getBitN(i, bool);
             val.setBitN(i, bool);
        }
        dram[addressAsInt()] = val;
    }

    public void load(String[] data) {

        int dataSize = Math.min(data.length, 1000); // Ensure it doesn't exceed memory limit
        System.out.println("Data size: " + dataSize);
        for (int i = 0; i < dataSize; i++) {
        if (data[i] != null && data[i].length() != 32) continue;
            Bit[] bitsArray = new Bit[32] ;

            for (int j = 0; j < 32; j++) {
                    bitsArray[j] = new Bit(data[i].charAt(j) == 't');

            }
            dram[i] = new Word32(bitsArray) ;// Use assign() with a new Word32
        }
    }
}
