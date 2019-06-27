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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;


public class NavigationNodeModelBuilder
{
	private final CMSNavigationNodeModel model;

	private NavigationNodeModelBuilder(final CMSNavigationNodeModel model)
	{
		this.model = model;
	}

	private NavigationNodeModelBuilder()
	{
		this.model = new CMSNavigationNodeModel();
	}

	public CMSNavigationNodeModel getModel()
	{
		return model;
	}

	public static NavigationNodeModelBuilder aModel()
	{
		return new NavigationNodeModelBuilder();
	}

	public static NavigationNodeModelBuilder fromModel(final CMSNavigationNodeModel model)
	{
		return new NavigationNodeModelBuilder(model);
	}

	public NavigationNodeModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public NavigationNodeModelBuilder withTitle(final String title, final Locale locale)
	{
		getModel().setTitle(title, locale);
		return this;
	}

	public NavigationNodeModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public NavigationNodeModelBuilder withParent(final CMSNavigationNodeModel parent)
	{
		getModel().setParent(parent);
		if (parent!= null)
		{
			if (CollectionUtils.isEmpty(parent.getChildren()))
			{
				parent.setChildren(Arrays.asList(getModel()));
			}
			else
			{
				final List<CMSNavigationNodeModel> list = new ArrayList<>(parent.getChildren());
				list.add(getModel());
				parent.setChildren(list);
			}
		}
		return this;
	}

	public NavigationNodeModelBuilder withChildren(final CMSNavigationNodeModel... children)
	{
		if (children != null)
		{
			Arrays.stream(children).forEach(child -> child.setParent(getModel()));
		}
		getModel().setChildren(Arrays.asList(children));
		return this;
	}

	public NavigationNodeModelBuilder withEntry(final CMSNavigationEntryModel entry)
	{
		if (getModel().getEntries() != null && getModel().getEntries().isEmpty())
		{
			getModel().getEntries().add(entry);
		}else{
			final List<CMSNavigationEntryModel> cmsNavigationEntryModels= new ArrayList<>();
			cmsNavigationEntryModels.add(entry);
			getModel().setEntries(cmsNavigationEntryModels);
		}
		return this;
	}

	public NavigationNodeModelBuilder withCatalogVersion(final CatalogVersionModel catalogVersion)
	{
		getModel().setCatalogVersion(catalogVersion);
		return this;
	}

	public CMSNavigationNodeModel build()
	{
		return this.getModel();
	}
}
