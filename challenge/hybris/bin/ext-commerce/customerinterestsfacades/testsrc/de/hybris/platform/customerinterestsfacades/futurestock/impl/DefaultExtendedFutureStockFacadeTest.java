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
package de.hybris.platform.customerinterestsfacades.futurestock.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.futurestock.FutureStockService;
import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;


@UnitTest
public class DefaultExtendedFutureStockFacadeTest
{
	@Mock
	private FutureStockService futureStockService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	private DefaultExtendedFutureStockFacade extendedFutureStockFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final Map<String, Map<Date, Integer>> productsMap = Maps.newHashMap();
		final Map<Date, Integer> map = Maps.newHashMap();
		map.put(new Date(), 150);
		productsMap.put("123456", map);
		Mockito.when(futureStockService.getFutureAvailability(Mockito.anyList())).thenReturn(productsMap);

		extendedFutureStockFacade = Mockito.spy(new DefaultExtendedFutureStockFacade());
		LanguageModel language = new LanguageModel();
		language.setIsocode("CN");
		when(commerceCommonI18NService.getDefaultLanguage()).thenReturn(language);
		extendedFutureStockFacade.setCommerceCommonI18NService(commerceCommonI18NService);
		extendedFutureStockFacade.setFutureStockService(futureStockService);
	}

	@Test
	public void getFutureAvailability()
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode("123456");
		final List<FutureStockData> futureStockDataList = extendedFutureStockFacade.getFutureAvailability(productModel);
		Assert.assertEquals(150, futureStockDataList.get(0).getStock().getStockLevel().intValue());
	}
}
