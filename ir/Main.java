package ir;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		//System.getenv().entrySet().forEach(System.out::println);
		//System.out.println(System.getProperty("user.dir"));
		File file = new File(System.getProperty("user.dir"), "/src/data");
		//System.out.println(file.exists()+"  "+file.getAbsolutePath());
		String str = "and all other Cyprian friends whose hospitalities and kindness";
		IrSystem ir = new IrSystem(file);
		//System.out.println(ir.tokenize(str));
		ir.cosineSimilarity(str);
		//ir.getPostings("cyprian").entrySet().forEach(System.out::println);
	}

}