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
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test instanciation of CityViews View to test EMF and Diagram, in File-System context
 * 
 * @author xtof, sylvain
 */
@RunWith(OrderedRunner.class)
public class TestEMFCityViewsInFSContext extends OpenflexoProjectAtRunTimeTestCase {

	private static VirtualModel cityViewsViewPoint;
	private static RepositoryFolder<FMLRTVirtualModelInstanceResource, ?> viewFolder;
	private static FMLRTVirtualModelInstance view;
	private static FlexoEditor editor;
	private static FlexoProject<File> project;
	private static FlexoProject<File> reloadedProject;

	/**
	 * Instantiate test resource center
	 */
	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager();
	}

	/**
	 * Test creating a view from Scratch.
	 */
	@Test
	@TestOrder(2)
	public void test1EMFCityViewsViewCreation() {

		log("test1EMFCityViewsViewCreation()");

		// CreateProject
		editor = createStandaloneProject("TestCreateView");
		project = (FlexoProject<File>) editor.getProject();
		assertNotNull(project.getVirtualModelInstanceRepository());

		// Load CityMapping ViewPoint
		cityViewsViewPoint = loadViewPoint("http://www.openflexo.org/cityviews");
		assertNotNull(cityViewsViewPoint);
		System.out.println("Found view point in " + ((VirtualModelResource) cityViewsViewPoint.getResource()).getIODelegate().toString());

		// Create View Folder
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		RepositoryFolder<FMLRTVirtualModelInstanceResource, ?> viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(((File) viewFolder.getSerializationArtefact()).exists());

		// Create View
		CreateBasicVirtualModelInstance addView = CreateBasicVirtualModelInstance.actionType.makeNewAction(viewFolder, null, editor);
		addView.setNewVirtualModelInstanceName("TestNewView");
		addView.setNewVirtualModelInstanceTitle("A nice title for a new view");
		addView.setVirtualModel(cityViewsViewPoint);
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		view = addView.getNewVirtualModelInstance();
		assertNotNull(view);
		System.out.println(
				"New view " + view + " created in " + ((FMLRTVirtualModelInstanceResource) view.getResource()).getIODelegate().toString());
		assertEquals(addView.getNewVirtualModelInstanceName(), view.getName());
		assertEquals(addView.getNewVirtualModelInstanceTitle(), view.getTitle());
		assertEquals(addView.getVirtualModel(), cityViewsViewPoint);
		assertTrue(((FMLRTVirtualModelInstanceResource) view.getResource()).getIODelegate().exists());

		// Reload Project
		FlexoEditor editor1 = reloadProject(project);
		reloadedProject = (FlexoProject<File>) editor1.getProject();
		// NOTE: this is strange => ask Syl
		assertTrue(editor1 != editor);
		assertTrue(reloadedProject != project);
		assertNotNull(reloadedProject.getVirtualModelInstanceRepository());
		assertEquals(1, reloadedProject.getVirtualModelInstanceRepository().getRootFolder().getChildren().size());
		viewFolder = reloadedProject.getVirtualModelInstanceRepository().getRootFolder().getChildren().get(0);
		assertEquals(1, viewFolder.getResources().size());
		FMLRTVirtualModelInstanceResource viewRes = viewFolder.getResources().get(0);
		assertEquals(viewRes, reloadedProject.getVirtualModelInstanceRepository().getResource(viewRes.getURI()));
		assertNotNull(viewRes);
		assertFalse(viewRes.isLoaded());
		view = viewRes.getVirtualModelInstance();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(reloadedProject.getDelegateResourceCenter(),
				((FMLRTVirtualModelInstanceResource) view.getResource()).getResourceCenter());
	}

	@Test
	@TestOrder(3)
	public void test3CreateVirtualModelInstance() {

		log("test3CreateVirtualModelInstance()");

		System.out.println("Create virtual model instance, view=" + view + " editor=" + editor);

		CreateBasicVirtualModelInstance createVirtualModelInstance = CreateBasicVirtualModelInstance.actionType.makeNewAction(view, null,
				editor);
		createVirtualModelInstance.setNewVirtualModelInstanceName("TestNewVirtualModel");
		createVirtualModelInstance.setNewVirtualModelInstanceTitle("A nice title for a new virtual model instance");

		VirtualModel cityView1VM = cityViewsViewPoint.getVirtualModelNamed("City1_View");
		assertNotNull(cityView1VM);

		createVirtualModelInstance.setVirtualModel(cityView1VM);

		createVirtualModelInstance.doAction();
		System.out.println("exception thrown=" + createVirtualModelInstance.getThrownException());
		assertTrue(createVirtualModelInstance.hasActionExecutionSucceeded());
		FMLRTVirtualModelInstance newVirtualModelInstance = createVirtualModelInstance.getNewVirtualModelInstance();
		System.out.println("New FMLRTVirtualModelInstance " + newVirtualModelInstance + " created in "
				+ ((FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().toString());
		assertNotNull(newVirtualModelInstance);
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceName(), newVirtualModelInstance.getName());
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceTitle(), newVirtualModelInstance.getTitle());
		assertEquals(createVirtualModelInstance.getVirtualModel(), cityView1VM);
		assertTrue(((FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().exists());
		assertEquals(reloadedProject.getDelegateResourceCenter(),
				((FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource()).getResourceCenter());

		for (ModelSlot ms : cityView1VM.getModelSlots()) {

			if (ms instanceof EMFModelSlot) {
				// EMFModelSlot emfModelSlot1 = (EMFModelSlot) ms;
				EMFModelResource modelResource1 = (EMFModelResource) project.getServiceManager().getResourceManager()
						.getModelWithURI("http://openflexo.org/integration-tests/TestResourceCenter/EMF/Model/city1/my.city1");
				assertNotNull(modelResource1);
				newVirtualModelInstance.setFlexoPropertyValue(ms, modelResource1);
			}
		}

	}

	private VirtualModel loadViewPoint(String viewPointURI) {
		log("Testing ViewPoint loading: " + viewPointURI);
		VirtualModelResource viewPointResource = (VirtualModelResource) serviceManager.getResourceManager().getResource(viewPointURI);
		assertNotNull(viewPointResource);
		assertFalse(viewPointResource.isLoaded());
		VirtualModel viewPoint = viewPointResource.getVirtualModel();
		assertTrue(viewPointResource.isLoaded());
		return viewPoint;
	}
}
