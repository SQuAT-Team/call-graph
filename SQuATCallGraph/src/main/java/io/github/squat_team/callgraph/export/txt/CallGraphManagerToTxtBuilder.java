package io.github.squat_team.callgraph.export.txt;

import java.util.HashMap;
import java.util.Map;

import io.github.squat_team.callgraph.data.CallEntity;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.CallGraphManager;

/**
 * Transforms the general information of the whole call graph manager into
 * formatted text.
 */
public class CallGraphManagerToTxtBuilder extends AbstractTxtBuilder {
	private CallGraphManager callGraphManager;
	private Map<String, String> components;

	public CallGraphManagerToTxtBuilder(CallGraphManager callGraphManager) {
		super();
		this.callGraphManager = callGraphManager;
		this.components = findAllComponents();
	}

	private Map<String, String> findAllComponents() {
		Map<String, String> components = new HashMap<>();
		for (CallGraph callGraph : callGraphManager.getCallGraphs()) {
			for (CallEntity callEntity : callGraph.getEntities().values()) {
				components.put(callEntity.getComponentId(), callEntity.getComponentName());
			}
		}
		return components;
	}

	@Override
	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("TOTAL COMPONENTS: ").append(components.size()).append(System.lineSeparator());
		appendComponents(builder, components);
		return builder.toString();
	}

}
