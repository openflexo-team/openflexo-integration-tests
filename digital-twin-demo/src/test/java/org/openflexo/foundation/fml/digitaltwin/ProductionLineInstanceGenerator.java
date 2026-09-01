/**
 *
 * Copyright (c) 2026, Openflexo
 *
 * This file is part of OpenFlexo integration tests.
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

package org.openflexo.foundation.fml.digitaltwin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.cli.CommandInterpreter;
import org.openflexo.foundation.fml.cli.ParseException;
import org.openflexo.foundation.fml.cli.command.FMLCommandExecutionException;
import org.openflexo.foundation.fml.cli.command.FMLScript;
import org.openflexo.foundation.fml.cli.test.FMLScriptParserTestCase;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

/**
 * Regenerates the serialized ProductionLine instance committed under {@code src/main/resources/INSTANCES}.
 *
 * <p>
 * This is a TOOL, not a test: it writes into the source tree, so it is {@link Ignore}d. Remove the annotation (or run it from the IDE) when
 * the instance has to be produced again - after a change to ProductionLine.fml, to production-line.xml or to maintenance.xlsx that alters
 * what the instance holds.
 *
 * <p>
 * The instance MUST be created inside the demo's own resource center, never in a sandbox one: an instance created in a temporary resource
 * center serializes its internal references as absolute {@code file:} URIs pointing at that temporary directory, and is therefore not
 * relocatable. Creating it here, with the command interpreter's working directory set to {@code <rc>/INSTANCES}, yields the portable
 * {@code http://openflexo.org/digital-twin-demo/INSTANCES/lineL2.fml.rt}.
 *
 * <p>
 * At run time the resource center is the classpath one ({@code build/resources/main}), so the generated resource is copied back into
 * {@code src/main/resources} - both share the same base URI, hence the same resource URIs.
 *
 * @author sylvain
 */
@Ignore("Tool: writes into src/main/resources. Remove this annotation to regenerate the instance.")
public class ProductionLineInstanceGenerator extends FMLScriptParserTestCase {

	static final String GENERATOR_SCRIPT = "Tools/GenerateProductionLineInstance.fmlscript";
	static final String INSTANCES_FOLDER = "INSTANCES";
	static final String INSTANCE_NAME = "lineL2.fml.rt";
	static final String BASE_URI = "http://openflexo.org/digital-twin-demo";

	@Test
	public void generateInstance() throws ModelDefinitionException, ParseException, IOException, FMLCommandExecutionException {

		instanciateTestServiceManager(XMLTechnologyAdapter.class, ExcelTechnologyAdapter.class);
		FlexoEditor editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

		File rcDirectory = demoResourceCenterDirectory();
		File instancesDirectory = new File(rcDirectory, INSTANCES_FOLDER);
		instancesDirectory.mkdirs();

		// Working directory inside the resource center => the new instance is created in that
		// repository folder, and gets a resource URI derived from the center's base URI.
		CommandInterpreter commandInterpreter = new CommandInterpreter(serviceManager, System.in, System.out, System.err,
				instancesDirectory);

		Resource scriptResource = ResourceLocator.locateResource(GENERATOR_SCRIPT);
		assertNotNull("Cannot find " + GENERATOR_SCRIPT, scriptResource);
		FMLScript script = parseFMLScript(scriptResource, commandInterpreter);
		checkFMLScript(GENERATOR_SCRIPT, script);
		script.execute();

		File generated = new File(instancesDirectory, INSTANCE_NAME);
		assertTrue("Instance was not written to " + generated, generated.exists());

		File sourceInstances = sourceInstancesDirectory(rcDirectory);
		File target = new File(sourceInstances, INSTANCE_NAME);
		FileUtils.deleteDir(target);
		FileUtils.copyDirToDir(generated, sourceInstances, CopyStrategy.REPLACE);
		System.out.println("Instance regenerated in " + target);
	}

	private File demoResourceCenterDirectory() {
		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			if (rc instanceof FileSystemBasedResourceCenter && BASE_URI.equals(rc.getDefaultBaseURI())) {
				return ((FileSystemBasedResourceCenter) rc).getRootDirectory();
			}
		}
		throw new IllegalStateException("Cannot find the digital-twin-demo resource center (base URI " + BASE_URI + ")");
	}

	/**
	 * The run-time resource center is build/resources/main; the instance belongs in the sources next to FML/.
	 */
	private File sourceInstancesDirectory(File rcDirectory) {
		File projectDirectory = rcDirectory.getParentFile().getParentFile().getParentFile();
		File sourceResources = new File(projectDirectory, "src/main/resources");
		assertTrue("Cannot locate src/main/resources from " + rcDirectory, sourceResources.isDirectory());
		File returned = new File(sourceResources, INSTANCES_FOLDER);
		returned.mkdirs();
		return returned;
	}
}
