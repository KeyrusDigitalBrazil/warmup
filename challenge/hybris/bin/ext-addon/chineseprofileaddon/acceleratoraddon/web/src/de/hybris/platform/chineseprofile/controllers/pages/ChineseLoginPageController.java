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
package de.hybris.platform.chineseprofile.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.consent.data.ConsentCookieData;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ConsentForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.chineseprofile.controllers.pages.imported.LoginPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;


/**
 * Chinese Login Controller. Handles login and register for the account flow.
 */
@Scope("tenant")
@RequestMapping(value = "/login")
public class ChineseLoginPageController extends LoginPageController
{

	private static final Logger LOGGER = Logger.getLogger(ChineseLoginPageController.class);

	private static final String CONSENT_FORM_GLOBAL_ERROR = "consent.form.global.error";

	private static final String FORM_GLOBAL_ERROR = "form.global.error";
	private static final String MOBILE_BIND_URL = "/mobile/register/bind";
	private static final String MOBILE_REBIND_URL = "/mobile/rebind";

	@Resource(name="httpSessionRequestCache")
	private HttpSessionRequestCache httpSessionRequestCache;


	/**
	 * This method takes data from the registration form and create a new customer account and attempts to log in using
	 * the credentials of this new user.
	 *
	 * @return true if there are no binding errors or the account does not already exists.
	 * @throws CMSItemNotFoundException
	 */
	@Override
	protected String processRegisterUserRequest(final String referer, final RegisterForm form, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute(form);
			model.addAttribute(new LoginForm());
			model.addAttribute(new GuestForm());
			GlobalMessages.addErrorMessage(model, FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}

		final RegisterData data = new RegisterData();
		data.setFirstName(form.getFirstName());
		data.setLastName(form.getLastName());
		data.setLogin(form.getEmail());
		data.setPassword(form.getPwd());
		data.setTitleCode(form.getTitleCode());
		try
		{
			getCustomerFacade().register(data);
			getAutoLoginStrategy().login(form.getEmail().toLowerCase(), form.getPwd(), request, response);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"registration.confirmation.message.title");
		}
		catch (final DuplicateUidException e)
		{
			LOGGER.warn("registration failed: " + e);
			model.addAttribute(form);
			model.addAttribute(new LoginForm());
			model.addAttribute(new GuestForm());
			bindingResult.rejectValue("email", "registration.error.account.exists.title");
			GlobalMessages.addErrorMessage(model, FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		// Consent form data
		try
		{
			final ConsentForm consentForm = form.getConsentForm();
			if (consentForm != null && consentForm.getConsentGiven())
			{
				getConsentFacade().giveConsent(consentForm.getConsentTemplateId(), consentForm.getConsentTemplateVersion());
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Error occurred while creating consents during registration", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, CONSENT_FORM_GLOBAL_ERROR);
		}

		// save anonymous-consent cookies as ConsentData
		final Cookie cookie = WebUtils.getCookie(request, WebConstants.ANONYMOUS_CONSENT_COOKIE);
		if (cookie != null)
		{
			try
			{
				final ObjectMapper mapper = new ObjectMapper();
				final List<ConsentCookieData> consentCookieDataList = Arrays.asList(mapper.readValue(
						URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.displayName()), ConsentCookieData[].class));
				consentCookieDataList.stream().filter(consentData -> WebConstants.CONSENT_GIVEN.equals(consentData.getConsentState()))
						.forEach(consentData -> consentFacade.giveConsent(consentData.getTemplateCode(),
								Integer.valueOf(consentData.getTemplateVersion())));
			}
			catch (final UnsupportedEncodingException e)
			{
				//we do nothing here and just log the error when catching the exception
				LOGGER.error(String.format("Cookie Data could not be decoded : %s", cookie.getValue()));
				LOGGER.error(e);
			}
			catch (final IOException e)
			{
				//we do nothing here and just log the error when catching the exception
				LOGGER.error("Cookie Data could not be mapped into the Object");
				LOGGER.error(e);
			}
			catch (final Exception e)
			{
				//we do nothing here and just log the error when catching the exception
				LOGGER.error("Error occurred while creating Anonymous cookie consents");
				LOGGER.error(e);
			}
		}

		customerConsentDataStrategy.populateCustomerConsentDataInSession();

		return REDIRECT_PREFIX + MOBILE_BIND_URL;
	}

	@Override
	protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response)
	{
		if (StringUtils.isNotBlank(referer) && validatateRefererAvalible(referer)
				&& StringUtils.contains(referer, request.getServerName()))
		{
			httpSessionRequestCache.saveRequest(request, response);
		}
	}

	protected boolean validatateRefererAvalible(final String referer)
	{
		return !StringUtils.endsWith(referer, "/login") && !StringUtils.endsWith(referer, MOBILE_REBIND_URL)
				&& !StringUtils.endsWith(referer, MOBILE_BIND_URL);
	}
}
