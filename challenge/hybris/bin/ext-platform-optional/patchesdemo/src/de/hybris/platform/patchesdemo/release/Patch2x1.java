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

import de.hybris.platform.patches.Rerunnable;
import de.hybris.platform.patchesdemo.structure.Release;
import de.hybris.platform.patchesdemo.structure.StructureState;


/**
 * Example patch doing nothing at all.
 */
public class Patch2x1 extends AbstractDemoPatch implements SimpleDemoPatch, Rerunnable
{

	public Patch2x1()
	{
		super("2_1", "02_01", Release.R2, StructureState.V3);
	}
}
