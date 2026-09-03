/**
 *
 * Copyright (c) 2026, Openflexo
 *
 * This file is part of OpenFlexo integration tests.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */
package org.openflexo.foundation.fml.multiprocess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationModel;
import org.openflexo.pamela.validation.ValidationReport;
import org.openflexo.pamela.validation.ValidationWarning;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Checks that the MultiProcessChallenge VirtualModels are FML-valid, i.e. that loading them in the
 * interactive tool raises no validation error.
 *
 * This matters more here than elsewhere: MetaModel was rewritten from a 2021 {@code .fml.xml}
 * serialization whose in-memory form carried 103 validation errors on this baseline. Zero errors is
 * the migration's acceptance criterion, and this test is what holds it.
 *
 * Neither VirtualModel mounts a model slot, so the service manager needs no technology adapter.
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class MultiProcessChallengeValidationTest extends OpenflexoTestCase {

	static final String METAMODEL_URI = "http://openflexo.org/multi-process-challenge/FML/MetaModel.fml";
	static final String ACME_URI = "http://openflexo.org/multi-process-challenge/FML/AcmeMetaModel.fml";
	static final String PROCESS_TYPE_EDITOR_URI = "http://openflexo.org/multi-process-challenge/FML/ProcessTypeEditor.fml";
	static final String PROCESS_EDITOR_URI = "http://openflexo.org/multi-process-challenge/FML/ProcessEditor.fml";

	static VirtualModelLibrary vmLibrary;

	@Test
	@TestOrder(1)
	public void loadServiceManager() throws Exception {
		instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		vmLibrary = serviceManager.getVirtualModelLibrary();
		assertNotNull(vmLibrary);
	}

	@Test
	@TestOrder(2)
	public void metaModelIsFMLValid() throws Exception {
		assertNoValidationError(METAMODEL_URI);
	}

	@Test
	@TestOrder(3)
	public void acmeMetaModelIsFMLValid() throws Exception {
		assertNoValidationError(ACME_URI);
	}

	@Test
	@TestOrder(4)
	public void processTypeEditorIsFMLValid() throws Exception {
		assertNoValidationError(PROCESS_TYPE_EDITOR_URI);
	}

	@Test
	@TestOrder(5)
	public void processEditorIsFMLValid() throws Exception {
		assertNoValidationError(PROCESS_EDITOR_URI);
	}

	private static void assertNoValidationError(String vmURI) throws Exception {
		VirtualModel vm = vmLibrary.getVirtualModel(vmURI);
		assertNotNull("VirtualModel not found by URI " + vmURI, vm);

		// A ParseException leaves an EMPTY compilation unit behind, which then validates with zero
		// errors - so "0 errors" alone proves nothing. Demand that the concepts actually got parsed.
		assertFalse("VirtualModel " + vm.getName() + " declares no concept: it most likely failed to PARSE "
				+ "(look for 'ParserException token:... line:N' in the log). A failed parse validates clean.",
				vm.getFlexoConcepts().isEmpty());

		ValidationModel validationModel = vmLibrary.getFMLValidationModel();
		ValidationReport report = validationModel.validate(vm.getCompilationUnit());

		System.out.println("Validation of " + vm.getName() + ": errors=" + report.getErrorsCount() + " warnings="
				+ report.getWarningsCount());
		for (ValidationIssue<?, ?> issue : report.getAllIssues()) {
			String kind = issue instanceof ValidationError ? "ERROR" : issue instanceof ValidationWarning ? "WARNING" : "INFO";
			System.out.println("  [" + kind + "] " + validationModel.localizedIssueMessage(issue) + "  >> "
					+ describe(issue.getValidable()));
		}

		assertEquals(vm.getName() + " must load without FML validation error", 0, report.getErrorsCount());
	}

	private static String describe(Object o) {
		if (o instanceof FMLObject) {
			return o.getClass().getSimpleName() + " : " + ((FMLObject) o).getStringRepresentation();
		}
		return String.valueOf(o);
	}
}
