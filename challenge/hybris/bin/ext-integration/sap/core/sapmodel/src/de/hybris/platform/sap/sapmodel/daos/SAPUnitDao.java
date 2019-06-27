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
package de.hybris.platform.sap.sapmodel.daos;

import java.util.List;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.daos.UnitDao;

public interface SAPUnitDao extends UnitDao
{
	
	
	public List<UnitModel> findUnitBySAPUnitCode(final String unitType);
}
