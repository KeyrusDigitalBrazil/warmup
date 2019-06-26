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
package de.hybris.platform.acceleratorservices.document.service;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Service for generating a document.
 */
public interface DocumentGenerationService
{
	/**
	 * Generates {@link MediaModel} from given business process and document page
	 *
	 * @param frontendTemplateName
	 * 		the code of the template to use for script generation
	 * @param businessProcessModel
	 * 		Business process object
	 * @return the {@link MediaModel}
	 */
	MediaModel generate(final String frontendTemplateName, final BusinessProcessModel businessProcessModel);
}
