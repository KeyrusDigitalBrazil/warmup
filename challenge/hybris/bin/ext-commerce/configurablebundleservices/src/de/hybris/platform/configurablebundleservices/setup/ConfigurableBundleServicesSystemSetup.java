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

package de.hybris.platform.configurablebundleservices.setup;

import de.hybris.platform.commerceservices.setup.CommerceServicesSystemSetup;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;


/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 */
@SystemSetup(extension = ConfigurableBundleServicesConstants.EXTENSIONNAME)
public class ConfigurableBundleServicesSystemSetup extends CommerceServicesSystemSetup
{
	/**
	 * This method will be called during the system initialization.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		importImpexFile(context, "/impex/bundleconstraints.impex", false);
	}
}
