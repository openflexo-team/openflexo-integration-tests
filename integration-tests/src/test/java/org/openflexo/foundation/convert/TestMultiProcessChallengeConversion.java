/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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

package org.openflexo.foundation.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.fml.controller.widget.fmleditor.FMLEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.project.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.validation.ValidationReport;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test {@link FMLEditor} component
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestMultiProcessChallengeConversion extends OpenflexoProjectAtRunTimeTestCase {

	public static final String PROJECT_URI = "http://www.openflexo.org/MULTIProcessChallenge";

	public static FMLCompilationUnit compilationUnit;
	
	@Test
	@TestOrder(3)
	public void loadAndConvertFMLResource() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		instanciateTestServiceManager();
		/*System.out.println("SM=" + serviceManager);
		FlexoResourceCenterService rcService = serviceManager.getResourceCenterService();
		for (FlexoResourceCenter<?> resourceCenter : rcService.getResourceCenters()) {
			System.out.println("> resourceCenter " + resourceCenter + " uri=" + resourceCenter.getDefaultBaseURI());
			for (FlexoResource<?> resource : resourceCenter.getAllResources()) {
				System.out.println(" > " + resource.getURI());
			}
		}*/

		FlexoProjectResource<?> projectResource = (FlexoProjectResource) serviceManager.getResourceManager().getResource(PROJECT_URI);
		System.out.println("Toutes les resources du projet:");

		System.out.println("projectResource=" + projectResource);

		FlexoEditor editor = loadProject(projectResource);
		FlexoProject<?> project = editor.getProject();

		System.out.println("Toutes les resources du projet:");
		for (FlexoResource<?> resource : project.getAllResources()) {
			System.out.println(" > " + resource.getURI());
		}

		CompilationUnitResource cuResource = (CompilationUnitResource) project
				.getResource("http://www.openflexo.org/MULTIProcessChallenge/MetaModel.fml");
		assertNotNull(cuResource);

		compilationUnit = cuResource.loadResourceData();
		assertNotNull(compilationUnit);

	}

	@Test
	@TestOrder(4)
	public void validate() {
		System.out.println("FML: " + compilationUnit.getFMLPrettyPrint());

		ValidationReport report = validate(compilationUnit);
		assertEquals(0, report.getAllErrors().size());
		
		// We don't save the resource, otherwise this test can be run only once !
		
	}

}
