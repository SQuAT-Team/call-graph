package io.github.squat_team.callgraph.export.dot;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;

/**
 * Generates the raw data for a dot file that can be transformed into a graph.
 */
public interface DotBuilder {

	/**
	 * Builds the raw data for a dot file that can be transformed into a graph.
	 * 
	 * @param config
	 * @return the file content.
	 */
	public String build(GraphVizConfiguration config);
}
