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
package de.hybris.platform.sap.productconfig.runtime.mock;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


/**
 * Interface for Configuration Mocks. There should be one implementation per KB.
 */
public interface ConfigMock
{
	ConfigModel createDefaultConfiguration();

	void checkModel(ConfigModel model);

	void checkInstance(ConfigModel model, InstanceModel instance);

	void checkCstic(ConfigModel model, InstanceModel instance, CsticModel cstic);

	void setConfigId(String nextConfigId);

	default boolean isChangeabeleVariant()
	{
		return false;
	}

}
