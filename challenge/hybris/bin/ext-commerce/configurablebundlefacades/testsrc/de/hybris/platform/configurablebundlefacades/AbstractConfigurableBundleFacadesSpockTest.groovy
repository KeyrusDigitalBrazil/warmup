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
 
 package de.hybris.platform.configurablebundlefacades

import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils
import de.hybris.platform.commercefacades.order.EntryGroupData
import de.hybris.platform.commercefacades.order.data.AbstractOrderData
import de.hybris.platform.commercefacades.order.data.OrderEntryData
import de.hybris.platform.commerceservices.order.CommerceCartModificationException
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade
import de.hybris.platform.servicelayer.util.ServicesUtil

import javax.annotation.Nonnull
import javax.annotation.Resource

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component;


/**
 *
 * Keywords from the old ATDD tests - now called from Spock tests
 *
 */
@Component
public abstract class AbstractConfigurableBundleFacadesSpockTest extends AbstractCommerceFacadesSpockTest {
	private static final Logger LOG = Logger.getLogger(AbstractConfigurableBundleFacadesSpockTest.class);

	@Resource
	private BundleCartFacade bundleCartFacade;

	@Resource
	private CommerceEntryGroupUtils commerceEntryGroupUtils;


	/**
	 * Start new bundle.
	 *
	 * @param componentId bundle component
	 * @param productCode product (should be one of the component's products)
	 * @param quantity number of products to add
	 * @return cart modification
	 * @throws CommerceCartModificationException if the bundle can not be created
	 */
	public OrderEntryData startNewBundle(final String componentId, final String productCode, final int quantity)
	throws CommerceCartModificationException {
		return getBundleCartFacade().startBundle(componentId, productCode, quantity).getEntry();
	}

	protected BundleCartFacade getBundleCartFacade() {
		return bundleCartFacade;
	}

	/**
	 * Find entry group data by groupNumber.
	 *
	 * @param order order that contains the desired entry group
	 * @param groupNumber group number
	 * @return entry group data
	 * @throws IllegalArgumentException if there is no such group
	 */
	public EntryGroupData getEntryGroup(@Nonnull final AbstractOrderData order, @Nonnull final Integer groupNumber) {
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		ServicesUtil.validateParameterNotNullStandardMessage("groupNum	ber", groupNumber);

		return getCommerceEntryGroupUtils().getGroup(order, groupNumber);
	}

	/**
	 * Find entry within an order by externalReferenceId.
	 *
	 * @param order order data
	 * @param externalReferenceId reference id
	 * @return entry group data
	 * @throws IllegalArgumentException if entry group was not found
	 */
	public EntryGroupData findEntryGroupByRefInOrder(@Nonnull final AbstractOrderData order, final String externalReferenceId) {
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		ServicesUtil.validateParameterNotNullStandardMessage("order.rootGroups", order.getRootGroups());
		EntryGroupData result;
		for (rootGroup in order.getRootGroups()) {
			for( group in getCommerceEntryGroupUtils().getNestedGroups(rootGroup)) {
				if (group.getExternalReferenceId().equals(externalReferenceId)) {
					result = group
					break
				}
			}
			if (result != null) {
				break
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("The order does not have a group with ref id '" + externalReferenceId + "'")
		}

		return result
	}

	/**
	 * Find entry within a tree by external reference id.
	 * @param rootGroupData root group of the tree
	 * @param externalReferenceId reference id
	 * @return entry group data
	 * @throws IllegalArgumentException if entry group was not found
	 */
	public EntryGroupData findEntryGroupByRefInTree(@Nonnull final EntryGroupData rootGroupData, final String externalReferenceId) {
		EntryGroupData result;
		for( group in getCommerceEntryGroupUtils().getNestedGroups(rootGroupData)) {
			if (group.getExternalReferenceId().equals(externalReferenceId)) {
				result = group
				break
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("The order does not have a group with ref id '" + externalReferenceId + "'")
		}

		return result
	}

	protected CommerceEntryGroupUtils getCommerceEntryGroupUtils() {
		return commerceEntryGroupUtils;
	}
}
