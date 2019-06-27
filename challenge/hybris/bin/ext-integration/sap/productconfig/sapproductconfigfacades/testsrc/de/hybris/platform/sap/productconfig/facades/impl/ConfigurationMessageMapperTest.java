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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationMessageMapperTest
{
	private static final String NAME = "A";
	private static final String DESCRIPTION = "B";
	private static final String PRODUCT_CODE = "product_123";

	@InjectMocks
	private ConfigurationMessageMapperImpl classUnderTest = new ConfigurationMessageMapperImpl();

	private ConfigModel configModel;
	private ConfigurationData configData;
	private ProductConfigMessageBuilder builder;
	@Mock
	private ValueFormatTranslator valueFormatTranslator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		configModel = new ConfigModelImpl();
		configData = new ConfigurationData();
		builder = new ProductConfigMessageBuilder();
		builder.appendBasicFields("a_test_message", "messagekey123", ProductConfigMessageSeverity.INFO);
		builder.appendSourceAndType(ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
	}

	@Test
	public void testMapMessagesFromModelToDataEmpty()
	{
		classUnderTest.mapMessagesFromModelToData(configData, configModel);
		assertTrue(configData.getMessages().isEmpty());
	}

	@Test
	public void testMapMessagesFromModelToDataInfo()
	{
		builder.appendSeverity(ProductConfigMessageSeverity.INFO);
		addMessageToConfig(configModel, builder.build());

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataWarning()
	{
		builder.appendSeverity(ProductConfigMessageSeverity.WARNING);
		addMessageToConfig(configModel, builder.build());

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.INFO, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataError()
	{
		builder.appendSeverity(ProductConfigMessageSeverity.ERROR);
		addMessageToConfig(configModel, builder.build());

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.ERROR, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataEndcode()
	{
		builder.appendMessage("><img src=x onerror=alert(1)>");
		addMessageToConfig(configModel, builder.build());

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("&gt;&lt;img&#x20;src&#x3d;x&#x20;onerror&#x3d;alert&#x28;1&#x29;&gt;",
				configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataEndcodeErr() throws UnsupportedEncodingException
	{
		builder.appendMessage("\uffff");
		addMessageToConfig(configModel, builder.build());

		classUnderTest = spy(classUnderTest);
		willThrow(UnsupportedEncodingException.class).given(classUnderTest).encodeHTML("\uffff");

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertNull(configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, configData.getMessages().get(0).getSeverity());
	}


	@Test
	public void testMapMessagesFromModelToDataForCsticEmptyMessages()
	{
		final CsticModel csticModel = new CsticModelImpl();
		final CsticData csticData = new CsticData();
		classUnderTest.mapMessagesFromModelToData(csticData, csticModel);

		assertEquals(0, csticData.getMessages().size());
	}


	@Test
	public void testMapMessagesFromModelToDataForCstic()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.getMessages().add(builder.build());

		final CsticData csticData = new CsticData();
		classUnderTest.mapMessagesFromModelToData(csticData, csticModel);

		assertEquals(1, csticData.getMessages().size());
		assertEquals("a_test_message", csticData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, csticData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataForCsticPromoMessage()
	{
		final CsticModel csticModel = new CsticModelImpl();
		builder.appendPromoType(ProductConfigMessagePromoType.PROMO_OPPORTUNITY);
		csticModel.getMessages().add(builder.build());

		final CsticData csticData = new CsticData();
		classUnderTest.mapMessagesFromModelToData(csticData, csticModel);

		assertEquals(1, csticData.getMessages().size());
		assertEquals("a_test_message", csticData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, csticData.getMessages().get(0).getSeverity());
		assertEquals(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, csticData.getMessages().get(0).getPromoType());
	}

	@Test
	public void testMapMessagesFromModelToDataForCsticExtended() throws ParseException
	{

		final CsticModel csticModel = new CsticModelImpl();
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		final Date newDate = formatter.parse("2020/05/31");
		final String date = "5/31/20";
		when(valueFormatTranslator.formatDate(newDate)).thenReturn(date);
		builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, "a_text_extended", newDate);
		csticModel.getMessages().add(builder.build());

		final CsticData csticData = new CsticData();
		classUnderTest.mapMessagesFromModelToData(csticData, csticModel);

		assertEquals(1, csticData.getMessages().size());
		assertEquals("a_test_message", csticData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, csticData.getMessages().get(0).getSeverity());
		assertEquals(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, csticData.getMessages().get(0).getPromoType());
		assertEquals("a_text_extended", csticData.getMessages().get(0).getExtendedMessage());
		assertEquals(date, csticData.getMessages().get(0).getEndDate());
	}

	@Test
	public void testMapMessagesFromModelToDataForCsticValueEmptyMessages()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		final CsticValueData valueData = new CsticValueData();

		classUnderTest.mapMessagesFromModelToData(valueData, valueModel);
		assertEquals(0, valueData.getMessages().size());

	}

	@Test
	public void testMapMessagesFromModelToDataForCsticValue()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		valueModel.getMessages().add(builder.build());

		final CsticValueData valueData = new CsticValueData();
		classUnderTest.mapMessagesFromModelToData(valueData, valueModel);

		assertEquals(1, valueData.getMessages().size());
		assertEquals("a_test_message", valueData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, valueData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataForCsticValuePromoMessage()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		builder.appendPromoType(ProductConfigMessagePromoType.PROMO_APPLIED);
		valueModel.getMessages().add(builder.build());

		final CsticValueData valueData = new CsticValueData();
		classUnderTest.mapMessagesFromModelToData(valueData, valueModel);

		assertEquals(1, valueData.getMessages().size());
		assertEquals("a_test_message", valueData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, valueData.getMessages().get(0).getSeverity());
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED, valueData.getMessages().get(0).getPromoType());
	}

	protected void addMessageToConfig(final ConfigModel configModel, final ProductConfigMessage message)
	{
		final Set<ProductConfigMessage> messages = configModel.getMessages();
		messages.add(message);
		configModel.setMessages(messages);
	}
}
