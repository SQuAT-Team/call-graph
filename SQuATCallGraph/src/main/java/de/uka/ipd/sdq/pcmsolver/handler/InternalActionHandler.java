/**
 * 
 */
package de.uka.ipd.sdq.pcmsolver.handler;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.solver.context.computed_allocation.ComputedAllocationFactory;
import org.palladiosimulator.solver.context.computed_allocation.ResourceDemand;

import de.uka.ipd.sdq.pcmsolver.visitors.ExpressionHelper;
import de.uka.ipd.sdq.pcmsolver.visitors.SeffVisitor;
import de.uka.ipd.sdq.stoex.Expression;

/**
 * @author Koziolek
 * 
 */
public class InternalActionHandler{
	
	private static Logger logger = Logger.getLogger(InternalActionHandler.class.getName());
	
	private ComputedAllocationFactory compAllocationFactory = ComputedAllocationFactory.eINSTANCE;

	private SeffVisitor visitor; 
	
	public InternalActionHandler(SeffVisitor seffVisitor) {
		visitor=seffVisitor;
	}

	/**
	 * @param action
	 */
	public void handle(InternalAction action) {
		//EList<ParametricResourceDemand> resourceDem = action.getResourceDemand_Action()
		
		Iterator resourceDemands = action.getResourceDemand_Action().iterator();
		while (resourceDemands.hasNext()) {
			ParametricResourceDemand prd = (ParametricResourceDemand) resourceDemands.next();
			ProcessingResourceType requiredResourceType = prd.getRequiredResource_ParametricResourceDemand();

			if (requiredResourceType.getEntityName().equals("SystemExternalResource")){
				EList<ResourceContainer> resConList = visitor.getContextWrapper().getPcmInstance().getResourceEnvironment().getResourceContainer_ResourceEnvironment();
				for (ResourceContainer resCon : resConList){
					if(resCon.getEntityName().equals("SystemExternalResourceContainer")){
						ProcessingResourceSpecification prs = resCon.getActiveResourceSpecifications_ResourceContainer().get(0);
						createActualResourceDemand(action, prd, prs);
					}
				}
			} else {
				EList<ProcessingResourceSpecification> resourceList = getResourceList();
				for (ProcessingResourceSpecification prs : resourceList) {
					ProcessingResourceType currentResourceType = prs
							.getActiveResourceType_ActiveResourceSpecification();
					if (currentResourceType.getEntityName().equals(
							requiredResourceType.getEntityName())) {
						createActualResourceDemand(action, prd, prs);
					}
				}
			}
		}
	}

	/**
	 * Create a ResourceDemand in the computed allocation context 
	 * for the given ParametricResourceDemand and ProcessingResourceSpecification
	 * The resulting ResourceDemand already takes into account the processing rate.
	 * @param prd
	 * @param prs
	 */
	private void createActualResourceDemand(InternalAction action, ParametricResourceDemand prd, ProcessingResourceSpecification prs) {
		// TODO: include current branch conditions and loop iterations
		
		String spec = prd.getSpecification_ParametericResourceDemand().getSpecification();
		
		// quick fix: (convert pmfs to pdfs). This quick fix allows to use resource demands like "100 * file.BYTESIZE". 
		// Do not remove this until PMFs can be properly handled by the following, because the modelling of compression 
		// or other file handling would become difficult, one could not use the BYTESIZE characterization anymore in resource demands.  
//		spec = spec.replaceAll("IntPMF", "DoublePDF");
//		spec = spec.replaceAll("DoublePMF", "DoublePDF");
		
		String actResDemSpecification = getSolvedSpecification(spec, prs);
		
//		actResDemSpecification = actResDemSpecification.replaceAll("IntPMF", "DoublePDF");
//		actResDemSpecification = actResDemSpecification.replaceAll("DoublePMF", "DoublePDF");
		
		// TODO: Is there a better way to check the type of the specification?
//		if(actResDemSpecification.contains("PMF") == true) {
//			throw new RuntimeException("Resource demand specification \"" + spec
//					+ "\" of InternalAction \"" + action.getEntityName() + "\" (ID = \"" + action.getId()
//					+ "\") evaluates to a probability mass function (PMF), but only probability density functions (PDF) are allowed.");
//		}
		
		ResourceDemand ard = compAllocationFactory.createResourceDemand();
		ard.setParametricResourceDemand_ResourceDemand(prd);
		
		PCMRandomVariable rv = CoreFactory.eINSTANCE.createPCMRandomVariable();
		rv.setSpecification(actResDemSpecification);
		
		//convertLiteralsToPDFs(rv);
		
		ard.setSpecification_ResourceDemand(rv);
		
		visitor.getContextWrapper().getCompAllCtx()
				.getResourceDemands_ComputedAllocationContext().add(ard);
	}

	/**
	 * Get a combined expression for the demanded time: specification / processing rate prs
	 * 
	 * As the expressions can be more complex, we added parentheses, resulting in 
	 * (specification)/((prs)*1.0). This expression is then solved in the ContextWrapper of this.visitor. 
	 * and finally again printed to a String. 
	 * 
	 * @param specification
	 * @param prs
	 * @return A String with the solved expression. 
	 */
	private String getSolvedSpecification(String specification, ProcessingResourceSpecification prs) {

		// quickly incorporate processing rate
		/* As both divisor and divident may evaluate to an integer and the first may be smaller  
		 * than the latter, I added the factor *1.0 so that it is not falsely rounded to 0 
		 * (without *1.0, e.g. (4) / 20 would result in a demand of 0 instead of 0.2) 
		 */
		specification = "("+ specification+") / (("+prs.getProcessingRate_ProcessingResourceSpecification().getSpecification()+")*1.0)";
		logger.debug("Actual Resource Demand (Expression): "+specification);
		
		Expression solvedExpr = (Expression) ExpressionHelper
				.getSolvedExpression(specification, visitor.getContextWrapper());
		
		
		
		String solvedSpecification = ExpressionHelper
				.getSolvedExpressionAsString(specification,
						visitor.getContextWrapper());
		logger.debug("Computed Actual Resource Demand: "+solvedSpecification);
		return solvedSpecification;
	}

	/**
	 * @return
	 */
	private EList<ProcessingResourceSpecification> getResourceList() {
		AllocationContext ac = visitor.getContextWrapper().getAllCtx();
		ResourceContainer currentResourceContainer = ac.getResourceContainer_AllocationContext();
		EList<ProcessingResourceSpecification> resourceList = currentResourceContainer
				.getActiveResourceSpecifications_ResourceContainer();
		return resourceList;
	}

}
