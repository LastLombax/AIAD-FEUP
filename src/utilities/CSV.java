package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSV {
	PrintWriter pw;
	StringBuilder sb;
	File f;

	String filePath;
	public CSV(String name) throws FileNotFoundException {
		filePath = System.getProperty("user.dir") + "\\data\\" + name + ".csv";
		f = new File(filePath);
		pw = new PrintWriter(f);
		sb = new StringBuilder();
		String ColumnNamesList = "Turno,ÈChanceller,TimesChanceller,FascistRatio,CartaEscolhida,Team";
		sb.append(ColumnNamesList + "\n");
		pw.write(sb.toString());

	}

	public void write(String information) {
		sb = new StringBuilder();
		sb.append(information);
		sb.append("\n");
		pw.write(sb.toString());
	}

	public void closeFile() {
		pw.close();
	}

}
