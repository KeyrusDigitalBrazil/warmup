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
package de.hybris.platform.cmswebservices.languages.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmswebservices.data.LanguageListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to deal with languages.
 *
 * @pathparam siteId Identifier for a configured site.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/languages")
public class LanguageController
{
	@Resource
	private LanguageFacade languageFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get languages", notes = "Retrieves all languages supported by a storefront.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "List of languages.", response = LanguageListData.class)
	})
	public LanguageListData getLanguages()
	{
		final LanguageListData languageList = new LanguageListData();
		languageList.setLanguages(getLanguageFacade().getLanguages());
		return languageList;
	}

	protected LanguageFacade getLanguageFacade()
	{
		return languageFacade;
	}

	public void setLanguageFacade(final LanguageFacade languageFacade)
	{
		this.languageFacade = languageFacade;
	}

}
