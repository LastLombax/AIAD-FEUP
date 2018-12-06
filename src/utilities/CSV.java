package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSV {
	FileWriter pw;
	StringBuilder sb;
	File f;

	String filePath;
	public CSV(String name) throws FileNotFoundException {
		filePath = System.getProperty("user.dir") + "\\data\\" + name + ".csv";
		f = new File(filePath);
		try {
			sb = new StringBuilder();
			if(!f.exists()) {
				String ColumnNamesList = "Turno,�Chanceller,TimesChanceller,FascistRatio,CartaEscolhida,Team";
				sb.append(ColumnNamesList + "\n");
			}
			pw = new FileWriter(f,true);
			pw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void write(String information) {
		sb = new StringBuilder();
		sb.append(information);
		sb.append("\n");
		try {
			pw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeFile() {
		try {
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
