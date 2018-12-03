package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
		String ColumnNamesList = "President,Cards2Chanceller,Chanceller,ChancellerCard,Team";
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

	public void writeMembership(String content) throws IOException {
		sb = new StringBuilder();
		closeFile();
		String [] roles = new String[7];

		for (int i = 0; i < 3; i++) {
			String [] aux = content.split(";");
			String [] aux2 = aux[0].split(":");
			String [] aux3 = aux2[0].split("_");
			roles[Integer.parseInt(aux3[1])] = aux2[1];

			String [] aux4 = aux[1].split(":");
			String [] aux5 = aux4[0].split("_");
			roles[Integer.parseInt(aux5[1])] = aux4[1];
			content = content.replace(aux[0] + ";" + aux[1] + ";", "");
		}
		
		String [] aux = content.split(";");
		String [] aux2 = aux[0].split(":");
		String [] aux3 = aux2[0].split("_");
		roles[Integer.parseInt(aux3[1])] = aux2[1];

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			sb.append(line + "\n");
			while((line = reader.readLine()) != null) {
				int a = Integer.parseInt(line.substring(7, 8));
				line += roles[a] +  "\n";
				sb.append(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(new FileOutputStream(filePath, false));
		pw.write(sb.toString());
	}

	public void closeFile() {
		pw.close();
	}

	public void saveDelegacy(String president, String chancellor) {

		System.out.println("saved chancellor: " + chancellor);
		sb = new StringBuilder();
		sb.append(president);
		sb.append(",");
		sb.append("");
		sb.append(",");
		sb.append(chancellor);
		pw.write(sb.toString());
	}

}
