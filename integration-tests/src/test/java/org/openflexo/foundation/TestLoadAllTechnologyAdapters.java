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

import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test instanciation of FMLModelFactory<br>
 * Here the model factory is instanciated with all known technology adapters
 * 
 */
@RunWith(OrderedRunner.class)
public class TestLoadAllTechnologyAdapters extends OpenflexoTestCase {

	private static final Logger logger = FlexoLogger.getLogger(TestLoadAllTechnologyAdapters.class.getPackage().getName());

	private static TechnologyAdapterService taService;

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

		taService = serviceManager.getTechnologyAdapterService();
		assertEquals(taService, serviceManager.getService(TechnologyAdapterService.class));

	}

	/**
	 * Check the presence of FMLTechnologyAdapter
	 */
	@Test
	@TestOrder(2)
	public void checkVirtualModelTechnologyAdapter() {
		log("checkVirtualModelTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(FMLTechnologyAdapter.class));
	}

	/**
	 * Check the presence of DiagramTechnologyAdapter
	 */
	@Test
	@TestOrder(3)
	public void checkDiagramTechnologyAdapter() {
		log("checkDiagramTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(DiagramTechnologyAdapter.class));
	}

	/**
	 * Check the presence of EMFTechnologyAdapter
	 */
	@Test
	@TestOrder(4)
	public void checkEMFTechnologyAdapter() {
		log("checkEMFTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(EMFTechnologyAdapter.class));
	}

	/**
	 * Check the presence of ExcelTechnologyAdapter
	 */
	@Test
	@TestOrder(5)
	public void checkExcelTechnologyAdapter() {
		log("checkExcelTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(ExcelTechnologyAdapter.class));
	}

	/**
	 * Check the presence of OWLTechnologyAdapter
	 */
	@Test
	@TestOrder(6)
	public void checkOWLTechnologyAdapter() {
		log("checkOWLTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(OWLTechnologyAdapter.class));
	}

	/**
	 * Check the presence of PowerpointTechnologyAdapter
	 */
	@Test
	@TestOrder(7)
	public void checkPowerpointTechnologyAdapter() {
		log("checkPowerpointTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(PowerpointTechnologyAdapter.class));
	}

	/**
	 * Check the presence of XMLTechnologyAdapter
	 */
	@Test
	@TestOrder(8)
	public void checkXMLTechnologyAdapter() {
		log("checkXMLTechnologyAdapter()");

		assertNotNull(taService.getTechnologyAdapter(XMLTechnologyAdapter.class));
	}


}
