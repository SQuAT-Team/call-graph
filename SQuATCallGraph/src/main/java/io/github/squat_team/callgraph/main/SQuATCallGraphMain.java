package io.github.squat_team.callgraph.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import io.github.squat_team.callgraph.CallGraphGenerator;
import io.github.squat_team.callgraph.config.CallGraphConfiguration;

/**
 * An example main class to run the Call Graph Generator. Final results should
 * be in src/main/resources/output.
 */
public class SQuATCallGraphMain {
	/*
	 * Change this to analyze another model.
	 */
	private final static String USAGE_MODEL_LOCATION = "cocome/cocome-cloud.usagemodel";
	private final static String OUTPUT_DIR = Paths.get("src", "main", "resources", "output").toAbsolutePath()
			.toString();

	private static ClassLoader CLASS_LOADER;
	private static File USAGE_MODEL;
	private static String BASE_DIR;
	private static String BASE_FILE_NAME;

	public static void init() {
		CLASS_LOADER = SQuATCallGraphMain.class.getClassLoader();
		USAGE_MODEL = new File(CLASS_LOADER.getResource(USAGE_MODEL_LOCATION).getFile());
		BASE_DIR = USAGE_MODEL.getParent();
		BASE_FILE_NAME = USAGE_MODEL.getName().replaceAll(".usagemodel", "");
	}

	public static void main(String[] args) throws IOException {
		init();

		// Configuration
		CallGraphConfiguration config = new CallGraphConfiguration(BASE_DIR, BASE_FILE_NAME, OUTPUT_DIR);
		config.setExportIds(true);
		
		// Optional, but should be set manually
		config.setDotPath("C:/Program Files (x86)/graphviz-2.38/release/bin/dot.exe");

		// Execute
		CallGraphGenerator callGraphGenerator = new CallGraphGenerator(config);
		callGraphGenerator.generate();
	}

}
