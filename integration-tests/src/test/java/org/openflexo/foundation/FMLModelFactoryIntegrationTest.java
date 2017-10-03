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

package org.openflexo.foundation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.ModelContext;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.MissingImplementationException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test instanciation of FMLModelFactory<br>
 * Here the model factory is instanciated with all known technology adapters
 * 
 */
@RunWith(OrderedRunner.class)
public class FMLModelFactoryIntegrationTest extends OpenflexoTestCase {

	private static final Logger logger = FlexoLogger.getLogger(FMLModelFactoryIntegrationTest.class.getPackage().getName());

	/**
	 * Instanciate test ServiceManager
	 */
	@Test
	@TestOrder(1)
	public void initializeServiceManager() {
		log("initializeServiceManager()");
		instanciateTestServiceManager();

		logger.info("services: " + serviceManager.getRegisteredServices());

		assertNotNull(serviceManager.getService(FlexoResourceCenterService.class));
		assertNotNull(serviceManager.getService(TechnologyAdapterService.class));

		TechnologyAdapterService taService = serviceManager.getTechnologyAdapterService();
		assertEquals(taService, serviceManager.getService(TechnologyAdapterService.class));

		assertNotNull(taService.getTechnologyAdapter(FMLTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(DiagramTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(EMFTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(ExcelTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(OWLTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(PowerpointTechnologyAdapter.class));
		assertNotNull(taService.getTechnologyAdapter(XMLTechnologyAdapter.class));
	}

	private void testVirtualModelModelFactoryWithTechnologyAdapter(TechnologyAdapter ta) {
		assertNotNull(ta);
		try {
			System.out.println("Instanciating FMLModelFactory");
			TechnologyAdapterService taService = DefaultTechnologyAdapterService.getNewInstance(null);
			taService.addToTechnologyAdapters(ta);
			FMLModelFactory factory = new FMLModelFactory(null, serviceManager, taService);
			ModelContext modelContext = factory.getModelContext();
			for (Iterator<ModelEntity> it = modelContext.getEntities(); it.hasNext();) {
				ModelEntity e = it.next();
				System.out.println("> Found " + e.getImplementedInterface());
			}
			for (Class<?> modelSlotClass : ta.getAvailableModelSlotTypes()) {
				assertNotNull(factory.getModelContext().getModelEntity(modelSlotClass));
			}
			factory.checkMethodImplementations();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (MissingImplementationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	/**
	 * Check the presence of FMLTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(2)
	public void checkFMLTechnologyAdapter() {
		log("checkFMLTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class));

	}

	/**
	 * Check the presence of DiagramTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(3)
	public void checkDiagramTechnologyAdapter() {
		log("checkDiagramTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class));
	}

	/**
	 * Check the presence of EMFTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(4)
	public void checkEMFTechnologyAdapter() {
		log("checkEMFTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class));
	}

	/**
	 * Check the presence of ExcelTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(5)
	public void checkExcelTechnologyAdapter() {
		log("checkExcelTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(ExcelTechnologyAdapter.class));
	}

	/**
	 * Check the presence of OWLTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(6)
	public void checkOWLTechnologyAdapter() {
		log("checkOWLTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class));
	}

	/**
	 * Check the presence of PowerpointTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(7)
	public void checkPowerpointTechnologyAdapter() {
		log("checkPowerpointTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(PowerpointTechnologyAdapter.class));
	}

	/**
	 * Check the presence of XMLTechnologyAdapter, instanciate FMLModelFactory with this TA
	 */
	@Test
	@TestOrder(8)
	public void checkXMLTechnologyAdapter() {
		log("checkXMLTechnologyAdapter()");

		testVirtualModelModelFactoryWithTechnologyAdapter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class));
	}

	/**
	 * 
	 */
	@Test
	@TestOrder(10)
	public void testInstantiateVirtualModelModelFactoryForAllTechnologyAdapters() {
		try {
			System.out.println("Instanciating ViewPointModelFactory");
			FMLModelFactory factory = new FMLModelFactory(null, serviceManager);
			ModelContext modelContext = factory.getModelContext();
			for (Iterator<ModelEntity> it = modelContext.getEntities(); it.hasNext();) {
				ModelEntity e = it.next();
				System.out.println("> Found " + e.getImplementedInterface());
			}
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
