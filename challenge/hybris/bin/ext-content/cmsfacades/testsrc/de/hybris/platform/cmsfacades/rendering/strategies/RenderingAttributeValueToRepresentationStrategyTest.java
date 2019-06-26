/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.rendering.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.CollectionToRepresentationConverter;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.rendering.predicates.attributes.AttributeContainsCMSComponentsPredicate;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RenderingAttributeValueToRepresentationStrategyTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String ATTRIBUTE_QUALIFIER = "Some qualifier";

	private final String model1Value = "Model_1_Value";
	private final String model1Representation = "Some model 1";

	private final String model2Value = "Model_2_Value";
	private final String model2Representation = "Some model 2";

	@Mock
	private AbstractCMSComponentModel cmsComponent1;

	@Mock
	private AbstractCMSComponentData componentData1;

	@Mock
	private ItemModel model1;

	@Mock
	private Function<Object, Object> transformationFunction;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	@Mock
	private ModelService modelService;

	@Mock
	private RenderingVisibilityService renderingVisibilityService;

	@Mock
	private CollectionToRepresentationConverter collectionToRepresentationConverter;

	@Mock
	private AttributeContainsCMSComponentsPredicate attributeContainsCMSComponentsPredicate;

	@Mock
	private de.hybris.platform.servicelayer.dto.converter.Converter<AbstractCMSComponentModel, AbstractCMSComponentData> cmsComponentModelToDataRenderingConverter;

	@Spy
	@InjectMocks
	private RenderingAttributeValueToRepresentationStrategy strategy;

	// --------------------------------------------------------------------------
	// Test SetUp
	// --------------------------------------------------------------------------
	@Before
	@SuppressWarnings("unchecked")
	public void setUp()
	{
		// Attribute Descriptor Model
		when(attributeDescriptorModel.getQualifier()).thenReturn(ATTRIBUTE_QUALIFIER);

		// Transformation Function
		when(transformationFunction.apply(model1Value)).thenReturn(model1Representation);
		when(transformationFunction.apply(model2Value)).thenReturn(model2Representation);
		when(transformationFunction.apply(cmsComponent1)).thenReturn(model1Representation);

		// Component Rendering Converter
		when(cmsComponentModelToDataRenderingConverter.convert(cmsComponent1)).thenReturn(componentData1);

		// Rendering Utils
		when(renderingVisibilityService.isVisible(any())).thenReturn(true);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenNullValue_whenGetElementToRenderingDataTransformationFunctionIsCalled_ItReturnsNull()
	{
		// WHEN
		Function function = strategy.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		Object result = function.apply(null);

		// THEN
		assertThat(result, nullValue());

		verify(renderingVisibilityService, never()).isVisible(any());
		verify(cmsComponentModelToDataRenderingConverter, never()).convert(any());
		verify(transformationFunction, never()).apply(any());
	}

	@Test
	public void givenNonCmsComponent_WhenElementTransformationFunctionIsCalled_ItCallsTheProvidedTransformationFunction()
	{
		// GIVEN
		when(attributeContainsCMSComponentsPredicate.test(any())).thenReturn(false);
		verify(transformationFunction, never()).apply(any());

		// WHEN
		Function<Object, Object> returnedFunction = strategy
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		Object result = returnedFunction.apply(model1Value);

		// THEN
		verify(cmsComponentModelToDataRenderingConverter, never()).convert(any());
		verify(transformationFunction, times(1)).apply(any());
		assertThat(result, is(model1Representation));
	}

	@Test
	public void givenVisibleCmsComponent_WhenElementTransformationFunctionIsCalled_ThenItCallsTheComponentRenderingConverterDirectly()
	{
		// GIVEN
		when(attributeContainsCMSComponentsPredicate.test(any())).thenReturn(true);
		verify(transformationFunction, never()).apply(any());

		// WHEN
		Function<Object, Object> returnedFunction = strategy
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		Object result = returnedFunction.apply(cmsComponent1);

		// THEN
		verify(transformationFunction, never()).apply(any());
		verify(cmsComponentModelToDataRenderingConverter).convert(cmsComponent1);
		assertThat(result, is(componentData1));
	}

	@Test
	public void givenInvisibleCmsComponent_WhenElementTransformationFunctionIsCalled_ThenItReturnsNull()
	{
		// GIVEN
		when(attributeContainsCMSComponentsPredicate.test(any())).thenReturn(true);
		markComponentAsDisallowed(cmsComponent1);
		verify(transformationFunction, never()).apply(any());

		// WHEN
		Function<Object, Object> returnedFunction = strategy
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		Object result = returnedFunction.apply(cmsComponent1);

		// THEN
		verify(cmsComponentModelToDataRenderingConverter, never()).convert(any());
		verify(transformationFunction, never()).apply(any());
		assertThat(result, nullValue());
	}

	@Test
	public void givenSimpleAttribute_WhenCalled_ThenItReturnsItsSimpleRepresentation()
	{
		// GIVEN
		addModelToValueMapping(model1, model1Value);
		verify(strategy, never()).getElementToRenderingDataTransformationFunction(any(), any());

		// WHEN
		Object result = strategy.getSimpleGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(strategy, times(1))
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		verify(transformationFunction, times(1)).apply(model1Value);
		assertThat(result, is(model1Representation));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenCollectionAttributeWithCmsComponents_WhenCalled_ThenItReturnsACollectionOfSimpleRepresentations()
	{
		// GIVEN
		Collection collectionOfValues = Arrays.asList(model1Value, model2Value);

		addModelToValueMapping(model1, collectionOfValues);

		when(attributeContainsCMSComponentsPredicate.test(attributeDescriptorModel)).thenReturn(true);

		when(collectionToRepresentationConverter.convert(any(), any(), any())).thenReturn(Arrays.asList(model1Representation, model2Representation));

		// WHEN
		Collection<String> result = (Collection<String>) strategy
				.getCollectionGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(cmsComponentModelToDataRenderingConverter, never()).convert(any());
		assertThat(result, contains(model1Representation, model2Representation));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenCollectionAttributeWithNoCmsComponents_WhenCalled_ThenItReturnsACollectionOfSimpleRepresentations()
	{
		// GIVEN
		Collection collectionOfValues = Arrays.asList(model1Value, model2Value);
		Collection transformedCollection = Arrays.asList(model1Representation, model2Representation);

		addModelToValueMapping(model1, collectionOfValues);
		when(collectionToRepresentationConverter.convert(any(), any(), any())).thenReturn(transformedCollection);
		when(attributeContainsCMSComponentsPredicate.test(attributeDescriptorModel)).thenReturn(false);

		verify(strategy, never()).getElementToRenderingDataTransformationFunction(any(), any());

		// WHEN
		Collection<String> result = (Collection<String>) strategy
				.getCollectionGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(cmsComponentModelToDataRenderingConverter, never()).convert(any());
		verify(strategy).getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		verify(collectionToRepresentationConverter).convert(eq(attributeDescriptorModel), eq(collectionOfValues), any());

		assertThat(result, is(transformedCollection));
	}

	@Test
	public void givenLocalizedValue_WhenCalled_ThenItReturnsItsRepresentationForTheCurrentLocale()
	{
		// GIVEN
		addModelToValueMapping(model1, model1Value);
		verify(strategy, never()).getElementToRenderingDataTransformationFunction(any(), any());

		// WHEN
		Object result = strategy.getLocalizedGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(strategy, times(1))
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		verify(transformationFunction, times(1)).apply(model1Value);
		assertThat(result, is(model1Representation));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenLocalizedCollection_WhenCalled_ThenItReturnsTheCollectionRepresentationForTheCurrentLocale()
	{
		// GIVEN
		Collection collectionOfValues = Arrays.asList(model1Value, model2Value);
		Collection transformedCollection = Arrays.asList(model1Representation, model2Representation);

		addModelToValueMapping(model1, collectionOfValues);
		when(collectionToRepresentationConverter.convert(any(), any(), any())).thenReturn(transformedCollection);
		when(attributeContainsCMSComponentsPredicate.test(attributeDescriptorModel)).thenReturn(false);

		verify(strategy, never()).getElementToRenderingDataTransformationFunction(any(), any());

		// WHEN
		Collection<String> result = (Collection<String>) strategy
				.getLocalizedCollectionGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(strategy, times(1))
				.getElementToRenderingDataTransformationFunction(attributeDescriptorModel, transformationFunction);
		verify(collectionToRepresentationConverter, times(1))
				.convert(eq(attributeDescriptorModel), eq(collectionOfValues), any());

		assertThat(result, is(transformedCollection));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void addModelToValueMapping(ItemModel source, Object returnValue)
	{
		when(modelService.getAttributeValue(source, ATTRIBUTE_QUALIFIER)).thenReturn(returnValue);
	}

	protected void markComponentAsDisallowed(AbstractCMSComponentModel component)
	{
		when(renderingVisibilityService.isVisible(component)).thenReturn(false);
	}
}
