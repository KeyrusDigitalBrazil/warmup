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
package de.hybris.platform.samlsinglesignon.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.collect.Lists;


@UnitTest
public class DefaultSAMLUtilTest extends ServicelayerTest
{
	private static final String SSO_USERID_KEY = "userIdKey";
	private static final String SSO_FIRSTNAME_KEY = "firstName";
	private static final String SSO_LASTNAME_KEY = "lastName";
	private static final String SSO_CUSTOM_KEY = "custom";
	private static final String SSO_LANGUAGE_KEY = "language";

	private SAMLCredential credential;
	private HttpServletRequest httpServletRequest;
	private CommonI18NService commonI18NService;
	private LanguageModel plLanguageModel;
	private LanguageModel deLanguageModel;
	private LanguageModel frLanguageModel;
	final Locale plLocale = new Locale("pl");
	final Locale deLocale = new Locale("de");
	final Locale frLocale = new Locale("fr");

	private final PropertyConfigSwitcher ssoUserIdSwitcher = new PropertyConfigSwitcher(SAMLUtil.SSO_USERID_KEY);
	private final PropertyConfigSwitcher ssoUserFirstNameSwitcher = new PropertyConfigSwitcher(SAMLUtil.SSO_FIRSTNAME_KEY);
	private final PropertyConfigSwitcher ssoUserLastNameSwitcher = new PropertyConfigSwitcher(SAMLUtil.SSO_LASTNAME_KEY);
	private final PropertyConfigSwitcher ssoUserLanguageSwitcher = new PropertyConfigSwitcher(SAMLUtil.SSO_LANGUAGE_KEY);

	@Before
	public void setup()
	{
		credential = Mockito.mock(SAMLCredential.class);
		httpServletRequest = Mockito.mock(HttpServletRequest.class);
		commonI18NService = Mockito.mock(CommonI18NService.class);

		plLanguageModel = Mockito.mock(LanguageModel.class);
		Mockito.when(plLanguageModel.getIsocode()).thenReturn("pl");
		deLanguageModel = Mockito.mock(LanguageModel.class);
		Mockito.when(deLanguageModel.getIsocode()).thenReturn("de");
		frLanguageModel = Mockito.mock(LanguageModel.class);
		Mockito.when(frLanguageModel.getIsocode()).thenReturn("fr");

		Mockito.when(commonI18NService.getAllLanguages())
				.thenReturn(Lists.newArrayList(plLanguageModel, deLanguageModel, frLanguageModel));
		Mockito.when(commonI18NService.getLocaleForIsoCode("pl")).thenReturn(plLocale);
		Mockito.when(commonI18NService.getLocaleForIsoCode("de")).thenReturn(deLocale);
		Mockito.when(commonI18NService.getLocaleForIsoCode("fr")).thenReturn(frLocale);
		Mockito.when(httpServletRequest.getHeader("accept-language")).thenReturn("nonempty string");

		ssoUserIdSwitcher.switchToValue(SSO_USERID_KEY);
		ssoUserFirstNameSwitcher.switchToValue(SSO_FIRSTNAME_KEY);
		ssoUserLastNameSwitcher.switchToValue(SSO_LASTNAME_KEY);
		ssoUserLanguageSwitcher.switchToValue(SSO_LANGUAGE_KEY);
	}

	@After
	public void tearDown()
	{
		ssoUserIdSwitcher.switchBackToDefault();
		ssoUserFirstNameSwitcher.switchBackToDefault();
		ssoUserLastNameSwitcher.switchBackToDefault();
		ssoUserLanguageSwitcher.switchBackToDefault();
	}

	@Test
	public void shouldGetUserId()
	{
		// given
		Mockito.when(credential.getAttributeAsString(SSO_USERID_KEY)).thenReturn("id");

		// when
		final String userId = SAMLUtil.getUserId(credential);

		// then
		assertThat(userId).isEqualTo("id");
	}

	@Test
	public void shouldReturnEmptyForGetUserIdWhenNoSSOKey()
	{
		// given
		Mockito.when(credential.getAttributeAsString(SSO_USERID_KEY)).thenReturn(null);

		// when
		final String userId = SAMLUtil.getUserId(credential);

		// then
		assertThat(userId).isEmpty();
	}

	@Test
	public void shouldGetUserName()
	{
		// given
		Mockito.when(credential.getAttributeAsString(SSO_FIRSTNAME_KEY)).thenReturn("first");
		Mockito.when(credential.getAttributeAsString(SSO_LASTNAME_KEY)).thenReturn("last");

		// when
		final String userName = SAMLUtil.getUserName(credential);

		// then
		assertThat(userName).isEqualTo("first last");
	}

	@Test
	public void shouldGetCustomAttribute()
	{
		// given
		Mockito.when(credential.getAttributeAsString(SSO_CUSTOM_KEY)).thenReturn("custom_value");

		// when
		final String customAttribute = SAMLUtil.getCustomAttribute(credential, SSO_CUSTOM_KEY);

		// then
		assertThat(customAttribute).isEqualTo("custom_value");
	}

	@Test
	public void shouldGetCustomAttributesList()
	{
		// given
		Mockito.when(credential.getAttributeAsStringArray(SSO_CUSTOM_KEY)).thenReturn(new String[]
		{ "custom_value1", "custom_value2" });

		// when
		final List<String> customAttributes = SAMLUtil.getCustomAttributes(credential, SSO_CUSTOM_KEY);

		// then
		assertThat(customAttributes).containsOnly("custom_value1", "custom_value2");
	}

	@Test
	public void shouldGetGermanLanguageFromCredential()
	{
		//given
		Mockito.when(credential.getAttributeAsString(SSO_LANGUAGE_KEY)).thenReturn("de");

		//when
		final String language = SAMLUtil.getLanguage(credential, httpServletRequest, commonI18NService);

		//then
		assertThat(language).isEqualTo("de");
	}

	@Test
	public void shouldGetFrenchLanguageFromHttpRequest()
	{
		//given
		Mockito.when(credential.getAttributeAsString(SSO_LANGUAGE_KEY)).thenReturn("ar");
		Mockito.when(httpServletRequest.getLocale()).thenReturn(frLocale);

		//when
		final String language = SAMLUtil.getLanguage(credential, httpServletRequest, commonI18NService);

		//then
		assertThat(language).isEqualTo("fr");
	}

	@Test
	public void shouldGetPolishLanguageAsDefaultLanguage()
	{
		//given
		Mockito.when(credential.getAttributeAsString(SSO_LANGUAGE_KEY)).thenReturn("");
		final Locale czLocale = new Locale("cz");
		Mockito.when(httpServletRequest.getLocale()).thenReturn(czLocale);
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(plLanguageModel);

		//when
		final String language = SAMLUtil.getLanguage(credential, httpServletRequest, commonI18NService);

		//then
		assertThat(language).isEqualTo("pl");
	}

	@Test
	public void shouldGetPolishLanguageAsDefaultLanguageWhenLocaleIsNotAvailable()
	{
		//given
		Mockito.when(credential.getAttributeAsString(SSO_LANGUAGE_KEY)).thenReturn("");
		final Locale czLocale = new Locale("cz");
		Mockito.when(httpServletRequest.getLocale()).thenReturn(czLocale);
		Mockito.when(commonI18NService.getLocaleForIsoCode("cz")).thenReturn(czLocale);
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(plLanguageModel);

		//when
		final String language = SAMLUtil.getLanguage(credential, httpServletRequest, commonI18NService);

		//then
		assertThat(language).isEqualTo("pl");
	}

	@Test
	public void shouldGetPolishLanguageAsDefaultLanguageWhenRequestHeaderLocaleIsNull()
	{
		//given
		Mockito.when(credential.getAttributeAsString(SSO_LANGUAGE_KEY)).thenReturn("");
		Mockito.when(httpServletRequest.getHeader("accept-language")).thenReturn(null);
		Mockito.when(httpServletRequest.getLocale()).thenReturn(deLocale);
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(plLanguageModel);

		//when
		final String language = SAMLUtil.getLanguage(credential, httpServletRequest, commonI18NService);

		//then
		assertThat(language).isEqualTo("pl");
	}

}
