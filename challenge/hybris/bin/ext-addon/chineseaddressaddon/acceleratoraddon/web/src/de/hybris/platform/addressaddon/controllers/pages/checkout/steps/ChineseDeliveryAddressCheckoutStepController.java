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
package de.hybris.platform.addressaddon.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.addressaddon.constants.ControllerConstants;
import de.hybris.platform.addressaddon.controllers.imported.DeliveryAddressCheckoutStepController;
import de.hybris.platform.addressaddon.forms.ChineseAddressForm;
import de.hybris.platform.addressaddon.handlers.ChineseAddressHandler;
import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.util.Config;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Scope("tenant")
@RequestMapping(value = "/checkout/multi/delivery-address")
public class ChineseDeliveryAddressCheckoutStepController extends DeliveryAddressCheckoutStepController
{
	private static final String CHINA_ISOCODE = "CN";

	private static final String DELIVERY_ADDRESS = "delivery-address";

	private static final String FORM_ENTRY_INVALID = "address.error.formentry.invalid";

	private ChineseAddressHandler chineseAddressHandler;

	@Resource(name = "chineseAddressFacade")
	private AddressFacade chineseAddressFacade;

	@Resource(name = "addressDataUtil")
	private AddressDataUtil addressDataUtil;

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_ADDRESS)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		super.enterStep(model, redirectAttributes);
		final ChineseAddressForm chineseAddressForm = chineseAddressHandler.setChineseAddressFormInModel(model);
		chineseAddressHandler.prepareAddressForm(model, chineseAddressForm);
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		getAddressValidator().validate(addressForm, bindingResult);
		populateCommonModelAttributes(model, cartData, addressForm);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, FORM_ENTRY_INVALID);
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = addressDataUtil.convertToAddressData(addressForm);
		processAddressVisibilityAndDefault(addressForm, newAddress);
		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");
		if (addressRequiresReview)
		{
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		getUserFacade().addAddress(newAddress);
		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		getCheckoutFacade().setDeliveryAddress(newAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{
			getUserFacade().removeAddress(previousSelectedAddress);
		}
		getCheckoutFacade().setDeliveryAddress(newAddress);
		return getCheckoutStep().nextStep();
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "countryIso=" + CHINA_ISOCODE)
	@RequireHardLogIn
	public String add(@ModelAttribute("addressForm") final ChineseAddressForm addressForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		getChineseAddressHandler().validate(addressForm, bindingResult);
		super.populateCommonModelAttributes(model, cartData, addressForm);
		if (bindingResult.hasErrors())
		{
			if (addressForm.getRegionIso() != null)
			{
				model.addAttribute("cities", chineseAddressFacade.getCitiesForRegion(addressForm.getRegionIso()));
			}
			if (addressForm.getCityIso() != null)
			{
				model.addAttribute("districts", chineseAddressFacade.getDistrictsForCity(addressForm.getCityIso()));
			}
			GlobalMessages.addErrorMessage(model, FORM_ENTRY_INVALID);
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = getChineseAddressHandler().prepareAddressData(addressForm);

		if (addressForm.getSaveInAddressBook() != null)
		{
			newAddress.setVisibleInAddressBook(addressForm.getSaveInAddressBook().booleanValue());
			if (addressForm.getSaveInAddressBook().booleanValue() && getUserFacade().isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
			}
		}
		else if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}
		getUserFacade().addAddress(newAddress);

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{ // temporary address should be removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);

		return getCheckoutStep().nextStep();
	}

	@Override
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editAddressForm(@RequestParam("editAddressCode") final String editAddressCode, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		super.editAddressForm(editAddressCode, model, redirectAttributes);

		AddressData addressData = null;
		if (StringUtils.isNotEmpty(editAddressCode))
		{
			addressData = getCheckoutFacade().getDeliveryAddressForCode(editAddressCode);
		}
		model.addAttribute("addressData", addressData);
		final ChineseAddressForm addressForm = chineseAddressHandler.setChineseAddressFormInModel(model);
		chineseAddressHandler.prepareAddressForm(model, addressForm);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		super.populateCommonModelAttributes(model, cartData, addressForm);

		if (addressData != null)
		{
			model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
		}
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}


	@Override
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@RequireHardLogIn
	public String edit(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		super.edit(addressForm, bindingResult, model, redirectModel);
		if (bindingResult.hasErrors())
		{
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}
		return getCheckoutStep().nextStep();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "countryIso=" + CHINA_ISOCODE)
	@RequireHardLogIn
	public String edit(@ModelAttribute("addressForm") final ChineseAddressForm addressForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		getChineseAddressHandler().validate(addressForm, bindingResult);
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		if (bindingResult.hasErrors())
		{
			super.populateCommonModelAttributes(model, cartData, addressForm);
			chineseAddressHandler.prepareAddressForm(model, addressForm);
			GlobalMessages.addErrorMessage(model, FORM_ENTRY_INVALID);
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = getChineseAddressHandler().prepareAddressData(addressForm);
		if (addressForm.getSaveInAddressBook() == null)
		{
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setVisibleInAddressBook(Boolean.TRUE.equals(addressForm.getSaveInAddressBook()));
		}

		newAddress.setDefaultAddress(getUserFacade().isAddressBookEmpty() || getUserFacade().getAddressBook().size() == 1
				|| Boolean.TRUE.equals(addressForm.getDefaultAddress()));

		getUserFacade().editAddress(newAddress);
		getCheckoutFacade().setDeliveryModeIfAvailable();
		getCheckoutFacade().setDeliveryAddress(newAddress);

		return getCheckoutStep().nextStep();
	}

	@Override
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel)
	{
		if (!CHINA_ISOCODE.equals(addressForm.getCountryIso()))
		{
			return super.doSelectSuggestedAddress(addressForm, redirectModel);
		}
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final ChineseAddressForm chineseAddressForm = new ChineseAddressForm();
		BeanUtils.copyProperties(addressForm, chineseAddressForm);
		if (CHINA_ISOCODE.equals(addressForm.getCountryIso()))
		{
			chineseAddressForm.setCityIso(request.getParameter("cityIso"));
			chineseAddressForm.setDistrictIso(request.getParameter("districtIso"));
			chineseAddressForm.setFullname(request.getParameter("fullname"));
			chineseAddressForm.setCellphone(request.getParameter("cellphone"));
		}
		final Set<String> resolveCountryRegions = org.springframework.util.StringUtils.commaDelimitedListToSet(Config
				.getParameter("resolve.country.regions"));

		final AddressData selectedAddress = chineseAddressHandler.prepareAddressData(chineseAddressForm);
		final CountryData countryData = getI18NFacade().getCountryForIsocode(addressForm.getCountryIso());
		selectedAddress.setCountry(countryData);
		selectedAddress.setPhone(addressForm.getPhone());

		if (resolveCountryRegions.contains(countryData.getIsocode()) && addressForm.getRegionIso() != null
				&& !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
			selectedAddress.setRegion(regionData);
		}

		if (addressForm.getSaveInAddressBook() != null)
		{
			selectedAddress.setVisibleInAddressBook(addressForm.getSaveInAddressBook().booleanValue());
		}

		if (Boolean.TRUE.equals(addressForm.getEditAddress()))
		{
			getUserFacade().editAddress(selectedAddress);
		}
		else
		{
			getUserFacade().addAddress(selectedAddress);
		}

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(selectedAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{ // temporary address should be removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "checkout.multi.address.added");

		return getCheckoutStep().nextStep();
	}


	@Override
	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(DELIVERY_ADDRESS);
	}

	public ChineseAddressHandler getChineseAddressHandler()
	{
		return chineseAddressHandler;
	}

	@Required
	public void setChineseAddressHandler(final ChineseAddressHandler chineseAddressHandler)
	{
		this.chineseAddressHandler = chineseAddressHandler;
	}

}
