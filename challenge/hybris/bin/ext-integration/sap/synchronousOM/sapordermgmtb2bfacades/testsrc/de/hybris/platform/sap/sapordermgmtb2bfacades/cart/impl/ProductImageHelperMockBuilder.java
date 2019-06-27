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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;

import org.easymock.EasyMock;


/**
 * 
 */
public class ProductImageHelperMockBuilder
{

	ProductImageHelper productImageHelperMock = EasyMock.createNiceMock(ProductImageHelper.class);

	public static ProductImageHelperMockBuilder create()
	{
		return new ProductImageHelperMockBuilder();
	}

	public ProductImageHelperMockBuilder withEnrichWithProductImages(final AbstractOrderData orderEntryDataMock)
	{
		productImageHelperMock.enrichWithProductImages(orderEntryDataMock);
		return this;
	}

	public ProductImageHelperMockBuilder withEnrichWithProductImages(final OrderEntryData orderEntryDataMock)
	{
		productImageHelperMock.enrichWithProductImages(orderEntryDataMock);
		return this;
	}

	public ProductImageHelper build()
	{
		EasyMock.replay(productImageHelperMock);
		return productImageHelperMock;
	}


}
