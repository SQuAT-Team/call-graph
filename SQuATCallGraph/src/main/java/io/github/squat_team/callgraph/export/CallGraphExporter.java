package io.github.squat_team.callgraph.export;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.CallGraphManager;
import io.github.squat_team.callgraph.data.CallGraphOverview;
import io.github.squat_team.callgraph.export.dot.CallGraphToDotBuilder;
import io.github.squat_team.callgraph.export.dot.DotGraphExporter;
import io.github.squat_team.callgraph.export.dot.SimpleCallGraphToDotBuilder;
import io.github.squat_team.callgraph.export.txt.CallGraphManagerToTxtBuilder;
import io.github.squat_team.callgraph.export.txt.CallGraphToTxtBuilder;
import io.github.squat_team.callgraph.export.txt.TxtExporter;

/**
 * Exports all the results files for a successful run of the call graph tool.
 * This includes a graph for each external call in the model, an overview graph
 * for all these graphs, and a text file with general information.
 */
public class CallGraphExporter {
	private CallGraphManager callGraphManager;

	public CallGraphExporter(CallGraphManager callGraphManager) {
		this.callGraphManager = callGraphManager;
	}

	public void export(GraphVizConfiguration config) {
		exportDotGraphs(config);
		exportTxtInfo(config);
	}

	private void exportDotGraphs(GraphVizConfiguration config) {
		exportOverviewGraph(config);
		exportCallGraphs(config);
	}

	private void exportOverviewGraph(GraphVizConfiguration config) {
		CallGraphOverview overview = new CallGraphOverview(callGraphManager);
		CallGraph overviewGraph = overview.getOverviewGraph();
		config.setOutputFileName(overviewGraph.getGraphName());
		SimpleCallGraphToDotBuilder builder = new SimpleCallGraphToDotBuilder(overviewGraph);
		String dotFormat = builder.build(config);
		DotGraphExporter dotExporter = new DotGraphExporter(config, dotFormat);
		dotExporter.exportDotGraph();
	}

	private void exportCallGraphs(GraphVizConfiguration config) {
		for (CallGraph callGraph : callGraphManager.getCallGraphs()) {
			exportCallGraph(callGraph, config);
		}
	}

	private void exportCallGraph(CallGraph callGraph, GraphVizConfiguration config) {
		config.setOutputFileName(callGraph.getGraphName());
		CallGraphToDotBuilder builder = new CallGraphToDotBuilder(callGraph);
		String dotFormat = builder.build(config);
		DotGraphExporter dotExporter = new DotGraphExporter(config, dotFormat);
		dotExporter.exportDotGraph();
	}

	private void exportTxtInfo(GraphVizConfiguration config) {
		// append general info
		StringBuilder builder = new StringBuilder();
		CallGraphManagerToTxtBuilder generalInfoBuilder = new CallGraphManagerToTxtBuilder(callGraphManager);
		builder.append(generalInfoBuilder.build());
		builder.append(System.lineSeparator()).append(System.lineSeparator());

		// append call graph specific info
		for (CallGraph callGraph : callGraphManager.getCallGraphs()) {
			CallGraphToTxtBuilder callGraphInfoBuilder = new CallGraphToTxtBuilder(callGraph);
			builder.append(callGraphInfoBuilder.build());
			builder.append(System.lineSeparator()).append(System.lineSeparator());
		}

		TxtExporter txtExporter = new TxtExporter(config, builder.toString());
		txtExporter.exportTxtFile();
	}

}
