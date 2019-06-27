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
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductPromotionsPopulator}
 */
@UnitTest
public class ProductPromotionsPopulatorTest
{
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	@Mock
	private TimeService timeService;
	@Mock
	private ModelService modelService;
	@Mock
	private BaseSiteService baseSiteService;

	private ProductPromotionsPopulator productPromotionsPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productPromotionsPopulator = new ProductPromotionsPopulator();
		productPromotionsPopulator.setModelService(modelService);
		productPromotionsPopulator.setTimeService(timeService);
		productPromotionsPopulator.setPromotionsConverter(promotionsConverter);
		productPromotionsPopulator.setPromotionsService(promotionsService);
		productPromotionsPopulator.setBaseSiteService(baseSiteService);
	}

	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final PromotionGroupModel defaultPromotionGroup = mock(PromotionGroupModel.class);
		final Date currentDate = DateUtils.round(new Date(), Calendar.MINUTE);
		final AbstractPromotionModel abstractPromotionModel = mock(AbstractPromotionModel.class);
		final PromotionData promotionData = mock(PromotionData.class);
		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		given(timeService.getCurrentTime()).willReturn(currentDate);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		given(baseSiteModel.getDefaultPromotionGroup()).willReturn(defaultPromotionGroup);
		Mockito
				.<List<? extends AbstractPromotionModel>> when(promotionsService
						.getAbstractProductPromotions(Collections.singletonList(defaultPromotionGroup), source, true, currentDate))
				.thenReturn(Collections.singletonList(abstractPromotionModel));
		given(promotionsConverter.convertAll(Collections.singletonList(abstractPromotionModel)))
				.willReturn(Collections.singletonList(promotionData));

		final ProductData result = new ProductData();
		productPromotionsPopulator.populate(source, result);

		Assert.assertEquals(1, result.getPotentialPromotions().size());
		Assert.assertEquals(promotionData, result.getPotentialPromotions().iterator().next());
	}
}
