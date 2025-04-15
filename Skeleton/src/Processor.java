import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {
    private Memory mem;
    public List<String> output = new LinkedList<>();
    private Stack<Word32> stack = new Stack<>();
    private Word32[] registers = new Word32[40];
    private boolean halt = false;
    private int progcounter = 0;
    private Word16 currentinstrcut = new Word16();
    private Word16 pendinginstrcut = null;
    Word16 tophalf = new Word16();
    Word16 bottomhalf = new Word16();
    int format;
    int dr = 0;
    int sr=0;
    ALU alu = new ALU();


    Word32 opcode1 = new Word32();
    Word32 opcode2 = new Word32();


    public Processor(Memory m) {
        mem = m;
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32();
        }
    }

    public void run() {
        Word16 temp = new Word16();
        mem.read();
        mem.value.getTopHalf(tophalf);
        tophalf.copy(currentinstrcut);
        while(!currentinstrcut.equals(temp) && !halt) {
            fetch();
            decode();
            execute();
            store();
        }
        printReg();
    }
    // reads the instruction from memory(top and bottom half)
    private void fetch() {

        //takes the leftover 16bits from the last instruction
        if(pendinginstrcut!= null){
            currentinstrcut = pendinginstrcut;
            pendinginstrcut = null;
        }
        else {
            Word32 temp = new Word32();
            Word32 currentpc = new Word32(); // current program counter used to know when to fetch and when to jump
            fromInt(progcounter, currentpc);


            //set the address to read from memory
            Adder.add(temp, currentpc, mem.address);
            mem.read();
            mem.value.getTopHalf(tophalf); // get the top half of the instruction from memory
            mem.value.getBottomHalf(bottomhalf);

            //copy the top and bottom half to the current instruction
            tophalf.copy(currentinstrcut);
            pendinginstrcut = new Word16();
            bottomhalf.copy(pendinginstrcut);


            Word16 temp2 = new Word16();
            if (currentinstrcut.equals(temp2)) {
                halt = true;
            }
            progcounter++;

        }
    }

    private void decode() {

        // Decode the instruction
        String currstr = BitString(currentinstrcut, 0, 15);
        String opcode = currstr.substring(0, 5);

        //for copy 10 r9
        //copy source into destination
        if (opcode.equals("tftff")) {
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);
            opcode2 = Word16SpLit(currentinstrcut, 11, 15);
            sr = toInt(opcode1);
            dr = toInt(opcode2);
//            registers[dr].copy(registers[sr]);
        }

        // checking for call, return, syscall, BLE, BLT,BGE, BGT, BEQ, BNE
        else if (opcode.equals("ftfft") || opcode.equals("ftftf") || opcode.equals("fttff") || opcode.equals("fttft")
                || opcode.equals("ftttf") || opcode.equals("ftttt") || opcode.equals("tffff") || opcode.equals("tffft") || opcode.equals("ftfff")) {
            //check for format (register)
            format = 3;
        }
        //store
        else if (opcode.equals("tfftt")) {
            format = 19;
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);
            opcode2 = Word16SpLit(currentinstrcut, 11, 15);
        }
        //load
        else if (opcode.equals("tfftf")) {
            format = 18;
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);
            opcode2 = Word16SpLit(currentinstrcut, 11, 15);
           // sr = toInt(opcode1);
           // dr = toInt(opcode2);
        }
        else if (getBitAt(currentinstrcut, 5) == Bit.boolValues.TRUE) {

            format = 2;
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);
            opcode2 = Word16SpLit(currentinstrcut, 11, 15);

            dr = toInt(opcode2);
            currentinstrcut.copy(alu.instruction);
            opcode1.copy(alu.op2);
            registers[dr].copy(alu.op1);
        }
        //check for format (immediate)
        else if (opcode.equals("fffft")) {

            format = 2;
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);

            opcode2 = Word16SpLit(currentinstrcut, 11, 15);

            dr = toInt(opcode2);
            currentinstrcut.copy(alu.instruction);
            opcode1.copy(alu.op1);
            registers[dr].copy(alu.op2);


        }
        //immediate

        //check for the format(2R)
        else if (getBitAt(currentinstrcut, 5) == Bit.boolValues.FALSE) {
            if (format != 1) {
                return;
            }
            format = 1;
            opcode1 = Word16SpLit(currentinstrcut, 6, 10);
            opcode2 = Word16SpLit(currentinstrcut, 11, 15);


            alu.instruction.copy(currentinstrcut);
            currentinstrcut.copy(alu.instruction);
            sr = toInt(opcode1);
            dr = toInt(opcode2);

            alu.op1.copy(registers[sr]);
            alu.op2.copy(registers[dr]);
        }
    }

    //gets the immediate and registers
    private String BitString(Word16 word, int start, int end) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i <= end; i++) {
            if (word.bits[i].getValue() == Bit.boolValues.TRUE) {
                result.append("t");
            } else {
                result.append("f");
            }
        }
        return result.toString();
    }
    public Bit.boolValues getBitAt(Word16 word, int index) {
        return word.bits[index].getValue();
    }
    // Method to convert bits to Word16 literals (to handle operands)
    private Word32 Word16SpLit(Word16 currentinstrcut, int start, int end) {
        Word32 bits = new Word32();
        int c = 31;
        for (int i = end; i >= start; i--) {
            bits.setBitN(c, currentinstrcut.bits[i]);
            c--;
        }
        return bits;
    }
    private void execute() {
        String opcode = BitString(currentinstrcut, 0, 4);


        //2R format
        if (format == 1) {
            alu.doInstruction();
            alu.result.copy(registers[sr]);
            alu.result.copy(registers[dr]);
        }
        if (format == 2) { //immediate
            alu.doInstruction();
            alu.result.copy(registers[dr]);

        }
        //copy
        if (opcode.equals("tftff")) {
            if (getBitAt(currentinstrcut, 5) == Bit.boolValues.FALSE) {
                registers[sr].copy(registers[dr]);
            } else {
                opcode1.copy(registers[dr]);

            }
        }
        if (format == 3) {

            alu.doInstruction();

            //syscall (8)
            if (opcode.equals("ftfff")) {
                format = 8;
            }
            //call (9)
            else if (opcode.equals("ftfft")) {
                Word32 temp = new Word32();
                fromInt(progcounter, temp);
                stack.push(temp);

                //jump to new location in register[sr]
                progcounter = toInt(registers[sr]);
            }
            //return (10)
            else if (opcode.equals("ftftf")) {
                if (!stack.isEmpty()) {
                    progcounter = toInt(stack.pop());
                } else {
                    halt = true; // Nothing to return to
                }
            }
            //compare (11)
            else if (opcode.equals("ftftt")) {
                alu.doInstruction();
            }
            //conditional branch
            else if (opcode.equals("fttff")) { // BLE - 12 (<=)

                if (toInt(registers[sr]) <= toInt(registers[dr])) {
                    progcounter = progcounter + 1; // or set to a specific target
                }
            } else if (opcode.equals("fttft")) { // BLT - 13 (<)
                if (toInt(registers[sr]) < toInt(registers[dr])) {
                    progcounter = progcounter + 1;
                }
            } else if (opcode.equals("ftttf")) { // BGE - 14 (>=)
                if (toInt(registers[sr]) >= toInt(registers[dr])) {
                    progcounter = progcounter + 1;
                }
            } else if (opcode.equals("ftttt")) { // BGT - 15 (>)
                if (toInt(registers[sr]) > toInt(registers[dr])) {
                    progcounter = progcounter + 1;
                }
            } else if (opcode.equals("tffff")) { // BEQ - 16 (==)
                if (toInt(registers[sr]) == toInt(registers[dr])) {
                    progcounter = progcounter + 1;
                }
            } else if (opcode.equals("tffft")) { // BNE - 17 (!=)
                if (alu.equal.getValue() != Bit.boolValues.TRUE) {
                    progcounter = progcounter + 1;
                    int immediateValue = toInt(opcode1);
                    progcounter += immediateValue;
                    progcounter--;
                }
            }
        }
        //load 18
        //Loads from memory address (immediate + destination) into destination register
        if (format == 18 && opcode.equals("tfftf")) {
            int srcReg = toInt(opcode1);
            int destReg = toInt(opcode2);
            int effectiveAddress = toInt(registers[srcReg]);

            Word32 address = new Word32();
            fromInt(effectiveAddress, address);
            address.copy(mem.address);

            mem.read();
            mem.value.copy(registers[destReg]);
        }

        //Store - 19 (Stores into memory address destination from source)
    }

    private void printReg() {
        System.out.println(registers.length);
        for (int i = 0; i < 32; i++) {
            var line = "r"+ i + ":" + registers[i].toString(); // TODO: add the register value here...
            output.add(line);
            System.out.println(line);
        }
    }

    private void printMem() {
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32();
            Word32 value = new Word32();
            // Convert i to Word32 here...
            fromInt(i, addr);
            // addr.copy(mem.address);
            mem.address.copy(addr);
            mem.read();
            //mem.value.copy(value);
            value.copy(mem.value);
            var line = i + ":" + value + "(" + TestConverter.toInt(value) + ")";
            output.add(line);
            System.out.println(line);
        }
    }

    private void store() {

        if(format == 1 || format == 2 || format ==5) {
            registers[dr].copy(alu.result);
        }
        //sysCall
        else if(format ==8){
            if(sr ==1){
                printMem();
            } else if (sr ==0) {
                printReg();
            }
        }
    }
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
}