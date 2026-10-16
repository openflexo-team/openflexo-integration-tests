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

package org.openflexo.foundation.fml.digitaltwin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Checks that the DigitalTwinDemo "ProductionLine" VirtualModel is FML-valid, i.e. that loading it in the
 * interactive tool raises no validation error. The J0-J6 {@code .fmlscript} suite proves business
 * values by execution but never runs the {@link ValidationModel} over the compilation unit, so this
 * test closes that gap: it runs the very same {@code FMLValidationModel} the interactive tool uses
 * and asserts zero errors (warnings such as "no deletion scheme" are tolerated).
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class DigitalTwinValidationTest extends OpenflexoTestCase {

	static final String VM_URI = "http://openflexo.org/digital-twin-demo/FML/ProductionLine.fml";

	static VirtualModel vm;

	@Test
	@TestOrder(1)
	public void loadVM() throws Exception {
		instanciateTestServiceManager(XMLTechnologyAdapter.class, ExcelTechnologyAdapter.class);
		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		vm = vpLib.getVirtualModel(VM_URI);
		assertNotNull("ProductionLine VM not found by URI " + VM_URI, vm);
	}

	@Test
	@TestOrder(2)
	public void virtualModelIsFMLValid() throws InterruptedException {
		ValidationModel validationModel = vm.getVirtualModelLibrary().getFMLValidationModel();
		ValidationReport report = validationModel.validate(vm.getCompilationUnit());

		System.out.println("Validation of " + vm.getName() + ": errors=" + report.getErrorsCount()
				+ " warnings=" + report.getWarningsCount());
		for (ValidationIssue<?, ?> issue : report.getAllIssues()) {
			String kind = issue instanceof ValidationError ? "ERROR" : issue instanceof ValidationWarning ? "WARNING" : "INFO";
			System.out.println("  [" + kind + "] " + validationModel.localizedIssueMessage(issue)
					+ "  >> " + describe(issue.getValidable()));
		}

		assertEquals("ProductionLine VM must load without FML validation error", 0, report.getErrorsCount());
	}

	private static String describe(Object o) {
		if (o instanceof FMLObject) {
			return o.getClass().getSimpleName() + " : " + ((FMLObject) o).getStringRepresentation();
		}
		return String.valueOf(o);
	}
}
