/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.foundation.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.SynchronizationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.binding.EditionActionBindingModel;
import org.openflexo.foundation.fml.binding.FlexoBehaviourBindingModel;
import org.openflexo.foundation.fml.controlgraph.ConditionalAction;
import org.openflexo.foundation.fml.controlgraph.IterationAction;
import org.openflexo.foundation.fml.controlgraph.Sequence;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.editionaction.DeclarationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.editionaction.FetchRequestCondition;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.editionaction.MatchingCriteria;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.fml.EMFObjectIndividualRole;
import org.openflexo.technologyadapter.emf.fml.editionaction.SelectEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestCityMappingBindingModel extends OpenflexoProjectAtRunTimeTestCase {

	// Unused private static FlexoEditor<?, ?, ?> editor;
	private static VirtualModel cityMappingVP;
	private static SynchronizationScheme syncScheme;

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void instantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// We are connected directely to the resource center embedded in a jar in the classpath
		// We use the ResourceCenter deployed in integration-tests-rc
		instanciateTestServiceManager(FMLTechnologyAdapter.class, FMLRTTechnologyAdapter.class, EMFTechnologyAdapter.class);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			System.out.println("rc: " + rc);
			for (FlexoResource<?> r : rc.getAllResources()) {
				System.out.println("  > " + r.getURI() + " " + r);
			}
		}

	}

	@Test
	@TestOrder(2)
	public void loadViewPoint() {
		String viewPointURI = "http://www.thalesgroup.com/openflexo/emf/CityMapping";
		log("Testing ViewPoint loading: " + viewPointURI);

		CompilationUnitResource vpRes = (CompilationUnitResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		VirtualModel vp = vpRes.getCompilationUnit().getVirtualModel();
		assertTrue(vpRes.isLoaded());
		cityMappingVP = vp;

		VirtualModel cityMapping = vp.getVirtualModelNamed("Mapping");
		assertNotNull(cityMapping);

	}

	@Test
	@TestOrder(3)
	public void checkSynchronizationScheme() {
		VirtualModel cityMapping = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMapping);

		syncScheme = cityMapping.getSynchronizationScheme();
		assertNotNull(syncScheme);

		System.out.println("FML=" + syncScheme.getFMLPrettyPrint());

		assertEquals(5, syncScheme.getBindingModel().getBindingVariablesCount());
		assertNotNull(syncScheme.getBindingModel().bindingVariableNamed("model1"));
		assertEquals(EMFModel.class, syncScheme.getBindingModel().bindingVariableNamed("model1").getType());
		assertNotNull(syncScheme.getBindingModel().bindingVariableNamed("model2"));
		assertEquals(EMFModel.class, syncScheme.getBindingModel().bindingVariableNamed("model2").getType());
		assertNotNull(syncScheme.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));

	}

	@Test
	@TestOrder(4)
	public void checkFetchRequestIteration() {

		assertTrue(syncScheme.getFlexoBehaviour().getControlGraph() instanceof Sequence);
		IterationAction iterationAction = (IterationAction) (((Sequence) syncScheme.getFlexoBehaviour().getControlGraph())
				.getControlGraph1());

		// System.out.println("IterationAction: " + iterationAction.getFMLPrettyPrint());

		assertEquals(5, iterationAction.getBindingModel().getBindingVariablesCount());
		assertEquals(6, iterationAction.getInferedBindingModel().getBindingVariablesCount());

		SelectEMFObjectIndividual fetchRequest1 = (SelectEMFObjectIndividual) iterationAction.getIterationAction();
		assertNotNull(fetchRequest1);

		/*SelectEMFObjectIndividual fetchRequest2 = (SelectEMFObjectIndividual) ((Sequence) iterationAction.getControlGraph())
				.getControlGraph1();
		assertNotNull(fetchRequest2);*/

		// assertSame(fetchRequest1, fetchRequest2);

		System.out.println("FML=" + syncScheme.getFlexoBehaviour().getFMLPrettyPrint());

		// System.out.println("fetchRequest1=" + fetchRequest1.getFMLPrettyPrint());

		assertEquals(5, fetchRequest1.getBindingModel().getBindingVariablesCount());

		// System.out.println("fetchRequest2=" + fetchRequest2.getFMLPrettyPrint());

		DeclarationAction<?> declaration = (DeclarationAction<?>) ((Sequence) iterationAction.getControlGraph()).getControlGraph1();
		SelectEMFObjectIndividual fetchRequest2 = (SelectEMFObjectIndividual) declaration.getAssignableAction();

		assertEquals(6, fetchRequest2.getBindingModel().getBindingVariablesCount());
		assertNotNull(fetchRequest2.getBindingModel().bindingVariableNamed("city1"));

		for (FetchRequestCondition c : fetchRequest2.getConditions()) {
			System.out.println("condition: " + c.getCondition() + " bm=" + c.getBindingModel());
			assertEquals(7, c.getBindingModel().getBindingVariablesCount());
			assertNotNull(c.getBindingModel().bindingVariableNamed(FetchRequestCondition.SELECTED));
			assertTrue(c.getCondition().isValid());
		}

	}

	@Test
	@TestOrder(4)
	public void checkMatchFlexoConceptInstance() {

		System.out.println("FML=" + syncScheme.getFlexoBehaviour().getFMLPrettyPrint());

		assertTrue(syncScheme.getFlexoBehaviour().getControlGraph() instanceof Sequence);
		IterationAction iterationAction = (IterationAction) (((Sequence) syncScheme.getFlexoBehaviour().getControlGraph())
				.getControlGraph1());

		// System.out.println("IterationAction: " + iterationAction.getFMLPrettyPrint());

		assertEquals(5, iterationAction.getBindingModel().getBindingVariablesCount());
		assertEquals(6, iterationAction.getInferedBindingModel().getBindingVariablesCount());

		/*for (int i = 0; i < iterationAction.getInferedBindingModel().getBindingVariablesCount(); i++) {
			System.out.println("1 / Variable at " + i + " = " + iterationAction.getInferedBindingModel().getBindingVariableAt(i));
		}*/

		Sequence seq = (Sequence) (((Sequence) iterationAction.getControlGraph()).getControlGraph2());

		ConditionalAction conditional = (ConditionalAction) seq.getControlGraph1();
		assertNotNull(conditional);

		MatchFlexoConceptInstance matchAction = (MatchFlexoConceptInstance) conditional.getThenControlGraph();
		assertNotNull(matchAction);
		assertTrue(matchAction.getBindingModel() instanceof EditionActionBindingModel);

		for (MatchingCriteria criteria : matchAction.getMatchingCriterias()) {
			// System.out.println("> Criteria: " + criteria.getValue());
			/*for (int i = 0; i < criteria.getBindingModel().getBindingVariablesCount(); i++) {
				System.out.println("2 / Variable at " + i + " = " + criteria.getBindingModel().getBindingVariableAt(i));
			}*/
			assertEquals(7, criteria.getBindingModel().getBindingVariablesCount());
			assertNotNull(criteria.getBindingModel().bindingVariableNamed("matchingCitiesInModel2"));
			assertTrue(criteria.getValue().isValid());
		}

	}

	@Test
	@TestOrder(5)
	public void checkConceptAndRolesBindingModel() {

		VirtualModel cityMapping = cityMappingVP.getVirtualModelNamed("Mapping");

		FlexoConcept fc = cityMapping.getFlexoConcept("City");

		FlexoRole<?> fcRole = (FlexoRole<?>) fc.getAccessibleProperty("cityInModel1");

		BindingModel fcRoleBindingModel = fcRole.getBindingModel();
		int nbBindVar = fcRoleBindingModel.getBindingVariablesCount();

		System.out.println(" \n\n***********************************");

		System.out.println(" \n****Binding Model");

		System.out.println(fcRoleBindingModel);

		System.out.println(" \n****Binding variables");

		for (int i = 0; i < nbBindVar; i++) {
			BindingVariable v = fcRoleBindingModel.getBindingVariableAt(i);
			System.out.println("BVar : " + v.getVariableName() + "[ " + v.getType().toString() + "]");
		}

		if (fcRole instanceof EMFObjectIndividualRole) {
			EMFObjectIndividualRole emfRole = (EMFObjectIndividualRole) fcRole;

			// Unused IFlexoOntologyClass aType =
			emfRole.getOntologicType();
		}

	}

	@Test
	@TestOrder(6)
	public void checkSynchronizeModel1FromModel2() {

		VirtualModel cityMapping = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMapping);

		ActionScheme actionScheme = (ActionScheme) cityMapping.getFlexoBehaviour("synchronizeModel1FromModel2");
		assertNotNull(actionScheme);

		System.out.println("FML=" + actionScheme.getFMLPrettyPrint());

		assertTrue(actionScheme.getControlGraph() instanceof Sequence);

		Sequence seq = (Sequence) actionScheme.getControlGraph();
		assertTrue(seq.getControlGraph1() instanceof DeclarationAction);
		assertTrue(seq.getControlGraph2() instanceof IterationAction);

		IterationAction iteration = (IterationAction) seq.getControlGraph2();

		assertTrue(iteration.getControlGraph() instanceof ExpressionAction);
	}

	@Test
	@TestOrder(7)
	public void checkSynchronizeModel2FromModel1() {

		VirtualModel cityMapping = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMapping);

		ActionScheme actionScheme = (ActionScheme) cityMapping.getFlexoBehaviour("synchronizeModel2FromModel1");
		assertNotNull(actionScheme);

		System.out.println("FML=" + actionScheme.getFMLPrettyPrint());

		assertTrue(actionScheme.getControlGraph() instanceof Sequence);

		Sequence seq = (Sequence) actionScheme.getControlGraph();
		assertTrue(seq.getControlGraph1() instanceof DeclarationAction);
		assertTrue(seq.getControlGraph2() instanceof IterationAction);

		IterationAction iteration = (IterationAction) seq.getControlGraph2();

		assertTrue(iteration.getControlGraph() instanceof ExpressionAction);
	}

	@Test
	@TestOrder(8)
	public void checkCityCreationScheme() {

		VirtualModel cityMapping = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMapping);

		FlexoConcept city = cityMapping.getFlexoConcept("City");
		assertNotNull(city);

		CreationScheme creationScheme = (CreationScheme) city.getFlexoBehaviour("createANewOne");

		System.out.println("FML=" + creationScheme.getFMLPrettyPrint());

		assertTrue(creationScheme.getControlGraph() instanceof Sequence);

		Sequence seq = (Sequence) creationScheme.getControlGraph();
		assertTrue(seq.getControlGraph1() instanceof AssignationAction);
		assertTrue(seq.getControlGraph2() instanceof AssignationAction);

		AssignationAction<?> assignation1 = (AssignationAction<?>) seq.getControlGraph1();
		AssignationAction<?> assignation2 = (AssignationAction<?>) seq.getControlGraph2();

		assertEquals("cityInModel1", assignation1.getAssignation().toString());
		assertEquals("cityInModel2", assignation2.getAssignation().toString());
	}

	@Test
	@TestOrder(10)
	public void checkViewPointValidity() {
		System.out.println("cityMappingVP FML=" + cityMappingVP.getFMLPrettyPrint());
		assertVirtualModelIsValid(cityMappingVP);
	}

}
