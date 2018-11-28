package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSV {
	PrintWriter pw;
	StringBuilder sb;
	File f;
	public CSV(String name) throws FileNotFoundException {
		String filePath = System.getProperty("user.dir") + "\\data\\" + name;
		f = new File(filePath + ".csv");
		pw = new PrintWriter(f);

		sb = new StringBuilder();
		String ColumnNamesList = "President,Cards2Chanceller,Chanceller,ChancellerCard,PresidentT,ChancellerT";
		sb.append(ColumnNamesList + "\n");
		pw.write(sb.toString());

	}

	public void write(String[] information) {
		sb = new StringBuilder();
		for (String s : information) {
			sb.append(s);
			sb.append(",");
		}
		sb.delete(sb.length()-1, sb.length()-1);
		sb.append("\n");
		pw.write(sb.toString());

	}
	
	public void updateTeam() {
		
		
	}


	public void closeFile() {
		pw.close();
	}

	public void saveDelegacy(String president, String chancellor) {
		sb = new StringBuilder();
		sb.append(president);
		sb.append(",");
		sb.append("");
		sb.append(",");
		sb.append(chancellor);
		pw.write(sb.toString());
	}
}
