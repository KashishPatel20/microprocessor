public class ALU {
    public Word16 instruction = new Word16();  // 16-bit instruction register
    public Word32 op1 = new Word32();           // 32-bit operand 1
    public Word32 op2 = new Word32();           // 32-bit operand 2
    public Word32 result = new Word32();        // 32-bit result register
    public Bit less = new Bit(false);           // flag for less than condition
    public Bit equal = new Bit(false);          // flag for equality condition


    // Perform the operation based on the op
    // code
    public void doInstruction() {
        result.setBitN(0, new Bit(false));  // Clear the result register
        // Extract the 5-bit opcode from the instruction register
        Bit[] opcode = new Bit[5];
        for (int i = 0; i < 5; i++) {
            opcode[i] = new Bit(false);
            instruction.getBitN(i, opcode[i]);
        }


        // Check the opcode pattern and perform the corresponding operation
        // Adder (opcode = 1)
        if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.FALSE && opcode[3].getValue() == Bit.boolValues.FALSE &&
                opcode[4].getValue() == Bit.boolValues.TRUE) {
            Adder.add(op1, op2, result);
        }
        // AND (opcode = 2)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.FALSE && opcode[3].getValue() == Bit.boolValues.TRUE &&
                opcode[4].getValue() == Bit.boolValues.FALSE) {
            Word32.and(op1, op2, result);
        }
        // Multiply (opcode = 3)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.FALSE && opcode[3].getValue() == Bit.boolValues.TRUE &&
                opcode[4].getValue() == Bit.boolValues.TRUE) {
            Multiplier.multiply(op1, op2, result);
        }
        // Left Shift (opcode = 4)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.TRUE && opcode[3].getValue() == Bit.boolValues.FALSE &&
                opcode[4].getValue() == Bit.boolValues.FALSE) {

            int shiftAmount = 0;
            for (int i = 0; i < 32; i++) {
                Bit bit = new Bit(false);
                op2.getBitN(31-i, bit);
                if (bit.getValue() == Bit.boolValues.TRUE) {
                shiftAmount += (int) Math.pow(2, i);
                }
            }
            Shifter.LeftShift(op1, shiftAmount, result);
        }
        // Subtract (opcode = 5)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.TRUE && opcode[3].getValue() == Bit.boolValues.FALSE &&
                opcode[4].getValue() == Bit.boolValues.TRUE) {
            Adder.subtract(op1, op2, result);
        }
        // OR (opcode = 6)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.TRUE && opcode[3].getValue() == Bit.boolValues.TRUE &&
                opcode[4].getValue() == Bit.boolValues.FALSE) {
            Word32.or(op1, op2, result);
        }
        // Right Shift (opcode = 7)
        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.FALSE &&
                opcode[2].getValue() == Bit.boolValues.TRUE && opcode[3].getValue() == Bit.boolValues.TRUE &&
                opcode[4].getValue() == Bit.boolValues.TRUE) {

            int shiftAmount = 0;
            for (int i = 0; i < 32; i++) {
                Bit bit = new Bit(false);
                op2.getBitN(31-i, bit);
                if (bit.getValue() == Bit.boolValues.TRUE) {
                    shiftAmount += (int) Math.pow(2, i);
                }
            }
            Shifter.RightShift(op1, shiftAmount, result);
        }

        // Compare (opcode = 11)

        else if (opcode[0].getValue() == Bit.boolValues.FALSE && opcode[1].getValue() == Bit.boolValues.TRUE &&
                opcode[2].getValue() == Bit.boolValues.FALSE && opcode[3].getValue() == Bit.boolValues.TRUE &&
                opcode[4].getValue() == Bit.boolValues.TRUE) {

            equal.assign(Bit.boolValues.TRUE);
            less.assign(Bit.boolValues.FALSE);

            Bit b = new Bit(false);
            instruction.getBitN(11, b);
            if (b.getValue() == Bit.boolValues.FALSE) {
                for (int i = 0; i < 32; i++) {
                    Bit check1 = new Bit(false);
                    Bit check2 = new Bit(false);
                    op1.getBitN(i, check1);
                    op2.getBitN(i, check2);

                    if (check1.getValue() != check2.getValue()) {
                        if (check1.getValue() == Bit.boolValues.TRUE) {
                            equal.assign(Bit.boolValues.FALSE);
                            less.assign(Bit.boolValues.FALSE);
                        } else {
                            equal.assign(Bit.boolValues.FALSE);
                            less.assign(Bit.boolValues.TRUE);
                        }
                        return;
                    }
                }
                    equal.assign(Bit.boolValues.TRUE);
                    less.assign(Bit.boolValues.FALSE);
                }
        }
    }
}
