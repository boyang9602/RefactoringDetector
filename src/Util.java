import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	public static void write(String path, String content) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileWriter writer  = new FileWriter(f);
		writer.write(content);
		writer.flush();
		writer.close();
	}

}
