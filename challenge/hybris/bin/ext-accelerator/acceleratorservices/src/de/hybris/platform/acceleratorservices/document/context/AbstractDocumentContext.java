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
package de.hybris.platform.acceleratorservices.document.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * The document velocity context.
 */
public abstract class AbstractDocumentContext<T extends BusinessProcessModel> extends AbstractHybrisVelocityContext
{
	public static final String DOCUMENT_LANGUAGE = "document_language";

	public void init(final T businessProcessModel, final DocumentPageModel documentPageModel)
	{
		super.setBaseSite(getSite(businessProcessModel));
		super.init(businessProcessModel,documentPageModel);

		final LanguageModel language = getDocumentLanguage(businessProcessModel);
		if (language != null)
		{
			put(DOCUMENT_LANGUAGE, language);
		}
	}

	public LanguageModel getDocumentLanguage()
	{
		return (LanguageModel) get(DOCUMENT_LANGUAGE);
	}

	protected abstract BaseSiteModel getSite(final T businessProcessModel);
	protected abstract LanguageModel getDocumentLanguage(final T businessProcessModel);

}

