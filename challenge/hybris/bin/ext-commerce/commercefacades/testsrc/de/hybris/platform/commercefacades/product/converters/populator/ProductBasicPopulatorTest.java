/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.ProductConfigurableChecker;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductBasicPopulator}
 */
@UnitTest
public class ProductBasicPopulatorTest
{
	private static final String PRODUCT_NAME = "proName";
	private static final String PRODUCT_MANUFACTURER = "proMan";
	private static final Double PRODUCT_AVG_RATING = Double.valueOf(3.45D);
	private static final String VARIANT_TYPE_CODE = "varCode";
	private static final String BASE_PRODUCT_CODE = "baseProduct";
	private static final String CONFIGURABLE_PRODUCT_CODE = "configurableProduct";
	private static final String CONFIGURATOR_TYPE = "MY_CONFIGURATOR";

	@Mock
	private ModelService modelService;
	@Mock
	private ProductConfigurableChecker productConfigurableChecker;

	private ProductBasicPopulator<ProductModel, ProductData> productBasicPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(Boolean.valueOf(productConfigurableChecker.isProductConfigurable(any(ProductModel.class))))
				.thenAnswer(invocationOnMock -> {
					final ProductModel product = (ProductModel) invocationOnMock.getArguments()[0];
					return Boolean.valueOf(CONFIGURABLE_PRODUCT_CODE.equals(product.getCode()));
				});

		Mockito.when(productConfigurableChecker.getFirstConfiguratorType(any(ProductModel.class))).thenReturn(CONFIGURATOR_TYPE);
		productBasicPopulator = new ProductBasicPopulator<>();
		productBasicPopulator.setModelService(modelService);
		productBasicPopulator.setProductConfigurableChecker(productConfigurableChecker);
	}

	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);

		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(modelService.getAttributeValue(source, ProductModel.MANUFACTURERNAME)).willReturn(PRODUCT_MANUFACTURER);
		given(source.getAverageRating()).willReturn(PRODUCT_AVG_RATING);
		given(source.getVariantType()).willReturn(variantTypeModel);
		given(variantTypeModel.getCode()).willReturn(VARIANT_TYPE_CODE);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(PRODUCT_MANUFACTURER, result.getManufacturer());
		Assert.assertEquals(PRODUCT_AVG_RATING, result.getAverageRating());
		Assert.assertEquals(VARIANT_TYPE_CODE, result.getVariantType());
	}

	@Test
	public void testPopulateNotVariantTyped()
	{
		final ProductModel source = mock(ProductModel.class);

		given(source.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(modelService.getAttributeValue(source, ProductModel.MANUFACTURERNAME)).willReturn(PRODUCT_MANUFACTURER);
		given(source.getVariantType()).willReturn(null);
		given(source.getAverageRating()).willReturn(PRODUCT_AVG_RATING);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(PRODUCT_MANUFACTURER, result.getManufacturer());
		Assert.assertEquals(PRODUCT_AVG_RATING, result.getAverageRating());
	}


	@Test
	public void testPopulateAttributeFallback()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(source.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(modelService.getAttributeValue(source, ProductModel.NAME)).willReturn(null);
		given(modelService.getAttributeValue(baseProduct, ProductModel.NAME)).willReturn(PRODUCT_NAME);
		given(baseProduct.getCode()).willReturn(CONFIGURABLE_PRODUCT_CODE);

		final ProductData result = new ProductData();
		productBasicPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_NAME, result.getName());
		Assert.assertEquals(CONFIGURABLE_PRODUCT_CODE, result.getBaseProduct());
	}

	@Test
	public void testConfigurableFieldDefaultValue()
	{
		final ProductModel productModel = mock(ProductModel.class);
		given(productModel.getCode()).willReturn(BASE_PRODUCT_CODE);
		given(modelService.getAttributeValue(productModel, ProductModel.NAME)).willReturn("test");
		given(modelService.getAttributeValue(productModel, ProductModel.MANUFACTURERNAME)).willReturn("manufacturer");
		given(productModel.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);

		final ProductData productData = new ProductData();
		productBasicPopulator.populate(productModel, productData);

		Assert.assertFalse(productData.getConfigurable());
	}

	@Test
	public void testConfigurableFieldWithConfiguration()
	{
		final ProductModel productModel = mock(ProductModel.class);
		given(productModel.getCode()).willReturn(CONFIGURABLE_PRODUCT_CODE);
		given(modelService.getAttributeValue(productModel, ProductModel.NAME)).willReturn("test");
		given(modelService.getAttributeValue(productModel, ProductModel.MANUFACTURERNAME)).willReturn("manufacturer");
		given(productModel.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);

		final ProductData productData = new ProductData();
		productBasicPopulator.populate(productModel, productData);

		Assert.assertTrue(productData.getConfigurable());
	}

	@Test
	public void testConfigurationType()
	{
		final ProductModel productModel = mock(ProductModel.class);
		given(productModel.getCode()).willReturn(CONFIGURABLE_PRODUCT_CODE);

		final ProductData productData = new ProductData();
		productBasicPopulator.populate(productModel, productData);

		Assert.assertEquals(CONFIGURATOR_TYPE, productData.getConfiguratorType());
	}

	@Test
	public void testConfigurationTypeNoSettings()
	{
		final ProductModel productModel = mock(ProductModel.class);
		given(productModel.getCode()).willReturn(CONFIGURABLE_PRODUCT_CODE);
		Mockito.when(productConfigurableChecker.getFirstConfiguratorType(any(ProductModel.class))).thenReturn(null);

		final ProductData productData = new ProductData();
		productBasicPopulator.populate(productModel, productData);

		Assert.assertNull(productData.getConfiguratorType());
	}
}
