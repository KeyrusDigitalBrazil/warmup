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
package de.hybris.platform.sap.sapmodel.services;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;


/**
 * Interface to provide access to UnitModel information for a given product SAPCode
 */
public interface SAPUnitService extends UnitService
{


	/**
	 * Get UnitModel for a given product SAPCode
	 * @param code String representation of SAPCode
	 * @return Returns UnitModel object associated with provided SAPCode
	 */
	public UnitModel getUnitForSAPCode(final String code);

}
