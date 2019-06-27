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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceConfigurationLifecycleStrategyImplTest
{

	private static final String SESSION_ID = "session123";
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private static final String LOGICAL_SYSTEM = "logical system";
	private static final String KB_VERSION = "kb version";
	private static final String KB_NAME = "kb name";
	private static final String VERSION = "version";
	private static final String UPDATED_VERSION = "updated version";
	private static final String USER_SESSION_ID = "userSessionId";

	private PersistenceConfigurationLifecycleStrategyImpl classUnderTest;
	private ProductConfigurationModel productConfigModel;
	private final List<ProductConfigurationModel> modelsInSession = new ArrayList<>();

	@Mock
	private UserModel currentUser;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private SessionService sessionService;
	@Mock
	private DefaultSession session;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;
	@Mock
	private ProviderFactory providerFactoryMock;
	@Mock
	private ConfigurationProvider providerMock;

	private KBKey kbKey;
	private ConfigModel config;


	@Before
	public void setUp() throws ConfigurationEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationLifecycleStrategyImpl();
		classUnderTest.setModelService(modelService);
		classUnderTest.setUserService(userService);
		classUnderTest.setSessionService(sessionService);
		classUnderTest.setPersistenceService(persistenceService);
		classUnderTest.setProviderFactory(providerFactoryMock);

		config = new ConfigModelImpl();
		config.setId(CONFIG_ID);
		config.setVersion(VERSION);
		config.setKbKey(new KBKeyImpl(PRODUCT_CODE, KB_NAME, LOGICAL_SYSTEM, KB_VERSION));
		kbKey = new KBKeyImpl(PRODUCT_CODE, null, null, "1.0");

		productConfigModel = new ProductConfigurationModel();
		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setVersion(VERSION);
		productConfigModel.setUser(currentUser);

		given(modelService.create(ProductConfigurationModel.class)).willReturn(productConfigModel);
		given(currentUser.getName()).willReturn("cpq01");
		given(currentUser.getPk()).willReturn(PK.BIG_PK);
		given(userService.getCurrentUser()).willReturn(currentUser);
		given(sessionService.getCurrentSession()).willReturn(session);
		given(session.getSessionId()).willReturn(SESSION_ID);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(persistenceService.getByConfigId(eq(CONFIG_ID), anyBoolean())).willReturn(productConfigModel);
		given(providerFactoryMock.getConfigurationProvider()).willReturn(providerMock);
		given(providerMock.createDefaultConfiguration(kbKey)).willReturn(config);
		given(providerMock.retrieveConfigurationModel(CONFIG_ID)).willReturn(config);
		given(providerMock.retrieveConfigurationModel(eq(CONFIG_ID), any(ConfigurationRetrievalOptions.class))).willReturn(config);


		given(persistenceService.getByUserSessionId(USER_SESSION_ID)).willReturn(modelsInSession);
	}

	@Test
	public void testPersistNewConfiguration()
	{
		classUnderTest.persistNewConfiguration(config);
		verify(modelService).save(productConfigModel);
		assertSame(currentUser, productConfigModel.getUser());
		assertEquals(SESSION_ID, productConfigModel.getUserSessionId());
		assertTrue(CollectionUtils.isEmpty(productConfigModel.getProduct()));
		assertEquals(CONFIG_ID, productConfigModel.getConfigurationId());
		assertEquals(VERSION, productConfigModel.getVersion());
		assertEquals(LOGICAL_SYSTEM, productConfigModel.getKbLogsys());
		assertEquals(KB_VERSION, productConfigModel.getKbVersion());
		assertEquals(KB_NAME, productConfigModel.getKbName());
	}

	@Test
	public void testConfigurationFromExternalSource()
	{
		final Configuration extConfig = new ConfigurationImpl();
		given(providerMock.createConfigurationFromExternalSource(extConfig)).willReturn(config);
		classUnderTest.createConfigurationFromExternalSource(extConfig);
		verify(modelService).save(productConfigModel);
		verify(providerMock).createConfigurationFromExternalSource(extConfig);
	}

	@Test
	public void testConfigurationFromExternalSourceWithKey()
	{
		final String extConfigXml = "dummy";
		given(providerMock.createConfigurationFromExternalSource(kbKey, extConfigXml)).willReturn(config);
		classUnderTest.createConfigurationFromExternalSource(kbKey, extConfigXml);
		verify(modelService).save(productConfigModel);
		verify(providerMock).createConfigurationFromExternalSource(kbKey, extConfigXml);
	}

	@Test
	public void testCreateDefaultConfiguration()
	{
		classUnderTest.createDefaultConfiguration(kbKey);
		verify(modelService).save(productConfigModel);
		verify(providerMock).createDefaultConfiguration(kbKey);
	}

	@Test
	public void testUpdateConfiguration() throws ConfigurationEngineException
	{
		config.setVersion(null);
		productConfigModel.setVersion(VERSION);
		Mockito.when(providerMock.changeConfiguration(config)).thenReturn(UPDATED_VERSION);

		assertTrue(classUnderTest.updateConfiguration(config));
		assertEquals(UPDATED_VERSION, config.getVersion());
		assertEquals(UPDATED_VERSION, productConfigModel.getVersion());
		verify(modelService).save(productConfigModel);
	}

	@Test
	public void testUpdateConfigurationNoUpdate() throws ConfigurationEngineException
	{
		config.setVersion(null);
		productConfigModel.setVersion(VERSION);
		Mockito.when(providerMock.changeConfiguration(config)).thenReturn(VERSION);

		assertFalse(classUnderTest.updateConfiguration(config));
		assertEquals(VERSION, config.getVersion());
		assertEquals(VERSION, productConfigModel.getVersion());
		verify(modelService, Mockito.times(0)).save(productConfigModel);
	}

	@Test
	public void testRetrieveConfigurationFromVariant() throws ConfigurationEngineException
	{
		given(providerMock.retrieveConfigurationFromVariant(kbKey.getProductCode(), "variantCode")).willReturn(config);
		classUnderTest.retrieveConfigurationFromVariant(kbKey.getProductCode(), "variantCode");
		verify(modelService).save(productConfigModel);
		verify(providerMock).retrieveConfigurationFromVariant(kbKey.getProductCode(), "variantCode");
	}

	@Test
	public void testRetrieveConfigurationModel() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID);
		verify(providerMock).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test
	public void testRetrieveConfigurationModelWithOptions() throws ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		classUnderTest.retrieveConfigurationModel(CONFIG_ID, options);
		verify(providerMock).retrieveConfigurationModel(CONFIG_ID, options);
	}

	@Test
	public void testRetrieveExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
		verify(providerMock).retrieveExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testReleaseExpiredUserSession()
	{
		given(persistenceService.getByUserSessionId(USER_SESSION_ID)).willReturn(Collections.singletonList(productConfigModel));
		given(userService.isAnonymousUser(currentUser)).willReturn(true);
		classUnderTest.releaseExpiredSessions(USER_SESSION_ID);
		verify(persistenceService).getByUserSessionId(USER_SESSION_ID);
		verify(providerMock).releaseSession(config.getId(), VERSION);
	}

	@Test
	public void testReleaseExpiredUserSessionNonAnonymous()
	{
		given(persistenceService.getByUserSessionId(USER_SESSION_ID)).willReturn(Collections.singletonList(productConfigModel));
		given(userService.isAnonymousUser(currentUser)).willReturn(false);
		classUnderTest.releaseExpiredSessions(USER_SESSION_ID);
		verifyZeroInteractions(providerMock);
	}

	@Test
	public void testReleaseExpiredUserSessionLinkedToDocument()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID))
				.willReturn(Collections.singletonList(new AbstractOrderEntryModel()));
		given(persistenceService.getByUserSessionId(USER_SESSION_ID)).willReturn(Collections.singletonList(productConfigModel));
		given(userService.isAnonymousUser(currentUser)).willReturn(true);
		classUnderTest.releaseExpiredSessions(USER_SESSION_ID);
		verifyZeroInteractions(providerMock);
	}

	@Test
	public void testReleaseSession()
	{
		classUnderTest.releaseSession(config.getId());
		verify(providerMock).releaseSession(config.getId(), VERSION);
		verify(modelService).remove(productConfigModel);
	}

	@Test
	public void testUpdateUserLinkToConfiguration_withProductLink()
	{
		final ProductConfigurationModel configModelwithProduct = createConfigurationModelWithUserAndProductLink();
		modelsInSession.add(configModelwithProduct);
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(null);
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(productConfigModel);
		given(persistenceService.getByProductCodeAndUser(PRODUCT_CODE, currentUser)).willReturn(productConfigModel);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);

		classUnderTest.updateUserLinkToConfiguration(USER_SESSION_ID);
		assertSame(currentUser, configModelwithProduct.getUser());
		verify(modelService).save(configModelwithProduct);
		verify(modelService).remove(productConfigModel);
		verify(providerMock).releaseSession(productConfigModel.getConfigurationId(), VERSION);
	}

	@Test
	public void testUpdateUserLinkToConfiguration_noProductLink()
	{
		final ProductConfigurationModel configModel = new ProductConfigurationModel();
		modelsInSession.add(configModel);

		classUnderTest.updateUserLinkToConfiguration(USER_SESSION_ID);
		assertSame(currentUser, configModel.getUser());
		verify(modelService).save(configModel);
	}

	@Test
	public void testUpdateUserLinkToConfigurationSwitchToAnonymous()
	{
		given(userService.isAnonymousUser(currentUser)).willReturn(true);
		final ProductConfigurationModel configModel = new ProductConfigurationModel();
		modelsInSession.add(configModel);

		classUnderTest.updateUserLinkToConfiguration(USER_SESSION_ID);
		assertNull(configModel.getUser());
		verifyZeroInteractions(modelService);
	}

	@Test
	public void testRemoveDuplicateProductLink_noDuplicatesFound()
	{
		final ProductConfigurationModel configModelwithProduct = createConfigurationModelWithUserAndProductLink();
		modelsInSession.add(configModelwithProduct);
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(null);

		classUnderTest.updateUserLinkToConfiguration(USER_SESSION_ID);
		verify(modelService, times(0)).remove(productConfigModel);
		assertSame(currentUser, configModelwithProduct.getUser());
		verify(modelService).save(configModelwithProduct);
	}

	@Test
	public void testRemoveDuplicateProductLinkWithOrderLink()
	{
		final ProductConfigurationModel configModelToCheck = createConfigurationModelWithUserAndProductLink();
		final ProductConfigurationModel configModelwithProduct = createConfigurationModelWithUserAndProductLink();

		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(entry);
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(configModelwithProduct);
		given(persistenceService.getByProductCodeAndUser(PRODUCT_CODE, currentUser)).willReturn(configModelwithProduct);

		classUnderTest.removeDuplicateProductLink(configModelToCheck);
		verify(modelService, times(0)).remove(configModelwithProduct);
		verify(modelService).save(configModelwithProduct);
		assertNull(configModelwithProduct.getProduct());
	}


	@Test
	public void testRemoveDuplicateProductLinkWithoutOrderLink()
	{
		final ProductConfigurationModel configModelwithProduct = createConfigurationModelWithUserAndProductLink();
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(null);
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(productConfigModel);
		given(persistenceService.getByProductCodeAndUser(PRODUCT_CODE, currentUser)).willReturn(productConfigModel);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);

		classUnderTest.removeDuplicateProductLink(configModelwithProduct);
		verify(modelService).remove(productConfigModel);
		verify(providerMock).releaseSession(productConfigModel.getConfigurationId(), VERSION);
	}

	private ProductConfigurationModel createConfigurationModelWithUserAndProductLink()
	{
		final ProductConfigurationModel configModelwithProduct = new ProductConfigurationModel();
		configModelwithProduct.setConfigurationId(CONFIG_ID);
		configModelwithProduct.setVersion(VERSION);
		final UserModel annonumUser = new UserModel();
		configModelwithProduct.setUser(annonumUser);

		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		final List<ProductModel> productModels = new ArrayList<ProductModel>();
		productModels.add(product);

		configModelwithProduct.setProduct(productModels);
		return configModelwithProduct;
	}

	@Test
	public void testIsConfigForCurrentUserCorrectUserSignedIn()
	{
		assertTrue(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testIsConfigForCurrentUserDifferentUserSignedIn()
	{
		productConfigModel.setUser(new UserModel());
		assertFalse(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testIsConfigForCurrentUserAdmin()
	{
		given(userService.isAdmin(currentUser)).willReturn(true);
		productConfigModel.setUser(new UserModel());
		assertTrue(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testIsConfigForCurrentUserAnonymous()
	{
		productConfigModel.setUserSessionId(SESSION_ID);
		productConfigModel.setUser(null);
		assertTrue(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testPersistSessionId()
	{
		assertTrue(classUnderTest.persistSessionId());
	}

	@Test
	public void testPersistSessionIdWrongAttributeType()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS)).thenReturn("HUHU");
		assertTrue(classUnderTest.persistSessionId());
	}

	@Test
	public void testPersistSessionIdForUnboundSession()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS))
				.thenReturn(Boolean.valueOf(true));
		assertFalse(classUnderTest.persistSessionId());
	}

	@Test
	public void testIsConfigKnownTrue()
	{
		assertTrue(classUnderTest.isConfigKnown(CONFIG_ID));
	}

	@Test
	public void testIsConfigKnownFalse()
	{
		assertFalse(classUnderTest.isConfigKnown("HUHU"));
	}

	@Test
	public void testReleaseSessionSafely()
	{
		classUnderTest.releaseSessionSafely(CONFIG_ID, VERSION);
		verify(providerMock).releaseSession(CONFIG_ID, VERSION);
	}

	@Test(expected = IllegalStateException.class)
	public void testReleaseSessionSafelyException()
	{
		willThrow(new IllegalStateException()).given(providerMock).releaseSession(CONFIG_ID, VERSION);
		classUnderTest.releaseSessionSafely(CONFIG_ID, VERSION);
	}

	public void testReleaseSessionSafelyExceptionHandled()
	{
		final IllegalStateException ex = new IllegalStateException(new ConfigurationNotFoundException());
		willThrow(ex).given(providerMock).releaseSession(CONFIG_ID, VERSION);
		classUnderTest.releaseSessionSafely(CONFIG_ID, VERSION);
	}

}
