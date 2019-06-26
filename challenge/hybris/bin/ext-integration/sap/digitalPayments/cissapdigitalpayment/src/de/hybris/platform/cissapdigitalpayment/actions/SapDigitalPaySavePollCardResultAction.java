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
package de.hybris.platform.cissapdigitalpayment.actions;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.model.SapDigitPayPollCardProcessModel;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Action class to convert the poll card result received from the SAP Digital payment to CCPaymentInfo object. Call the
 * servicess that create a Hybris subscription, create CreditCardPaymentInfoModel and save it to cart.
 */
public class SapDigitalPaySavePollCardResultAction extends AbstractSimpleDecisionAction<SapDigitPayPollCardProcessModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(SapDigitalPaySavePollCardResultAction.class);

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;
	private Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter;
	private SapDigitalPaymentService sapDigitalPaymentService;





	@Override
	public Transition executeAction(final SapDigitPayPollCardProcessModel pollCardProcess) throws RetryLaterException, Exception
	{

		final Map<String, Object> params = new HashMap<>();
		params.put(CisSapDigitalPaymentConstant.CART, pollCardProcess.getSessionCart());
		params.put(CisSapDigitalPaymentConstant.USER, pollCardProcess.getSessionUser());
		params.put(CisSapDigitalPaymentConstant.BASE_STORE, pollCardProcess.getBaseStore());

		try
		{
			final SAPDigitalPaymentConfigurationModel sapDigiPayConfig = pollCardProcess.getBaseStore()
					.getSapDigitalPaymentConfiguration();

			final CisSapDigitalPaymentPollRegisteredCardResult cisSapDigitalPaymentPollRegisteredCardResult = getCisSapDigitalPaymentService()
					.pollRegisteredCard(pollCardProcess.getSessionId(), sapDigiPayConfig).toBlocking().last();
			if (null != cisSapDigitalPaymentPollRegisteredCardResult)
			{
				final CCPaymentInfoData ccPaymentInfoData = getCisSapDigitalPaymentPaymentInfoConverter()
						.convert(cisSapDigitalPaymentPollRegisteredCardResult);
				ccPaymentInfoData.setSaved(true);



				final CreditCardPaymentInfoModel cardPaymentInfo = getSapDigitalPaymentService()
						.createPaymentSubscription(ccPaymentInfoData, params);
				getModelService().save(cardPaymentInfo);
				final boolean isCardDetailsSavedtoCart = getSapDigitalPaymentService()
						.saveCreditCardPaymentDetailsToCart(cardPaymentInfo.getPk().toString(), params);

				if (isCardDetailsSavedtoCart)
				{
					return Transition.OK;
				}
			}
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error in executing  Credit card polling process [%s]", e));
			}
			LOG.error(String.format("Error in executing  Credit card polling process [%s]", e.getMessage()));
		}
		return Transition.NOK;
	}



	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}

	/**
	 * @return the cisSapDigitalPaymentPaymentInfoConverter
	 */
	public Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> getCisSapDigitalPaymentPaymentInfoConverter()
	{
		return cisSapDigitalPaymentPaymentInfoConverter;
	}

	/**
	 * @param cisSapDigitalPaymentPaymentInfoConverter
	 *           the cisSapDigitalPaymentPaymentInfoConverter to set
	 */
	public void setCisSapDigitalPaymentPaymentInfoConverter(
			final Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter)
	{
		this.cisSapDigitalPaymentPaymentInfoConverter = cisSapDigitalPaymentPaymentInfoConverter;
	}

	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}

	/**
	 * @param sapDigitalPaymentService
	 *           the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}

}
