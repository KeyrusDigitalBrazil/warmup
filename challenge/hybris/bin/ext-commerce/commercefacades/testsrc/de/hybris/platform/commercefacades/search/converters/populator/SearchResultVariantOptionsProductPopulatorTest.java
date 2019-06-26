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
/**
 *
 */
package de.hybris.platform.commercefacades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.SolrFirstVariantCategoryManager;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SearchResultVariantOptionsProductPopulatorTest
{
	protected static final String BASE_SITE_UID = "siteUid";
	protected static final String BASE_SITE_ROLL_UP_PROPERTY = SearchResultVariantOptionsProductPopulator.VARIANT_ROLLUP_PROPERTY_CONFIG
			+ "." + BASE_SITE_UID;

	@Mock
	private VariantsService variantsService;
	@Mock
	private SolrFirstVariantCategoryManager categoryManager;
	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private UrlResolver<ProductData> productDataUrlResolver;
	@Mock
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	@Mock
	private Converter<ProductModel, StockData> stockConverter;
	@Mock
	private Converter<StockLevelStatus, StockData> stockLevelStatusConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ProductService productService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private BaseSiteService baseSiteService;

	private SearchResultVariantOptionsProductPopulator searchResultVariantOptionsProductPopulator;
	private SearchResultValueData searchResultValueData;
	private ProductData result;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		searchResultVariantOptionsProductPopulator = new SearchResultVariantOptionsProductPopulator();
		searchResultVariantOptionsProductPopulator.setVariantsService(variantsService);
		searchResultVariantOptionsProductPopulator.setImageFormatMapping(imageFormatMapping);
		searchResultVariantOptionsProductPopulator.setPriceDataFactory(priceDataFactory);
		searchResultVariantOptionsProductPopulator.setProductDataUrlResolver(productDataUrlResolver);
		searchResultVariantOptionsProductPopulator.setProductFeatureListPopulator(productFeatureListPopulator);
		searchResultVariantOptionsProductPopulator.setProductService(productService);
		searchResultVariantOptionsProductPopulator.setCommonI18NService(commonI18NService);
		searchResultVariantOptionsProductPopulator.setStockConverter(stockConverter);
		searchResultVariantOptionsProductPopulator.setStockLevelStatusConverter(stockLevelStatusConverter);
		searchResultVariantOptionsProductPopulator.setConfigurationService(configurationService);
		searchResultVariantOptionsProductPopulator.setBaseSiteService(baseSiteService);

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("thumbnail")).willReturn("96Wx96H");
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("product")).willReturn("300Wx300H");
		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(baseSite.getUid()).willReturn(BASE_SITE_UID);

		searchResultValueData = new SearchResultValueData();
		result = new ProductData();
	}

	@Test
	public void testPopulateEmpty()
	{
		// when
		searchResultVariantOptionsProductPopulator.populate(searchResultValueData, result);

		// then
		Assert.assertNull(result.getCode());
		Assert.assertNull(result.getVariantOptions());
	}

	@Test
	public void testPopulateWhenRollupPropertyIsSet()
	{
		// given
		populateData();
		given(variantsService.getVariantAttributes("variantModel")).willReturn(new HashSet<String>(Arrays.asList("size", "style")));
		given(configuration.getString(BASE_SITE_ROLL_UP_PROPERTY)).willReturn("size");

		// when
		searchResultVariantOptionsProductPopulator.populate(searchResultValueData, result);

		// then
		Assert.assertEquals("variant1", result.getCode());
		Assert.assertEquals(result.getVariantOptions().size(), 1);
		final VariantOptionData variantOptionData = result.getVariantOptions().get(0);
		Assert.assertEquals(variantOptionData.getCode(), "variant2");
		Assert.assertEquals(variantOptionData.getVariantOptionQualifiers().size(), 6);
		for (final VariantOptionQualifierData variantOptionQualifierData : variantOptionData.getVariantOptionQualifiers())
		{
			if (SearchResultVariantOptionsProductPopulator.ROLLUP_PROPERTY.equals(variantOptionQualifierData.getQualifier()))
			{
				Assert.assertEquals(variantOptionQualifierData.getValue(), "size");
			}
		}
	}

	@Test
	public void testPopulateWhenRollupPropertyIsNotSet()
	{
		// given
		populateData();
		given(variantsService.getVariantAttributes("variantModel")).willReturn(new HashSet<String>(Arrays.asList("size", "style")));

		// when
		searchResultVariantOptionsProductPopulator.populate(searchResultValueData, result);

		// then
		Assert.assertEquals("variant1", result.getCode());
		Assert.assertEquals(result.getVariantOptions().size(), 1);
		final VariantOptionData variantOptionData = result.getVariantOptions().get(0);
		Assert.assertEquals(variantOptionData.getCode(), "variant2");
		Assert.assertEquals(variantOptionData.getVariantOptionQualifiers().size(), 6);
		for (final VariantOptionQualifierData variantOptionQualifierData : variantOptionData.getVariantOptionQualifiers())
		{
			if (SearchResultVariantOptionsProductPopulator.ROLLUP_PROPERTY.equals(variantOptionQualifierData.getQualifier()))
			{
				Assert.assertEquals(variantOptionQualifierData.getValue(), "style");
			}
		}
	}

	@Test
	public void testPopulateWhenRollupPropertyIsSetWrongly()
	{
		// given
		populateData();
		given(variantsService.getVariantAttributes("variantModel")).willReturn(new HashSet<String>(Arrays.asList("size", "style")));
		given(configuration.getString(BASE_SITE_ROLL_UP_PROPERTY)).willReturn("st;le");

		// when
		searchResultVariantOptionsProductPopulator.populate(searchResultValueData, result);

		// then
		Assert.assertEquals("variant1", result.getCode());
		Assert.assertEquals(result.getVariantOptions().size(), 0);
	}

	private void populateData()
	{
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "variant1");
		map.put("name", "name variant1");
		map.put("style", "red");
		map.put("size", "M");
		map.put("img-96Wx96H", "thumbnail1");
		map.put("img-300Wx300H", "productImage1");
		map.put("itemtype", "variantModel");
		searchResultValueData.setValues(map);

		final SearchResultValueData variant2 = new SearchResultValueData();
		final Map<String, Object> variantMap = new HashMap<String, Object>();
		variantMap.put("code", "variant2");
		variantMap.put("name", "name variant2");
		variantMap.put("style", "red");
		variantMap.put("size", "L");
		variantMap.put("img-96Wx96H", "thumbnail2");
		variantMap.put("img-300Wx300H", "productImage2");
		variantMap.put("itemtype", "variantModel");
		variant2.setValues(variantMap);

		searchResultValueData.setVariants(Collections.singletonList(variant2));
	}
}