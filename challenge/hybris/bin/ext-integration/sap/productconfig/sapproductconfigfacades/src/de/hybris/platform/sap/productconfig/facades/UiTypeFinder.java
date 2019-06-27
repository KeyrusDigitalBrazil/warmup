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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper to determine the UI-type for a given Characteristic. It will determine how the characteristic is rendered on
 * the UI.
 */
public interface UiTypeFinder
{
	/**
	 * @param model
	 * @return UIType that decides how the characteristic is rendered on the UI
	 */
	UiType findUiTypeForCstic(CsticModel model);

	/**
	 * @param model
	 * @param data
	 * @return UIType that decides how the characteristic is rendered on the UI
	 */
	UiType findUiTypeForCstic(CsticModel model, CsticData data);


	/**
	 * @param csticModel
	 * @return UIValidatioType that decides how the user input for this characteristic is validated
	 */
	UiValidationType findUiValidationTypeForCstic(CsticModel csticModel);

}
