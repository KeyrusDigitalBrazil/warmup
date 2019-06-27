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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.TypeOfCollectionEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.service.AttributeDescriptorNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
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
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.core.edm.provider.EdmNavigationPropertyImplProv;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EntityCollectionPropertyProcessorUnitTest
{
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final String IS_PART_OF = "s:IsPartOf";
	private static final String IS_AUTO_CREATE = "s:IsAutoCreate";
	private static final ItemModel ITEM = mock(ItemModel.class);
	private static final String INTEGRATION_OBJECT_CODE = "IntegrationObjectType";
	private static final String ITEM_TYPE = "TypeA";
	private static final String TEST_ATTRIBUTE = "attributeName";

	private Map<String, Object> entryProperties;

	@Mock
	private ModelService modelService;
	@Mock
	private ModelEntityService modelEntityService;
	@Mock
	private IntegrationObjectService integrationObjectService;
	@Mock
	private TypeService typeService;
	@Mock
	private EdmNavigationPropertyImplProv edmTyped;
	@Mock
	private EdmEntitySet entitySet;
	@Mock
	private EdmEntityType entityType;
	@Mock
	private ODataEntry oDataEntry;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Spy
	@InjectMocks
	private EntityCollectionPropertyProcessor collectionProcessor;
	@Captor
	private ArgumentCaptor<Collection> collectionCaptor;

	@Before
	public void setUp() throws EdmException
	{
		entryProperties = Maps.newHashMap();
		when(entitySet.getEntityType()).thenReturn(entityType);
		when(entityType.getPropertyNames()).thenAnswer(i -> Lists.newArrayList(entryProperties.keySet()));

		when(oDataEntry.getProperties()).thenReturn(entryProperties);

		when(ITEM.getItemtype()).thenReturn("MyType");
		when(typeService.getAttributeDescriptor(anyString(), anyString())).thenAnswer(mockAnswer());

	}

	private Answer<AttributeDescriptorModel> mockAnswer()
	{
		return i -> {
			when(attributeDescriptor.getItemtype()).thenReturn(i.getArgumentAt(0, String.class));
			when(attributeDescriptor.getQualifier()).thenReturn(i.getArgumentAt(1, String.class));
			return attributeDescriptor;
		};
	}

	@Test
	public void testIsPropertySupportedWithNoMatchingAttribute()
	{
		assertThat(collectionProcessor.isPropertySupported(Optional.empty(), "test")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveCollection()
	{
		final Optional<TypeAttributeDescriptor> descriptor = Optional.of(typeAttributeDescriptor(true,true));

		assertThat(collectionProcessor.isPropertySupported(descriptor, "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithNotPrimitiveCollection()
	{
		final Optional<TypeAttributeDescriptor> descriptor = Optional.of(typeAttributeDescriptor(true,false));

		assertThat(collectionProcessor.isPropertySupported(descriptor, "a")).isTrue();
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveValue()
	{
		final Optional<TypeAttributeDescriptor>  descriptor = Optional.of(typeAttributeDescriptor(false, true));

		assertThat(collectionProcessor.isPropertySupported(descriptor, "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithNonPrimitiveValue()
	{
		final Optional<TypeAttributeDescriptor>  descriptor = Optional.of(typeAttributeDescriptor(false, false));

		assertThat(collectionProcessor.isPropertySupported(descriptor, "a")).isFalse();
	}

	@Test
	public void testItemWithNoProperties() throws EdmException
	{
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelService, never()).setAttributeValue(eq(ITEM), anyString(), any());
		verify(modelEntityService, never()).createOrUpdateItem(any(), any());
	}

	@Test
	public void testEntityNoSettableProperties() throws EdmException
	{
		givenProperty(TEST_ATTRIBUTE);
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		when(attributeDescriptor.getWritable()).thenReturn(false);

		when(modelService.isNew(ITEM)).thenReturn(false);

		assertThat(collectionProcessor.isItemPropertySettable(ITEM, TEST_ATTRIBUTE, storageRequest())).isEqualTo(false);
	}

	@Test
	public void testEntitySettableProperties() throws EdmException
	{
		givenProperty(TEST_ATTRIBUTE);
		propertyOfType(TypeOfCollectionEnum.COLLECTION);

		when(modelService.isNew(ITEM)).thenReturn(true);

		assertThat(collectionProcessor.isItemPropertySettable(ITEM, TEST_ATTRIBUTE, storageRequest())).isEqualTo(true);
	}

	@Test
	public void testProcessItemWithIntegrationKey() throws EdmException
	{
		givenProperty(INTEGRATION_KEY_PROPERTY_NAME, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, never()).createOrUpdateItem(any(), any());
		verify(modelService, never()).setAttributeValue(any(), anyString(), any());
	}

	@Test
	public void testProcessSuccessWithMergeOfCollectionEntries() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(3);
	}

	@Test
	public void testAddNewItemToCollectionWhenAutoCreateIsTrue() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_AUTO_CREATE);
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(any())).thenReturn(true);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(3);
	}

	private void givenProperty(final String name, final Object... values) throws EdmException
	{
		final ODataFeed entry = mock(ODataFeed.class);
		entryProperties.put(name, entry);

		final EdmType edmType = mock(EdmType.class);
		when(edmType.getKind()).thenReturn(EdmTypeKind.ENTITY);
		when(edmType.getName()).thenReturn(ITEM_TYPE);
		when(edmTyped.getMultiplicity()).thenReturn(EdmMultiplicity.MANY);
		when(edmTyped.getType()).thenReturn(edmType);
		when(entityType.getProperty(name)).thenReturn(edmTyped);

		final ODataEntry dataEntry = mock(ODataEntry.class);
		when(entry.getEntries()).thenReturn(Arrays.asList(dataEntry, dataEntry));
		when(modelService.getAttributeValue(ITEM, name)).thenReturn(Lists.newArrayList(values));

		when(attributeDescriptor.getWritable()).thenReturn(true);
		when(attributeDescriptor.getName()).thenReturn(name);
		when(attributeDescriptor.getQualifier()).thenReturn(name);
		when(attributeDescriptor.getItemtype()).thenReturn(ITEM_TYPE);
		doReturn(attributeDescriptor).when(typeService).getAttributeDescriptor(eq(ITEM_TYPE), eq(name));
	}

	@Test
	public void testProcessSuccessForTwoPartOfCollectionEntries() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(2);
	}

	@Test
	public void testProcessSuccessForTwoPartOfCollectionEntriesWithSet() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);

		propertyOfType(TypeOfCollectionEnum.SET);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		when(modelEntityService.createOrUpdateItem(any(), any())).thenReturn(mock(ItemModel.class));
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(1);  // it's a set.
	}

	@Test
	public void testProcessSuccessForTwoCollectionEntries() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations("SOME_OTHER_ANNOTATION");
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(2);
	}

	@Test
	public void testProcessSuccessForPropertyOfMapType() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);

		propertyOfMapType();
		prepareAnnotations("SOME_OTHER_ANNOTATION");
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(2);
	}

	@Test
	public void testProcessSuccessForTwoCollectionEntriesWhenExistingItemOnAttributeIsNotDuplicatedInCollection() throws EdmException
	{
		givenIsPropertySupported();
		final ItemModel existingItem = mock(ItemModel.class);
		givenProperty(TEST_ATTRIBUTE, existingItem);

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations("SOME_OTHER_ANNOTATION");
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(ITEM)).thenReturn(false);
		when(modelEntityService.createOrUpdateItem(any(), any())).thenReturn(existingItem, mock(ItemModel.class));
		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelEntityService, times(2)).createOrUpdateItem(any(), any());
		verify(modelService).setAttributeValue(eq(ITEM), anyString(), collectionCaptor.capture());
		assertThat(collectionCaptor.getValue()).hasSize(2);
	}

	@Test
	public void testExceptionWhileProcess() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();
		doThrow(EdmException.class).when(entityType).getProperty(any());

		assertThatThrownBy(() -> collectionProcessor.processItem(ITEM, storageRequest()))
				.isInstanceOf(EdmException.class);
	}

	@Test
	public void testPartOfRelationToSetOwnerAsForeignKey() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		final ItemModel modelToCreate = mock(ItemModel.class);
		when(modelEntityService.createOrUpdateItem(any(), any())).thenReturn(modelToCreate);
		when(modelService.isNew(modelToCreate)).thenReturn(true);

		collectionProcessor.processItem(ITEM, storageRequest());

		// it called twice because it has two collection entries.
		verify(modelToCreate, times(2)).setOwner(eq(ITEM));
	}

	@Test
	public void testAutoCreateAttributeDoesNotSetOwner() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations(IS_AUTO_CREATE);
		prepareGetEntitySetReferencedByProperty();

		final ItemModel modelToCreate = mock(ItemModel.class);
		when(modelEntityService.createOrUpdateItem(any(), any())).thenReturn(modelToCreate);
		when(modelService.isNew(modelToCreate)).thenReturn(true);

		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelToCreate, never()).setOwner(eq(ITEM));
	}

	@Test
	public void testInnerStorageRequestHasIntegrationKeySet() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);
		prepareAnnotations(IS_PART_OF);
		prepareGetEntitySetReferencedByProperty();

		final ODataFeed aFeed = (ODataFeed) oDataEntry.getProperties().get(TEST_ATTRIBUTE);
		final ODataEntry a = aFeed.getEntries().get(0);

		when(modelEntityService.addIntegrationKeyToODataEntry(any(), eq(a))).thenReturn("123|abc");

		collectionProcessor.processItem(ITEM, storageRequest());

		final ArgumentCaptor<StorageRequest> requestCaptor = ArgumentCaptor.forClass(StorageRequest.class);
		verify(modelEntityService, times(2)).createOrUpdateItem(requestCaptor.capture(), any());

		final List<StorageRequest> capturedRequests = requestCaptor.getAllValues();
		capturedRequests.forEach(r -> assertThat(r.getIntegrationKey()).isEqualTo("123|abc"));
	}

	@Test
	public void testPartOfRelationNotToOverrideOwnerForExistingItem() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));

		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations("FOOBAR");
		prepareGetEntitySetReferencedByProperty();

		final ItemModel modelToCreate = mock(ItemModel.class);
		when(modelEntityService.createOrUpdateItem(any(), any())).thenReturn(modelToCreate);
		when(modelService.isNew(modelToCreate)).thenReturn(false);

		collectionProcessor.processItem(ITEM, storageRequest());

		verify(modelToCreate, times(0)).setOwner(eq(ITEM));
	}


	@Test
	public void testNotPartOfAndNewCollectionItemThrowsException() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareAnnotations("BARFOO");
		prepareGetEntitySetReferencedByProperty();

		when(modelService.isNew(any())).thenReturn(true);

		assertThatThrownBy(() -> collectionProcessor.processItem(ITEM, storageRequest()))
				.isInstanceOf(InvalidDataException.class)
				.hasMessageStartingWith("Required NavigationProperty for EntityType [")
				.hasMessageEndingWith("] does not exist in the System.");
	}

	@Test
	public void testPopulateEntryEmptyCollection() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE);
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();

		final ODataEntry entry = oDataEntry();
		collectionProcessor.processEntity(entry, conversionRequest());

		verify(modelEntityService, never()).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).isEmpty();
	}

	@Test
	public void testPopulateEntrySingletonCollection() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();

		final ODataEntry entry = oDataEntry();
		collectionProcessor.processEntity(entry, conversionRequest());

		verify(modelEntityService).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).hasSize(1);
	}

	@Test
	public void testPopulateEntryCollection() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();

		final ODataEntry entry = oDataEntry();
		collectionProcessor.processEntity(entry, conversionRequest());

		verify(modelEntityService, times(2)).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).hasSize(2);
	}

	@Test
	public void testPopulateEntryCollectionWhenNavigationSegmentProvidedIsIncluded() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();

		final ODataEntry entry = oDataEntry();
		final ConversionOptions options = conversionOptionsBuilder().withNavigationSegment(navigationSegment(TEST_ATTRIBUTE)).build();

		collectionProcessor.processEntity(entry, conversionRequest(options));

		verify(modelEntityService, times(2)).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).hasSize(2);
	}

	@Test
	public void testPopulateEntryCollectionWhenNavigationSegmentProvidedNotIncluded() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);
		prepareGetEntitySetReferencedByProperty();
		prepareAnnotations(IS_PART_OF);

		final ODataEntry entry = oDataEntry();
		final ConversionOptions options = conversionOptionsBuilder().withNavigationSegment(navigationSegment("b")).build();

		collectionProcessor.processEntity(entry, conversionRequest(options));

		verify(modelEntityService, never()).getODataEntry(any());
		assertThat(entry.getProperties()).isEmpty();
	}


	@Test
	public void testPopulateEntryWithApplyInnerPopulationFalse() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);

		final ConversionOptions options = conversionOptionsBuilder().withIncludeCollections(false).build();
		collectionProcessor.processEntity(oDataEntry(), conversionRequest(options));

		verify(modelEntityService, never()).getODataEntry(any());
	}

	@Test
	public void testPopulateEntrySet() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.SET);

		final ODataEntry entry = oDataEntry();
		collectionProcessor.processEntity(entry, conversionRequest());

		verify(modelEntityService, times(2)).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).hasSize(2);
	}

	@Test
	public void testPopulateEntrySetWhenPropertyValueIsNotCollection() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(Object.class));
		propertyOfType(TypeOfCollectionEnum.SET);
		when(ITEM.getProperty(TEST_ATTRIBUTE)).thenReturn(mock(Object.class));

		collectionProcessor.processEntity(oDataEntry(), conversionRequest());

		verify(modelEntityService, never()).getODataEntry(any());
	}

	@Test
	public void testPopulateEntrySetWhenPropertyValueIsNotCollectionOfItems() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(Object.class));
		propertyOfType(TypeOfCollectionEnum.SET);

		collectionProcessor.processEntity(oDataEntry(), conversionRequest());

		verify(modelEntityService, never()).getODataEntry(any());
	}

	@Test
	public void testPopulateEntryEnumNotSupported() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(HybrisEnumValue.class));
		propertyOfType(TypeOfCollectionEnum.COLLECTION);

		final ODataEntry entry = oDataEntry();
		collectionProcessor.processEntity(entry, conversionRequest());

		verify(modelEntityService, never()).getODataEntry(any());
		assertThat(entry.getProperties()).containsKey(TEST_ATTRIBUTE);
		assertThat(entry.getProperties().get(TEST_ATTRIBUTE)).isInstanceOf(ODataFeed.class);
		assertThat(((ODataFeed)entry.getProperties().get(TEST_ATTRIBUTE)).getEntries()).isEmpty();
	}

	@Test
	public void testProcessEntryRethrowsAttributeDescriptorNotFoundException() throws EdmException
	{
		givenIsPropertySupported();
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.SET);

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		final ODataEntry entry = oDataEntry();

		assertThatThrownBy(() -> collectionProcessor.processEntity(entry, conversionRequest()))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}


	@Test
	public void testProcessItemRethrowsAttributeDescriptorNotFoundException() throws EdmException
	{
		givenProperty(TEST_ATTRIBUTE, mock(ItemModel.class), mock(ItemModel.class));
		propertyOfType(TypeOfCollectionEnum.SET);
		prepareGetEntitySetReferencedByProperty();

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> collectionProcessor.processItem(ITEM, storageRequest()))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}

	private void givenIsPropertySupported()
	{
		final TypeAttributeDescriptor typeAttributeDescriptor = typeAttributeDescriptor(true,false);
		doReturn(Optional.of(typeAttributeDescriptor)).when(collectionProcessor).findTypeDescriptorAttributeForItem(any(IntegrationObjectItemModel.class), any(String.class));
	}

	private StorageRequest storageRequest() throws EdmException
	{
		return storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(LOCALE)
				.withAcceptLocale(LOCALE)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(INTEGRATION_OBJECT_CODE)
				.build();
	}

	private ItemConversionRequest conversionRequest() throws EdmException
	{
		return conversionRequest(conversionOptionsBuilder().build());
	}

	private ItemConversionRequest conversionRequest(final ConversionOptions options) throws EdmException
	{
		final ItemConversionRequest request = mock(ItemConversionRequest.class);
		doReturn(entitySet).when(request).getEntitySet();
		doReturn(entitySet).when(request).getEntitySetReferencedByProperty(anyString());
		doReturn(entityType).when(request).getEntityType();
		doReturn(LOCALE).when(request).getAcceptLocale();
		doReturn(ITEM).when(request).getItemModel();
		doReturn(INTEGRATION_OBJECT_CODE).when(request).getIntegrationObjectCode();
		doReturn(options).when(request).getOptions();
		return request;
	}

	private NavigationSegment navigationSegment(final String name) throws EdmException
	{
		final EdmNavigationProperty navigationProperty = mock(EdmNavigationProperty.class);
		final NavigationSegment navigationSegment = mock(NavigationSegment.class);
		when(navigationProperty.getName()).thenReturn(name);
		when(navigationSegment.getNavigationProperty()).thenReturn(navigationProperty);
		return navigationSegment;
	}

	protected ODataEntryImpl oDataEntry()
	{
		return new ODataEntryImpl(Maps.newHashMap(), new MediaMetadataImpl(), new EntryMetadataImpl(),
				new ExpandSelectTreeNodeImpl());
	}

	private void prepareAnnotations(final String... annotations) throws EdmException
	{
		final EdmAnnotations edmAnnotations = mock(EdmAnnotations.class);

		final List<EdmAnnotationAttribute> attributes = Arrays.stream(annotations).map(a -> {
			final EdmAnnotationAttribute annotationAttribute = mock(EdmAnnotationAttribute.class);
			when(annotationAttribute.getText()).thenReturn("true");
			when(annotationAttribute.getName()).thenReturn(a);
			return annotationAttribute;
		}).collect(Collectors.toList());

		when(edmAnnotations.getAnnotationAttributes()).thenReturn((attributes));
		when(((EdmAnnotatable) edmTyped).getAnnotations()).thenReturn(edmAnnotations);
	}

	private void propertyOfType(final TypeOfCollectionEnum collectionType)
	{
		when(attributeDescriptor.getWritable()).thenReturn(true);

		final CollectionTypeModel collectionModel = mock(CollectionTypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(collectionModel);
		when(collectionModel.getTypeOfCollection()).thenReturn(collectionType);
		doReturn(attributeDescriptor).when(typeService).getAttributeDescriptor(anyString(), anyString());
	}

	private void propertyOfMapType()
	{
		final MapTypeModel collectionModel = mock(MapTypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(collectionModel);
		doReturn(attributeDescriptor).when(typeService).getAttributeDescriptor(anyString(), anyString());
	}

	private void prepareGetEntitySetReferencedByProperty() throws EdmException
	{
		final EdmEntityContainer entityContainer = mock(EdmEntityContainer.class);
		when(entitySet.getEntityContainer()).thenReturn(entityContainer);
		when(entityContainer.getEntitySet(anyString())).thenReturn(entitySet);
	}
}
