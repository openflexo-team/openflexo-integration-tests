/**
 * 
 * Copyright (c) 2015-2015, Openflexo
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

import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.SynchronizationScheme;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateViewInFolder;
import org.openflexo.foundation.fml.rt.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.foundation.fml.rt.rm.ViewResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlotInstanceConfiguration;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


import static org.junit.Assert.*;

@RunWith(OrderedRunner.class)
public class TestCityMappingViewBigModels extends OpenflexoProjectAtRunTimeTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;
	private static ViewPoint cityMappingVP;
	private static RepositoryFolder<ViewResource, ?> viewFolder;
	private static View view;

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// We are connected directely to the resource center embedded in a jar in the classpath
		// We use the ResourceCenter deployed in integration-tests-rc
		instanciateTestServiceManager(
				FMLTechnologyAdapter.class, FMLRTTechnologyAdapter.class, EMFTechnologyAdapter.class
		);
	}

	@Test
	@TestOrder(2)
	public void test1CreateProject() {
		editor = createProject("TestCreateView");
		project = editor.getProject();

		assertNotNull(project.getViewLibrary());
	}

	@Test
	@TestOrder(3)
	public void loadViewPoint() {
		String viewPointURI = "http://www.thalesgroup.com/openflexo/emf/CityMapping";
		log("Testing ViewPoint loading: " + viewPointURI);

		ViewPointResource vpRes = (ViewPointResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertTrue(vpRes.isLoaded());
		cityMappingVP = vp;

	}

	@Test
	@TestOrder(4)
	public void test2LoadCityMappingViewPoint() {
		assertNotNull(cityMappingVP);
		System.out.println("Found view point in " + ((ViewPointResource) cityMappingVP.getResource()).getIODelegate().toString());

		VirtualModel cityMappingVM = cityMappingVP.getVirtualModelNamed("CityMapping");
		assertNotNull(cityMappingVM);

		SynchronizationScheme ss = cityMappingVM.getSynchronizationScheme();
		assertNotNull(ss);

	}

	@Test
	@TestOrder(5)
	public void test3CreateViewFolder() {
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType.makeNewAction(project.getViewLibrary().getRootFolder(),
				null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(((File) viewFolder.getSerializationArtefact()).exists());
	}

	@Test
	@TestOrder(6)
	public void test4CreateView() {
		CreateViewInFolder addView = CreateViewInFolder.actionType.makeNewAction(viewFolder, null, editor);
		addView.setNewViewName("TestNewView");
		addView.setNewViewTitle("A nice title for a new view");
		addView.setViewpointResource((ViewPointResource) cityMappingVP.getResource());
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + ((ViewResource) newView.getResource()).getIODelegate().toString());
		assertNotNull(newView);
		assertEquals(addView.getNewViewName(), newView.getName());
		assertEquals(addView.getNewViewTitle(), newView.getTitle());
		assertEquals(addView.getViewpointResource().getViewPoint(), cityMappingVP);
		assertTrue(((ViewResource) newView.getResource()).getIODelegate().exists());
	}

	@Test
	@TestOrder(7)
	public void test5ReloadProject() {
		editor = reloadProject(project.getProjectDirectory());
		project = editor.getProject();
		assertNotNull(project.getViewLibrary());
		assertEquals(1, project.getViewLibrary().getRootFolder().getChildren().size());
		viewFolder = project.getViewLibrary().getRootFolder().getChildren().get(0);
		assertEquals(1, viewFolder.getResources().size());
		ViewResource viewRes = viewFolder.getResources().get(0);
		assertEquals(viewRes, project.getViewLibrary().getResource(viewRes.getURI()));
		assertNotNull(viewRes);
		assertFalse(viewRes.isLoaded());
		view = viewRes.getView();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project, ((ViewResource) view.getResource()).getResourceCenter());
	}

	@Test
	@TestOrder(8)
	public void test6CreateVirtualModelInstance() {
		System.out.println("Create virtual model instance, view=" + view + " editor=" + editor);

		CreateBasicVirtualModelInstance createVirtualModelInstance = CreateBasicVirtualModelInstance.actionType.makeNewAction(view, null,
				editor);
		createVirtualModelInstance.setNewVirtualModelInstanceName("TestNewVirtualModel");
		createVirtualModelInstance.setNewVirtualModelInstanceTitle("A nice title for a new virtual model instance");

		VirtualModel cityMappingVM = cityMappingVP.getVirtualModelNamed("CityMapping");
		assertNotNull(cityMappingVM);

		createVirtualModelInstance.setVirtualModel(cityMappingVM);

		ModelSlot emfModelSlot1 = cityMappingVM.getModelSlots().get(0);
		TypeAwareModelSlotInstanceConfiguration emfModelSlotConfiguration1 = (TypeAwareModelSlotInstanceConfiguration) createVirtualModelInstance
				.getModelSlotInstanceConfiguration(emfModelSlot1);
		emfModelSlotConfiguration1.setOption(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		FlexoModelResource<?, ?, ?, ?> modelResource1 = project.getServiceManager().getResourceManager().getModelWithURI(
				"http://openflexo.org/integration-tests/TestResourceCenter/ViewPointsOpenflexo17/EMF/Model/city1/generated_10khTry8.city1");
		assertNotNull(modelResource1);
		emfModelSlotConfiguration1.setModelResource(modelResource1);
		assertTrue(emfModelSlotConfiguration1.isValidConfiguration());

		ModelSlot emfModelSlot2 = cityMappingVM.getModelSlots().get(1);
		TypeAwareModelSlotInstanceConfiguration emfModelSlotConfiguration2 = (TypeAwareModelSlotInstanceConfiguration) createVirtualModelInstance
				.getModelSlotInstanceConfiguration(emfModelSlot2);
		emfModelSlotConfiguration2.setOption(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		FlexoModelResource<?, ?, ?, ?> modelResource2 = project.getServiceManager().getResourceManager().getModelWithURI(
				"http://openflexo.org/integration-tests/TestResourceCenter/ViewPointsOpenflexo17/EMF/Model/city2/generated_10khTry8.city2");
		assertNotNull(modelResource2);
		emfModelSlotConfiguration2.setModelResource(modelResource2);
		assertTrue(emfModelSlotConfiguration2.isValidConfiguration());

		// TODO : Fix Performance Issue with SelectEMFObjectIndividual

		// createVirtualModelInstance.doAction();
		/*
		System.out.println("exception thrown=" + createVirtualModelInstance.getThrownException());
		assertTrue(createVirtualModelInstance.hasActionExecutionSucceeded());
		VirtualModelInstance newVirtualModelInstance = createVirtualModelInstance.getNewVirtualModelInstance();
		System.out.println("New VirtualModelInstance " + newVirtualModelInstance + " created in "
				+ ((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().toString());
		assertNotNull(newVirtualModelInstance);
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceName(), newVirtualModelInstance.getName());
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceTitle(), newVirtualModelInstance.getTitle());
		assertEquals(createVirtualModelInstance.getVirtualModel(), cityMappingVM);
		assertTrue(((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().exists());
		assertEquals(project, ((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getProject());
		assertEquals(project, newVirtualModelInstance.getProject());
		
		FlexoConcept cityEP = cityMappingVM.getFlexoConcept("City");
		FlexoConcept houseEP = cityMappingVM.getFlexoConcept("House");
		FlexoConcept appartmentEP = cityMappingVM.getFlexoConcept("Appartment");
		FlexoConcept mansionEP = cityMappingVM.getFlexoConcept("Mansion");
		FlexoConcept residentEP = cityMappingVM.getFlexoConcept("Resident");
		
		assertNotNull(cityEP);
		assertNotNull(houseEP);
		assertNotNull(appartmentEP);
		assertNotNull(mansionEP);
		assertNotNull(residentEP);
		
		System.out.println("FCI: " + newVirtualModelInstance.getFlexoConceptInstances(cityEP));
		
		for (FlexoConceptInstance fci : newVirtualModelInstance.getFlexoConceptInstances(cityEP)) {
			System.out.println("> " + fci);
		}
		*/
		// newVirtualModelInstance.synchronize(editor);
		/*
				System.out.println("Les FCI2: " + newVirtualModelInstance.getFlexoConceptInstances(cityEP));
		
				assertEquals(5, newVirtualModelInstance.getFlexoConceptInstances(cityEP).size());
				assertEquals(3, newVirtualModelInstance.getFlexoConceptInstances(houseEP).size());
				assertEquals(2, newVirtualModelInstance.getFlexoConceptInstances(appartmentEP).size());
				assertEquals(1, newVirtualModelInstance.getFlexoConceptInstances(mansionEP).size());
				assertEquals(3, newVirtualModelInstance.getFlexoConceptInstances(residentEP).size());
		*/
	}
}
