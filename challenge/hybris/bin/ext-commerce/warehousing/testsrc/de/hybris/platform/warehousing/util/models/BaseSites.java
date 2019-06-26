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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.site.dao.BaseSiteDao;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.warehousing.util.builder.BaseSiteModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class BaseSites extends AbstractItems<BaseSiteModel>
{
	public static final String UID_AMERICAS = "americas";

	private BaseSiteDao baseSiteDao;
	private BaseStores baseStores;

	public BaseSiteModel Americas()
	{
		return getOrSaveAndReturn(() -> getBaseSiteDao().findBaseSiteByUID(UID_AMERICAS), 
				() -> BaseSiteModelBuilder.aModel() 
						.withUid(UID_AMERICAS) 
						.withChannel(SiteChannel.B2C) 
						.withStores(getBaseStores().NorthAmerica()) 
						.build());
	}

	public BaseSiteDao getBaseSiteDao()
	{
		return baseSiteDao;
	}

	@Required
	public void setBaseSiteDao(final BaseSiteDao baseSiteDao)
	{
		this.baseSiteDao = baseSiteDao;
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

}
