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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.cli.CommandInterpreter;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.ActionSchemeAction;
import org.openflexo.foundation.fml.rt.action.ActionSchemeActionFactory;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.fml.cli.command.FMLScript;
import org.openflexo.foundation.fml.cli.command.fml.FMLAssertException;
import org.openflexo.foundation.fml.cli.test.FMLScriptParserTestCase;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;

/**
 * Proves that an edit made to the workbook OUTSIDE Openflexo is picked up without restarting: the demo's
 * {@code refresh()} behaviour re-reads the file and re-attaches the structure, and every derived value follows.
 *
 * <p>
 * The edit is made with POI straight on the file, deliberately bypassing Openflexo - that is the whole point,
 * an edit going through the platform would need no refresh.
 *
 * <p>
 * It runs on a THROWAWAY COPY of maintenance.xlsx, created before the service manager starts so the resource
 * center picks it up, and deleted afterwards. Tests run with {@code maxParallelForks = 4}, and J1-J7 read the
 * real workbook: editing that one here would make them flaky.
 *
 * <p>
 * The instance is set up by an FML script, but everything after the external edit is done in Java. A second script
 * would have been more readable, and does not work: script variables survive from one script to the next (same
 * {@link CommandInterpreter}) but their STATIC TYPE does not - each script is validated on its own, so {@code line}
 * is untyped there and {@code line.getEquipmentByCode(...)} is rejected as an unresolved path element before
 * execution. Neither a cast nor re-loading the VirtualModel brings the type back.
 *
 * @author sylvain
 */
public class ExternalWorkbookEditTest extends FMLScriptParserTestCase {

	static final String BEFORE_SCRIPT = "Tools/HotReload_Before.fmlscript";
	static final String SOURCE_WORKBOOK = "maintenance.xlsx";
	static final String WORKING_COPY = "maintenance-hotreload.xlsx";

	static final String READINGS_SHEET = "Readings";
	static final double NEW_VIBRATION = 9.9;

	@Test
	public void externalEditIsPickedUpByRefresh() throws Exception {

		File workingCopy = createWorkingCopy();
		try {
			instanciateTestServiceManager(XMLTechnologyAdapter.class, ExcelTechnologyAdapter.class);
			FlexoEditor editor = new DefaultFlexoEditor(null, serviceManager);
			assertNotNull(editor);
			CommandInterpreter commandInterpreter = new CommandInterpreter(serviceManager, System.in, System.out, System.err, HOME_DIR);

			execute(BEFORE_SCRIPT, commandInterpreter);

			FMLRTVirtualModelInstance line = retrieveCreatedInstance();
			assertNotNull("Script did not leave a ProductionLine instance behind", line);

			// Counts are read, not hard-coded: what matters is the delta. The workbook may already carry
			// whatever an earlier run of J6_Manip2 wrote into it.
			int reflectedBefore = countReflectedReadings(line);
			FlexoConceptInstance convBefore = equipmentWithCode(line, "CONV03");
			assertNotNull(convBefore);
			int roleReadingsBefore = ((List<?>) convBefore.getFlexoPropertyValue("readings")).size();
			System.out.println("before external edit: reflected readings=" + reflectedBefore + " CONV03 role readings="
					+ roleReadingsBefore + " lastVibration=" + convBefore.getFlexoPropertyValue("lastVibration"));

			// The external edit: POI writes into the file, Openflexo knows nothing about it.
			appendVibrationReading(workingCopy, "CONV03", NEW_VIBRATION);

			// Nothing was reloaded yet, so the model is still showing the previous content.
			assertEquals("The model should not see the edit before refresh()", reflectedBefore, countReflectedReadings(line));

			// One call to the demo's own behaviour: re-read the workbook and re-attach the structure.
			executeBehaviour(line, "refresh", editor);

			// The reflected view was rebuilt from the file, so the new row exists as a Reading...
			assertEquals("refresh() did not pick up the new row", reflectedBefore + 1, countReflectedReadings(line));

			// ... it entered the "readings" role through synchronize(), and the derived values followed.
			FlexoConceptInstance convAfter = equipmentWithCode(line, "CONV03");
			assertNotNull(convAfter);
			int roleReadingsAfter = ((List<?>) convAfter.getFlexoPropertyValue("readings")).size();
			assertEquals("The new reading did not enter the role", roleReadingsBefore + 1, roleReadingsAfter);
			assertEquals(NEW_VIBRATION, (Double) convAfter.getFlexoPropertyValue("lastVibration"), 0.001);
			assertEquals("EXCEEDED", convAfter.getFlexoPropertyValue("thresholdStatus"));

			System.out.println("after refresh: reflected readings=" + countReflectedReadings(line) + " CONV03 role readings="
					+ roleReadingsAfter + " lastVibration=" + convAfter.getFlexoPropertyValue("lastVibration") + " status="
					+ convAfter.getFlexoPropertyValue("thresholdStatus"));
			System.out.println("Test PASSED: an edit made outside Openflexo is picked up by refresh(), no restart");
		} finally {
			workingCopy.delete();
		}
	}

	/**
	 * Retrieve the instance the script created. Going through the ResourceManager rather than reading the script's "line" variable back
	 * from the command interpreter: the interpreter's binding model does not expose script variables to callers.
	 */
	private FMLRTVirtualModelInstance retrieveCreatedInstance() {
		for (FMLRTVirtualModelInstanceResource resource : serviceManager.getResourceManager()
				.getRegisteredResources(FMLRTVirtualModelInstanceResource.class)) {
			if (resource.isLoaded()) {
				return resource.getLoadedResourceData();
			}
		}
		return null;
	}

	/** Number of Reading instances in the reflected view of the workbook. */
	private int countReflectedReadings(FMLRTVirtualModelInstance line) {
		VirtualModelInstance<?, ?> maintenance = line.getFlexoPropertyValue("maintenance");
		assertNotNull("The reflected model slot is not resolved", maintenance);
		return maintenance.getFlexoConceptInstances("Reading").size();
	}

	private FlexoConceptInstance equipmentWithCode(FMLRTVirtualModelInstance line, String code) {
		for (FlexoConceptInstance equipment : line.getFlexoConceptInstances("Equipment")) {
			if (code.equals(equipment.getFlexoPropertyValue("code"))) {
				return equipment;
			}
		}
		return null;
	}

	private void executeBehaviour(FMLRTVirtualModelInstance instance, String behaviourName, FlexoEditor editor) {
		FlexoBehaviour behaviour = instance.getFlexoConcept().getFlexoBehaviour(behaviourName);
		assertNotNull("No behaviour '" + behaviourName + "' on " + instance.getFlexoConcept(), behaviour);
		assertTrue(behaviourName + " is expected to be an ActionScheme", behaviour instanceof ActionScheme);
		ActionSchemeActionFactory factory = new ActionSchemeActionFactory((ActionScheme) behaviour, instance);
		ActionSchemeAction action = factory.makeNewAction(instance, null, editor);
		action.doAction();
	}

	private void execute(String scriptPath, CommandInterpreter commandInterpreter) throws Exception {
		Resource scriptResource = ResourceLocator.locateResource(scriptPath);
		assertNotNull("Cannot find " + scriptPath, scriptResource);
		FMLScript script = parseFMLScript(scriptResource, commandInterpreter);
		checkFMLScript(scriptPath, script);
		try {
			script.execute();
		} catch (FMLAssertException e) {
			fail(scriptPath + ": " + e.getMessage());
		}
	}

	/**
	 * Copy maintenance.xlsx next to itself in the resource center, BEFORE the service manager starts, so that the copy is discovered as a
	 * resource of its own with a predictable URI.
	 */
	private File createWorkingCopy() throws IOException {
		Resource source = ResourceLocator.locateResource(SOURCE_WORKBOOK);
		assertNotNull("Cannot find " + SOURCE_WORKBOOK, source);
		File sourceFile = ResourceLocator.retrieveResourceAsFile(source);
		assertNotNull("Cannot resolve " + SOURCE_WORKBOOK + " as a file", sourceFile);
		File copy = new File(sourceFile.getParentFile(), WORKING_COPY);
		Files.copy(sourceFile.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
		assertTrue(copy.exists());
		return copy;
	}

	/**
	 * Append one vibration reading to the "Readings" sheet, using POI directly. Column layout is the one declared by
	 * MaintenanceRecords.fml: 0 Date | 1 code | 2 quantity | 3 value | 4 unit | 5 operator.
	 */
	private void appendVibrationReading(File file, String equipmentCode, double value) throws Exception {
		Workbook workbook;
		try (FileInputStream in = new FileInputStream(file)) {
			workbook = WorkbookFactory.create(in);
		}
		Sheet sheet = workbook.getSheet(READINGS_SHEET);
		assertNotNull("No '" + READINGS_SHEET + "' sheet in " + file, sheet);
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		row.createCell(0).setCellValue(new Date());
		row.createCell(1).setCellValue(equipmentCode);
		row.createCell(2).setCellValue("Vibration");
		row.createCell(3).setCellValue(value);
		row.createCell(4).setCellValue("mm/s");
		row.createCell(5).setCellValue("EXT");
		try (FileOutputStream out = new FileOutputStream(file)) {
			workbook.write(out);
		}
		workbook.close();
	}
}
