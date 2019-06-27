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


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.populator.CMSVersionToItemModelPopulator;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.persistence.audit.payload.PayloadDeserializer;
import de.hybris.platform.persistence.audit.payload.json.AuditPayload;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionToItemModelPreviewConverterTest
{

	private final String TYPE_CODE = "Type_code";
	private final String PAYLOAD = "Payload";

	@InjectMocks
	private DefaultCMSVersionToItemModelPreviewConverter converter;

	@Mock
	CMSVersionModel version;

	@Mock
	private ModelService modelService;

	@Mock
	private ItemModel itemModel;

	@Mock
	private PayloadDeserializer payloadDeserializer;

	@Mock
	private AuditPayload auditPayload;

	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;


	@Mock
	private CMSVersionToItemModelPopulator populator;


	@Before
	public void setup()
	{
		doReturn(TYPE_CODE).when(version).getItemTypeCode();
		doReturn(PAYLOAD).when(version).getPayload();
		doReturn(itemModel).when(modelService).create(TYPE_CODE);
		doReturn(auditPayload).when(payloadDeserializer).deserialize(PAYLOAD);
		doNothing().when(cmsVersionSessionContextProvider).addGeneratedItemToCache(itemModel, version);
		doNothing().when(populator).populate(auditPayload, itemModel);
	}

	@Test
	public void willCallCmsVersionToModelPopulator_populate()
	{
		// WHEN
		converter.convert(version);
		// THEN
		verify(populator).populate(auditPayload, itemModel);
	}

	@Test(expected = ConversionException.class)
	public void willBubbleUpConversionException()
	{
		// GIVEN
		doThrow(ConversionException.class).when(populator).populate(auditPayload, itemModel);
		// WHEN
		converter.convert(version);
	}
}

