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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.store.BaseStoreModel;

import com.google.common.collect.Lists;


public class BaseSiteModelBuilder
{
	private final BaseSiteModel model;

	private BaseSiteModelBuilder()
	{
		model = new BaseSiteModel();
	}

	private BaseSiteModel getModel()
	{
		return this.model;
	}

	public static BaseSiteModelBuilder aModel()
	{
		return new BaseSiteModelBuilder();
	}

	public BaseSiteModel build()
	{
		return getModel();
	}

	public BaseSiteModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public BaseSiteModelBuilder withChannel(final SiteChannel channel)
	{
		getModel().setChannel(channel);
		return this;
	}

	public BaseSiteModelBuilder withStores(final BaseStoreModel... stores)
	{
		getModel().setStores(Lists.newArrayList(stores));
		return this;
	}

}
