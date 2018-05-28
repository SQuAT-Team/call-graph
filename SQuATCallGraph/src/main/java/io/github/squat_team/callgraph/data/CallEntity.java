package io.github.squat_team.callgraph.data;

/**
 * A call entity represents a call to a specific SEFF. This is specified by the
 * owning component, the implemented interface and the implemented method.
 */
public class CallEntity {

	private String componentName;
	private String componentId;
	private String methodName;
	private String methodId;
	private String interfaceName;
	private String interfaceId;

	private boolean empty = true;

	public CallEntity(String componentName, String componentId, String methodName, String methodId,
			String interfaceName, String interfaceId) {
		this.componentName = componentName;
		this.componentId = componentId;
		this.methodName = methodName;
		this.methodId = methodId;
		this.interfaceName = interfaceName;
		this.interfaceId = interfaceId;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentId() {
		return componentId;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodId() {
		return methodId;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Marks this SEFF as empty. This means it is not implemented or just a
	 * Start-Stop-SEFF.
	 * 
	 * @param empty
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}
