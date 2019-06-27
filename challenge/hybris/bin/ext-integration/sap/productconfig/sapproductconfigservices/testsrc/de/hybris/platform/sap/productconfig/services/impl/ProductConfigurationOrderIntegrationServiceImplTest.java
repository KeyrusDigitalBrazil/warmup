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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationOrderIntegrationServiceImplTest
{
	private ProductConfigurationOrderIntegrationServiceImpl classUnderTest;

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String NEW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"9\" VALUE_TXT=\"Value 9\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";

	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";
	private static final String CONFIG_ID = "abc123";

	@Mock
	private ProductConfigurationService configurationService;
	@Mock
	private TrackingRecorder mockTrackingRecorder;
	@Mock
	private ConfigModel mockConfigModel;
	@Mock
	private CartEntryModel mockCartEntry;
	@Mock
	private ProductModel mockProductModel;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy mockConfigurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy mockConfigurationAbstractOrderIntegrationStrategy;
	@Mock
	private ModelService mockModelService;
	@Mock
	private CommerceCartService mockCommerceCartService;
	@Mock
	private CartModel mockCartModel;

	private static final long keyAsLong = 12;
	private final PK primaryKey = PK.fromLong(keyAsLong);
	private CommerceCartParameter parameters;
	private static final String configId = "1";
	private final ConfigModel configModel = new ConfigModelImpl();
	private final InstanceModel instanceModel = new InstanceModelImpl();

	@Before
	public void setup()
	{
		classUnderTest = new ProductConfigurationOrderIntegrationServiceImpl();
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigurationService(configurationService);
		when(configurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		classUnderTest.setAbstractOrderEntryLinkStrategy(mockConfigurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(mockConfigurationAbstractOrderIntegrationStrategy);
		classUnderTest.setRecorder(mockTrackingRecorder);
		classUnderTest.setCommerceCartService(mockCommerceCartService);
		classUnderTest.setModelService(mockModelService);

		when(mockConfigModel.getId()).thenReturn(CONFIG_ID);
		when(mockCartEntry.getPk()).thenReturn(primaryKey);
		when(mockCartEntry.getProduct()).thenReturn(mockProductModel);
		when(mockCartEntry.getOrder()).thenReturn(mockCartModel);
		when(mockConfigurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(configId);
		when(mockConfigurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntry(mockCartEntry))
				.thenReturn(configModel);

		configModel.setRootInstance(instanceModel);
		configModel.setId(configId);
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testUpdateCartEntryExternalConfiguration() throws Exception
	{
		when(configurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);
		classUnderTest.updateCartEntryExternalConfiguration(parameters, mockCartEntry);
		verify(mockModelService).save(mockCartEntry);
	}

	@Test
	public void testUpdateCartEntryExternalConfiguration_withXML() throws Exception
	{
		final CartEntryModel cartEntry = Mockito.spy(new CartEntryModel());
		when(cartEntry.getPk()).thenReturn(primaryKey);
		when(cartEntry.getProduct()).thenReturn(mockProductModel);
		final ConfigModel cfgModel = createConfigModel();
		final String cartEntryKey = cartEntry.getPk().toString();
		when(configurationService.createConfigurationFromExternal(Mockito.any(), Mockito.eq(NEW_XML), Mockito.eq(cartEntryKey)))
				.thenReturn(cfgModel);

		when(configurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(NEW_XML);

		classUnderTest.updateCartEntryExternalConfiguration(NEW_XML, cartEntry);

		verify(mockConfigurationAbstractOrderIntegrationStrategy).updateAbstractOrderEntryOnUpdate(CONFIG_ID, cartEntry);
		verify(mockModelService, Mockito.times(0)).save(cartEntry);
	}

	private ConfigModel createConfigModel()
	{
		final PriceModel currentTotalPrice = new PriceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		currentTotalPrice.setCurrency("EUR");
		currentTotalPrice.setPriceValue(BigDecimal.valueOf(132.85));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		return configModel;
	}

	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testSessionAccessService()
	{
		final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy = new PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl();
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		assertEquals("Service should be available", configurationAbstractOrderEntryLinkStrategy,
				classUnderTest.getAbstractOrderEntryLinkStrategy());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesEmptyConfig()
	{
		final CartEntryConfigurationAttributes entryAttribs = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Empty configuration not consistent", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
		assertEquals("No errors expected", 0, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNoExternalCFG()
	{
		// no configuration: in this case we create a default configuration
		// which should not contain issues
		final CartEntryConfigurationAttributes cartEntryConfigurationAttributes = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertEquals("No errors expected", 0, cartEntryConfigurationAttributes.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNumberOfIssues()
	{
		when(Integer.valueOf(configurationService.getTotalNumberOfIssues(configModel))).thenReturn(Integer.valueOf(1));
		final CartEntryConfigurationAttributes entryAttribs = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertNotNull(entryAttribs);
		assertEquals("One error expected", 1, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributes()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		checkCartEntryConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotComplete()
	{
		configModel.setComplete(false);
		configModel.setConsistent(true);
		checkCartEntryNotConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(false);
		checkCartEntryNotConsistent();
	}

	private void checkCartEntryConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration should be consistent ", Boolean.TRUE, entryAttribs.getConfigurationConsistent());
	}

	private void checkCartEntryNotConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration shouldn't be consistent ", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
	}

	@Test
	public void testNoConfigID()
	{
		final String cartEntryKey = mockCartEntry.getPk().toString();
		final String externalConfig = "testExternalConfig";
		when(mockConfigurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		when(configurationService.createConfigurationFromExternal(any(), eq(externalConfig), eq(cartEntryKey)))
				.thenReturn(configModel);

		final CartEntryConfigurationAttributes entryAttribs = classUnderTest
				.calculateCartEntryConfigurationAttributes(mockCartEntry);
		assertNotNull(entryAttribs);
	}

	@Test
	public void testEnsureConfigurationInSessionWithIdAndModel()
	{
		final String cartEntryKey = mockCartEntry.getPk().toString();
		classUnderTest.ensureConfigurationInSession(cartEntryKey, mockCartEntry.getProduct().getCode(),
				mockCartEntry.getExternalConfiguration());
		verify(configurationService, times(1)).retrieveConfigurationModel(configId);
		verify(configurationService, times(0)).createDefaultConfiguration(any());
		verify(configurationService, times(0)).createConfigurationFromExternal(any(), any());
	}

	@Test
	public void testEnsureConfigurationInSessionWithoutIdAndWithoutExternal()
	{
		when(mockConfigurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		when(configurationService.createDefaultConfiguration(any())).thenReturn(configModel);
		classUnderTest.ensureConfigurationInSession(mockCartEntry.getPk().toString(), mockCartEntry.getProduct().getCode(),
				mockCartEntry.getExternalConfiguration());
		verify(configurationService, times(0)).retrieveConfigurationModel(configId);
		verify(configurationService, times(1)).createDefaultConfiguration(any());
		verify(configurationService, times(0)).createConfigurationFromExternal(any(), any());
	}

	@Test
	public void testEnsureConfigurationInSessionWithoutIdAndWithExternal()
	{
		when(mockCartEntry.getExternalConfiguration()).thenReturn(DUMMY_XML);
		final String cartEntryKey = mockCartEntry.getPk().toString();
		when(configurationService.createConfigurationFromExternal(any(), eq(DUMMY_XML), eq(cartEntryKey))).thenReturn(configModel);
		when(mockConfigurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		classUnderTest.ensureConfigurationInSession(cartEntryKey, mockCartEntry.getProduct().getCode(),
				mockCartEntry.getExternalConfiguration());
		verify(configurationService, times(0)).retrieveConfigurationModel(configId);
		verify(configurationService, times(0)).createDefaultConfiguration(any());
		verify(configurationService, times(1)).createConfigurationFromExternal(any(), eq(DUMMY_XML), eq(cartEntryKey));
	}

	@Test
	public void testFillSummaryMap()
	{
		when(Integer.valueOf(configurationService.getTotalNumberOfIssues(configModel))).thenReturn(Integer.valueOf(1));
		final ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
		classUnderTest.fillSummaryMap(mockCartEntry);
		verify(mockCartEntry, times(1)).setCpqStatusSummaryMap(arg.capture());
		assertNotNull(arg.getValue());
		assertEquals(1, arg.getValue().size());
		assertEquals(Integer.valueOf(1), arg.getValue().get(ProductInfoStatus.ERROR));
	}

	@Test
	public void testFillSummaryMapClear()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		final ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
		classUnderTest.fillSummaryMap(mockCartEntry);
		verify(mockCartEntry, times(1)).setCpqStatusSummaryMap(arg.capture());
		assertNotNull(arg.getValue());
		assertTrue(arg.getValue().isEmpty());
	}

	@Test
	public void testConfigurationAbstractOrderIntegrationStrategy()
	{
		assertEquals(mockConfigurationAbstractOrderIntegrationStrategy,
				classUnderTest.getConfigurationAbstractOrderIntegrationStrategy());
	}

	@Test
	public void testHasProductChangedForCartItem()
	{
		final AbstractOrderEntryModel cartItem = new AbstractOrderEntryModel();
		final ProductModel productInCart = new ProductModel();
		productInCart.setCode("product_1");
		cartItem.setProduct(productInCart);
		final ProductModel product = new ProductModel();
		product.setCode("product_1");
		assertFalse(classUnderTest.hasProductChangedForCartItem(product, cartItem));

		product.setCode("product_2");
		assertTrue(classUnderTest.hasProductChangedForCartItem(product, cartItem));
	}

	@Test
	public void testupdateCartEntryProductReturnsFalse()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final ProductModel productInCart = new ProductModel();
		productInCart.setCode("product_1");
		entry.setProduct(productInCart);
		final ProductModel product = new ProductModel();
		product.setCode("product_1");

		assertFalse(classUnderTest.updateCartEntryProduct(entry, product, configId));
	}

	@Test
	public void testupdateCartEntryProductReturnsTrue()
	{
		final ProductModel productInCart = new ProductModel();
		productInCart.setCode("product_1");
		final AbstractOrderEntryModel mockEntry = mock(AbstractOrderEntryModel.class);
		when(mockEntry.getPk()).thenReturn(primaryKey);
		when(mockEntry.getProduct()).thenReturn(productInCart);

		final ProductModel product = new ProductModel();
		product.setCode("product_2");

		assertTrue(classUnderTest.updateCartEntryProduct(mockEntry, product, configId));
	}
}
