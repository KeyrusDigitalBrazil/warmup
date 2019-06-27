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

import static de.hybris.platform.cms2.model.contents.CMSItemModel.NAME;
import static de.hybris.platform.cms2.model.contents.CMSItemModel.UID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_COMPONENT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CLONE_MODEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_MAP;
import static de.hybris.platform.core.model.ItemModel.ITEMTYPE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.strategy.impl.ComponentCloningStrategy;
import de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemValidator;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.cmsitems.converter.DefaultCMSItemConverterSemiIntegrationTest.MainClass;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSItemConverterTest
{
	private static final String INVALID = "invalid";
	private static final String QUALIFIER_1 = "Some_qualifier_1";
	private static final String QUALIFIER_2 = "Some_qualifier_2";
	private static final String QUALIFIER_3 = "Some_qualifier_3";

	private static final String ATTR_1_VALUE = "some_attr_1_value";
	private static final String ATTR_2_VALUE = "some_attr_2_value";
	private static final String ATTR_3_VALUE = "some_attr_3_value";

	private final ArgumentCaptor<String> qualifierCaptor = ArgumentCaptor.forClass(String.class);
	private final ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

	@Mock
	private TypeService typeService;
	@Mock
	private ModelService modelService;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private CMSItemModel cmsItem;
	@Mock
	private OriginalClonedItemProvider<ItemModel> originalClonedItemProvider;
	@Mock
	private ComponentCloningStrategy componentCloningStrategy;
	@Mock
	private CloneComponentContextProvider cloneComponentContextProvider;
	@Mock
	private AttributeStrategyConverterProvider cloneAttributeStrategyConverter;
	@Mock
	private CMSItemValidator<ItemModel> cmsItemValidator;
	@Mock
	private ComposedTypeToAttributeCollectionConverter composedTypeToAttributeCollectionConverter;
	@Mock
	private CMSPermissionChecker cmsPermissionChecker;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel1;
	@Mock
	private AttributeDescriptorModel attributeDescriptorModel2;
	@Mock
	private RelationDescriptorModel relationDescriptorModel;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private ComposedTypeModel attributeComposedType;

	@Spy
	@InjectMocks
	private DefaultCMSItemConverter cmsItemConverter;

	@Before
	public void setUp()
	{
		when(composedType.getCode()).thenReturn(CMSItemModel._TYPECODE);

		// Permissions
		when(permissionCRUDService.canChangeAttribute(any(), any())).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(any(), any())).thenReturn(true);
		when(cmsPermissionChecker.hasPermissionForContainedType(any(), any())).thenReturn(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConvertWhenItemModelIsNull()
	{
		cmsItemConverter.convert((ItemModel) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConvertWhenMapIsNull() throws InstantiationException, IllegalAccessException
	{
		cmsItemConverter.convert((Map<String, Object>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConvertWhenMapHasNoType() throws InstantiationException, IllegalAccessException
	{
		cmsItemConverter.convert(new HashMap<String, Object>());
	}

	@Test
	public void shouldFindModificationForSimpleAttributeType()
	{
		when(cloneComponentContextProvider.isInitialized()).thenReturn(TRUE);
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(NAME, "value");
		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_MAP)).thenReturn(sourceMap);

		final boolean value = cmsItemConverter.initializeCloneComponentAttributeContext(NAME, "clone-test-data");

		assertThat(value, is(TRUE));
	}

	@Test
	public void shouldFindModificationForComplexAttributeType()
	{
		when(cloneComponentContextProvider.isInitialized()).thenReturn(TRUE);
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(NAME, new HashMap<>());
		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_MAP)).thenReturn(sourceMap);

		final Map<String, Object> inputMap = new HashMap<>();
		final Map<String, String> localizedNames = new HashMap<>();
		localizedNames.put("en", "name");
		localizedNames.put("fr", "nom");
		inputMap.put(NAME, localizedNames);

		final boolean value = cmsItemConverter.initializeCloneComponentAttributeContext(NAME, inputMap);

		assertThat(value, is(TRUE));
	}

	@Test
	public void shouldNotFindModificationForUidAttribute()
	{
		when(cloneComponentContextProvider.isInitialized()).thenReturn(TRUE);
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(UID, "comp_1234");
		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_MAP)).thenReturn(sourceMap);

		final boolean value = cmsItemConverter.initializeCloneComponentAttributeContext(UID, "clone_9876");

		assertThat(value, is(FALSE));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotInitializeCloneComponentAttributeContextForNonComponentCloningFlow()
	{
		when(cloneComponentContextProvider.isInitialized()).thenReturn(FALSE);
		cmsItemConverter.initializeCloneComponentAttributeContext(UID, "clone_9876");
	}

	@Test
	public void shouldCloneComponentFromRepresentation() throws CMSItemNotFoundException
	{
		final AbstractCMSComponentModel sourceComponentModel = mock(AbstractCMSComponentModel.class);
		final AbstractCMSComponentModel cloneComponentModel = mock(AbstractCMSComponentModel.class);
		when(uniqueItemIdentifierService.getItemModel(anyString(), any())).thenReturn(Optional.of(sourceComponentModel));
		when(componentCloningStrategy.clone(sourceComponentModel, Optional.empty(), Optional.empty()))
				.thenReturn(cloneComponentModel);
		doReturn(new HashMap<>()).when(cmsItemConverter).convert(sourceComponentModel);
		doReturn(new HashMap<>()).when(cmsItemConverter).convert(cloneComponentModel);

		cmsItemConverter.getCloneModelFromRepresentation("#uuid_1234#");

		verify(componentCloningStrategy).clone(sourceComponentModel, Optional.empty(), Optional.empty());
		verify(cloneComponentContextProvider, times(2)).initializeItem(any());
	}

	@Test
	public void shouldNotCloneComponentFromRepresentationWithSessionAlreadyInitialized() throws CMSItemNotFoundException
	{
		when(cloneComponentContextProvider.isInitialized()).thenReturn(TRUE);

		cmsItemConverter.getCloneModelFromRepresentation("#uuid_1234#");

		verifyZeroInteractions(componentCloningStrategy);
		verify(cloneComponentContextProvider, times(0)).initializeItem(any());
		verify(cloneComponentContextProvider).findItemForKey(SESSION_CLONE_COMPONENT_CLONE_MODEL);
	}

	@Test
	public void shouldGetItemModelFromCloneComponentRepresentation()
	{
		final String sourceUuid = "#uuid_1234#";
		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, TRUE);
		doReturn(new AbstractCMSComponentModel()).when(cmsItemConverter).getCloneModelFromRepresentation(sourceUuid);

		cmsItemConverter.getItemModelFromRepresentation(inputMap);

		verify(cmsItemConverter).getCloneModelFromRepresentation(sourceUuid);
		verify(cmsItemConverter).isCloneComponentFlow(inputMap);
	}

	@Test
	public void shouldCreateItemModelFromRepresentation()
	{
		// GIVEN
		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(ITEMTYPE, CMSItemModel._TYPECODE);
		inputMap.put(FIELD_COMPONENT_UUID, null);
		inputMap.put(FIELD_CLONE_COMPONENT, FALSE);

		when(cmsItem.getItemtype()).thenReturn(CMSItemModel._TYPECODE);
		when(typeService.getComposedTypeForCode(CMSItemModel._TYPECODE)).thenReturn(composedType);
		doReturn(MainClass.class).when(typeService).getModelClass(composedType);

		// WHEN
		cmsItemConverter.getItemModelFromRepresentation(inputMap);

		// THEN
		verify(modelService).create(any(Class.class));
		verify(modelService).initDefaults(any());
	}

	@Test
	public void isCloneComponentFlowShouldReturnFalseForNonCloneableComponents()
	{
		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(UID, "comp_1234");

		assertThat(cmsItemConverter.isCloneComponentFlow(inputMap), is(FALSE));
	}

	@Test
	public void isCloneComponentFlowShouldReturnTrueForCloneableComponents()
	{
		final String sourceUuid = "#uuid_1234#";
		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, TRUE);

		assertThat(cmsItemConverter.isCloneComponentFlow(inputMap), is(TRUE));
	}

	@Test
	public void isDynamicAttributeShouldReturnTrueForDynamicAttributes()
	{
		// GIVEN
		when(attributeDescriptorModel1.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModel1.getProperty()).thenReturn(false);

		// WHEN
		final boolean result = cmsItemConverter.isDynamicAttribute(attributeDescriptorModel1);

		// THEN
		assertThat(result, is(true));
	}

	@Test
	public void isDynamicAttributeShouldReturnFalseForNonDynamicAttributes()
	{
		// GIVEN
		when(attributeDescriptorModel1.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModel1.getProperty()).thenReturn(true);

		// WHEN
		final boolean result = cmsItemConverter.isDynamicAttribute(attributeDescriptorModel1);

		// THEN
		assertThat(result, is(false));
	}

	@Test
	public void isDynamicAttributeShouldReturnFalseForRelationAttributes()
	{
		// GIVEN
		when(attributeDescriptorModel1.getItemtype()).thenReturn(RelationDescriptorModel._TYPECODE);
		when(attributeDescriptorModel1.getProperty()).thenReturn(false);

		// WHEN
		final boolean result = cmsItemConverter.isDynamicAttribute(attributeDescriptorModel1);

		// THEN
		assertThat(result, is(false));
	}

	@Test
	public void convertAndValidateShouldReturnAPopulatedItemModel()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_1, QUALIFIER_2, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldNotPopulateNonWritableAttributes()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);
		makeAttributeNonWritable(attributeDescriptorModel2);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_1, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldSkipAttributesWithNoReadPermissions()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);
		denyReadPermissionToPrincipal(CMSItemModel._TYPECODE, QUALIFIER_2);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_1, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldSkipAttributesWithNoWritePermissions()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);
		denyChangePermissionToPrincipal(CMSItemModel._TYPECODE, QUALIFIER_1);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_2, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldSkipComposedTypeAttributesWithNoChangePermissionForContainingType()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);

		when(attributeComposedType.getItemtype()).thenReturn(ComposedTypeModel._TYPECODE);
		when(attributeDescriptorModel1.getAttributeType()).thenReturn(attributeComposedType);
		when(cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptorModel1, PermissionsConstants.CHANGE))
				.thenReturn(FALSE);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_2, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldNotPopulateDynamicAttributes()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);
		makeAttributeDynamic(attributeDescriptorModel1);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		assertValuesFromMapWereSetInModel(map, QUALIFIER_2, QUALIFIER_3);
		assertCloneProviderWasHandled(result);
	}

	@Test
	public void convertAndValidateShouldCatchAttributeValidationErrorsAndCollectThem()
	{
		// GIVEN
		final Map<String, Object> map = buildTestDataMap();
		setUpDataToModelTest(map);
		makeAttributeThrowExceptionWhenValidated(attributeDescriptorModel2, AttributeNotSupportedException.class);
		makeAttributeThrowExceptionWhenValidated(relationDescriptorModel, ValidationException.class);

		// WHEN
		final ItemModel result = cmsItemConverter.convertAndValidate(map, composedType);

		// THEN
		verify(cmsItemConverter, times(3)).validate(any(), any(), any());
		verify(cmsItemConverter).collectValidationErrors(any(ValidationException.class), any(), any());
		verify(cmsItemValidator).validate(result);

		assertValuesFromMapWereSetInModel(map, QUALIFIER_1);
		assertCloneProviderWasHandled(result);
	}


	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected Map<String, Object> buildTestDataMap()
	{
		final Map<String, Object> testMap = new HashMap<>();
		testMap.put(QUALIFIER_1, ATTR_1_VALUE);
		testMap.put(QUALIFIER_2, ATTR_2_VALUE);
		testMap.put(QUALIFIER_3, ATTR_3_VALUE);

		return testMap;
	}

	protected void setUpAttributeDescriptorWithDefaultValues(final AttributeDescriptorModel attr, final String qualifier)
	{
		final String itemType = (attr instanceof RelationDescriptorModel) ? RelationDescriptorModel._TYPECODE
				: AttributeDescriptorModel._TYPECODE;

		when(attr.getQualifier()).thenReturn(qualifier);
		when(attr.getPartOf()).thenReturn(true);
		when(attr.getWritable()).thenReturn(true);
		when(attr.getProperty()).thenReturn(true);
		when(attr.getItemtype()).thenReturn(itemType);
	}

	protected void setUpDataToModelTest(final Map<String, Object> testMap)
	{
		// Item Model
		final ItemModel itemModel = new ItemModel();

		// Attribute Descriptors
		setUpAttributeDescriptorWithDefaultValues(attributeDescriptorModel1, QUALIFIER_1);
		setUpAttributeDescriptorWithDefaultValues(attributeDescriptorModel2, QUALIFIER_2);
		setUpAttributeDescriptorWithDefaultValues(relationDescriptorModel, QUALIFIER_3);

		when(typeService.getAttributeDescriptor(CMSItemModel._TYPECODE, QUALIFIER_1)).thenReturn(attributeDescriptorModel1);
		when(typeService.getAttributeDescriptor(CMSItemModel._TYPECODE, QUALIFIER_2)).thenReturn(attributeDescriptorModel2);
		when(typeService.getAttributeDescriptor(CMSItemModel._TYPECODE, QUALIFIER_3)).thenReturn(relationDescriptorModel);

		// Cms Item Converter
		cmsItemConverter.setComposedTypeToAttributeCollectionConverter(composedTypeToAttributeCollectionConverter);

		doReturn(itemModel).when(cmsItemConverter).getItemModelFromRepresentation(testMap);
		doNothing().when(cmsItemConverter).validate(any(), any(), any());
		doNothing().when(cmsItemConverter).collectValidationErrors(any(), any(), any());
		doReturn(Arrays.asList(attributeDescriptorModel1, attributeDescriptorModel2, relationDescriptorModel))
				.when(composedTypeToAttributeCollectionConverter).convert(composedType);

		// Representation to Attribute
		doAnswer(invocation -> (Function<AttributeDescriptorModel, Optional<Object>>) attr -> Optional
				.of(invocation.getArgumentAt(0, Object.class))).when(cmsItemConverter).convertRepresentationToAttributeValue(any(), any());
	}

	protected void makeAttributeNonWritable(final AttributeDescriptorModel attr)
	{
		when(attr.getWritable()).thenReturn(false);
	}

	protected void denyChangePermissionToPrincipal(final String typeCode, final String qualifier)
	{
		when(permissionCRUDService.canChangeAttribute(typeCode, qualifier)).thenReturn(false);
	}

	protected void denyReadPermissionToPrincipal(final String typeCode, final String qualifier)
	{
		when(permissionCRUDService.canReadAttribute(typeCode, qualifier)).thenReturn(false);
	}

	protected void makeAttributeDynamic(final AttributeDescriptorModel attr)
	{
		when(attr.getProperty()).thenReturn(false);
	}

	protected <T extends RuntimeException> void makeAttributeThrowExceptionWhenValidated(final AttributeDescriptorModel attr,
			final Class<T> expectedExceptionClass)
	{
		doThrow(expectedExceptionClass).when(cmsItemConverter).validate(any(), eq(attr), any());
	}

	protected void assertValuesFromMapWereSetInModel(final Map<String, Object> originalMap, final String... expectedValues)
	{
		verify(modelService, times(expectedValues.length)).setAttributeValue(any(ItemModel.class), qualifierCaptor.capture(),
				valueCaptor.capture());
		for (int i = 0; i < expectedValues.length; i++)
		{
			final String expectedKey = expectedValues[i];
			assertThat(qualifierCaptor.getAllValues().get(i), is(expectedKey));
			assertThat(valueCaptor.getAllValues().get(i), is(originalMap.get(expectedKey)));
		}
	}

	protected void assertCloneProviderWasHandled(final ItemModel itemModel)
	{
		verify(originalClonedItemProvider).initializeItem(itemModel);
		verify(originalClonedItemProvider).finalizeItem();
	}
}
