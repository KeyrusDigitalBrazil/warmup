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
package de.hybris.platform.customerinterestsfacades.productinterest.populators;


import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductInterestPopulatorTest
{
	private ProductInterestPopulator productInterestPopulator;

	@Mock
	private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;
	@Mock
	private ProductData productData;
	@Mock
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;
	@Mock
	private NotificationPreferenceFacade notificationPreferenceFacade;

	@Mock
	private NotificationPreferenceData preference;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productInterestPopulator = new ProductInterestPopulator();
		productInterestPopulator.setProductConverter(productConverter);
		productInterestPopulator.setProductPriceAndStockConverter(productPriceAndStockConverter);
		productInterestPopulator.setNotificationPreferenceFacade(notificationPreferenceFacade);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateProductInteresWithSourceNull()
	{
		final ProductInterestModel productInterestModel = null;
		final ProductInterestData productInterestData = new ProductInterestData();
		productInterestPopulator.populate(productInterestModel, productInterestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateProductInteresWithTargetNull()
	{
		final ProductInterestModel productInterestModel = new ProductInterestModel();
		final ProductInterestData productInterestData = null;
		productInterestPopulator.populate(productInterestModel, productInterestData);
	}

	@Test
	public void testPopulateProductInterest()
	{
		final Date date = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		final CustomerModel customer = new CustomerModel();
		customer.setUid("test@gmail.com");
		final ProductInterestModel source = new ProductInterestModel();
		source.setExpiryDate(date);
		source.setCustomer(customer);
		source.setNotificationType(NotificationType.NOTIFICATION);
		final ProductModel productModel = new ProductModel();
		source.setProduct(productModel);

		Mockito.when(productConverter.convert(Mockito.any())).thenReturn(productData);
		Mockito.when(productConverter.convert(Mockito.any())).thenReturn(productData);
		Mockito.when(productPriceAndStockConverter.convert(Mockito.any())).thenReturn(productData);
		Mockito.when(notificationPreferenceFacade.getValidNotificationPreferences(Mockito.any()))
				.thenReturn(Arrays.asList(preference));

		final ProductInterestData target = new ProductInterestData();

		productInterestPopulator.populate(source, target);

		assertEquals(date, target.getExpiryDate());
		assertEquals(NotificationType.NOTIFICATION, target.getNotificationType());
		assertEquals(productData, target.getProduct());
		assertEquals(preference, target.getNotificationChannels().get(0));
	}
}
