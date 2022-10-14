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

import java.awt.Image;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FMLModelContext.FMLProperty;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.annotations.SeeAlso;
import org.openflexo.foundation.fml.annotations.UsageExample;
import org.openflexo.foundation.fml.editionaction.AssignableAction;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

/**
 * Documentation generator for {@link EditionAction}
 * 
 */
public class EditionActionGenerator<EA extends EditionAction> extends AbstractGenerator<EA> {

	private static final Logger logger = FlexoLogger.getLogger(EditionActionGenerator.class.getPackage().getName());

	private EA ea;

	public EditionActionGenerator(Class<EA> objectClass, TADocGenerator<?> taDocGenerator) {
		super(objectClass, taDocGenerator);
		ea = getFMLModelFactory().newInstance(getObjectClass());
		generateIconFiles();
	}

	@Override
	protected Image getIcon() {
		return getTechnologyAdapterController().getIconForEditionAction(getObjectClass()).getImage();
	}

	@Override
	public void generate() {

		StringBuffer sb = new StringBuffer();

		sb.append("# <tt>" + getFMLKeyword() + "</tt> &nbsp; " + getBigIconAsHTML() + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("[//]: # (Do not edit this file, which is automatically generated)" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append(getFMLDescription() + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append(StringUtils.LINE_SEPARATOR);
		sb.append("---" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("## Usage" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("```java" + StringUtils.LINE_SEPARATOR);
		sb.append(toCode(getUsage(false)) + StringUtils.LINE_SEPARATOR);
		sb.append("```" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("or" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("```java" + StringUtils.LINE_SEPARATOR);
		sb.append(toCode(getUsage(true)) + StringUtils.LINE_SEPARATOR);
		sb.append("```" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		int requiredProperties = 0;
		for (FMLProperty fmlProperty : getFMLProperties()) {
			if (fmlProperty.isRequired()) {
				requiredProperties++;
			}
		}

		if (requiredProperties > 0) {
			sb.append("where" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			for (FMLProperty fmlProperty : getFMLProperties()) {
				if (fmlProperty.isRequired()) {
					Type propertyType = fmlProperty.getModelProperty().getGetterMethod().getGenericReturnType();
					if (propertyType instanceof ParameterizedType
							&& ((ParameterizedType) propertyType).getRawType().equals(DataBinding.class)) {
						Type argType = ((ParameterizedType) propertyType).getActualTypeArguments()[0];
						sb.append("- \\<" + fmlProperty.getPathNameInUsage() + "\\>");
						sb.append(" addresses a `" + TypeUtils.simpleRepresentation(argType) + "`" + StringUtils.LINE_SEPARATOR);
					}
					else {
						sb.append("- \\<" + fmlProperty.getPathNameInUsage() + "\\>");
						sb.append(" addresses a `" + TypeUtils.simpleRepresentation(propertyType) + "`" + StringUtils.LINE_SEPARATOR);
					}
				}
			}
			sb.append(StringUtils.LINE_SEPARATOR);
		}

		if (getFMLProperties().size() > 0) {

			sb.append("---" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			sb.append("## Configuration" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			sb.append("| Property        | Type                    | &nbsp;Required&nbsp;  |" + StringUtils.LINE_SEPARATOR);
			sb.append("| --------------- |-------------------------| :------:|" + StringUtils.LINE_SEPARATOR);

			for (FMLProperty fmlProperty : getFMLProperties()) {
				String propertyName = fmlProperty.getLabel();
				String propertyType;
				Type pType = fmlProperty.getModelProperty().getGetterMethod().getGenericReturnType();
				if (pType instanceof ParameterizedType && ((ParameterizedType) pType).getRawType().equals(DataBinding.class)) {
					Type argType = ((ParameterizedType) pType).getActualTypeArguments()[0];
					propertyType = TypeUtils.simpleRepresentation(argType);
				}
				else {
					propertyType = TypeUtils.simpleRepresentation(pType);
				}
				sb.append("| `" + propertyName + "` &nbsp; | `" + propertyType + "` &nbsp; | " + (fmlProperty.isRequired() ? "yes" : "no")
						+ " |" + StringUtils.LINE_SEPARATOR);
			}
			sb.append(StringUtils.LINE_SEPARATOR);

			sb.append("---" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			for (FMLProperty fmlProperty : getFMLProperties()) {
				String propertyName = fmlProperty.getLabel();
				sb.append("- `" + propertyName + "` : " + toMD(fmlProperty.getDescription()) + StringUtils.LINE_SEPARATOR);
			}

			sb.append(StringUtils.LINE_SEPARATOR);
		}

		if (getFMLExamples().size() > 0) {
			sb.append("## Examples" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			for (UsageExample usageExample : getFMLExamples()) {

				sb.append("```java" + StringUtils.LINE_SEPARATOR);
				sb.append(toCode(usageExample.example()) + StringUtils.LINE_SEPARATOR);
				sb.append("```" + StringUtils.LINE_SEPARATOR);
				sb.append(StringUtils.LINE_SEPARATOR);
				sb.append(toMD(usageExample.description()) + StringUtils.LINE_SEPARATOR);
				sb.append(StringUtils.LINE_SEPARATOR);

			}
		}

		sb.append("---" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append("## Javadoc" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);

		sb.append(getJavadocReference());
		sb.append(StringUtils.LINE_SEPARATOR);

		if (getReferences().size() > 0) {
			sb.append("---" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);
			sb.append("## See also" + StringUtils.LINE_SEPARATOR);
			sb.append(StringUtils.LINE_SEPARATOR);

			for (SeeAlso reference : getReferences()) {

				AbstractGenerator<? extends FMLObject> generatorReference = getReference(reference.value());
				sb.append(" - " + generatorReference.getSmallIconAsHTML());
				sb.append(" [`" + generatorReference.getFMLKeyword() + "`](" + generatorReference.getObjectClass().getSimpleName()
						+ ".html) : " + generatorReference.getFMLShortDescription());
				sb.append(StringUtils.LINE_SEPARATOR);

			}
			sb.append(StringUtils.LINE_SEPARATOR);
		}

		render(sb);
	}

	private String getUsage(boolean fullQualified) {
		StringBuffer returned = new StringBuffer();
		if (ea instanceof AssignableAction) {
			returned.append("[" + TypeUtils.simpleRepresentation(((AssignableAction) ea).getAssignableType()) + " <value> =]"
					+ StringUtils.LINE_SEPARATOR);
		}
		if (fullQualified) {
			returned.append(getTechnologyAdapter().getIdentifier() + "::");
		}
		returned.append(getFMLKeyword() + "(");
		boolean hasOptionalProperties = false;
		boolean isFirst = true;
		for (FMLProperty fmlProperty : getFMLProperties()) {
			if (fmlProperty.isRequired()) {
				if (!isFirst) {
					returned.append(",");
				}
				returned.append(fmlProperty.getLabel() + "=<" + fmlProperty.getPathNameInUsage() + ">");
				isFirst = false;
			}
			else {
				hasOptionalProperties = true;
			}
		}
		if (hasOptionalProperties) {
			returned.append("[" + (isFirst ? "" : ",") + "options]");
		}
		returned.append(")");
		return returned.toString();
	}

	// @formatter:off	
	/*
	append(dynamicContents(() -> getVisibilityAsString(getModelObject().getVisibility()), SPACE), getVisibilityFragment());
	append(dynamicContents(() -> serializeType(getModelObject().getType())), getTypeFragment());
	append(dynamicContents(() -> serializeCardinality(getModelObject().getCardinality())), getCardinalityFragment());
	append(dynamicContents(SPACE,() -> getModelObject().getName(), SPACE), getNameFragment());
	append(staticContents("", "with", SPACE), getWithFragment());
	when(() -> isFullQualified())
	.thenAppend(dynamicContents(() -> getFMLFactory().serializeTAId(getModelObject())), getTaIdFragment())
	.thenAppend(staticContents("::"), getColonColonFragment());
	append(dynamicContents(() -> serializeFlexoRoleName(getModelObject())), getRoleFragment());
	when(() -> hasFMLProperties())
	.thenAppend(staticContents("("), getFMLParametersLParFragment())
	.thenAppend(childrenContents("","", () -> getModelObject().getFMLPropertyValues(getFactory()), ", ","", Indentation.DoNotIndent,
			FMLPropertyValue.class))
	.thenAppend(staticContents(")"), getFMLParametersRParFragment());
	append(staticContents(";"), getSemiFragment());
	 */
	// @formatter:on	

}