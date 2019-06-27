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
package de.hybris.platform.sapdigitalpaymentaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentDeleteCardException;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.core.GenericSearchConstants.LOG;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/my-account")
public class SapDigitalPaymentAccountPaymentDetailsPageController extends AbstractSearchPageController
{

	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentAccountPaymentDetailsPageController.class);

	private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/payment-details";


	@Resource(name = "sapDigitalPaymentUserFacade")
	private UserFacade sapDigitalPaymentUserFacade;



	@RequestMapping(value = "/remove-sap-digital-payment-method", method = RequestMethod.POST)
	@RequireHardLogIn
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		try
		{
			getSapDigitalPaymentUserFacade().unlinkCCPaymentInfo(paymentMethodId);
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.profile.paymentCart.removed");
		}
		catch (final SapDigitalPaymentDeleteCardException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Card deletion failed." + e);
			}
			LOG.error("Card deletion failed." + e.getMessage());
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.profile.paymentCart.removed.error");
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Error while fetching delete card response from SAP Digital payments." + e);
			}
			LOG.error("Error while fetching delete card response from SAP Digital payments." + e.getMessage());
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.profile.paymentCart.removed.error");
		}
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}



	/**
	 * @return the sapDigitalPaymentUserFacade
	 */
	public UserFacade getSapDigitalPaymentUserFacade()
	{
		return sapDigitalPaymentUserFacade;
	}



	/**
	 * @param sapDigitalPaymentUserFacade
	 *           the sapDigitalPaymentUserFacade to set
	 */
	public void setSapDigitalPaymentUserFacade(UserFacade sapDigitalPaymentUserFacade)
	{
		this.sapDigitalPaymentUserFacade = sapDigitalPaymentUserFacade;
	}



}
