/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Integration-tests, a component of the software infrastructure 
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

package org.openflexo.foundation.viewpoint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestLoadViewPoints extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadViewPoints.class.getPackage().getName());

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// TODO: create a project where all those tests don't need a manual import of projects
		// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
		instanciateTestServiceManager();
	}

	private void testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		CompilationUnitResource vpRes = (CompilationUnitResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		VirtualModel vp = vpRes.getCompilationUnit().getVirtualModel();
		assertNotNull(vp);
		assertTrue(vpRes.isLoaded());

		System.out.println("Found " + vp);

		for (FlexoResource<?> r : vpRes.getContents()) {
			assertTrue(r instanceof CompilationUnitResource);
			CompilationUnitResource vmRes = (CompilationUnitResource) r;
			VirtualModel vm = vmRes.getCompilationUnit().getVirtualModel();
			assertNotNull(vm);
			assertTrue(vmRes.isLoaded());

			System.out.println("Loaded VirtualModel " + vm.getName());
			System.out.println(vm.getFMLRepresentation());

			assertVirtualModelIsValid(vm);
		}

		assertVirtualModelIsValid(vp);

	}

	@Test
	@TestOrder(2)
	public void test1LoadBasicOntology() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/Basic/BasicOntologyEditor.fml");
	}

	/*@Test
	@TestOrder(3)
	public void test2LoadBDN() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl");
	}
	
	@Test
	@TestOrder(4)
	public void test3LoadOC() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl");
	}
	
	@Test
	@TestOrder(5)
	public void test4LoadOM() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
	}
	
	@Test
	@TestOrder(6)
	public void test5LoadOUD() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
	}
	
	@Test
	@TestOrder(7)
	public void test6LoadSKOS() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
	}
	
	@Test
	@TestOrder(8)
	public void test7LoadUMLPackage() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/UML/PackageDiagram.owl");
	}
	
	@Test
	@TestOrder(9)
	public void test8LoadUMLUseCases() {
		testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
	}*/
}
