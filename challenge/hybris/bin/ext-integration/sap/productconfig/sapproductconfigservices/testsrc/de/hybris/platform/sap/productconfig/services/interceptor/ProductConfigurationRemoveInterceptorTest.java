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
package de.hybris.platform.sap.productconfig.services.interceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationRemoveInterceptorTest
{

	private static final String CONFIG_ID = "c123";
	private static final String CART_ENTRY_KEY = "123";

	private ProductConfigurationRemoveInterceptor classUnderTest;

	@Mock
	private ProductConfigurationPersistenceService persitenceServiceMock;
	@Mock
	private ConfigurationLifecycleStrategy lifeCycleStrategyMock;
	@Mock
	private InterceptorContext ctxtMock;
	@Mock
	private ModelService modelServiceMock;
	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModelMock;
	@Mock
	private BaseSiteService baseSiteServiceMock;
	@Mock
	private ProductConfigEventListenerUtil productConfigEventListenerUtil;

	private ProductConfigurationModel productConfigModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationRemoveInterceptor();
		classUnderTest.setProductConfigurationPersistenceService(persitenceServiceMock);
		classUnderTest.setConfigurationLifecycleStrategy(lifeCycleStrategyMock);
		classUnderTest.setBaseSiteService(baseSiteServiceMock);
		classUnderTest.setProductConfigEventListenerUtil(productConfigEventListenerUtil);

		given(ctxtMock.getModelService()).willReturn(modelServiceMock);
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(new BaseSiteModel());

		productConfigModel = new ProductConfigurationModel();
		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setProduct(Collections.EMPTY_LIST);

		given(abstractOrderEntryModelMock.getProductConfiguration()).willReturn(productConfigModel);
		given(abstractOrderEntryModelMock.getPk()).willReturn(PK.parse(CART_ENTRY_KEY));
		given(persitenceServiceMock.isOnlyRelatedToGivenEntry(CONFIG_ID, CART_ENTRY_KEY)).willReturn(true);
	}

	@Test
	public void testOnRemove() throws InterceptorException
	{
		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verify(lifeCycleStrategyMock).releaseSession(CONFIG_ID);
	}

	@Test
	public void testOnRemovedDraft() throws InterceptorException
	{
		given(abstractOrderEntryModelMock.getProductConfiguration()).willReturn(null);
		given(abstractOrderEntryModelMock.getProductConfigurationDraft()).willReturn(productConfigModel);
		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verify(lifeCycleStrategyMock).releaseSession(CONFIG_ID);
	}


	@Test
	public void testOnRemoveNoBaseSite() throws InterceptorException
	{
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(null);
		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verifyZeroInteractions(lifeCycleStrategyMock);
	}

	@Test
	public void testOnRemoveNoConfigModel() throws InterceptorException
	{
		given(abstractOrderEntryModelMock.getProductConfiguration()).willReturn(null);
		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verifyZeroInteractions(lifeCycleStrategyMock);
	}

	@Test
	public void testOnRemoveFalsePersistence() throws InterceptorException
	{
		given(persitenceServiceMock.isOnlyRelatedToGivenEntry(CONFIG_ID, CART_ENTRY_KEY)).willReturn(false);
		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verifyZeroInteractions(lifeCycleStrategyMock);
	}

	@Test
	public void testOnRemoveWithProductLink() throws InterceptorException
	{
		final ProductModel product = new ProductModel();
		product.setCode("productCode");
		productConfigModel.setProduct(Collections.singletonList(product));

		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verifyZeroInteractions(lifeCycleStrategyMock);
	}


	@Test
	public void testOnRemoveWithProductLinkForDraft() throws InterceptorException
	{
		final ProductModel product = new ProductModel();
		product.setCode("productCode");
		productConfigModel.setProduct(Collections.singletonList(product));
		given(abstractOrderEntryModelMock.getProductConfiguration()).willReturn(null);
		given(abstractOrderEntryModelMock.getProductConfigurationDraft()).willReturn(productConfigModel);

		classUnderTest.onRemove(abstractOrderEntryModelMock, ctxtMock);
		verify(lifeCycleStrategyMock).releaseSession(CONFIG_ID);
	}

	@Test
	public void testCheckBaseSiteAvailableReturnFalse()
	{
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(null);
		assertFalse(classUnderTest.checkBaseSiteAvailable(CONFIG_ID));
	}

	@Test
	public void testCheckBaseSiteAvailableReturnTrue()
	{
		assertTrue(classUnderTest.checkBaseSiteAvailable(CONFIG_ID));
	}

	@Test
	public void testCheckBaseSiteAvailableReturnTrueFromCronJob()
	{
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(null);
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		given(productConfigEventListenerUtil.getBaseSiteFromCronJob()).willReturn(baseSiteModel);
		assertTrue(classUnderTest.checkBaseSiteAvailable(CONFIG_ID));
		verify(baseSiteServiceMock).setCurrentBaseSite(baseSiteModel, false);
	}
}
