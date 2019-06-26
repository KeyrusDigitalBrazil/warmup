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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.CLOSE_BRACE;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.COMMA;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.DOT;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.EMPTY_STRING;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.GET;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.HCI_PUBLICATION_STATUS_CREATED;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.HCI_PUBLICATION_STATUS_OK;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.IS;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.OPEN_BRACE;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.REPLICATED_TYPE_DATA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;

public class DefaultSaveEventHandler implements AfterSaveEventHandler {

	private ModelService modelService;
	private ConfigurationService configurationService;
	private Publisher publisher;
	private static final Logger LOGGER = LogManager.getLogger(DefaultSaveEventHandler.class);

	@Override
	public void handleEvent(AfterSaveEvent event) {

		try {
			ItemModel model = null;
			if(event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE) {
				model = modelService.get(event.getPk());
			}
			if (shouldHandle(event, model)) {
				ResponseData resData = handle(event, model);
				afterHandle(event, model, resData);
			}

		} catch (ModelLoadingException e) {
			LOGGER.error("Pk is not of itemModel: ", e);
		} catch (Exception e) {
			LOGGER.error("Failed to replicate", e);
		}

	}

	public boolean shouldHandle(AfterSaveEvent event, ItemModel model) throws Exception{
		return true;
	}

	public ResponseData handle(AfterSaveEvent event, ItemModel model) throws Exception {
   
		ResponseData resData = null;
		if(model != null) {
			String json = getFinalJson(model);
			resData = publish(json, model.getItemtype());
		}
		return resData;

	}
	
	public ResponseData publish(String json, String itemType) throws IOException{
		final ResponseData resData = getPublisher().publishJson(json, itemType);
		final String resStatus = resData.getStatus();
		if (HCI_PUBLICATION_STATUS_CREATED.equals(resStatus) || HCI_PUBLICATION_STATUS_OK.equals(resStatus)) {
			LOGGER.info("Published Successfully");
		}
		return resData;
	}
	
	public Map<String, Object> convertJsonToMap(String json) throws IOException{
		Map<String, Object> populatedFields = (LinkedHashMap<String, Object>) new ObjectMapper().readValue(json,
				LinkedHashMap.class);
		return populatedFields;
	}
	
	public String convertMapToJson(Map<String, Object> map) throws IOException {
		ObjectMapper mapperObj = new ObjectMapper();
		return mapperObj.writeValueAsString(map);
	}
	
	
	public String getFinalJson(ItemModel model) throws ReflectiveOperationException, IOException  {
		Map<String, Object> populatedFields = new LinkedHashMap<String, Object>();
		return getFinalJson(model, populatedFields);
	}
	
	public String getFinalJson(ItemModel model, String json) throws IOException, ReflectiveOperationException  {
		Map<String, Object> populatedFields = convertJsonToMap(json);
		return getFinalJson(model, populatedFields);
	}
	
	public String getFinalJson(ItemModel model, Map<String, Object> populatedFields) throws ReflectiveOperationException, IOException  {
		String json = null;
		List<Object> fields = getFieldsToPopulate(model);
		if (fields != null) {
			populateFields(model, fields, populatedFields);
		}
		json = convertMapToJson(populatedFields);
		return json;
	}
	
	public List<Object> getFieldsToPopulate(ItemModel model){
		String itemType = model.getItemtype();
		String typeData = getFieldsString(itemType);
		if (typeData != null && !typeData.trim().equals(EMPTY_STRING)) {
			return getAllFields(new StringBuilder(typeData));
		}
	    return null;
	}
	
	public String getFieldsString(String itemType){
		String typeData = getConfigurationService().getConfiguration().getString(
				REPLICATED_TYPE_DATA + DOT + itemType.toLowerCase());
		return typeData;
	}
	   
	public Map<String, Object> populateFields(ItemModel model, List<Object> fields, Map<String, Object> populatedFields) throws ReflectiveOperationException
			 {
		Class<?> classOb = model.getClass();
		
		for (Object item : fields) {
			if (item instanceof String) {
				Object value = getValue(model, item);
				if(value instanceof Collection || value instanceof ItemModel) {
					throw new RuntimeException("Can't convet model to json");
				}else if(value != null){
					populatedFields.put(item.toString(), value.toString());
				}
			} else if (item instanceof Map) {
				String key = ((Map<String, Object>) item).keySet().stream().findFirst().get();
				List<Object> value = (List) ((Map<String, Object>) item).get(key);
				Object relModel = classOb.getMethod(getMethodName(key)).invoke(model);
				if (relModel instanceof ItemModel) {
					populatedFields.put(key,
							populateFields((ItemModel) relModel, value, new LinkedHashMap<String, Object>()));
				} else if (relModel instanceof Collection) {
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					populateFieldsInList(list, relModel, value);
					populatedFields.put(key, list);
				}
			}
		}
		return populatedFields;
	}
	
	private Object getValue(ItemModel model, Object item) throws ReflectiveOperationException{
		Class<?> classOb = model.getClass();
		Object value  = null;
		try {
			value  = classOb.getMethod(getMethodName(item.toString())).invoke(model);
		}catch(NoSuchMethodException e) {
			try {
				value  = classOb.getMethod(getBooleanMethodName(item.toString())).invoke(model);
			}catch(NoSuchMethodException ex){
				LOGGER.warn("Field '" + item.toString() + "' does not exist.");
			}
			
		}
		return value;
		
	}

	private void populateFieldsInList(List<Map<String, Object>> list, Object relModel, List<Object> value) throws ReflectiveOperationException{
		for (ItemModel currModel : (Collection<ItemModel>) relModel) {
			list.add(populateFields((ItemModel) currModel, value, new LinkedHashMap<String, Object>()));
		}
	}

	private String getMethodName(String fieldName) {
		return GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	private String getBooleanMethodName(String fieldName) {
		return IS + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
	}

	private List<Object> getAllFields(StringBuilder typeData) {
		List<Object> list = new ArrayList<Object>();
		while (typeData.indexOf(CLOSE_BRACE) >= 0) {
			int openBraceIndex = typeData.indexOf(OPEN_BRACE);
			int closeBraceIndex = typeData.indexOf(CLOSE_BRACE);
			if (openBraceIndex < closeBraceIndex && openBraceIndex >= 0) {
				if (openBraceIndex != 0) {
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					String arr[] = typeData.substring(0, openBraceIndex).split(COMMA);
					typeData.replace(0, openBraceIndex + 1, "");
					list.addAll(processList(Arrays.asList(arr)));
					list.remove(list.size() - 1);
					map.put(arr[arr.length - 1], getAllFields(typeData));
					list.add(map);
				} else {
					typeData.replace(0, 1, EMPTY_STRING);
					list.addAll(getAllFields(typeData));
				}
			} else {
				list.addAll(processList(Arrays.asList(typeData.substring(0, closeBraceIndex).split(COMMA))));
				typeData.replace(0, closeBraceIndex + 1, EMPTY_STRING);
				return list;
			}
		}
		return list;
	}

	private static List<String> processList(List<String> list) {
		List<String> clist = new ArrayList<String>();
		clist.addAll(list);
		for (int i = 0; i < clist.size(); i++) {
			String ele = clist.get(i);
			String trimmedEle = ele.trim();
			if (trimmedEle.equals(EMPTY_STRING)) {
				clist.remove(i);
			}else {
				clist.set(i, trimmedEle);
			}
		}
		return clist;
	}

	public void afterHandle(AfterSaveEvent event, ItemModel model, ResponseData resData) {

	}

	@Required
	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}
	
	@Required
	public ModelService getModelService() {
		return this.modelService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	/**
	 * @return the hciPublisher
	 */
	public Publisher getPublisher()
	{
		return publisher;
	}

	@Required
	public void setPublisher(final Publisher publisher)
	{
		this.publisher = publisher;
	}

}
