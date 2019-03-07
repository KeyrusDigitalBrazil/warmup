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
package br.com.keyrus.warmup.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import br.com.keyrus.warmup.core.constants.KeyruswarmupCoreConstants;
import br.com.keyrus.warmup.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class KeyruswarmupCoreManager extends GeneratedKeyruswarmupCoreManager
{
	public static final KeyruswarmupCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (KeyruswarmupCoreManager) em.getExtension(KeyruswarmupCoreConstants.EXTENSIONNAME);
	}
}
