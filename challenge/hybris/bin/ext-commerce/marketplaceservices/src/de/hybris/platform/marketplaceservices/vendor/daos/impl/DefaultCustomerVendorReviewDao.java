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
package de.hybris.platform.marketplaceservices.vendor.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.marketplaceservices.vendor.daos.CustomerVendorReviewDao;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link CustomerVendorReviewDao}.
 */
public class DefaultCustomerVendorReviewDao extends DefaultGenericDao<CustomerVendorReviewModel>
		implements CustomerVendorReviewDao
{
	protected static final String VENDOR_CODE = "code";
	protected static final String APPROVAL_STATUS = "approvalStatus";
	protected static final String CONSIGNMENT_CODE = "code";
	protected static final String UID = "uid";
	private static final String LANGUAGE = "language";
	private static final String BY_CREATE_DATE_ASC = " order by {CREATEDATE} ASC";
	private static final String BY_CREATE_DATE_DESC = " order by {CREATEDATE} DESC";

	protected static final String FIND_REVIEWS = "SELECT {r:" + CustomerVendorReviewModel.PK + "} FROM {"
			+ CustomerVendorReviewModel._TYPECODE + " AS r JOIN " + VendorModel._TYPECODE + " AS v ON {v:" + VendorModel.PK
			+ "} = {r:" + CustomerVendorReviewModel.VENDOR + "}} WHERE {v:" + VendorModel.CODE + "}=?" + VENDOR_CODE + " AND {r:"
			+ CustomerVendorReviewModel.APPROVALSTATUS + "}=?" + APPROVAL_STATUS;

	protected static final String FIND_REVIEWS_FOR_CURRENT_LANGUAGE = "SELECT {r:" + CustomerVendorReviewModel.PK + "} FROM {"
			+ CustomerVendorReviewModel._TYPECODE + " AS r JOIN " + VendorModel._TYPECODE + " AS v ON {v:" + VendorModel.PK
			+ "} = {r:" + CustomerVendorReviewModel.VENDOR + "} JOIN " + LanguageModel._TYPECODE + " AS l ON {l:" + LanguageModel.PK
			+ "} = {r:" + CustomerVendorReviewModel.LANGUAGE + "}} WHERE {v:" + VendorModel.CODE + "}=?" + VENDOR_CODE + " AND {r:"
			+ CustomerVendorReviewModel.APPROVALSTATUS + "}=?" + APPROVAL_STATUS + " AND {l:" + LanguageModel.ISOCODE + "} = ?"
			+ LANGUAGE;

	protected static final String POSTED_REVIEW = "SELECT 1 FROM {" + CustomerVendorReviewModel._TYPECODE + " AS r JOIN "
			+ ConsignmentModel._TYPECODE + " AS c ON {r: " + CustomerVendorReviewModel.CONSIGNMENT + "}={c:" + ConsignmentModel.PK
			+ "} JOIN " + UserModel._TYPECODE + " AS u ON {r:" + CustomerVendorReviewModel.USER + "}={u:" + UserModel.PK
			+ "}} WHERE {c:" + ConsignmentModel.CODE + "}=?" + CONSIGNMENT_CODE + " AND {u:" + UserModel.UID + "}=?" + UID;

	private static final String FIND_REVIEWS_BY_USER = "SELECT {" + CustomerVendorReviewModel.PK + "} FROM {"
			+ CustomerVendorReviewModel._TYPECODE + "} WHERE {" + CustomerVendorReviewModel.USER + "} = ?user";

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	public DefaultCustomerVendorReviewDao()
	{
		super(CustomerVendorReviewModel._TYPECODE);
	}

	@Override
	public Collection<CustomerVendorReviewModel> findReviewsForVendor(final VendorModel vendor)
	{
		validateParameterNotNull(vendor, "Vendor must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_REVIEWS);
		query.addQueryParameter(VENDOR_CODE, vendor.getCode());
		query.addQueryParameter(APPROVAL_STATUS, CustomerReviewApprovalType.APPROVED);
		return getFlexibleSearchService().<CustomerVendorReviewModel> search(query).getResult();
	}

	@Override
	public boolean postedReview(final String consignmentCode, final UserModel user)
	{
		validateParameterNotNull(consignmentCode, "Consignment code must not be null");
		validateParameterNotNull(user, "User must not be null");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(POSTED_REVIEW);
		query.addQueryParameter(CONSIGNMENT_CODE, consignmentCode);
		query.addQueryParameter(UID, user.getUid());

		final int resultSize = getFlexibleSearchService().search(query).getResult().size();
		return Boolean.valueOf(resultSize > 0);
	}

	@Override
	public SearchPageData<CustomerVendorReviewModel> findPagedReviewsForVendor(final String vendorCode,
			final LanguageModel language, final PageableData pageableData)
	{

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byCreateDateAsc", FIND_REVIEWS_FOR_CURRENT_LANGUAGE + BY_CREATE_DATE_ASC),
				createSortQueryData("byCreateDateDesc", FIND_REVIEWS_FOR_CURRENT_LANGUAGE + BY_CREATE_DATE_DESC));

		final Map<String, Object> params = new HashMap<>();
		params.put(VENDOR_CODE, vendorCode);
		params.put(APPROVAL_STATUS, CustomerReviewApprovalType.APPROVED);
		params.put(LANGUAGE, language.getIsocode());

		return getPagedFlexibleSearchService().<CustomerVendorReviewModel> search(sortQueries, "byCreateDateDesc", params,
				pageableData);
	}

	@Override
	public Collection<CustomerVendorReviewModel> findReviewsByUser(final UserModel user)
	{
		return getFlexibleSearchService().<CustomerVendorReviewModel> search(FIND_REVIEWS_BY_USER,
				Collections.singletonMap("user", user)).getResult();
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

}
