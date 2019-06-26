/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.marketplaceservices.strategies.VendorOrderExportStrategy;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.CSVConstants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of ExportVendorOrderStrategy
 */
public class DefaultVendorOrderExportStrategy implements VendorOrderExportStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultVendorOrderExportStrategy.class);
	private String exportDataBaseDirectory;
	private VendorDao vendorDao;
	private static final String LINE_SEPERATOR = "\n";
	private static final String DELIMITER = ",";
	private static final String MINUS = "-";
	protected static final String FIELD_NAMES = "ConsignmentCode,OrderCode,Date,VendorSku,Quantity,TotalPrice,User,DeliveryMode,DeliveryAddress,PaymentMode,PaymentStatus,Status\n";
	
	@Override
	public void exportOrdersForVendor(final String vendorCode)
	{
		final String fileName = new SimpleDateFormat("yyyyMMddHHmm'.csv'").format(new Date());
		final String filePath = getVendorBaseDirectory(vendorCode) + File.separator + fileName;

		final List<ConsignmentEntryModel> consignmententries = getVendorOrders(vendorCode);

		if (CollectionUtils.isNotEmpty(consignmententries))
		{
			try (PrintWriter csvWriter = new PrintWriter(filePath, CSVConstants.DEFAULT_ENCODING))
			{
				csvWriter.print(FIELD_NAMES);
				for (final ConsignmentEntryModel consignmententry : consignmententries)
				{
					csvWriter.print(getVendorOrderContent(consignmententry));
				}

			}
			catch (final IOException e)//NOSONAR
			{
				final String pattern = "IOException occurs while exporting vendor orders as csv file for {0}";
				final String message = MessageFormat.format(pattern, vendorCode);
				LOG.error(message);
			}
		}
	}

	@Override
	public boolean readyToExportOrdersForVendor(final String vendorCode)
	{
		final String path = getVendorBaseDirectory(vendorCode);
		final File file = new File(path);
		return file.exists() && file.isDirectory();
	}

	protected String getExportDataBaseDirectory()
	{
		return exportDataBaseDirectory;
	}

	@Required
	public void setExportDataBaseDirectory(final String exportDataBaseDirectory)
	{
		this.exportDataBaseDirectory = exportDataBaseDirectory;
	}

	protected List<ConsignmentEntryModel> getVendorOrders(final String vendorCode)
	{
		return getVendorDao().findPendingConsignmentEntryForVendor(vendorCode);
	}

	protected String getVendorOrderContent(final ConsignmentEntryModel consignmententry)
	{
		final AbstractOrderEntryModel orderEntry = consignmententry.getOrderEntry();
		final AbstractOrderModel order = orderEntry.getOrder();
		final ConsignmentModel consignment = consignmententry.getConsignment();
		final AddressModel deliveryAddress = order.getDeliveryAddress();

		final StringBuilder address = new StringBuilder();
		address.append(deliveryAddress.getStreetname()).append(MINUS).append(deliveryAddress.getStreetnumber()).append(MINUS)
			   .append(deliveryAddress.getTown()).append(MINUS).append(deliveryAddress.getPostalcode()).append(MINUS)
			   .append(deliveryAddress.getCountry().getName(Locale.ENGLISH));
	   
		String paymentMode = StringUtils.EMPTY, paymentStatus = StringUtils.EMPTY, vendorSku = StringUtils.EMPTY;
		if (order.getPaymentMode() != null) {
			paymentMode = order.getPaymentMode().getName();
		}
		if (order.getPaymentStatus() != null){
			paymentStatus = order.getPaymentStatus().getCode();
		}
		if(orderEntry.getProduct().getVendorSku() != null){
			vendorSku = orderEntry.getProduct().getVendorSku();
		}

		final StringBuilder csvContent = new StringBuilder();
		csvContent.append(StringEscapeUtils.escapeCsv(consignment.getCode()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(order.getCode()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(order.getDate().toString()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(vendorSku))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(orderEntry.getQuantity().toString())).append(StringEscapeUtils.escapeCsv(orderEntry.getUnit().getUnitType()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(order.getCurrency().getSymbol())).append(StringEscapeUtils.escapeCsv(orderEntry.getTotalPrice().toString()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(order.getUser().getName()))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(consignment.getDeliveryMode().getName(Locale.ENGLISH)))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(address.toString())).append(DELIMITER).append(StringEscapeUtils.escapeCsv(paymentMode))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(paymentStatus))
				  .append(DELIMITER).append(StringEscapeUtils.escapeCsv(consignment.getStatusDisplay())).append(LINE_SEPERATOR);

		return csvContent.toString();
	}

	/**
	 * get base directory for vendor
	 *
	 * @param vendorCode
	 *           the vendor's code
	 * @return the directory
	 */
	protected String getVendorBaseDirectory(final String vendorCode)
	{
		if (getExportDataBaseDirectory().endsWith(File.separator))
		{
			return getExportDataBaseDirectory() + vendorCode;
		}
		else
		{
			return getExportDataBaseDirectory() + File.separator + vendorCode;
		}
	}

	protected VendorDao getVendorDao()
	{
		return vendorDao;
	}

	@Required
	public void setVendorDao(final VendorDao vendorDao)
	{
		this.vendorDao = vendorDao;
	}

}
