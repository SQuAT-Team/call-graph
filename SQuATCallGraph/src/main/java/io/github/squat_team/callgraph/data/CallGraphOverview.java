package io.github.squat_team.callgraph.data;

import java.util.List;

/**
 * Can be used to generate an overview graph for the external calls in a model.
 */
public class CallGraphOverview {
	private final static String ID = "overviewid";
	private final static String NAME = "External Calls";
	private final static String INTERFACE_NAME = "";

	private CallGraph overviewGraph = new CallGraph(NAME, ID, INTERFACE_NAME);
	private List<CallGraph> callGraphs;

	public CallGraphOverview(CallGraphManager callGraphManager) {
		this.callGraphs = callGraphManager.getCallGraphs();
		generateOverviewGraph();
	}

	private void generateOverviewGraph() {
		CallEntity previousEntity = overviewGraph.getStartEntity();
		for (CallGraph graph : callGraphs) {
			CallEntity currentEntity = overviewGraph.getEntity(graph.getGraphName(), graph.getGraphId(), "", "",
					graph.getGraphInterfaceName(), "");
			overviewGraph.addLink(previousEntity, currentEntity);
			previousEntity = currentEntity;
		}
	}

	public CallGraph getOverviewGraph() {
		return overviewGraph;
	}
}
