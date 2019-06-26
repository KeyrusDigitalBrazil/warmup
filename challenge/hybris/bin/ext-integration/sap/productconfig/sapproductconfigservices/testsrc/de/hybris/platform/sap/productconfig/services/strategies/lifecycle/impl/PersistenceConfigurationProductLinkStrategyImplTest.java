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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceConfigurationProductLinkStrategyImplTest
{

	private static final String SESSION_ID = "session123";
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private static final String NEW_CONFIG_ID = "newConfigId";

	private PersistenceConfigurationProductLinkStrategyImpl classUnderTest;
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();
	private final ProductModel productModel = new ProductModel();
	private final UserModel currentUser = new UserModel();

	@Mock
	private ProductConfigurationPersistenceService persistenceService;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductService productService;

	@Mock
	private UserService userService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationProductLinkStrategyImpl();
		classUnderTest.setModelService(modelService);
		classUnderTest.setProductService(productService);
		classUnderTest.setPersistenceService(persistenceService);
		classUnderTest.setUserService(userService);

		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setProduct(Collections.singletonList(productModel));
		productConfigModel.setUser(currentUser);
		productModel.setCode(PRODUCT_CODE);

		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(productConfigModel);
		given(userService.getCurrentUser()).willReturn(currentUser);
	}

	@Test
	public void testSetConfigIdForProduct()
	{
		given(persistenceService.getByProductCode(PRODUCT_CODE)).willReturn(null);
		classUnderTest.setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
		verify(modelService).save(productConfigModel);
		assertTrue(productConfigModel.getProduct().contains(productModel));
		assertTrue(productConfigModel.getUser().equals(currentUser));
		assertEquals(1, productConfigModel.getProduct().size());
		assertEquals(CONFIG_ID, productConfigModel.getConfigurationId());
	}

	@Test
	public void testSetConfigIdForProductUpdatingLink()
	{
		final ProductConfigurationModel newProductConfigModel = new ProductConfigurationModel();
		newProductConfigModel.setConfigurationId(NEW_CONFIG_ID);
		given(persistenceService.getByConfigId(NEW_CONFIG_ID)).willReturn(newProductConfigModel);
		productConfigModel.setProduct(Collections.singletonList(productModel));

		classUnderTest.setConfigIdForProduct(PRODUCT_CODE, NEW_CONFIG_ID);

		verify(modelService).save(newProductConfigModel);
		assertTrue(newProductConfigModel.getProduct().contains(productModel));
		assertEquals(1, newProductConfigModel.getProduct().size());

		verify(modelService).save(productConfigModel);
		assertTrue(CollectionUtils.isEmpty(productConfigModel.getProduct()));
	}


	@Test
	public void testGetConfigIdForProduct()
	{
		final String configId = classUnderTest.getConfigIdForProduct(PRODUCT_CODE);
		assertEquals(CONFIG_ID, configId);
	}

	@Test
	public void testGetConfigIdForProductNull()
	{
		final String configId = classUnderTest.getConfigIdForProduct("bla");
		assertNull(configId);
	}


	@Test
	public void testRemoveConfigIdForProduct()
	{
		classUnderTest.removeConfigIdForProduct(PRODUCT_CODE);
		assertTrue(CollectionUtils.isEmpty(productConfigModel.getProduct()));
		verify(modelService).save(productConfigModel);
	}


	@Test
	public void testRemoveConfigIdForProductNull()
	{
		classUnderTest.removeConfigIdForProduct("bla");
		verifyZeroInteractions(modelService);
	}

	@Test
	public void testRetrieveProductCode()
	{
		final String result = classUnderTest.retrieveProductCode(CONFIG_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result);
	}

	@Test
	public void testRetrieveProductCodeNoProduct()
	{
		productConfigModel.setProduct(Collections.EMPTY_SET);
		assertNull(classUnderTest.retrieveProductCode(CONFIG_ID));
	}

	@Test
	public void testRetrieveProductCodeNullProduct()
	{
		productConfigModel.setProduct(null);
		assertNull(classUnderTest.retrieveProductCode(CONFIG_ID));
	}
}
