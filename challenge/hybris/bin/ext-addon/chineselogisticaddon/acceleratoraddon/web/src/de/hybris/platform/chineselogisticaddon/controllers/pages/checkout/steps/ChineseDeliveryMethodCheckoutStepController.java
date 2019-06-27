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
package de.hybris.platform.chineselogisticaddon.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.chineselogisticaddon.constants.ControllerConstants;
import de.hybris.platform.chineselogisticaddon.controllers.imported.DeliveryMethodCheckoutStepController;
import de.hybris.platform.chineselogisticfacades.delivery.DeliveryTimeSlotFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for the DeliveryMethodCheckoutStep of the checkout steps
 */
@Scope("tenant")
@RequestMapping(value = "/checkout/multi/delivery-method")
public class ChineseDeliveryMethodCheckoutStepController extends DeliveryMethodCheckoutStepController
{
	private static final String DELIVERY_METHOD = "delivery-method";

	@Resource(name = "deliveryTimeSlotFacade")
	private DeliveryTimeSlotFacade deliveryTimeSlotFacade;

	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		super.enterStep(model, redirectAttributes);
		model.addAttribute("deliveryTimeSlots", deliveryTimeSlotFacade.getAllDeliveryTimeSlots());
		return ControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
	}

	/**
	 * This method gets called when the "Use Selected Delivery Method" button is clicked. It sets the selected delivery
	 * mode on the checkout facade and reloads the page highlighting the selected delivery Mode.
	 *
	 * @param selectedDeliveryMethod
	 *           - the id of the delivery mode.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectDeliveryMode(@RequestParam("delivery_method") final String selectedDeliveryMethod,
			@RequestParam("deliveryTimeSlot") final String deliveryTimeSlot)
	{
		if (StringUtils.isNotEmpty(selectedDeliveryMethod))
		{
			getCheckoutFacade().setDeliveryMode(selectedDeliveryMethod);
		}
		if (StringUtils.isNotEmpty(deliveryTimeSlot))
		{
			getCheckoutFacade().setDeliveryTimeSlot(deliveryTimeSlot);
		}
		return getCheckoutStep().nextStep();
	}

	@Override
	protected DeliveryTimeSlotFacade getCheckoutFacade()
	{
		return deliveryTimeSlotFacade;
	}

}
