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
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestViewpoints extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestViewpoints.class.getPackage().getName());

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager();

	}

	private VirtualModel testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		VirtualModelResource vpRes = (VirtualModelResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		VirtualModel vp = vpRes.getVirtualModel();
		assertTrue(vpRes.isLoaded());

		return vp;

	}

	@Test
	@TestOrder(2)
	public void test1BasicOntologyEditor() {
		log("test1BasicOntologyEditor()");
		VirtualModel basicOntologyEditor = testLoadViewPoint(
				"http://www.openflexo.org/openflexo/ViewPoints/Basic/BasicOntologyEditor.fml");
		assertNotNull(basicOntologyEditor);
		System.out.println("Read resource " + ((VirtualModelResource) basicOntologyEditor.getResource()).getIODelegate().toString());

		assertVirtualModelIsValid(basicOntologyEditor);

	}

	@Test
	@TestOrder(3)
	public void test2BDN() {
		log("test2BDN()");
		assertVirtualModelIsValid(
				testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl"));
	}

	@Test
	@TestOrder(4)
	public void test3OrganizationalChart() {
		log("test3OrganizationalChart()");
		assertVirtualModelIsValid(
				testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl"));
	}

	@Test
	@TestOrder(5)
	public void test4OrganizationalMap() {
		log("test4OrganizationalMap()");
		assertVirtualModelIsValid(
				testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl"));
	}

	@Test
	@TestOrder(6)
	public void test5OrganizationalUnitDefinition() {
		log("test5OrganizationalUnitDefinition()");
		assertVirtualModelIsValid(
				testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl"));
	}

	@Test
	@TestOrder(7)
	public void test6SKOS() {
		log("test6SKOS()");
		assertVirtualModelIsValid(testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl"));
	}

	@Test
	@TestOrder(8)
	public void test7BasicOrganizationTreeEditor() {
		log("test7BasicOrganizationTreeEditor()");
		assertVirtualModelIsValid(testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/Tests/BasicOrganizationTreeEditor"));
	}

	@Test
	@TestOrder(9)
	public void test8UMLPackageDiagram() {
		log("test8UMLPackageDiagram()");
		assertVirtualModelIsValid(testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/UML/PackageDiagram.owl"));
	}

	@Test
	@TestOrder(10)
	public void test9UMLUCDiagram() {
		log("test9UMLUCDiagram()");
		assertVirtualModelIsValid(testLoadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/UML/UseCaseDiagram.owl"));
	}
}
