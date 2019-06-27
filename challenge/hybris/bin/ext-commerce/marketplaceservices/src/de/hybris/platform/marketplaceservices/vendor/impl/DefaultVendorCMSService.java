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
package de.hybris.platform.marketplaceservices.vendor.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import de.hybris.platform.acceleratorcms.model.components.JspIncludeComponentModel;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageTemplateDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.cms2lib.enums.CarouselScroll;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.marketplaceservices.dao.MarketplaceCMSComponentDao;
import de.hybris.platform.marketplaceservices.data.SyncRequestData;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.model.restrictions.CMSVendorRestrictionModel;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Provide CMS related services
 */
public class DefaultVendorCMSService implements VendorCMSService
{
	private CMSPageTemplateDao cmsPageTemplateDao;
	private CMSPageDao cmsPageDao;
	private CMSRestrictionDao cmsRestrictionDao;
	private CMSContentSlotDao cmsContentSlotDao;
	private MarketplaceCMSComponentDao cmsComponentDao;
	private ModelService modelService;
	private SyncConfig syncConfig;
	private SearchRestrictionService searchRestrictionService;
	private SessionService sessionService;
	private CatalogTypeService catalogTypeService;
	private CatalogVersionService catalogVersionService;
	private SynchronizationStatusService platformSynchronizationStatusService;
	private CatalogSynchronizationService catalogSynchronizationService;

	@Override
	public Optional<PageTemplateModel> getPageTemplateByIdAndCatalogVersion(final String id,
			final CatalogVersionModel catalogVersion)
	{
		return getCmsPageTemplateDao().findPageTemplatesByIdAndCatalogVersion(id, catalogVersion).stream()
				.filter(PageTemplateModel::getActive).findAny();
	}

	@Override
	public Optional<AbstractPageModel> getPageByIdAndCatalogVersion(final String id, final CatalogVersionModel catalogVersion)
	{
		return getCmsPageDao().findPagesByIdAndCatalogVersion(id, catalogVersion).stream().findAny();
	}

	@Override
	public Optional<AbstractRestrictionModel> getRestrictionByIdAndCatalogVersion(final String id,
			final CatalogVersionModel catalogVersion)
	{
		return getCmsRestrictionDao().findRestrictionsById(id, catalogVersion).stream().findAny();
	}

	@Override
	public Optional<ContentSlotForPageModel> getContentSlotRelationByIdAndPositionInPage(final String id,
			final AbstractPageModel page, final String position)
	{
		final CatalogVersionModel catalogVersion = page.getCatalogVersion();
		return getCmsContentSlotDao().findContentSlotRelationsByPageAndPosition(page, position, catalogVersion).stream()
				.filter(contentSlotForPage -> id.equalsIgnoreCase(contentSlotForPage.getUid())).findAny();
	}

	@Override
	public Optional<ContentSlotModel> getContentSlotByIdAndCatalogVersion(final String id,
			final CatalogVersionModel catalogVersion)
	{
		final List<CatalogVersionModel> catalogVersions = new ArrayList<>();
		catalogVersions.add(catalogVersion);
		return getCmsContentSlotDao().findContentSlotsByIdAndCatalogVersions(id, catalogVersions).stream()
				.filter(ContentSlotModel::getActive).findAny();
	}

	@Override
	public Optional<ContentSlotModel> getContentSlotByPageAndPosition(final String position, final AbstractPageModel page,
			final CatalogVersionModel catalogVersion)
	{
		return getCmsContentSlotDao().findContentSlotRelationsByPageAndPosition(page, position, catalogVersion).stream()
				.map(ContentSlotForPageModel::getContentSlot).findAny();
	}

	@Override
	public Optional<AbstractCMSComponentModel> getCMSComponentByIdAndCatalogVersion(final String id,
			final CatalogVersionModel catalogVersion)
	{
		return getCmsComponentDao().findCMSComponentsByIdAndCatalogVersion(id, catalogVersion).stream().findAny();
	}

	@Override
	public List<AbstractCMSComponentModel> getCMSComponentsByContentSlotAndCatalogVersions(final String contentSlotId,
			final List<CatalogVersionModel> catalogVersions)
	{
		return getCmsComponentDao().findCMSComponentsByContentSlot(contentSlotId, catalogVersions);
	}

	@Override
	public VendorPageModel saveOrUpdateCMSVendorPage(final String pageUid, final String pageName,
			final CatalogVersionModel catalogVersion, final PageTemplateModel pageTemplate, final boolean isDefaultPage,
			final CmsApprovalStatus approvalStatus)
	{
		final VendorPageModel vendorPage = (VendorPageModel) getPageByIdAndCatalogVersion(pageUid, catalogVersion)
				.orElse(new VendorPageModel());
		vendorPage.setCatalogVersion(catalogVersion);
		vendorPage.setUid(pageUid);
		vendorPage.setName(pageName);
		vendorPage.setMasterTemplate(pageTemplate);
		vendorPage.setApprovalStatus(approvalStatus);
		vendorPage.setDefaultPage(isDefaultPage);
		getModelService().save(vendorPage);
		return vendorPage;
	}

	@Override
	public void saveOrUpdateCMSVendorRestriction(final VendorModel vendor, final CatalogVersionModel catalogVersion,
			final String uid, final String name, final AbstractPageModel... pages)
	{
		final CMSVendorRestrictionModel cmsVendorRestriction = (CMSVendorRestrictionModel) getRestrictionByIdAndCatalogVersion(uid,
				catalogVersion).orElse(new CMSVendorRestrictionModel());
		cmsVendorRestriction.setCatalogVersion(catalogVersion);
		cmsVendorRestriction.setUid(uid);
		cmsVendorRestriction.setName(name);
		cmsVendorRestriction.setPages(Arrays.asList(pages));
		cmsVendorRestriction.setVendor(vendor);
		getModelService().save(cmsVendorRestriction);
	}

	@Override
	public ContentSlotModel saveOrUpdateCMSContentSlot(final CatalogVersionModel catalogVersion, final String uid,
			final String name, final boolean active)
	{
		final ContentSlotModel contentSlot = getContentSlotByIdAndCatalogVersion(uid, catalogVersion)
				.orElse(new ContentSlotModel());
		contentSlot.setCatalogVersion(catalogVersion);
		contentSlot.setUid(uid);
		contentSlot.setName(name);
		contentSlot.setActive(active);
		getModelService().save(contentSlot);
		return contentSlot;
	}

	@Override
	public void saveOrUpdateCMSContentSlotForPage(final CatalogVersionModel catalogVersion, final String uid,
			final String position, final AbstractPageModel page, final ContentSlotModel contentSlot)
	{
		final ContentSlotForPageModel contentSlotForPage = getContentSlotRelationByIdAndPositionInPage(uid, page, position)
				.orElse(new ContentSlotForPageModel());
		contentSlotForPage.setCatalogVersion(catalogVersion);
		contentSlotForPage.setUid(uid);
		contentSlotForPage.setPosition(position);
		contentSlotForPage.setPage(page);
		contentSlotForPage.setContentSlot(contentSlot);
		getModelService().save(contentSlotForPage);
	}

	@Override
	public JspIncludeComponentModel saveOrUpdateJspIncludeComponent(final CatalogVersionModel catalogVersion, final String uid,
			final String name, final String page)
	{
		final JspIncludeComponentModel jspIncludeComponent = (JspIncludeComponentModel) getCMSComponentByIdAndCatalogVersion(uid,
				catalogVersion).orElse(new JspIncludeComponentModel());
		jspIncludeComponent.setCatalogVersion(catalogVersion);
		jspIncludeComponent.setUid(uid);
		jspIncludeComponent.setName(name);
		jspIncludeComponent.setPage(page);
		getModelService().save(jspIncludeComponent);
		return jspIncludeComponent;
	}

	@Override
	public ProductCarouselComponentModel saveOrUpdateProductCarouselComponent(final CatalogVersionModel catalogVersion,
			final String uid, final String name, final CarouselScroll scroll, final boolean popup)
	{
		final ProductCarouselComponentModel carouselComponent = (ProductCarouselComponentModel) getCMSComponentByIdAndCatalogVersion(
				uid, catalogVersion).orElse(new ProductCarouselComponentModel());
		carouselComponent.setCatalogVersion(catalogVersion);
		carouselComponent.setUid(uid);
		carouselComponent.setName(name);
		carouselComponent.setScroll(scroll);
		carouselComponent.setPopup(popup);
		getModelService().save(carouselComponent);
		return carouselComponent;
	}

	@Override
	public SyncItemStatus getProductCarouselSynchronizationStatus(final ProductCarouselComponentModel carousel)
	{
		checkArgument(carousel != null, "carousel cannot be null");
		return getPlatformSynchronizationStatusService()
				.getSyncInfo(carousel, getRelevantSyncItemJob(getSyncRequestData(carousel), carousel)).getSyncStatus();
	}

	@Override
	public void performProductCarouselSynchronization(final ProductCarouselComponentModel carousel, final boolean synchronous)
	{
		checkArgument(carousel != null, "carousel must not be null");
		getSyncConfig().setSynchronous(synchronous);
		getCatalogSynchronizationService().performSynchronization(getItemList(carousel),
				getRelevantSyncItemJob(getSyncRequestData(carousel), carousel), getSyncConfig());
	}

	protected List<ItemModel> getItemList(ItemModel item)
	{
		return Collections.singletonList(item);
	}

	protected SyncRequestData getSyncRequestData(final ProductCarouselComponentModel model)
	{
		final CatalogModel catalog = model.getCatalogVersion().getCatalog();
		final String catalogID = catalog.getId();
		final String targetVersion = catalog.getActiveCatalogVersion().getVersion();
		final String sourceVersion = catalog.getCatalogVersions().stream().filter(v -> !v.getVersion().equals(targetVersion))
				.findFirst().map(v -> v.getVersion()).orElse(null);

		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogID);
		syncRequestData.setSourceVersionId(sourceVersion);
		syncRequestData.setTargetVersionId(targetVersion);

		return syncRequestData;
	}

	protected SyncItemJobModel getRelevantSyncItemJob(final SyncRequestData syncRequestData, final ItemModel item)
	{

		final boolean isOutbound = isOutboundSynchronization(syncRequestData, item);

		List<SyncItemJobModel> synchronizations;
		if (isOutbound)
		{
			synchronizations = getPlatformSynchronizationStatusService().getOutboundSynchronizations(item);
		}
		else
		{
			synchronizations = getPlatformSynchronizationStatusService().getInboundSynchronizations(item);
		}

		//select synchronization matching source and target catalog versions
		return synchronizations.stream()
				.filter(job -> job.getSourceVersion().getVersion().equals(syncRequestData.getSourceVersionId())
						&& job.getTargetVersion().getVersion().equals(syncRequestData.getTargetVersionId()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(format(
						"No SyncItemJobModel was found from %s to %s versions for catalog id %s", syncRequestData.getSourceVersionId(),
						syncRequestData.getTargetVersionId(), syncRequestData.getCatalogId())));
	}

	protected synchronized boolean isOutboundSynchronization(final SyncRequestData syncRequestData, final ItemModel item)
	{
		return (Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				return isExecutableInLocalView(syncRequestData, item);
			}
		});
	}

	protected boolean isExecutableInLocalView(final SyncRequestData syncRequestData, final ItemModel item)
	{
		boolean wasEnabled = false;
		try
		{
			wasEnabled = getSearchRestrictionService().isSearchRestrictionsEnabled();
			if (wasEnabled)
			{
				getSearchRestrictionService().disableSearchRestrictions();
			}
			final CatalogVersionModel sourceVersion = getCatalogVersionService().getCatalogVersion(syncRequestData.getCatalogId(),
					syncRequestData.getSourceVersionId());
			final CatalogVersionModel itemCatalogVersion = getCatalogTypeService()
					.getCatalogVersionForCatalogVersionAwareModel(item);

			return ObjectUtils.equals(sourceVersion, itemCatalogVersion);
		}
		finally
		{
			if (wasEnabled)
			{
				getSearchRestrictionService().enableSearchRestrictions();
			}
		}

	}

	protected CMSPageTemplateDao getCmsPageTemplateDao()
	{
		return cmsPageTemplateDao;
	}

	@Required
	public void setCmsPageTemplateDao(final CMSPageTemplateDao cmsPageTemplateDao)
	{
		this.cmsPageTemplateDao = cmsPageTemplateDao;
	}

	protected CMSPageDao getCmsPageDao()
	{
		return cmsPageDao;
	}

	@Required
	public void setCmsPageDao(final CMSPageDao cmsPageDao)
	{
		this.cmsPageDao = cmsPageDao;
	}

	protected CMSRestrictionDao getCmsRestrictionDao()
	{
		return cmsRestrictionDao;
	}

	@Required
	public void setCmsRestrictionDao(final CMSRestrictionDao cmsRestrictionDao)
	{
		this.cmsRestrictionDao = cmsRestrictionDao;
	}

	protected CMSContentSlotDao getCmsContentSlotDao()
	{
		return cmsContentSlotDao;
	}

	@Required
	public void setCmsContentSlotDao(final CMSContentSlotDao cmsContentSlotDao)
	{
		this.cmsContentSlotDao = cmsContentSlotDao;
	}

	protected MarketplaceCMSComponentDao getCmsComponentDao()
	{
		return cmsComponentDao;
	}

	@Required
	public void setCmsComponentDao(final MarketplaceCMSComponentDao cmsComponentDao)
	{
		this.cmsComponentDao = cmsComponentDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected SyncConfig getSyncConfig()
	{
		return syncConfig;
	}

	@Required
	public void setSyncConfig(final SyncConfig syncConfig)
	{
		this.syncConfig = syncConfig;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CatalogTypeService getCatalogTypeService()
	{
		return catalogTypeService;
	}

	@Required
	public void setCatalogTypeService(final CatalogTypeService catalogTypeService)
	{
		this.catalogTypeService = catalogTypeService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected SynchronizationStatusService getPlatformSynchronizationStatusService()
	{
		return platformSynchronizationStatusService;
	}

	@Required
	public void setPlatformSynchronizationStatusService(final SynchronizationStatusService platformSynchronizationStatusService)
	{
		this.platformSynchronizationStatusService = platformSynchronizationStatusService;
	}

	protected CatalogSynchronizationService getCatalogSynchronizationService()
	{
		return catalogSynchronizationService;
	}

	@Required
	public void setCatalogSynchronizationService(final CatalogSynchronizationService catalogSynchronizationService)
	{
		this.catalogSynchronizationService = catalogSynchronizationService;
	}

}
