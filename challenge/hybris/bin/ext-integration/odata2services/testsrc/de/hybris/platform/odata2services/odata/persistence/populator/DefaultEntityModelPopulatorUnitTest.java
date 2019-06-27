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

package de.hybris.platform.odata2services.odata.persistence.populator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEntityModelPopulatorUnitTest
{
	@Spy
	private final List<PropertyProcessor> propertyProcessors = Lists.newArrayList();

	@InjectMocks
	private final DefaultEntityModelPopulator entityModelPopulator = new DefaultEntityModelPopulator();

	private EdmEntitySet entitySet;
	private EdmEntityType entityType;
	private ODataEntry entry;
	private ItemModel itemModel;
	private StorageRequest storageRequest;
	private ItemConversionRequest conversionRequest;

	private final Map<String, Object> properties = Maps.newHashMap();

	@Before
	public void setUp() throws EdmException
	{
		entitySet = mock(EdmEntitySet.class);
		entityType = mock(EdmEntityType.class);
		entry = mock(ODataEntryImpl.class);
		itemModel = mock(ItemModel.class);

		storageRequest = mock(StorageRequest.class);
		when(storageRequest.getEntitySet()).thenReturn(entitySet);
		when(storageRequest.getEntityType()).thenReturn(entityType);
		when(storageRequest.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		when(storageRequest.getODataEntry()).thenReturn(entry);

		conversionRequest = mock(ItemConversionRequest.class);
		when(conversionRequest.getEntitySet()).thenReturn(entitySet);
		when(conversionRequest.getEntityType()).thenReturn(entityType);
		when(conversionRequest.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		when(conversionRequest.getItemModel()).thenReturn(itemModel);

		when(entityType.getName()).thenReturn("MyType");
		when(entry.getProperties()).thenReturn(properties);
	}

	@Test
	public void testPopulateItem_NullItem()
	{
		assertThatThrownBy(() -> entityModelPopulator.populateItem(null, storageRequest))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testPopulateItem_NullContext()
	{
		assertThatThrownBy(() -> entityModelPopulator.populateItem(itemModel, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testPopulateItem_WithExistingItem() throws EdmException
	{
		final PropertyProcessor processor1 = mock(PropertyProcessor.class);
		final PropertyProcessor processor2 = mock(PropertyProcessor.class);
		propertyProcessors.addAll(Arrays.asList(processor1, processor2));

		final ItemModel item = itemModel;
		entityModelPopulator.populateItem(itemModel, storageRequest);

		verify(processor1).processItem(itemModel, storageRequest);
		verify(processor2).processItem(itemModel, storageRequest);

		assertThat(itemModel).isSameAs(item);
	}

	@Test
	public void testPopulateItem_ExceptionIsReThrown() throws EdmException
	{
		final PropertyProcessor propertyProcessor = mock(PropertyProcessor.class);
		propertyProcessors.add(propertyProcessor);

		final EdmException exception = mock(EdmException.class);
		doThrow(exception).when(propertyProcessor).processItem(any(), any());

		assertThatThrownBy(() -> entityModelPopulator.populateItem(itemModel, storageRequest))
				.isInstanceOf(EdmException.class)
				.isEqualTo(exception);
	}

	@Test
	public void testPopulateEntity_NullEntry()
	{
		assertThatThrownBy(() -> entityModelPopulator.populateEntity(null, conversionRequest))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testPopulateEntity_NullRequest()
	{
		assertThatThrownBy(() -> entityModelPopulator.populateEntity(entry, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testPopulateEntity_WithExistingItem() throws EdmException
	{
		final PropertyProcessor processor1 = mock(PropertyProcessor.class);
		final PropertyProcessor processor2 = mock(PropertyProcessor.class);
		propertyProcessors.addAll(Arrays.asList(processor1, processor2));

		entityModelPopulator.populateEntity(entry, conversionRequest);

		verify(processor1).processEntity(entry, conversionRequest);
		verify(processor2).processEntity(entry, conversionRequest);
	}

	@Test
	public void testPopulateEntity_ExceptionIsReThrown() throws EdmException
	{
		final PropertyProcessor propertyProcessor = mock(PropertyProcessor.class);
		propertyProcessors.add(propertyProcessor);

		final EdmException exception = mock(EdmException.class);
		doThrow(exception).when(propertyProcessor).processEntity(any(), any());

		assertThatThrownBy(() -> entityModelPopulator.populateEntity(entry, conversionRequest))
				.isInstanceOf(EdmException.class)
				.isEqualTo(exception);
	}
}
