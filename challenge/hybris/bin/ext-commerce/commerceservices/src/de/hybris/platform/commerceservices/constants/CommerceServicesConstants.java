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
package de.hybris.platform.commerceservices.constants;

import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.util.DiscountValue;


/**
 * Global class for all CommerceServices constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })
public final class CommerceServicesConstants extends GeneratedCommerceServicesConstants
{
	/**
	 * If true, configured hooks will be called before and after addToCart calls in commerce services.
	 */
	public static final String ADDTOCARTHOOK_ENABLED = "commerceservices.commerceaddtocartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart calculation calls in commerce services.
	 */
	public static final String CARTCALCULATIONHOOK_ENABLED = "commerceservices.commercecartcalculationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after authorize payment calls in commerce services.
	 */
	public static final String AUTHORIZEPAYMENTHOOK_ENABLED = "commerceservices.authorizepaymentmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after place order calls in commerce services.
	 */
	public static final String PLACEORDERHOOK_ENABLED = "commerceservices.commerceplaceordermethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after update cart entries calls in commerce services.
	 */
	public static final String UPDATECARTENTRYHOOK_ENABLED = "commerceservices.commerceupdatecartentryhook.enabled";
	/**
	 * If true, configured hooks will be called before and after addToCart calls in commerce services.
	 */
	public static final String SAVECARTHOOK_ENABLED = "commerceservices.commercesavecartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after flag for deletion calls in commerce services.
	 */
	public static final String FLAGFORDELETIONHOOK_ENABLED = "commerceservices.commerceflagfordeletionmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after
	 * {@link CommerceSaveCartService#cloneSavedCart(de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter)}
	 * calls in commerce services.
	 */
	public static final String CLONESAVEDCARTHOOK_ENABLED = "commerceservices.commerceclonesavedcartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before cart restoration in commerce services.
	 */
	public static final String SAVECARTRESTORATIONHOOK_ENABLED = "commerceservices.commercesavecartrestorationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart validation calls in commerce services.
	 */
	public static final String CARTVALIDATIONHOOK_ENABLED = "commerceservices.commercecartvalidationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart validation calls in commerce services.
	 */
	public static final String REMOVEENTRYGROUPHOOK_ENABLED = "commerceservices.commerceremoveentrygroupmethodhook.enabled";
	/**
	 * Default parent group id for all seller agents
	 */
	public static final String SELLER_AGENT_GROUP_UID = "salesemployeegroup";
	/**
	 * Default parent group id for all seller approver agents
	 */
	public static final String SELLER_APPROVER_AGENT_GROUP_UID = "salesapprovergroup";
	/**
	 * Defines organization related user groups(=roles).
	 */
	public static final String ORGANIZATION_ROLES = "commerceservices.organization.roles";
	/**
	 * Defines organization related admin groups.
	 */
	public static final String ORGANIZATION_ROLES_ADMIN_GROUPS = "commerceservices.organization.roles.admin.groups";
	/**
	 * Defines quote seller auto approval's threshold amount
	 */
	public static final String QUOTE_APPROVAL_THRESHOLD = "commerceservices.quote.seller.auto.approval.threshold";
	/**
	 * Defines the configurable default offer validity period in days
	 */
	public static final String QUOTE_DEFAULT_OFFER_VALIDITY_PERIOD_IN_DAYS = "commerceservices.quote.default.offer.validity.period.days";
	/**
	 * Defines the minimum offer validity period in days
	 */
	public static final String QUOTE_MIN_OFFER_VALIDITY_PERIOD_IN_DAYS = "commerceservices.quote.min.offer.validity.period.days";
	/**
	 * Discount code for quote specific {@link DiscountValue} objects.
	 */
	public static final String QUOTE_DISCOUNT_CODE = "QuoteDiscount";
	/**
	 * Defines the default minimum threshold for initiating a quote negotiation.
	 */
	public static final String QUOTE_REQUEST_INITIATION_THRESHOLD = "quote.request.initiation.threshold";
	/**
	 * If true, OrgUnit path will be generated or updated.
	 */
	public static final String ORG_UNIT_PATH_GENERATION_ENABLED = "commerceservices.org.unit.path.generation.enabled";
}
