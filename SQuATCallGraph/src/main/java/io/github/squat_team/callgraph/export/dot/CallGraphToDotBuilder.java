package io.github.squat_team.callgraph.export.dot;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.github.squat_team.callgraph.config.GraphVizConfiguration;
import io.github.squat_team.callgraph.data.CallEntity;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.Link;

/**
 * Generates the dot file content for one external call.
 */
public class CallGraphToDotBuilder extends AbstractDotBuilder {

	private final static String ENTRY_START = "\"";
	private final static String ENTRY_MIDDLE = "\" [label=\"";
	private final static String ENTRY_END = "\",shape=\"none\"] ";

	private final static String COMPONENT_START = "subgraph \"cluster";
	private final static String COMPONENT_MIDDLE1 = "\" { label = \"";
	private final static String COMPONENT_MIDDLE2 = "\"; shape = \"box\"; style = \"filled\"; fillcolor = \"white\"; ";
	private final static String COMPONENT_END = "} ";

	private final static String METHOD_START = "\"";
	private final static String METHOD_MIDDLE = "\" [label=\"";
	private final static String METHOD_END_EMPTY = "\",shape=\"oval\",style=\"dotted\",color=\"#000000\",fillcolor=\"white\"] ";
	private final static String METHOD_END_NOT_EMPTY = "\",shape=\"oval\",style=\"filled\",color=\"#000000\",fillcolor=\"white\"] ";

	private final static String LINK_START = "";
	private final static String LINK_MIDDLE = "->";
	private final static String LINK_END1 = "[label=\"calls (";
	private final static String LINK_END2 = "x)\", style=\"solid\", arrowhead=\"open\", color=\"#000000\"]";

	private Map<String, List<CallEntity>> methodsInComponents;
	private Collection<Link> links;
	private CallEntity start;

	public CallGraphToDotBuilder(CallGraph callGraph) {
		methodsInComponents = reorder(callGraph.getEntities().values());
		links = callGraph.getLinks();
		start = callGraph.getStartEntity();
	}

	public String build(GraphVizConfiguration config) {
		StringBuilder builder = new StringBuilder();

		// Start Node
		builder.append(ENTRY_START);
		builder.append(filterID(start.getInterfaceId())).append(filterID(start.getMethodId()));
		builder.append(ENTRY_MIDDLE);
		builder.append(start.getComponentName());
		builder.append(ENTRY_END);

		// All components
		for (List<CallEntity> methods : methodsInComponents.values()) {
			builder.append(COMPONENT_START);
			builder.append(filterID(methods.get(0).getComponentId()));
			builder.append(COMPONENT_MIDDLE1);
			builder.append(methods.get(0).getComponentName());
			if (config.isExportIds()) {
				builder.append(" (").append(methods.get(0).getComponentId()).append(")");
			}
			builder.append(COMPONENT_MIDDLE2);

			// All methods inside a component
			for (CallEntity method : methods) {
				builder.append(METHOD_START);
				builder.append(filterID(method.getInterfaceId())).append(filterID(method.getMethodId()));
				builder.append(METHOD_MIDDLE);
				builder.append(method.getInterfaceName());
				if (config.isExportIds()) {
					builder.append(" (").append(method.getInterfaceId()).append(")");
				}
				builder.append(System.lineSeparator()).append(method.getMethodName());
				if (config.isExportIds()) {
					builder.append(" (").append(method.getMethodId()).append(")");
				}

				if (method.isEmpty()) {
					builder.append(METHOD_END_EMPTY);
				} else {
					builder.append(METHOD_END_NOT_EMPTY);
				}
			}
			builder.append(COMPONENT_END);
		}

		// All the calls from one methods to another
		for (Link link : links) {
			builder.append(LINK_START);
			builder.append(filterID(link.getStartEntity().getInterfaceId()))
					.append(filterID(link.getStartEntity().getMethodId()));
			builder.append(LINK_MIDDLE);
			builder.append(filterID(link.getTargetEntity().getInterfaceId()))
					.append(filterID(link.getTargetEntity().getMethodId()));
			builder.append(LINK_END1);
			builder.append(link.getCount());
			builder.append(LINK_END2);
		}

		return builder.toString();
	}

}
