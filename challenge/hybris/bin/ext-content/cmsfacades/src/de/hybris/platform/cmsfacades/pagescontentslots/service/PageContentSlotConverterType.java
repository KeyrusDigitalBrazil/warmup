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
package de.hybris.platform.cmsfacades.pagescontentslots.service;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;


/**
 * Represents meta-information about a <code>CMSRelationModel</code> class and the converter required to convert the
 * information to a <code>PageContentSlotData</code>
 */
public interface PageContentSlotConverterType
{
	Class<? extends CMSRelationModel> getClassType();

	void setClassType(Class<? extends CMSRelationModel> classType);

	AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> getConverter();

	void setConverter(AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> converter);
}
