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
package de.hybris.platform.marketplaceservices.vendor;

import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;


/**
 * Service to find a specific RuleEngineContext for Vendor ProductCatalogVersion
 */
public interface VendorPromotionRuleEngineContextService
{
	/**
	 * find a specific Promotion RuleEngineContext by name
	 *
	 * @param contextName
	 *           the specific name for Promotion RuleEngineContext
	 * @return the Promotion RuleEngineContext found
	 */
	AbstractRuleEngineContextModel findVendorRuleEngineContextByName(String contextName);


}
