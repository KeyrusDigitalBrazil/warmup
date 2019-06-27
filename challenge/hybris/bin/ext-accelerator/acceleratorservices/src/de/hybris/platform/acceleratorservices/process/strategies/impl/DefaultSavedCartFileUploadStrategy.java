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


import de.hybris.platform.acceleratorservices.cartfileupload.data.SavedCartFileUploadReportData;
import de.hybris.platform.acceleratorservices.process.strategies.SavedCartFileUploadStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultSavedCartFileUploadStrategy implements SavedCartFileUploadStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSavedCartFileUploadStrategy.class);
	private ProductService productService;
	private MediaService mediaService;
	private ModelService modelService;
	private CommerceCartService commerceCartService;
	private Long numberOfLinesToSkip;
	private Integer productCodeIndex;
	private Integer qtyIndex;
	private String delimiter;

	@Override
	public SavedCartFileUploadReportData createSavedCartFromFile(final MediaModel mediaModel, final CartModel cartModel)
			throws IOException
	{
		final List<CommerceCartModification> errorModifications = new LinkedList<>();
		final SavedCartFileUploadReportData savedCartFileUploadReportData = new SavedCartFileUploadReportData();
		final AtomicInteger successCounter = new AtomicInteger(0);
		final AtomicInteger partialImportCounter = new AtomicInteger(0);
		final AtomicInteger failureCounter = new AtomicInteger(0);
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				mediaService.getStreamFromMedia(mediaModel)));
				final Stream<String> lines = bufferedReader.lines())
		{
			lines.skip(getNumberOfLinesToSkip().longValue()).filter(line -> StringUtils.isNotBlank(line)).forEach(line -> {
				final String[] cartAttributes = line.split(delimiter);
				if (cartAttributes.length >= 2)
				{
					try
					{
						final CommerceCartModification commerceCartModification = addLinesToCart(cartAttributes, cartModel);
						if (!CommerceCartModificationStatus.SUCCESS.equals(commerceCartModification.getStatusCode()))
						{
							errorModifications.add(commerceCartModification);
							if (commerceCartModification.getQuantityAdded() > 0)
							{
								partialImportCounter.incrementAndGet();
							}
							else
							{
								failureCounter.incrementAndGet();
							}
						}
						else
						{
							successCounter.incrementAndGet();
						}
					}
					catch (final CommerceCartModificationException | NumberFormatException | UnknownIdentifierException e)
					{
						writeDebugLog("Import of line for cart:" + cartModel.getCode() + " failed due to" + e.getMessage());
						errorModifications.add(handleExceptionForImport(e));
						failureCounter.incrementAndGet();
					}
				}
			});
			savedCartFileUploadReportData.setErrorModificationList(errorModifications);
			savedCartFileUploadReportData.setSuccessCount(Integer.valueOf(successCounter.get()));
			savedCartFileUploadReportData.setFailureCount(Integer.valueOf(failureCounter.get()));
			savedCartFileUploadReportData.setPartialImportCount(Integer.valueOf(partialImportCounter.get()));
		}
		return savedCartFileUploadReportData;
	}

	protected CommerceCartModification addLinesToCart(final String[] cartAttributes, final CartModel cartModel)
			throws CommerceCartModificationException
	{
		final String productCode = StringUtils.trim(cartAttributes[getProductCodeIndex().intValue() - 1]);
		final Long qty = Long.valueOf(StringUtils.trim(cartAttributes[getQtyIndex().intValue() - 1]));
		final CommerceCartParameter commerceCartParameter = createCommerceCartParam(productCode, qty.longValue(), cartModel);
		return getCommerceCartService().addToCart(commerceCartParameter);
	}


	protected CommerceCartParameter createCommerceCartParam(final String code, final long quantity, final CartModel cartModel)
			throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final CommerceCartParameter parameter = createCommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setQuantity(quantity);
		parameter.setProduct(product);
		parameter.setCreateNewEntry(false);
		return parameter;
	}

	protected CommerceCartModification handleExceptionForImport(final Exception ex)
	{
		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		commerceCartModification.setStatusCode(ex.getMessage());
		commerceCartModification.setQuantityAdded(0);

		return commerceCartModification;
	}

	protected void writeDebugLog(final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message);
		}
	}

	protected CommerceCartParameter createCommerceCartParameter()
	{
		return new CommerceCartParameter();
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected String getDelimiter()
	{
		return delimiter;
	}

	@Required
	public void setDelimiter(final String delimiter)
	{
		this.delimiter = delimiter;
	}

	protected Integer getProductCodeIndex()
	{
		return productCodeIndex;
	}

	@Required
	public void setProductCodeIndex(final Integer productCodeIndex)
	{
		this.productCodeIndex = productCodeIndex;
	}

	protected Long getNumberOfLinesToSkip()
	{
		return numberOfLinesToSkip;
	}

	@Required
	public void setNumberOfLinesToSkip(final Long numberOfLinesToSkip)
	{
		this.numberOfLinesToSkip = numberOfLinesToSkip;
	}

	protected Integer getQtyIndex()
	{
		return qtyIndex;
	}

	@Required
	public void setQtyIndex(final Integer qtyIndex)
	{
		this.qtyIndex = qtyIndex;
	}
}
