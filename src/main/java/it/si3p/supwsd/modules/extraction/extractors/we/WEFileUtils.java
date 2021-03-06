package it.si3p.supwsd.modules.extraction.extractors.we;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.code.externalsorting.ExternalSort;

/**
 * @author papandrea
 *
 */
public class WEFileUtils {

	public static File sortFile(String inputFile, String outputFile, Comparator<String> comparator) throws IOException {

		File file;
		List<File> files;

		files = ExternalSort.sortInBatch(new File(inputFile));
		file = new File(outputFile);
		ExternalSort.mergeSortedFiles(files, file, comparator);

		return file;
	}

	public static void splitFile(File inputFile, String keysFile, String outDir, String separator, int blockSize)
			throws IOException {

		String line;
		String word = "";
		List<String> lines;
		int index = 1, i = 0;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
				BufferedWriter keysWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(keysFile)));) {

			lines = new ArrayList<String>();

			while ((line = reader.readLine()) != null || !lines.isEmpty()) {

				if (line != null) {
					lines.add(line);
					word = line.substring(0, line.indexOf(separator));
					index++;
				}

				if (index % blockSize == 0 || line == null) {

					keysWriter.write(word);
					keysWriter.newLine();

					try (BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(outDir + i++)))) {

						for (String l : lines) {
							writer.write(l);
							writer.newLine();
						}

						lines.clear();
					}
				}
			}
		}
	}

	public static void sortFilesDir(String inputDir, String separator, Comparator<String> comparator)
			throws IOException {

		BufferedReader reader;
		BufferedWriter writer;
		Map<String, String> vectors;
		List<String> keys;
		File folder;
		String line;
		int index;

		folder = new File(inputDir);

		for (File file : folder.listFiles()) {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			vectors = new HashMap<String, String>();

			while ((line = reader.readLine()) != null) {

				index = line.indexOf(separator);

				if (index > -1)
					vectors.put(line.substring(0, index), line);
			}

			reader.close();
			keys = new ArrayList<String>(vectors.keySet());
			keys.sort(comparator);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

			for (String key : keys) {
				writer.write(vectors.get(key));
				writer.newLine();
			}

			writer.close();
			vectors.clear();
			keys.clear();
		}
	}

	public static List<String> readLines(String file) throws IOException {

		List<String> lines;
		String line;

		lines = new ArrayList<String>();

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

			while ((line = reader.readLine()) != null)
				lines.add(line);
		}

		return lines;
	}

	public static int getCountLines(String file) throws IOException {

		int lines = 0;

		try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));){
			
			lineNumberReader.skip(Long.MAX_VALUE);
			lines = lineNumberReader.getLineNumber();
		}

		return lines;
	}

}
