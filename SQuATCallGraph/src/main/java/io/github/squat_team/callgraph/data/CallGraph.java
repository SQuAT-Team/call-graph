package io.github.squat_team.callgraph.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A call graph consists of a start node, SEFFs as nodes and links between the SEFFs.
 */
public class CallGraph {
	private String graphName;
	private String graphId;
	private String graphInterfaceName;
	private Map<String, CallEntity> entities = new HashMap<String, CallEntity>();
	private Map<String, Link> links = new HashMap<>();
	private CallEntity start;

	public Map<String, CallEntity> getEntities() {
		return entities;
	}

	protected CallGraph(String graphName, String graphId, String graphInterfaceName) {
		super();
		this.graphName = graphName;
		this.graphId = graphId;
		this.graphInterfaceName = graphInterfaceName;
		String startName;
		if (graphInterfaceName.isEmpty()) {
			startName = "Start:" + System.lineSeparator() + graphName;
		} else {
			startName = "Start:" + System.lineSeparator() + graphInterfaceName + System.lineSeparator() + graphName;
		}
		start = new CallEntity(startName, "start", "", "start", graphInterfaceName, "start");
	}

	public String getGraphInterfaceName() {
		return graphInterfaceName;
	}

	public Collection<Link> getLinks() {
		return links.values();
	}

	public String getGraphName() {
		return graphName;
	}

	public String getGraphId() {
		return graphId;
	}

	public CallEntity getStartEntity() {
		return start;
	}

	public CallEntity getEntity(String componentName, String componentId, String methodName, String methodId,
			String interfaceName, String interfaceId) {
		String searchId = methodId + componentId + interfaceId;
		if (entities.containsKey(searchId)) {
			return entities.get(searchId);
		} else {
			CallEntity callEntity = new CallEntity(componentName, componentId, methodName, methodId, interfaceName,
					interfaceId);
			entities.put(searchId, callEntity);
			return callEntity;
		}
	}

	public void addLink(CallEntity startEntity, CallEntity targetEntity) {
		String startId = startEntity.getMethodId() + startEntity.getComponentId() + startEntity.getInterfaceId();
		String endId = targetEntity.getMethodId() + targetEntity.getComponentId() + targetEntity.getInterfaceId();
		String linkId = startId + endId;

		if (links.containsKey(linkId)) {
			Link existingLink = links.get(linkId);
			existingLink.setCount(existingLink.getCount() + 1);
		} else {
			links.put(linkId, new Link(startEntity, targetEntity));
		}
	}

	public void printDebug() {
		System.out.println("GRAPH: " + this.graphName);
		System.out.println("ENTITIES: " + entities.size());
		for (CallEntity entity : entities.values()) {
			System.out.println("- ENTITY: " + entity.getComponentName() + " " + entity.getInterfaceName() + " "
					+ entity.getMethodName() + " empty: " + entity.isEmpty());
		}
		System.out.println("LINKS: " + links.size());
		for (Link link : links.values()) {
			System.out.println("- LINK: " + "(" + link.getCount() + "x)");
			System.out.println("-- START: " + link.getStartEntity().getComponentName() + " "
					+ link.getStartEntity().getInterfaceName() + " " + link.getStartEntity().getMethodName());
			System.out.println("-- TARGET: " + link.getTargetEntity().getComponentName() + " "
					+ link.getTargetEntity().getInterfaceName() + " " + link.getTargetEntity().getMethodName());
		}
	}
}
