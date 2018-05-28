package de.uka.ipd.sdq.pcmsolver.visitors;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.parameter.ParameterFactory;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.solver.context.computed_allocation.ComputedAllocationFactory;
import org.palladiosimulator.solver.context.computed_usage.ComputedUsageFactory;

import de.uka.ipd.sdq.pcmsolver.models.PCMInstance;
import de.uka.ipd.sdq.pcmsolver.transformations.ContextWrapper;
import io.github.squat_team.callgraph.data.CallEntity;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.CallGraphManager;

/**
 * Visitor that builds the computed usage and computed allocation contexts by
 * calling {@link SeffVisitor} for each {@link EntryLevelSystemCall}.
 * 
 * @author Koziolek, Martens
 * 
 */
public class UsageModelVisitor extends UsagemodelSwitch {
	protected static Logger logger = Logger.getLogger(UsageModelVisitor.class.getName());

	protected PCMInstance pcmInstance;
	private ComputedUsageFactory compUsageFactory;
	private ComputedAllocationFactory compAllocationFactory;
	private ParameterFactory parameterFactory;
	
	private CallGraphManager callGraphManager;

	protected ContextWrapper myContextWrapper = null;

	/**
	 * Solves dependencies for this {@link PCMInstance} and adds the results to
	 * the {@link PCMInstance}'s computed contexts (such as
	 * {@link PCMInstance#getComputedUsage()}).
	 * 
	 * @param inst
	 *            an instance of the Palladio Component Metamodel
	 */
	public UsageModelVisitor(PCMInstance inst) {
		pcmInstance = inst;
		compUsageFactory = ComputedUsageFactory.eINSTANCE;
		compAllocationFactory = ComputedAllocationFactory.eINSTANCE;
		parameterFactory = ParameterFactory.eINSTANCE;
		callGraphManager = new CallGraphManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uka.ipd.sdq.pcm.usagemodel.util.UsagemodelSwitch#caseScenarioBehaviour
	 * (de.uka.ipd.sdq.pcm.usagemodel.ScenarioBehaviour)
	 */
	@Override
	public Object caseScenarioBehaviour(ScenarioBehaviour object) {
		logger.debug("VisitScenarioBehaviour");
		doSwitch(getStartAction(object));
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uka.ipd.sdq.pcm.usagemodel.util.UsagemodelSwitch#caseStart(de.uka.ipd.
	 * sdq.pcm.usagemodel.Start)
	 */
	@Override
	public Object caseStart(Start object) {
		logger.debug("VisitStart");
		doSwitch(object.getSuccessor());
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uka.ipd.sdq.pcm.usagemodel.util.UsagemodelSwitch#caseStop(de.uka.ipd.
	 * sdq.pcm.usagemodel.Stop)
	 */
	@Override
	public Object caseStop(Stop object) {
		logger.debug("VisitStop");
		return object;
	}

	@Override
	public Object caseBranch(Branch object) {
		logger.debug("VisitBranch");
		EList<BranchTransition> btList = object.getBranchTransitions_Branch();
		for (BranchTransition bt : btList) {
			doSwitch(bt.getBranchedBehaviour_BranchTransition());
		}
		doSwitch(object.getSuccessor());
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uka.ipd.sdq.pcm.usagemodel.util.UsagemodelSwitch#
	 * caseEntryLevelSystemCall(de.uka.ipd.sdq.pcm.usagemodel.
	 * EntryLevelSystemCall)
	 */
	@Override
	public Object caseEntryLevelSystemCall(EntryLevelSystemCall elsc) {
		String elscName = elsc.getOperationSignature__EntryLevelSystemCall().getEntityName();
		String elscID = elsc.getOperationSignature__EntryLevelSystemCall().getId();
		String elscInterfaceName = elsc.getProvidedRole_EntryLevelSystemCall().getProvidedInterface__OperationProvidedRole().getEntityName();
		
		CallGraph callGraph = callGraphManager.generateNewCallGraph(elscName, elscID, elscInterfaceName);

		logger.debug("VisitEntryLevelSystemCall");
		logger.debug("Called System Method " + elscName);

		// Get List of ContextWrappers, one for each called component instance
		List<ContextWrapper> contextWrapperList;
		if (myContextWrapper == null)
			contextWrapperList = ContextWrapper.getContextWrapperFor(elsc, pcmInstance);
		else
			contextWrapperList = myContextWrapper.getContextWrapperFor(elsc);

		for (ContextWrapper contextWrapper : contextWrapperList) {
			ServiceEffectSpecification seff = contextWrapper.getNextSEFF(elsc);
			OperationSignature signature = elsc.getOperationSignature__EntryLevelSystemCall();
			String methodName = signature.getEntityName();
			String methodId = signature.getId();
			String interfaceName = elsc.getProvidedRole_EntryLevelSystemCall()
					.getProvidedInterface__OperationProvidedRole().getEntityName();
			String interfaceId = elsc.getProvidedRole_EntryLevelSystemCall()
					.getProvidedInterface__OperationProvidedRole().getId();
			String componentName = seff.getBasicComponent_ServiceEffectSpecification().getEntityName();
			String componentId = seff.getBasicComponent_ServiceEffectSpecification().getId();

			CallEntity startCallEntity = callGraph.getStartEntity();
			CallEntity targetCallEntity = callGraph.getEntity(componentName, componentId, methodName, methodId,
					interfaceName, interfaceId);
			callGraph.addLink(startCallEntity, targetCallEntity);

			SeffVisitor visitor = new SeffVisitor(seff, contextWrapper, callGraph, targetCallEntity);
			// try {
			visitor.doSwitch((ResourceDemandingSEFF) seff);
			/*
			 * } catch (Exception e) { logger.error(
			 * "Error while visiting RDSEFF"); e.printStackTrace(); }
			 */

		}

		// XXX: The internal myContextWrapper is not affected by the handling of
		// the
		// EntryLevelSystem call because the copies of it handle it. This was
		// different
		// before allowing replication, when only one ContextWrapper instance
		// was used.
		doSwitch(elsc.getSuccessor());
		return elsc;
	}

	@Override
	public Object caseDelay(Delay object) {
		logger.debug("VisitDelay");
		doSwitch(object.getSuccessor());
		return object;
	}

	@Override
	public Object caseLoop(Loop object) {
		logger.debug("VisitLoop");
		doSwitch(object.getBodyBehaviour_Loop());
		doSwitch(object.getSuccessor());
		return object;
	}

	/**
	 * @param object
	 * @return
	 */
	private Start getStartAction(ScenarioBehaviour object) {
		Start startAction = (Start) EMFQueryHelper.getObjectByType(object.getActions_ScenarioBehaviour(), Start.class);
		return startAction;
	}

	/**
	 * Get the Call Graph Manager associated with this visitor run and all subordinated {@link SeffVisitor}s.
	 * 
	 * @return
	 */
	public CallGraphManager getCallGraphManager() {
		return callGraphManager;
	}

}
