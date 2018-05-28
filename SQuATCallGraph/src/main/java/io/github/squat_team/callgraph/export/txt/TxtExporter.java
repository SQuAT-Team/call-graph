package io.github.squat_team.callgraph.export.txt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;

/**
 * Exports all textual graph information to a text file.
 */
public class TxtExporter {
	private final static String FILE_NAME = "CallGraphInfo";
	private final static String FILE_EXTENSION = ".txt";

	private GraphVizConfiguration graphVizConfig;
	private String txtFormat;

	public TxtExporter(GraphVizConfiguration graphVizConfig, String txtFormat) {
		super();
		this.graphVizConfig = graphVizConfig;
		this.txtFormat = txtFormat;
	}

	public void exportTxtFile() {
		File out = new File(graphVizConfig.getOutputDir() + FILE_NAME + FILE_EXTENSION);
		writeToFile(out, txtFormat);
	}

	private void writeToFile(File outputFile, String text) {
		try {
			FileOutputStream is = new FileOutputStream(outputFile);
			OutputStreamWriter osw = new OutputStreamWriter(is);
			Writer w = new BufferedWriter(osw);
			w.write(text);
			w.close();
		} catch (IOException e) {
			System.err.println("Couldn't write output file for Call Graph");
		}
	}

}
