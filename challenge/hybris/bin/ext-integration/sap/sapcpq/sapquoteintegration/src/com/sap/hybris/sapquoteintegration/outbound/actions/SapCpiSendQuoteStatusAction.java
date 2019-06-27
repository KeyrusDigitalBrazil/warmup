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
package com.sap.hybris.sapquoteintegration.outbound.actions;



import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.RESPONSE_MESSAGE;
import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.getPropertyValue;
import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.isSentSuccessfully;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteConversionService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 *
 */
public class SapCpiSendQuoteStatusAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SapCpiSendQuoteStatusAction.class);

	private QuoteService quoteService;
	private SapCpiOutboundQuoteConversionService quoteConversionService;
	private SapCpiOutboundQuoteService sapCpiOutboundQuoteService;

	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{
		Transition result = Transition.NOK;
		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			SAPCpiOutboundQuoteStatusModel scpiQuoteStatus = null;
			
			scpiQuoteStatus = getQuoteConversionService().convertQuoteToSapCpiQuoteStatus(quote);

			getSapCpiOutboundQuoteService().sendQuoteStatus(scpiQuoteStatus).subscribe(

					// onNext
					responseEntityMap -> {

						Registry.activateMasterTenant();
						final String reponseMessage = getPropertyValue(responseEntityMap, RESPONSE_MESSAGE);
						if (isSentSuccessfully(responseEntityMap))
						{
							setQuoteStatus(quote, ExportStatus.EXPORTED);
							LOG.info(String.format("The quote status for [%s] has been successfully sent to the backend through SCPI! %n%s",
									quote.getCode(), reponseMessage));

						}
						else
						{
							setQuoteStatus(quote, ExportStatus.NOTEXPORTED);
							LOG.error(String.format("The quote status for [%s] has not been sent to the backend! %n%s", quote.getCode(),
									reponseMessage));

						}
						resetEndMessage(process, reponseMessage);
					}
					// onError
					, error -> {

						Registry.activateMasterTenant();

						setQuoteStatus(quote, ExportStatus.NOTEXPORTED);
						LOG.error(String.format("The quote status for [%s] has not been sent to the backend through SCPI! %n%s", quote.getCode(),
								error.getMessage()));
						resetEndMessage(process, error.getMessage());

					});

			if (quote.getExportStatus().equals(ExportStatus.EXPORTED))
			{
				result = Transition.OK;
			}
		}
		return result;
	}

	protected void resetEndMessage(final QuoteProcessModel process, final String responseMessage)
	{
		process.setEndMessage(responseMessage);
		modelService.save(process);
	}


	protected void setQuoteStatus(final QuoteModel quote, final ExportStatus exportStatus)
	{
		quote.setExportStatus(exportStatus);
		modelService.save(quote);
	}

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	public SapCpiOutboundQuoteConversionService getQuoteConversionService()
	{
		return quoteConversionService;
	}

	@Required
	public void setQuoteConversionService(final SapCpiOutboundQuoteConversionService quoteConversionService)
	{
		this.quoteConversionService = quoteConversionService;
	}

	public SapCpiOutboundQuoteService getSapCpiOutboundQuoteService()
	{
		return sapCpiOutboundQuoteService;
	}

	@Required
	public void setSapCpiOutboundQuoteService(final SapCpiOutboundQuoteService sapCpiOutboundQuoteService)
	{
		this.sapCpiOutboundQuoteService = sapCpiOutboundQuoteService;
	}

}
