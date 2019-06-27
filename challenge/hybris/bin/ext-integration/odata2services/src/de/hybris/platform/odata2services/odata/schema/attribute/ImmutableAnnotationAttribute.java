/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.schema.attribute;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

public class ImmutableAnnotationAttribute extends AnnotationAttribute
{
	private boolean nameAssigned;
	private boolean textAssigned;

	@Override
	public AnnotationAttribute setName(final String name)
	{
		if (nameAssigned)
		{
			throw new UnsupportedOperationException("Annotation name cannot be changed once assigned");
		}
		nameAssigned = true;
		return super.setName(name);
	}

	@Override
	public AnnotationAttribute setText(final String text)
	{
		if (textAssigned)
		{
			throw new UnsupportedOperationException("AnnotationAttribute text cannot be changed once assigned");
		}
		textAssigned = true;
		return super.setText(text);
	}
}