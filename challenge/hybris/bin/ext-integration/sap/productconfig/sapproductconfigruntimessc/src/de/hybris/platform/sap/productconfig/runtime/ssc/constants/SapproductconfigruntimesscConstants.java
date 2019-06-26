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
package de.hybris.platform.sap.productconfig.runtime.ssc.constants;



/**
 * Global class for all Sapproductconfigruntimessc constants.
 */
public final class SapproductconfigruntimesscConstants extends GeneratedSapproductconfigruntimesscConstants
{
	/**
	 * SSC-Runtime Environment 'hybris' requires an running hybris server. This should be used in productive mode.
	 */
	public static final String RUNTIME_ENVIRONMENT_HYBRIS = "hybris";
	/**
	 * SSC-Runtime Environment 'standalone' does not required a running hybris, however this means that there is also no
	 * caching of SSC related data. Hence this is not suitable for productive mode, but can be usefull for excuting unit
	 * and standalone integration tests
	 */
	public static final String RUNTIME_ENVIRONMENT_STANDALONE = "standalone";
	/**
	 * SSC property key for the runtime environemnt
	 */
	public static final String RUNTIME_ENVIRONMENT = "runtimeEnvironment";

	/**
	 * Backoffice field for the pricing procedure in SSC context
	 */
	public static final String CONFIGURATION_PRICING_PROCEDURE = "sapproductconfig_pricingprocedure";
	/**
	 * Backoffice field for the condition function for the base price in SSC context
	 */
	public static final String CONFIGURATION_CONDITION_FUNCTION_BASE_PRICE = "sapproductconfig_condfunc_baseprice";
	/**
	 * Backoffice field for the condition function for the selected options price in SSC context
	 */
	public static final String CONFIGURATION_CONDITION_FUNCTION_SELECTED_OPTIONS = "sapproductconfig_condfunc_selectedoptions";

	/**
	 * Represents the marker of the explanation section of the explanation long text.
	 */
	public static final String EXPLANATION = "&EXPLANATION&";

	/**
	 * Represents the marker of the documentation section of the explanation long text.
	 */
	public static final String DOCUMENTATION = "&DOCUMENTATION&";

	/**
	 * Product Type "MARA"
	 */
	public static final String PRODUCT_TYPE_MARA = "MARA";

	/**
	 * Data Source Prefix "crm"
	 */
	public static final String DATA_SOURCE_CRM = "crm";

	/**
	 * Pricing attribute name "VKORG" Sales Organization
	 */
	public static final String PRICING_ATTRIBUTE_VKORG = "VKORG";

	/**
	 * Pricing attribute name "VTWEG" Distribution Channel
	 */
	public static final String PRICING_ATTRIBUTE_VTWEG = "VTWEG";

	/**
	 * Pricing attribute name "SPART" Division
	 */
	public static final String PRICING_ATTRIBUTE_SPART = "SPART";

	/**
	 * Pricing attribute name "HEADER_SPART": Division on header level. This is needed due to differences in SAP CRM and
	 * ERP data model. SSC runs on SAP CRM data model. In the latter data model, the division does not need to exist on
	 * header level necessarily (in ERP, it always exists as part of the sales area).
	 */
	public static final String PRICING_ATTRIBUTE_HEADER_SPART = "HEADER_SPART";

	/**
	 * Pricing attribute name "KUNNR" Customer Number
	 */
	public static final String PRICING_ATTRIBUTE_KUNNR = "KUNNR";

	/**
	 * Pricing attribute name "LAND1" Country Key
	 */
	public static final String PRICING_ATTRIBUTE_LAND1 = "LAND1";

	/**
	 * Pricing attribute name "KONDA" Customer Price Group
	 */
	public static final String PRICING_ATTRIBUTE_KONDA = "KONDA";

	/**
	 * Pricing attribute name "KONWA" Currency
	 */
	public static final String PRICING_ATTRIBUTE_KONWA = "KONWA";

	/**
	 * Pricing attribute name "PMATN" Pricing Reference Material
	 */
	public static final String PRICING_ATTRIBUTE_PMATN = "PMATN";

	/**
	 * Pricing attribute name "PRSFD" Pricing indicator
	 */
	public static final String PRICING_ATTRIBUTE_PRSFD = "PRSFD";

	/**
	 * Application "V"
	 */
	public static final String APPLICATION_V = "V";

	/**
	 * Usage "A"
	 */
	public static final String USAGE_A = "A";

	/**
	 * Default timestamp
	 */
	public static final String DET_DEFAULT_TIMESTAMP = "DET_DEFAULT_TIMESTAMP";

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

	private SapproductconfigruntimesscConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
