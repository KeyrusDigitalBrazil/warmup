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
package de.hybris.platform.cmsfacades.pages.service;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;


/**
 * performs the necessary initialization that a newly created {@code AbstractPageModel} may require before saving.
 */
public interface PageInitializer
{
	/**
	 * Perform any necessary initialization onto a newly created page such as associating non shared slots
	 *
	 * @param page - the instance of {@code AbstractPageModel}
	 * @return the modified page
	 */
	AbstractPageModel initialize(AbstractPageModel page);
}
