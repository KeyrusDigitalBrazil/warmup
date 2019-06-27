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
package de.hybris.platform.acceleratorservices.cartfileupload.events;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.media.MediaModel;


public class SavedCartFileUploadEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	private MediaModel fileMedia;

	public MediaModel getFileMedia()
	{
		return fileMedia;
	}

	public void setFileMedia(MediaModel fileMedia)
	{
		this.fileMedia = fileMedia;
	}

}
