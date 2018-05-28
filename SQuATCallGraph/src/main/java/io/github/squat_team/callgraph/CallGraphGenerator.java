package io.github.squat_team.callgraph;

import java.util.Properties;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import de.fakeller.palladio.environment.PalladioEclipseEnvironment;
import de.uka.ipd.sdq.pcmsolver.models.PCMInstance;
import de.uka.ipd.sdq.pcmsolver.visitors.UsageModelVisitor;
import io.github.squat_team.callgraph.config.CallGraphConfiguration;
import io.github.squat_team.callgraph.config.DependencySolverConfiguration;
import io.github.squat_team.callgraph.config.GraphVizConfiguration;
import io.github.squat_team.callgraph.data.CallGraph;
import io.github.squat_team.callgraph.data.CallGraphManager;
import io.github.squat_team.callgraph.export.CallGraphExporter;

/**
 * Outer interface for the call graph generation. A
 * {@link CallGraphConfiguration} has to be provided, then a call graph will be
 * generated as specified in the configuration when
 * {@link CallGraphGenerator#generate()} gets called.
 */
public class CallGraphGenerator {

	CallGraphConfiguration configuration;

	public CallGraphGenerator(CallGraphConfiguration configuration) {
		super();
		this.configuration = configuration;
	};

	/**
	 * Runs the analysis and exports the result files to the location specified in the configuration.
	 */
	public void generate() {
		setupEclipseEnvironment();
		CallGraphManager callGraphData = runDependencySolver(setupModel());
		outputDataDebugInformation(callGraphData);
		exportResults(callGraphData);
	}

	private void setupEclipseEnvironment() {
		PalladioEclipseEnvironment.INSTANCE.setup();
	}

	private PCMInstance setupModel() {
		DependencySolverConfiguration dsConfig = configuration.getDependencySolverConfiguration();
		Properties properties = new Properties();
		properties.put("Filename_UsageModel", dsConfig.getUsageModelPath());
		properties.put("Filename_AllocationModel", dsConfig.getAllocationModelPath());
		return new PCMInstance(properties);
	}

	private CallGraphManager runDependencySolver(PCMInstance model) {
		UsageModelVisitor visitor = new UsageModelVisitor(model);
		UsageScenario us = (UsageScenario) model.getUsageModel().getUsageScenario_UsageModel().get(0);
		visitor.doSwitch(us.getScenarioBehaviour_UsageScenario());
		return visitor.getCallGraphManager();
	}

	private void outputDataDebugInformation(CallGraphManager callGraphData) {
		if (configuration.isDebugMode()) {
			System.out.println("CALL GRAPHS: " + callGraphData.getCallGraphs().size());
			for (CallGraph callGraph : callGraphData.getCallGraphs()) {
				callGraph.printDebug();
			}
		}
	}

	private void exportResults(CallGraphManager callGraphData) {
		GraphVizConfiguration graphVizConfig = configuration.getGraphVizConfiguration();
		CallGraphExporter exporter = new CallGraphExporter(callGraphData);
		exporter.export(graphVizConfig);
	}

}
