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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter for converting order / cart entries. It adds bundle specific data (bundle no, component) and flags the
 * entry as Valid/Updatable/Removable/Editable.
 *
 * @deprecated since 6.4. Use {@link BundleCommerceOrderEntryPopulator} instead.
 */
@Deprecated
public class BundleOrderEntryPopulator extends BundleCommerceOrderEntryPopulator
{

	private static final Logger LOG = Logger.getLogger(BundleOrderEntryPopulator.class);

	private BundleCommerceCartService bundleCommerceCartService;

	/**
	 * Modify order entry: set flags for Updateable/Removable/Editable/IsValid
	 */
	@Override
	protected void addCommon(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		if (orderEntry instanceof CartEntryModel)
		{
			final CartModel cart = ((CartEntryModel) orderEntry).getOrder();
			adjustUpdateable(entry, orderEntry);
			adjustRemoveable(entry, (CartEntryModel) orderEntry);
			adjustEditable(entry, (CartEntryModel) orderEntry, cart);
			adjustIsValid(entry, (CartEntryModel) orderEntry, cart);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Won't set flags Updateable/Removable/Editable/IsValid for " + orderEntry.getClass());
			}
		}
	}

	/**
	 * Modify method to call backend service to determine if an entry is updateable or not
	 *
	 * @param entry
	 * @param entryToUpdate
	 *           the {@link AbstractOrderEntryModel}
	 */
	@Override
	protected void adjustUpdateable(final OrderEntryData entry, final AbstractOrderEntryModel entryToUpdate)
	{
		if (entryToUpdate instanceof CartEntryModel)
		{
			final CartEntryModel orderEntry = (CartEntryModel) entryToUpdate;
			entry.setUpdateable(getBundleCommerceCartService().checkIsEntryUpdateable(orderEntry));
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Won't set flag Updateable for " + entryToUpdate.getClass());
			}
		}
	}

	/**
	 * 
	 * Calls backend service to determine if entry is removable or not
	 * 
	 * @param entry
	 * @param entryToUpdate
	 */
	protected void adjustRemoveable(final OrderEntryData entry, final CartEntryModel entryToUpdate)
	{
		entry.setRemoveable(getBundleCommerceCartService().checkIsEntryRemovable(entryToUpdate));
	}

	/**
	 * Calls backend service to determine if entry is editable or not. The backend service is for cart items that are
	 * part of a bundle For standalone products return false
	 * 
	 * @param entry
	 * @param entryToEdit
	 * @param cart
	 */
	protected void adjustEditable(final OrderEntryData entry, final CartEntryModel entryToEdit, final CartModel cart)
	{
		final boolean result;
		if (entryToEdit.getBundleNo() != null && entryToEdit.getBundleNo().intValue() > 0)
		{
			final CartModel masterCart = (CartModel) getMasterAbstractOrderFromOrder(cart);
			result = getBundleCommerceCartService().checkIsComponentEditable(masterCart, entryToEdit.getBundleTemplate(),
					entryToEdit.getBundleNo().intValue());

		}
		else
		//standalone product
		{
			result = false;
		}
		entry.setEditable(result);
	}

	protected AbstractOrderModel getMasterAbstractOrderFromOrder(final AbstractOrderModel abstractOrder)
	{
		return abstractOrder.getParent() == null ? abstractOrder : abstractOrder.getParent();
	}

	/**
	 * Calls backend service to determine whether the order entry is valid or not
	 * 
	 * @param entry
	 * @param cartEntry
	 * @param cart
	 */
	protected void adjustIsValid(final OrderEntryData entry, final CartEntryModel cartEntry, final CartModel cart)
	{
		final CartModel masterCart = (CartModel) getMasterAbstractOrderFromOrder(cart);
		entry.setValid(getBundleCommerceCartService().checkIsComponentSelectionCriteriaMet(masterCart,
				cartEntry.getBundleTemplate(), cartEntry.getBundleNo() == null ? -1 : cartEntry.getBundleNo().intValue()));
	}

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}
}
