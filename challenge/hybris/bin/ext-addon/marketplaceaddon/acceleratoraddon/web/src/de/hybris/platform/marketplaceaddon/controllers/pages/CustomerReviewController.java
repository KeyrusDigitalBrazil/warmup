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
package de.hybris.platform.marketplaceaddon.controllers.pages;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.VendorReviewData;
import de.hybris.platform.consignmenttrackingfacades.ConsignmentTrackingFacade;
import de.hybris.platform.marketplaceaddon.controllers.MarketplaceaddonControllerConstants;
import de.hybris.platform.marketplaceaddon.forms.OrderReviewForm;
import de.hybris.platform.marketplacefacades.vendor.CustomerVendorReviewFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


@Controller
@RequestMapping("/my-account/order")
public class CustomerReviewController extends AbstractPageController
{

	private static final String ORDER_CODE_PATH_VARIABEL = "/{orderCode:.*}";
	private static final String CONSIGNMENT_CODE_PATH_VARIABEL = "/{consignmentCode:.*}";
	private static final String ORDER_DETAIL_PAGE = "/my-account/order/";
	private static final String ORDER_HISTORY_PAGE = "/my-account/orders";
	private static final String BREADCRUMB_ORDER_REVIEW_KEY = "text.order.review.breadcrumb";
	private static final String BREADCRUMB_ORDER_HISTORY_KEY = "text.account.orderHistory";
	private static final String BREADCRUMB_ORDER_DETAIL_KEY = "text.account.order.orderBreadcrumb";
	private static final String GLOBAL_ERROR_KEY = "order.review.general.error";
	private static final String REVIEW_HEADLINE = "Review";

	@Resource(name = "customerReviewValidator")
	private Validator customerReviewValidator;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@Resource(name = "customerVendorReviewFacade")
	private CustomerVendorReviewFacade customerVendorReviewFacade;

	@Resource(name = "consignmentTrackingFacade")
	private ConsignmentTrackingFacade consignmentTrackingFacade;

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	private static final String[] DISALLOWED_FIELDS = new String[] {};

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@RequireHardLogIn
	@RequestMapping(value = ORDER_CODE_PATH_VARIABEL + "/review" + CONSIGNMENT_CODE_PATH_VARIABEL, method = RequestMethod.GET)
	public String enterPage(@PathVariable final String orderCode, @PathVariable final String consignmentCode,
			final OrderReviewForm orderReviewForm, final Model model) throws CMSItemNotFoundException
	{
		try
		{
			getOrderFacade().getOrderDetailsForCode(orderCode);
		}
		catch (final UnknownIdentifierException ex)
		{
			return FORWARD_PREFIX + "/404";
		}

		final Optional<ConsignmentData> optional = getConsignmentTrackingFacade().getConsignmentByCode(orderCode, consignmentCode);

		if (!optional.isPresent())
		{
			return FORWARD_PREFIX + "/404";
		}

		if (!optional.get().getReviewable())
		{
			return REDIRECT_PREFIX + ORDER_HISTORY_PAGE;
		}

		model.addAttribute("consignment", optional.get());
		populateCommonModelAttributes(model, orderReviewForm, orderCode);

		return MarketplaceaddonControllerConstants.Views.Pages.Order.OrderReviewPage;
	}

	@RequireHardLogIn
	@RequestMapping(value = ORDER_CODE_PATH_VARIABEL + "/review" + CONSIGNMENT_CODE_PATH_VARIABEL, method = RequestMethod.POST)
	public String postReview(@PathVariable final String orderCode, @PathVariable final String consignmentCode,
			final OrderReviewForm orderReviewForm, final Model model, final BindingResult result,
			final RedirectAttributes redirectAttrs) throws CMSItemNotFoundException
	{
		if (getCustomerVendorReviewFacade().postedReview(consignmentCode))
		{
			return REDIRECT_PREFIX + ORDER_DETAIL_PAGE + orderCode;
		}

		getCustomerReviewValidator().validate(orderReviewForm, result);
		if (result.hasErrors())
		{
			getConsignmentTrackingFacade().getConsignmentByCode(orderCode, consignmentCode)
					.ifPresent(consignment -> model.addAttribute("consignment", consignment));
			populateCommonModelAttributes(model, orderReviewForm, orderCode);
			GlobalMessages.addErrorMessage(model, GLOBAL_ERROR_KEY);
			return MarketplaceaddonControllerConstants.Views.Pages.Order.OrderReviewPage;
		}

		orderReviewForm.getProductReviewForms().forEach(k -> {
			final ReviewData reviewData = new ReviewData();
			reviewData.setRating(k.getRating());
			reviewData.setComment(XSSFilterUtil.filter(k.getComment()));
			reviewData.setHeadline(REVIEW_HEADLINE);
			getProductFacade().postReview(k.getProductCode(), reviewData);
		});

		final VendorReviewData vendorReviewData = new VendorReviewData();
		vendorReviewData.setCommunication(orderReviewForm.getCommunication());
		vendorReviewData.setDelivery(orderReviewForm.getDelivery());
		vendorReviewData.setSatisfaction(orderReviewForm.getSatisfaction());
		vendorReviewData.setComment(orderReviewForm.getComment());
		getCustomerVendorReviewFacade().postReview(orderCode, consignmentCode, vendorReviewData);

		return REDIRECT_PREFIX + ORDER_DETAIL_PAGE + orderCode;
	}

	protected void populateCommonModelAttributes(final Model model, final OrderReviewForm form, final String orderCode)
			throws CMSItemNotFoundException
	{
		final List<Breadcrumb> breadcrumbs = getAccountBreadcrumbBuilder().getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(ORDER_HISTORY_PAGE,
				getMessageSource().getMessage(BREADCRUMB_ORDER_HISTORY_KEY, null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(
				new Breadcrumb(ORDER_DETAIL_PAGE + orderCode, getMessageSource().getMessage(BREADCRUMB_ORDER_DETAIL_KEY, new Object[]
				{ orderCode }, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage(BREADCRUMB_ORDER_REVIEW_KEY, new Object[] {}, getI18nService().getCurrentLocale()),
				null));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbs);
		model.addAttribute("orderReviewForm", form);
		final ContentPageModel orderPage = getContentPageForLabelOrId("order");
		storeCmsPageInModel(model, orderPage);
		setUpMetaDataForContentPage(model, orderPage);
	}


	protected Validator getCustomerReviewValidator()
	{
		return customerReviewValidator;
	}

	protected ResourceBreadcrumbBuilder getAccountBreadcrumbBuilder()
	{
		return accountBreadcrumbBuilder;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected CustomerVendorReviewFacade getCustomerVendorReviewFacade()
	{
		return customerVendorReviewFacade;
	}

	protected ConsignmentTrackingFacade getConsignmentTrackingFacade()
	{
		return consignmentTrackingFacade;
	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}
}
