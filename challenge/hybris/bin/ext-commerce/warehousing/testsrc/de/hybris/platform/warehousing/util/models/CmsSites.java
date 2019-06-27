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

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSSiteDao;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.warehousing.util.builder.CmsSiteModelBuilder;
import de.hybris.platform.warehousing.util.builder.PromotionGroupBuilder;

import java.util.List;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Required;


public class CmsSites extends AbstractItems<CMSSiteModel>
{
	public static final String UID_CANADA = "canada";

	private CMSSiteDao cmsSiteDao;
	private BaseStores baseStores;
	private ContentCatalogs contentCatalogs;

	public CMSSiteModel Canada()
	{
		final List<CMSSiteModel> cmsSitesList = getCMSSiteDao().findCMSSitesById(UID_CANADA);
		return getOrSaveAndReturn(() -> cmsSitesList.isEmpty() ? null : cmsSitesList.get(0),
				() -> CmsSiteModelBuilder.aModel().withUid(UID_CANADA).withChannel(SiteChannel.B2C)
						.withStores(getBaseStores().NorthAmerica())
						.withDefaultPromotionGroup(PromotionGroupBuilder.aModel().withIdentifier("").build())
						.withContentCatalogs(Lists.newArrayList(getContentCatalogs().contentCatalog_online())).build());
	}

	public CMSSiteDao getCMSSiteDao()
	{
		return cmsSiteDao;
	}

	@Required
	public void setCMSSiteDao(final CMSSiteDao cmsSiteDao)
	{
		this.cmsSiteDao = cmsSiteDao;
	}

	public BaseStores getBaseStores()
	{
		return baseStores;
	}

	@Required
	public void setBaseStores(final BaseStores baseStores)
	{
		this.baseStores = baseStores;
	}

	public ContentCatalogs getContentCatalogs()
	{
		return contentCatalogs;
	}

	@Required
	public void setContentCatalogs(final ContentCatalogs contentCatalogs)
	{
		this.contentCatalogs = contentCatalogs;
	}

}
