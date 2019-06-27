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
package de.hybris.platform.marketplaceservices.vendor;

import de.hybris.platform.acceleratorcms.model.components.JspIncludeComponentModel;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2lib.enums.CarouselScroll;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.List;
import java.util.Optional;


/**
 * Used to handle data in CMS site/page/template/component
 */
public interface VendorCMSService
{
	/**
	 * get page template by given id in given catalog version
	 *
	 * @param id
	 *           the given template id
	 * @param catalogVersion
	 *           the given catalog version
	 * @return Optional of the instance of the page template if found and empty optional otherwise
	 */
	Optional<PageTemplateModel> getPageTemplateByIdAndCatalogVersion(String id, CatalogVersionModel catalogVersion);

	/**
	 * get page by it and catalog version
	 *
	 * @param id
	 *           the uid of the page
	 * @param catalogVersion
	 *           the catalog version
	 * @return Optional of the instance of the page if found and empty optional otherwise
	 */
	Optional<AbstractPageModel> getPageByIdAndCatalogVersion(String id, CatalogVersionModel catalogVersion);

	/**
	 * get restriction by id and catalog version
	 *
	 * @param id
	 *           the uid of the restriction
	 * @param catalogVersion
	 *           the catalog version
	 * @return Optional of the instance of the restriction if found and empty optional otherwise
	 */
	Optional<AbstractRestrictionModel> getRestrictionByIdAndCatalogVersion(String id, CatalogVersionModel catalogVersion);

	/**
	 * get ContentSlotForPage according to its position in a page
	 *
	 * @param id
	 *           ContentSlotForPage's Id
	 * @param page
	 *           the instance of page
	 * @param position
	 *           the specific position
	 * @return tOptional of the instance of the content slot relation if found and empty optional otherwise
	 */

	Optional<ContentSlotForPageModel> getContentSlotRelationByIdAndPositionInPage(String id, AbstractPageModel page,
			String position);

	/**
	 * get content slot by its uid and catalog version
	 *
	 * @param id
	 *           the uid of the content slot
	 * @param catalogVersion
	 *           the catalog version
	 * @return Optional of the instance of the content slot if found and empty optional otherwise
	 */
	Optional<ContentSlotModel> getContentSlotByIdAndCatalogVersion(String id, CatalogVersionModel catalogVersion);

	/**
	 * get content slot by its position, page and catalog version
	 *
	 * @param position
	 *           position of the content slot
	 * @param page
	 *           page of the content slot
	 * @param catalogVersion
	 *           the catalog version
	 * @return the expected content slot otherwise empty option
	 */
	Optional<ContentSlotModel> getContentSlotByPageAndPosition(String position, AbstractPageModel page,
			CatalogVersionModel catalogVersion);

	/**
	 * get component by its uid and catalog version
	 *
	 * @param id
	 *           the uid of the component
	 * @param catalogVersion
	 *           the catalog version
	 * @return Optional of the instance of the component if found and empty optional otherwise
	 */
	Optional<AbstractCMSComponentModel> getCMSComponentByIdAndCatalogVersion(String id, CatalogVersionModel catalogVersion);

	/**
	 * get component by content slot and catalog version
	 *
	 * @param contentSlotId
	 *           Id of content slot
	 * @param catalogVersions
	 *           list of catalog versions
	 * @return List of component in given content slot and catalog version
	 */
	List<AbstractCMSComponentModel> getCMSComponentsByContentSlotAndCatalogVersions(String contentSlotId,
			List<CatalogVersionModel> catalogVersions);


	/**
	 * create an instance of CMS VendorPageModel
	 *
	 * @param pageUid
	 *           the page's uid
	 * @param pageName
	 *           the page's name
	 * @param catalogVersion
	 *           the catalogversion of this page
	 * @param pageTemplate
	 *           the template of page used
	 * @param isDefaultPage
	 *           using this page as a default?
	 * @param approvalStatus
	 *           is approved or checked?
	 * @return The instance of the page
	 */
	VendorPageModel saveOrUpdateCMSVendorPage(String pageUid, String pageName, CatalogVersionModel catalogVersion,
			PageTemplateModel pageTemplate, boolean isDefaultPage, CmsApprovalStatus approvalStatus);

	/**
	 * create an instance of page restriction for a vendor
	 *
	 * @param vendor
	 *           the specific page
	 * @param catalogVersion
	 *           the catalog version
	 * @param uid
	 *           the restrication's ID
	 * @param name
	 *           the restrication's name
	 * @param pages
	 *           pages of restriction
	 */
	void saveOrUpdateCMSVendorRestriction(VendorModel vendor, CatalogVersionModel catalogVersion, String uid, String name,
			AbstractPageModel... pages);

	/**
	 * create content slot
	 *
	 * @param catalogVersion
	 *           the catalogversion of this page using
	 * @param uid
	 *           content slot id
	 * @param name
	 *           content slot name
	 * @param active
	 *           is active?
	 * @return the content slot instance
	 */
	ContentSlotModel saveOrUpdateCMSContentSlot(CatalogVersionModel catalogVersion, String uid, String name, boolean active);

	/**
	 * create the content slot for a CMS page
	 *
	 * @param catalogVersion
	 *           the catalogversion of this page using
	 * @param uid
	 *           the id of this content slot
	 * @param position
	 *           where is the content slot
	 * @param page
	 *           the page of this content slot
	 * @param contentSlot
	 *           detailed content slot reference
	 */
	void saveOrUpdateCMSContentSlotForPage(CatalogVersionModel catalogVersion, String uid, String position, AbstractPageModel page,
			ContentSlotModel contentSlot);

	/**
	 * create JSP include component
	 *
	 * @param catalogVersion
	 *           the catalogversion of this page using
	 * @param uid
	 *           the id of this component
	 * @param name
	 *           name of the component
	 * @param page
	 *           detailed jsp location of this component
	 *
	 * @return the instance of this jspincludecomponent
	 */
	JspIncludeComponentModel saveOrUpdateJspIncludeComponent(CatalogVersionModel catalogVersion, String uid, String name,
			String page);

	/**
	 * create product carousel component
	 *
	 * @param catalogVersion
	 *           the catalog of this component
	 * @param uid
	 *           the id of this component
	 * @param name
	 *           the id of this component
	 * @param scroll
	 *           how to scroll the carousel
	 * @param popup
	 *           is pop-up?
	 * @return the instance of this component
	 */
	ProductCarouselComponentModel saveOrUpdateProductCarouselComponent(CatalogVersionModel catalogVersion, String uid, String name,
			CarouselScroll scroll, boolean popup);

	/**
	 * Gets the synchronization item status for a given {@link ProductCarouselComponentModel}
	 *
	 * @param carousel
	 *           carousel model we are interested in getting the synchronization status
	 * @return the given carousel {@link SyncItemStatus}
	 */
	SyncItemStatus getProductCarouselSynchronizationStatus(final ProductCarouselComponentModel carousel);

	/**
	 * Performs the synchronization of a model of {@link ProductCarouselComponentModel} taking in consideration the
	 * source and target catalog versions.
	 *
	 * @param carousel
	 *           the carousel model that we want to synchronize
	 * @param synchronous
	 *           run with synchronous or asynchronous
	 */
	void performProductCarouselSynchronization(final ProductCarouselComponentModel carousel, final boolean synchronous);
}

