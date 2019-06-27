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
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl;

import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.SapDataHubInboundStockLevelHelper;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.IncreaseEventModel;
import de.hybris.platform.warehousing.model.ShrinkageEventModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class SapOmsDataHubInboundStockLevelHelper implements SapDataHubInboundStockLevelHelper {

  private static final Logger LOG = Logger.getLogger(SapOmsDataHubInboundStockLevelHelper.class);

  private ModelService modelService;
  private InventoryEventService inventoryEventService;
  private CommerceAvailabilityCalculationStrategy commerceStockLevelCalculationStrategy;

  @Override
  public void processStockLevelNotification(String stockLevelQuantity, Item stockLevelItem) {

    Long quantity = readAndValidateQuantity(stockLevelQuantity);

    if (quantity != null) {

      // Update stock level
      updateStockLevel(stockLevelItem, quantity);
    }

  }

  protected void updateStockLevel(Item stockLevelItem, Long quantity) {

    StockLevelModel stockLevelModel = modelService.get(stockLevelItem.getPK());
    updateStockLevel(stockLevelModel, quantity);

  }

  protected void updateStockLevel(StockLevelModel stockLevelModel, Long quantity) {

    String productCode = stockLevelModel.getProperty("productCode").toString();

    // Import the initial stock value from ERP
    if (isInitialStockLevel(stockLevelModel)) {

      stockLevelModel.setAvailable(quantity.intValue());
      modelService.save(stockLevelModel);
      LOG.info(String.format("Initial stock level of quantity [%s] for product [%s] was imported from ERP!", quantity.toString(), productCode));

      // Update inventory based on ERP stock level import
    } else {

      // Calculate the ATP quantity for the given stock level
      Long atpQuantity = commerceStockLevelCalculationStrategy.calculateAvailability(Collections.singleton(stockLevelModel));

      // Calculate the quantity that is not shipped
      Long unShippedQuantity = calculateStockLevelUnShippedQuantity(stockLevelModel);

      // The quantity discrepancy between hybris and ERP for the given stock level
      Long discrepantQuantity = quantity - (atpQuantity + unShippedQuantity);

      // Trigger increase inventory event
      if (discrepantQuantity > 0) {

        IncreaseEventModel increaseEventModel = modelService.create(IncreaseEventModel.class);
        increaseEventModel.setQuantity(discrepantQuantity);
        increaseEventModel.setStockLevel(stockLevelModel);
        inventoryEventService.createIncreaseEvent(increaseEventModel);
        LOG.info(String.format("Increase inventory event by [%s] for product [%s] based on ERP stock level import!", discrepantQuantity, productCode));

        // Trigger shrinkage inventory event
      } else if (discrepantQuantity < 0) {

        ShrinkageEventModel shrinkageEventModel = modelService.create(ShrinkageEventModel.class);
        shrinkageEventModel.setQuantity(Math.abs(discrepantQuantity));
        shrinkageEventModel.setStockLevel(stockLevelModel);
        inventoryEventService.createShrinkageEvent(shrinkageEventModel);
        LOG.info(String.format("Decrease inventory event by [%s] for product [%s] based on ERP stock level import!", Math.abs(discrepantQuantity), productCode));

      } else {

        LOG.info(String.format("The stock level for product [%s] is the same in platform and ERP, there is no need for inventory adjustment!", productCode));

      }

    }

  }

  protected boolean isInitialStockLevel(StockLevelModel stockLevelModel) {
    return stockLevelModel.getAvailable() == 0;
  }

  protected Long calculateStockLevelUnShippedQuantity(StockLevelModel stockLevel) {

    return stockLevel.getInventoryEvents().stream()// Loop through  all inventory events of the related stock level
            .filter(inventoryEvent -> inventoryEvent.getConsignmentEntry() != null &&
                    !inventoryEvent.getConsignmentEntry().getStatus().equals(ConsignmentEntryStatus.SHIPPED))// Filter the inventory event with shipped consignment entry
            .mapToLong(inventoryEvent -> inventoryEvent.getConsignmentEntry().getQuantity() - (inventoryEvent.getConsignmentEntry().getShippedQuantity() == null ? 0L : inventoryEvent.getConsignmentEntry().getShippedQuantity())).sum();
  }

  protected Long readAndValidateQuantity(String quantity) {

    try {
      validateParameterNotNull(quantity, "Stock level must not be null!");
      return Double.valueOf(quantity).longValue();
    } catch (Exception ex) {
      LOG.error(String.format("The imported stock level quantity [%s] is not valid! %s", quantity, ex));
      return null;
    }
  }

  protected CommerceAvailabilityCalculationStrategy getCommerceStockLevelCalculationStrategy() {
    return commerceStockLevelCalculationStrategy;
  }

  @Required
  public void setCommerceStockLevelCalculationStrategy(CommerceAvailabilityCalculationStrategy commerceStockLevelCalculationStrategy) {
    this.commerceStockLevelCalculationStrategy = commerceStockLevelCalculationStrategy;
  }

  protected InventoryEventService getInventoryEventService() {
    return inventoryEventService;
  }

  @Required
  public void setInventoryEventService(InventoryEventService inventoryEventService) {
    this.inventoryEventService = inventoryEventService;
  }

  protected ModelService getModelService() {
    return modelService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}