import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class DataManager {

	private String modelName;
	private String filePath;
	private String saveDirectory;
	
	public DataManager(String modelName, String filePath, String saveDirectory) {
		this.modelName = modelName;
		this.filePath = filePath;
		this.saveDirectory = saveDirectory;
	}
	
	public boolean generateFile()  {
		
		File saveFile = new File(saveDirectory + modelName + "_Mapping.fst");
		System.out.println(saveDirectory + modelName + "_Mapping.fst");
		PrintWriter out = null;
		try {
			out = new PrintWriter(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.write("$ more " + modelName + ".fst" + "\n");
		out.write("# faceshift target mapping file" + "\n");
		out.write("name=" + modelName + "\n");
		out.write("filename=" + filePath + "\n");
		out.write("texdir=" + filePath.substring(0, filePath.indexOf("/" + modelName)) + "\n");
		
		// read from template, copy to new file
		Scanner in;
		in = new Scanner(this.getClass().getResourceAsStream("template.fst"));
		
		while(in.hasNextLine()) {
			out.write(in.nextLine() + "\n");
		}
		
		in.close();
		out.close();
		return true;
	}
	
}
