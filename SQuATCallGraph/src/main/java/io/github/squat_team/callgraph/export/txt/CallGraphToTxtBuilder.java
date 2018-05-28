package io.github.squat_team.callgraph.export.txt;

import java.util.HashMap;
import java.util.Map;

import io.github.squat_team.callgraph.data.CallEntity;
import io.github.squat_team.callgraph.data.CallGraph;

/**
 * Transforms the data of one call graph (associated with one external call)
 * into formatted text.
 */
public class CallGraphToTxtBuilder extends AbstractTxtBuilder {
	private CallGraph callGraph;
	private Map<String, String> components;

	public CallGraphToTxtBuilder(CallGraph callGraph) {
		super();
		this.callGraph = callGraph;
		this.components = findAllComponents();
	}

	private Map<String, String> findAllComponents() {
		Map<String, String> components = new HashMap<>();
		for (CallEntity callEntity : callGraph.getEntities().values()) {
			components.put(callEntity.getComponentId(), callEntity.getComponentName());
		}
		return components;
	}

	@Override
	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("COMPONENTS OF ").append(callGraph.getGraphName()).append(": ").append(components.size())
				.append(System.lineSeparator());
		appendComponents(builder, components);
		return builder.toString();
	}

}
