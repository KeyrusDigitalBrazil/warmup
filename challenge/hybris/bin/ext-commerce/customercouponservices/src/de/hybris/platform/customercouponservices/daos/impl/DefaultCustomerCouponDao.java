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
package de.hybris.platform.customercouponservices.daos.impl;

import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.CatForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;



/**
 * Default implementation of {@link CustomerCouponDao}
 */
public class DefaultCustomerCouponDao extends DefaultGenericDao<CustomerCouponModel> implements CustomerCouponDao
{
	private static final String CUSTOMERCOUPON_CUSTOMER_RELATION = "CustomerCoupon2Customer";
	private static final String CUSTOMER = "customer";
	private static final String CUSTOMERUID = "customerUid";

	private static final String SEARCH_PROMOTION_RULE_QUERY = "select {pr." + PromotionSourceRuleModel.PK + "} from {"
			+ PromotionSourceRuleModel._TYPECODE + " as pr}, {" + RuleStatus._TYPECODE + " as rs} where {pr."
			+ PromotionSourceRuleModel.STATUS + "} = {rs.pk} and {rs.code} = '" + RuleStatus.PUBLISHED.getCode() + "' and {pr."
			+ PromotionSourceRuleModel.CODE + "} = ?code";

	private static final String FIND_PROMOTION_RULE_FOR_PRODUCT = "SELECT {r.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{ProductForPromotionSourceRule as rel}, {" + RuleStatus._TYPECODE
			+ " as rs} WHERE {r.status} = {rs.pk} and {rs.code} " + "= '" + RuleStatus.PUBLISHED.getCode()
			+ "' AND {rel.rule} = {r.pk} AND {rel.productCode} =?productCode";

	private static final String FIND_EXCLUDED_PROMOTION_RULE_FOR_PRODUCT = "SELECT {r.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{ExcludedProductForRule as rel}, {" + RuleStatus._TYPECODE + " as rs} WHERE {r.status} = {rs.pk} and {rs.code} "
			+ "= '" + RuleStatus.PUBLISHED.getCode() + "' AND {rel.rule} = {r.pk} AND {rel.productCode} =?productCode";

	private static final String FIND_PRODUCT_FOR_PROMOTION_RULE = "SELECT {rel.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{ProductForPromotionSourceRule as rel}, {" + RuleStatus._TYPECODE
			+ " as rs} WHERE {r.status} = {rs.pk} and {rs.code} " + "= '" + RuleStatus.PUBLISHED.getCode()
			+ "' AND {rel.rule} = {r.pk} AND {r.code} =?code";

	private static final String FIND_PROMOTION_RULE_FOR_CATEGORY = "SELECT {r.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{CatForPromotionSourceRule as rel}, {" + RuleStatus._TYPECODE + " as rs} WHERE {r.status} = {rs.pk} AND {rs.code} "
			+ "= '" + RuleStatus.PUBLISHED.getCode() + "' AND {rel.rule} = {r.pk} AND {rel.categoryCode} =?categoryCode";

	private static final String FIND_EXCLUDED_PROMOTION_RULE_FOR_CATEGORY = "SELECT {r.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{ExcludedCatForRule as rel}, {" + RuleStatus._TYPECODE + " as rs} WHERE {r.status} = {rs.pk} AND {rs.code} " + "= '"
			+ RuleStatus.PUBLISHED.getCode() + "' AND {rel.rule} = {r.pk} AND {rel.categoryCode} =?categoryCode";

	private static final String FIND_CATEGORY_FOR_PROMOTION_RULE = "SELECT {rel.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{CatForPromotionSourceRule as rel}, {" + RuleStatus._TYPECODE + " as rs} WHERE {r.status} = {rs.pk} AND {rs.code} "
			+ "= '" + RuleStatus.PUBLISHED.getCode() + "' AND {rel.rule} = {r.pk} AND {r.code} =?code";

	private static final String FIND_CUSTOMER_COUPON_FOR_PROMOTION_RULE = "SELECT {cc.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{CustomerCouponForPromotionSourceRule as rel},{CustomerCoupon as cc}, {" + RuleStatus._TYPECODE
			+ " as rs} WHERE {r.status} = {rs.pk} AND {rs.code} " + "= '" + RuleStatus.PUBLISHED.getCode()
			+ "' AND {rel.rule} = {r.pk} AND {rel.customerCouponCode} = {cc.couponId} AND {r.code} =?code";

	private static final String FIND_PROMOTION_RULE_FOR_COUPONCODE = "SELECT {r.pk} as pr FROM {PromotionSourceRule as r}, "
			+ "{CustomerCouponForPromotionSourceRule as rel}, {" + RuleStatus._TYPECODE
			+ " as rs} WHERE {r.status} = {rs.pk} AND {rs.code} " + "= '" + RuleStatus.PUBLISHED.getCode()
			+ "' AND {rel.rule} = {r.pk} AND {rel.customerCouponCode} =?customerCouponCode";

	private static final String SEARCH_COUPON_QUERY = "select {cp:pk} from {" + CustomerCouponModel._TYPECODE + " as cp left join "
			+ CUSTOMERCOUPON_CUSTOMER_RELATION + " as ccr on {ccr:source} = {cp:pk} left join " + CustomerModel._TYPECODE
			+ " as cst on {cst:pk}={ccr:target}} where {" + CustomerCouponModel.ACTIVE + "} = 1 and {" + CustomerCouponModel.ENDDATE
			+ "} > ?now and {cst:" + CustomerModel.PK + "} = ?customer ";

	private static final String SEARCH_EFFECTIVE_COUPON_QUERY = "select {cp.pk} from {" + CustomerCouponModel._TYPECODE
			+ " as cp left join " + CUSTOMERCOUPON_CUSTOMER_RELATION + " as ccr on {ccr:source} = {cp:pk} left join "
			+ CustomerModel._TYPECODE + " as cst on {cst.pk}={ccr:target} } where {" + CustomerCouponModel.ACTIVE + "} = 1 and {"
			+ CustomerCouponModel.ENDDATE + "} > ?now and {" + CustomerCouponModel.STARTDATE + " } <?now and {cst."
			+ CustomerModel.UID + "} = ?customerUid";

	private static final String CHECK_EFFECTIVE_COUPON_QUERY = "select {cp.pk} from {" + CustomerCouponModel._TYPECODE
			+ " as cp left join " + CUSTOMERCOUPON_CUSTOMER_RELATION + " as ccr on {ccr:source} = {cp:pk} left join "
			+ CustomerModel._TYPECODE + " as cst on {cst.pk}={ccr:target} } where {" + CustomerCouponModel.ACTIVE + "} = 1 and {"
			+ CustomerCouponModel.ENDDATE + "} > ?now and {" + CustomerCouponModel.STARTDATE + " } <?now and {cst."
			+ CustomerModel.UID + "} = ?customerUid and {" + CustomerCouponModel.COUPONID + " } = ?couponCode";

	private static final String CHECK_ASIGNED_COUPON_QUERY = "select {cp.pk} from {" + CustomerCouponModel._TYPECODE
			+ " as cp left join " + CUSTOMERCOUPON_CUSTOMER_RELATION + " as ccr on {ccr:source} = {cp:pk} left join "
			+ CustomerModel._TYPECODE + " as cst on {cst.pk}={ccr:target} } where {" + CustomerCouponModel.ACTIVE + "} = 1 and {cst."
			+ CustomerModel.UID + "} = ?customerUid and {" + CustomerCouponModel.COUPONID + " } = ?couponCode";

	private static final String FIND_ALL_CUSTOMERCOUPON_FORPROMOTIONSOURCERULE_QUERY = "SELECT {"
			+ CustomerCouponForPromotionSourceRuleModel.PK + "} FROM {" + CustomerCouponForPromotionSourceRuleModel._TYPECODE
			+ "} WHERE {" + CustomerCouponForPromotionSourceRuleModel.RULE + "} = ?rule";

	private static final String FIND_ALL_CUSTOMERCOUPON_FORPROMOTIONSOURCERULE_FQL = "SELECT {"
			+ CustomerCouponForPromotionSourceRuleModel.PK + "} FROM {" + CustomerCouponForPromotionSourceRuleModel._TYPECODE
			+ "} WHERE {" + CustomerCouponForPromotionSourceRuleModel.RULE + "} = ?rule AND EXISTS ({{"
			+ "SELECT {dr.promotion} FROM {" + RuleBasedPromotionModel._TYPECODE + " as rbp JOIN " + DroolsRuleModel._TYPECODE
			+ " as dr ON {dr.pk} = {rbp.rule} JOIN " + DroolsKIEBaseModel._TYPECODE + " as kb ON {kb.pk} = {dr.kieBase} JOIN "
			+ DroolsKIEModuleModel._TYPECODE + " as km ON {kb.kieModule} = {km.pk} JOIN " + AbstractRuleModel._TYPECODE
			+ " as rule ON {rule.pk} = {dr.sourceRule}} WHERE {km.name} = ?name AND {rule.pk} = ?rule }})";

	private static final String FIND_ASSIGNABLE_COUPONS_QUERY = "SELECT {C:" + CustomerCouponModel.PK + "} FROM {"
			+ CustomerCouponModel._TYPECODE + " AS C} WHERE {C:" + CustomerCouponModel.ASSIGNABLE + "} = 1 AND {C:"
			+ CustomerCouponModel.STARTDATE + "} IS NOT NULL AND {C:" + CustomerCouponModel.ENDDATE + "} IS NOT NULL AND {C:"
			+ CustomerCouponModel.ENDDATE + "} >= ?now AND {C:" + CustomerCouponModel.ACTIVE + "} = 1 AND {C:"
			+ CustomerCouponModel.PK + "} NOT IN ({{SELECT {CC:pk} FROM {" + CustomerCouponModel._TYPECODE
			+ " AS CC JOIN CUSTOMERCOUPON2CUSTOMER AS R ON {CC:pk} = {R:source} JOIN " + CustomerModel._TYPECODE
			+ " AS U ON {R:target} = {U:pk}} WHERE {U:pk} = ?customer}}) ";

	private static final String FIND_ASSIGNED_COUPONS_BY_CUSTOMER = "SELECT {C:" + CustomerCouponModel.PK + "} FROM {"
			+ CustomerCouponModel._TYPECODE + " AS C LEFT JOIN CustomerCoupon2Customer AS R ON {C:" + CustomerCouponModel.PK
			+ "} = {R:source} LEFT JOIN " + CustomerModel._TYPECODE + " AS U ON {U:" + CustomerModel.PK + "} = {R:target}} WHERE {U:"
			+ CustomerModel.PK + "} = ?customer AND {C:" + CustomerCouponModel.ENDDATE + "} >= ?now ";

	private static final String BY_STARTDATE_ASC = "ORDER BY {" + AbstractPromotionModel.STARTDATE + "} ASC";
	private static final String BY_STARTDATE_DESC = "ORDER BY {" + AbstractPromotionModel.STARTDATE + "} DESC";
	private static final String BY_ENDDATE_ASC = "ORDER BY {" + AbstractPromotionModel.ENDDATE + "} ASC";
	private static final String BY_ENDDATE_DESC = "ORDER BY {" + AbstractPromotionModel.ENDDATE + "} DESC";

	private PagedFlexibleSearchService pagedFlexibleSearchService;
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;
	private Map<String, String> customerCouponSortCodeToQueryAlias;

	public DefaultCustomerCouponDao()
	{
		super(CustomerCouponModel._TYPECODE);
	}

	@Override
	public Optional<PromotionSourceRuleModel> findPromotionSourceRuleByCode(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_PROMOTION_RULE_QUERY);
		query.addQueryParameter("code", code);
		final List<PromotionSourceRuleModel> result = getFlexibleSearchService().<PromotionSourceRuleModel> search(query)
				.getResult();
		if (CollectionUtils.isNotEmpty(result))
		{
			return Optional.of(result.get(0));
		}
		return Optional.empty();
	}

	@Override
	public List<PromotionSourceRuleModel> findPromotionSourceRuleByProduct(final String productCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PROMOTION_RULE_FOR_PRODUCT);
		query.addQueryParameter("productCode", productCode);
		return getFlexibleSearchService().<PromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public List<PromotionSourceRuleModel> findExclPromotionSourceRuleByProduct(final String productCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_EXCLUDED_PROMOTION_RULE_FOR_PRODUCT);
		query.addQueryParameter("productCode", productCode);
		return getFlexibleSearchService().<PromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public List<PromotionSourceRuleModel> findPromotionSourceRuleByCategory(final String categoryCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PROMOTION_RULE_FOR_CATEGORY);
		query.addQueryParameter("categoryCode", categoryCode);
		return getFlexibleSearchService().<PromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public List<PromotionSourceRuleModel> findExclPromotionSourceRuleByCategory(final String categoryCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_EXCLUDED_PROMOTION_RULE_FOR_CATEGORY);
		query.addQueryParameter("categoryCode", categoryCode);
		return getFlexibleSearchService().<PromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public List<CustomerCouponModel> findCustomerCouponByPromotionSourceRule(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CUSTOMER_COUPON_FOR_PROMOTION_RULE);
		query.addQueryParameter("code", code);
		return getFlexibleSearchService().<CustomerCouponModel> search(query).getResult();
	}

	@Override
	public List<PromotionSourceRuleModel> findPromotionSourceRuleByCouponCode(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PROMOTION_RULE_FOR_COUPONCODE);
		query.addQueryParameter("customerCouponCode", code);
		return getFlexibleSearchService().<PromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public List<ProductForPromotionSourceRuleModel> findProductForPromotionSourceRuleByPromotion(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PRODUCT_FOR_PROMOTION_RULE);
		query.addQueryParameter("code", code);
		return getFlexibleSearchService().<ProductForPromotionSourceRuleModel> search(query).getResult();
	}


	@Override
	public List<CatForPromotionSourceRuleModel> findCategoryForPromotionSourceRuleByPromotion(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CATEGORY_FOR_PROMOTION_RULE);
		query.addQueryParameter("code", code);
		return getFlexibleSearchService().<CatForPromotionSourceRuleModel> search(query).getResult();
	}

	@Override
	public de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> findCustomerCouponsByCustomer(
			final CustomerModel customer, final PageableData pageableData)
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byStartDateAsc", SEARCH_COUPON_QUERY + BY_STARTDATE_ASC),
				createSortQueryData("byStartDateDesc", SEARCH_COUPON_QUERY + BY_STARTDATE_DESC),
				createSortQueryData("byEndDateAsc", SEARCH_COUPON_QUERY + BY_ENDDATE_ASC),
				createSortQueryData("byEndDateDesc", SEARCH_COUPON_QUERY + BY_ENDDATE_DESC));

		final Map<String, Object> params = new HashMap<>(0);
		params.put("now", new Date());
		params.put(CUSTOMER, customer);

		return getPagedFlexibleSearchService().search(sortQueries, "byEndDateAsc", params, pageableData);
	}


	@Override
	public List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(final PromotionSourceRuleModel rule)
	{
		return getFlexibleSearchService().<CustomerCouponForPromotionSourceRuleModel> search(
				FIND_ALL_CUSTOMERCOUPON_FORPROMOTIONSOURCERULE_QUERY, Collections.singletonMap("rule", rule)).getResult();
	}

	@Override
	public List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(final PromotionSourceRuleModel rule,
			final String moduleName)
	{
		final Map<String, Object> params = new HashMap<>(0);
		params.put("rule", rule);
		params.put("name", moduleName);

		return getFlexibleSearchService()
				.<CustomerCouponForPromotionSourceRuleModel> search(FIND_ALL_CUSTOMERCOUPON_FORPROMOTIONSOURCERULE_FQL, params)
				.getResult();
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	@Override
	public List<CustomerCouponModel> findEffectiveCustomerCouponsByCustomer(final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_EFFECTIVE_COUPON_QUERY);
		query.addQueryParameter("now", new Date());
		query.addQueryParameter(CUSTOMERUID, customer.getUid());
		return getFlexibleSearchService().<CustomerCouponModel> search(query).getResult();
	}

	@Override
	public boolean checkCustomerCouponAvailableForCustomer(final String couponCode, final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(CHECK_EFFECTIVE_COUPON_QUERY);
		query.addQueryParameter("now", new Date());
		query.addQueryParameter(CUSTOMERUID, customer.getUid());
		query.addQueryParameter("couponCode", couponCode);
		final int resultCount = getFlexibleSearchService().search(query).getTotalCount();
		return Boolean.valueOf(resultCount > 0);
	}

	@Override
	public int countAssignedCouponForCustomer(final String couponCode, final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(CHECK_ASIGNED_COUPON_QUERY);
		query.addQueryParameter(CUSTOMERUID, customer.getUid());
		query.addQueryParameter("couponCode", couponCode);
		return getFlexibleSearchService().search(query).getTotalCount();
	}

	@Override
	public List<CustomerCouponModel> findAssignableCoupons(final CustomerModel customer, final String text)
	{
		return findAssignmentCoupons(customer, text, FIND_ASSIGNABLE_COUPONS_QUERY);
	}

	@Override
	public List<CustomerCouponModel> findAssignedCouponsByCustomer(final CustomerModel customer, final String text)
	{
		return findAssignmentCoupons(customer, text, FIND_ASSIGNED_COUPONS_BY_CUSTOMER);
	}

	protected List<CustomerCouponModel> findAssignmentCoupons(final CustomerModel customer, final String text, final String query)
	{
		final StringBuilder fql = new StringBuilder(query);
		final Map<String, Object> params = new HashMap<>(0);
		params.put("now", Calendar.getInstance().getTime());
		params.put(CUSTOMER, customer);

		if (StringUtils.isNotBlank(text))
		{
			fql.append("AND (LOWER({C:");
			fql.append(CustomerCouponModel.COUPONID);
			fql.append("}) LIKE ?text OR LOWER({C:");
			fql.append(CustomerCouponModel.NAME);
			fql.append("}) LIKE ?text)");
			params.put("text", "%" + text.trim().toLowerCase(Locale.ROOT) + "%");
		}

		return getFlexibleSearchService().<CustomerCouponModel> search(fql.toString(), params).getResult();
	}

	@Override
	public SearchPageData<CustomerCouponModel> findPaginatedCouponsByCustomer(final CustomerModel customer,
			final SearchPageData searchPageData)
	{
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		parameter.setSearchPageData(searchPageData);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_COUPON_QUERY);
		query.addQueryParameter("now", Calendar.getInstance().getTime());
		query.addQueryParameter(CUSTOMER, customer);
		parameter.setFlexibleSearchQuery(query);

		parameter.setSortCodeToQueryAlias(getCustomerCouponSortCodeToQueryAlias());

		return getPaginatedFlexibleSearchService().search(parameter);
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

	protected PaginatedFlexibleSearchService getPaginatedFlexibleSearchService()
	{
		return paginatedFlexibleSearchService;
	}

	@Required
	public void setPaginatedFlexibleSearchService(final PaginatedFlexibleSearchService paginatedFlexibleSearchService)
	{
		this.paginatedFlexibleSearchService = paginatedFlexibleSearchService;
	}

	protected Map<String, String> getCustomerCouponSortCodeToQueryAlias()
	{
		return customerCouponSortCodeToQueryAlias;
	}

	@Required
	public void setCustomerCouponSortCodeToQueryAlias(final Map<String, String> customerCouponSortCodeToQueryAlias)
	{
		this.customerCouponSortCodeToQueryAlias = customerCouponSortCodeToQueryAlias;
	}

}
