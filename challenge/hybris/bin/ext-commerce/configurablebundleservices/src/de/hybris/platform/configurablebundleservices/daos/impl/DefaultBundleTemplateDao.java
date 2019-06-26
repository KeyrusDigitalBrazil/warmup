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

package de.hybris.platform.configurablebundleservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.daos.BundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Default implementation of the {@link BundleTemplateDao}.
 */
public class DefaultBundleTemplateDao extends AbstractItemDao implements BundleTemplateDao
{
	private static final String RESTRICTION_ONLY_APPROVED_ARCHIVED = " AND {status} IN ( "
			+ " {{ SELECT {bts.pk} FROM { BundleTemplateStatus AS bts JOIN EnumerationValue AS ev ON {bts.status} = {ev.pk} } WHERE {ev.Code} IN ('approved', 'archived') }} "
			+ ")";

	private static final String RESTRICTION_ONLY_APPROVED = " AND {status} IN ( "
			+ " {{ SELECT {bts.pk} FROM { BundleTemplateStatus AS bts JOIN EnumerationValue AS ev ON {bts.status} = {ev.pk} } WHERE {ev.Code} = 'approved' }} "
			+ ")";

	private static final String FIND_BUNDLETEMPLATE_QUERY = "SELECT {" + BundleTemplateModel.PK + "} FROM {"
			+ BundleTemplateModel._TYPECODE + "} where {" + BundleTemplateModel.ID + "}= ?uid" + RESTRICTION_ONLY_APPROVED_ARCHIVED;

	private static final String FIND_BUNDLETEMPLATE_QUERY_BY_VERSION = "SELECT {" + BundleTemplateModel.PK + "} FROM {"
			+ BundleTemplateModel._TYPECODE + "} where {" + BundleTemplateModel.ID + "}= ?uid and  {" + BundleTemplateModel.VERSION
			+ "}=?version";

	private static final String FIND_BUNDLETEMPLATES_BY_PRODUCT_QUERY = "SELECT {bt:" + BundleTemplateModel.PK + "} FROM {"
			+ BundleTemplateModel._TYPECODE + " AS bt JOIN " + BundleTemplateModel._PRODUCTSBUNDLETEMPLATESRELATION
			+ " AS prodRel ON {prodRel:target}={bt:" + BundleTemplateModel.PK + "}} WHERE {prodRel:source}=?product AND {bt:"
			+ BundleTemplateModel.PARENTTEMPLATE + "} IS NOT NULL" + RESTRICTION_ONLY_APPROVED + " ORDER BY {bt:"
			+ BundleTemplateModel.ID + "} ASC";

	private static final String FIND_TEMPLATES_BY_MASTERORDER_AND_BUNDLENO_QUERY = "SELECT DISTINCT {"
			+ AbstractOrderEntryModel.BUNDLETEMPLATE + "} FROM {" + AbstractOrderEntryModel._TYPECODE + "} WHERE {"
			+ AbstractOrderEntryModel.ORDER + "}=?masterAbstractOrder AND {" + AbstractOrderEntryModel.BUNDLENO + "}=?bundleNo";

	private static final String FIND_ALL_ROOT_BUNDLETEMPLATES = "SELECT {" + BundleTemplateModel.PK + "} FROM {"
			+ BundleTemplateModel._TYPECODE + "}where {" + BundleTemplateModel.PARENTTEMPLATE + "} IS NULL AND {"
			+ BundleTemplateModel.CATALOGVERSION + "} = ?catalogVersion " + " ORDER BY {" + BundleTemplateModel.ID + "} ASC";

	private static final String FIND_ALL_APPROVED_ROOT_BUNDLETEMPLATES = "SELECT {" + BundleTemplateModel.PK + "} FROM {"
			+ BundleTemplateModel._TYPECODE + "}where {" + BundleTemplateModel.PARENTTEMPLATE + "} IS NULL AND {"
			+ BundleTemplateModel.CATALOGVERSION + "} = ?catalogVersion " + RESTRICTION_ONLY_APPROVED + " ORDER BY {"
			+ BundleTemplateModel.ID + "} ASC";

	private static final String FIND_ALL_ABSTRACTORDERENTRIES_BY_BUNDLETEMPLATE = "SELECT DISTINCT {" + AbstractOrderEntryModel.PK
			+ "} FROM {" + AbstractOrderEntryModel._TYPECODE + "} WHERE {" + AbstractOrderEntryModel.BUNDLETEMPLATE
			+ "}=?bundleTemplate";

	@Override
	@Nonnull
	public BundleTemplateModel findBundleTemplateById(final String bundleId)
	{
		validateParameterNotNullStandardMessage("bundleId", bundleId);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BUNDLETEMPLATE_QUERY);
		query.addQueryParameter("uid", bundleId);
		return searchUnique(query);
	}


	@Override
	@Nonnull
	public BundleTemplateModel findBundleTemplateByIdAndVersion(final String bundleId, final String version)
	{
		validateParameterNotNullStandardMessage("bundleId", bundleId);
		validateParameterNotNullStandardMessage("version", version);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BUNDLETEMPLATE_QUERY_BY_VERSION);
		query.addQueryParameter("uid", bundleId);
		query.addQueryParameter("version", version);
		return searchUnique(query);

	}


	@Override
	@Nonnull
	public List<BundleTemplateModel> findBundleTemplatesByProduct(final ProductModel productModel)
	{
		validateParameterNotNullStandardMessage("productModel", productModel);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BUNDLETEMPLATES_BY_PRODUCT_QUERY);
		query.addQueryParameter("product", productModel);
		final SearchResult<BundleTemplateModel> result = search(query);

		return result.getResult();
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> findTemplatesByMasterOrderAndBundleNo(final AbstractOrderModel masterAbstractOrder,
			final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterAbstractOrder", masterAbstractOrder);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_TEMPLATES_BY_MASTERORDER_AND_BUNDLENO_QUERY);
		query.addQueryParameter("masterAbstractOrder", masterAbstractOrder);
		query.addQueryParameter("bundleNo", Integer.valueOf(bundleNo));
		final SearchResult<BundleTemplateModel> result = search(query);

		return result.getResult();
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> findAllRootBundleTemplates(final CatalogVersionModel catalogVersion)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_ROOT_BUNDLETEMPLATES);
		query.addQueryParameter("catalogVersion", catalogVersion);
		final SearchResult<BundleTemplateModel> result = search(query);

		return result.getResult();
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> findAllApprovedRootBundleTemplates(final CatalogVersionModel catalogVersion)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_APPROVED_ROOT_BUNDLETEMPLATES);
		query.addQueryParameter("catalogVersion", catalogVersion);
		final SearchResult<BundleTemplateModel> result = search(query);

		return result.getResult();
	}

	@Override
	@Nonnull
	public List<AbstractOrderEntryModel> findAbstractOrderEntriesByBundleTemplate(final BundleTemplateModel bundleTemplate)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_ABSTRACTORDERENTRIES_BY_BUNDLETEMPLATE);
		query.addQueryParameter("bundleTemplate", bundleTemplate);
		final SearchResult<AbstractOrderEntryModel> result = search(query);

		return result.getResult();
	}
}
