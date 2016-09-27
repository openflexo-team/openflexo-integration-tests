/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Copyright (c) 2011-2012, AgileBirds
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
import org.openflexo.foundation.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.action.CreateViewInFolder;
import org.openflexo.foundation.fml.rt.rm.ViewResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test instanciation of City Mapping View with 2 EMF
 * 
 * @author gbesancon
 */
@RunWith(OrderedRunner.class)
public class TestEMFCityMappingView extends OpenflexoProjectAtRunTimeTestCase {

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

	/**
	 * Test creating Diagram and model from scratch.
	 */
	@Test
	@TestOrder(2)
	public void testEMFCityMapping() {
		// CreateProject
		FlexoEditor editor = createProject("TestCreateView");
		FlexoProject project = editor.getProject();
		assertNotNull(project.getViewLibrary());

		// Load CityMapping ViewPoint
		ViewPoint cityMappingViewPoint = loadViewPoint("http://www.thalesgroup.com/openflexo/emf/CityMapping");
		assertNotNull(cityMappingViewPoint);
		System.out
				.println("Found view point in " + ((ViewPointResource) cityMappingViewPoint.getResource()).getFlexoIODelegate().toString());

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
		addView.setViewpointResource((ViewPointResource) cityMappingViewPoint.getResource());
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + ((ViewResource) newView.getResource()).getFlexoIODelegate().toString());
		assertNotNull(newView);
		assertEquals(addView.getNewViewName(), newView.getName());
		assertEquals(addView.getNewViewTitle(), newView.getTitle());
		assertEquals(addView.getViewpointResource().getViewPoint(), cityMappingViewPoint);
		assertTrue(((ViewResource) newView.getResource()).getFlexoIODelegate().exists());

		// Reload Project
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
		View view = viewRes.getView();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project, ((ViewResource) view.getResource()).getResourceCenter());

		// CreateDiagram
		System.out.println("Create diagram, view=" + view + " editor=" + editor);
		System.out.println("editor project = " + editor.getProject());
		DiagramTechnologyAdapter diagramTA = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
		RepositoryFolder<DiagramResource, ?> diagramFolder = diagramTA.getDiagramRepository(editor.getProject()).getRootFolder();
		CreateDiagram createDiagram = CreateDiagram.actionType.makeNewAction(diagramFolder, null, editor);
		createDiagram.setDiagramName("TestNewDiagram");
		createDiagram.setDiagramTitle("A nice title for a new diagram");
		// TODO : rewrite all of this
		/*createDiagram.setDiagramSpecification(cityMappingViewPoint.getDefaultDiagramSpecification());
		// Populate modelSlots
		assertEquals(3, cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlots().size());
		// Model Slot Diagram
		ModelSlot diagramModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("diagram");
		assertNotNull(diagramModelSlot);
		DiagramModelSlotInstanceConfiguration diagramModelSlotConfiguration = (DiagramModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(diagramModelSlot);
		diagramModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.Autoconfigure);
		assertTrue(diagramModelSlotConfiguration.isValidConfiguration());
		// Model Slot city1
		ModelSlot city1ModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("city1");
		assertNotNull(city1ModelSlot);
		TypeAwareModelSlotInstanceConfiguration city1ModelSlotConfiguration = (TypeAwareModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(city1ModelSlot);
		city1ModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		city1ModelSlotConfiguration.setModelUri("http://www.thalesgroup.com/openflexo/emf/CityMapping/myCity1");
		city1ModelSlotConfiguration.setRelativePath("/");
		city1ModelSlotConfiguration.setFilename("city.city1");
		assertTrue(city1ModelSlotConfiguration.isValidConfiguration());
		// Model Slot city2
		ModelSlot city2ModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("city2");
		assertNotNull(city2ModelSlot);
		TypeAwareModelSlotInstanceConfiguration city2ModelSlotConfiguration = (TypeAwareModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(city2ModelSlot);
		city2ModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		city2ModelSlotConfiguration.setModelUri("http://www.thalesgroup.com/openflexo/emf/CityMapping/myCity2");
		city2ModelSlotConfiguration.setRelativePath("/");
		city2ModelSlotConfiguration.setFilename("city.city2");
		assertTrue(city2ModelSlotConfiguration.isValidConfiguration());
		// Do Action CreateDiagram.
		createDiagram.doAction();
		System.out.println("exception thrown=" + createDiagram.getThrownException());
		assertTrue(createDiagram.hasActionExecutionSucceeded());
		Diagram newDiagram = createDiagram.getNewDiagram();
		System.out.println("New diagram " + newDiagram + " created in " + newDiagram.getResource().getFile());
		assertNotNull(newDiagram);
		assertEquals(createDiagram.getNewVirtualModelInstanceName(), newDiagram.getName());
		assertEquals(createDiagram.getNewVirtualModelInstanceTitle(), newDiagram.getTitle());
		assertEquals(createDiagram.getDiagramSpecification(), cityMappingViewPoint.getDefaultDiagramSpecification());
		assertTrue(newDiagram.getResource().getFile().exists());
		assertEquals(project, newDiagram.getResource().getProject());
		assertEquals(project, newDiagram.getProject());
		
		// Test ModelSlotInstance well created and initialized
		assertEquals(3, newDiagram.getModelSlotInstances().size());
		ModelSlotInstance<?, ?> diagramModelSlotInstance = newDiagram.getModelSlotInstance("diagram");
		assertNotNull(diagramModelSlotInstance);
		FlexoOntologyModelSlotInstance<?, ?, ?> city1ModelSlotInstance = (FlexoOntologyModelSlotInstance<?, ?, ?>) newDiagram
				.getModelSlotInstance("city1");
		assertNotNull(city1ModelSlotInstance);
		FlexoOntologyModelSlotInstance<?, ?, ?> city2ModelSlotInstance = (FlexoOntologyModelSlotInstance<?, ?, ?>) newDiagram
				.getModelSlotInstance("city2");
		assertNotNull(city2ModelSlotInstance);
		// System.out.println("DiagramModel=" + diagramModelSlotInstance.getModelURI());
		System.out.println("City1Model=" + city1ModelSlotInstance.getModelURI());
		System.out.println("City2Model=" + city2ModelSlotInstance.getModelURI());
		assertNotNull(city1ModelSlotInstance.getModelURI());
		assertNotNull(city2ModelSlotInstance.getModelURI());
		assertNotNull(city1ModelSlotInstance.getModel());
		assertNotNull(city2ModelSlotInstance.getModel());
		
		// Populate Diagram
		DiagramSpecification diagramSpecification = cityMappingViewPoint.getDefaultDiagramSpecification();
		assertEquals(6, diagramSpecification.getFlexoConcepts().size());
		FlexoConcept cityFlexoConcept = diagramSpecification.getFlexoConcept("City");
		Vector<DropScheme> cityDropSchemes = cityFlexoConcept.getDropSchemes();
		assertEquals(1, cityDropSchemes.size());
		
		DropSchemeAction cityDropSchemeAction = DropSchemeAction.actionType.makeNewAction(createDiagram.getNewDiagram().getRootPane(),
				null, editor);
		DropScheme cityDropScheme = cityDropSchemes.get(0);
		cityDropSchemeAction.setDropScheme(cityDropScheme);
		FlexoBehaviourParameter cityNameParameter = cityDropScheme.getParameter("name");
		assertNotNull(cityNameParameter);
		cityDropSchemeAction.setParameterValue(cityNameParameter, "Brest");
		FlexoBehaviourParameter cityCity1UriParameter = cityDropScheme.getParameter("city1Uri");
		assertNotNull(cityCity1UriParameter);
		cityDropSchemeAction.setParameterValue(cityCity1UriParameter, "city1");
		FlexoBehaviourParameter cityCity2UriParameter = cityDropScheme.getParameter("city2Uri");
		assertNotNull(cityCity2UriParameter);
		cityDropSchemeAction.setParameterValue(cityCity2UriParameter, "city2");
		assertEquals("Brest", cityDropSchemeAction.getParameterValue(cityNameParameter));
		assertEquals("city1", cityDropSchemeAction.getParameterValue(cityCity1UriParameter));
		assertEquals("city2", cityDropSchemeAction.getParameterValue(cityCity2UriParameter));
		cityDropSchemeAction.doAction();
		*/
	}

	private ViewPoint loadViewPoint(String viewPointURI) {
		log("Testing ViewPoint loading: " + viewPointURI);
		System.out.println("resourceCenter=" + resourceCenter);
		System.out.println("resourceCenter.getViewPointRepository()=" + resourceCenter.getViewPointRepository());
		ViewPointResource viewPointResource = resourceCenter.getViewPointRepository().getResource(viewPointURI);
		assertNotNull(viewPointResource);
		assertFalse(viewPointResource.isLoaded());
		ViewPoint viewPoint = viewPointResource.getViewPoint();
		assertTrue(viewPointResource.isLoaded());
		return viewPoint;
	}
}
