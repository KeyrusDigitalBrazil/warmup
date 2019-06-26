/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.daos.LanguageDao;
import de.hybris.platform.warehousing.util.builder.LanguageModelBuilder;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class Languages extends AbstractItems<LanguageModel>
{
	public static final String ISOCODE_ENGLISH = "en";

	private LanguageDao languageDao;

	public LanguageModel English()
	{
		return getFromCollectionOrSaveAndReturn(() -> getLanguageDao().findLanguagesByCode(ISOCODE_ENGLISH), 
				() -> LanguageModelBuilder.aModel() 
						.withIsocode(ISOCODE_ENGLISH) 
						.withName("English", Locale.ENGLISH) 
						.withActive(Boolean.TRUE) 
						.build());
	}

	public LanguageDao getLanguageDao()
	{
		return languageDao;
	}

	@Required
	public void setLanguageDao(final LanguageDao languageDao)
	{
		this.languageDao = languageDao;
	}

}
