import java.util.List;

public class Module {

    public List<String> DefinitionList;
    public List<String> UseList;
    public List<String> ProgramText;

    public void printModule() {
    	for (String str : DefinitionList) {
    		System.out.print(str + " ");
    	}
    	System.out.println();
    	for (String str2 : UseList) {
    		System.out.print(str2 + " ");
    	}
    	System.out.println();
    	for (String str3 : ProgramText) {
    		System.out.print(str3 + " ");
    	}
    	System.out.println();
        //System.out.println("done");
    }

}
