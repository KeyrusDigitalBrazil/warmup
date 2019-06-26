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
package de.hybris.platform.chinesetaxinvoiceaddon.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.chinesetaxinvoiceaddon.controllers.ControllerConstants;
import de.hybris.platform.chinesetaxinvoiceaddon.forms.TaxInvoiceForm;
import de.hybris.platform.chinesetaxinvoiceaddon.forms.validation.TaxInvoiceValidator;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoicefacades.facades.TaxInvoiceCheckoutFacade;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceCategory;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/checkout/multi/tax-invoice")
public class ChineseTaxInvoiceController extends AbstractCheckoutStepController
{

	private static final String TAX_INVOICE = "tax-invoice";
	private static final String[] DISALLOWED_FIELDS = new String[] {};

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Autowired
	private TaxInvoiceValidator taxInvoiceValidator;

	@Resource(name = "chineseTaxInvoiceCheckoutFacade")
	private TaxInvoiceCheckoutFacade taxInvoiceCheckoutFacade;


	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = TAX_INVOICE)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{

		final CartData cartData = taxInvoiceCheckoutFacade.getCheckoutCart();
		final TaxInvoiceForm invoiceForm = new TaxInvoiceForm();
		if (cartData.getTaxInvoice() != null)
		{
			final TaxInvoiceData taxInvoiceData = cartData.getTaxInvoice();
			invoiceForm.setInvoiceRequired(true);
			BeanUtils.copyProperties(taxInvoiceData, invoiceForm);
		}

		model.addAttribute("invoiceForm", invoiceForm);
		populatorAttributes(model);
		return ControllerConstants.Views.MultiStepCheckout.CHINESE_TAX_INVOICE_PAGE;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addInvoice(@ModelAttribute("invoiceForm") final TaxInvoiceForm invoiceForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirect) throws CMSItemNotFoundException
	{

		if (invoiceForm.isInvoiceRequired())
		{
			taxInvoiceValidator.validate(invoiceForm, bindingResult);

			if (bindingResult.hasErrors())
			{
				populatorAttributes(model);
				return ControllerConstants.Views.MultiStepCheckout.CHINESE_TAX_INVOICE_PAGE;
			}

			final TaxInvoiceData taxInvoiceData = new TaxInvoiceData();
			BeanUtils.copyProperties(invoiceForm, taxInvoiceData);

			taxInvoiceCheckoutFacade.setTaxInvoice(taxInvoiceData);
		}
		else
		{
			if (StringUtils.isNotBlank(invoiceForm.getId()))
			{
				taxInvoiceCheckoutFacade.removeTaxInvoice(invoiceForm.getId());
			}
		}

		return getCheckoutStep().nextStep();
	}

	@Override
	@RequireHardLogIn
	@RequestMapping(value = "/back", method = RequestMethod.GET)
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@Override
	@RequireHardLogIn
	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	protected CheckoutStep getCheckoutStep()
	{

		return getCheckoutStep(TAX_INVOICE);
	}

	protected void setTaxInvoices(final Model model)
	{
		final List<InvoiceCategory> invoiceCategories = enumerationService.getEnumerationValues(InvoiceCategory.class);
		final List<InvoiceRecipientType> recipientTypes = enumerationService.getEnumerationValues(InvoiceRecipientType.class);

		model.addAttribute("invoiceCategories", invoiceCategories);
		model.addAttribute("recipientTypes", recipientTypes);
	}

	protected String getBreadcrumbKey()
	{
		return "checkout.multi." + getCheckoutStep().getProgressBarId() + ".breadcrumb";
	}

	protected void populatorAttributes(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("cartData", taxInvoiceCheckoutFacade.getCheckoutCart());
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbKey()));
		setTaxInvoices(model);
		prepareDataForPage(model);
		final ContentPageModel multiCheckoutSummaryPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, multiCheckoutSummaryPage);
		setUpMetaDataForContentPage(model, multiCheckoutSummaryPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}


	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}


	protected TaxInvoiceValidator getTaxInvoiceValidator()
	{
		return taxInvoiceValidator;
	}

	public void setTaxInvoiceValidator(final TaxInvoiceValidator taxInvoiceValidator)
	{
		this.taxInvoiceValidator = taxInvoiceValidator;
	}

	protected TaxInvoiceCheckoutFacade getTaxInvoiceCheckoutFacade()
	{
		return taxInvoiceCheckoutFacade;
	}

	public void setTaxInvoiceCheckoutFacade(final TaxInvoiceCheckoutFacade taxInvoiceCheckoutFacade)
	{
		this.taxInvoiceCheckoutFacade = taxInvoiceCheckoutFacade;
	}


}
