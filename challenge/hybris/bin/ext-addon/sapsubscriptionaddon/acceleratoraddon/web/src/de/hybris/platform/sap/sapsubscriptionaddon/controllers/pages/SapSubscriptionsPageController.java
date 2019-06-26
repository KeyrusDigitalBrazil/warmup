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
package de.hybris.platform.sap.sapsubscriptionaddon.controllers.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.sapsubscriptionaddon.controllers.SapsubscriptionaddonControllerConstants;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionBillForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionCancellationForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionExtensionForm;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;

/**
 * Controller for subscriptions page.
 */
@Controller
@RequestMapping("/my-account")
public class SapSubscriptionsPageController extends AbstractSearchPageController 
{

	// Internal Redirects
	private static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
	private static final String REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS = REDIRECT_PREFIX + "/my-account/subscriptions";
	private static final String REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE= REDIRECT_PREFIX + "/my-account/subscription/";

	// CMS Pages
	private static final String SUBSCRIPTIONS_CMS_PAGE = "subscriptions";
	private static final String SUBSCRIPTION_DETAILS_CMS_PAGE = "subscription";
	private static final String SUBSCRIPTION_BILL_CMS_PAGE = "subscriptionBills";
	private static final String SUBSCRIPTION_BILL_DETAILS_CMS_PAGE = "subscriptionBillDetails";

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a
	 * Uri value is incorrectly extracted if it contains on or more '.'
	 * characters. Please see https://jira.springsource.org/browse/SPR-6164 for
	 * a discussion on the issue and future resolution.
	 */
	private static final String SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN = "{subscriptionCode:.*}";
	private static final Logger LOG = Logger.getLogger(SapSubscriptionsPageController.class);
	
	@Resource(name = "customerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "subscriptionFacade")
	private SapRevenueCloudSubscriptionFacade sapSubscriptionFacade;

	/*
	 * Display All Subscription
	 */
	@RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
	@RequireHardLogIn
	public String subscriptions(@Nonnull final Model model) throws CMSItemNotFoundException 
	{
		List<SubscriptionData> sortedSubscriptions = new ArrayList<>();
		try 
		{
			final Collection<SubscriptionData> subscriptions = sapSubscriptionFacade.getSubscriptions();
			if (subscriptions != null)
			{
				sortedSubscriptions = new ArrayList<>(subscriptions);
			}
		} 
		catch (final SubscriptionFacadeException e) 
		{
			LOG.error("Error while retrieving subscriptions", e);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTIONS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTIONS_CMS_PAGE));
		model.addAttribute(SUBSCRIPTIONS_CMS_PAGE, sortedSubscriptions);
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	/*
	 * Get Subscription Details
	 */
	@RequestMapping(value = "/subscription/" + SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String subscriptionDetail(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model)
			throws CMSItemNotFoundException 
	{
		try
		{
			final SubscriptionData subscriptionDetails = sapSubscriptionFacade.getSubscription(subscriptionCode);
			model.addAttribute("subscriptionData", subscriptionDetails);
			final List<Breadcrumb> breadcrumbs = buildSubscriptionDetailBreadcrumb(subscriptionDetails);
			model.addAttribute("breadcrumbs", breadcrumbs);			
			final List<ProductData> upsellingOptions = sapSubscriptionFacade
					.getUpsellingOptionsForSubscription(subscriptionDetails.getProductCode());
			model.addAttribute("upgradable", CollectionUtils.isNotEmpty(upsellingOptions));

		} 
		catch (final SubscriptionFacadeException e)
		{
			LOG.warn("Attempted to load a subscription that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_DETAILS_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_DETAILS_CMS_PAGE));
		return getViewForPage(model);
	}	
	
	/*
	 * Cancel Subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/cancel", method = RequestMethod.POST)
	public String cancelSubscription(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
			@ModelAttribute("subscriptionCancellationForm") final SubscriptionCancellationForm subscriptionCancellationForm,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException 
	{
		try 
		{
			sapSubscriptionFacade.cancelSubscription(
					populateCancellationSubscriptionData(subscriptionCode, subscriptionCancellationForm));
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
					"text.account.subscription.cancel.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		} 
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.cancel.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		}
	}

	/*
	 * extend subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/extend", method = RequestMethod.POST)
	public String extendSubscription(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
			@ModelAttribute("subscriptionExtensionForm") final SubscriptionExtensionForm subscriptionExtensionForm,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException 
	{
		try 
		{
			boolean errors = validateExtensionForm(subscriptionExtensionForm,redirectModel);
			if(errors)
			{
				return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
			}
			sapSubscriptionFacade.extendSubscription(populateExtendSubscriptionData(subscriptionCode, subscriptionExtensionForm));
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
					"text.account.subscription.extendTerm.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		} 
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.extend.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
	}
	
	/*
	 * calculate effective cancellation date
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/caleffDate/{version:.*}", method = RequestMethod.GET)
	public String calculateCancellationDate(@PathVariable("subscriptionCode") final String subscriptionCode,@PathVariable("version") final String version, 
			final Model model,final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try 
		{
			SubscriptionData subscriptionData = sapSubscriptionFacade.computeCancellationDate(subscriptionCode);
			model.addAttribute("subscriptionData", subscriptionData);
			model.addAttribute("version",version);
			model.addAttribute("subscriptionId", subscriptionCode);
			LOG.info("Effective End Date : " + subscriptionData.getEndDate());
			return SapsubscriptionaddonControllerConstants.Views.Fragments.Subscriptions.CancelSubscriptionPopup;
			
		} 
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while calculating cancellation date");
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"error occured while fetching effective end date");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
	}
	
	
	/*
	 * fetch subscription bills
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/bills", method = RequestMethod.GET)
	public String getSubscriptionBillsPage( final Model model,
			@ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException 
	{
		try 
		{
			Collection<SubscriptionBillingData> subscriptionBillsList = sapSubscriptionFacade.getSubscriptionBills(subscriptionBillForm.getSubscriptionId(),
					subscriptionBillForm.getFromDate(),subscriptionBillForm.getToDate());
			model.addAttribute(SUBSCRIPTION_BILL_CMS_PAGE, subscriptionBillsList);
		} 
		catch (final Exception ex) // NOSONAR
		{	
			LOG.error("error occured while fetching subscription bills");
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions.bills"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}
	
	/*
	 * fetch subscription bill using billId
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/bills/"  + "{billId:.*}", method = RequestMethod.GET)
	public String getSubscriptionBillById( @PathVariable("billId") final String billId,final Model model,
			@ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException 
	{
		try 
		{
			List<SubscriptionBillingData> subscriptionBillList = sapSubscriptionFacade.getSubscriptionBillsById(billId);
			final List<Breadcrumb> breadcrumbs = buildSubscriptionBillDetailBreadcrumb(billId);
			model.addAttribute("subscriptionBill", subscriptionBillList);
			model.addAttribute("breadcrumbs", breadcrumbs);
		} 
		catch (final Exception ex) // NOSONAR
		{	
			LOG.error("error occured while fetching subscription bills for BillId : "+ billId + ex);
		}
		
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_DETAILS_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
		
	}
	
	/*
	 * fetch subscription bills
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/bills", method = RequestMethod.POST)
	public String getSubscriptionBills( final Model model,
			@ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			Collection<SubscriptionBillingData> subscriptionBills = sapSubscriptionFacade.getSubscriptionBills(subscriptionBillForm.getSubscriptionId(),
					subscriptionBillForm.getFromDate(),subscriptionBillForm.getToDate());
			model.addAttribute("subscriptionBills", subscriptionBills);
		} 
		catch (final Exception ex) // NOSONAR
		{	
			LOG.error("error occured while fetching subscription bills " + ex);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	protected List<Breadcrumb> buildSubscriptionDetailBreadcrumb(final SubscriptionData subscriptionData)
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions");
		breadcrumbs.get(breadcrumbs.size() - 1).setUrl("/my-account/subscriptions");
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.subscription.subscriptionBreadcrumb",
						new Object[] { subscriptionData.getDocumentNumber() }, "{0}",
						getI18nService().getCurrentLocale()),
				null));
		return breadcrumbs;
	}
	
	protected List<Breadcrumb> buildSubscriptionBillDetailBreadcrumb(final String billId) 
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions.bills");
		breadcrumbs.get(breadcrumbs.size() - 1).setUrl("/my-account/subscription/bills");
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.subscription.subscriptionBillNumber",
						new Object[] { billId }, "{0}",
						getI18nService().getCurrentLocale()),
				null));
		return breadcrumbs;
	}

	protected SubscriptionData populateCancellationSubscriptionData(String code,
			SubscriptionCancellationForm cancellationForm)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(cancellationForm.getVersion());
		subscriptionData.setId(code);
		subscriptionData.setValidTillDate(cancellationForm.getSubscriptionEndDate());
		subscriptionData.setEndDate(cancellationForm.getValidUntilDate());
		subscriptionData.setRatePlanId(cancellationForm.getRatePlanId());
		return subscriptionData;
	}

	protected SubscriptionData populateExtendSubscriptionData(String code, SubscriptionExtensionForm extensionForm)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(extensionForm.getVersion());
		subscriptionData.setId(code);
		subscriptionData.setExtendedPeriod(extensionForm.getExtensionPeriod());
		subscriptionData.setValidTillDate(extensionForm.getValidTilldate());
		subscriptionData.setRatePlanId(extensionForm.getRatePlanId());
		subscriptionData.setUnlimited(extensionForm.isUnlimited());
		return subscriptionData;
	}

	protected boolean validateExtensionForm(final SubscriptionExtensionForm subscriptionExtensionForm,final RedirectAttributes redirectModel)
	{
		boolean errors = false;
		if(subscriptionExtensionForm.isUnlimited())
		{
			return false;
		}
		String extendedperiod = subscriptionExtensionForm.getExtensionPeriod();
		if(extendedperiod.isEmpty())
		{
			errors = true;
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"subscription.extend.empty.period");
			return errors;
		}
		try 
		{
			Integer.parseInt(extendedperiod);
		}
		catch (NumberFormatException ne)
		{
			errors = true;
			LOG.error("Entered value is not a valid number");
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"enter a valid number");
			return errors;
		}
		return errors;
	}
}
