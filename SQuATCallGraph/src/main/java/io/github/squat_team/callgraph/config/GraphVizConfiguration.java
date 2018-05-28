package io.github.squat_team.callgraph.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Stores all the information that is required by Graphviz and other export
 * modules.
 *
 */
public class GraphVizConfiguration {
	/**
	 * Detects the client's operating system.
	 */
	private final static String OS_NAME = System.getProperty("os.name").replaceAll("\\s", "").replaceAll("\\d", "");
	private final Path tempDirectory;

	private String userDotPath;
	private String dotForMacOSX = "/usr/local/bin/dot";
	private String dotForWindows = "C:/Program Files (x86)/graphviz-2.38/release/bin/dot.exe";
	private String dotForLinux = "/usr/bin/dot";

	private String outputDir = "";
	private String outputFileName = "default";

	private boolean exportIds = false;

	protected GraphVizConfiguration(String outputDir) throws IOException {
		this.outputDir = outputDir;
		tempDirectory = Files.createTempDirectory("tempdotfiles");
		tempDirectory.toFile().deleteOnExit();
	}

	public boolean isExportIds() {
		return exportIds;
	}

	protected void setExportIds(boolean exportIds) {
		this.exportIds = exportIds;
	}

	public String getOutputDir() {
		if (!outputDir.isEmpty() && !outputDir.endsWith(File.separator)) {
			return outputDir + File.separator;
		} else {
			return outputDir;
		}
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	protected void setDotPath(String dotPath) {
		this.userDotPath = dotPath;
	}

	public String getDotPath() {
		if (userDotPath == null) {
			if (OS_NAME.contains("Windows")) {
				return dotForWindows;
			} else if (OS_NAME.contains("Linux")) {
				return dotForLinux;
			} else if (OS_NAME.contains("MacOSX")) {
				return dotForMacOSX;
			} else {
				throw new UnsupportedOperationException("Not implemented for used OS.");
			}
		} else {
			return userDotPath;
		}
	}

	public String getTempPath() {
		return tempDirectory.toAbsolutePath().toString();
	}
}
