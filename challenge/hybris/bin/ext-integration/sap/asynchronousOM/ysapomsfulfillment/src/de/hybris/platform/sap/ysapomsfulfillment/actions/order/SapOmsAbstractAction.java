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
package de.hybris.platform.sap.ysapomsfulfillment.actions.order;

import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;

public abstract class SapOmsAbstractAction<T extends BusinessProcessModel> extends AbstractAction<T> {


	protected enum Transition {
		
		OK, WAIT;

		public static Set<String> getStringValues() {
			
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values()) {
				res.add(transition.toString());
			}
			
			return res;
		}
	
	}

	@Override
	public Set<String> getTransitions() {
		return Transition.getStringValues();
	}

}
