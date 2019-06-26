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

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


/**
 * Helper class to generate unique UI keys.<br>
 */
public interface UniqueUIKeyGenerator
{
	/**
	 * @param instance
	 *           instance data
	 * @return unique key for a UIGroup representing an Instance
	 */
	String generateGroupIdForInstance(final InstanceModel instance);

	/**
	 * @param instance
	 *           instance the group belongs to
	 * @param csticModelGroup
	 *           group data
	 * @return unique key for a UIGroup representing an Group
	 */
	String generateGroupIdForGroup(final InstanceModel instance, final CsticGroup csticModelGroup);

	/**
	 * @param uiGroupId
	 *           UIGroup id
	 * @return extract instance id from the UIGroup Id
	 */
	String retrieveInstanceId(final String uiGroupId);

	/**
	 * Generates a Unique Key for a cstic, o a cstic value, if provided.
	 *
	 * @param model
	 *           cstic model
	 * @param value
	 *           cstic value model. If <code>null<code> the key is only specific for the cstic
	 * @param prefix
	 *           key prefix containing group and/or instance components
	 * @return unique key
	 */
	String generateCsticId(final CsticModel model, final CsticValueModel value, final String prefix);

	/**
	 * splits the given cstic UI key into its components
	 *
	 * @param csticUiKey
	 * @return a csticQualifier
	 */
	CsticQualifier splitId(String csticUiKey);

	/**
	 * creates a cstic ui key from the provided components
	 *
	 * @param csticQualifier
	 * @return unique Key
	 */
	String generateId(CsticQualifier csticQualifier);

}
