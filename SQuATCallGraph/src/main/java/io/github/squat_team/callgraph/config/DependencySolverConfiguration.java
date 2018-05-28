package io.github.squat_team.callgraph.config;

import java.io.File;

/**
 * Stores all the information the PCM Dependency Solver requires.
 */
public class DependencySolverConfiguration {
	private final static String USAGE_MODEL_EXTENSION = ".usagemodel";
	private final static String ALLOCATION_MODEL_EXTENSION = ".allocation";
	
	private String baseDirectoryPath;
	private String baseFileName;
	
	protected DependencySolverConfiguration(String baseDirectoryPath, String baseFileName){
		this.baseDirectoryPath = baseDirectoryPath;
		this.baseFileName = baseFileName;
	}
	
	public String getUsageModelPath() {
		return baseDirectoryPath + File.separator + baseFileName + USAGE_MODEL_EXTENSION;
	}

	public String getAllocationModelPath() {
		return baseDirectoryPath + File.separator + baseFileName + ALLOCATION_MODEL_EXTENSION;
	}

}
