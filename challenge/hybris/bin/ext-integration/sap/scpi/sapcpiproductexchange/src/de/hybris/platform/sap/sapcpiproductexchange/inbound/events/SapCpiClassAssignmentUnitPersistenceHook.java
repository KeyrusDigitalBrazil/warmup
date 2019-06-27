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
package de.hybris.platform.sap.sapcpiproductexchange.inbound.events;

import java.util.Optional;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.ClassificationSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import org.springframework.beans.factory.annotation.Required;

public class SapCpiClassAssignmentUnitPersistenceHook implements PrePersistHook {

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiClassAssignmentUnitPersistenceHook.class);
	private ClassificationSystemService classificationSystemService;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof ClassAttributeAssignmentModel)
		{

			LOG.info("The persistence hook sapCpiClassAssignmentUnitPersistenceHook is called!");

			final ClassAttributeAssignmentModel assignment = (ClassAttributeAssignmentModel) item;
			String unitCode = assignment.getSapCpiAssignmentUnitCode();

			assignment.setUnit(unitCode==null||unitCode.matches("\\s*")?null:classificationSystemService.getAttributeUnitForCode(assignment.getSystemVersion(), unitCode));
			assignment.setSapCpiAssignmentUnitCode(null);

			return Optional.of(item);
		}

		return Optional.of(item);
	}

	protected ClassificationSystemService getClassificationSystemService()
	{
		return classificationSystemService;
	}

	@Required
	public void setClassificationSystemService(ClassificationSystemService classificationSystemService)
	{
		this.classificationSystemService = classificationSystemService;
	}
}
