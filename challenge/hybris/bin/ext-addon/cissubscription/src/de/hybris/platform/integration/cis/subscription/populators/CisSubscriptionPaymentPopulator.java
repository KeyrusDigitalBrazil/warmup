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
package de.hybris.platform.integration.cis.subscription.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPaymentData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.collections.MapUtils;

import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.subscription.model.CisSubscriptionTransactionResult;
import org.springframework.http.ResponseEntity;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate the {@link SubscriptionPaymentData} with the {@link CisSubscriptionTransactionResult} data
 */

public class CisSubscriptionPaymentPopulator implements Populator<ResponseEntity, SubscriptionPaymentData>
{
	@Override
	public void populate(final ResponseEntity source, final SubscriptionPaymentData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);

		final Map<String, String> parameters = new HashMap<String, String>();
		if (source == null)
		{
			parameters.put("statusCode", String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
			parameters.put("status", Response.Status.INTERNAL_SERVER_ERROR.name() + ":RestResponse is empty");
			parameters.put("decision", String.valueOf(CisDecision.ERROR));
			target.setParameters(parameters);
			return;
		}

		parameters.put("status", (source.getStatusCode() == null) ? "" : source.getStatusCode().value() + ":"
				+ source.getStatusCode().name() + ":" + source.getStatusCode().getReasonPhrase());
		parameters.put("statusCode", String.valueOf(source.getStatusCode()));
		target.setPostUrl(convertLocationToUrl(source));

		if (source.getBody() instanceof CisSubscriptionTransactionResult)
		{
			final CisSubscriptionTransactionResult result = (CisSubscriptionTransactionResult) source.getBody();
			parameters.put("clientAuthorizationId", result.getClientAuthorizationId());
			parameters.put("clientRefId", result.getClientRefId());
			parameters.put("href", result.getHref());
			parameters.put("id", result.getId());
			parameters.put("merchantProductId", result.getMerchantProductId());
			parameters.put("sessionTransactionToken", result.getSessionTransactionToken());
			parameters.put("vendorId", result.getVendorId());
			parameters.put("vendorReasonCode", result.getVendorReasonCode());
			parameters.put("vendorStatusCode", result.getVendorStatusCode());
			parameters.put("amount", (result.getAmount() == null) ? "" : result.getAmount().toString());
			parameters.put("decision", (result.getDecision() == null) ? "" : result.getDecision().name());

			if (result.getVendorResponses() != null && MapUtils.isNotEmpty(result.getVendorResponses().getMap()))
			{
				parameters.putAll(result.getVendorResponses().getMap());
			}
		}

		target.setParameters(parameters);
	}

	private String convertLocationToUrl(final ResponseEntity source)
	{
		if (source.getHeaders().getLocation() == null)
		{
			return null;
		}

		try
		{
			final URL url = source.getHeaders().getLocation().toURL();
			return url.toString();
		}
		catch (final MalformedURLException e)
		{
			return null;
		}
	}

}
