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
package de.hybris.platform.cmsfacades.util.dao;

import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


public interface CMSPageTypeDao extends Dao
{
	CMSPageTypeModel getCMSPageTypeByCode(String code);
}
