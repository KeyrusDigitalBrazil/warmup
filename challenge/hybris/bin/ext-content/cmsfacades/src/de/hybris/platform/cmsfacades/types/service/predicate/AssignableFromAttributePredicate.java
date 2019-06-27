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

import com.google.common.base.Preconditions;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if an attribute type is assignable from the input <code>typeCode</code> passed from
 * the configuration. If matches, will return true else false.
 * Optionally it will check as well whether the declaring enclosing type is assignable from <code>enclosingTypeCode</code> passed from
 * the configuration
 */
public class AssignableFromAttributePredicate implements Predicate<AttributeDescriptorModel>
{

	private String typeCode;
	private String enclosingTypeCode;

	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	private TypeService typeService;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		Preconditions.checkArgument(isNotBlank(this.getTypeCode()) || isNotBlank(this.getEnclosingTypeCode()), "either typeCode or enclosingTypeCode must be provided.");

		final BiPredicate<String, Class<?>> isTypeOf = (providedTypeCode, modelClass) ->
		{
			ComposedTypeModel composedTypeForCode = getTypeService().getComposedTypeForCode(providedTypeCode);
			Class<ItemModel> parentClass = getTypeService().getModelClass(composedTypeForCode);

			return parentClass.isAssignableFrom(modelClass);
		};

		boolean isAttributeAssignableFrom = true;
		if (isNotBlank(this.getTypeCode())){
			isAttributeAssignableFrom = isTypeOf.test(getTypeCode(), getAttributeDescriptorModelHelperService().getAttributeClass(attributeDescriptor));
		}

		boolean isEnclosingTypeAssignableFrom = true;
		if (isNotBlank(this.getEnclosingTypeCode())){
			isEnclosingTypeAssignableFrom = isTypeOf.test(getEnclosingTypeCode(), getAttributeDescriptorModelHelperService().getDeclaringEnclosingTypeClass(attributeDescriptor));
		}

		return isAttributeAssignableFrom && isEnclosingTypeAssignableFrom;
	}

	protected String getTypeCode()
	{
		return typeCode;
	}

	public void setTypeCode(final String typeCode)
	{
		this.typeCode = typeCode;
	}

	protected String getEnclosingTypeCode()
	{
		return enclosingTypeCode;
	}

	public void setEnclosingTypeCode(String enclosingTypeCode)
	{
		this.enclosingTypeCode = enclosingTypeCode;
	}

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;

	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

}
