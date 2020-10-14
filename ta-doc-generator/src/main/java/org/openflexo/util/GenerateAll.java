/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.util;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.gina.test.OpenflexoTestCaseWithGUI;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;

/**
 * Generate documentation for all TA
 * 
 */
public class GenerateAll extends OpenflexoTestCaseWithGUI {

	public static void main(String[] args) {
		ApplicationContext applicationContext = instanciateTestServiceManager(FMLTechnologyAdapter.class, FMLRTTechnologyAdapter.class,
				DiagramTechnologyAdapter.class);
		generateDocForTechnologyAdapter(FMLTechnologyAdapter.class, "openflexo-core", "flexo-foundation", "flexo-foundation",
				applicationContext);
		generateDocForTechnologyAdapter(FMLRTTechnologyAdapter.class, "openflexo-core", "flexo-foundation", "flexo-foundation",
				applicationContext);
		generateDocForTechnologyAdapter(DiagramTechnologyAdapter.class, "openflexo-diagram", "diagram-ta", "flexodiagram",
				applicationContext);
		// generateDocForTechnologyAdapter(OWLTechnologyAdapter.class, "openflexo-owl/owl-ta", applicationContext);
		System.exit(0);
	}

	private static <TA extends TechnologyAdapter<TA>> void generateDocForTechnologyAdapter(Class<TA> taClass, String repositoryName,
			String modelProjectName, String mvnArtefactName, ApplicationContext applicationContext) {
		TADocGenerator<?> generator = new TADocGenerator<>(taClass, repositoryName, modelProjectName, mvnArtefactName, applicationContext);
		generator.generate();
	}

}
