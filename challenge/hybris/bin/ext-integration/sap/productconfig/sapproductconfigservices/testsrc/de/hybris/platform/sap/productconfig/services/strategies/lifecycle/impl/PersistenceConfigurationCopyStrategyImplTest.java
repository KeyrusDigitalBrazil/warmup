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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDependencyHandler;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PersistenceConfigurationCopyStrategyImplTest
{
	private static final String CONFIG_ID = "S1";
	private static final String NEW_CONFIG_ID = "S2";
	private static final String PRODUCT_ID = "PRODUCT_ID";

	@InjectMocks
	private PersistenceConfigurationCopyStrategyImpl classUnderTest;

	@Mock
	private ProductConfigurationPersistenceService persistenceService;
	@Mock
	private UserService userService;
	@Mock
	private ConfigurationDeepCopyHandler configDeepCopyHandler;
	@Mock
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ConfigurationDependencyHandler configurationDependencyHandler;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private ProductConfigurationCacheAccessService cacheAccessService;

	private final ProductModel product = new ProductModel();
	private final AbstractOrderModel order = new OrderModel();
	private final AbstractOrderModel clone = new OrderModel();
	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel cloneEntry = new AbstractOrderEntryModel();
	private final ProductConfigurationModel configModel = new ProductConfigurationModel();
	private final ProductConfigurationModel newConfigModel = new ProductConfigurationModel();
	private final UserModel documentUser = new UserModel();
	private final UserModel aonymousUser = new UserModel();



	@Before
	@SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
	public void setUp()
	{
		configModel.setConfigurationId(CONFIG_ID);
		product.setCode(PRODUCT_ID);
		order.setEntries(Arrays.asList(entry));
		order.setUser(documentUser);
		clone.setEntries(Arrays.asList(cloneEntry));
		entry.setProductConfiguration(configModel);
		entry.setProduct(product);
		cloneEntry.setProductConfiguration(configModel);

		when(configDeepCopyHandler.deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN)).thenReturn(NEW_CONFIG_ID);
		when(persistenceService.getByConfigId(NEW_CONFIG_ID)).thenReturn(newConfigModel);
		given(userService.isAnonymousUser(documentUser)).willReturn(false);
		given(userService.isAnonymousUser(aonymousUser)).willReturn(true);
		when(assignmentResolverStrategy.retrieveRelatedObjectType(clone)).thenReturn(ProductConfigurationRelatedObjectType.UNKNOWN);
	}

	@Test
	public void testFinalizeCloneAonymousUser()
	{
		given(userService.getCurrentUser()).willReturn(aonymousUser);
		classUnderTest.finalizeClone(order, clone);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(persistenceService).getByConfigId(NEW_CONFIG_ID);
		assertSame(newConfigModel, cloneEntry.getProductConfiguration());
		verify(userService).setCurrentUser(documentUser);
		verify(userService).setCurrentUser(aonymousUser);
	}

	@Test
	public void testFinalizeClone()
	{
		given(userService.getCurrentUser()).willReturn(documentUser);
		classUnderTest.finalizeClone(order, clone);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(persistenceService).getByConfigId(NEW_CONFIG_ID);
		assertSame(newConfigModel, cloneEntry.getProductConfiguration());
		verify(userService).setCurrentUser(documentUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFinalizeCloneNoSource()
	{
		classUnderTest.finalizeClone(null, clone);
	}

	@Test
	public void testFinalizeCloneBaseSiteNotNull()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(new BaseSiteModel());
		given(userService.getCurrentUser()).willReturn(documentUser);
		classUnderTest.finalizeClone(order, clone);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(persistenceService).getByConfigId(NEW_CONFIG_ID);
		assertSame(newConfigModel, cloneEntry.getProductConfiguration());
		verify(userService).setCurrentUser(documentUser);
	}

	@Test
	public void testFinalizeCloneTargetIsQuote()
	{
		final AbstractOrderModel cloneQuote = new QuoteModel();
		cloneQuote.setEntries(Arrays.asList(cloneEntry));
		when(assignmentResolverStrategy.retrieveRelatedObjectType(cloneQuote))
				.thenReturn(ProductConfigurationRelatedObjectType.UNKNOWN);

		given(userService.getCurrentUser()).willReturn(documentUser);
		classUnderTest.finalizeClone(order, cloneQuote);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(persistenceService).getByConfigId(NEW_CONFIG_ID);
		assertSame(newConfigModel, cloneEntry.getProductConfiguration());
		verify(userService).setCurrentUser(documentUser);
	}

	@Test
	public void testFinalizeCloneNoTargetEntries()
	{
		//must not crash if no entries are present. In this case the respective attribute can be null in the target document
		clone.setEntries(null);
		classUnderTest.finalizeClone(order, clone);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
	}

	@Test
	public void testReplaceProductConfiguration()
	{
		final HashMap<String, String> oldConfigId2newConfigIdMap = new HashMap();
		oldConfigId2newConfigIdMap.put(CONFIG_ID, NEW_CONFIG_ID);
		classUnderTest.replaceProductConfiguration(entry, oldConfigId2newConfigIdMap);
		verify(persistenceService).getByConfigId(NEW_CONFIG_ID);
		assertSame(newConfigModel, entry.getProductConfiguration());
	}

	@Test
	public void testReplaceProductConfigurationNoProductConfigurationModel()
	{
		entry.setProductConfiguration(null);
		classUnderTest.replaceProductConfiguration(entry, new HashMap());
		verify(persistenceService, never()).getByConfigId(NEW_CONFIG_ID);
		assertNull(entry.getProductConfiguration());
	}

	@Test
	public void testReplaceProductConfigurationNoConfigId()
	{
		configModel.setConfigurationId(null);
		classUnderTest.replaceProductConfiguration(entry, new HashMap());
		verify(persistenceService, never()).getByConfigId(NEW_CONFIG_ID);
		assertSame(configModel, entry.getProductConfiguration());
	}

	@Test
	public void testFinalizeCloneEntry()
	{
		final HashMap<String, String> oldConfigId2newConfigIdMap = new HashMap();
		classUnderTest.finalizeCloneEntry(entry, oldConfigId2newConfigIdMap, ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(configDeepCopyHandler).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		assertEquals(NEW_CONFIG_ID, oldConfigId2newConfigIdMap.get(CONFIG_ID));
	}

	@Test
	public void testFinalizeCloneEntryNoProductConfigurationModel()
	{
		entry.setProductConfiguration(null);
		classUnderTest.finalizeCloneEntry(entry, new HashMap(), ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(configDeepCopyHandler, never()).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
	}

	@Test
	public void testFinalizeCloneEntryNoConfigId()
	{
		configModel.setConfigurationId(null);
		classUnderTest.finalizeCloneEntry(entry, new HashMap(), ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(configDeepCopyHandler, never()).deepCopyConfiguration(CONFIG_ID, PRODUCT_ID, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
	}

	@Test
	public void testReplaceBaseSiteIfPossibleNotReplaced()
	{
		final boolean replaced = classUnderTest.replaceBaseSiteIfPossible(order);
		assertFalse(replaced);
	}

	@Test
	public void testReplaceBaseSiteIfPossibleReplaced()
	{
		final BaseSiteModel baseSite = new BaseSiteModel();
		order.setSite(baseSite);
		final boolean replaced = classUnderTest.replaceBaseSiteIfPossible(order);
		verify(baseSiteService).setCurrentBaseSite(baseSite, true);
		assertTrue(replaced);
	}

	@Test
	public void testUpdateCurrentUserIfRequiredNoUpdate()
	{
		given(userService.getCurrentUser()).willReturn(documentUser);
		final UserModel userToReplace = classUnderTest.updateCurrentUserIfRequired(order);
		verify(userService, never()).setCurrentUser(any());
		assertSame(documentUser, userToReplace);
	}

	@Test
	public void testUpdateCurrentUserIfRequiredUserSet()
	{
		given(userService.getCurrentUser()).willReturn(aonymousUser);
		final UserModel userToReplace = classUnderTest.updateCurrentUserIfRequired(order);
		verify(userService).setCurrentUser(documentUser);
		assertSame(aonymousUser, userToReplace);
	}

	@Test
	public void testCopyProductConfigurationDependentObjects()
	{
		final Map<String, String> oldConfigId2newConfigIdMap = new HashMap<>();
		oldConfigId2newConfigIdMap.put(CONFIG_ID, NEW_CONFIG_ID);
		classUnderTest.copyProductConfigurationDependentObjects(oldConfigId2newConfigIdMap);
		verify(configurationDependencyHandler).copyProductConfigurationDependency(CONFIG_ID, NEW_CONFIG_ID);
		verify(cacheAccessService).removeConfigAttributeState(NEW_CONFIG_ID);
	}
}
