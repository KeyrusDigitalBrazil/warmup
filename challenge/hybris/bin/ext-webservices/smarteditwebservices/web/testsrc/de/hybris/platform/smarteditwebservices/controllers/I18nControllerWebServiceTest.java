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
package de.hybris.platform.smarteditwebservices.controllers;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertOk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants;
import de.hybris.platform.smarteditwebservices.data.SmarteditLanguageListData;
import de.hybris.platform.smarteditwebservices.data.TranslationMapData;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.localization.TypeLocalization;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;


@NeedsEmbeddedServer(webExtensions =
{ SmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class I18nControllerWebServiceTest extends ServicelayerTest
{
	private WsRequestBuilder wsRequestBuilder;

	private static final String GET_LANGUAGES = "/v1/i18n/languages";

	private final String L10N_KEY = "smarteditwebservices.key.dummy";
	private final String L10N_VALUE = "value.dummy";

	private static final String URI_EN = "/v1/i18n/translations/en_US";
	private static final String URI_EN_DASH = "/v1/i18n/translations/en-US";
	private static final String URI_DE = "/v1/i18n/translations/de";
	private static final String URI_FR = "/v1/i18n/translations/fr";
	private static final String URI_LATIN = "/v1/i18n/translations/latin";
	private static final String URI_NONEXISTENT = "v1/i18n/translations/nonexistent";

	@Resource
	private ModelService modelService;

	@Before
	public void setup()
	{
		wsRequestBuilder = new WsRequestBuilder().extensionName(SmarteditwebservicesConstants.EXTENSIONNAME);
	}

	@Before
	public void setUpLanguages()
	{

		//#### English is set by default

		final LanguageModel de = modelService.create(LanguageModel.class);
		de.setIsocode(Locale.GERMAN.getLanguage());

		final LanguageModel fr = modelService.create(LanguageModel.class);
		fr.setIsocode(Locale.FRENCH.getLanguage());
		fr.setFallbackLanguages(Lists.newArrayList(de));

		modelService.saveAll(de, fr);

		//Remove dummy value from french language so the fallback languages can be tested
		final Properties properties = new Properties();
		properties.put(L10N_KEY, "");
		TypeLocalization.getInstance().getLocalizations().put(HybrisJUnit4TransactionalTest.getOrCreateLanguage(Locale.FRENCH.getLanguage()), properties);
	}

	@Test
	public void shouldGetEnglishTranslations()
	{
		final Response response = wsRequestBuilder//
				.path(URI_EN)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertTranslation(response, L10N_VALUE);
	}

	@Test
	public void shouldFailWhenGetTranslationForLangWithDash()
	{
		final Response response = wsRequestBuilder//
				.path(URI_EN_DASH)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void shouldGetGermanTranslations()
	{
		final Response response = wsRequestBuilder//
				.path(URI_DE)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertTranslation(response, "Wert.Dummy");
	}

	@Test
	public void shouldGetFallackTranslations()
	{
		final Response response = wsRequestBuilder//
				.path(URI_FR)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertTranslation(response, "Wert.Dummy");
	}

	@Test
	public void will_get_the_supported_languages()
	{
		final Response response = wsRequestBuilder//
				.path(GET_LANGUAGES) //
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.readEntity(SmarteditLanguageListData.class).getLanguages().size(), is(3));
		assertThat(response.getStatus(), is(HttpStatus.OK.value()));
	}

	@Test
	public void will_get_default_locale_when_no_fallback_available()
	{
		final Response response = wsRequestBuilder//
				.path(URI_LATIN)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus(), is(HttpStatus.OK.value()));
		final TranslationMapData entity = response.readEntity(TranslationMapData.class);
		assertThat(entity.getValue().get(L10N_KEY), is(L10N_VALUE));
	}

	@Test
	public void will_retrieve_default_for_non_existent_locale()
	{
		final Response response = wsRequestBuilder//
				.path(URI_NONEXISTENT)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus(), is(HttpStatus.OK.value()));
	}

	protected void assertTranslation(final Response response, final String value)
	{
		assertOk(response, Boolean.FALSE.booleanValue());
		final TranslationMapData entity = response.readEntity(TranslationMapData.class);
		assertEquals(value, entity.getValue().get(L10N_KEY));
	}
}
