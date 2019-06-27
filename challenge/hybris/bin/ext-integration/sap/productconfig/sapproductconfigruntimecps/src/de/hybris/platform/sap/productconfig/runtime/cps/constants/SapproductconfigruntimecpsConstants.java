/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.constants;

/**
 * Global class for all Sapproductconfigruntimecps constants.
 */
public final class SapproductconfigruntimecpsConstants extends GeneratedSapproductconfigruntimecpsConstants
{
	/**
	 * cache key of product configuration cache container
	 */
	public static final String PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER = "productConfigCPSSessionAttributeContainer";
	/**
	 * General Group Id, used in CPS for all characteristics, which are not assigned to another group
	 */
	public static final String CPS_GENERAL_GROUP_ID = "$GENERAL";
	/**
	 * backoffice field name of the pricing procedure for cps
	 */
	public static final String CONFIGURATION_PRICING_PROCEDURE = "sapproductconfig_pricingprocedure_cps";
	/**
	 * backoffice field name of the confition function for the base price within cps
	 */
	public static final String CONFIGURATION_CONDITION_FUNCTION_BASE_PRICE = "sapproductconfig_condfunc_baseprice_cps";
	/**
	 * backoffice field name of the confition function for the selected options price within cps
	 */
	public static final String CONFIGURATION_CONDITION_FUNCTION_SELECTED_OPTIONS = "sapproductconfig_condfunc_selectedoptions_cps";


	/**
	 * Pricing attribute name for Sales Organization
	 */
	public static final String PRICING_ATTRIBUTE_SALES_ORG = "KOMK-VKORG";

	/**
	 * Pricing attribute name for Distribution Channel
	 */

	public static final String PRICING_ATTRIBUTE_DIST_CHANNEL = "KOMK-VTWEG";

	/**
	 * Pricing attribute name for Division
	 */
	public static final String PRICING_ATTRIBUTE_DIVISION = "KOMK-SPART";

	/**
	 * Pricing attribute name for Division (Item)
	 */
	public static final String PRICING_ATTRIBUTE_DIVISION_ITEM = "KOMP-SPART";

	/**
	 * Pricing attribute name for Customer Number
	 */
	public static final String PRICING_ATTRIBUTE_CUSTOMER_NUMBER = "KOMK-KUNNR";

	/**
	 * Pricing attribute name for Country Key
	 */
	public static final String PRICING_ATTRIBUTE_COUNTRY = "KOMK-LAND1";

	/**
	 * Pricing attribute name for Customer Price Group
	 */
	public static final String PRICING_ATTRIBUTE_CUSTOMER_PRICE_GROUP = "KOMK-KONDA";

	/**
	 * Pricing attribute name for Currency
	 */
	public static final String PRICING_ATTRIBUTE_CURRENCY = "KOMK-WAERK";

	/**
	 * Pricing attribute name for Pricing Reference Material
	 */
	public static final String PRICING_ATTRIBUTE_MATERIAL_NUMBER = "KOMP-PMATN";

	/**
	 * Pricing attribute name for Pricing indicator
	 */
	public static final String PRICING_ATTRIBUTE_PRSFD = "KOMP-PRSFD";

	/**
	 * Access Date name for Price date
	 */
	public static final String ACCESS_DATE_PRICE_DATE = "KOMK-PRSDT";


	/**
	 * Context attribute name "VBAK-ERDAT" Creation Date
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAK_ERDAT = "VBAK-ERDAT";

	/**
	 * Context attribute name "VBAP-KWMENG" Order Quantity
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAP_KWMENG = "VBAP-KWMENG";

	/**
	 * Context attribute name "VBAP-MATNR" Material Number
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAP_MATNR = "VBAP-MATNR";

	/**
	 * Context attribute name "VBAK-SPART" Division
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAK_SPART = "VBAK-SPART";

	/**
	 * Context attribute name "VBAK-VTWEG" Distribution Channel
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAK_VTWEG = "VBAK-VTWEG";

	/**
	 * Context attribute name "VBAK-VKORG" Sales Organization
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAK_VKORG = "VBAK-VKORG";

	/**
	 * Context attribute name "VBPA_AG-LAND1" AG Country Key
	 */
	public static final String CONTEXT_ATTRIBUTE_VBPA_AG_LAND1 = "VBPA_AG-LAND1";

	/**
	 * Context attribute name "VBPA_RG-LAND1" RG Country Key
	 */
	public static final String CONTEXT_ATTRIBUTE_VBPA_RG_LAND1 = "VBPA_RG-LAND1";

	/**
	 * Context attribute name "VBAK-KUNNR" Sold-to party
	 */
	public static final String CONTEXT_ATTRIBUTE_VBAK_KUNNR = "VBAK-KUNNR";

	/**
	 * Context attribute name "VBPA_AG-KUNNR" AG Customer Number
	 */
	public static final String CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR = "VBPA_AG-KUNNR";

	/**
	 * Context attribute name "VBPA_RG-KUNNR" RG Customer Number
	 */
	public static final String CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR = "VBPA_RG-KUNNR";

	/**
	 * Represents item type product
	 */
	public static final String ITEM_TYPE_MARA = "MARA";

	/**
	 * Represents item type class node
	 */
	public static final String ITEM_TYPE_KLAH = "KLAH";


	/**
	 * Suffix for original pricing input and result keys in cache
	 */
	public static final String ORIGINAL_PRICING = "_ORIGINAL";

	/**
	 * Required additional data parts for master data service call
	 */
	public static final String MASTER_DATA_ADDITIONAL_SELECTION = "products,classes,characteristics,characteristicSpecifics,bomItems,description";


	private SapproductconfigruntimecpsConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
