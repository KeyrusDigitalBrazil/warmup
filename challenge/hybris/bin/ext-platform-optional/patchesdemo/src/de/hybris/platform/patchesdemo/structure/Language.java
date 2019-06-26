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
package de.hybris.platform.patchesdemo.structure;

import de.hybris.platform.patches.organisation.ImportLanguage;


/**
 * Example of languages enumeration used in import process.
 */
public enum Language implements ImportLanguage
{

	EN_US("en_US"), EN_CA("en_CA"), FR_CA("fr_CA"), FR_FR("fr_FR"), DE_DE("de_DE");

	private String code;

	Language(final String code)
	{
		this.code = code;
	}

	@Override
	public String getCode()
	{
		return this.code;
	}

}
