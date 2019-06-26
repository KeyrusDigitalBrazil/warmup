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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


/**
 * Product configuration parameter renderer.
 */
public class ProductConfigParameterRenderer implements ComboitemRenderer<Object>
{
	@Override
	public void render(final Comboitem item, final Object data, final int index)
	{
		item.setValue(data);
		final String label = String.valueOf(data);
		item.setLabel(label);
	}
}
