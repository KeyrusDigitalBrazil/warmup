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

import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.smarteditwebservices.data.SmarteditLanguageListData;
import de.hybris.platform.smarteditwebservices.data.TranslationMapData;
import de.hybris.platform.smarteditwebservices.i18n.facade.SmarteditI18nFacade;

import java.util.Locale;

import javax.annotation.Resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * Controller to retrieve cms management internationalization data
 */
@Controller
@RequestMapping(API_VERSION + "/i18n")
@PreAuthorize("permitAll")
@Api(tags = "languages")
public class I18nController
{
	@Resource
	private SmarteditI18nFacade smarteEditI18nFacade;

	@RequestMapping(value = "/translations/{locale}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get a Translation Map", notes = "Endpoint to retrieve translated data using the specified locale value")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "locale", value = "Locale identifier consisting of a language and region", required = true, dataType = "string", paramType = "path")
	})
	public TranslationMapData getTranslationMap(@PathVariable("locale") final Locale locale)
	{
		final TranslationMapData translationMapData = new TranslationMapData();
		translationMapData.setValue(getSmartEditI18nFacade().getTranslationMap(locale));
		return translationMapData;
	}

	@RequestMapping(value = "/languages", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get Languages", notes = "Endpoint to retrieve list of supported languages")
	public SmarteditLanguageListData getToolingLanguages()
	{
		final SmarteditLanguageListData result = new SmarteditLanguageListData();
		result.setLanguages(getSmartEditI18nFacade().getSupportedLanguages());
		return result;
	}

	protected SmarteditI18nFacade getSmartEditI18nFacade()
	{
		return smarteEditI18nFacade;
	}

	public void setSmartEditI18nFacade(final SmarteditI18nFacade cmsI18nFacade)
	{
		this.smarteEditI18nFacade = cmsI18nFacade;
	}
}
