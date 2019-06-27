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
package de.hybris.platform.sap.productconfig.rules.rao.populator;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigurationRuleAwareService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class ProductConfigCartRAOPopulatorTest
{

	private static final String CONFIG_MODEL_ID = "12345";
	private static final String DUMMY_XML = "<xml>dummy</xml>";
	private static final String CONFIG_P_CODE = "configurableProductCode";
	private static final String CART_CODE = "1234";
	private static final String NO_CONFIG_P_CODE = "noConfigProductCode";
	private static final String ENTRY_PK = "123";
	private static final String CONFIG_MODEL_ID_NULL = null;
	private ProductConfigCartRAOPopulator classUnderTest;
	private CartModel cartModel;
	private CartRAO cartRao;
	private List<AbstractOrderEntryModel> entries;
	private ProductModel noConfigProduct;
	@Spy
	private AbstractOrderEntryModel entry;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ProductConfigurationRuleAwareService configService;
	private ConfigModel configModel;
	@Mock
	private Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter;
	private final ProductConfigRAO configRAO = new ProductConfigRAO();

	@Mock
	private Converter<UserModel, UserRAO> userConverter;
	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigCartRAOPopulator();
		cartRao = new CartRAO();
		createEmptyCart();
		createNoConfigEntry();
		classUnderTest.setProductConfigRaoConverter(productConfigRaoConverter);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setProductConfigService(configService);
		classUnderTest.setUserConverter(userConverter);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);

		given(productConfigRaoConverter.convert(configModel)).willReturn(configRAO);

		given(configService.retrieveConfigurationModelBypassRules(null))
				.willThrow(new IllegalArgumentException("config not found"));
		given(configService.createConfigurationFromExternal(Mockito.any(KBKey.class), Mockito.isNull(String.class)))
				.willThrow(new IllegalArgumentException("config not found"));

	}

	private void createNoConfigEntry()
	{
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).willReturn(false);

		noConfigProduct = new ProductModel();
		noConfigProduct.setCode(NO_CONFIG_P_CODE);
		entry.setQuantity(Long.valueOf(3));
		entry.setProduct(noConfigProduct);
		entry.setEntryNumber(Integer.valueOf(10));
		final PK pk = PK.parse(ENTRY_PK);
		given(entry.getPk()).willReturn(pk);

		configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		configModel.setId(CONFIG_MODEL_ID);
	}

	private void createEmptyCart()
	{
		cartModel = new CartModel();
		cartModel.setCode(CART_CODE);
		entries = new ArrayList<>();
		cartModel.setEntries(entries);
	}

	@Test
	public void testPopulateBasic()
	{
		classUnderTest.populate(cartModel, cartRao);
		assertEquals(CART_CODE, cartRao.getCode());
	}


	@Test
	public void testPopulateWithEntries()
	{
		entries.add(entry);
		classUnderTest.populate(cartModel, cartRao);
		assertEquals(1, cartRao.getEntries().size());
		final OrderEntryRAO firstCartRaoEntry = cartRao.getEntries().iterator().next();
		assertEquals(NO_CONFIG_P_CODE, firstCartRaoEntry.getProduct().getCode());
		assertEquals(3, firstCartRaoEntry.getQuantity());
		// For not-configurable products, we create the productConfigRAO containing productCode,
		// however it does not contain any cstic / value RAOs
		assertNotNull(firstCartRaoEntry.getProductConfiguration());
		assertEquals(NO_CONFIG_P_CODE, firstCartRaoEntry.getProductConfiguration().getProductCode());
		assertNull(firstCartRaoEntry.getProductConfiguration().getCstics());
	}

	@Test
	public void testPopulateWithEntries_externalConfigurationNull()
	{
		entries.add(entry);
		makeEntryConfigurablewithOutExternalConfig();
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entry.getPk().toString()))
				.willReturn(CONFIG_MODEL_ID_NULL);
		classUnderTest.populate(cartModel, cartRao);
		assertEquals(1, cartRao.getEntries().size());
		final OrderEntryRAO firstCartRaoEntry = cartRao.getEntries().iterator().next();
		assertEquals(CONFIG_P_CODE, firstCartRaoEntry.getProduct().getCode());
		assertEquals(3, firstCartRaoEntry.getQuantity());
		assertNull(firstCartRaoEntry.getProductConfiguration());
	}

	@Test
	public void testPopulateWithEntriesConfigurable()
	{
		entries.add(entry);
		makeEntryConfigurable();
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entry.getPk().toString()))
				.willReturn(CONFIG_MODEL_ID);
		given(configService.retrieveConfigurationModelBypassRules(CONFIG_MODEL_ID)).willReturn(configModel);
		classUnderTest.populate(cartModel, cartRao);
		assertEquals(1, cartRao.getEntries().size());
		final OrderEntryRAO firstCartRaoEntry = cartRao.getEntries().iterator().next();
		assertEquals(CONFIG_P_CODE, firstCartRaoEntry.getProduct().getCode());
		assertNotNull(firstCartRaoEntry.getProductConfiguration());
	}

	@Test
	public void testPopulateWithEntriesConfigurable_noSessionServiceAvailable()
	{
		entries.add(entry);
		makeEntryConfigurable();
		final String entryKey = entry.getPk().toString();
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entryKey)).willReturn(null);
		willReturn(configModel).given(configService).createConfigurationFromExternalBypassRules(Mockito.any(KBKey.class),
				Mockito.eq(DUMMY_XML));

		classUnderTest.populate(cartModel, cartRao);
		assertEquals(1, cartRao.getEntries().size());
		final OrderEntryRAO firstCartRaoEntry = cartRao.getEntries().iterator().next();
		assertEquals(CONFIG_P_CODE, firstCartRaoEntry.getProduct().getCode());
		assertNotNull(firstCartRaoEntry.getProductConfiguration());
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy).setConfigIdForCartEntry(entryKey, configModel.getId());
	}

	private void makeEntryConfigurable()
	{
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).willReturn(true);

		entry.getProduct().setCode(CONFIG_P_CODE);
		entry.setExternalConfiguration(DUMMY_XML);
	}

	private void makeEntryConfigurablewithOutExternalConfig()
	{
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).willReturn(true);

		entry.getProduct().setCode(CONFIG_P_CODE);
		entry.setExternalConfiguration(null);
	}

	@Test
	public void testPopulateUser()
	{
		final UserModel userModel = new UserModel();
		final UserRAO userRAO = new UserRAO();
		given(userConverter.convert(userModel)).willReturn(userRAO);

		cartModel.setUser(userModel);
		classUnderTest.populate(cartModel, cartRao);
		assertEquals(CART_CODE, cartRao.getCode());
		assertEquals(userRAO, cartRao.getUser());
	}


	@Test
	public void testPopulateWithEntriesNotConfigurableProduct()
	{
		entries.add(entry);
		makeEntryConfigurable();
		// Not Configurable Variant
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).willReturn(false);

		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entry.getPk().toString()))
				.willReturn(CONFIG_MODEL_ID);
		given(configService.retrieveConfigurationModelBypassRules(CONFIG_MODEL_ID)).willReturn(configModel);

		final ProductConfigCartRAOPopulator spyClassUnderTest = spy(classUnderTest);
		spyClassUnderTest.populate(cartModel, cartRao);
		verify(spyClassUnderTest, Mockito.never()).populateProductConfig(Mockito.any(), Mockito.any());
	}

}
