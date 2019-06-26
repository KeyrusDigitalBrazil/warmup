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
package de.hybris.platform.chineseprofile.controllers.pages.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.chineseprofile.constants.WebConstants;
import de.hybris.platform.chineseprofile.controllers.ControllerConstants;
import de.hybris.platform.chineseprofilefacades.customer.ChineseCustomerFacade;
import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * This controller show verification code page.
 */
@Controller
@RequestMapping("/verification-code")
public class VerificationCodeController extends AbstractPageController
{

	private static final String VERIFICATION_CODE_PAGE_URL = "/verification-code";

	private static final Pattern MOBILE_NUMBER_REGEX = Pattern.compile("^1(3|5|7|8)(\\d{9})$");

	@Resource(name = "chineseCustomerFacade")
	private ChineseCustomerFacade customerFacade;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Resource(name = "commerceCommonI18NService")
	private CommerceCommonI18NService commerceCommonI18NService;


	@RequestMapping
	public String getVerificationCode(final Model model)
	{

		final VerificationData data = getSessionService().getAttribute(WebConstants.VERIFICATION_CODE_FOR_MOBILE_BINDING);
		model.addAttribute("verificationData", data);

		return ControllerConstants.Views.Pages.Account.VerificationCodeMockPage;
	}

	@ResponseBody
	@RequestMapping(value = "/create-code", method = RequestMethod.GET)
	public String createVerifivationCode(final String mobileNumber)
	{

		final String verificationCode = customerFacade.generateVerificationCode();
		final VerificationData data = new VerificationData();
		data.setMobileNumber(mobileNumber);
		data.setVerificationCode(verificationCode);

		customerFacade.saveVerificationCodeInSession(data, WebConstants.VERIFICATION_CODE_FOR_MOBILE_BINDING);
		customerFacade.sendVerificationCode(data);

		return VERIFICATION_CODE_PAGE_URL;
	}

	@ResponseBody
	@RequestMapping(value = "/create-code", method = RequestMethod.GET, params = "flag=profile")
	public String createVerifivationCode()
	{

		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final String verificationCode = customerFacade.generateVerificationCode();
		final VerificationData data = new VerificationData();
		data.setVerificationCode(verificationCode);
		data.setMobileNumber(customerData.getMobileNumber());

		customerFacade.saveVerificationCodeInSession(data, WebConstants.VERIFICATION_CODE_FOR_MOBILE_BINDING);
		customerFacade.sendVerificationCode(data);

		return VERIFICATION_CODE_PAGE_URL;
	}

	@ResponseBody
	@RequestMapping(value = "/mobile/check", method = RequestMethod.GET)
	public String checkMobileNumber(final String mobileNumber)
	{
		if (StringUtils.isBlank(mobileNumber) || !MOBILE_NUMBER_REGEX.matcher(mobileNumber).matches())
		{
			return messageSource.getMessage("register.mobileNumber.invalid", new Object[] {},
					commerceCommonI18NService.getCurrentLocale());
		}

		if (!customerFacade.isMobileNumberUnique(mobileNumber))
		{
			return messageSource.getMessage("register.mobileNumber.registered", new Object[] {},
					commerceCommonI18NService.getCurrentLocale());
		}

		return "";
	}

}
