package io.github.squat_team.callgraph.export.dot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.squat_team.callgraph.data.CallEntity;

/**
 * Provides general methods for the implementation of a {@link DotBuilder}.
 */
public abstract class AbstractDotBuilder implements DotBuilder {

	/**
	 * Removes symbols that are not allowed in a dot file.
	 * 
	 * @param id
	 * @return
	 */
	protected String filterID(String id) {
		return id.replaceAll("-", "");
	}

	/**
	 * Orders the SEFFs based on their component.
	 * 
	 * @param entities
	 * 
	 * @return List for all the called SEFFs of one component. The component is
	 *         identified by its ID.
	 */
	protected Map<String, List<CallEntity>> reorder(Collection<CallEntity> entities) {
		Map<String, List<CallEntity>> reorderedEntities = new HashMap<String, List<CallEntity>>();
		for (CallEntity entity : entities) {
			if (reorderedEntities.containsKey(entity.getComponentId())) {
				// add entity to list
				reorderedEntities.get(entity.getComponentId()).add(entity);
			} else {
				// add new list with entity
				List<CallEntity> entityList = new ArrayList<CallEntity>();
				entityList.add(entity);
				reorderedEntities.put(entity.getComponentId(), entityList);
			}
		}
		return reorderedEntities;
	}

}
