/**
 * 
 * Copyright (c) 2013-2014, Openflexo
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
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestBasicOntologyEditorView extends OpenflexoProjectAtRunTimeTestCase {

	public static FlexoProject<File> project;
	private static FlexoEditor editor;
	private static VirtualModel basicOntologyEditor;
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
		instanciateTestServiceManager(FMLTechnologyAdapter.class, FMLRTTechnologyAdapter.class, OWLTechnologyAdapter.class);

	}

	@Test
	@TestOrder(2)
	public void test1CreateProject() {
		editor = createStandaloneProject("TestCreateView");
		project = (FlexoProject<File>) editor.getProject();

		assertNotNull(project.getVirtualModelInstanceRepository());
	}

	private VirtualModel loadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		CompilationUnitResource vpRes = (CompilationUnitResource) serviceManager.getResourceManager().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		VirtualModel vp = vpRes.getCompilationUnit();
		assertTrue(vpRes.isLoaded());

		return vp;

	}

	@Test
	@TestOrder(3)
	public void test2LoadBasicOntologyEditorViewPoint() {
		basicOntologyEditor = loadViewPoint("http://www.openflexo.org/openflexo/ViewPoints/Basic/BasicOntologyEditor.fml");
		assertNotNull(basicOntologyEditor);
		System.out.println("Found view point in " + ((CompilationUnitResource) basicOntologyEditor.getResource()).getIODelegate().toString());
	}

	@Test
	@TestOrder(4)
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
	@TestOrder(5)
	public void test4CreateView() {
		CreateBasicVirtualModelInstance addView = CreateBasicVirtualModelInstance.actionType.makeNewAction(viewFolder, null, editor);
		addView.setNewVirtualModelInstanceName("TestNewView");
		addView.setNewVirtualModelInstanceTitle("A nice title for a new view");
		addView.setVirtualModel(basicOntologyEditor);
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		FMLRTVirtualModelInstance newView = addView.getNewVirtualModelInstance();
		System.out.println("New view " + newView + " created in "
				+ ((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().toString());
		assertNotNull(newView);
		assertEquals(addView.getNewVirtualModelInstanceName(), newView.getName());
		assertEquals(addView.getNewVirtualModelInstanceTitle(), newView.getTitle());
		assertEquals(addView.getVirtualModel(), basicOntologyEditor);
		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
	}

	@Test
	@TestOrder(6)
	public void test5ReloadProject() {
		editor = reloadProject(project);
		project = (FlexoProject<File>) editor.getProject();
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
		assertEquals(project.getDelegateResourceCenter(), ((FMLRTVirtualModelInstanceResource) view.getResource()).getResourceCenter());
	}

	/*public void test6CreateVirtualModel() {
		CreateVirtualModelInstance createVirtualModelInstance = CreateVirtualModelInstance.actionType.makeNewAction(view, null, editor);
		createVirtualModelInstance.newVirtualModelInstanceName = "TestNewVirtualModel";
		createVirtualModelInstance.newVirtualModelInstanceTitle = "A nice title for a new virtual model instance";
		createVirtualModelInstance.virtualModel = basicOntologyEditor;
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + newView.getResource().getFile());
		assertNotNull(newView);
		assertEquals(addView.newViewName, newView.getName());
		assertEquals(addView.newViewTitle, newView.getTitle());
		assertEquals(addView.viewpoint, basicOntologyEditor);
		assertTrue(newView.getResource().getFile().exists());
	}*/

	public void test6CreateDiagram() {
		System.out.println("Create diagram, view=" + view + " editor=" + editor);
		System.out.println("editor project = " + editor.getProject());
		DiagramTechnologyAdapter diagramTA = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
		RepositoryFolder<DiagramResource, ?> diagramFolder = diagramTA.getDiagramRepository(editor.getProject()).getRootFolder();
		CreateDiagram createDiagram = CreateDiagram.actionType.makeNewAction(diagramFolder, null, editor);
		createDiagram.setDiagramName("TestNewDiagram");
		createDiagram.setDiagramTitle("A nice title for a new diagram");
		// createDiagram.setDiagramSpecification(basicOntologyEditor.getDefaultDiagramSpecification());
		createDiagram.doAction();
		System.out.println("exception thrown=" + createDiagram.getThrownException());
		// createDiagram.getThrownException().printStackTrace();
		assertTrue(createDiagram.hasActionExecutionSucceeded());
		Diagram newDiagram = createDiagram.getNewDiagram();
		System.out.println(
				"New diagram " + newDiagram + " created in " + ((DiagramResource) newDiagram.getResource()).getIODelegate().toString());
		assertNotNull(newDiagram);
		assertEquals(createDiagram.getDiagramName(), newDiagram.getName());
		assertEquals(createDiagram.getDiagramTitle(), newDiagram.getTitle());
		// assertEquals(createDiagram.getDiagramSpecification(), basicOntologyEditor.getDefaultDiagramSpecification());
		assertTrue(((DiagramResource) newDiagram.getResource()).getIODelegate().exists());
	}
}
