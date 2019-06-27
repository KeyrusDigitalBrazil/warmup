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
package de.hybris.platform.sapdigitalpaymentaddon.controllers.pages.checkout;

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentPollRegisteredCardException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRegisterUrlException;
import de.hybris.platform.cissapdigitalpayment.facade.CisSapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.facade.SapDigitalPaymentFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.sapdigitalpaymentaddon.controllers.ControllerConstants;
import de.hybris.platform.sapdigitalpaymentaddon.controllers.pages.checkout.validation.BillingAddressValidator;
import de.hybris.platform.sapdigitalpaymentaddon.forms.BillingAddressDetailsForm;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * Controller class to handle all the payment and billing address requests in the SAP Digital payment checkout flow
 */
@Controller
@RequestMapping(value = "/checkout/multi/sap-digital-payment")
public class SapDigitalPaymentAndBillingMethodCheckoutStepController extends AbstractCheckoutStepController
{

	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentAndBillingMethodCheckoutStepController.class);

	private static final String PAYMENT_BILLING_METHOD = "payment-billing-method";

	private static final String CART_DATA_ATTR = "cartData";

	private static final String PAYMENT_DETAILS_ERROR = "checkout.multi.paymentMethod.addPaymentDetails.generalError";



	@Resource(name = "cisSapDigitalPaymentFacade")
	private CisSapDigitalPaymentFacade cisSapDigitalPaymentFacade;

	@Resource(name = "addressDataUtil")
	private AddressDataUtil addressDataUtil;

	@Resource(name = "sapDigitalPaymentFacade")
	private SapDigitalPaymentFacade sapDigitalPaymentFacade;

	@Resource(name = "billingAddressValidator")
	BillingAddressValidator billingAddressValidator;

	@Resource(name = "userFacade")
	private UserFacade userFacade;


	/**
	 * Register the card with SAP Digital payment
	 *
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */

	@RequestMapping(value = "/cards/new-card", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_BILLING_METHOD)
	public String registerNewCard(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{

				final String hopPostUrl = getSapDigitalPaymentFacade().getCardRegistrationUrl();
				startPollCardProcess();

				//If the URL from the digital payment is not empty, redirect the user to the digital payment UI
				if (StringUtils.isNotEmpty(hopPostUrl))
				{
					return REDIRECT_PREFIX + hopPostUrl;
				}
			}
			catch (final SapDigitalPaymentRegisterUrlException ex)
			{
				logError(ex, "Digital payment registeration URL request failed");
				GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
				//Status codes hardcoded
				model.addAttribute("decision", "ERROR");
				model.addAttribute("reasonCode", "150");
				model.addAttribute("redirectUrl", "checkout/multi/sap-digital-payment/billing-address/add");
				setupAddPaymentPage(model);
				return ControllerConstants.Views.Pages.MultiStepCheckout.DigitalPaymentGeneralErrorPage;
			}
			catch (final RuntimeException ex)
			{
				//Generic exception handling block.
				logError(ex, "Digital payment registeration URL request failed due to unknown error");
				GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
				return ControllerConstants.Views.Pages.MultiStepCheckout.DigitalPaymentGeneralErrorPage;
			}

		}
		GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
		model.addAttribute("redirectUrl", "checkout/multi/billing-address/add");
		setupAddPaymentPage(model);
		return ControllerConstants.Views.Pages.MultiStepCheckout.DigitalPaymentGeneralErrorPage;
	}





	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 *
	 * @param selectedPaymentMethodId
	 *           - the id of the payment method to use.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/cards/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
	{
		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
		}
		return getCheckoutStep().nextStep();
	}




	@RequestMapping(value = "/cards/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/cards/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}


	/**
	 * Display the billing and payment page
	 */
	@Override
	@RequestMapping(value = "billing-address/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_BILLING_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getCheckoutFacade().setDeliveryModeIfAvailable();
		setupBillingAddressPage(model);


		// Build up the SOP form data and render page containing form
		final BillingAddressDetailsForm billingAddressDetailsForm = new BillingAddressDetailsForm();

		final AddressForm addressForm = new AddressForm();
		billingAddressDetailsForm.setBillingAddress(addressForm);
		model.addAttribute("billingAddressDetailsForm", billingAddressDetailsForm);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute(CART_DATA_ATTR, cartData);

		if (StringUtils.isNotBlank(billingAddressDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(billingAddressDetailsForm.getBillTo_country()));
			model.addAttribute("country", billingAddressDetailsForm.getBillTo_country());
		}

		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditBillingAddressPage;

	}

	/**
	 * Adds the billing address details to the cart
	 *
	 * @param model
	 * @param billingAddressDetailsForm
	 * @param bindingResult
	 * @return String
	 * @throws CMSItemNotFoundException
	 */


	@RequestMapping(value = "billing-address/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addBillingAddress(final Model model, final BillingAddressDetailsForm billingAddressDetailsForm,
			final BindingResult bindingResult) throws CMSItemNotFoundException
	{

		//Copy the address fields.
		copyBillingAddressFields(billingAddressDetailsForm);
		getBillingAddressValidator().validate(billingAddressDetailsForm, bindingResult);
		setupBillingAddressPage(model);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.billingAddress.formentry.invalid");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditBillingAddressPage;
		}

		try
		{

			final AddressData addressData;
			if (billingAddressDetailsForm.isUseDeliveryAddress())
			{
				addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
				if (addressData == null)
				{
					GlobalMessages.addErrorMessage(model,
							"checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
					return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditBillingAddressPage;
				}

				addressData.setBillingAddress(true); // mark this as billing address
			}
			else
			{
				final AddressForm addressForm = billingAddressDetailsForm.getBillingAddress();
				addressData = getAddressDataUtil().convertToAddressData(addressForm);
				addressData.setShippingAddress(Boolean.TRUE.equals(addressForm.getShippingAddress()));
				addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
			}

			getAddressVerificationFacade().verifyAddressData(addressData);

			getCisSapDigitalPaymentFacade().addPaymentAddressToCart(addressData); //Add payment billing address details to the cart
			setCheckoutStepLinksForModel(model, getCheckoutStep());
			return getCheckoutStep().nextStep();
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Error while adding the billing details to the cart" + e);
			}
			LOG.error("Error while adding the billing details to the cart" + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.error.billingAddress.formentry.invalid");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditBillingAddressPage;

		}
	}



	/**
	 * @param ex
	 */
	private static void logError(final Exception ex, final String errorMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(errorMessage + ex);
		}
		LOG.error(errorMessage);
	}



	/**
	 * Fetch the session Id and start the poll card process
	 */
	private void startPollCardProcess()
	{
		try
		{
			// Start a process to poll the card and check the register card status
			final String sessionId = cisSapDigitalPaymentFacade.getSapDigitalPaymentRegisterCardSession();
			getSapDigitalPaymentFacade().createPollRegisteredCardProcess(sessionId);
		}
		catch (final SapDigitalPaymentPollRegisteredCardException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Error while polling the registered card details" + ex);
			}
			LOG.error("Error while polling the registered card details" + ex.getMessage());

		}
	}


	/**
	 * @param billingAddressDetailsForm
	 */
	private void copyBillingAddressFields(final BillingAddressDetailsForm billingAddressDetailsForm)
	{
		// YTODO Auto-generated method stub
		if (!billingAddressDetailsForm.isUseDeliveryAddress())
		{
			final AddressForm billingAddress = new AddressForm();
			billingAddress.setTitleCode(billingAddressDetailsForm.getBillTo_titleCode());
			billingAddress.setFirstName(billingAddressDetailsForm.getBillTo_firstName());
			billingAddress.setLastName(billingAddressDetailsForm.getBillTo_lastName());
			billingAddress.setLine1(billingAddressDetailsForm.getBillTo_street1());
			billingAddress.setLine2(billingAddressDetailsForm.getBillTo_street2());
			billingAddress.setTownCity(billingAddressDetailsForm.getBillTo_city());
			billingAddress.setCountryIso(billingAddressDetailsForm.getBillTo_country());
			billingAddress.setPostcode(billingAddressDetailsForm.getBillTo_postalCode());
			billingAddress.setPhone(billingAddressDetailsForm.getBillTo_phoneNumber());
			billingAddress.setRegionIso(billingAddressDetailsForm.getBillTo_state());
			billingAddress.setBillingAddress(Boolean.TRUE);
			billingAddress.setShippingAddress(Boolean.FALSE);
			billingAddressDetailsForm.setBillingAddress(billingAddress);
		}
	}

	protected void setupBillingAddressPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.billingAddress.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}



	/**
	 * @return the billingAddressValidator
	 */
	public BillingAddressValidator getBillingAddressValidator()
	{
		return billingAddressValidator;
	}


	/**
	 * @param billingAddressValidator
	 *           the billingAddressValidator to set
	 */
	public void setBillingAddressValidator(final BillingAddressValidator billingAddressValidator)
	{
		this.billingAddressValidator = billingAddressValidator;
	}


	/**
	 * @return the userFacade
	 */
	@Override
	public UserFacade getUserFacade()
	{
		return userFacade;
	}


	/**
	 * @param userFacade
	 *           the userFacade to set
	 */
	public void setUserFacade(final UserFacade userFacade)
	{
		this.userFacade = userFacade;
	}


	protected boolean checkPaymentSubscription(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm,
			final CCPaymentInfoData newPaymentSubscription)
	{
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) && getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.createSubscription.failedMsg");
			return false;
		}
		return true;
	}

	protected void fillInPaymentData(@Valid final PaymentDetailsForm paymentDetailsForm, final CCPaymentInfoData paymentInfoData)
	{
		paymentInfoData.setId(paymentDetailsForm.getPaymentId());
		paymentInfoData.setCardType(paymentDetailsForm.getCardTypeCode());
		paymentInfoData.setAccountHolderName(paymentDetailsForm.getNameOnCard());
		paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());
		paymentInfoData.setStartMonth(paymentDetailsForm.getStartMonth());
		paymentInfoData.setStartYear(paymentDetailsForm.getStartYear());
		paymentInfoData.setExpiryMonth(paymentDetailsForm.getExpiryMonth());
		paymentInfoData.setExpiryYear(paymentDetailsForm.getExpiryYear());
		if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) || getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			paymentInfoData.setSaved(true);
		}
		paymentInfoData.setIssueNumber(paymentDetailsForm.getIssueNumber());
	}


	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}


	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_BILLING_METHOD);
	}

	/**
	 * @return the cisSapDigitalPaymentFacade
	 */
	public CisSapDigitalPaymentFacade getCisSapDigitalPaymentFacade()
	{
		return cisSapDigitalPaymentFacade;
	}

	/**
	 * @param cisSapDigitalPaymentFacade
	 *           the cisSapDigitalPaymentFacade to set
	 */
	public void setCisSapDigitalPaymentFacade(final CisSapDigitalPaymentFacade cisSapDigitalPaymentFacade)
	{
		this.cisSapDigitalPaymentFacade = cisSapDigitalPaymentFacade;
	}


	/**
	 * @return the sapDigitalPaymentFacade
	 */
	public SapDigitalPaymentFacade getSapDigitalPaymentFacade()
	{
		return sapDigitalPaymentFacade;
	}


	/**
	 * @param sapDigitalPaymentFacade
	 *           the sapDigitalPaymentFacade to set
	 */
	public void setSapDigitalPaymentFacade(final SapDigitalPaymentFacade sapDigitalPaymentFacade)
	{
		this.sapDigitalPaymentFacade = sapDigitalPaymentFacade;
	}

	/**
	 * @return the addressDataUtil
	 */
	public AddressDataUtil getAddressDataUtil()
	{
		return addressDataUtil;
	}

	/**
	 * @param addressDataUtil
	 *           the addressDataUtil to set
	 */
	public void setAddressDataUtil(final AddressDataUtil addressDataUtil)
	{
		this.addressDataUtil = addressDataUtil;
	}



}
