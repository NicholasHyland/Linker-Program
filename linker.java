import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;

public class linker {

        public static List<Symbol> firstPass(List<Module> modules) {

            int base = 0;
            List<Symbol> symbols = new ArrayList<Symbol>();

            for (Module mod : modules) {

                // figure out symbols
                int startIndex = 1;
                int endIndex = (2 * Integer.parseInt(mod.DefinitionList.get(0))) + 1;

                for (; startIndex < endIndex; startIndex += 2) {
                    if (Integer.parseInt(mod.DefinitionList.get(0)) == 0) {
                        continue;
                    }
                    Symbol pair = new Symbol();
                    pair.symbol = mod.DefinitionList.get(startIndex);
                    if (mutiplyDefined(pair.symbol, symbols)) {
                        System.out.println("Error: " + pair.symbol + " has already been defined!");
                        System.out.println();
                        continue;
                    }
                    pair.address = Integer.parseInt(mod.DefinitionList.get(startIndex + 1)) + base;
                    if ((2 * ((pair.address - base) + 1) + 1) > mod.ProgramText.size()) {
                        System.out.println("Error: The value of " + pair.symbol + " is outside the module; zero (relative) used");
                        pair.address = 0;
                    }
                    symbols.add(pair);
                }
                base += Integer.parseInt(mod.ProgramText.get(0));
                }

            return symbols;
       }
    
        public static void checkE(Module mod, int modNum) {

            int size = (2 * Integer.parseInt(mod.UseList.get(0))) + 1;
            if (size == 1) {
                return;
            }

            List<Integer> usedIndex = new ArrayList<Integer>();

            int startIndex = 2;
            int next = 0;
            int value;
            for (; startIndex < size; startIndex += 2) {
                next = Integer.parseInt(mod.UseList.get(startIndex));
                value = 0;
                while (value != 777) {
                    if ((2 * (next + 1)) > mod.ProgramText.size()){
                        System.out.println("Error: Pointer in use chain exceeds module size in mod " + modNum + "; chain terminated.");
                        break;
                    }
                    String key = mod.ProgramText.get((2 * next) + 1);
                    if (!key.equals("E")) {
                        System.out.println("Error: " + key + " type address use on chain in mod " + modNum + "; treated as E type.");
                    }
                    usedIndex.add((2 * next) + 1);
                    value = (Integer.parseInt(mod.ProgramText.get(2 * (next + 1)))) % 1000;
                    next = value;
                }
            }
            boolean used;
            for (int x = 1; x < mod.ProgramText.size(); x += 2) {
                used = false;
                if (mod.ProgramText.get(x).equals("E")) {
                    for (int pos : usedIndex) {
                        if (pos == x) {
                            used = true;
                        }
                    }
                    if (used == false) {
                        System.out.println("Error: E type address not on use chain in mod " + modNum + "; treated as I type.");
                    }
                }
                else {
                    continue;
                }
            }
        }

        public static void secondPass(List<Module> modules, List<Symbol> symbols) {

            System.out.println("Memory Map: ");
            System.out.println();

            int base = 0;
            int add;
            int value;
            int modNum = 0;

            for (Module mod : modules) {
                System.out.println("Module " + modNum);
                checkE(mod, modNum);

                add = 0;
                value = 0;
                int startIndex = 1;
                int endIndex = (2 * Integer.parseInt(mod.ProgramText.get(0))) + 1;

                for (; startIndex < endIndex; startIndex += 2) {
                    if (mod.ProgramText.get(startIndex).equals("I") || mod.ProgramText.get(startIndex).equals("A")) {
                        System.out.println(mod.ProgramText.get(startIndex + 1));
                    }
                    else if (mod.ProgramText.get(startIndex).equals("R")) {
                        System.out.println(Integer.parseInt(mod.ProgramText.get(startIndex + 1)) + base);
                    }
                    else if (mod.ProgramText.get(startIndex).equals("E")) {
                        add = Integer.parseInt(mod.ProgramText.get(startIndex + 1)) / 1000;
                        add *= 1000;

                        if (contains(mod.UseList.get(1), symbols) == false) {
                            System.out.println(add + " Error: " + mod.UseList.get(1) + " is not defined; zero used.");
                        }
                        else {
                            for (Symbol sym : symbols) {
                                if (mod.UseList.get(1).equals(sym.symbol)) {
                                    sym.isUsed();
                                    value = sym.address;
                                    add += value;
                                    System.out.println(add);
                                    break;
                                }
                            }
                        }
                    }
                }

                base += Integer.parseInt(mod.ProgramText.get(0));
                modNum++;
            }
            System.out.println();
            printUsed(symbols);
        }

    public static boolean contains(String symbolString, List<Symbol> symbols) {
        for (Symbol sym : symbols) {
            if (sym.symbol.equals(symbolString)) {
                return true;
            }
        }
        return false;
    }

    public static void printUsed(List<Symbol> symbols) {
        for (Symbol sym : symbols) {
            if (sym.used == false) {
                System.out.println("Warning: " + sym.symbol + " was defined but never used.");
            }
        }
    }

    public static void printSymbols(List<Symbol> symbols){

        for (Symbol sym : symbols) {
            System.out.println(sym.symbol + " = " + sym.address);
        }
    }
    
    public static boolean mutiplyDefined(String symbolString, List<Symbol> symbols) {

        for (Symbol sym : symbols) {
            if (symbolString.equals(sym.symbol)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws error {

        Scanner textScan = new Scanner(System.in);

        String line = "";

        List<Module> modules = new ArrayList<Module>();

        while (textScan.hasNextLine()) {
            line += textScan.nextLine() + " ";
        }

        List<String> elements = Arrays.asList(line.split("\\s+"));      

        int startIndex = 0;
        int endIndex = 0;

        while (startIndex != elements.size()) {
            
            Module mod = new Module();

            for (int i = 0; i < 3; i++) {

                endIndex = startIndex + (2 * Integer.parseInt(elements.get(startIndex))) + 1;
                if (i == 0) {
                    mod.DefinitionList = elements.subList(startIndex, endIndex);
                }
                else if (i == 1) {
                    mod.UseList = elements.subList(startIndex, endIndex);
                }
                else {
                    mod.ProgramText = elements.subList(startIndex, endIndex);
                    modules.add(mod);
                }

                startIndex = endIndex;
            }
        }

        System.out.println("Symbol Table: ");
        System.out.println();
        List<Symbol> symbols = firstPass(modules);

        printSymbols(symbols);
        System.out.println();

        secondPass(modules, symbols);
    }
}

