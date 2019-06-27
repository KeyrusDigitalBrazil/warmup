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
package de.hybris.platform.patchesdemo.setup;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.patches.AbstractPatchesSystemSetup;
import de.hybris.platform.patchesdemo.constants.PatchesDemoConstants;

import java.util.List;


/**
 * Example of SystemSetup that is used for creating patches data.
 */
@SystemSetup(extension = PatchesDemoConstants.EXTENSIONNAME)
public class PatchesDemoSystemSetup extends AbstractPatchesSystemSetup
{
	@Override
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext setupContext)
	{
		super.createEssentialData(setupContext);
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param setupContext
	 *           the context provides the selected parameters and values
	 */
	@Override
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext setupContext)
	{
		super.createProjectData(setupContext);
	}

	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		return super.getInitializationOptions();
	}
}
