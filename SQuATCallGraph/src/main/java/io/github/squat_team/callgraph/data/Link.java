package io.github.squat_team.callgraph.data;

/**
 * A link describes a call from one SEFF to another.
 */
public class Link {
	private CallEntity startEntity;
	private CallEntity targetEntity;
	private int count;

	public Link(CallEntity startEntity, CallEntity targetEntity) {
		super();
		this.startEntity = startEntity;
		this.targetEntity = targetEntity;
		setCount(1);
	}

	public CallEntity getStartEntity() {
		return startEntity;
	}

	public CallEntity getTargetEntity() {
		return targetEntity;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
