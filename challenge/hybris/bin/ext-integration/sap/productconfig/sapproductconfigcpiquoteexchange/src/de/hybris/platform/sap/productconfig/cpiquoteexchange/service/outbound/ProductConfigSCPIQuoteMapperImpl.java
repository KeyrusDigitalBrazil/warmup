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
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.outbound;

import de.hybris.platform.core.model.order.QuoteModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteMapperService;


public class ProductConfigSCPIQuoteMapperImpl implements SapCpiQuoteMapperService<QuoteModel, SAPCpiOutboundQuoteModel>
{

	static final String SAPPRODUCTCONFIG_PRICINGPROCEDURE_CPS = "Sapproductconfig_pricingprocedure_cps";
	private static final Logger LOG = Logger.getLogger(ProductConfigSCPIQuoteMapperImpl.class);

	@Override
	public void map(final QuoteModel source, final SAPCpiOutboundQuoteModel target)
	{
		if (containsAtLeastOneConfiguration(source))
		{
			final String pricingProcedure = source.getStore().getSAPConfiguration()
					.getProperty(SAPPRODUCTCONFIG_PRICINGPROCEDURE_CPS);
			if (!StringUtils.isEmpty(pricingProcedure))
			{
				target.setPricingProcedure(pricingProcedure);
			}
			else
			{
				LOG.warn(
						"Could not derive a pricing procedure for a quote containg configurable products. Please maintain a pricing procedure in backoffice: SAP Configuration -> SAP Basestore Configuration -> Product Configuration");
			}
		}
	}

	protected boolean containsAtLeastOneConfiguration(final QuoteModel quoteModel)
	{
		return quoteModel.getEntries().stream().anyMatch(entry -> null != entry.getProductConfiguration());
	}

}
