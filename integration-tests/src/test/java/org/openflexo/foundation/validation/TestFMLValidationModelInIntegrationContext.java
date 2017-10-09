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

package org.openflexo.foundation.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.FMLValidationModel;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationRuleSet;
import org.openflexo.technologyadapter.diagram.fml.OverridingGraphicalRepresentation.ConnectorOverridingGraphicalRepresentation;
import org.openflexo.technologyadapter.diagram.fml.OverridingGraphicalRepresentation.ShapeOverridingGraphicalRepresentation;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * This unit test is intented to test FML validation model in integration context (all technology adapters are loaded)
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFMLValidationModelInIntegrationContext extends OpenflexoTestCase {

	static FMLValidationModel validationModel;

	@Test
	@TestOrder(1)
	public void testCreateFMLValidationModel() throws ModelDefinitionException {

		instanciateTestServiceManager();
		validationModel = new FMLValidationModel(serviceManager.getTechnologyAdapterService());
		System.out.println("class number= " + validationModel.getValidationModelFactory().getModelContext().getEntityCount());

		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ForegroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.GraphicalElementSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.fml.editionaction.SelectEMFObjectIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.AddFlexoConceptInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.SelectFlexoConceptInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.SelectIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.CellStyleAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.DeleteFlexoConceptInstance.class) != null);
		assertTrue(
				validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.shapes.Star.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FMLLocalizedEntry.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ContainerGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ColorGradientBackgroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.OverridingGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.AbstractCreationScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FMLLocalizedDictionary.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.FGEStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.SemanticsExcelModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.AddToListAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.AddFlexoConceptInstanceParameter.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.VirtualModel.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.PrimitiveActorReference.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagramElementAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.connectors.CurveConnectorSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Square.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.editionaction.XMLDataPropertyAssertion.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.NoneBackgroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.TechnologySpecificFlexoBehaviour.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.DiagramRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Polygon.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.AddExcelSheet.class) != null);
		assertTrue(
				validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.shapes.Arc.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.SelectExcelRow.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.FreeDiagramModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.AddExcelCell.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddStatement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.ShapeRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.SelectExcelCell.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.ShapeSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Triangle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.OWLClassRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.FMLDiagramPaletteElementBinding.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.MatchingCriteria.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoBehaviourParameter.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.GraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.DeleteFlexoConceptInstanceParameter.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.RemoveFromListAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.AddExcelRow.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoBehaviourObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.RectangularOctogon.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConceptObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConceptStructuralFacet.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.ConnectorRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.ObjectPropertyStatementRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ShapeGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.CreateDiagram.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.inspector.FlexoConceptInspector.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.fml.EMFClassClassRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.OWLDataPropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.BusinessConceptInstanceRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.editionaction.GetXMLDocumentRoot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.EditionAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.technologyadapter.FreeModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.FreeXMLURIProcessor.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.OWLObjectPropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddOWLIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.editionaction.SetXMLDocumentRoot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.XMLTypeRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.metamodel.XMLMetaModel.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.FMLDiagramPaletteElementBindingParameter.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.OWLModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.connectors.ConnectorSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.SelectExcelSheet.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.OntologicObjectRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.fml.EMFEnumClassRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.TextureBackgroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.SynchronizationScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.FetchRequestCondition.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Chevron.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.FlexoConceptInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.connectors.RectPolylinConnectorSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.VirtualModelModelSlotInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.XMLMetaModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.IndividualRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.ComplexCurve.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.RegularPolygon.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.FMLRTModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.GraphicalElementRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.TextStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.controlgraph.ConditionalAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.XMLModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.CreateFlexoConceptInstanceParameter.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.controlgraph.ControlStructureAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.AbstractActionScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddRestrictionStatement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.fml.editionaction.AddEMFObjectIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.fml.EMFObjectIndividualRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FMLObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.metamodel.XMLObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.PropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Losange.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.OWLIndividualRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.PrimitiveRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.BackgroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.XMLActorReference.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.DiagramNavigationScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.editionaction.AddPowerpointSlide.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.DiagramFlexoBehaviour.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.LinkScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.editionaction.DiagramAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.AddConcept.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.PowerpointSlideRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.BasicPowerpointModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.ExcelColumnRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddDataPropertyStatement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConceptInstanceRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.metamodel.XMLProperty.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.FreeXMLModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.FetchRequest.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(ConnectorOverridingGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.ExcelCellRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.emf.EMFModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.AddClass.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConceptConstraint.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConceptBehaviouralFacet.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.PowerpointShapeRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.editionaction.AddXMLIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.ActorReference.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.GraphicalElementAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoBehaviour.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.FreeModelSlotInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(ShapeOverridingGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.editionaction.AddXMLType.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoConcept.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.ObjectPropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.editionaction.AddPowerpointShape.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddObjectPropertyStatement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.editionaction.SelectPowerpointShape.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.BasicExcelModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.PowerpointModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.StatementRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.ActionScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.ExcelRowRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.BusinessConceptTypeRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.fml.DropScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Circle.class) != null);
		assertTrue(
				validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.shapes.Plus.class) != null);
		// assertTrue(validationModel.getValidationModelFactory().getModelContext().getModelEntity(ConceptActorReference.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.shapes.Rectangle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.fml.XMLIndividualRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.metamodel.XMLType.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ColorBackgroundStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.editionaction.AddBusinessConceptInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.TypeAwareModelSlotInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.AssignableAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.ModelSlotInstance.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.SubClassStatementRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddSubClassStatement.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.DataPropertyAssertion.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.AbstractAssertion.class) != null);
		assertTrue(
				validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.shapes.Oval.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.VirtualModelInstanceObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.resource.ResourceData.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.DataPropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.DeleteAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.FlexoObject.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.ClassRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.AbstractXMLModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.AbstractXMLURIProcessor.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.AddIndividual.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.BackgroundImageBackgroundStyle.class) != null);
		assertTrue(
				validationModel.getValidationModelFactory().getModelContext().getModelEntity(org.openflexo.fge.ShadowStyle.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.rt.ModelObjectActorReference.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.xml.XMLURIProcessor.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.CreationScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.technologyadapter.ModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.DeletionScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.editionaction.AssignationAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.CloningScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.editionaction.AddOWLClass.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.technologyadapter.TypeAwareModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.controlgraph.IterationAction.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.NavigationScheme.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.ConnectorGraphicalRepresentation.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.fge.connectors.LineConnectorSpecification.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.diagram.DiagramModelSlot.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.FlexoRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.fml.inspector.InspectorEntry.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.DataPropertyStatementRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.owl.fml.OWLPropertyRole.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.foundation.ontology.fml.editionaction.ObjectPropertyAssertion.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.powerpoint.fml.editionaction.SelectPowerpointSlide.class) != null);
		assertTrue(validationModel.getValidationModelFactory().getModelContext()
				.getModelEntity(org.openflexo.technologyadapter.excel.fml.ExcelSheetRole.class) != null);

		for (int i = 0; i < validationModel.getSortedClasses().size(); i++) {
			System.out.println("> " + validationModel.getSortedClasses().get(i));
		}

	}

	@Test
	@TestOrder(2)
	public void testAddShapeValidationRules() throws ModelDefinitionException {
		ValidationRuleSet<?> ruleSet = validationModel.getRuleSet(AddShape.class);
		assertNotNull(ruleSet);
		System.out.println("ruleSet=" + ruleSet + " size=" + ruleSet.getRulesCount());
		for (ValidationRule r : ruleSet.getRules()) {
			System.out.println("> " + r);
		}
		assertTrue(ruleSet.containsRuleClass(AddShape.AddShapeActionMustAdressAValidShapeRole.class));
		assertTrue(ruleSet.containsRuleClass(AddShape.AddShapeActionMustHaveAValidContainer.class));
	}

	@Test
	@TestOrder(3)
	public void testAddConnectorValidationRules() throws ModelDefinitionException {
		ValidationRuleSet<?> ruleSet = validationModel.getRuleSet(AddConnector.class);
		assertNotNull(ruleSet);
		System.out.println("ruleSet=" + ruleSet + " size=" + ruleSet.getRulesCount());
		for (ValidationRule r : ruleSet.getRules()) {
			System.out.println("> " + r);
		}
		assertTrue(ruleSet.containsRuleClass(AddConnector.AddConnectorActionMustAdressAValidConnectorRole.class));
		assertTrue(ruleSet.containsRuleClass(AddConnector.AddConnectorActionMustHaveAValidStartingShape.class));
		assertTrue(ruleSet.containsRuleClass(AddConnector.AddConnectorActionMustHaveAValidEndingShape.class));
	}

	@Test
	@TestOrder(4)
	public void testGraphicalActionValidationRules() throws ModelDefinitionException {
		ValidationRuleSet<?> ruleSet = validationModel.getRuleSet(GraphicalAction.class);
		assertNotNull(ruleSet);
		System.out.println("ruleSet=" + ruleSet + " size=" + ruleSet.getRulesCount());
		for (ValidationRule r : ruleSet.getRules()) {
			System.out.println("> " + r);
		}
		assertTrue(ruleSet.containsRuleClass(GraphicalAction.GraphicalActionMustHaveASubject.class));
		assertTrue(ruleSet.containsRuleClass(GraphicalAction.GraphicalActionMustDefineAValue.class));
	}

}
