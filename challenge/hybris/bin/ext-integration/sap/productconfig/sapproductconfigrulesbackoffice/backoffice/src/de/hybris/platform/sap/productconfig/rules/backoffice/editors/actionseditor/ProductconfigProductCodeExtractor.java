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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.List;


/**
 * Provides a list of product codes used in "Currently configuring product" conditions in a product configuration rule
 */
public interface ProductconfigProductCodeExtractor
{

	/**
	 * Retrieves a list of product codes used in "Currently configuring product" conditions in a product configuration
	 * rule
	 *
	 * @param ruleModelRef
	 *           Productconfig source rule model
	 * @return List of product codes
	 */
	List<String> retrieveProductCodeList(ProductConfigSourceRuleModel ruleModelRef);

}
