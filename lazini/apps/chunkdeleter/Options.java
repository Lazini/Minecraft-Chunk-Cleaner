package lazini.apps.chunkdeleter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Options {

	private ArrayList<Option> optionsList = new ArrayList<>(50);
	private File saveFolderFile;

	public Options() {
		File file = new File("options.txt");
		int minY, maxY, radius;
		String blockIDs[];

		if (!file.exists()) { 
			System.err.println("Options file not found! Place it in the same folder as the jar file and try again!");
			System.exit(-1);
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
//			int COUNTER = 0;
			while (line != null) {
				line = line.replace(" ", "");
				if (line.startsWith("#")) {
					line = br.readLine();
					continue;
				}
				if (line.startsWith("saveFolderLoc:")) {
					String saveFolderString;
					int pos = line.indexOf(":");
					saveFolderString = line.substring(pos + 1, line.length());
					saveFolderString = saveFolderString.replace(" ", "");
					line = br.readLine().replace(" ", "");
					System.out.println("World file location: " + saveFolderString);
					saveFolderFile = new File(saveFolderString, "region"); // XXX TEMPORARY!!
				} else if (line.startsWith("Settings")) {

					line = br.readLine().replace(" ", "");
					String whitelistedBlockIDsString, minYString, maxYString, radiusString;
					Option option = new Option();
					while (line != null && !line.equalsIgnoreCase("Settings")) {
						line = line.replace(" ", "");
						if (line.startsWith("whitelistedBlockIDs:")) {
							int pos = line.indexOf(":");
							whitelistedBlockIDsString = line.substring(pos + 1, line.length());
							blockIDs = reloadRawBlockIDs(whitelistedBlockIDsString);
							option.setBlockIDs(blockIDs);
						} else if (line.startsWith("minY:")) {
							int pos = line.indexOf(":");
							minYString = line.substring(pos + 1, line.length());
							minY = reloadMinY(minYString);
							option.setMinY(minY);
						} else if (line.startsWith("maxY:")) {
							int pos = line.indexOf(":");
							maxYString = line.substring(pos + 1, line.length());
							maxY = reloadMaxY(maxYString);
							option.setMaxY(maxY);
						} else if (line.startsWith("radius:")) {
							int pos = line.indexOf(":");
							radiusString = line.substring(pos + 1, line.length());
							radius = reloadRadius(radiusString);
							option.setRadius(radius);
						}
//						COUNTER++;
//						System.out.println(COUNTER);
						line = br.readLine();
					}
					optionsList.add(option);

				} else {
					line = br.readLine();
					continue;
				}
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Something went wrong! Exiting!");
			System.exit(-1);
		}

	}

	/*/**
	 * Reads the <i>options.txt</i> file and gets the current options that are
	 * needed for enqueuing. To get the options individually, call the get
	 * methods for each option available.
	 */
//	private  void reloadConfig() throws FileNotFoundException {}

	private String[] reloadRawBlockIDs(String whitelistedBlockIDsString) {
		String temp = whitelistedBlockIDsString.replaceAll(" ", "");
		String[] list = temp.split(",");
		String[] array = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			try {
				array[i] = String.valueOf(list[i]);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				System.err.println("Whitelisted block IDs list cannot be empty! Whitespace is allowed");
				System.err.println("Exiting!");
				System.exit(-1);
			}
		}
		return array;

	}

	private int reloadMinY(String minYString) {
		try {
			minYString = minYString.replace(" ", "");
			return Math.max(Integer.parseInt(minYString), 0);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			System.err.println("Minimum Y value cannot be empty! Nor can you put more than 2 values! Whitespace is allowed");
			System.err.println("Exiting!");
			System.exit(-1);
			return 0;
		}
	}

	private int reloadMaxY(String maxYString) {
		try {
			maxYString = maxYString.replace(" ", "");
			return Math.min(Integer.parseInt(maxYString), 256);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			System.err.println("Maximum Y value cannot be empty! Nor can you put more than 2 values! Whitespace is allowed");
			System.err.println("Exiting!");
			System.exit(-1);
			return 256;
		}
	}

	private int reloadRadius(String radiusString) {
		try {
			radiusString = radiusString.replace(" ", "");
			return Integer.valueOf(radiusString) >= 0 ? Integer.valueOf(radiusString) : 0;
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			System.err.println("Radius value cannot be empty! Nor can you put more than 2 values! Whitespace is allowed");
			System.err.println("Exiting!");
			System.exit(-1);
			return 0;
		}
	}

	public File getSaveFolderFile() {
		if (!saveFolderFile.isDirectory()) {
			System.err.println("Invalid save folder location!");
			System.err.println("Exiting!");
			System.exit(-1);
		}
		return saveFolderFile;
	}

	public ArrayList<Option> getOptionsList() {
		return optionsList;
	}

}
