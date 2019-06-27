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
import static de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessorTestUtils.typeAttributeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
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
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.service.AttributeDescriptorNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PrimitivePropertyProcessorUnitTest
{
	private static final String INTEGRATION_OBJECT_CODE = "IntegrationObjectType";

	@Mock
	private ModelService modelService;
	@Mock
	private StorageRequest storageRequest;
	@Mock
	private ItemConversionRequest conversionRequest;
	@Mock
	private IntegrationObjectService integrationObjectService;
	@Mock
	private TypeService typeService;
	@Mock
	private EdmEntitySet entitySet;
	@Mock
	private EdmEntityType entityType;
	@Mock
	private ODataEntry oDataEntry;

	@InjectMocks
	@Spy
	private PrimitivePropertyProcessor propertyProcessor;

	private final ItemModel item = mock(ItemModel.class);
	private final Locale locale = Locale.ENGLISH;

	private Map<String, Object> properties;

	@Before
	public void setUp() throws EdmException
	{
		properties = Maps.newHashMap();
		when(storageRequest.getEntitySet()).thenReturn(entitySet);
		when(storageRequest.getEntityType()).thenReturn(entityType);
		when(storageRequest.getContentLocale()).thenReturn(locale);
		when(storageRequest.getODataEntry()).thenReturn(oDataEntry);
		when(storageRequest.getIntegrationObjectCode()).thenReturn(INTEGRATION_OBJECT_CODE);

		when(conversionRequest.getEntitySet()).thenReturn(entitySet);
		when(conversionRequest.getEntityType()).thenReturn(entityType);
		when(conversionRequest.getAcceptLocale()).thenReturn(locale);
		when(conversionRequest.getItemModel()).thenReturn(item);
		when(conversionRequest.getIntegrationObjectCode()).thenReturn(INTEGRATION_OBJECT_CODE);

		when(oDataEntry.getProperties()).thenReturn(properties);
		when(item.getItemtype()).thenReturn("MyType");
		when(entityType.getName()).thenReturn("entityName");

		propertyProcessor.setIntegrationObjectService(integrationObjectService);
	}

	private void givenProperty(final String name, final String type, final Object val) throws EdmException
	{
		this.properties.put(name, val);

		final EdmTyped edmTyped = mock(EdmTyped.class);
		when(edmTyped.getMultiplicity()).thenReturn(EdmMultiplicity.ONE);
		final EdmType edmType = mock(EdmType.class);
		when(edmType.getKind()).thenReturn(EdmTypeKind.SIMPLE);
		when(edmType.getName()).thenReturn(type);

		when(edmTyped.getType()).thenReturn(edmType);
		when(entityType.getProperty(name)).thenReturn(edmTyped);

		mockAttributeDescriptor(type, name, false);
	}

	private void givenPropertyForItem(final String name, final String type, final boolean localizable, final Object val) throws EdmException
	{
		givenProperty(name, type, val);
		this.properties.remove(name);

		when(modelService.getAttributeValue(item, name)).thenReturn(val);
		when(modelService.getAttributeValue(eq(item), eq(name), any())).thenReturn(val);

		mockAttributeDescriptor(type, name, localizable);
	}

	private void mockAttributeDescriptor(final String type, final String name, final boolean localizable)
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getLocalized()).thenReturn(localizable);
		when(attributeDescriptor.getItemtype()).thenReturn(type);
		when(attributeDescriptor.getName()).thenReturn(name);
		when(attributeDescriptor.getQualifier()).thenReturn(name);
		when(attributeDescriptor.getWritable()).thenReturn(true);
		when(integrationObjectService.findItemAttributeName(any(), any(), eq(name))).thenReturn(name);
		when(typeService.getAttributeDescriptor(anyString(), eq(name))).thenReturn(attributeDescriptor);
	}

	private void givenPropertyForItem(final String name, final String type, final Object val) throws EdmException
	{
		givenPropertyForItem(name, type, false, val);
	}

	private void givenSimplePropertyWithAnnotation(final String name, final String annotation, final String annotationValue) throws EdmException
	{
		this.properties.put(name, null);
		final EdmProperty edmSimpleProperty = mock(EdmProperty.class);

		final EdmType edmType = mock(EdmType.class);
		when(edmType.getKind()).thenReturn(EdmTypeKind.SIMPLE);

		when(edmSimpleProperty.getType()).thenReturn(edmType);

		final EdmAnnotations edmAnnotations = mock(EdmAnnotations.class);
		final EdmAnnotationAttribute attribute = mock(EdmAnnotationAttribute.class);
		when(edmSimpleProperty.getAnnotations()).thenReturn(edmAnnotations);

		when(edmAnnotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(attribute));
		when(attribute.getName()).thenReturn(annotation);
		when(attribute.getText()).thenReturn(annotationValue);

		when(entityType.getProperty(name)).thenReturn(edmSimpleProperty);
		mockAttributeDescriptor("", name, false);
	}

	private void givenIsPropertySupported(final boolean propertySupported)
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(!propertySupported, propertySupported);

		doReturn(Optional.of(attributeDescriptor)).when(propertyProcessor).findTypeDescriptorAttributeForItem(any(IntegrationObjectItemModel.class), any(String.class));
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveCollection()
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(true, true);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(attributeDescriptor), "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithNonPrimitiveCollection()
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(true, false);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(attributeDescriptor), "a")).isFalse();
	}

	@Test
	public void testIsPropertySupportedWithPrimitiveNotCollection()
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(false, true);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(attributeDescriptor), "a")).isTrue();
	}

	@Test
	public void testIsPropertySupportedWithNotPrimitiveNotCollection()
	{
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(false, false);

		assertThat(propertyProcessor.isPropertySupported(Optional.of(attributeDescriptor), "a")).isFalse();
	}

	@Test
	public void testItemWithNoProperties() throws EdmException
	{
		propertyProcessor.processItem(item, storageRequest);

		verifyItemSetAttributeValueIsNotCalled(item);
	}

	@Test
	public void testProcessItemWithNoSupportedProperties() throws EdmException
	{
		givenProperty("a", "TypeA", null);
		when(entityType.getProperty("a")).thenReturn(null);
		givenProperty("b", "TypeB", null);
		when(entityType.getProperty("b").getType().getKind()).thenReturn(EdmTypeKind.ENTITY);
		givenIsPropertySupported(false);

		propertyProcessor.processItem(item, storageRequest);

		verifyItemSetAttributeValueIsNotCalled(item);
	}

	@Test
	public void testProcessItemWithSupportedPropertiesNoSettableProperties() throws EdmException
	{
		givenProperty("a", "TypeA", null);
		givenProperty("b", "TypeB", null);

		when(modelService.isNew(item)).thenReturn(false);
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor("TypeA", "a");
		when(attributeDescriptor.getWritable()).thenReturn(false);

		final AttributeDescriptorModel attributeDescriptorB = typeService.getAttributeDescriptor("TypeB", "b");
		when(attributeDescriptorB.getWritable()).thenReturn(false);

		propertyProcessor.processItem(item, storageRequest);

		verifyItemSetAttributeValueIsNotCalled(item);
	}

	@Test
	public void testProcessItemWithSupportedPropertiesWithSettableProperties() throws EdmException
	{
		final Calendar calendar = GregorianCalendar.getInstance();
		givenProperty("a", "TypeA", calendar);
		givenProperty("b", "TypeB", "some localizable string");
		mockAttributeDescriptor("TypeB", "b", true);
		givenProperty("c", "TypeC", "some value");
		mockAttributeDescriptor("TypeC", "c", false);
		givenIsPropertySupported(true);

		when(modelService.isNew(item)).thenReturn(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService).setAttributeValue(item, "a", calendar.getTime());
		verify(modelService).setAttributeValue(item, "b", Collections.singletonMap(locale, "some localizable string"));
		verify(modelService).setAttributeValue(item, "c", "some value");
	}

	@Test
	public void testProcessItemWithKeyPropertyForExistingItemNotSetAgainstItemModel() throws EdmException
	{
		givenSimplePropertyWithAnnotation("a", "s:IsUnique", "true");
		when(modelService.isNew(item)).thenReturn(false);
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verifyItemSetAttributeValueIsNotCalled(item);
	}

	@Test
	public void testProcessItemWithKeyPropertyForNewItemSetAgainstItemModel() throws EdmException
	{
		givenProperty("a", "TypeA", "some localizable string");
		when(modelService.isNew(item)).thenReturn(true);
		when(entityType.getKeyPropertyNames()).thenReturn(Collections.singletonList("a"));
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService, times(1)).setAttributeValue(eq(item), anyString(), anyString());
	}

	@Test
	public void testProcessItemWithNonKeyPropertyForExistingItemSetAgainstItemModel() throws EdmException
	{
		givenProperty("a", "TypeA", "some localizable string");

		when(modelService.isNew(item)).thenReturn(true);
		when(entityType.getKeyPropertyNames()).thenReturn(Collections.singletonList("b"));
		givenIsPropertySupported(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService, times(1)).setAttributeValue(eq(item), anyString(), anyString());
	}

	@Test
	public void testProcessItemWithIntegrationKeyPresent() throws EdmException
	{
		givenProperty("integrationKey", "SomeItem", "abc-123");
		when(modelService.isNew(item)).thenReturn(true);

		propertyProcessor.processItem(item, storageRequest);

		verify(modelService, never()).setAttributeValue(eq(item), eq(INTEGRATION_KEY_PROPERTY_NAME), any());
	}

	@Test
	public void testProcessEntityWithNoSupportedProperties() throws EdmException
	{
		givenPropertyForItem("a", "TypeA", new Object());
		when(entityType.getProperty("a")).thenReturn(null);
		givenPropertyForItem("b", "TypeB", new Object());
		when(entityType.getProperty("b").getType().getKind()).thenReturn(EdmTypeKind.ENTITY);
		givenPropertyForItem(INTEGRATION_KEY_PROPERTY_NAME, "TypeA", new Object());
		when(entityType.getProperty(INTEGRATION_KEY_PROPERTY_NAME).getType().getKind()).thenReturn(EdmTypeKind.ENTITY);
		givenIsPropertySupported(false);

		when(entityType.getPropertyNames()).thenReturn(Lists.newArrayList("a", "b", INTEGRATION_KEY_PROPERTY_NAME));

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		verify(modelService, never()).getAttributeValue(item, "a");
		verify(modelService, never()).getAttributeValue(item, "b", locale);
		verify(modelService, never()).getAttributeValue(item, INTEGRATION_KEY_PROPERTY_NAME);
		assertThat(oDataEntry.getProperties()).isEmpty();
	}

	@Test
	public void testProcessEntityWithNoProperties() throws EdmException
	{
		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		assertThat(oDataEntry.getProperties()).isEmpty();
		verify(modelService, never()).getAttributeValue(eq(item), anyString());
		verify(modelService, never()).getAttributeValue(eq(item), anyString(), any());
	}

	@Test
	public void testProcessEntityWithSupportedAndSettableProperties() throws EdmException
	{
		final Date date = new Date();
		givenPropertyForItem("a", "TypeA", date);
		givenPropertyForItem("b", "TypeB", true, "some localizable string");
		givenPropertyForItem("c", "TypeC", false, "some value");
		givenIsPropertySupported(true);

		when(entityType.getPropertyNames()).thenReturn(Lists.newArrayList("a", "b", "c"));

		propertyProcessor.processEntity(oDataEntry, conversionRequest);

		verify(modelService).getAttributeValue(item, "a");
		verify(modelService).getAttributeValue(item, "b", locale);
		verify(modelService).getAttributeValue(item, "c");
		assertThat(oDataEntry.getProperties())
				.contains(entry("a", DateUtils.toCalendar(date)),
						entry("b", "some localizable string"),
						entry("c", "some value"));
	}

	@Test
	public void testProcessEntityAttributeDescriptorNotFoundExceptionIsRethrown() throws EdmException
	{
		givenPropertyForItem("c", "TypeC", false, "some value");
		when(entityType.getPropertyNames()).thenReturn(Lists.newArrayList("c"));
		givenIsPropertySupported(true);

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> propertyProcessor.processEntity(oDataEntry, conversionRequest))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}


	@Test
	public void testProcessItemAttributeDescriptorNotFoundExceptionIsRethrown() throws EdmException
	{
		givenProperty("c", "TypeC", "abc-123");
		when(entityType.getPropertyNames()).thenReturn(Lists.newArrayList("c"));

		doThrow(AttributeDescriptorNotFoundException.class)
				.when(integrationObjectService).findItemAttributeName(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> propertyProcessor.processItem(item, storageRequest))
				.isInstanceOf(AttributeDescriptorNotFoundException.class);
	}

	private void verifyItemSetAttributeValueIsNotCalled(final ItemModel itemModel)
	{
		verify(modelService, never()).setAttributeValue(eq(itemModel), anyString(), any());
	}
}
