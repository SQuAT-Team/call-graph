package io.github.squat_team.callgraph.data;

import java.util.ArrayList;
import java.util.List;

/**
 * As a call graph is only used for a single external call and there can be
 * several external calls in a model in general, this class knows all the
 * subgraphs of the model.
 */
public class CallGraphManager {
	private List<CallGraph> callGraphs = new ArrayList<CallGraph>();

	public CallGraph generateNewCallGraph(String graphName, String graphId, String graphInterfaceName) {
		CallGraph callGraph = new CallGraph(graphName, graphId, graphInterfaceName);
		callGraphs.add(callGraph);
		return callGraph;
	}

	public List<CallGraph> getCallGraphs() {
		return callGraphs;
	}

}
