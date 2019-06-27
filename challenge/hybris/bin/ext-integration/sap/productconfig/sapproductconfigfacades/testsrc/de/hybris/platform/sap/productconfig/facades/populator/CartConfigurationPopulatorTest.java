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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@SuppressWarnings("javadoc")
@UnitTest
public class CartConfigurationPopulatorTest extends AbstractOrderConfigurationPopulatorTest
{
	private CartConfigurationPopulator classUnderTest;
	private ConfigModel configModel;
	private ProductModel productModel;
	private ERPVariantProductModel erpVariantProductModel;

	@Mock
	private ModelService modelService;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService;
	@Mock
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;
	private final Double basePrice = new Double("1234");

	@Mock
	private PriceData priceDataBasePrice;

	@Mock
	private final CartPopulator<CartData> cartPopulator = new CartPopulator<>();
	@Mock
	private CommerceCartService commerceCartService;

	@Mock
	private CartModel sourceCart;
	@Mock
	private CartData targetCart;
	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;


	@Override
	@Before
	public void setup()
	{
		super.setup();
		source = new CartModel();
		source.setEntries(entryList);

		target = new CartData();
		target.setEntries(targetEntryList);

		configModel = new ConfigModelImpl();
		configModel.setId("id");

		productModel = new ProductModel();
		productModel.setCode("Product");

		erpVariantProductModel = new ERPVariantProductModel();
		erpVariantProductModel.setCode("ERP Variant Product");

		classUnderTest = new CartConfigurationPopulator();
		super.classUnderTest = classUnderTest;
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setConfigurationPricingOrderIntegrationService(configurationPricingOrderIntegrationService);
		classUnderTest.setCommerceCartService(commerceCartService);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		classUnderTest.setProductConfigurationPricingStrategy(productConfigurationPricingStrategy);

		Mockito.when(Boolean.valueOf(modelService.isRemoved(sourceEntry))).thenReturn(Boolean.FALSE);
		Mockito.when(
				configurationPricingOrderIntegrationService.ensureConfigurationInSession(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(configModel);
		Mockito.when(sourceEntry.getOrder()).thenReturn(source);
		Mockito.when(sourceEntry.getBasePrice()).thenReturn(basePrice);
		Mockito
				.when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(basePrice), sourceEntry.getOrder().getCurrency()))
				.thenReturn(priceDataBasePrice);
		classUnderTest.setPriceDataFactory(priceDataFactory);
		classUnderTest.setCartPopulator(cartPopulator);
	}

	@Test(expected = RuntimeException.class)
	public void testPopulateNoTargetItems()
	{
		Mockito.when(sourceEntry.getProduct()).thenReturn(productModel);
		Mockito.when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
		//entry numbers do not match->exception
		classUnderTest.populate((CartModel) source, (CartData) target);
	}

	@Test
	public void testPopulate()
	{
		final String configId = "123";
		final PK value = initializeSourceItem("Ext", configId);


		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		assertTrue("ItemPK not set", targetEntry.getItemPK().equals(value.toString()));
		assertTrue("Configuration not marked as attached", targetEntry.isConfigurationAttached());
		assertTrue("Configuration should be consistent", targetEntry.isConfigurationConsistent());
		verify(configurationAbstractOrderIntegrationStrategy).getConfigurationForAbstractOrderEntry(sourceEntry);
	}

	@Test
	public void testPopulateWithVariant()
	{
		final PK value = initializeVariantSourceItem(null);
		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		verify(configurationAbstractOrderIntegrationStrategy, times(0)).getConfigurationForAbstractOrderEntry(sourceEntry);
	}

	@Test
	public void testPopulateWithChangeableVariant()
	{
		final PK value = initializeVariantSourceItem("123");
		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		verify(configurationAbstractOrderIntegrationStrategy).getConfigurationForAbstractOrderEntry(sourceEntry);
	}

	@Test
	public void testPopulateCartEntryRemoved()
	{

		final String configId = "123";
		initializeSourceItem("Ext", configId);
		Mockito.when(Boolean.valueOf(modelService.isRemoved(sourceEntry))).thenReturn(Boolean.TRUE);
		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		assertFalse(targetEntry.isConfigurationAttached());
		assertFalse(targetEntry.isConfigurationConsistent());
	}


	@Test
	public void testPopulate_notConfigurable()
	{
		initializeSourceItem("Ext", null);

		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		assertFalse("Configuration marked as attached", targetEntry.isConfigurationAttached());
		assertFalse("Configuration should be inconsistent", targetEntry.isConfigurationConsistent());
		assertEquals("ErrorCount should be zero for non configurable products", 0, targetEntry.getConfigurationErrorCount());
	}



	@Test
	public void testPopulate_numberErrors_conflicts()
	{
		final Map<ProductInfoStatus, Integer> cpqSummary = new HashMap<>();
		cpqSummary.put(ProductInfoStatus.ERROR, Integer.valueOf(numberOfErrors));
		Mockito.when(sourceEntry.getCpqStatusSummaryMap()).thenReturn(cpqSummary);
		final String configId = "123";
		initializeSourceItem("Ext", configId);

		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);
		assertEquals(numberOfErrors, targetEntry.getConfigurationErrorCount());
	}

	@Test
	public void testPopulateConfigCompleteAndConsistent()
	{
		final String configId = "123";

		initializeSourceItem("Ext", configId);

		targetEntryList.add(targetEntry);

		classUnderTest.populate((CartModel) source, (CartData) target);

		assertTrue("Configuration should be consistent", targetEntry.isConfigurationConsistent());

	}



	@Test
	public void testPopulateConfigNoConfigModelInSession()
	{
		final String configId = "123";

		initializeSourceItem("Ext", configId);

		targetEntryList.add(targetEntry);
		classUnderTest.populate((CartModel) source, (CartData) target);

		assertTrue("Configuration should be consistent", targetEntry.isConfigurationConsistent());

	}


	@Test
	public void testPopulateNoConfigurationAttached()
	{
		final String configId = "";
		initializeSourceItem("", configId);
		assertFalse(targetEntry.isConfigurationAttached());
		targetEntryList.add(targetEntry);
		classUnderTest.populate((CartModel) source, (CartData) target);
		assertTrue("Configuration must be marked as attached, as a default config is created",
				targetEntry.isConfigurationAttached());
	}

	@Test
	public void testValidatePriceError()
	{
		mockMethodsThatAccessTenant();
		Mockito.when(productConfigurationPricingStrategy.isCartPricingErrorPresent(configModel)).thenReturn(Boolean.TRUE);
		targetEntry.setConfigurationConsistent(true);
		targetEntry.setConfigurationInfos(null);
		assertFalse(classUnderTest.validatePrice(configModel, sourceEntry, targetEntry));
		assertFalse(targetEntry.isConfigurationConsistent());
		assertEquals(1, targetEntry.getConfigurationInfos().size());
	}

	protected void mockMethodsThatAccessTenant()
	{
		if (Registry.hasCurrentTenant())
		{
			// use of spy is required here! getLocalizedText statically access Localization class which will in turn attemp to start the tenant
			// Registry.hasCurrentTenant() only works if the junit tentant is supressed, which is not always the case, when running tests integrated in team pipeline
			classUnderTest = Mockito.spy(classUnderTest);
			willReturn("a text").given(classUnderTest).getLocalizedText(Mockito.anyString());
		}
	}

	@Test
	public void testValidatePrice()
	{
		configModel.setPricingError(false);
		targetEntry.setConfigurationConsistent(true);
		targetEntry.setConfigurationInfos(null);
		classUnderTest.validatePrice(configModel, sourceEntry, targetEntry);
		assertTrue(targetEntry.isConfigurationConsistent());
		assertNull(targetEntry.getConfigurationInfos());
	}

	@Test
	public void testValidatePriceResultTrue()
	{
		configModel.setPricingError(false);
		targetEntry.setConfigurationConsistent(true);
		targetEntry.setConfigurationInfos(null);
		assertTrue(classUnderTest.validatePrice(configModel, sourceEntry, targetEntry));
	}



	@Test
	public void testCreateInlinePriceError()
	{
		mockMethodsThatAccessTenant();
		final List<ConfigurationInfoData> resultList = classUnderTest.createInlinePriceError();
		assertEquals(1, resultList.size());
		final ConfigurationInfoData result = resultList.get(0);
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, result.getConfiguratorType());
		assertEquals(ProductInfoStatus.ERROR, result.getStatus());
		if (Registry.hasCurrentTenant())
		{
			// in case a tenant is active, assert translated text is not empty
			assertFalse(result.getConfigurationLabel().isEmpty());
			assertFalse(result.getConfigurationValue().isEmpty());
		}
		else
		{
			// in case no tenant is active, assert we get the key
			assertEquals(CartConfigurationPopulator.PRICING_ERROR_TITLE, result.getConfigurationLabel());
			assertEquals(CartConfigurationPopulator.PRICING_ERROR_DESCRIPTION, result.getConfigurationValue());
		}
	}

	private PK initializeSourceItem(final String extConfig, final String configId)
	{
		final PK value = PK.fromLong(123);

		Mockito.when(sourceEntry.getPk()).thenReturn(value);
		Mockito.when(sourceEntry.getProduct()).thenReturn(productModel);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(targetEntry.getItemPK()))
				.thenReturn(configId);

		Mockito.when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(configId != null);

		return value;
	}

	private PK initializeVariantSourceItem(final String configId)
	{
		final PK value = PK.fromLong(123);

		Mockito.when(sourceEntry.getPk()).thenReturn(value);
		Mockito.when(sourceEntry.getProduct()).thenReturn(erpVariantProductModel);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(targetEntry.getItemPK()))
				.thenReturn(configId);

		Mockito.when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(configId != null);

		return value;
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testWriteToTargetEntryIllegalArgument()
	{
		super.testWriteToTargetEntryIllegalArgument();
	}

	@Override
	@Test
	public void testWriteToTargetEntry()
	{
		super.testWriteToTargetEntry();
	}

	@Override
	@Test
	public void testWriteToTargetEntryInconsistent()
	{
		super.testWriteToTargetEntryInconsistent();
	}

	@Override
	@Test
	public void testCreateConfigurationInfos()
	{
		super.testCreateConfigurationInfos();
	}

	@Override
	@Test(expected = ConversionException.class)
	public void testCreateConfigurationInfosException()
	{
		super.testCreateConfigurationInfosException();
	}

	@Override
	@Test(expected = IllegalStateException.class)
	public void testWriteToTargetEntrySummaryMapNull()
	{
		super.testWriteToTargetEntrySummaryMapNull();
	}


	@Test
	public void testModelService()
	{
		assertEquals(modelService, classUnderTest.getModelService());
	}

	@Test
	public void testCartPopulator()
	{
		assertEquals(cartPopulator, classUnderTest.getCartPopulator());
	}

	@Test
	public void testConfigurationAbstractOrderIntegrationStrategy()
	{
		assertEquals(configurationAbstractOrderIntegrationStrategy,
				classUnderTest.getConfigurationAbstractOrderIntegrationStrategy());
	}

	@After
	public void tearDown()
	{
		// This is necessary to delete answer objects between test-method execution
		reset(sourceEntry);
	}
}
