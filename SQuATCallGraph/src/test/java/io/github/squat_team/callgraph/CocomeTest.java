package io.github.squat_team.callgraph;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.squat_team.callgraph.config.CallGraphConfiguration;

/**
 * Simple Test and result verification for the Cocome model.
 */
public class CocomeTest {
	private static ClassLoader classLoader;
	private static Path outputDirectoryPath;
	private static Path outputDirectoryPath2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		classLoader = CocomeTest.class.getClassLoader();
		File usageModel = new File(classLoader.getResource("cocome/cocome-cloud.usagemodel").getFile());

		outputDirectoryPath = Paths.get("src", "test", "resources", "callgraph-output");
		outputDirectoryPath2 = Paths.get("src", "test", "resources", "callgraph-output-ids");

		String baseModelDirectory = usageModel.getParent();
		String baseModelName = usageModel.getName().replaceAll(".usagemodel", "");
		String outputDirectory = outputDirectoryPath.toAbsolutePath().toString();
		String outputDirectory2 = outputDirectoryPath2.toAbsolutePath().toString();

		// just to be sure the files are all gone
		deleteOldFiles(outputDirectoryPath);
		deleteOldFiles(outputDirectoryPath2);

		try {
			// run call graph generation WITHOUT ids
			CallGraphConfiguration configuration = new CallGraphConfiguration(baseModelDirectory, baseModelName,
					outputDirectory);
			configuration.setExportIds(false);
			CallGraphGenerator callGenerator = new CallGraphGenerator(configuration);
			callGenerator.generate();

			// run call graph generation WITH ids
			CallGraphConfiguration configuration2 = new CallGraphConfiguration(baseModelDirectory, baseModelName,
					outputDirectory2);
			configuration2.setExportIds(true);
			CallGraphGenerator callGenerator2 = new CallGraphGenerator(configuration2);
			callGenerator2.generate();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception setting up Cocome test");
		}
	}

	@AfterClass
	public static void cleanUp() {
		deleteOldFiles(outputDirectoryPath);
		deleteOldFiles(outputDirectoryPath2);
	}

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Assures the result files have been generated.
	 */
	@Test
	public void testFilesExist() {
		testAllFilesExist(outputDirectoryPath);
		testAllFilesExist(outputDirectoryPath2);
	}

	private void testAllFilesExist(Path directoryPath) {
		File directory = directoryPath.toFile();
		assertEquals(15, directory.listFiles().length);

		testFileExists(directory, "addDigitToBarcode.dot");
		testFileExists(directory, "clearBarcode.dot");
		testFileExists(directory, "enterCashAmount.dot");
		testFileExists(directory, "External Calls.dot");
		testFileExists(directory, "resetSale.dot");
		testFileExists(directory, "scanBarcode.dot");
		testFileExists(directory, "startCashPayment.dot");

		testFileExists(directory, "addDigitToBarcode.pdf");
		testFileExists(directory, "clearBarcode.pdf");
		testFileExists(directory, "enterCashAmount.pdf");
		testFileExists(directory, "External Calls.pdf");
		testFileExists(directory, "resetSale.pdf");
		testFileExists(directory, "scanBarcode.pdf");
		testFileExists(directory, "startCashPayment.pdf");

		testFileExists(directory, "CallGraphInfo.txt");
	}

	private void testFileExists(File directory, String fileName) {
		assertTrue((new File(directory, fileName)).exists());
	}

	/**
	 * Assures the generated files are not empty.
	 */
	@Test
	public void testFilesNotEmpty() {
		testAllFilesNotEmpty(outputDirectoryPath);
		testAllFilesNotEmpty(outputDirectoryPath2);
	}

	public void testAllFilesNotEmpty(Path directoryPath) {
		File directory = directoryPath.toFile();

		testFileNotEmpty(directory, "addDigitToBarcode.dot");
		testFileNotEmpty(directory, "clearBarcode.dot");
		testFileNotEmpty(directory, "enterCashAmount.dot");
		testFileNotEmpty(directory, "External Calls.dot");
		testFileNotEmpty(directory, "resetSale.dot");
		testFileNotEmpty(directory, "scanBarcode.dot");
		testFileNotEmpty(directory, "startCashPayment.dot");

		testFileNotEmpty(directory, "addDigitToBarcode.pdf");
		testFileNotEmpty(directory, "clearBarcode.pdf");
		testFileNotEmpty(directory, "enterCashAmount.pdf");
		testFileNotEmpty(directory, "External Calls.pdf");
		testFileNotEmpty(directory, "resetSale.pdf");
		testFileNotEmpty(directory, "scanBarcode.pdf");
		testFileNotEmpty(directory, "startCashPayment.pdf");

		testFileNotEmpty(directory, "CallGraphInfo.txt");
	}

	private void testFileNotEmpty(File directory, String fileName) {
		assertTrue((new File(directory, fileName)).length() > 256);
	}

	/**
	 * Assures that the generated files, which contain ids, are bigger than the
	 * files, which do not contain ids.
	 */
	@Test
	public void testFileWithIdIsBigger() {
		File directory = outputDirectoryPath.toFile();
		File directory2 = outputDirectoryPath2.toFile();

		testFilesSizesDiffer(directory, directory2, "addDigitToBarcode.dot");
		testFilesSizesDiffer(directory, directory2, "clearBarcode.dot");
		testFilesSizesDiffer(directory, directory2, "enterCashAmount.dot");
		testFilesSizesDiffer(directory, directory2, "resetSale.dot");
		testFilesSizesDiffer(directory, directory2, "scanBarcode.dot");
		testFilesSizesDiffer(directory, directory2, "startCashPayment.dot");

		testFilesSizesDiffer(directory, directory2, "addDigitToBarcode.pdf");
		testFilesSizesDiffer(directory, directory2, "clearBarcode.pdf");
		testFilesSizesDiffer(directory, directory2, "enterCashAmount.pdf");
		testFilesSizesDiffer(directory, directory2, "resetSale.pdf");
		testFilesSizesDiffer(directory, directory2, "scanBarcode.pdf");
		testFilesSizesDiffer(directory, directory2, "startCashPayment.pdf");
	}

	private void testFilesSizesDiffer(File smallDirectory, File bigDirectory, String fileName) {
		File smallFile = (new File(smallDirectory, fileName));
		File bigFile = (new File(bigDirectory, fileName));
		assertTrue(smallFile.length() < bigFile.length());
	}

	private static void deleteOldFiles(Path outputDirectory) {
		for (File outputFile : outputDirectory.toFile().listFiles()) {
			outputFile.delete();
			assertFalse(outputFile.exists());
		}
	}

}
