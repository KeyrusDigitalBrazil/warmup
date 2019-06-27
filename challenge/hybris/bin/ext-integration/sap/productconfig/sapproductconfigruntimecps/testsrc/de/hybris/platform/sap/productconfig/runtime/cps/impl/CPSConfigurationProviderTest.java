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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConfigurationChangeAdapter;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.ConfigurationModificationHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.GenericTestConfigModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSConfigurationProviderTest
{
	private static final String EXTERNAL_CONFIG = "external Config";
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "Product Code";
	private static final Date NOW = new Date();
	private static final Integer KBID = Integer.valueOf(124);
	private final static String KB_NAME = "kb";
	private final static String KB_LOGSYS = "RR4CLNT910";
	private final static String KB_VERSION = "1.0";

	private final CPSConfigurationProvider classUnderTest = new CPSConfigurationProvider();
	private final CPSConfiguration configuration = new CPSConfiguration();
	private KBKey kbKey = new KBKeyImpl(PRODUCT_CODE, KB_NAME, KB_LOGSYS, KB_VERSION);
	private ConfigModel genericTestModel;

	@Mock
	private Converter<CPSConfiguration, ConfigModel> configModelConverter;
	@Mock
	private CharonFacade charonFacade;
	@Mock
	private PricingHandler pricingHandler;
	@Mock
	private CharonKbDeterminationFacade charonKbDeterminationFacade;
	@Mock
	private ExternalConfigurationFromVariantStrategy externalConfigurationFromVariantStrategy;
	@Mock
	private CPSConfigurationChangeAdapter configurationChangeAdapter;
	@Mock
	private ConfigurationModificationHandler configurationModificationHandler;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigModelConverter(configModelConverter);
		classUnderTest.setCharonFacade(charonFacade);
		classUnderTest.setCharonKbDeterminationFacade(charonKbDeterminationFacade);
		classUnderTest.setExternalConfigurationFromVariantStrategy(externalConfigurationFromVariantStrategy);
		classUnderTest.setConfigurationChangeAdapter(configurationChangeAdapter);
		classUnderTest.setConfigurationModificationHandler(configurationModificationHandler);

		Mockito.when(charonKbDeterminationFacade.readKbIdForDate(Mockito.anyString(), Mockito.any())).thenReturn(KBID);
		genericTestModel = new GenericTestConfigModelImpl(new Properties()).createDefaultConfiguration();


	}

	@Test
	public void testConfigurationConverter()
	{
		assertEquals(configModelConverter, classUnderTest.getConfigModelConverter());
	}

	@Test
	public void testGetConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID);
		verify(charonFacade).getConfiguration(CONFIG_ID);
		verify(configurationModificationHandler, Mockito.times(0)).adjustVariantConditions(any(), any());
		verify(configModelConverter).convert(Mockito.any());
	}

	@Test
	public void testRetrieveConfigurationWithOptions() throws ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setDiscountList(new ArrayList<>());
		classUnderTest.retrieveConfigurationModel(CONFIG_ID, options);
		verify(charonFacade).getConfiguration(CONFIG_ID);
		verify(configModelConverter).convert(any());
		verify(configurationModificationHandler).adjustVariantConditions(any(), eq(options));
	}

	@Test
	public void testRetrieveConfigurationWithOptionsDiscountsNull() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID, new ConfigurationRetrievalOptions());
		Mockito.verify(charonFacade).getConfiguration(CONFIG_ID);
		Mockito.verify(configurationModificationHandler, Mockito.times(0)).adjustVariantConditions(Mockito.any(ConfigModel.class),
				Mockito.any());
		Mockito.verify(configModelConverter).convert(Mockito.any());
		verify(configurationModificationHandler, times(0)).adjustVariantConditions(any(), any());
	}

	@Test
	public void testGetExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
		Mockito.verify(charonFacade).getExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testCreateConfigurationFromExternal()
	{
		final String product = "PRODUCT";
		final KBKey kbKey = new KBKeyImpl(product);
		Mockito.when(charonFacade.createConfigurationFromExternal(EXTERNAL_CONFIG, product)).thenReturn(configuration);
		classUnderTest.createConfigurationFromExternalSource(kbKey, EXTERNAL_CONFIG);
		Mockito.verify(charonFacade).createConfigurationFromExternal(EXTERNAL_CONFIG, product);
		Mockito.verify(configModelConverter).convert(configuration);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCreateConfigurationFromExternal_som()
	{
		final Configuration extConfiguration = new ConfigurationImpl();
		classUnderTest.createConfigurationFromExternalSource(extConfiguration);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testReleaseSession()
	{
		classUnderTest.releaseSession(CONFIG_ID);
	}

	@Test
	public void testReleaseSessionWithVersion()
	{
		classUnderTest.releaseSession(CONFIG_ID, KB_VERSION);
		Mockito.verify(charonFacade).releaseSession(CONFIG_ID, KB_VERSION);
	}

	@Test
	public void testCharonKbDeterminationFacade()
	{
		assertEquals(charonKbDeterminationFacade, classUnderTest.getCharonKbDeterminationFacade());
	}


	@Test
	public void testKbForDateExists()
	{
		assertFalse(classUnderTest.isKbForDateExists(PRODUCT_CODE, NOW));
	}

	@Test
	public void testKbVersionExistsWithExtConfig()
	{
		given(charonKbDeterminationFacade.parseKBKeyFromExtConfig(PRODUCT_CODE, EXTERNAL_CONFIG)).willReturn(kbKey);
		assertFalse(classUnderTest.isKbVersionExists(kbKey, EXTERNAL_CONFIG));
		verify(charonKbDeterminationFacade).hasKBForKey(kbKey);
	}

	@Test
	public void testKbVersionExists()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE, KB_NAME, KB_LOGSYS, KB_VERSION);
		assertFalse(classUnderTest.isKbVersionExists(kbKey));
		verify(charonKbDeterminationFacade).hasKBForKey(kbKey);
	}

	@Test
	public void testKbVersionValid()
	{
		assertFalse(classUnderTest.isKbVersionValid(kbKey));
		verify(charonKbDeterminationFacade).hasValidKBForKey(kbKey);
	}

	@Test
	public void testFindKbId()
	{
		final Integer idFound = classUnderTest.findKbId(kbKey);
		assertEquals(KBID, idFound);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullKbKey()
	{
		classUnderTest.findKbId(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullProduct()
	{
		kbKey = new KBKeyImpl(null, KB_NAME, KB_LOGSYS, KB_VERSION);
		classUnderTest.findKbId(kbKey);
	}

	@Test
	public void testIsConfigureVariantSupported()
	{
		assertTrue(classUnderTest.isConfigureVariantSupported());
	}

	@Test
	public void testGetExternalConfigurationFromVariantStrategy()
	{
		assertEquals(externalConfigurationFromVariantStrategy, classUnderTest.getExternalConfigurationFromVariantStrategy());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUpdateConfiguration() throws ConfigurationEngineException
	{
		assertTrue(classUnderTest.updateConfiguration(genericTestModel));
	}

	@Test
	public void testChangeConfiguration() throws ConfigurationEngineException
	{
		final String updatedVersion = "updated version";
		Mockito.when(charonFacade.updateConfiguration(Mockito.any())).thenReturn(updatedVersion);
		assertEquals(updatedVersion, classUnderTest.changeConfiguration(genericTestModel));
	}

	@Test
	public void testChangeConfigurationNoUpdate() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.updateConfiguration(Mockito.any())).thenReturn(GenericTestConfigModelImpl.VERSION);
		assertEquals(GenericTestConfigModelImpl.VERSION, classUnderTest.changeConfiguration(genericTestModel));
	}

}
