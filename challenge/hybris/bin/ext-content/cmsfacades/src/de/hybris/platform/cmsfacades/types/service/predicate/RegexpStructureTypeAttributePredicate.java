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
package de.hybris.platform.cmsfacades.types.service.predicate;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if the declaring enclosing type {@link de.hybris.platform.core.model.ItemModel#_TYPECODE} and the attribute 
 * type match with <code>{CMS_TYPECODE}@{ATTRIBUTE_ITEMTYPE}:{ATTRIBUTE_TYPECODE}</code>.
 *
 * The <code>pattern</code> attribute is regular expression String that will be matched against the enclosing type' TYPECODE and 
 * the combination of the attribute's item type and type code.
 * 
 * For example, suppose that in an instance of this Predicate, the <code>pattern</code> is <code>.*@AtomicType:java.lang.String</code>.  
 * Then this Predicate will return <code>true</code> for all any Item Type that has an attribute of the <code>java.lang.String</code> Java type.
 */
public class RegexpStructureTypeAttributePredicate implements Predicate<AttributeDescriptorModel>
{
	private String pattern;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		/*
		 * at this stage the AttributeDescriptorModel.getDeclaringEnclosingType() can be null if used within a populator on a custom
		 * structure declaration for a non persistent property
		 */
		final String key = (attributeDescriptor.getDeclaringEnclosingType() != null ? attributeDescriptor
				.getDeclaringEnclosingType().getCode() : "")
				+ "@"
				+ attributeDescriptor.getAttributeType().getItemtype()
				+ ":"
				+ attributeDescriptor.getAttributeType().getCode();
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(key);
		return m.matches();
	}

	protected String getPattern()
	{
		return pattern;
	}

	@Required
	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}
}
