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
package de.hybris.platform.b2b.punchout.actions;

/**
 * A generic action that takes an input and depending on its purpose may populate the output.
 * 
 * @param <In>
 *           the input type
 * @param <Out>
 *           the output type
 */
public interface PunchOutProcessingAction<In, Out>
{

	/**
	 * Processes the input and populates the output.
	 * 
	 * @param input
	 *           the input object
	 * @param output
	 *           the output object
	 */
	void process(In input, Out output);

}
