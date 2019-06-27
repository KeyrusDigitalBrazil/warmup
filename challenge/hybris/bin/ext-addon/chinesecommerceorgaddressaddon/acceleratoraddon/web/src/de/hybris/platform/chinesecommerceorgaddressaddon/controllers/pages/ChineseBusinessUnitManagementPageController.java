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
package de.hybris.platform.chinesecommerceorgaddressaddon.controllers.pages;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.chinesecommerceorgaddressaddon.breadcrumb.impl.ChinesecommerceorgaddressaddonCompanyBreadcrumbBuilder;
import de.hybris.platform.chinesecommerceorgaddressaddon.constants.WebConstants;
import de.hybris.platform.chinesecommerceorgaddressaddon.controllers.ChinesecommerceorgaddressaddonControllerConstants;
import de.hybris.platform.chinesecommerceorgaddressaddon.forms.ChineseUnitAddressForm;
import de.hybris.platform.chinesecommerceorgaddressaddon.handlers.ChineseUnitAddressHandler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CountryType;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Process the request for adding and editing chinese address for units
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-units")
public class ChineseBusinessUnitManagementPageController extends AbstractPageController
{

	private static final String COUNTRY_ATTR = "country";
	private static final String REGIONS_ATTR = "regions";
	private static final String CITIES_ATTR = "cities";
	private static final String DISTRICTS_ATTR = "districts";
	private static final String ADDRESS_FORM_ATTR = "addressForm";
	private static final String COUNTRY_DATA_ATTR = "countryData";
	private static final String TITLE_DATA_ATTR = "titleData";
	private static final String ADDRESS_DATA_ATTR = "addressData";
	private static final String UNIT_NAME_ATTR = "unitName";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String UNIT_UID_ATTR = "uid";
	private static final String UNIT_DATA_ATTR = "unitdata";
	protected static final String MANAGE_UNITS_CMS_PAGE = "manageUnits";
	protected static final String MANAGE_UNIT_DETAILS_URL = "/my-company/organization-management/manage-units/details/?unit=%s";
	protected static final String REDIRECT_TO_UNIT_DETAILS = REDIRECT_PREFIX + MANAGE_UNIT_DETAILS_URL;
	private static final String[] DISALLOWED_FIELDS = new String[] {};

	@Resource(name = "chineseAddressFacade")
	private AddressFacade chineseAddressFacade;

	@Resource(name = "chineseUnitAddressHandler")
	private ChineseUnitAddressHandler chineseUnitAddressHandler;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "checkoutFacade")
	protected CheckoutFacade checkoutFacade;

	@Resource(name = "b2bUnitFacade")
	protected B2BUnitFacade b2bUnitFacade;

	@Resource(name = "addressDataUtil")
	private AddressDataUtil addressDataUtil;

	@Resource(name = "pageTitleResolver")
	private PageTitleResolver pageTitleResolver;

	@Resource(name = "chinesecommerceorgaddressaddonCompanyBreadcrumbBuilder")
	protected ChinesecommerceorgaddressaddonCompanyBreadcrumbBuilder chinesecommerceorgaddressaddonCompanyBreadcrumbBuilder;

	/**
	 * @return the i18NFacade
	 */
	public I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
	    binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@RequestMapping(value = "/addressform", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getCountryAddressForm(@RequestParam(value = "addressCode", required = false) final String addressCode,
			@RequestParam(value = "unit", required = false) final String unit,
			@RequestParam("countryIsoCode") final String countryIsoCode, final Model model)
	{
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getCountries(CountryType.SHIPPING));
		model.addAttribute(COUNTRY_ATTR, countryIsoCode);
		model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(countryIsoCode));
		model.addAttribute(TITLE_DATA_ATTR, getUserFacade().getTitles());

		if (StringUtils.isEmpty(unit)) {
			final ChineseUnitAddressForm chineseAddressForm = new ChineseUnitAddressForm();
			chineseAddressForm.setCountryIso(countryIsoCode);
			model.addAttribute(ADDRESS_FORM_ATTR, chineseAddressForm);

			return ChinesecommerceorgaddressaddonControllerConstants.Views.Fragments.Account.CountryAddressForm;
		}
		
		final AddressForm addressForm = new AddressForm();
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(unit);
		if (Objects.nonNull(unitData) && CollectionUtils.isNotEmpty(unitData.getAddresses())) {
			for (final AddressData addressData : unitData.getAddresses()) {
				if (addressData.getId() != null && addressData.getId().equals(addressCode)
						&& countryIsoCode.equals(addressData.getCountry().getIsocode())) {
					model.addAttribute(ADDRESS_DATA_ATTR, addressData);
					addressDataUtil.convert(addressData, addressForm);
					break;
				}
			}
		}

		final ChineseUnitAddressForm chineseAddressForm = new ChineseUnitAddressForm();
		BeanUtils.copyProperties(addressForm, chineseAddressForm);
		model.addAttribute(ADDRESS_FORM_ATTR, chineseAddressForm);
		chineseUnitAddressHandler.prepareAddressForm(model, chineseAddressForm);
		
		return ChinesecommerceorgaddressaddonControllerConstants.Views.Fragments.Account.CountryAddressForm;
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.POST, params = "countryIso=" + WebConstants.CHINA_ISOCODE)
	@RequireHardLogIn
	public String addAddress(@RequestParam("unit") final String unit,
			@ModelAttribute("addressForm") final ChineseUnitAddressForm addressForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		chineseUnitAddressHandler.validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			return prepareModelAndViewAfterError(model, addressForm, unit, null);
		}
		final AddressData newAddress = chineseUnitAddressHandler.prepareChineseUnitAddressData(addressForm);
			b2bUnitFacade.addAddressToUnit(newAddress, unit);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added");
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/edit-address", method = RequestMethod.POST, params = "countryIso=" + WebConstants.CHINA_ISOCODE)
	@RequireHardLogIn
	public String editAddress(@RequestParam("unit") final String unit, @RequestParam("addressId") final String addressId,
			@ModelAttribute("addressForm") final ChineseUnitAddressForm addressForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		chineseUnitAddressHandler.validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			return prepareModelAndViewAfterError(model, addressForm, unit, addressId);
		}
		final AddressData newAddress = chineseUnitAddressHandler.prepareChineseUnitAddressData(addressForm);
			b2bUnitFacade.editAddressOfUnit(newAddress, unit);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"account.confirmation.address.updated");
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/edit-address", method =
	{ RequestMethod.GET }, params = "countryIso=" + WebConstants.CHINA_ISOCODE)
	@RequireHardLogIn
	public String editAddress(@RequestParam("unit") final String unit, @RequestParam("addressId") final String addressId,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final AddressForm addressForm = new AddressForm();
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getCountries(CountryType.SHIPPING));
		model.addAttribute(TITLE_DATA_ATTR, getUserFacade().getTitles());
		model.addAttribute(ADDRESS_FORM_ATTR, addressForm);
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(unit);
		if (unit != null)
		{
			for (final AddressData addressData : unitData.getAddresses())
			{
				if (addressData.getId() != null && addressData.getId().equals(addressId))
				{
					model.addAttribute(ADDRESS_DATA_ATTR, addressData);
					addressDataUtil.convertBasic(addressData, addressForm);
					break;
				}
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "b2bunit.notfound");
		}
		final ContentPageModel manageUnitsPage = getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE);
		storeCmsPageInModel(model, manageUnitsPage);
		setUpMetaDataForContentPage(model, manageUnitsPage);
		final List<Breadcrumb> breadcrumbs = chinesecommerceorgaddressaddonCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs
				.add(new Breadcrumb(
						String.format("/my-company/organization-management/manage-units/edit-address/?unit=%s&addressId=%s",
								urlEncode(unit), urlEncode(addressId)),
						getMessageSource().getMessage("text.company.manage.units.editAddress", new Object[]
		{ unit }, "Edit Address for {0} Business Unit ", getI18nService().getCurrentLocale()), null));

		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		model.addAttribute(UNIT_UID_ATTR, unit);
		model.addAttribute(UNIT_NAME_ATTR, unitData.getName());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		final AddressData addressData = (AddressData) model.asMap().get(ADDRESS_DATA_ATTR);
		if (addressData != null)
		{
			addressForm.setPhone(addressData.getPhone());
		}
		final ChineseUnitAddressForm chineseAddressForm = new ChineseUnitAddressForm();
		BeanUtils.copyProperties(addressForm, chineseAddressForm);

		model.addAttribute(COUNTRY_ATTR, chineseAddressForm.getCountryIso());
		model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(chineseAddressForm.getCountryIso()));
		model.addAttribute(ADDRESS_FORM_ATTR, chineseAddressForm);

		chineseUnitAddressHandler.prepareAddressForm(model, chineseAddressForm);
		return ChinesecommerceorgaddressaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitAddAddressPage;
	}

	@RequestMapping(value = "/formataddress", method = RequestMethod.GET)
	public String getAddressData(@RequestParam("unit") final String unit,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(unit);
		model.addAttribute(UNIT_DATA_ATTR, unitData);
		return ChinesecommerceorgaddressaddonControllerConstants.Views.Fragments.Account.ChineseAddressForm;
	}

	protected String prepareModelAndViewAfterError(final Model model, final ChineseUnitAddressForm addressForm, final String unit,
			final String addressId) throws CMSItemNotFoundException
	{
		model.addAttribute(COUNTRY_ATTR, addressForm.getCountryIso());
		model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getCountries(CountryType.SHIPPING));
		model.addAttribute(TITLE_DATA_ATTR, getUserFacade().getTitles());
		model.addAttribute(ADDRESS_FORM_ATTR, addressForm);

		if (addressForm.getRegionIso() != null)
		{
			model.addAttribute(CITIES_ATTR, chineseAddressFacade.getCitiesForRegion(addressForm.getRegionIso()));
		}
		if (addressForm.getCityIso() != null)
		{
			model.addAttribute(DISTRICTS_ATTR, chineseAddressFacade.getDistrictsForCity(addressForm.getCityIso()));
		}

		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(unit);
		if (unitData != null && unitData.getAddresses() != null)
		{
			final List<AddressData> unitAddresses = unitData.getAddresses();
			for (final AddressData addressData : unitAddresses)
			{
				if (addressId != null && addressData.getId() != null && addressId.equals(addressData.getId()))
				{
					model.addAttribute(ADDRESS_DATA_ATTR, addressData);
				}
			}
			model.addAttribute(UNIT_NAME_ATTR, unitData.getName());
		}

		final List<Breadcrumb> breadcrumbs = chinesecommerceorgaddressaddonCompanyBreadcrumbBuilder
				.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(
				String.format("/my-company/organization-management/manage-units/add-address/?unit=%s", urlEncode(unit)),
				getMessageSource().getMessage("text.company.manage.units.addAddress", new Object[]
				{ unit }, "Add Address to {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		GlobalMessages.addErrorMessage(model, "form.global.error");
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		model.addAttribute(UNIT_UID_ATTR, unit);
		return ChinesecommerceorgaddressaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitAddAddressPage;
	}
}
