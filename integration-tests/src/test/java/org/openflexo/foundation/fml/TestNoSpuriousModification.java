package org.openflexo.foundation.fml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * CORE-D-45: loading, validating or pretty-printing a VirtualModel nobody edited must not mark its resource modified (the application
 * would offer to save it).
 */
@RunWith(OrderedRunner.class)
public class TestNoSpuriousModification extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestNoSpuriousModification.class.getPackage().getName());

	private static final List<CompilationUnitResource> resources = new ArrayList<>();

	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {
		log("test0InstantiateResourceCenter()");
		instanciateTestServiceManager();
	}

	/**
	 * Load every compilation unit of every resource center. None must be modified.
	 */
	@Test
	@TestOrder(2)
	public void test1Loading() throws Exception {
		log("test1Loading()");
		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			for (FlexoResource<?> r : rc.getAllResources()) {
				if (r instanceof CompilationUnitResource) {
					resources.add((CompilationUnitResource) r);
				}
			}
		}
		assertTrue("No compilation unit found", !resources.isEmpty());
		for (CompilationUnitResource r : resources) {
			r.getCompilationUnit();
		}
		System.out.println("Checked " + resources.size() + " compilation units");
		assertNoneModified("loading");
	}

	@Test
	@TestOrder(3)
	public void test2Validation() {
		log("test2Validation()");
		for (CompilationUnitResource r : resources) {
			VirtualModel vm = r.getCompilationUnit().getVirtualModel();
			if (vm != null) {
				validate(vm);
			}
		}
		assertNoneModified("validation");
	}

	@Test
	@TestOrder(4)
	public void test3PrettyPrint() {
		log("test3PrettyPrint()");
		for (CompilationUnitResource r : resources) {
			r.getCompilationUnit().getFMLPrettyPrint();
		}
		assertNoneModified("pretty-print");
	}

	private void assertNoneModified(String step) {
		// Not only the compilation units: printing a DiagramSpecification loads its example diagrams and palettes, which must not be
		// modified either
		List<String> modified = new ArrayList<>();
		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			for (FlexoResource<?> r : rc.getAllResources()) {
				if (r.isLoaded() && r.isModified()) {
					modified.add(r.getURI());
				}
			}
		}
		assertEquals("Resources modified after " + step + ": " + modified, 0, modified.size());
	}
}
