package io.github.squat_team.callgraph.export.dot;

import java.util.Collection;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;
import io.github.squat_team.callgraph.data.CallEntity;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.Link;

/**
 * Used for a call graph with a simple structure, like the overview graph.
 */
public class SimpleCallGraphToDotBuilder extends AbstractDotBuilder {

	private final static String PREFIX = "rankdir=LR; ";
	
	private final static String ENTRY_START = "\"";
	private final static String ENTRY_MIDDLE = "\" [label=\"";
	private final static String ENTRY_END = "\",shape=\"none\"] ";

	private final static String METHOD_START = "\"";
	private final static String METHOD_MIDDLE = "\" [label=\"";
	private final static String METHOD_END = "\",shape=\"oval\",style=\"filled\",color=\"#000000\",fillcolor=\"white\"] ";

	private final static String LINK_START = "";
	private final static String LINK_MIDDLE = "->";
	private final static String LINK_END = "[label=\"next\", style=\"solid\", arrowhead=\"open\", color=\"#000000\"]";
	
	private Collection<CallEntity> methods;
	private Collection<Link> links;
	private CallEntity start;

	public SimpleCallGraphToDotBuilder(CallGraph callGraph) {
		methods = callGraph.getEntities().values();
		links = callGraph.getLinks();
		start = callGraph.getStartEntity();
	}
	
	public String build(GraphVizConfiguration config) {
		StringBuilder builder = new StringBuilder();

		builder.append(PREFIX);
		
		builder.append(ENTRY_START);
		builder.append(filterID(start.getComponentId()));
		builder.append(ENTRY_MIDDLE);
		builder.append(start.getComponentName());
		builder.append(ENTRY_END);

			for (CallEntity method : methods) {
				builder.append(METHOD_START);
				builder.append(filterID(method.getComponentId()));
				builder.append(METHOD_MIDDLE);
				builder.append(method.getComponentName());
				builder.append(METHOD_END);
		}

		for (Link link : links) {
			builder.append(LINK_START);
			builder.append(filterID(link.getStartEntity().getComponentId()));
			builder.append(LINK_MIDDLE);
			builder.append(filterID(link.getTargetEntity().getComponentId()));
			builder.append(LINK_END);
		}

		return builder.toString();
	}
}
