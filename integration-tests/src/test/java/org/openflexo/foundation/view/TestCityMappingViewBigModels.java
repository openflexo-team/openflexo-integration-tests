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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.SynchronizationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestCityMappingViewBigModels extends OpenflexoProjectAtRunTimeTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;
	private static VirtualModel cityMappingVP;
	private static RepositoryFolder<FMLRTVirtualModelInstanceResource, ?> viewFolder;
	private static FMLRTVirtualModelInstance view;

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// We are connected directely to the resource center embedded in a jar in the classpath
		// We use the ResourceCenter deployed in integration-tests-rc
		instanciateTestServiceManager(FMLTechnologyAdapter.class, FMLRTTechnologyAdapter.class, EMFTechnologyAdapter.class);
	}

	@Test
	@TestOrder(2)
	public void test1CreateProject() {
		editor = createProject("TestCreateView");
		project = editor.getProject();

		assertNotNull(project.getVirtualModelInstanceRepository());
	}

	@Test
	@TestOrder(3)
	public void loadViewPoint() {
		String viewPointURI = "http://www.thalesgroup.com/openflexo/emf/CityMapping";
		log("Testing ViewPoint loading: " + viewPointURI);

		VirtualModelResource vpRes = (VirtualModelResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		VirtualModel vp = vpRes.getVirtualModel();
		assertTrue(vpRes.isLoaded());
		cityMappingVP = vp;

	}

	@Test
	@TestOrder(4)
	public void test2LoadCityMappingViewPoint() {
		assertNotNull(cityMappingVP);
		System.out.println("Found view point in " + ((VirtualModelResource) cityMappingVP.getResource()).getIODelegate().toString());

		VirtualModel cityMappingVM = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMappingVM);

		SynchronizationScheme ss = cityMappingVM.getSynchronizationScheme();
		assertNotNull(ss);

	}

	@Test
	@TestOrder(5)
	public void test3CreateViewFolder() {
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(((File) viewFolder.getSerializationArtefact()).exists());
	}

	@Test
	@TestOrder(6)
	public void test4CreateView() {
		CreateBasicVirtualModelInstance addView = CreateBasicVirtualModelInstance.actionType.makeNewAction(viewFolder, null, editor);
		addView.setNewVirtualModelInstanceName("TestNewView");
		addView.setNewVirtualModelInstanceTitle("A nice title for a new view");
		addView.setVirtualModel(cityMappingVP);
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		FMLRTVirtualModelInstance newView = addView.getNewVirtualModelInstance();
		System.out.println("New view " + newView + " created in "
				+ ((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().toString());
		assertNotNull(newView);
		assertEquals(addView.getNewVirtualModelInstanceName(), newView.getName());
		assertEquals(addView.getNewVirtualModelInstanceTitle(), newView.getTitle());
		assertEquals(addView.getVirtualModel(), cityMappingVP);
		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
	}

	@Test
	@TestOrder(7)
	public void test5ReloadProject() {
		editor = reloadProject(project.getProjectDirectory());
		project = editor.getProject();
		assertNotNull(project.getVirtualModelInstanceRepository());
		assertEquals(1, project.getVirtualModelInstanceRepository().getRootFolder().getChildren().size());
		viewFolder = project.getVirtualModelInstanceRepository().getRootFolder().getChildren().get(0);
		assertEquals(1, viewFolder.getResources().size());
		FMLRTVirtualModelInstanceResource viewRes = viewFolder.getResources().get(0);
		assertEquals(viewRes, project.getVirtualModelInstanceRepository().getResource(viewRes.getURI()));
		assertNotNull(viewRes);
		assertFalse(viewRes.isLoaded());
		view = viewRes.getVirtualModelInstance();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project, ((FMLRTVirtualModelInstanceResource) view.getResource()).getResourceCenter());
	}

	@Test
	@TestOrder(8)
	public void test6CreateVirtualModelInstance() {
		System.out.println("Create virtual model instance, view=" + view + " editor=" + editor);

		CreateBasicVirtualModelInstance createVirtualModelInstance = CreateBasicVirtualModelInstance.actionType.makeNewAction(view, null,
				editor);
		createVirtualModelInstance.setNewVirtualModelInstanceName("TestNewVirtualModel");
		createVirtualModelInstance.setNewVirtualModelInstanceTitle("A nice title for a new virtual model instance");

		VirtualModel cityMappingVM = cityMappingVP.getVirtualModelNamed("Mapping");
		assertNotNull(cityMappingVM);

		createVirtualModelInstance.setVirtualModel(cityMappingVM);

		// TODO : Fix Performance Issue with SelectEMFObjectIndividual

		// createVirtualModelInstance.doAction();
		/*
		System.out.println("exception thrown=" + createVirtualModelInstance.getThrownException());
		assertTrue(createVirtualModelInstance.hasActionExecutionSucceeded());
		FMLRTVirtualModelInstance newVirtualModelInstance = createVirtualModelInstance.getNewVirtualModelInstance();
		System.out.println("New FMLRTVirtualModelInstance " + newVirtualModelInstance + " created in "
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
