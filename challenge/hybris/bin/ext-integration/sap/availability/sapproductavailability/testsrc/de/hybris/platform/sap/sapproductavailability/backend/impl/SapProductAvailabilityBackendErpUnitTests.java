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
package de.hybris.platform.sap.sapproductavailability.backend.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailability;
import de.hybris.platform.sap.sapproductavailability.constants.SapproductavailabilityConstants;


@UnitTest
public class SapProductAvailabilityBackendErpUnitTests 
{
	
	@Mock
	private JCoConnection connection;
	
	@Mock
	private JCoManagedConnectionFactory managedConnectionFactory;
	
	@Mock
	private ModuleConfigurationAccess configAccess;
	
	@Mock
	private SapProductAvailabilityCache cachedBackend;
	
	
	
	private SapProductAvailabilityBackendERP availabilityBackend;
	
	@Before
	public void setUp() throws BackendException{
		
		MockitoAnnotations.initMocks(this);
		
		// config attributes
		given(configAccess.getProperty(SapproductavailabilityConstants.SALES_ORG)).willReturn("1000");
		given(configAccess.getProperty(SapproductavailabilityConstants.DIS_CHANNEL)).willReturn("10");
		given(configAccess.getProperty(SapproductavailabilityConstants.DIVISION)).willReturn("10");
				
				
		availabilityBackend = new SapProductAvailabilityBackendERP() {
			
			@Override
			public JCoConnection getDefaultJCoConnection()
			{
				return connection;
			}
			
			
			@Override
			public ModuleConfigurationAccess getConfigAccess() {
				return configAccess;
			}
			
			@Override
			public SapProductAvailabilityCache getCachedBackend() {
				return cachedBackend;
			}
			
			
		};
		
	}


	
	protected void mockMaterialDetail() throws BackendException{
		
		// added for is material in plant
		String bapiMaterialDetail = "BAPI_MATERIAL_GET_DETAIL";
		
		JCoFunction bapiMatGetDetail = Mockito.mock(JCoFunction.class);
		
		given(connection.getFunction(bapiMaterialDetail)).willReturn(bapiMatGetDetail);
		
		JCoParameterList matPlantImportParameterList = Mockito.mock(JCoParameterList.class);
		given(bapiMatGetDetail.getImportParameterList()).willReturn(matPlantImportParameterList);
		
		
		JCoParameterList matPlantExportParameterList = Mockito.mock(JCoParameterList.class);
		given(bapiMatGetDetail.getExportParameterList()).willReturn(matPlantExportParameterList);
		
		
		JCoStructure matPlantStructure = Mockito.mock(JCoStructure.class);
		given(matPlantStructure.getString("TYPE")).willReturn("S");
		
		given(matPlantExportParameterList.getStructure("RETURN")).willReturn(matPlantStructure);
	}
	
	@Test
	public void testReadPlantForCustomerMaterial() throws BusinessObjectException, BackendException
	{
		
		mockReadPlantForCustomerMaterialJcoCalls();
	
		assertEquals(availabilityBackend.readPlantForCustomerMaterial("MATDEMO_05", "JV02"), "1000");

	}


	protected void mockReadPlantForCustomerMaterialJcoCalls() throws BackendException {
			String material = "MATDEMO_05";
			String customerId = "JV02";
			String bapiCustomerInfo = "BAPI_CUSTMATINFO_GETDETAILM";
			JCoFunction functionCustomerInfo = Mockito.mock(JCoFunction.class);
			
			given(cachedBackend.readCachedPlantCustomer(material, customerId, "1000", "10")).willReturn(null);
					 
			given(connection.getFunction(bapiCustomerInfo)).willReturn(functionCustomerInfo);
			
			
			JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);
			given(functionCustomerInfo.getImportParameterList()).willReturn(importParameterList);
			
			
			JCoParameterList tableParameterList = Mockito.mock(JCoParameterList.class);
			given(functionCustomerInfo.getTableParameterList()).willReturn(tableParameterList);
			
			JCoTable matTable = Mockito.mock(JCoTable.class);
			given(tableParameterList.getTable("CUSTOMERMATERIALINFO")).willReturn(matTable);
			
			JCoTable results = Mockito.mock(JCoTable.class);
			given(results.getNumRows()).willReturn(1);
			given(results.getString("PLANT")).willReturn("1000");
			
			given(tableParameterList.getTable("CUSTOMERMATERIALINFODETAIL")).willReturn(results);
			
		 
			mockMaterialDetail();
		
	}
	
	
	@Test
	public void testReadPlant() throws BusinessObjectException, BackendException
	{
		
		mockReadPlant();
	
		String customerId = "JV02";
		final UnitModel unit = new UnitModel();
		unit.setCode("PC");

		final ProductModel productModel = new ProductModel();
		productModel.setUnit(unit);
		productModel.setCode("EPHBR01");
		
		
		assertEquals(availabilityBackend.readPlant(productModel, customerId), "1000");
		
		
	}
	
	protected void mockReadPlant() throws BackendException {
		
		given(cachedBackend.readCachedPlant("EPHBR01", "JV02","1000", "10")).willReturn(null);
		
		given(cachedBackend.readCachedPlantMaterial("EPHBR01", "1000", "10")).willReturn(null);
		
		given(cachedBackend.readCachedPlantMaterial("EPHBR01", "1000", "10")).willReturn(null);
				 
		String bapiMaterialPlant = "BAPI_MVKE_ARRAY_READ";
		
		JCoFunction functionMaterialPlant = Mockito.mock(JCoFunction.class);
		
		given(connection.getFunction(bapiMaterialPlant)).willReturn(functionMaterialPlant);
		
		JCoParameterList tableParameterList = Mockito.mock(JCoParameterList.class);
		given(functionMaterialPlant.getTableParameterList()).willReturn(tableParameterList);
		
		
		JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);
		given(functionMaterialPlant.getImportParameterList()).willReturn(importParameterList);
		
		JCoListMetaData jcoListMetaData = Mockito.mock(JCoListMetaData.class);
		given(jcoListMetaData.hasField("TVTA_SPART")).willReturn(true);
		
		JCoTable matTable = Mockito.mock(JCoTable.class);
		given(tableParameterList.getTable("IPRE10")).willReturn(matTable);
				
		
		JCoTable results = Mockito.mock(JCoTable.class);
		given(results.getNumRows()).willReturn(1);
		given(results.getString("DWERK")).willReturn("1000");
		
		given(tableParameterList.getTable("MVKE_TAB")).willReturn(results);
		
		
		JCoListMetaData recordMetaData = Mockito.mock(JCoListMetaData.class);    
		given(recordMetaData.hasField("TVTA_SPART")).willReturn(true);
		given(importParameterList.getListMetaData()).willReturn(recordMetaData);
		
		// read plant for customer
		String material = "MATDEMO_05";
		String customerId = "JV02";
		String bapiCustomerInfo = "BAPI_CUSTMATINFO_GETDETAILM";
		JCoFunction functionCustomerInfo = Mockito.mock(JCoFunction.class);
		
		given(cachedBackend.readCachedPlantCustomer(material, customerId, "1000", "10")).willReturn(null);
				 
		given(connection.getFunction(bapiCustomerInfo)).willReturn(functionCustomerInfo);
		
		given(functionCustomerInfo.getImportParameterList()).willReturn(importParameterList);
		
		given(functionCustomerInfo.getTableParameterList()).willReturn(tableParameterList);

		given(tableParameterList.getTable("CUSTOMERMATERIALINFO")).willReturn(matTable);

		given(results.getNumRows()).willReturn(1);
		given(results.getString("PLANT")).willReturn("1000");
		
		given(tableParameterList.getTable("CUSTOMERMATERIALINFODETAIL")).willReturn(results);
		
		mockMaterialDetail();
		
	}
	
	@Test
	public void testReadProductAvailability() throws BackendException
	{
		
		mockReadProductAvailability();
	
		String customerId = "JV02";
		final UnitModel unit = new UnitModel();
		unit.setCode("PC");
		unit.setSapCode("ST");

		final ProductModel productModel = new ProductModel();
		productModel.setUnit(unit);
		productModel.setCode("EPHBR01");
		
		
		String plant = "1000";
		
		final Long requestedQuantity = Long.valueOf(50);
		
		final SapProductAvailability availability = availabilityBackend.readProductAvailability(productModel, customerId,
				plant, requestedQuantity);

		assertNotNull(availability);

		assertEquals(100, availability.getCurrentStockLevel().longValue());
				

	}
	
	
	protected void mockReadProductAvailability() throws BackendException {
				
		String customerId = "JV02";
		UnitModel unit = new UnitModel();
		unit.setCode("PC");
		unit.setSapCode("PC");

		ProductModel productModel = new ProductModel();
		productModel.setUnit(unit);
		productModel.setCode("EPHBR01");
		
		String plant = "1000";
		
		final Long requestedQuantity = Long.valueOf(100);
		
		given(cachedBackend.readCachedProductAvailability(productModel, customerId, plant,
				requestedQuantity)).willReturn(null);
							 
		String bapiMaterialAvailability = "BAPI_MATERIAL_AVAILABILITY";
				
		JCoFunction functionMaterialAvailability = Mockito.mock(JCoFunction.class);
				
		given(connection.getFunction(bapiMaterialAvailability)).willReturn(functionMaterialAvailability);
		
		JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);
		given(functionMaterialAvailability.getImportParameterList()).willReturn(importParameterList);
				
		JCoParameterList tableParameterList = Mockito.mock(JCoParameterList.class);
		given(functionMaterialAvailability.getTableParameterList()).willReturn(tableParameterList);
		
		JCoTable inputTable = Mockito.mock(JCoTable.class);
		given(tableParameterList.getTable("WMDVSX")).willReturn(inputTable);
		
		JCoParameterList exportParameterList = Mockito.mock(JCoParameterList.class);
		given(exportParameterList.getString("AV_QTY_PLT")).willReturn("100.000");
		given(functionMaterialAvailability.getExportParameterList()).willReturn(exportParameterList);
		
		JCoTable results = Mockito.mock(JCoTable.class);
		
		given(results.isEmpty()).willReturn(false);
		
		given(results.getNumRows()).willReturn(1);
		
		given(results.getString("COM_DATE")).willReturn("2015-10-12");
		
		given(results.getString("COM_QTY")).willReturn("2000.000");
		
		given(tableParameterList.getTable("WMDVEX")).willReturn(results);
		
		
	}
	
	
	
}
