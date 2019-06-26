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
package de.hybris.platform.patchesdemo.release;

import de.hybris.platform.patches.Patch;
import de.hybris.platform.patches.Rerunnable;
import de.hybris.platform.patchesdemo.structure.Release;
import de.hybris.platform.patchesdemo.structure.StructureState;

import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;


/**
 * Example patch which is switched off by default as it throws major exception, which stops whole init/update process.
 * Should be switched on to demonstrate error handling or tracking of problems.
 */
public class MajorErrorPatch extends AbstractDemoPatch implements SimpleDemoPatch, Rerunnable
{

	public MajorErrorPatch()
	{
		super("major_error_patch", "patch_with_major_error", Release.E1, StructureState.LAST);
	}

	/**
	 * Creates project data. See {@link Patch#createProjectData(de.hybris.platform.patches.organisation.StructureState)}.
	 */
	@Override
	public void createProjectData(final de.hybris.platform.patches.organisation.StructureState structureState)
	{
		throw new DataSourceLookupFailureException("Data source is not accessible - major error");
	}
}
