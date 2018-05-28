package io.github.squat_team.callgraph.config;

import java.io.IOException;

import io.github.squat_team.callgraph.CallGraphGenerator;

/**
 * A configuration for running the call graph generation process. This
 * configuration is part of the outer interface and has to be passed to
 * {@link CallGraphGenerator}.
 */
public class CallGraphConfiguration {
	private DependencySolverConfiguration dependencySolverConfiguration;
	private GraphVizConfiguration graphVizConfiguration;
	private boolean debugMode = false;

	/**
	 * Initializes a new configuration for the call graph tool.
	 * 
	 * @param baseModelPath
	 *            The directory where the model is located in.
	 * @param baseFileName
	 *            The name of the model without file extension.
	 * @param outputPath
	 *            The directory the result files are written to.
	 * @throws IOException
	 *             This can happen if the temp directory can not be created by
	 *             the VM.
	 */
	public CallGraphConfiguration(String baseModelPath, String baseFileName, String outputPath) throws IOException {
		dependencySolverConfiguration = new DependencySolverConfiguration(baseModelPath, baseFileName);
		graphVizConfiguration = new GraphVizConfiguration(outputPath);
	}

	public DependencySolverConfiguration getDependencySolverConfiguration() {
		return dependencySolverConfiguration;
	}

	public GraphVizConfiguration getGraphVizConfiguration() {
		return graphVizConfiguration;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Specify whether the tool should output additional debug information to
	 * the console.
	 * 
	 * @param debugMode
	 *            true for additional debug information.
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * This option (de-)activates the export of ids of model elements in the
	 * graphs. Components in PCM can have the same name, because of that it is
	 * sometimes necessary to know the id to identify them. However, the graph
	 * will be less compact.
	 * 
	 * @param exportIds
	 */
	public void setExportIds(boolean exportIds) {
		graphVizConfiguration.setExportIds(exportIds);
	}

	/**
	 * Sets the path of the graphviz executable file. Optional, but it is highly
	 * recommended to set the path manually. Otherwise the tool will try to
	 * guess the location.
	 * 
	 * @param dotPath
	 *            the path of the executable, e.g., C:/Program Files
	 *            (x86)/graphviz-2.38/release/bin/dot.exe.
	 */
	public void setDotPath(String dotPath) {
		graphVizConfiguration.setDotPath(dotPath);
	}

}
