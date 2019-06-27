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
package de.hybris.platform.consignmenttrackingmock.data;

import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The class is just used for mockup to retrieve consignment by tracking id, the senario will not happen in reality
 */
public class MockDataProvider
{
	private static final Logger LOG = Logger.getLogger(MockDataProvider.class);

	private FlexibleSearchService flexibleSearchService;

	/**
	 * Query a ConsignmentModel for tracking id.
	 *
	 * @param trackingId
	 *           the specific tracking id
	 * @return ConsignmentModel
	 */
	public Optional<ConsignmentModel> getConsignmentForTrackingId(final String carrierCode, final String trackingId)
	{
		final String fql = "SELECT {" + ConsignmentModel.PK + "} " + "FROM {" + ConsignmentModel._TYPECODE + " } WHERE {"
				+ ConsignmentModel.TRACKINGID + "} = ?trackingId and {" + ConsignmentModel.CARRIERDETAILS + "} in ({{select {"
				+ CarrierModel.PK + "} from {" + CarrierModel._TYPECODE + "} where {" + CarrierModel.CODE + "}= ?carrierCode}})";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(fql);
		query.addQueryParameter("carrierCode", carrierCode);
		query.addQueryParameter("trackingId", trackingId);

		try
		{
			final ConsignmentModel result = getFlexibleSearchService().searchUnique(query);
			return Optional.ofNullable(result);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.info("No query result for the given trackingId.");
		}
		return Optional.empty();
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
