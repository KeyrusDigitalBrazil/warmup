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
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiOrderClient;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOAuthService;
import de.hybris.platform.sap.sapcpiorderexchange.data.SapSendToSapCpiResult;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import rx.Single;

import static de.hybris.platform.sap.sapcpiorderexchange.constants.SapCpiResponseStatus.*;

/**
 * SapCpiOrderDefaultService
 */
public class SapCpiOrderDefaultService implements SapCpiOrderService
{

	private static final Logger LOG = Logger.getLogger(SapCpiOrderDefaultService.class);
	private SapCpiOrderClient sapCpiOrderClient;
	private SapCpiOAuthService sapOAuthService;

	@Override
	public Single<SapSendToSapCpiResult> sendOrder(final SapCpiOrder sapCpiOrder)
	{

		try
		{

			return getSapOAuthService().getToken().toSingle()
					.flatMap(accessToken -> getSapCpiOrderClient()
							.sendOrder(new StringBuilder().append("Bearer ").append(accessToken).toString(), sapCpiOrder).toSingle()
							.flatMap(sapCpiResponse -> {

								if (SCPI_SUCCESS.getStatus().equalsIgnoreCase(sapCpiResponse.getStatus()))
								{

									return getResponse(true, sapCpiResponse.getMessage());

								}
								else if (SCPI_ERROR.getStatus().equalsIgnoreCase(sapCpiResponse.getStatus()))
								{

									return getResponse(false, sapCpiResponse.getMessage());

								}
								else
								{

									String msg = String.format("Unable to send the order [%s] to SAP backend through SCPI!",
											sapCpiOrder.getOrderId());
									LOG.error(msg);
									return getResponse(false, msg);

								}
							}));

		}
		catch (final Exception ex)
		{

			LOG.error(ex);
			String msg = String.format("Unable to send the order [%s] to SAP backend through SCPI! %s", sapCpiOrder.getOrderId(),
					ex.getMessage());
			LOG.error(msg);
			return getResponse(false, msg);

		}


	}

	@Override
	public Single<SapSendToSapCpiResult> sendOrderCancellation(SapCpiOrderCancellation sapCpiOrderCancellation)
	{

		try
		{

			return getSapOAuthService().getToken().toSingle()
					.flatMap(accessToken -> getSapCpiOrderClient()
							.sendOrderCancellation(new StringBuilder().append("Bearer ").append(accessToken).toString(),
									sapCpiOrderCancellation)
							.toSingle().flatMap(sapCpiResponse -> {

								if (SCPI_SUCCESS.getStatus().equalsIgnoreCase(sapCpiResponse.getStatus()))
								{

									return getResponse(true, sapCpiResponse.getMessage());

								}
								else if (SCPI_ERROR.getStatus().equalsIgnoreCase(sapCpiResponse.getStatus()))
								{

									return getResponse(false, sapCpiResponse.getMessage());

								}
								else
								{

									String msg = String.format("Unable to send the order [%s] cancellation to SAP backend through SCPI!",
											sapCpiOrderCancellation.getOrderId());
									LOG.error(msg);
									return getResponse(false, msg);

								}

							}

			));

		}
		catch (final Exception ex)
		{

			LOG.error(ex);
			String msg = String.format("Unable to send the order [%s] cancellation to SAP backend through SCPI! %s",
					sapCpiOrderCancellation.getOrderId(), ex.getMessage());
			LOG.error(msg);
			return getResponse(false, msg);

		}

	}

	protected Single<SapSendToSapCpiResult> getResponse(boolean successful, String message)
	{

		SapSendToSapCpiResult sapSendToSapCpiResult = new SapSendToSapCpiResult();
		sapSendToSapCpiResult.setSuccessful(successful);
		sapSendToSapCpiResult.setMessage(message);

		return Single.just(sapSendToSapCpiResult);
	}

	protected SapCpiOAuthService getSapOAuthService()
	{
		return sapOAuthService;
	}

	@Required
	public void setSapOAuthService(final SapCpiOAuthService sapOAuthService)
	{
		this.sapOAuthService = sapOAuthService;
	}


	protected SapCpiOrderClient getSapCpiOrderClient()
	{
		return sapCpiOrderClient;
	}

	@Required
	public void setSapCpiOrderClient(final SapCpiOrderClient sapCpiOrderClient)
	{
		this.sapCpiOrderClient = sapCpiOrderClient;
	}

}
