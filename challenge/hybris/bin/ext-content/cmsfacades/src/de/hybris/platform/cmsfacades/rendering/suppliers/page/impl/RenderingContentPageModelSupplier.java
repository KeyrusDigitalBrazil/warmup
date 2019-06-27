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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cmsfacades.rendering.suppliers.page.RenderingPageModelSupplier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * Implementation of {@link RenderingPageModelSupplier} for Content page.
 */
public class RenderingContentPageModelSupplier implements RenderingPageModelSupplier
{
	private Predicate<String> constrainedBy;
	private CMSPageService cmsPageService;
	private CMSSiteService cmsSiteService;

	@Override
	public Predicate<String> getConstrainedBy()
	{
		return constrainedBy;
	}

	@Override
	public Optional<AbstractPageModel> getPageModel(final String pageLabelOrId)
	{
		return getContentPageModel(pageLabelOrId);
	}

	@Override
	public Optional<RestrictionData> getRestrictionData(String qualifier)
	{
		return Optional.empty();
	}

	/**
	 * Returns {@link Optional} {@link AbstractPageModel} based on pageLabelOrId.
	 * Extracts the page in the following order:
	 * - by label or id.
	 * - current catalog home page.
	 * - by default label or id
	 *
	 * @param pageLabelOrId the page label or id
	 * @return {@link Optional} {@link AbstractPageModel} page model
	 */
	protected Optional<AbstractPageModel> getContentPageModel(final String pageLabelOrId)
	{
		final Supplier<Optional<ContentPageModel>> page = getPageForLabelOrId(pageLabelOrId);
		final Supplier<Optional<ContentPageModel>> homePage = getCurrentCatalogHomePage();
		final Supplier<Optional<ContentPageModel>> startPage = getPageForLabelOrId(getStartPageLabelOrId());

		final ContentPageModel resultPage = page.get().orElse(homePage.get().orElse(startPage.get().orElse(null)));

		return Optional.ofNullable(resultPage);
	}

	/**
	 * Returns the {@link Optional} page by label or id
	 *
	 * @param pageLabelOrId
	 * @return the {@link Optional} {@link AbstractPageModel}
	 */
	protected Supplier<Optional<ContentPageModel>> getPageForLabelOrId(final String pageLabelOrId)
	{
		return () -> {
			if (!StringUtils.isBlank(pageLabelOrId))
			{
				try
				{
					return Optional.ofNullable(getCmsPageService().getPageForLabelOrId(pageLabelOrId));
				}
				catch (CMSItemNotFoundException e)
				{
					return Optional.empty();
				}
			}
			else
			{
				return Optional.empty();
			}
		};
	}

	/**
	 * Returns default page label or id
	 *
	 * @return the page label or id
	 */
	protected String getStartPageLabelOrId()
	{
		final Optional<CMSSiteModel> currentSite = getCurrentSite();
		return currentSite.map(site -> getCmsSiteService().getStartPageLabelOrId(site)).orElse(null);
	}

	/**
	 * Returns a home page for the current catalog.
	 *
	 * @return {@link Optional} {@link AbstractPageModel}
	 */
	protected Supplier<Optional<ContentPageModel>> getCurrentCatalogHomePage()
	{
		return () -> Optional.ofNullable(getCmsPageService().getHomepage());
	}

	/**
	 * Returns current site
	 *
	 * @return {@link Optional} {@link CMSSiteModel}
	 */
	protected Optional<CMSSiteModel> getCurrentSite()
	{
		return Optional.ofNullable(getCmsSiteService().getCurrentSite());
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

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}
}
