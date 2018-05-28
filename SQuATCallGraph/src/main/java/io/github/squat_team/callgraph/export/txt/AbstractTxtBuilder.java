package io.github.squat_team.callgraph.export.txt;

import java.util.Map;

/**
 * Provides general methods for the implementation of a {@link TxtBuilder}.
 *
 */
public abstract class AbstractTxtBuilder implements TxtBuilder {

	protected StringBuilder appendComponent(StringBuilder builder, String id, String name) {
		builder.append("- ").append(name).append(" (").append(id).append(")").append(System.lineSeparator());
		return builder;
	}

	protected StringBuilder appendComponents(StringBuilder builder, Map<String, String> components) {
		for (String componentId : components.keySet()) {
			appendComponent(builder, componentId, components.get(componentId));
		}
		return builder;
	}
}
