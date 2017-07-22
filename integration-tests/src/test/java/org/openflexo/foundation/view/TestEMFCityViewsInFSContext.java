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
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateViewInFolder;
import org.openflexo.foundation.fml.rt.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.foundation.fml.rt.rm.ViewResource;
import org.openflexo.foundation.fml.rt.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlotInstanceConfiguration;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test instanciation of CityViews View to test EMF and Diagram, in File-System context
 * 
 * @author xtof, sylvain
 */
@RunWith(OrderedRunner.class)
public class TestEMFCityViewsInFSContext extends OpenflexoProjectAtRunTimeTestCase {

	private static ViewPoint cityViewsViewPoint;
	private static RepositoryFolder<ViewResource, ?> viewFolder;
	private static View view;
	private static FlexoEditor editor;
	private static FlexoProject project;

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
		// CreateProject
		editor = createProject("TestCreateView");
		project = editor.getProject();
		assertNotNull(project.getViewLibrary());

		// Load CityMapping ViewPoint
		cityViewsViewPoint = loadViewPoint("http://www.openflexo.org/cityviews");
		assertNotNull(cityViewsViewPoint);
		System.out.println("Found view point in " + ((ViewPointResource) cityViewsViewPoint.getResource()).getIODelegate().toString());

		// Create View Folder
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType.makeNewAction(project.getViewLibrary().getRootFolder(),
				null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		RepositoryFolder<ViewResource, ?> viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(((File) viewFolder.getSerializationArtefact()).exists());

		// Create View
		CreateViewInFolder addView = CreateViewInFolder.actionType.makeNewAction(viewFolder, null, editor);
		addView.setNewViewName("TestNewView");
		addView.setNewViewTitle("A nice title for a new view");
		addView.setViewpointResource((ViewPointResource) cityViewsViewPoint.getResource());
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		view = addView.getNewView();
		System.out.println("New view " + view + " created in " + ((ViewResource) view.getResource()).getIODelegate().toString());
		assertNotNull(view);
		assertEquals(addView.getNewViewName(), view.getName());
		assertEquals(addView.getNewViewTitle(), view.getTitle());
		assertEquals(addView.getViewpointResource().getViewPoint(), cityViewsViewPoint);
		assertTrue(((ViewResource) view.getResource()).getIODelegate().exists());

		// Reload Project
		FlexoEditor editor1 = reloadProject(project.getProjectDirectory());
		FlexoProject project1 = editor1.getProject();
		// NOTE: this is strange => ask Syl
		assertTrue(editor1 != editor);
		assertTrue(project1 != project);
		assertNotNull(project1.getViewLibrary());
		assertEquals(1, project1.getViewLibrary().getRootFolder().getChildren().size());
		viewFolder = project1.getViewLibrary().getRootFolder().getChildren().get(0);
		assertEquals(1, viewFolder.getResources().size());
		ViewResource viewRes = viewFolder.getResources().get(0);
		assertEquals(viewRes, project1.getViewLibrary().getResource(viewRes.getURI()));
		assertNotNull(viewRes);
		assertFalse(viewRes.isLoaded());
		View view = viewRes.getView();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project1, ((ViewResource) view.getResource()).getResourceCenter());
	}

	@Test
	@TestOrder(3)
	public void test3CreateVirtualModelInstance() {

		System.out.println("Create virtual model instance, view=" + view + " editor=" + editor);

		CreateBasicVirtualModelInstance createVirtualModelInstance = CreateBasicVirtualModelInstance.actionType.makeNewAction(view, null,
				editor);
		createVirtualModelInstance.setNewVirtualModelInstanceName("TestNewVirtualModel");
		createVirtualModelInstance.setNewVirtualModelInstanceTitle("A nice title for a new virtual model instance");

		VirtualModel cityView1VM = cityViewsViewPoint.getVirtualModelNamed("City1_View");
		assertNotNull(cityView1VM);

		createVirtualModelInstance.setVirtualModel(cityView1VM);

		for (ModelSlot ms : cityView1VM.getModelSlots()) {

			if (ms instanceof EMFModelSlot) {
				EMFModelSlot emfModelSlot1 = (EMFModelSlot) ms;
				TypeAwareModelSlotInstanceConfiguration emfModelSlotConfiguration1 = (TypeAwareModelSlotInstanceConfiguration) createVirtualModelInstance
						.getModelSlotInstanceConfiguration(emfModelSlot1);
				emfModelSlotConfiguration1.setOption(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
				System.out.println("Searching http://openflexo.org/integration-tests/TestResourceCenter/EMF/Model/city1/my.city1");
				FlexoModelResource<?, ?, ?, ?> modelResource1 = project.getServiceManager().getResourceManager()
						.getModelWithURI("http://openflexo.org/integration-tests/TestResourceCenter/EMF/Model/city1/my.city1");
				assertNotNull(modelResource1);
				emfModelSlotConfiguration1.setModelResource(modelResource1);
				assertTrue(emfModelSlotConfiguration1.isValidConfiguration());
			}
			if (ms instanceof TypedDiagramModelSlot) {

				TypedDiagramModelSlot diagModelSlot1 = (TypedDiagramModelSlot) ms;

				TypedDiagramModelSlotInstanceConfiguration diagramModelSlotInstanceConfiguration = (TypedDiagramModelSlotInstanceConfiguration) createVirtualModelInstance
						.getModelSlotInstanceConfiguration(ms);
				assertNotNull(diagramModelSlotInstanceConfiguration);
				diagramModelSlotInstanceConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
				assertTrue(diagramModelSlotInstanceConfiguration.isValidConfiguration());

			}
		}

		createVirtualModelInstance.doAction();
		System.out.println("exception thrown=" + createVirtualModelInstance.getThrownException());
		assertTrue(createVirtualModelInstance.hasActionExecutionSucceeded());
		FMLRTVirtualModelInstance newVirtualModelInstance = createVirtualModelInstance.getNewVirtualModelInstance();
		System.out.println("New FMLRTVirtualModelInstance " + newVirtualModelInstance + " created in "
				+ ((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().toString());
		assertNotNull(newVirtualModelInstance);
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceName(), newVirtualModelInstance.getName());
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceTitle(), newVirtualModelInstance.getTitle());
		assertEquals(createVirtualModelInstance.getVirtualModel(), cityView1VM);
		assertTrue(((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().exists());
		assertEquals(project, ((VirtualModelInstanceResource) newVirtualModelInstance.getResource()).getResourceCenter());
	}

	private ViewPoint loadViewPoint(String viewPointURI) {
		log("Testing ViewPoint loading: " + viewPointURI);
		ViewPointResource viewPointResource = (ViewPointResource) serviceManager.getResourceManager().getResource(viewPointURI);
		assertNotNull(viewPointResource);
		System.out.println("Found ViewPoint in " + viewPointResource.getIODelegate().getSerializationArtefact());
		assertFalse(viewPointResource.isLoaded());
		ViewPoint viewPoint = viewPointResource.getViewPoint();
		assertTrue(viewPointResource.isLoaded());
		return viewPoint;
	}
}
