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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;


/**
 * Default implementation of the {@link AbstractBundleRuleDao} for sub-type {@link DisableProductBundleRuleModel}.
 */
public class DefaultDisableProductBundleRuleDao extends AbstractBundleRuleDao<DisableProductBundleRuleModel>
{
	private static final String FIND_BUNDLE_RULES_BY_TARGET_PRODUCT = "SELECT {rule:" + DisableProductBundleRuleModel.PK
			+ "} FROM {" + DisableProductBundleRuleModel._TYPECODE + " AS rule JOIN "
			+ ProductModel._ABSTRACTBUNDLERULESTARGETPRODUCTSRELATION + " AS targetRel ON {targetRel:source}={rule:"
			+ DisableProductBundleRuleModel.PK + "}} WHERE {targetRel:target}=?product";

	private static final String FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_QUERY = FIND_BUNDLE_RULES_BY_TARGET_PRODUCT
			+ " AND {rule:" + DisableProductBundleRuleModel.BUNDLETEMPLATE + "}=?bundleTemplate";

	private static final String FIND_BUNDLE_RULES_BY_PRODUCT_AND_ROOT_TEMPLATE_QUERY = "SELECT DISTINCT {rule:"
			+ DisableProductBundleRuleModel.PK + "} FROM {" + DisableProductBundleRuleModel._TYPECODE + " AS rule JOIN "
			+ BundleTemplateModel._TYPECODE + " AS templ ON {templ:PK}={rule:" + DisableProductBundleRuleModel.BUNDLETEMPLATE
			+ "} JOIN " + ProductModel._ABSTRACTBUNDLERULESTARGETPRODUCTSRELATION
			+ " AS targetRel ON {targetRel:source}={rule:PK} JOIN " + ProductModel._ABSTRACTBUNDLERULESCONDITIONALPRODUCTSRELATION
			+ " AS condRel ON {condRel:source}={rule:PK}}" + " WHERE {templ:parentTemplate}=?rootBundleTemplate AND "
			+ " ({targetRel:target}=?product OR {condRel:target}=?product)";

	@Override
	public String getFindBundleRulesByTargetProductQuery()
	{
		return FIND_BUNDLE_RULES_BY_TARGET_PRODUCT;
	}

	@Override
	public String getFindBundleRulesByTargetProductAndTemplateQuery()
	{
		return FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_QUERY;
	}

	@Override
	public String getFindBundleRulesByProductAndRootTemplateQuery()
	{
		return FIND_BUNDLE_RULES_BY_PRODUCT_AND_ROOT_TEMPLATE_QUERY;
	}
}
