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
package de.hybris.platform.cms2.version.converter.impl;


import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.ItemNotFoundException;
import de.hybris.platform.cms2.exceptions.ItemRollbackException;
import de.hybris.platform.cms2.items.service.ItemService;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.converter.rollback.ItemRollbackConverter;
import de.hybris.platform.cms2.version.converter.rollback.ItemRollbackStrategyConverterProvider;
import de.hybris.platform.cms2.version.populator.CMSVersionToItemModelPopulator;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.persistence.audit.payload.PayloadDeserializer;
import de.hybris.platform.persistence.audit.payload.json.AuditPayload;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionToItemModelRollbackConverterTest
{

	private final String TYPE_CODE = "Type_code";
	private final String PAYLOAD = "Payload";

	@InjectMocks
	private DefaultCMSVersionToItemModelRollbackConverter converter;

	@Mock
	CMSVersionModel version;

	@Mock
	private ItemModel itemModel;

	@Mock
	private ItemModel customItemModel;

	@Mock
	private ItemService itemService;

	@Mock
	private PayloadDeserializer payloadDeserializer;

	@Mock
	private AuditPayload auditPayload;

	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Mock
	private ItemRollbackStrategyConverterProvider itemRollbackStrategyConverterProvider;

	@Mock
	private ItemRollbackConverter itemRollbackConverter;

	@Mock
	private CMSVersionToItemModelPopulator populator;

	private Map<String, Object> mappedAttributes;


	@Before
	public void setup() throws ItemNotFoundException, ItemRollbackException
	{
		doReturn(TYPE_CODE).when(version).getItemTypeCode();
		doReturn(PAYLOAD).when(version).getPayload();
		mappedAttributes = new HashMap<>();
		doReturn(itemModel).when(itemService).getItemByAttributeValues(TYPE_CODE, mappedAttributes);
		doReturn(auditPayload).when(payloadDeserializer).deserialize(PAYLOAD);
		doReturn(mappedAttributes).when(auditPayload).getAttributes();
		doNothing().when(cmsVersionSessionContextProvider).addGeneratedItemToCache(itemModel, version);
		doNothing().when(populator).populate(auditPayload, itemModel);

		doReturn(Optional.empty()).when(itemRollbackStrategyConverterProvider).getConverter(itemModel);
		doReturn(customItemModel).when(itemRollbackConverter).rollbackItem(itemModel, version, auditPayload);
	}

	@Test
	public void willCallCmsVersionToModelPopulator_populate()
	{
		// WHEN
		converter.convert(version);
		// THEN
		verify(populator).populate(auditPayload, itemModel);
	}

	@Test
	public void givenCustomRollbackConverterExistsForItem_WhenConvertIsCalled_ThenItCallsTheCustomConverterAndPopulatesTheItemReturned()
	{
		// GIVEN
		doReturn(Optional.of(itemRollbackConverter)).when(itemRollbackStrategyConverterProvider).getConverter(itemModel);

		// WHEN
		converter.convert(version);

		// THEN
		verify(populator).populate(auditPayload, customItemModel);
	}

	@Test(expected = ConversionException.class)
	public void willBubbleUpConversionException()
	{
		// GIVEN
		doThrow(ConversionException.class).when(populator).populate(auditPayload, itemModel);
		// WHEN
		converter.convert(version);
	}

	@Test
	public void willCatchItemNotFoundExceptionAndReturnNull() throws ItemNotFoundException
	{
		// GIVEN
		doThrow(ItemNotFoundException.class).when(itemService).getItemByAttributeValues(TYPE_CODE, mappedAttributes);
		// WHEN
		final ItemModel itemModel = converter.convert(version);
		// THEN
		assertThat(itemModel, is(nullValue()));
	}

	@Test
	public void willCatchItemRollbackExceptionAndReturnNull() throws ItemRollbackException
	{
		// GIVEN
		doReturn(Optional.of(itemRollbackConverter)).when(itemRollbackStrategyConverterProvider).getConverter(itemModel);
		doThrow(ItemRollbackException.class).when(itemRollbackConverter).rollbackItem(itemModel, version, auditPayload);

		// WHEN
		final ItemModel itemModel = converter.convert(version);

		// THEN
		assertThat(itemModel, is(nullValue()));
	}
}
