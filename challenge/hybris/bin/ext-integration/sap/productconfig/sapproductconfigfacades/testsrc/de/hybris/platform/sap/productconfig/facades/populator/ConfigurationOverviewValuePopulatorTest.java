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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.facades.impl.ClassificationSystemCPQAttributesProviderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValueMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests
 */
@UnitTest
public class ConfigurationOverviewValuePopulatorTest
{
	@Mock
	public ConfigPricingImpl mockConfigPricing;
	@Mock
	public PriceModel mockValuePrice;
	@Mock
	public ValueFormatTranslator mockValueFormatTranslator;
	@Mock
	public UiTypeFinder mockUiTypeFinder;

	@InjectMocks
	public ConfigurationOverviewValuePopulator classUnderTest = new ConfigurationOverviewValuePopulator();

	public CsticValueModel source;
	public CharacteristicValue target;
	public PriceData price;
	public PriceData obsoletePrice;
	public CsticModel cstic;
	public Collection<Map> options;

	private static final String WRONG_VALUE_NAME = "Wrong value name: ";
	private static final String WRONG_CHARACTERISTIC_NAME = "Wrong characteristic name: ";
	private static final String WRONG_PRICE = "Wrong price: ";

	public static final String DISCOUNT_TEXT = "Get 10% of discount";
	public static final String END_DATE_TEXT = "10/6/18";
	public static final String PROMO_TEXT = "You could get 10% discount";
	public static Date END_DATE;

	public static final String CSTIC_NAME = "SOFTWARE_CENTER";
	public static final String CSTIC_LAN_DEPENDENT_NAME = "Software center";
	public static final String VALUE_NAME = "SOFTWARE";
	public static final String VALUE_LAN_DEPENDENT_NAME = "Soffware Engineering";

	public static final String HYBRIS_CSTIC_LAN_DEPENDENT_NAME = "hybris Software center";
	public static final String HYBRIS_VALUE_LAN_DEPENDENT_NAME = "hybris Soffware Engineering";

	public static final String PRICE = "$25.99";
	public static final String OBSOLETE_PRICE = "$20.99";
	public ProductConfigMessageBuilder productConfigMessageBuilder;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		END_DATE = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse(END_DATE_TEXT);
		final ClassificationSystemCPQAttributesProviderImpl nameProvider = new ClassificationSystemCPQAttributesProviderImpl();
		nameProvider.setUiTypeFinder(new UiTypeFinderImpl());
		nameProvider.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		classUnderTest.setNameProvider(nameProvider);

		target = new CharacteristicValue();
		source = new CsticValueModelImpl();

		source.setLanguageDependentName(VALUE_LAN_DEPENDENT_NAME);
		source.setName(VALUE_NAME);

		source.setValuePrice(mockValuePrice);

		final Set<ProductConfigMessage> messages = new HashSet<>();
		productConfigMessageBuilder = new ProductConfigMessageBuilder();
		productConfigMessageBuilder.appendPromoType(ProductConfigMessagePromoType.PROMO_APPLIED).appendMessage(DISCOUNT_TEXT);
		messages.add(productConfigMessageBuilder.build());
		source.setMessages(messages);

		price = new PriceData();
		price.setFormattedValue(PRICE);

		obsoletePrice = new PriceData();
		obsoletePrice.setFormattedValue(OBSOLETE_PRICE);

		cstic = new CsticModelImpl();
		cstic.setName(CSTIC_NAME);
		cstic.setLanguageDependentName(CSTIC_LAN_DEPENDENT_NAME);

		options = new ArrayList<Map>();
		final HashMap<String, Object> optionsMap = new HashMap<String, Object>();
		optionsMap.put(ConfigurationOverviewValuePopulator.CSTIC_MODEL, cstic);
		final ClassificationSystemCPQAttributesContainer hybrisNames = ClassificationSystemCPQAttributesContainer.NULL_OBJ;
		optionsMap.put(ConfigurationOverviewValuePopulator.HYBRIS_NAMES, hybrisNames);
		options.add(optionsMap);
	}

	@Test
	public void testLangDependentNameNotNullAndNameNull()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		source.setName(null);

		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_LAN_DEPENDENT_NAME, target.getValue());
	}

	@Test
	public void testLangDependentNameNullAndNameNull()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		source.setName(null);
		source.setLanguageDependentName(null);
		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertNull("We expect value equals null", target.getValue());
	}

	@Test
	public void testLangDependentNameNullAndNameNotNull()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		source.setLanguageDependentName(null);
		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_NAME, target.getValue());
	}

	@Test
	public void testLangDependentNameNotNullAndNameNotNull()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_LAN_DEPENDENT_NAME, target.getValue());
	}

	@Test
	public void testNoConfigPrice()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(ConfigPricing.NO_PRICE);

		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_LAN_DEPENDENT_NAME, target.getValue());
		assertNull(target.getPriceDescription());
	}

	@Test
	public void testProductConfigMessages()
	{
		when(mockValueFormatTranslator.formatDate(any())).thenReturn("");
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		when(mockConfigPricing.getObsoletePriceData(mockValuePrice)).thenReturn(price);
		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertNotNull(target.getMessages());
		assertEquals(1, target.getMessages().size());
	}

	@Test
	public void testProductConfigMessagesNoPrice()
	{
		when(mockValueFormatTranslator.formatDate(any())).thenReturn("");
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(ConfigPricing.NO_PRICE);
		when(mockConfigPricing.getObsoletePriceData(mockValuePrice)).thenReturn(price);
		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertNotNull(target.getMessages());
		assertEquals(0, target.getMessages().size());
	}

	@Test
	public void testValuePrice()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);

		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_LAN_DEPENDENT_NAME, target.getValue());
		assertEquals(WRONG_PRICE, PRICE, target.getPriceDescription());
		assertNull("No obsolete price expected", target.getObsoletePriceDescription());
	}

	@Test
	public void testObsoletePrice()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(price);
		when(mockConfigPricing.getObsoletePriceData(mockValuePrice)).thenReturn(obsoletePrice);

		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals(WRONG_VALUE_NAME, VALUE_LAN_DEPENDENT_NAME, target.getValue());
		assertEquals(WRONG_PRICE, PRICE, target.getPriceDescription());
		assertEquals(WRONG_PRICE, OBSOLETE_PRICE, target.getObsoletePriceDescription());
	}

	@Test
	public void testGetCsticPriceDescription()
	{
		final String obsoletePriceString = classUnderTest.getCsticPriceDescription(obsoletePrice);

		assertNotNull(obsoletePriceString);
		assertEquals(OBSOLETE_PRICE, obsoletePriceString);
	}

	@Test
	public void testGetCsticPriceDescriptionNullValue()
	{
		final String result = classUnderTest.getCsticPriceDescription(null);
		assertNull(result);
	}

	@Test
	public void testGetCsticPriceDescriptionNoPriceValue()
	{
		final String result = classUnderTest.getCsticPriceDescription(ConfigPricing.NO_PRICE);
		assertNull(result);
	}

	@Test
	public void testHybrisNames()
	{
		when(mockConfigPricing.getPriceData(mockValuePrice)).thenReturn(ConfigPricing.NO_PRICE);

		final HashMap optionsMap = (HashMap) options.iterator().next();
		final Map<String, String> hybris_value_names = new HashMap<String, String>();
		hybris_value_names.put(CSTIC_NAME + "_" + VALUE_NAME, HYBRIS_VALUE_LAN_DEPENDENT_NAME);
		final ClassificationSystemCPQAttributesContainer hybrisNames = new ClassificationSystemCPQAttributesContainer(CSTIC_NAME,
				HYBRIS_CSTIC_LAN_DEPENDENT_NAME, null, hybris_value_names, Collections.emptyMap(), Collections.emptyList(),
				Collections.emptyMap());
		optionsMap.put(ConfigurationOverviewValuePopulator.HYBRIS_NAMES, hybrisNames);

		classUnderTest.populate(source, target, options);

		assertNotNull(target);
		assertEquals(WRONG_CHARACTERISTIC_NAME, HYBRIS_CSTIC_LAN_DEPENDENT_NAME, target.getCharacteristic());
		assertEquals("Wrong value name: ", HYBRIS_VALUE_LAN_DEPENDENT_NAME, target.getValue());
	}

	@Test
	public void testProcessMessageWithoutDate()
	{
		when(mockValueFormatTranslator.formatDate(null)).thenReturn("");

		final ProductConfigMessage productConfigMessage = productConfigMessageBuilder.reset().appendMessage(DISCOUNT_TEXT).build();

		final CharacteristicValueMessage messageData = classUnderTest.processMessage(productConfigMessage);
		assertNotNull(messageData);
		assertEquals(DISCOUNT_TEXT, messageData.getMessage());
		assertEquals("", messageData.getEndDate());
	}

	@Test
	public void testProcessMessage() throws Exception
	{
		when(mockValueFormatTranslator.formatDate(END_DATE)).thenReturn(END_DATE_TEXT);
		final ProductConfigMessage productConfigMessage = productConfigMessageBuilder.reset().appendMessage(DISCOUNT_TEXT)
				.appendEndDate(END_DATE).build();

		final CharacteristicValueMessage messageData = classUnderTest.processMessage(productConfigMessage);
		assertNotNull(messageData);
		assertEquals(DISCOUNT_TEXT, messageData.getMessage());
		assertEquals(END_DATE_TEXT, messageData.getEndDate());
	}

	@Test
	public void testProcessMessages()
	{
		final Set<ProductConfigMessage> messages = new HashSet<>();
		messages.add(productConfigMessageBuilder.reset().appendPromoType(ProductConfigMessagePromoType.PROMO_APPLIED)
				.appendMessage(DISCOUNT_TEXT).appendEndDate(END_DATE).build());
		messages.add(productConfigMessageBuilder.reset().appendPromoType(ProductConfigMessagePromoType.PROMO_OPPORTUNITY)
				.appendMessage(PROMO_TEXT).build());

		final List<CharacteristicValueMessage> csticMessages = classUnderTest.processMessages(messages);
		assertNotNull(csticMessages);
		assertEquals(1, csticMessages.size());
		assertEquals(DISCOUNT_TEXT, csticMessages.get(0).getMessage());
	}

	@Test
	public void testProcessMessagesNullList()
	{
		final List<CharacteristicValueMessage> csticMessages = classUnderTest.processMessages(null);
		assertNotNull(csticMessages);
		assertEquals(0, csticMessages.size());
	}

	@Test
	public void testProcessMessagesWithoutPromoType()
	{
		final Set<ProductConfigMessage> messages = new HashSet<>();
		messages.add(productConfigMessageBuilder.reset().appendPromoType(ProductConfigMessagePromoType.PROMO_APPLIED)
				.appendMessage(DISCOUNT_TEXT).appendEndDate(END_DATE).build());
		messages.add(productConfigMessageBuilder.reset().appendPromoType(null).appendMessage(PROMO_TEXT).build());

		final List<CharacteristicValueMessage> csticMessages = classUnderTest.processMessages(messages);
		assertNotNull(csticMessages);
		assertEquals(1, csticMessages.size());
		assertEquals(DISCOUNT_TEXT, csticMessages.get(0).getMessage());
	}
}
