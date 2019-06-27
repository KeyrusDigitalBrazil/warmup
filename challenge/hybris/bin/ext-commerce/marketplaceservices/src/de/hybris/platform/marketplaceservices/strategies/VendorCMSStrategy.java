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
package de.hybris.platform.marketplaceservices.strategies;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.List;


/**
 * A strategy for create cms page/restriction/slot/component for a vendor.
 */
public interface VendorCMSStrategy
{
	/**
	 * A key to create the whole landing page with default content slots and components
	 *
	 * @param vendor
	 *           the specific vendor
	 * @return the instance of this page
	 */
	AbstractPageModel prepareLandingPageForVendor(VendorModel vendor);


	/**
	 * get the content slot instance from a vendor page by its position
	 *
	 * @param vendor
	 *           the specific vendor
	 * @param position
	 *           the position
	 * @param catalogVersion
	 *           the catalog version the position of this content slot
	 * @return the instance of this content slot
	 */
	ContentSlotModel getContentSlotByPositionAndCatalogVersion(VendorModel vendor, String position,
			CatalogVersionModel catalogVersion);

	/**
	 * get the product carousel components in a vendor landing page
	 *
	 * @param vendor
	 *           the specific vendor
	 * @return list of product carousel components
	 */
	List<AbstractCMSComponentModel> getVendorProductCarouselComponents(VendorModel vendor);
}
