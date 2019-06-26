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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationAccessControlService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationServiceImplTest
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImplTest.class);
	private static final String PRODUCT_KEY = "product key";
	private static final String CONFIG_ID = "abc123";
	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String NEW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"9\" VALUE_TXT=\"Value 9\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";
	private static final String PRODUCT_CODE = "PRODUCT_CODE";
	private static final PK primaryKey = PK.fromLong(12);
	private static final String BASE_PRODUCT_CODE = "base product";


	private final ProductConfigurationServiceImpl classUnderTest = new ProductConfigurationServiceImpl();

	@Mock
	private TrackingRecorder recorder;
	@Mock
	private ConfigurationProvider configurationProviderMock;
	@Mock
	private ConfigModel modelMock;
	@Mock
	private ProviderFactory providerFactoryMock;
	@Mock
	private CartEntryModel cartEntry;
	@Mock
	private ProductModel productModel;
	@Mock
	private ConfigurationLifecycleStrategy configLifecycleStrategy;
	@Mock
	private ConfigurationModelCacheStrategy configModelCacheStrategy;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductConfigurationModel productConfigurationModel;
	@Mock
	private ProductConfigurationAccessControlService productConfigurationAccessControlService;
	@Mock
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	@Mock
	private ProductConfigurationCacheAccessService mockedCacheAccessService;
	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;
	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	private CommerceCartParameter parameters;
	private final ConfigModel configModel = new ConfigModelImpl();
	private final InstanceModel instanceModel = new InstanceModelImpl();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setProviderFactory(providerFactoryMock);
		classUnderTest.setProductConfigurationAccessControlService(productConfigurationAccessControlService);
		when(providerFactoryMock.getConfigurationProvider()).thenReturn(configurationProviderMock);
		when(configurationProviderMock.createConfigurationFromExternalSource(any(KBKey.class), anyString()))
				.thenReturn(configModel);
		when(configurationProviderMock.createDefaultConfiguration((any(KBKey.class)))).thenReturn(configModel);
		given(productConfigurationAccessControlService.isUpdateAllowed(anyString())).willReturn(true);
		given(productConfigurationAccessControlService.isReleaseAllowed(anyString())).willReturn(true);
		given(productConfigurationAccessControlService.isReadAllowed(anyString())).willReturn(true);

		classUnderTest.setConfigLifecycleStrategy(configLifecycleStrategy);
		classUnderTest.setConfigModelCacheStrategy(configModelCacheStrategy);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setRecorder(recorder);
		classUnderTest.setAssignmentResolverStrategy(assignmentResolverStrategy);

		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);

		when(modelMock.getId()).thenReturn(CONFIG_ID);
		when(cartEntry.getPk()).thenReturn(primaryKey);
		when(cartEntry.getProduct()).thenReturn(productModel);
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(CONFIG_ID);

		when(configModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(configModel);
		when(persistenceService.getByConfigId(anyString())).thenReturn(productConfigurationModel);
		when(productConfigurationModel.getConfigurationId()).thenReturn(CONFIG_ID);

		configModel.setRootInstance(instanceModel);
		configModel.setId(CONFIG_ID);
		configModel.setKbKey(new KBKeyImpl(null));
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testRetrieveConfiguration() throws Exception
	{

		when(configLifecycleStrategy.retrieveConfigurationModel("1")).thenReturn(modelMock);
		final ConfigModel retrievedModel = classUnderTest.retrieveConfigurationModel("1");
		assertTrue("Not delegated", retrievedModel == modelMock);
	}

	@Test
	public void testRetrieveExternalConfiguration() throws Exception
	{
		when(configLifecycleStrategy.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);
		final String xmlString = classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
		assertTrue("Not delegated", xmlString == DUMMY_XML);
	}

	@Test
	public void testRetrieveExternalConfigurationFailure() throws ConfigurationEngineException
	{
		when(configLifecycleStrategy.retrieveExternalConfiguration(CONFIG_ID)).thenThrow(ConfigurationEngineException.class);
		try
		{
			classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
			fail();
		}
		catch (final IllegalStateException ex)
		{
			verify(configModelCacheStrategy).purge();
		}
	}


	@Test
	public void testCreateConfigurationFromExternalSource() throws Exception
	{
		final KBKey kbKey = new KBKeyImpl("pCode");
		when(configLifecycleStrategy.createConfigurationFromExternalSource(kbKey, "extConfig")).thenReturn(configModel);

		final ConfigModel craetedConfigModel = classUnderTest.createConfigurationFromExternal(kbKey, "extConfig");

		assertSame(configModel, craetedConfigModel);
		verify(configModelCacheStrategy).setConfigurationModelEngineState(CONFIG_ID, craetedConfigModel);
		verifyNoMoreInteractions(configModelCacheStrategy);

	}

	@Test
	public void testCreateConfigurationFromExternalSourceWithEntryAndOptions() throws Exception
	{
		final KBKey kbKey = new KBKeyImpl("pCode");
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		Mockito.when(configLifecycleStrategy.createConfigurationFromExternalSource(kbKey, "extConfig")).thenReturn(configModel);

		final ConfigModel createdConfigModel = classUnderTest.createConfigurationFromExternal(kbKey, "extConfig", "123", options);

		assertSame(configModel, createdConfigModel);
		verify(configModelCacheStrategy).setConfigurationModelEngineState(CONFIG_ID, createdConfigModel);
		verify(configurationAbstractOrderEntryLinkStrategy).setConfigIdForCartEntry("123", CONFIG_ID);
	}

	@Test
	public void testCreateConfigurationFromExternalSourceWithEntry() throws Exception
	{
		final KBKey kbKey = new KBKeyImpl("pCode");
		when(configLifecycleStrategy.createConfigurationFromExternalSource(kbKey, "extConfig")).thenReturn(configModel);

		final ConfigModel craetedConfigModel = classUnderTest.createConfigurationFromExternal(kbKey, "extConfig", "123");

		assertSame(configModel, craetedConfigModel);
		verify(configModelCacheStrategy).setConfigurationModelEngineState(CONFIG_ID, craetedConfigModel);
		verify(configurationAbstractOrderEntryLinkStrategy).setConfigIdForCartEntry("123", CONFIG_ID);
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
	public void testGetLockNotNull()
	{
		assertNotNull("Lock objects may not be null", ProductConfigurationServiceImpl.getLock(CONFIG_ID_1));
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
	public void testRetrieveConfigurationCached() throws ConfigurationEngineException
	{

		when(configModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(null);
		when(configLifecycleStrategy.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);

		ConfigModel retrievedModel = classUnderTest.retrieveConfigurationModel(CONFIG_ID);

		verify(configModelCacheStrategy, times(2)).setConfigurationModelEngineState(contains(CONFIG_ID), same(retrievedModel));
		when(configModelCacheStrategy.getConfigurationModelEngineState(contains(CONFIG_ID))).thenReturn(modelMock);
		retrievedModel = classUnderTest.retrieveConfigurationModel(CONFIG_ID);
		verify(configLifecycleStrategy, times(1)).retrieveConfigurationModel(CONFIG_ID);
		assertTrue("Not delegated", retrievedModel == modelMock);
	}

	@Test
	public void testRetrieveConfigurationModelFromConfigurationEngineWithOptions() throws ConfigurationEngineException
	{

		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		when(configLifecycleStrategy.retrieveConfigurationModel(CONFIG_ID, options)).thenReturn(modelMock);
		final ConfigModel retrievedModel = classUnderTest.retrieveConfigurationModelFromConfigurationEngine(CONFIG_ID, options);
		verify(configLifecycleStrategy).retrieveConfigurationModel(CONFIG_ID, options);
	}

	@Test
	public void testRetrieveConfigurationModelFromConfigurationEngineNullOptions() throws ConfigurationEngineException
	{

		when(configLifecycleStrategy.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		final ConfigModel retrievedModel = classUnderTest.retrieveConfigurationModelFromConfigurationEngine(CONFIG_ID, null);
		verify(configLifecycleStrategy).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveConfigurationEngineException() throws ConfigurationEngineException
	{

		when(configLifecycleStrategy.retrieveConfigurationModel(CONFIG_ID)).thenThrow(new ConfigurationEngineException());
		try
		{
			classUnderTest.retrieveConfigurationModelFromConfigurationEngine(CONFIG_ID, null);
		}
		catch (final IllegalStateException ex)
		{
			verify(configModelCacheStrategy).purge();
			verify(configModelCacheStrategy).removeConfigAttributeState(CONFIG_ID);
			assertTrue(ex.getCause() instanceof ConfigurationEngineException);
			throw ex;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConfigurationEngineException() throws ConfigurationEngineException
	{

		when(Boolean.valueOf(configLifecycleStrategy.updateConfiguration(modelMock))).thenThrow(new ConfigurationEngineException());
		try
		{
			classUnderTest.updateConfiguration(modelMock);
		}
		catch (final IllegalStateException ex)
		{
			verify(configModelCacheStrategy).removeConfigAttributeState(CONFIG_ID);
			verify(configModelCacheStrategy).purge();
			assertTrue(ex.getCause() instanceof ConfigurationEngineException);
			throw ex;
		}
	}

	@Test
	public void testUpdateConfigurationInvalidateCache() throws ConfigurationEngineException
	{
		when(configLifecycleStrategy.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		when(Boolean.valueOf(configLifecycleStrategy.updateConfiguration(modelMock))).thenReturn(Boolean.TRUE);

		classUnderTest.updateConfiguration(modelMock);

		verify(configModelCacheStrategy, times(1)).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void testGetNumberOfConflictsEmptyConfig()
	{
		final int numberOfConflicts = classUnderTest.countNumberOfSolvableConflicts(configModel);
		assertEquals("No conflicts", 0, numberOfConflicts);
	}

	@Test
	public void testGetNumberOfConflictsWithSolvableConflicts()
	{
		final SolvableConflictModel conflict = new SolvableConflictModelImpl();
		configModel.setSolvableConflicts(Arrays.asList(conflict));
		final int numberOfConflicts = classUnderTest.countNumberOfSolvableConflicts(configModel);
		assertEquals("We expect one conflict", 1, numberOfConflicts);
	}

	@Test
	public void testGetNumberOfConflictsWithNotConsistenCstics()
	{
		instanceModel.setCstics(createListOfCsticsOnlyConsistenFlag());

		final List<InstanceModel> subInstances = instanceModel.getSubInstances();
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setCstics(createListOfCsticsOnlyConsistenFlag());
		subInstances.add(subInstance);
		instanceModel.setSubInstances(subInstances);

		final List<CsticModel> cstics = configModel.getRootInstance().getCstics();
		cstics.get(1).setConsistent(false);
		configModel.getRootInstance().setCstics(cstics);

		final int numberOfConflicts = classUnderTest.countNumberOfSolvableConflicts(configModel);
		assertEquals("We expect four conflict", 4, numberOfConflicts);
	}

	private List<CsticModel> createListOfCsticsOnlyConsistenFlag()
	{
		final List<CsticModel> cstics = new ArrayList<>();
		CsticModel cstic = new CsticModelImpl();
		cstic.setConsistent(false);
		cstics.add(cstic);

		cstic = new CsticModelImpl();
		cstic.setConsistent(false);
		cstics.add(cstic);

		cstic = new CsticModelImpl();
		cstic.setConsistent(true);
		cstics.add(cstic);
		return cstics;
	}

	@Test
	public void releaseSession()
	{
		classUnderTest.releaseSession(CONFIG_ID);
		verify(configLifecycleStrategy).releaseSession(CONFIG_ID);
		verify(configModelCacheStrategy).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void releaseSession_true()
	{
		classUnderTest.releaseSession(CONFIG_ID, true);
		verify(configLifecycleStrategy).releaseSession(CONFIG_ID);
		verifyZeroInteractions(configModelCacheStrategy);
	}

	@Test
	public void releaseSession_false()
	{
		classUnderTest.releaseSession(CONFIG_ID, false);
		verify(configLifecycleStrategy).releaseSession(CONFIG_ID);
		verify(configModelCacheStrategy).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void hasKbForDate_noInput()
	{
		final String productCode = null;
		final Date kbDate = null;
		assertFalse("No KB version exist.", classUnderTest.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void hasKbForDate_false() throws ParseException
	{
		final String productCode = "Product1";
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final Date kbDate = sdf.parse("20120201");
		when(Boolean.valueOf(configurationProviderMock.isKbForDateExists(productCode, kbDate))).thenReturn(Boolean.FALSE);

		assertFalse("No KB version exists.", classUnderTest.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void isKbVersionValid()
	{

		final KBKey kbKey = new KBKeyImpl(PRODUCT_KEY);
		classUnderTest.isKbVersionValid(kbKey);

		verify(configurationProviderMock).isKbVersionValid(kbKey);
	}

	@Test
	public void isKbForDateExists_true() throws ParseException
	{
		final String productCode = "Product2";
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final Date kbDate = sdf.parse("20150201");
		when(Boolean.valueOf(configurationProviderMock.isKbForDateExists(productCode, kbDate))).thenReturn(Boolean.TRUE);

		assertTrue("The KB version have to exist.", classUnderTest.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void testRemoveConfigFromCache()
	{
		classUnderTest.removeConfigAttributesFromCache(CONFIG_ID);
		verify(configModelCacheStrategy).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void testCleanUpAfterEngineErrorNoConfigModel()
	{
		classUnderTest.cleanUpAfterEngineError(CONFIG_ID);
		verify(configModelCacheStrategy).purge();
	}

	@Test
	public void testExtractKbKey()
	{
		classUnderTest.extractKbKey(PRODUCT_KEY, DUMMY_XML);
		verify(configurationProviderMock).extractKbKey(PRODUCT_KEY, DUMMY_XML);
	}

	@Test
	public void testCheckUpdateAllowed()
	{
		classUnderTest.checkUpdateAllowed(modelMock);
		verify(productConfigurationAccessControlService).isUpdateAllowed(CONFIG_ID);
	}

	@Test(expected = ProductConfigurationAccessException.class)
	public void testCheckUpdateAllowedForbidden()
	{
		when(productConfigurationAccessControlService.isUpdateAllowed(CONFIG_ID)).thenReturn(false);
		classUnderTest.checkUpdateAllowed(modelMock);
	}

	@Test
	public void testRetrieveCorrectPricingDatePastDate()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.ORDER_ENTRY);
		when(assignmentResolverStrategy.retrieveCreationDateForRelatedEntry(CONFIG_ID)).thenReturn(new Date());
		final ConfigurationRetrievalOptions result = classUnderTest.retrieveCorrectPricingDate(CONFIG_ID);
		assertNotNull(result);
		assertNull(result.getDiscountList());
		assertNotNull(result.getPricingDate());
	}

	@Test
	public void testRetrieveCorrectPricingDateNull()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.CART_ENTRY);
		assertNull(classUnderTest.retrieveCorrectPricingDate(CONFIG_ID));
	}

	@Test
	public void testIsRelatedObjectReadOnlyFalseCart()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.CART_ENTRY);
		assertFalse(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, null));
	}

	@Test
	public void testIsRelatedObjectReadOnlyTrueSavedCart()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY);
		assertTrue(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, null));
	}

	@Test
	public void testIsRelatedObjectReadOnlyTrueOrder()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.ORDER_ENTRY);
		assertTrue(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, null));
	}

	@Test
	public void testIsRelatedObjectReadOnlyTrueQuote()
	{
		when(assignmentResolverStrategy.retrieveRelatedObjectType(anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.QUOTE_ENTRY);
		assertTrue(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, null));
	}

	@Test
	public void testIsRelatedObjectReadOnlyOptionsFalseCart()
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setRelatedObjectType(ProductConfigurationRelatedObjectType.CART_ENTRY);
		assertFalse(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, options));
	}

	@Test
	public void testIsRelatedObjectReadOnlyOptionsTrueSavedCart()
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setRelatedObjectType(ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY);
		assertTrue(classUnderTest.isRelatedObjectReadOnly(CONFIG_ID, options));
	}

	@Test
	public void testRetrieveRelatedObjectTypeOptionsNull()
	{
		final ProductConfigurationRelatedObjectType type = classUnderTest.retrieveRelatedObjectType(CONFIG_ID, null);
		verify(assignmentResolverStrategy).retrieveRelatedObjectType(CONFIG_ID);
	}

	@Test
	public void testRetrieveRelatedObjectTypeOptionsCart()
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setRelatedObjectType(ProductConfigurationRelatedObjectType.CART_ENTRY);
		final ProductConfigurationRelatedObjectType type = classUnderTest.retrieveRelatedObjectType(CONFIG_ID, options);
		verify(assignmentResolverStrategy, times(0)).retrieveRelatedObjectType(CONFIG_ID);
		assertEquals(ProductConfigurationRelatedObjectType.CART_ENTRY, type);
	}

	@Test
	public void testRetrieveRelatedObjectTypeOptionsNotNullButType()
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		final ProductConfigurationRelatedObjectType type = classUnderTest.retrieveRelatedObjectType(CONFIG_ID, options);
		verify(assignmentResolverStrategy).retrieveRelatedObjectType(CONFIG_ID);
	}


	public void testCheckReleaseAllowed()
	{
		classUnderTest.checkReleaseAllowed(CONFIG_ID);
		verify(productConfigurationAccessControlService).isUpdateAllowed(CONFIG_ID);
	}

	@Test(expected = ProductConfigurationAccessException.class)
	public void testCheckReleaseAllowedForbidden()
	{
		when(productConfigurationAccessControlService.isReleaseAllowed(CONFIG_ID)).thenReturn(false);
		classUnderTest.checkReleaseAllowed(CONFIG_ID);
	}

	public void testCheckReadAllowed()
	{
		classUnderTest.checkReadAllowed(CONFIG_ID);
		verify(productConfigurationAccessControlService).isReadAllowed(CONFIG_ID);
	}

	@Test(expected = ProductConfigurationAccessException.class)
	public void testCheckReadAllowedForbidden()
	{
		when(productConfigurationAccessControlService.isReadAllowed(CONFIG_ID)).thenReturn(false);
		classUnderTest.checkReadAllowed(CONFIG_ID);
	}

	@Test
	public void testAssignVariantProductPCPV() throws Exception
	{
		final String productCode = "PRODUCT_CODE";
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbKey(new KBKeyImpl("test_code", null, null, null));
		when(mockConfigProductUtil.getProductForCurrentCatalog(productCode)).thenReturn(productModel);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(productModel)).thenReturn(true);
		classUnderTest.updateKbKeyForVariants(configModel, BASE_PRODUCT_CODE, PRODUCT_CODE);
		assertEquals(PRODUCT_CODE, configModel.getKbKey().getProductCode());
	}

	@Test
	public void testAssignVariantProductNotPCPV() throws Exception
	{
		final String productCode = "PRODUCT_CODE";
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbKey(new KBKeyImpl("test_code", null, null, null));
		when(mockConfigProductUtil.getProductForCurrentCatalog(productCode)).thenReturn(productModel);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(productModel)).thenReturn(false);
		classUnderTest.updateKbKeyForVariants(configModel, BASE_PRODUCT_CODE, PRODUCT_CODE);
		assertEquals(BASE_PRODUCT_CODE, configModel.getKbKey().getProductCode());
	}

	@Test
	public void testIsChangeableVariantPCPV() throws Exception
	{

		final String productCode = "PRODUCT_CODE";
		when(mockConfigProductUtil.getProductForCurrentCatalog(productCode)).thenReturn(productModel);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(productModel)).thenReturn(true);
		assertTrue(classUnderTest.isChangeableVariant(productCode));
	}

	@Test
	public void testIsChangeableVariantNotPCPV() throws Exception
	{
		final String productCode = "PRODUCT_CODE";
		when(mockConfigProductUtil.getProductForCurrentCatalog(productCode)).thenReturn(productModel);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(productModel)).thenReturn(false);
		assertFalse(classUnderTest.isChangeableVariant(productCode));
	}
}
