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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BBookingLineEntryModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;

import java.util.Date;
import java.util.List;



/**
 * A dao around {@link B2BBookingLineEntryModel}
 * 
 *
 * @spring.bean b2bBookingLineEntryDao
 */
public interface B2BBookingLineEntryDao
{

	/**
	 * Gets the total cost of BookingLines between particular dates
	 * 
	 * @param costCenter
	 *           the costCenter
	 * @param startDate
	 *           the startDate of the date range
	 * @param endDate
	 *           the endDate of the date range
	 * @return amount of the total cost
	 * @deprecated Since 4.4. Use {@link #findTotalCostByCostCenterAndDate(B2BCostCenterModel,Date,Date)} instead
	 */
	@Deprecated
	public abstract Double getTotalCost(final B2BCostCenterModel costCenter, final Date startDate, final Date endDate);

	/**
	 * Gets the total cost of BookingLines between a particular date range
	 * 
	 * @param costCenter
	 *           the costCenter
	 * @param startDate
	 *           the startDate of the date range
	 * @param endDate
	 *           the endDate of the date range
	 * @return amount of the total cost
	 */
	public abstract Double findTotalCostByCostCenterAndDate(final B2BCostCenterModel costCenter, final Date startDate,
			final Date endDate);

	/**
	 * @param costCenters
	 * @param bookingDate
	 * @return a list of open bookings
	 * @deprecated Since 4.4. Use {@link #findOpenBookingLineEntriesByCostCenterAndDate(List, Date)} instead
	 */
	@Deprecated
	public abstract List<B2BBookingLineEntryModel> getOpenBookingLineEntries(final List<B2BCostCenterModel> costCenters,
			final Date bookingDate);

	/**
	 * Finds open booking lines
	 * 
	 * @param costCenters
	 *           the costCenter
	 * @param bookingDate
	 *           the booking date
	 * @return a list of open bookings
	 */
	public abstract List<B2BBookingLineEntryModel> findOpenBookingLineEntriesByCostCenterAndDate(
			final List<B2BCostCenterModel> costCenters, final Date bookingDate);

}
