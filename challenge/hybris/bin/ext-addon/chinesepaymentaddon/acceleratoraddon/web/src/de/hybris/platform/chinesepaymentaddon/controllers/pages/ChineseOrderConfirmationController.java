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
package de.hybris.platform.chinesepaymentaddon.controllers.pages;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;
import de.hybris.platform.chinesepaymentaddon.constants.ControllerConstants;
import de.hybris.platform.chinesepaymentaddon.controllers.imported.CheckoutController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Scope("tenant")
@RequestMapping(value = "/checkout")
public class ChineseOrderConfirmationController extends CheckoutController
{
	private static final String CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL = "orderConfirmation";
	private static final String CONTINUE_URL_KEY = "continueUrl";
	private static final Logger LOG = Logger.getLogger(ChineseOrderConfirmationController.class);


	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@Override
	protected String processOrderCode(final String orderCode, final Model model, final HttpServletRequest request, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		final OrderData orderDetails;

		try
		{
			orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load an order confirmation that does not exist or is not visible. Redirect to home page.");
			return REDIRECT_PREFIX + ROOT;
		}

		addRegistrationConsentDataToModel(model);

		if (orderDetails.isGuestCustomer()
				&& !StringUtils.substringBefore(orderDetails.getUser().getUid(), "|").equals(
						getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID)))
		{
			return getCheckoutRedirectUrl();
		}

		setOrderEntryData(orderDetails);

		model.addAttribute("orderCode", orderCode);
		model.addAttribute("orderData", orderDetails);
		model.addAttribute("allItems", orderDetails.getEntries());
		model.addAttribute("deliveryAddress", orderDetails.getDeliveryAddress());
		model.addAttribute("deliveryMode", orderDetails.getDeliveryMode());
		model.addAttribute("paymentInfo", orderDetails.getPaymentInfo());
		model.addAttribute("pageType", PageType.ORDERCONFIRMATION.name());

		final String uid;
		uid = setUidByOrderData(model, orderDetails);
		model.addAttribute("email", uid);

		final String continueUrl = getSessionService().getAttribute(WebConstants.CONTINUE_URL);
		model.addAttribute(CONTINUE_URL_KEY, (continueUrl != null && !continueUrl.isEmpty()) ? continueUrl : ROOT);

		final ContentPageModel checkoutOrderConfirmationPage = getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, checkoutOrderConfirmationPage);
		setUpMetaDataForContentPage(model, checkoutOrderConfirmationPage);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		if (ResponsiveUtils.isResponsive())
		{
			return getViewForPage(model);
		}

		return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPage;
	}

	protected String setUidByOrderData(final Model model, final OrderData orderDetails)
	{
		final String uid;
		if (orderDetails.isGuestCustomer() && !model.containsAttribute("guestRegisterForm"))
		{
			final GuestRegisterForm guestRegisterForm = new GuestRegisterForm();
			guestRegisterForm.setOrderCode(orderDetails.getGuid());
			uid = StringUtils.substringAfter(orderDetails.getUser().getUid(), "|");
			guestRegisterForm.setUid(uid);
			model.addAttribute(guestRegisterForm);
		}
		else
		{
			uid = orderDetails.getUser().getUid();
		}
		return uid;
	}

	protected void setOrderEntryData(final OrderData orderDetails)
	{
		if (orderDetails.getEntries() != null && !orderDetails.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : orderDetails.getEntries())
			{
				final String productCode = entry.getProduct().getCode();
				final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES));
				entry.setProduct(product);
			}
		}
	}
}
