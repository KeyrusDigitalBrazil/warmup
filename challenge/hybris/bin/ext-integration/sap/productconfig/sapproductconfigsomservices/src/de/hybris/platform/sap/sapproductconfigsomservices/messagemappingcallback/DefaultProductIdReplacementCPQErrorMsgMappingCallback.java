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
package de.hybris.platform.sap.sapproductconfigsomservices.messagemappingcallback;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;

import java.util.Locale;


/**
 * This callback is registered in messages.xml. It replaces the product ID (product code of the hybris product model)
 * with the language dependent product description.
 */
public class DefaultProductIdReplacementCPQErrorMsgMappingCallback implements MessageMappingCallbackProcessor
{

	/**
	 * Reference to the spring bean declaration
	 */
	public static final String SAP_PRODUCTID_REPLACEMENT_CALLBACK_ID = "sapProductIdReplacementForConfiguration";
	private ProductService productService;
	private Locale locale;


	@Override
	public boolean process(final BackendMessage message)
	{
		final String[] vars = message.getVars();

		final String productIdFromBackend = vars[1];

		final ProductModel productModel = productService.getProductForCode(productIdFromBackend);

		if (productModel != null)
		{
			final String productDescription = productModel.getName(getLocale());
			if (productDescription != null && !("").equals(productDescription))
			{
				vars[1] = productDescription;
			}
		}
		return true;

	}

	protected Locale getLocale()
	{
		if (this.locale == null)
		{
			return LocaleUtil.getLocale();
		}
		else
		{
			return this.locale;
		}
	}


	protected void setLocale(final Locale locale)
	{
		this.locale = locale;
	}

	@Override
	public String getId()
	{
		return SAP_PRODUCTID_REPLACEMENT_CALLBACK_ID;
	}

	/**
	 * Sets product service
	 *
	 * @param productService
	 *           Standard productService
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}



}
