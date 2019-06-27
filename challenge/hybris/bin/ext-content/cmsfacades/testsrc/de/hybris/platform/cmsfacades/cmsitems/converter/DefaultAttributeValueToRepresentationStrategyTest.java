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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.CollectionToRepresentationConverter;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Function;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CMSITEMS_INVALID_CONVERSION_ERROR;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeValueToRepresentationStrategyTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String ENGLISH = "En";
	private final String FRENCH = "Fr";

	private final String ATTRIBUTE_QUALIFIER = "Some qualifier";

	private final String model1Value = "Model_1_Value";
	private final String model1Representation = "Some model 1";

	private final String model2Value = "Model_2_Value";
	private final String model2Representation = "Some model 2";

	private Map<String, Object> languageValueMap;

	@Mock
	private ItemModel model1;

	@Mock
	private Function<Object, Object> transformationFunction;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	@Mock
	private ModelService modelService;

	@Mock
	private CollectionToRepresentationConverter collectionToRepresentationConverter;

	@Mock
	private ValidationErrors validationErrors;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private LocalizedPopulator localizedPopulator;

	@Mock
	private CloneComponentContextProvider cloneComponentContextProvider;

	@Spy
	@InjectMocks
	private DefaultAttributeValueToRepresentationStrategy strategy;

	private enum ExceptionType {
		VALIDATION,
		CONVERSION,
		ANY
	}

	// --------------------------------------------------------------------------
	// Test SetUp
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		// Attribute Descriptor Model
		when(attributeDescriptorModel.getQualifier()).thenReturn(ATTRIBUTE_QUALIFIER);

		// Transformation Function
		when(transformationFunction.apply(model1Value)).thenReturn(model1Representation);
		when(transformationFunction.apply(model2Value)).thenReturn(model2Representation);

		// Validation Errors
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

		// Localized Populator
		when(localizedPopulator.populateAsMapOfLanguages(any())).then(param -> {
			Map<String, Object> map = new HashMap<>();
			Function function = param.getArgumentAt(0, Function.class);

			map.put(ENGLISH, function.apply(Locale.ENGLISH));
			map.put(FRENCH, function.apply(Locale.FRENCH));

			return map;
		});
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenSimpleAttribute_WhenCalled_ThenItReturnsItsSimpleRepresentation()
	{
		// GIVEN
		addModelToValueMapping(model1, model1Value);

		// WHEN
		Object result = strategy.getSimpleGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(transformationFunction, times(1)).apply(model1Value);
		assertThat(result, is(model1Representation));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenCollectionAttribute_WhenCalled_ThenItReturnsACollectionOfSimpleRepresentations()
	{
		// GIVEN
		Collection collectionOfValues = Arrays.asList(model1Value, model2Value);
		Collection transformedCollection = Arrays.asList(model1Representation, model2Representation);

		addModelToValueMapping(model1, collectionOfValues);
		when(collectionToRepresentationConverter.convert(any(), any(), any())).thenReturn(transformedCollection);

		// WHEN
		Collection<String> result = (Collection<String>) strategy
				.getCollectionGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(collectionToRepresentationConverter, times(1))
				.convert(attributeDescriptorModel, collectionOfValues, transformationFunction);

		assertThat(result, is(transformedCollection));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenLocalizedItem_WhenCalled_ThenItReturnsMapWithRepresentationForEachLanguage()
	{
		// GIVEN
		ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);

		addModelToValueMapping(model1, Locale.ENGLISH, model1Value);
		addModelToValueMapping(model1, Locale.FRENCH, model2Value);

		Map<String, Object> transformedMap = new HashMap<>();
		transformedMap.put(ENGLISH, model1Representation);
		transformedMap.put(FRENCH, model2Representation);
		when(strategy.convertLocalizedValue(any(), any(), any())).thenReturn(transformedMap);

		// WHEN
		Map<String, Object> result = (Map<String, Object>) strategy
				.getLocalizedGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		verify(strategy, times(1))
				.convertLocalizedValue(eq(attributeDescriptorModel), mapArgumentCaptor.capture(), eq(transformationFunction));

		assertThat(mapArgumentCaptor.getValue().get(ENGLISH), is(model1Value));
		assertThat(mapArgumentCaptor.getValue().get(FRENCH), is(model2Value));
		assertThat(result, is(transformedMap));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenLocalizedCollection_WhenCalled_ThenItReturnsMapWithTransformedCollectionForEachLanguage()
	{
		// GIVEN
		List<Object> model1CollectionValue = Arrays.asList(model1Value);
		List<Object> model2CollectionValue = Arrays.asList(model2Value);

		List<Object> model1ConvertedCollectionValue = Arrays.asList(model1Representation);
		List<Object> model2ConvertedCollectionValue = Arrays.asList(model2Representation);

		addModelToValueMapping(model1, Locale.ENGLISH, model1CollectionValue);
		addModelToValueMapping(model1, Locale.FRENCH, model2CollectionValue);

		when(collectionToRepresentationConverter.convert(any(), eq(model1CollectionValue), any())).thenReturn(model1ConvertedCollectionValue);
		when(collectionToRepresentationConverter.convert(any(), eq(model2CollectionValue), any())).thenReturn(model2ConvertedCollectionValue);

		when(strategy.convertLocalizedValue(any(), any(), any())).then((param) -> {
			Map<String, Object> localizedMap = param.getArgumentAt(1, Map.class);
			Function<Object, Object> transformationFn = param.getArgumentAt(2, Function.class);

			Map<String, Object> newMap = new HashMap<>();

			newMap.put(ENGLISH, transformationFn.apply(localizedMap.get(ENGLISH)));
			newMap.put(FRENCH, transformationFn.apply(localizedMap.get(FRENCH)));

			return newMap;
		});

		// WHEN
		Map<String, Collection> result = (Map<String, Collection>) strategy
				.getLocalizedCollectionGetter(attributeDescriptorModel, model1, transformationFunction).get();

		// THEN
		assertThat(result.get(ENGLISH), is(model1ConvertedCollectionValue));
		assertThat(result.get(FRENCH), is(model2ConvertedCollectionValue));
	}

	@Test
	public void givenEmptyMap_WhenConvertLocalizedValueIsCalled_ThenItReturnsNull()
	{
		// WHEN
		Map<String, Object> result = strategy.convertLocalizedValue(attributeDescriptorModel, null, transformationFunction);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenMap_WhenConvertLocalizedValueIsCalled_ThenTheValueForEachValueIsTransformed()
	{
		// GIVEN
		setUpLanguageValueMap();

		// WHEN
		Map<String, Object> result = strategy
				.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(model1Value);
		verify(transformationFunction, times(1)).apply(model2Value);

		assertThat(result.size(), is(languageValueMap.size()));
		assertThat(result.get(ENGLISH), is(model1Representation));
		assertThat(result.get(FRENCH), is(model2Representation));
	}

	@Test
	public void givenInvalidParameter_WhenConvertLocalizedValueIsCalled_ThenTheValidationExceptionIsTriggeredAndHandled()
	{
		// GIVEN
		setUpLanguageValueMap();
		throwExceptionWhenFunctionExecutes(model1Value, ExceptionType.VALIDATION);

		// WHEN
		Map<String, Object> result = strategy
				.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(model1Value);
		verify(transformationFunction, times(1)).apply(model2Value);
		verify(validationErrorsProvider, times(1))
				.collectValidationErrors(any(), eq(Optional.of(ENGLISH)), eq(Optional.empty()));

		assertThat(result.size(), is(languageValueMap.size()));
		assertThat(result.get(ENGLISH), nullValue());
		assertThat(result.get(FRENCH), is(model2Representation));
	}

	@Test
	public void givenNotSupportedParameter_WhenConvertLocalizedValueIsCalled_ThenTheConversionExceptionIsTriggeredAndHandled()
	{
		// GIVEN
		setUpLanguageValueMap();
		throwExceptionWhenFunctionExecutes(model1Value, ExceptionType.CONVERSION);

		ValidationError conversionError;
		ArgumentCaptor<ValidationError> validationErrorCaptor = ArgumentCaptor.forClass(ValidationError.class);

		// WHEN
		Map<String, Object> result = strategy
				.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(model1Value);
		verify(transformationFunction, times(1)).apply(model2Value);

		assertThat(result.size(), is(languageValueMap.size()));
		assertThat(result.get(ENGLISH), nullValue());
		assertThat(result.get(FRENCH), is(model2Representation));

		verify(validationErrors, times(1)).add(validationErrorCaptor.capture());
		conversionError = validationErrorCaptor.getValue();

		assertThat(conversionError.getField(), is(ATTRIBUTE_QUALIFIER));
		assertThat(conversionError.getRejectedValue(), is(model1Value));
		assertThat(conversionError.getLanguage(), is(ENGLISH));
		assertThat(conversionError.getErrorCode(), is(CMSITEMS_INVALID_CONVERSION_ERROR));
	}

	@Test(expected = Exception.class)
	public void WhenConvertLocalizedValueIsCalled_AndThereIsAnException_ThenExceptionIsNotCaught()
	{
		// GIVEN
		setUpLanguageValueMap();
		throwExceptionWhenFunctionExecutes(model1Value, ExceptionType.ANY);

		// WHEN
		strategy.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(model1Value);
		verify(transformationFunction, never()).apply(model2Value);
	}

	@Test
	public void GivenCloneComponentContextProviderIsInitialized_WhenConvertLocalizedValueIsCalled_ThenItInitializesTheItemInThatContext()
	{
		// GIVEN
		ArgumentCaptor<AbstractMap.SimpleImmutableEntry> entryCaptor = ArgumentCaptor.forClass(AbstractMap.SimpleImmutableEntry.class);

		setUpLanguageValueMap();
		when(cloneComponentContextProvider.isInitialized()).thenReturn(true);

		// WHEN
		strategy.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(cloneComponentContextProvider, times(2)).initializeItem(entryCaptor.capture());
		assertThat(entryCaptor.getAllValues().get(0).getKey(), is(SESSION_CLONE_COMPONENT_LOCALE));
		assertThat(entryCaptor.getAllValues().get(0).getValue(), is(ENGLISH));

		assertThat(entryCaptor.getAllValues().get(1).getKey(), is(SESSION_CLONE_COMPONENT_LOCALE));
		assertThat(entryCaptor.getAllValues().get(1).getValue(), is(FRENCH));
	}

	@Test
	public void GivenCloneComponentContextProviderIsNotInitialized_WhenConvertLocalizedValueIsCalled_ThenItDoesNotInitializeTheItem()
	{
		// GIVEN
		setUpLanguageValueMap();
		when(cloneComponentContextProvider.isInitialized()).thenReturn(false);

		// WHEN
		strategy.convertLocalizedValue(attributeDescriptorModel, languageValueMap, transformationFunction);

		// THEN
		verify(cloneComponentContextProvider, never()).initializeItem(any());
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void addModelToValueMapping(ItemModel source, Object returnValue)
	{
		when(modelService.getAttributeValue(source, ATTRIBUTE_QUALIFIER)).thenReturn(returnValue);
	}

	protected void addModelToValueMapping(ItemModel source, Locale locale, Object returnValue)
	{
		when(modelService.getAttributeValue(source, ATTRIBUTE_QUALIFIER, locale)).thenReturn(returnValue);
	}

	protected void setUpLanguageValueMap()
	{
		languageValueMap = new HashMap<>();
		languageValueMap.put(ENGLISH, model1Value);
		languageValueMap.put(FRENCH, model2Value);
	}

	@SuppressWarnings("unchecked")
	public void throwExceptionWhenFunctionExecutes(String parameter, DefaultAttributeValueToRepresentationStrategyTest.ExceptionType exceptionType)
	{
		Class exceptionClass;
		switch (exceptionType)
		{
			case VALIDATION:
				exceptionClass = ValidationException.class;
				break;
			case CONVERSION:
				exceptionClass = ConversionException.class;
				break;
			default:
				exceptionClass = Exception.class;
				break;
		}

		when(transformationFunction.apply(parameter)).thenThrow(exceptionClass);
	}
}
