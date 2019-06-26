/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder;
import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;
import static de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessorTestUtils.typeAttributeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.service.AttributeDescriptorNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.creation.NeverCreateItemStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EntityPropertyProcessorUnitTest
{
	private static final String IS_UNIQUE = "s:IsUnique";
	private static final String IS_PART_OF = "s:IsPartOf";
	private static final String IS_AUTO_CREATE = "s:IsAutoCreate";
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final String INTEGRATION_OBJECT_CODE = "IntegrationObjectType";

	@Mock
	private ModelService modelService;
	@Mock
	private ModelEntityService entityService;
	@Mock
	private TypeService typeService;
	@Mock
	private IntegrationObjectService integrationObjectService;
	@Mock
	private EdmEntitySet entitySet;
	@Mock
	private EdmEntityType entityType;
	@Mock
	private ODataEntryImpl oDataEntry;
	@Mock
	private ItemModel item;
	@Mock
	private ItemConversionRequest conversionRequest;
	@InjectMocks
	@Spy
	private EntityPropertyProcessor propertyProcessor;
	private Map<String, Object> properties;
	private StorageRequest storageRequest;

	@Before
	public void setUp() throws EdmException
	{
		properties = Maps.newHashMap();

		when(conversionRequest.getEntityType()).thenReturn(entityType);
		when(conversionRequest.getItemModel()).thenReturn(item);
		when(conversionRequest.getOptions()).thenReturn(conversionOptionsBuilder().build());

		when(oDataEntry.getProperties()).thenReturn(properties);
		when(item.getItemtype()).thenReturn("MyType");
		when(entityType.getName()).thenReturn("MyType");
		when(entityType.getPropertyNames()).thenReturn(new ArrayList<>());

		when(entityService.createOrUpdateItem(any(), any())).thenReturn(mock(ItemModel.class));
		when(entityService.getODataEntry(any())).thenReturn(mock(ODataEntry.class));

		storageRequest = storageRequest();
	}

	private StorageRequest storageRequest() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(entityType);
		final EdmEntityContainer entityContainer = mock(EdmEntityContainer.class);
		when(entitySet.getEntityType()).thenReturn(entityType);
		when(entitySet.getEntityContainer()).thenReturn(entityContainer);
		when(entityContainer.getEntitySet(any())).thenReturn(entitySet);

		final StorageRequest request = storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(LOCALE)
				.withAcceptLocale(LOCALE)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(INTEGRATION_OBJECT_CODE)
				.build();
		return request;
	}

	private void givenProperty(final String name, final String... annotations) throws EdmException
	{
		final ODataEntry entry1 = mock(ODataEntryImpl.class);
		this.properties.put(name, entry1);

		final EdmNavigationProperty edmNavigationProperty = mock(EdmNavigationProperty.class);
		when(edmNavigationProperty.getName()).thenReturn(name);
		when(entityType.getProperty(name)).thenReturn(edmNavigationProperty);

		when(edmNavigationProperty.getMultiplicity()).thenReturn(EdmMultiplicity.ONE);
		final EdmType edmType = mock(EdmType.class);
		when(edmType.getKind()).thenReturn(EdmTypeKind.ENTITY);
		when(edmNavigationProperty.getType()).thenReturn(edmType);
		when(edmType.getName()).thenReturn("edmTypedName");

		final EdmAnnotations edmAnnotations = mock(EdmAnnotations.class);

		final List<EdmAnnotationAttribute> attributes = Arrays.stream(annotations).map(a -> {
			final EdmAnnotationAttribute annotationAttribute = mock(EdmAnnotationAttribute.class);
			when(annotationAttribute.getName()).thenReturn(a);
			when(annotationAttribute.getText()).thenReturn("true");
			return annotationAttribute;
		}).collect(Collectors.toList());

		when(edmAnnotations.getAnnotationAttributes()).thenReturn(attributes);
		when(edmNavigationProperty.getAnnotations()).thenReturn(edmAnnotations);

		mockAttributeDescriptor(name, entityType.getName());
	}

	private void givenPropertyForItem(final String name, final Object value, final String... annotations) throws EdmException
	{
		givenProperty(name, annotations);
		entityType.getPropertyNames().add(name);
		this.properties.remove(name);

		when(modelService.getAttributeValue(item, name)).thenReturn(value);
		when(modelService.getAttributeValue(eq(item), eq(name), any())).thenReturn(value);
	}

	private void mockAttributeDescriptor(final String name, final String parentType)
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		when(attributeDescriptorModel.getLocalized()).thenReturn(false);
		when(attributeDescriptorModel.getItemtype()).thenReturn(parentType);
		when(attributeDescriptorModel.getName()).thenReturn(name);
		when(attributeDescriptorModel.getQualifier()).thenReturn(name);
		when(attributeDescriptorModel.getWritable()).thenReturn(true);
		doAnswer(i -> attributeDescriptorModel)
				.when(typeService)
				.getAttributeDescriptor(anyString(), eq(name));
		when(integrationObjectService.findItemAttributeName(anyString(), eq(parentType), eq(name))).thenReturn(name);
	}

	@Test
	public void testIsPropertySupportedWithNonPrimitiveCollection()
	{
		final TypeAttributeDescriptor descriptor = typeAttributeDescriptor(true, false);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(descriptor), "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveCollection()
	{
		final TypeAttributeDescriptor descriptor = typeAttributeDescriptor(true, true);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(descriptor), "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveValue()
	{
		final TypeAttributeDescriptor descriptor = typeAttributeDescriptor(false, true);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(descriptor), "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithNonPrimitiveValue()
	{
		final TypeAttributeDescriptor descriptor = typeAttributeDescriptor(false, false);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(descriptor), "a")).isTrue();
	}

	@Test
	public void testEntityWithNoProperties() throws EdmException
	{
		propertyProcessor.processItem(item, storageRequest);

		verifySetAttributeValueIsNotCalled();
	}

	@Test
	public void testEntityWithNoSupportedProperties() throws EdmException
	{
		givenProperty("a");
		when(entityType.getProperty("a")).thenReturn(null);
		givenProperty("b");
		when(entityType.getProperty("b").getMultiplicity()).thenReturn(EdmMultiplicity.MANY);
		givenProperty("c");
		when(entityType.getProperty("c").getType().getKind()).thenReturn(EdmTypeKind.COMPLEX);
		givenProperty(INTEGRATION_KEY_PROPERTY_NAME);
		givenIsPropertySupported(false);

		propertyProcessor.processItem(item, storageRequest);

		verifySetAttributeValueIsNotCalled();
	}

	@Test
	public void testEntityWithSupportedProperties_NoSettableProperties() throws EdmException
	{
		givenProperty("a");
		givenProperty("b");
		givenProperty("c");

		makeAttributesNonWritable("a", "b", "c");
		when(modelService.isNew(item)).thenReturn(false);

		propertyProcessor.processItem(item, storageRequest);

		verifySetAttributeValueIsNotCalled();
	}

	@Test
	public void testEntityWithSupportedProperties_IsPartOfTrue() throws EdmException
	{
		givenIsPropertySupported(true);
		final String propertyName = processAnItemWithAttributeAnnotation(IS_PART_OF);

		verify(entityService).createOrUpdateItem(any(StorageRequest.class), any(CreateItemStrategy.class));
		verify(modelService).setAttributeValue(item, propertyName, item);
	}

	@Test
	public void testEntityWithSupportedPropertiesIsAutoCreateTrue() throws EdmException
	{
		givenIsPropertySupported(true);
		final String propertyName = processAnItemWithAttributeAnnotation(IS_AUTO_CREATE);

		verify(entityService).createOrUpdateItem(any(StorageRequest.class), any(CreateItemStrategy.class));
		verify(modelService).setAttributeValue(item, propertyName, item);
	}

	@Test
	public void testEntityWithPropertyAutoCreateTrueNeverCallsSetOwner() throws EdmException
	{
		givenIsPropertySupported(true);
		processAnItemWithAttributeAnnotation(IS_AUTO_CREATE);

		verify(item, never()).setOwner(any());
	}

	@Test
	public void testInnerStorageRequestHasIntegrationKeySet() throws EdmException
	{
		givenProperty("a");
		final ODataEntry a = (ODataEntry) oDataEntry.getProperties().get("a");
		when(entityService.addIntegrationKeyToODataEntry(any(), eq(a))).thenReturn("123|abc");
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		final StorageRequest capturedRequest = captureInnerStorageRequest();
		assertThat(capturedRequest.getIntegrationKey()).isEqualTo("123|abc");
	}

	private StorageRequest captureInnerStorageRequest() throws EdmException
	{
		final ArgumentCaptor<StorageRequest> requestCaptor = ArgumentCaptor.forClass(StorageRequest.class);
		verify(entityService).createOrUpdateItem(requestCaptor.capture(), any());
		return requestCaptor.getValue();
	}

	private String processAnItemWithAttributeAnnotation(final String annotation) throws EdmException
	{
		final String propertyName = "a";
		givenProperty(propertyName, annotation);

		when(modelService.isNew(item)).thenReturn(true);
		when(entityService.createOrUpdateItem(any(), any(CreateItemStrategy.class))).thenReturn(item);

		propertyProcessor.processItem(item, storageRequest);
		return propertyName;
	}

	@Test
	public void testEntityWithSupportedProperties_IsPartOfFalse() throws EdmException
	{
		final String propertyName = "a";
		givenProperty(propertyName);

		when(modelService.isNew(item)).thenReturn(true);
		givenIsPropertySupported(true);

		doThrow(InvalidDataException.class).when(entityService)
				.createOrUpdateItem(any(), any(NeverCreateItemStrategy.class));

		assertThatThrownBy(() -> propertyProcessor.processItem(item, storageRequest))
				.isInstanceOf(InvalidDataException.class);
	}

	@Test
	public void testEntityWithSupportedProperties_WithSettableProperties_ExistingItem() throws EdmException
	{
		givenProperty("a");
		givenProperty("b");

		when(modelService.isNew(item)).thenReturn(true);

		final ItemModel existingItem = mock(ItemModel.class);
		when(entityService.createOrUpdateItem(any(), any())).thenReturn(existingItem);
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService).setAttributeValue(item, "a", existingItem);
		verify(modelService).setAttributeValue(item, "b", existingItem);
	}

	@Test
	public void testEntityWithSupportedProperties_WithSettableProperties_NewItem() throws EdmException
	{
		givenProperty("a");
		givenProperty("b");

		when(modelService.isNew(item)).thenReturn(false);

		final ItemModel existingItem = mock(ItemModel.class);
		when(entityService.createOrUpdateItem(any(), any())).thenReturn(existingItem);
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService).setAttributeValue(item, "a", existingItem);
		verify(modelService).setAttributeValue(item, "b", existingItem);
	}

	@Test
	public void testEntityWithSupportedProperties_NoWritableProperties() throws EdmException
	{
		givenProperty("a");
		givenProperty("b");
		makeAttributesNonWritable("a", "b");

		when(modelService.isNew(item)).thenReturn(false);

		propertyProcessor.processItem(item, storageRequest);

		verifySetAttributeValueIsNotCalled();
	}

	@Test
	public void testEntityWithSupportedProperties_WithSettableProperties_Exception() throws EdmException
	{
		givenProperty("a");
		givenProperty("b");

		when(modelService.isNew(item)).thenReturn(true);

		doThrow(EdmException.class).when(entityService).createOrUpdateItem(any(), any());
		givenIsPropertySupported(true);

		assertThatThrownBy(() -> propertyProcessor.processItem(item, storageRequest))
				.isInstanceOf(EdmException.class);

		verify(modelService, never()).setAttributeValue(eq(item), anyString(), any());
	}

	@Test
	public void testPartOfRelation_ToSetOwnerAsForeignKey() throws EdmException
	{
		givenIsPropertySupported(true);
		processAnItemWithAttributeAnnotation(IS_PART_OF);

		verify(entityService).createOrUpdateItem(any(StorageRequest.class), any(CreateItemStrategy.class));
		verify(item).setOwner(item);
	}

	@Test
	public void testPartOfRelation_NotToOverrideOwnerForExistingItem() throws EdmException
	{
		final String propertyName = "a";
		givenProperty(propertyName, IS_PART_OF);

		when(modelService.isNew(item)).thenReturn(false);

		when(entityService.createOrUpdateItem(any(), any(CreateItemStrategy.class))).thenReturn(item);

		makeAttributesNonWritable("a");
		when(entityService.createOrUpdateItem(any(), any(CreateItemStrategy.class))).thenReturn(item);

		propertyProcessor.processItem(item, storageRequest);

		verify(item, times(0)).setOwner(item);
	}

	@Test
	public void testEnumerationValue() throws EdmException
	{
		final ItemModel enumerationValueModel = mock(EnumerationValueModel.class);
		final PK mockPK = PK.BIG_PK;

		when(enumerationValueModel.getPk()).thenReturn(mockPK);
		when(modelService.get(mockPK)).thenReturn(enumerationValueModel);

		final String propertyName = "a";
		givenProperty(propertyName, IS_PART_OF);

		when(modelService.isNew(enumerationValueModel)).thenReturn(false);

		when(entityService.createOrUpdateItem(any(), any())).thenReturn(enumerationValueModel);
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService).get(mockPK);
		verify(modelService).setAttributeValue(item, propertyName, enumerationValueModel);
	}

	private void makeAttributesNonWritable(final String... attributeNames)
	{
		for(final String name : attributeNames)
		{
			final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor("itemModel", name);
			when(attributeDescriptor.getWritable()).thenReturn(false);
		}
	}

	@Test
	public void testItemNotConvertedWhenPropertyIsNotSupported() throws EdmException
	{
		givenPropertyForItem("a", mock(ItemModel.class), IS_PART_OF);
		when(entityType.getProperty("a")).thenReturn(null);
		givenPropertyForItem("b", mock(ItemModel.class), IS_PART_OF);
		when(entityType.getProperty("b").getMultiplicity()).thenReturn(EdmMultiplicity.MANY);
		givenPropertyForItem("c", mock(ItemModel.class), IS_PART_OF);
		when(entityType.getProperty("c").getType().getKind()).thenReturn(EdmTypeKind.COMPLEX);
		givenPropertyForItem(INTEGRATION_KEY_PROPERTY_NAME, mock(ItemModel.class), IS_PART_OF);
		when(entityType.getProperty(INTEGRATION_KEY_PROPERTY_NAME).getType().getKind()).thenReturn(EdmTypeKind.ENTITY);

		givenIsPropertySupported(false);

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		verify(modelService, never()).getAttributeValue(item, "a");
		verify(modelService, never()).getAttributeValue(item, "b", LOCALE);
		verify(modelService, never()).getAttributeValue(item, "c");
		verify(modelService, never()).getAttributeValue(item, INTEGRATION_KEY_PROPERTY_NAME);

		assertThat(oDataEntry.getProperties()).isEmpty();
	}

	@Test
	public void testItemNotConvertedWhenEntityHasNoProperties() throws EdmException
	{
		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		assertThat(oDataEntry.getProperties()).isEmpty();
		verify(modelService, never()).getAttributeValue(eq(item), anyString());
		verify(modelService, never()).getAttributeValue(eq(item), anyString(), any());
	}

	@Test
	public void testItemConvertedWithSupportedProperties_WithSettableProperties() throws EdmException
	{
		final ItemModel itemValue = mock(ItemModel.class);
		final EnumerationValueModel enumValue = mock(EnumerationValueModel.class);
		when(enumValue.getCode()).thenReturn("code");
		final HybrisEnumValue hybrisEnumValue = mock(HybrisEnumValue.class);
		when(hybrisEnumValue.getCode()).thenReturn("someHybrisEnumCode");
		givenPropertyForItem("a", itemValue);
		givenPropertyForItem("b", enumValue);
		givenPropertyForItem("c", hybrisEnumValue);

		givenIsPropertySupported(true);

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		verify(modelService).getAttributeValue(item, "a");
		verify(modelService).getAttributeValue(item, "b");
		verify(modelService).getAttributeValue(item, "c");

		assertThat(oDataEntry.getProperties())
				.contains(entry("b", "code"))
				.containsKey("c")
				.containsKey("a");
		assertThat(oDataEntry.getProperties().get("a")).isInstanceOf(ODataEntry.class);
		assertThat(oDataEntry.getProperties().get("c")).isInstanceOf(ODataEntry.class);
	}

	@Test
	public void testAttributeDescriptorNotFoundExceptionIsRethrown_processEntity() throws EdmException
	{
		givenIsPropertySupported(true);
		givenPropertyForItem("c", false, "some value");

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> propertyProcessor.processEntity(oDataEntry, conversionRequest))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}

	@Test
	public void testAttributeDescriptorNotFoundExceptionIsRethrown_processItem() throws EdmException
	{
		givenProperty("a");
		when(modelService.isNew(item)).thenReturn(true);

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> propertyProcessor.processItem(item, storageRequest))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}

	@Test
	public void testItemNotConvertedWhenNestedNavigationPropertyProvided() throws EdmException
	{
		givenIsPropertySupported(true);
		givenPropertyForItem("item", mock(ItemModel.class));

		doReturn(optionsWithNavigationSegments("item")).when(conversionRequest).getOptions();
		doReturn(1).when(conversionRequest).getConversionLevel();

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		assertThat(oDataEntry.getProperties()).doesNotContainKey("item");
	}

	@Test
	public void testItemConverted_whenTopLevelNavigationPropertyIsProvided() throws EdmException
	{
		givenIsPropertySupported(true);
		final ItemModel itemModel = mock(ItemModel.class);
		givenPropertyForItem("a", itemModel);
		givenPropertyForItem("b", itemModel);
		givenPropertyForItem("c", itemModel);

		doReturn(optionsWithNavigationSegments("a")).when(conversionRequest).getOptions();

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		assertThat(oDataEntry.getProperties())
				.isNotEmpty()
				.doesNotContainKey("b")
				.doesNotContainKey("c");
		assertThat(oDataEntry.getProperties().get("a")).isInstanceOf(ODataEntry.class);
	}

	@Test
	public void testItemConverted_whenNavigationPropertyIsKeyProperty() throws EdmException
	{
		final ItemModel itemModel1 = mock(ItemModel.class);
		givenPropertyForItem("code", itemModel1, IS_UNIQUE);

		doReturn(2).when(conversionRequest).getConversionLevel();

		givenIsPropertySupported(true);

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		assertThat(oDataEntry.getProperties()).isNotEmpty();
		verify(modelService).getAttributeValue(item, "code");

		assertThat(oDataEntry.getProperties().get("code")).isInstanceOf(ODataEntry.class);
	}

	private void givenIsPropertySupported(final boolean propertySupported)
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(false, false);
		when(attributeDescriptor.isCollection()).thenReturn(!propertySupported);

		doReturn(Optional.of(attributeDescriptor)).when(propertyProcessor).findTypeDescriptorAttributeForItem(any(IntegrationObjectItemModel.class), any(String.class));
	}

	private NavigationSegment givenNavigationSegment(final String name) throws EdmException
	{
		final NavigationSegment navigationSegment = mock(NavigationSegment.class);
		final EdmNavigationProperty navigationProperty = mock(EdmNavigationProperty.class);
		when(navigationProperty.getName()).thenReturn(name);
		when(navigationSegment.getNavigationProperty()).thenReturn(navigationProperty);
		return navigationSegment;
	}

	private void verifySetAttributeValueIsNotCalled()
	{
		verify(modelService, never()).setAttributeValue(eq(item), anyString(), anyMap());
		verify(modelService, never()).setAttributeValue(eq(item), anyString(), anyObject());
	}

	private ConversionOptions optionsWithNavigationSegments(final String... names) throws EdmException
	{
		final List<NavigationSegment> navigationSegments = new ArrayList<>(names.length);
		for (final String name : names)
		{
			navigationSegments.add(givenNavigationSegment(name));
		}
		return conversionOptionsBuilder().withNavigationSegments(navigationSegments).build();
	}
}
