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
package de.hybris.platform.acceleratorservices.document.factory;

import de.hybris.platform.acceleratorservices.document.context.AbstractDocumentContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Interface used to create the velocity context for rendering documents.
 */
public interface DocumentContextFactory<T extends BusinessProcessModel>
{
	/**
	 * Create the velocity context for rendering a document.
	 *
	 * @param businessProcessModel
	 * 		the {@link BusinessProcessModel}
	 * @param documentPageModel
	 * 		the {@link DocumentPageModel}
	 * @param renderTemplate
	 * 		the {@link RendererTemplateModel}
	 * @return the velocity context
	 */
	AbstractDocumentContext<BusinessProcessModel> create(T businessProcessModel, DocumentPageModel documentPageModel,
			RendererTemplateModel renderTemplate);
}
