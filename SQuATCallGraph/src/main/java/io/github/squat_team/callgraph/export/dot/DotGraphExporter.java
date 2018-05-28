package io.github.squat_team.callgraph.export.dot;

import java.io.File;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;
import io.github.squat_team.callgraph.export.graph.GraphViz;

/**
 * Exports raw dot data to files and graphs.
 */
public class DotGraphExporter {
	private GraphVizConfiguration graphVizConfig;
	private String dotFormat;

	/**
	 * Initializes a dot graph exporter.
	 * 
	 * @param graphVizConfig
	 *            the configuration for Graph Viz.
	 * @param dotFormat
	 *            the content of the dot file
	 */
	public DotGraphExporter(GraphVizConfiguration graphVizConfig, String dotFormat) {
		super();
		this.graphVizConfig = graphVizConfig;
		this.dotFormat = dotFormat;
	}

	public void exportDotGraph() {
		GraphViz gv = new GraphViz(graphVizConfig);
		gv.addln(gv.start_graph());
		gv.add(dotFormat);
		gv.addln(gv.end_graph());
		// String type = "gif";
		String type = "pdf";
		// gv.increaseDpi();
		gv.decreaseDpi();
		gv.decreaseDpi();

		File out = new File(graphVizConfig.getOutputDir() + graphVizConfig.getOutputFileName() + "." + type);
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type,
				graphVizConfig.getOutputDir() + graphVizConfig.getOutputFileName()), out);
	}

}
