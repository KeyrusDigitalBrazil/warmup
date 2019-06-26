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
package de.hybris.platform.acceleratorservices.process.strategies.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.cartfileupload.data.SavedCartFileUploadReportData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;


@UnitTest
public class DefaultSavedCartFileUploadStrategyTest
{
	private DefaultSavedCartFileUploadStrategy spyObj;

	@Mock
	private ProductService productService;

	@Mock
	private MediaService mediaService;

	@Mock
	private ModelService modelService;

	@Mock
	private DefaultCommerceCartService commerceCartService;

	@Mock
	private MediaModel mediaModel;

	@Mock
	private MediaModel badMediaModel;

	@Mock
	private MediaModel productNotFoundMediaModel;

	private CartModel cartModel;

	@Before
	public void setUp() throws Exception
	{
		final ClassPathResource resource = new ClassPathResource("/acceleratorservices/test/testCSVFileUploadSavedCart.csv");
		final ClassPathResource badResource = new ClassPathResource("/acceleratorservices/test/testBadCSVFileUploadSavedCart.csv");
		final ClassPathResource productNotFoundResource = new ClassPathResource(
				"/acceleratorservices/test/testCSVFileUploadForProductNotFound.csv");

		final DefaultSavedCartFileUploadStrategy defaultSavedCartFileUploadStrategy = new DefaultSavedCartFileUploadStrategy();
		MockitoAnnotations.initMocks(this);
		defaultSavedCartFileUploadStrategy.setCommerceCartService(commerceCartService);
		defaultSavedCartFileUploadStrategy.setModelService(modelService);
		defaultSavedCartFileUploadStrategy.setMediaService(mediaService);
		defaultSavedCartFileUploadStrategy.setProductService(productService);

		given(mediaService.getStreamFromMedia(mediaModel)).willReturn(resource.getInputStream());
		given(mediaService.getStreamFromMedia(badMediaModel)).willReturn(badResource.getInputStream());
		given(mediaService.getStreamFromMedia(productNotFoundMediaModel)).willReturn(productNotFoundResource.getInputStream());
		spyObj = Mockito.spy(defaultSavedCartFileUploadStrategy);
		spyObj.setQtyIndex(Integer.valueOf(2));
		spyObj.setProductCodeIndex(Integer.valueOf(1));
		spyObj.setDelimiter(",");
		spyObj.setNumberOfLinesToSkip(Long.valueOf(1L));
	}

	@Test
	public void testCreateSavedCartFromFileForSuccessResponse() throws CommerceCartModificationException, IOException
	{
		final SavedCartFileUploadReportData savedCartFileUploadReportData = spyObj.createSavedCartFromFile(mediaModel,
				createCartModelMock(true));
		assertEquals(0, savedCartFileUploadReportData.getErrorModificationList().size());
		assertEquals(0, savedCartFileUploadReportData.getFailureCount().intValue());
		assertEquals(5, savedCartFileUploadReportData.getSuccessCount().intValue());
		assertEquals(0, savedCartFileUploadReportData.getPartialImportCount().intValue());
	}

	@Test
	public void testCreateSavedCartFromFileForFailureResponse() throws CommerceCartModificationException, IOException
	{
		final SavedCartFileUploadReportData savedCartFileUploadReportData = spyObj.createSavedCartFromFile(mediaModel,
				createCartModelMock(false));
		assertEquals(5, savedCartFileUploadReportData.getErrorModificationList().size());
		assertEquals(4, savedCartFileUploadReportData.getFailureCount().intValue());
		assertEquals(0, savedCartFileUploadReportData.getSuccessCount().intValue());
		assertEquals(1, savedCartFileUploadReportData.getPartialImportCount().intValue());
	}

	@Test
	public void testCreateSavedCartFromFileForNumberFormatIssues() throws CommerceCartModificationException, IOException
	{
		final SavedCartFileUploadReportData savedCartFileUploadReportData = spyObj.createSavedCartFromFile(badMediaModel,
				createCartModelMock(true));
		assertEquals(1, savedCartFileUploadReportData.getErrorModificationList().size());
		assertEquals(1, savedCartFileUploadReportData.getFailureCount().intValue());
		assertEquals(1, savedCartFileUploadReportData.getSuccessCount().intValue());
		assertEquals(0, savedCartFileUploadReportData.getPartialImportCount().intValue());
	}

	@Test
	public void testCreateSavedCartFromFileForUnknownIdentifierIssues() throws CommerceCartModificationException, IOException
	{
		//Tests product not found, illegal line, empty lines, ordering of the error list, line with commas with blank values
		final CartModel cartModel = createCartModelMock(true);
		when(spyObj.createCommerceCartParam("productNotFound1", 3L, cartModel))
				.thenThrow(new UnknownIdentifierException("productNotFound1 cannot be found"));
		when(spyObj.createCommerceCartParam("productNotFound2", 4L, cartModel))
				.thenThrow(new UnknownIdentifierException("productNotFound2 cannot be found"));
		when(spyObj.createCommerceCartParam("productNotFound3", 5L, cartModel))
				.thenThrow(new UnknownIdentifierException("productNotFound3 cannot be found"));
		final SavedCartFileUploadReportData savedCartFileUploadReportData = spyObj
				.createSavedCartFromFile(productNotFoundMediaModel, cartModel);
		assertEquals(4, savedCartFileUploadReportData.getErrorModificationList().size());
		assertEquals("productNotFound1 cannot be found",
				savedCartFileUploadReportData.getErrorModificationList().get(0).getStatusCode());
		assertEquals("productNotFound2 cannot be found",
				savedCartFileUploadReportData.getErrorModificationList().get(1).getStatusCode());
		assertEquals("productNotFound3 cannot be found",
				savedCartFileUploadReportData.getErrorModificationList().get(2).getStatusCode());
		assertEquals(4, savedCartFileUploadReportData.getFailureCount().intValue());
		assertEquals(2, savedCartFileUploadReportData.getSuccessCount().intValue());
		assertEquals(0, savedCartFileUploadReportData.getPartialImportCount().intValue());
	}

	protected CartModel createCartModelMock(final boolean successCartModification) throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter1 = Mockito.mock(CommerceCartParameter.class);
		final CommerceCartParameter commerceCartParameter2 = Mockito.mock(CommerceCartParameter.class);

		final CommerceCartParameter commerceCartParameter3 = Mockito.mock(CommerceCartParameter.class);
		final CommerceCartParameter commerceCartParameter4 = Mockito.mock(CommerceCartParameter.class);

		final ProductModel product1 = Mockito.mock(ProductModel.class);
		final ProductModel product2 = Mockito.mock(ProductModel.class);

		given(productService.getProductForCode("00123")).willReturn(product1);
		given(productService.getProductForCode("00124")).willReturn(product2);
		given(productService.getProductForCode("00125")).willReturn(product1);
		given(productService.getProductForCode("XC60S90")).willReturn(product2);

		given(commerceCartParameter1.getProduct()).willReturn(product1);
		given(commerceCartParameter2.getProduct()).willReturn(product2);
		given(commerceCartParameter3.getProduct()).willReturn(product1);
		given(commerceCartParameter4.getProduct()).willReturn(product2);


		final CommerceCartModification commerceCartModification1 = Mockito.mock(CommerceCartModification.class);
		final CommerceCartModification commerceCartModification2 = Mockito.mock(CommerceCartModification.class);
		final CommerceCartModification commerceCartModification3 = Mockito.mock(CommerceCartModification.class);
		final CommerceCartModification commerceCartModification4 = Mockito.mock(CommerceCartModification.class);

		if (successCartModification)
		{
			given(commerceCartModification1.getStatusCode()).willReturn(CommerceCartModificationStatus.SUCCESS);
			given(commerceCartModification2.getStatusCode()).willReturn(CommerceCartModificationStatus.SUCCESS);
			given(commerceCartModification3.getStatusCode()).willReturn(CommerceCartModificationStatus.SUCCESS);
			given(commerceCartModification4.getStatusCode()).willReturn(CommerceCartModificationStatus.SUCCESS);
		}
		else
		{
			given(Long.valueOf(commerceCartModification1.getQuantityAdded())).willReturn(Long.valueOf(1L));
			given(Long.valueOf(commerceCartModification2.getQuantityAdded())).willReturn(Long.valueOf(0L));
			given(Long.valueOf(commerceCartModification3.getQuantityAdded())).willReturn(Long.valueOf(0L));
			given(Long.valueOf(commerceCartModification4.getQuantityAdded())).willReturn(Long.valueOf(0L));
			given(commerceCartModification1.getStatusCode()).willReturn(CommerceCartModificationStatus.LOW_STOCK);
			given(commerceCartModification2.getStatusCode()).willReturn(CommerceCartModificationStatus.NO_STOCK);
			given(commerceCartModification3.getStatusCode()).willReturn(CommerceCartModificationStatus.NO_STOCK);
			given(commerceCartModification4.getStatusCode()).willReturn(CommerceCartModificationStatus.NO_STOCK);

		}

		given(commerceCartService.addToCart(commerceCartParameter1)).willReturn(commerceCartModification1);
		given(commerceCartService.addToCart(commerceCartParameter2)).willReturn(commerceCartModification2);
		given(commerceCartService.addToCart(commerceCartParameter3)).willReturn(commerceCartModification3);
		given(commerceCartService.addToCart(commerceCartParameter4)).willReturn(commerceCartModification4);

		cartModel = Mockito.mock(CartModel.class);

		when(spyObj.createCommerceCartParam("00123", 2L, cartModel)).thenReturn(commerceCartParameter1);
		when(spyObj.createCommerceCartParam("00124", 3L, cartModel)).thenReturn(commerceCartParameter2);
		when(spyObj.createCommerceCartParam("XC60S90", 8L, cartModel)).thenReturn(commerceCartParameter3);
		when(spyObj.createCommerceCartParam("XC60S90", 9L, cartModel)).thenReturn(commerceCartParameter3);
		when(spyObj.createCommerceCartParam("00125", 3L, cartModel)).thenReturn(commerceCartParameter4);

		return cartModel;
	}

}
