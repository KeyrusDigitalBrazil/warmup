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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPSCacheKeyGeneratorImplTest
{
	private static final String USER_ID = "user id";
	private static final String SERVICE_CLIENT_ID = "Service client Id";
	private static final String CONFIG_ID = "config id";
	private static final String PRODUCT = "product";
	private static final String BASE_SITE_UID = "base site uid";
	private static final String SERVICE_ID = "service id";
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenant id";
	private static final String LANGUAGE = "language";
	private static final String KB_ID = "kb id";
	@InjectMocks
	private CPSCacheKeyGeneratorImpl classUnderTest;

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseSiteModel baseSiteModel;
	@Mock
	private YaasConfigurationService yaasConfigurationService;
	@Mock
	private ProductConfigurationUserIdProvider userIdProvider;
	@Mock
	private YaasServiceModel serviceModel;
	@Mock
	private BaseSiteServiceMappingModel mappingModel;
	@Mock
	private YaasClientCredentialModel credentialModel;
	@Mock
	private YaasProjectModel projectModel;


	@Before
	public void setup()
	{
		Mockito.when(yaasConfigurationService.getYaasServiceForId(SERVICE_ID)).thenReturn(serviceModel);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getUid()).thenReturn(BASE_SITE_UID);
		Mockito.when(yaasConfigurationService.getBaseSiteServiceMappingForId(BASE_SITE_UID, serviceModel)).thenReturn(mappingModel);
		Mockito.when(mappingModel.getYaasClientCredential()).thenReturn(credentialModel);
		Mockito.when(credentialModel.getYaasProject()).thenReturn(projectModel);
		Mockito.when(projectModel.getIdentifier()).thenReturn(CPS_SERVICE_TENANT);
		Mockito.when(serviceModel.getServiceURL()).thenReturn(CPS_SERVICE_URL);
		Mockito.when(userIdProvider.getCurrentUserId()).thenReturn(USER_ID);
	}

	@Test
	public void testCreateMasterDataCacheKey()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.MASTER_DATA_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createMasterDataCacheKey(KB_ID, LANGUAGE);
		assertNotNull(result);
		assertEquals(KB_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_KB_ID));
		assertEquals(LANGUAGE, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_MASTER_DATA, result.getTypeCode());
	}

	@Test
	public void testGetCPSServiceParameter()
	{
		final Pair<String, String> result = classUnderTest.getCPSServiceParameter(SERVICE_ID);
		assertEquals(CPS_SERVICE_URL, result.getLeft());
		assertEquals(CPS_SERVICE_TENANT, result.getRight());
	}

	@Test
	public void testCreateKnowledgeBaseHeadersCacheKey()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.KB_DETERMINATION_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createKnowledgeBaseHeadersCacheKey(PRODUCT);
		assertNotNull(result);
		assertEquals(PRODUCT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_PRODUCT));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_KNOWLEDGEBASES, result.getTypeCode());
	}

	@Test
	public void testCreateCookieCacheKey()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.CONFIGRATION_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createCookieCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CONFIG_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_COOKIE, result.getTypeCode());
	}

	@Test
	public void testCreateValuePricesCacheKey()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.PRICING_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createValuePricesCacheKey(KB_ID, PRODUCT);
		assertNotNull(result);
		assertEquals(KB_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_KB_ID));
		assertEquals(PRODUCT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_PRODUCT));
		assertEquals(USER_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_USER_ID));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_VALUE_PRICES, result.getTypeCode());
	}

	@Test
	public void testCreateValuePricesCacheKeyWithNullPricingProduct()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.PRICING_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createValuePricesCacheKey(KB_ID, null);
		assertNotNull(result);
		assertEquals(KB_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_KB_ID));
		assertNull(result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_PRODUCT));
		assertEquals(USER_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_USER_ID));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_VALUE_PRICES, result.getTypeCode());
	}

	@Test
	public void testRetrieveBasicCPSParameters()
	{
		Mockito.when(yaasConfigurationService.getYaasServiceForId(SERVICE_CLIENT_ID)).thenReturn(serviceModel);
		final Map<String, String> result = classUnderTest.retrieveBasicCPSParameters(SERVICE_CLIENT_ID);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(CPS_SERVICE_URL, result.get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL));
		assertEquals(CPS_SERVICE_TENANT, result.get(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT));
	}

	@Test
	public void testCreateConfigurationCacheKey()
	{
		classUnderTest = Mockito.spy(classUnderTest);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(CPSCacheKeyGeneratorImpl.CONFIGRATION_SERVICE_ID))
				.thenReturn(serviceModel);
		final ProductConfigurationCacheKey result = classUnderTest.createConfigurationCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CONFIG_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_USER_ID));
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CacheUnitValueType.NON_SERIALIZABLE, result.getCacheValueType());
		assertEquals(CPSCacheKeyGeneratorImpl.TYPECODE_RUNTIME_CONFIGURATION, result.getTypeCode());
	}
}
