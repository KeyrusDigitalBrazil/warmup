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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cms2.servicelayer.daos.CMSMediaFormatDao;
import de.hybris.platform.cmsfacades.util.builder.MediaFormatModelBuilder;
import de.hybris.platform.core.model.media.MediaFormatModel;


public class MediaFormatModelMother extends AbstractModelMother<MediaFormatModel>
{
	public static final String WIDESCREEN = "widescreen";
	public static final String DESKTOP = "desktop";
	public static final String TABLET = "tablet";
	public static final String MOBILE = "mobile";

	private CMSMediaFormatDao mediaFormatDao;

	public MediaFormatModel createWidescreenFormat()
	{
		return createMediaFormatWithQualifier(WIDESCREEN);
	}

	public MediaFormatModel createDesktopFormat()
	{
		return createMediaFormatWithQualifier(DESKTOP);
	}

	public MediaFormatModel createTabletFormat()
	{
		return createMediaFormatWithQualifier(TABLET);
	}

	public MediaFormatModel createMobileFormat()
	{
		return createMediaFormatWithQualifier(MOBILE);
	}

	protected MediaFormatModel createMediaFormatWithQualifier(final String qualifier)
	{
		return getOrSaveAndReturn(() -> getMediaFormatDao().getMediaFormatByQualifier(qualifier), () -> {
			return MediaFormatModelBuilder.aModel() //
					.withQualifier(qualifier).build();
		});
	}

	public CMSMediaFormatDao getMediaFormatDao()
	{
		return mediaFormatDao;
	}

	public void setMediaFormatDao(final CMSMediaFormatDao mediaFormatDao)
	{
		this.mediaFormatDao = mediaFormatDao;
	}
}
