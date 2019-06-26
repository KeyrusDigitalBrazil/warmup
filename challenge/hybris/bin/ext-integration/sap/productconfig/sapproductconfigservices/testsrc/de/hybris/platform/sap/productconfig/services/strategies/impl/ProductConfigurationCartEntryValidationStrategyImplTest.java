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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests
 */
@UnitTest
public class ProductConfigurationCartEntryValidationStrategyImplTest
{


	protected ProductConfigurationCartEntryValidationStrategyImpl classUnderTest;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;

	@Mock
	private CartEntryModel cartEntryModel;

	@Mock
	private ProductModel productModel;

	@Mock
	private ModelService modelService;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	private ConfigModel configModel;


	private static final String configId = "1";

	/**
	 * Before each test
	 */
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationCartEntryValidationStrategyImpl();
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setProductConfigurationPricingStrategy(productConfigurationPricingStrategy);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);

		when(cartEntryModel.getProduct()).thenReturn(productModel);
		when(cartEntryModel.getPk()).thenReturn(PK.fromLong(1));
		configModel = new ConfigModelImpl();
		configModel.setId(configId);
		when(productConfigurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		when(modelService.create(CPQOrderEntryProductInfoModel.class)).thenReturn(new CPQOrderEntryProductInfoModel());
		when(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(cartEntryModel)).thenReturn(true);
		when(configurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntry(cartEntryModel))
				.thenReturn(configModel);
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(productModel)).thenReturn(true);


	}


	/**
	 * No external configuration attached to cart entry
	 */
	@Test
	public void testValidateNoConfiguration()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(productModel)).thenReturn(false);
		assertNull(classUnderTest.validateConfiguration(cartEntryModel));
	}

	/**
	 * Configuration is not complete
	 */
	@Test
	public void testValidateNotComplete()
	{
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION, modification.getStatusCode());
	}

	/**
	 * Configuration is complete and consistent-> No validation message
	 */
	@Test
	public void testValidateCompleteAndConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNull(modification);
	}

	/**
	 * Configuration is not consistent but complete: validation message
	 */
	@Test
	public void testValidateCompleteNotConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(false);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION, modification.getStatusCode());
	}

	/**
	 * Configuration is not complete
	 */
	@Test
	public void testValidateKbNotValid()
	{
		when(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(cartEntryModel)).thenReturn(false);

		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.KB_NOT_VALID, modification.getStatusCode());
		Mockito.verify(configurationAbstractOrderIntegrationStrategy).invalidateCartEntryConfiguration(cartEntryModel);
	}

	@Test
	public void testValidatePricingError()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		Mockito.when(productConfigurationPricingStrategy.isCartPricingErrorPresent(configModel)).thenReturn(Boolean.TRUE);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.PRICING_ERROR, modification.getStatusCode());
	}

	@Test
	public void testResetConfigurationInfo()
	{
		final CartEntryModel orderEntry = new CartEntryModel();
		orderEntry.setProductInfos(Collections.unmodifiableList(Collections.emptyList()));
		classUnderTest.resetConfigurationInfo(orderEntry);
		assertEquals(1, orderEntry.getProductInfos().size());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, orderEntry.getProductInfos().get(0).getConfiguratorType());
		assertSame(orderEntry, orderEntry.getProductInfos().get(0).getOrderEntry());
	}

	@Test
	public void testAbstractOrderEntryLinkStrategy()
	{
		assertEquals(configurationAbstractOrderEntryLinkStrategy, classUnderTest.getAbstractOrderEntryLinkStrategy());
	}

	@Test
	public void testCpqConfigurableChecker()
	{
		assertEquals(cpqConfigurableChecker, classUnderTest.getCpqConfigurableChecker());
	}
}
