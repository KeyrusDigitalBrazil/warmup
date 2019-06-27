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
package de.hybris.platform.addressservices.strategies.impl;

import de.hybris.platform.addressservices.strategies.NameWithTitleFormatStrategy;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class ChineseNameWithTitleFormatStrategy implements NameWithTitleFormatStrategy
{
	private UserService userService;

	private CommonI18NService commonI18NService;

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.addressfacades.strategies.NameWithTitleFormatStrategy#getFullnameWithTitle(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public String getFullnameWithTitle(final String fullname, final String title)
	{
		final String isocode = commonI18NService.getCurrentLanguage().getIsocode();
		return getFullnameWithTitleForISOCode(fullname, title, isocode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.addressfacades.strategies.NameWithTitleFormatStrategy#getFullnameWithTitle(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getFullnameWithTitle(final String firstname, final String lastname, final String title)
	{
		final String isocode = commonI18NService.getCurrentLanguage().getIsocode();
		return getFullnameWithTitleForISOCode(firstname, lastname, title, isocode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.addressfacades.strategies.NameWithTitleFormatStrategy#getFullnameWithTitle(java.lang.
	 * String, java.lang.String, de.hybris.platform.commercefacades.storesession.data.LanguageData)
	 */
	@Override
	public String getFullnameWithTitleForISOCode(final String fullname, final String title, final String isocode)
	{
		if (StringUtils.isEmpty(fullname))
		{
			return null;
		}
		final StringBuilder fullnameWithTitle = new StringBuilder();
		if (isocode.equals(Locale.CHINESE.getLanguage()) && isNotReverent(title, isocode))
		{
			fullnameWithTitle.append(fullname).append(" ").append(title);
		}
		else
		{
			fullnameWithTitle.append(title).append(" ").append(fullname);
		}
		return fullnameWithTitle.toString();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.addressfacades.strategies.NameWithTitleFormatStrategy#getFullnameWithTitle(java.lang.
	 * String, java.lang.String, java.lang.String, de.hybris.platform.commercefacades.storesession.data.LanguageData)
	 */
	@Override
	public String getFullnameWithTitleForISOCode(final String firstname, final String lastname, final String title,
			final String isocode)
	{
		if (StringUtils.isEmpty(firstname) && StringUtils.isEmpty(lastname))
		{
			return null;
		}

		final StringBuilder fullnameWithTitle = new StringBuilder();
		if (isocode.equals(Locale.CHINESE.getLanguage()) && isNotReverent(title, isocode))
		{
			if (containsChineseCharacter(firstname) || containsChineseCharacter(lastname))
			{
				fullnameWithTitle.append(lastname).append(" ").append(firstname).append(" ").append(title);
			}
			else
			{
				fullnameWithTitle.append(firstname).append(" ").append(lastname).append(" ").append(title);
			}
		}
		else
		{
			fullnameWithTitle.append(title).append(" ");
			if (containsChineseCharacter(firstname) || containsChineseCharacter(lastname))
			{
				fullnameWithTitle.append(lastname).append(" ").append(firstname);
			}
			else
			{
				fullnameWithTitle.append(firstname).append(" ").append(lastname);
			}
		}
		return fullnameWithTitle.toString();
	}

	protected boolean containsChineseCharacter(final String s)
	{
		if (StringUtils.isNotEmpty(s))
		{
			for (int i = 0, codepoint = 0; i < s.length(); codepoint = s.codePointAt(i),i+= Character.charCount(codepoint))
			{
				if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * if title is reverent, should be in the front of fullname
	 *
	 * @param isocode
	 * @return
	 */
	protected boolean isNotReverent(final String titleName, final String isocode)
	{
		final TitleModel title = userService.getTitleForCode("rev");
		final Locale currentLocale = new Locale(isocode);
		return !title.getName(currentLocale).equalsIgnoreCase(titleName);
	}
}
