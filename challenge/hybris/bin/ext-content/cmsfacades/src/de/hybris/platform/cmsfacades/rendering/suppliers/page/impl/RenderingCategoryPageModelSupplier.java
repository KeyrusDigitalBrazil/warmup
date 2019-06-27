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
package de.hybris.platform.cmsfacades.rendering.suppliers.page.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cmsfacades.rendering.suppliers.page.RenderingPageModelSupplier;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;
import java.util.function.Predicate;


/**
 * Implementation of {@link RenderingPageModelSupplier} for Category page.
 */
public class RenderingCategoryPageModelSupplier implements RenderingPageModelSupplier
{
	private Predicate<String> constrainedBy;
	private CMSPageService cmsPageService;
	private CMSDataFactory cmsDataFactory;

	@Override
	public Predicate<String> getConstrainedBy()
	{
		return constrainedBy;
	}

	@Override
	public Optional<AbstractPageModel> getPageModel(String categoryCode)
	{
		try
		{
			return Optional.of(getCmsPageService().getPageForCategoryCode(categoryCode));
		}
		catch (CMSItemNotFoundException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<RestrictionData> getRestrictionData(String categoryCode)
	{
		return Optional.ofNullable(getCmsDataFactory().createRestrictionData(categoryCode, null, null));
	}

	@Required
	public void setConstrainedBy(Predicate<String> constrainedBy)
	{
		this.constrainedBy = constrainedBy;
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}

	protected CMSDataFactory getCmsDataFactory()
	{
		return cmsDataFactory;
	}

	@Required
	public void setCmsDataFactory(CMSDataFactory cmsDataFactory)
	{
		this.cmsDataFactory = cmsDataFactory;
	}
}
