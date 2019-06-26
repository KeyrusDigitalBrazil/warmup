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
package de.hybris.platform.integrationbackoffice.services;

import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the write requests of the extension's widgets
 */
public class WriteService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WriteService.class);

	private ModelService modelService;
	private ReadService readService;

	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

	public void setReadService(final ReadService readService) {
		this.readService = readService;
	}

	/**
	 * Creates a base Integration Object.
	 * @param name the integration object's name
	 * @param type the type of integration object (Inbound, Outbound)
	 * @return an empty integration object
	 */
	public IntegrationObjectModel createIntegrationObject(final String name, final IntegrationType type) {
		final IntegrationObjectModel ioModel = modelService.create(IntegrationObjectModel.class);
		ioModel.setCode(name);
		ioModel.setIntegrationType(type);
		ioModel.setItems(Collections.emptySet());
		LOGGER.info("Integration object {} created", ioModel.getCode());
		return ioModel;
	}

	/**
	 * Clears the old definition of the IntehrationObjectModel. The model is not deleted, this method simply removes all
	 * of its items and their associated attributes.
	 * @param integrationObjectModel Model to be cleared
	 * @return The cleared model
	 */
	public IntegrationObjectModel clearIntegrationObject(final IntegrationObjectModel integrationObjectModel) {
		final Set<IntegrationObjectItemModel> ioItems = new HashSet<>(integrationObjectModel.getItems());

		// Clear the model service of any previous definition of the object looking to be persisted
		ioItems.forEach(ioi -> modelService.removeAll(ioi.getAttributes()));
		modelService.removeAll(ioItems);
		integrationObjectModel.setItems(null);
		ioItems.clear();
		return integrationObjectModel;
	}

	/**
	 * Iterates through the map containing the IntegrationObjectItemDefinitionModel (as the key) and
	 * the IntegrationObjectItemAttributeDefinitionModels (the list of DTOs)
	 * @param ioModel the integration object that will contain the integration object items and integration object item attributes
	 * @param objectMap a map with integration object item codes as keys and lists of integration object item attribute DTOs as values
	 * @return Definition of IntegrationObjkectModel to be saved.
	 */
	public IntegrationObjectModel createDefinitions(IntegrationObjectModel ioModel, Map<ComposedTypeModel, List<ListItemDTO>> objectMap) {

		final IntegrationObjectModel clearedIO = clearIntegrationObject(ioModel);
		final Set<IntegrationObjectItemModel> ioItems = new HashSet<>();

		objectMap.forEach((key, value) -> {
			// Set the IntegrationObjectItemModel and its properties
			final IntegrationObjectItemModel ioItem = buildIntegrationObjectItem(clearedIO, key);

			// Iterate through the list of DTOs creating IntegrationObjectItemAttributeModels
			final Set<IntegrationObjectItemAttributeModel> attributes = buildIntegrationObjectItemAttribute(value, ioItem);

			ioItem.setAttributes(attributes);
			ioItems.add(ioItem);
		});

		clearedIO.setItems(ioItems);
		final IntegrationObjectModel integrationObjectModel = setReturnIntegrationObjectItem(clearedIO);
		return integrationObjectModel;
	}

	/**
	 * Saves the IntegrationObjectModel to the model service
	 * @param ioModel Model to be saved
	 */
	public void persistDefinitons(IntegrationObjectModel ioModel) {
		modelService.save(ioModel);
		LOGGER.info("Integration object {} updated", ioModel.getCode());
	}

	IntegrationObjectItemModel buildIntegrationObjectItem(IntegrationObjectModel ioModel, ComposedTypeModel ctm) {
		final IntegrationObjectItemModel ioItem = modelService.create(IntegrationObjectItemModel.class);
		ioItem.setCode(ctm.getCode());
		ioItem.setIntegrationObject(ioModel);
		ioItem.setType(ctm);
		return ioItem;
	}

	Set<IntegrationObjectItemAttributeModel> buildIntegrationObjectItemAttribute(List<ListItemDTO> dtos, IntegrationObjectItemModel ioItem) {
		return dtos.stream().map(dto -> {
			final IntegrationObjectItemAttributeModel ioiaModel = modelService.create(IntegrationObjectItemAttributeModel.class);
			ioiaModel.setAttributeDescriptor(dto.getAttributeDescriptor());
			ioiaModel.setAttributeName(dto.getAttributeDescriptor().getQualifier());
			ioiaModel.setIntegrationObjectItem(ioItem);
			ioiaModel.setUnique(dto.getAttributeDescriptor().getUnique() || dto.isCustomUnique());
			ioiaModel.setReturnIntegrationObjectItem(null);
			ioiaModel.setAutoCreate(dto.isAutocreate());
			return ioiaModel;
		}).collect(Collectors.toSet());
	}

	IntegrationObjectModel setReturnIntegrationObjectItem(IntegrationObjectModel integrationObject) {
		final Set<IntegrationObjectItemModel> integrationObjectItems = integrationObject.getItems();
		final Set<IntegrationObjectItemAttributeModel> integrationObjectItemAttributes = new HashSet<>();
		integrationObjectItems.forEach(ioi -> integrationObjectItemAttributes.addAll(ioi.getAttributes()));

		integrationObjectItemAttributes.forEach(attribute -> {
			final String attributeCode = determineAttributeCode(attribute);
			if (attributeCode != null) {
				integrationObjectItems.forEach(item -> {
					if (attributeCode.equals(item.getCode())) {
						attribute.setReturnIntegrationObjectItem(item);
					}
				});
			}
		});

		return integrationObject;
	}

	private String determineAttributeCode(IntegrationObjectItemAttributeModel attribute) {
		final String attributeType = attribute.getAttributeDescriptor().getAttributeType().getItemtype();

		if (readService.isCollectionType(attributeType)) {
			return ((CollectionTypeModel) attribute.getAttributeDescriptor().getAttributeType()).getElementType().getCode();
		} else if (readService.isComposedType(attributeType) || readService.isEnumerationMetaType(attributeType)) {
			return attribute.getAttributeDescriptor().getAttributeType().getCode();
		} else {
			return null;
		}
	}

	/**
	 * Delete an integration object from the type system
	 * @param integrationObject the integration object to be deleted
	 */
	public void deleteIntegrationObject(IntegrationObjectModel integrationObject) {
		modelService.remove(integrationObject.getPk());
		LOGGER.info("Integration object {} deleted", integrationObject.getCode());
	}

}
