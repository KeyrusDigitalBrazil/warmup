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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PricingRuleAwareServiceImplTest
{
	private static final String CONFIG_ID = "configId";
	private static final String KB_ID = "kbId";
	private static final String PRODUCT_CODE = "PRODUCT_ID";

	private PricingRuleAwareServiceImpl classUnderTest;

	@Mock
	private PricingProvider mockedPricingProvider;
	@Mock
	private ProviderFactory mockedProviderFactory;
	@Mock
	private ProductConfigRulesResultUtil rulesResultUtil;
	@Mock
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	@Mock
	private ConfigurationModelCacheStrategy configModelCache;


	private List<ProductConfigurationDiscount> discounts;
	private ConfigModel configModel;
	private List<PriceValueUpdateModel> updateModels;
	private CsticModel csticModel;
	private Map<String, Map<String, List<ProductConfigMessage>>> messagesByCstic;
	private Map<String, List<ProductConfigMessage>> messagesByValue;
	private ProductConfigMessage discountMessage;
	private ProductConfigMessageBuilder builder;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingRuleAwareServiceImpl();
		classUnderTest.setProviderFactory(mockedProviderFactory);
		given(mockedProviderFactory.getPricingProvider()).willReturn(mockedPricingProvider);
		classUnderTest.setRulesResultUtil(rulesResultUtil);
		classUnderTest.setAssignmentResolverStrategy(assignmentResolverStrategy);
		classUnderTest.setConfigurationModelCacheStrategy(configModelCache);
		discounts = Collections.emptyList();
		given(rulesResultUtil.retrieveRulesBasedVariantConditionModifications(CONFIG_ID)).willReturn(discounts);
		configModel = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		configModel.setId(CONFIG_ID);
		configModel.setKbId(KB_ID);
		csticModel = configModel.getRootInstance().getCstics().get(0);
		csticModel.setAssignedValues(csticModel.getAssignableValues());
		updateModels = Collections.emptyList();
		messagesByCstic = new HashMap<>();
		messagesByValue = new HashMap<>();
		messagesByCstic.put(csticModel.getName(), messagesByValue);
		given(rulesResultUtil.retrieveDiscountMessages(CONFIG_ID)).willReturn(messagesByCstic);

		discountMessage = new ProductConfigMessageBuilder().build();
		messagesByValue.put(csticModel.getAssignedValues().get(1).getName(), Collections.singletonList(discountMessage));

		builder = new ProductConfigMessageBuilder();

		given(assignmentResolverStrategy.retrieveRelatedProductCode(CONFIG_ID)).willReturn(PRODUCT_CODE);
		given(configModelCache.getConfigurationModelEngineState(CONFIG_ID)).willReturn(configModel);
	}


	@Test
	public void testRetrieveValuePricesForConfigModel() throws PricingEngineException
	{
		classUnderTest.retrieveValuePrices(configModel);
		verify(rulesResultUtil).retrieveRulesBasedVariantConditionModifications(CONFIG_ID);
		verify(mockedPricingProvider).fillValuePrices(eq(configModel), Mockito.any());
	}

	@Test
	public void testRetrieveValuePricesForPriceValueUpdateModel() throws PricingEngineException
	{
		classUnderTest.retrieveValuePrices(updateModels, KB_ID, CONFIG_ID);
		verify(rulesResultUtil).retrieveRulesBasedVariantConditionModifications(CONFIG_ID);
		verify(mockedPricingProvider).fillValuePrices(eq(updateModels), eq(KB_ID), Mockito.any());
	}

	@Test
	public void testPrepareRetrievalOptionsForDiscounts()
	{
		final ConfigurationRetrievalOptions result = classUnderTest.prepareRetrievalOptions(configModel);
		assertNotNull(result);
		assertEquals(discounts, result.getDiscountList());
	}


	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, configModel);
		Mockito.verify(mockedPricingProvider, Mockito.times(1)).fillValuePrices(eq(updateModels), eq(KB_ID),
				Mockito.any(ConfigurationRetrievalOptions.class));
	}

	@Test
	public void testFillValuePricesException() throws PricingEngineException
	{
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.anyList(), Mockito.anyString());
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, configModel);
	}

	@Test
	public void testRestoreDsicountMessagesEmpty()
	{
		given(rulesResultUtil.retrieveDiscountMessages(CONFIG_ID)).willReturn(Collections.emptyMap());
		classUnderTest.restoreDiscountMessages(configModel);
		assertNoMessagePresent();
	}

	@Test
	public void testRestoreDsicountMessages()
	{
		classUnderTest.restoreDiscountMessages(configModel);
		assertMessagePresent();
	}



	@Test
	public void testRestoreDsicountMessagesForCsticEmpty()
	{
		classUnderTest.restoreDiscountMessageForCstic(csticModel, Collections.emptyMap());
		assertNoMessagePresent();
	}

	@Test
	public void testRestoreDsicountMessagesForCstic()
	{
		classUnderTest.restoreDiscountMessageForCstic(csticModel, messagesByValue);
		assertMessagePresent();
	}

	@Test
	public void testRestoreDsicountMessagesForInstanceEmpty()
	{
		classUnderTest.restoreDiscountMessageForInstance(configModel.getRootInstance(), Collections.emptyMap());
		assertNoMessagePresent();

	}

	@Test
	public void testRestoreDsicountMessagesForInstance()
	{
		classUnderTest.restoreDiscountMessageForInstance(configModel.getRootInstance(), messagesByCstic);
		assertMessagePresent();
	}

	@Test
	public void testRestoreDsicountMessagesForSubInstance()
	{
		final InstanceModelImpl newRoot = new InstanceModelImpl();
		newRoot.setSubInstances(Collections.singletonList(configModel.getRootInstance()));
		classUnderTest.restoreDiscountMessageForInstance(newRoot, messagesByCstic);
		assertMessagePresent();
	}

	@Test
	public void testRemovePromoAppliedMessagesWithoutMessages()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		valueModel.setMessages(Collections.EMPTY_SET);
		classUnderTest.removePromoAppliedMessages(valueModel);
		assertTrue(valueModel.getMessages().isEmpty());
	}

	@Test
	public void testRemovePromoAppliedMessagesWithoutPromoAppliedMessages()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		final Set<ProductConfigMessage> messages = new HashSet<>();
		messages.add(createMessage("a_test_message1", "messagekey1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		messages.add(createMessage("a_test_message123", "messagekey2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		messages.add(createMessage("a_test_message3", "messagekey3", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		valueModel.setMessages(messages);

		assertEquals(3, valueModel.getMessages().size());
		classUnderTest.removePromoAppliedMessages(valueModel);
		assertEquals(3, valueModel.getMessages().size());
	}

	@Test
	public void testRemovePromoAppliedMessagesWithPromoAppliedMessages()
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		final Set<ProductConfigMessage> messages = new HashSet<>();
		messages.add(createMessage("a_test_message1", "messagekey2", ProductConfigMessagePromoType.PROMO_APPLIED));
		messages.add(createMessage("a_test_message2", "messagekey1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		messages.add(createMessage("a_test_message3", "messagekey2", ProductConfigMessagePromoType.PROMO_APPLIED));
		messages.add(createMessage("a_test_message4", "messagekey3", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		messages.add(createMessage("a_test_message5", "messagekey1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY));
		valueModel.setMessages(messages);

		assertEquals(5, valueModel.getMessages().size());
		classUnderTest.removePromoAppliedMessages(valueModel);
		assertEquals(3, valueModel.getMessages().size());
	}

	protected ProductConfigMessage createMessage(final String messageStr, final String messageKey,
			final ProductConfigMessagePromoType promoType)
	{
		builder.reset();
		builder.appendBasicFields(messageStr, messageKey, ProductConfigMessageSeverity.INFO);
		builder.appendSourceAndType(ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		builder.appendPromoType(promoType);
		return builder.build();
	}


	protected void assertMessagePresent()
	{
		assertTrue(csticModel.getMessages().isEmpty());
		assertTrue(csticModel.getAssignedValues().get(0).getMessages().isEmpty());
		assertEquals(1, csticModel.getAssignedValues().get(1).getMessages().size());
	}

	protected void assertNoMessagePresent()
	{
		assertTrue(csticModel.getMessages().isEmpty());
		assertTrue(csticModel.getAssignedValues().get(0).getMessages().isEmpty());
		assertTrue(csticModel.getAssignedValues().get(1).getMessages().isEmpty());
	}

	@Test
	public void testPrepareRetrievalOptionsWithDate()
	{
		final Date date = new Date();
		final String productCode = "PRODUCT";
		when(assignmentResolverStrategy.retrieveCreationDateForRelatedEntry(CONFIG_ID)).thenReturn(date);
		when(assignmentResolverStrategy.retrieveRelatedProductCode(CONFIG_ID)).thenReturn(productCode);
		final ConfigurationRetrievalOptions options = classUnderTest.prepareRetrievalOptionsWithDate(configModel);
		assertNotNull(options);
		assertEquals(date, options.getPricingDate());
		assertNull(productCode, options.getPricingProduct());
		assertEquals(discounts, options.getDiscountList());
	}


}
