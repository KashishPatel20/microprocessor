import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Assembler {

    public static String[] assemble(String[] input) {

        Map<String, String> opcodes = new HashMap<>();
        opcodes.put("halt", "fffff");
        opcodes.put("add", "fffft");
        opcodes.put("and", "ffftf");
        opcodes.put("multiply", "ffftt");
        opcodes.put("leftshift", "fftff");
        opcodes.put("subtract", "fftft");
        opcodes.put("or", "ffttf");
        opcodes.put("rightshift", "ffttt");
        opcodes.put("syscall", "ftfff");
        opcodes.put("call", "ftfft");
        opcodes.put("return", "ftftf");
        opcodes.put("compare", "ftftt");
        opcodes.put("ble", "fttff");
        opcodes.put("blt", "fttft");
        opcodes.put("bge", "ftttf");
        opcodes.put("bgt", "ftttt");
        opcodes.put("beq", "tffff");
        opcodes.put("bne", "tffft");
        opcodes.put("load", "tfftf");
        opcodes.put("store", "tfftt");
        opcodes.put("copy", "tftff");

        LinkedList<String> output = new LinkedList<>();
        // Iterate through each line of input (eg- input = add r1 r2)
        for (String line : input) {
            String[] parts = line.split(" ");
            String opcode = parts[0];
            String instruction = opcodes.get(opcode);

            StringBuilder binaryInstruction = new StringBuilder(instruction);

            // Process the opcodes
            if (parts.length == 1) {
                binaryInstruction.append("fffffffffff");  // 11 'f' bits for the single operand case
            } else if (parts.length == 2) {
                // Handling immediate value
                if (parts[1].matches("-?\\d+")) {
                    int immediate = Integer.parseInt(parts[1]);

                    // For negative numbers, we calculate two's complement representation manually
                    String immediateBinary;
                    if (immediate < 0) {
                        // Two's complement for negative numbers (11-bit)
                        immediateBinary = String.format("%11s", Integer.toBinaryString((1 << 11) + immediate)).replace(' ', '0');
                    } else {
                        // For positive numbers, just convert to binary and pad
                        immediateBinary = String.format("%11s", Integer.toBinaryString(immediate)).replace(' ', '0');
                    }
                    // Convert binary to 'f' and 't' format
                    String immediateInTf = immediateBinary.replace('0', 'f').replace('1', 't');
                    binaryInstruction.append(immediateInTf);
                }
            }
            // handles 2R and immediate
            else {
                // Two registers
                if (isRegister(parts[1]) && isRegister(parts[2])) {
                    binaryInstruction.append("f");
                    binaryInstruction.append(formatBinary(parts[1])); // Convert r1 to 5-bit
                    binaryInstruction.append(formatBinary(parts[2])); // Convert r2 to 5-bit

                    //For immediate value and register
                } else if (isImmediate(parts[1]) && isRegister(parts[2])) {
                    binaryInstruction.append("t");
                    binaryInstruction.append(formatBinary(parts[1])); // Convert immediate (1) to 5-bit
                    binaryInstruction.append(formatBinary(parts[2])); // Convert r2 to 5-bit
                }
            }

            output.add(binaryInstruction.toString());
        }
        return output.toArray(new String[0]);
    }

    private static boolean isRegister(String operand) {
        //check the digits (0-9) and the letter 'r'
        return operand.matches("r\\d+");
    }

    private static String formatBinary(String register) {
        int num = Integer.parseInt(register.replace("r", "")); // Remove 'r' if register

        // Handle negative numbers with two's complement
        String binary;
        if (num < 0) {
            // Two's complement for negative numbers: add 2^5 (32) to make it positive and then convert to binary
            binary = String.format("%5s", Integer.toBinaryString((1 << 5) + num)).replace(' ', '0');
        } else {
            // Convert positive number to binary and pad with leading zeros
            binary = String.format("%5s", Integer.toBinaryString(num)).replace(' ', '0');
        }

        return binary.replace('0', 'f').replace('1', 't'); // Convert 0 -> 'f' and 1 -> 't'
    }

    //for negative (or -6 r9)
    private static boolean isImmediate(String operand) {
        return operand.matches("-?\\d+");
    }

    public static String[] finalOutput(String[] input) {
        LinkedList<String> combinedOutput = new LinkedList<>();

        // Iterate through the input array in pairs
        for (int i = 0; i < input.length; i += 2) {
            // If the next line exists, merge the current and next line
            if (i + 1 < input.length) {
                // Combine the two 16-bit instructions into a single 32-bit string
                combinedOutput.add(input[i] + input[i + 1]);
            } else {
                // If the next line doesn't exist (odd number of instructions)
                combinedOutput.add(input[i] + "ffffffffffffffff");
            }
        }
        // Convert the LinkedList to an array and return it
        return combinedOutput.toArray(new String[0]);
    }
}
