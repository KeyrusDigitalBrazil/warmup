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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;


import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PartnerBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.ProcessTypeConverter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;




/**
 * ERP implementation of LrdActionsStrategy and ConstantsR3Lrd.
 *
 * @see LrdActionsStrategy
 * @see ConstantsR3Lrd
 * @version 1.0
 */
public class LrdActionsStrategyERP extends BaseStrategyERP implements LrdActionsStrategy, ConstantsR3Lrd
{

	private static final String ACTION_PARAM_NEW_FREIGHT_DETERMINATION = "H";

	private static final String ACTION_ID_PRICING = "PRICING";
	/**
	 * In case setting to 'X': Deactivating of switch for check of open documents (contracts, quotations): no error message
	 * should be returned when a open contract or quotation exists for the entered product of the order
	 */
	protected static String FIELD_NO_MESSAGES_DOC = "NO_MESSAGES_DOC";
	/**
	 * LO-API should not trigger ERP conversion exits
	 */
	protected static final String FIELD_NO_CONVERSION = "NO_CONVERSION";

	/**
	 * constant naming the key referenced in factory-config.xml where the strategy class is specified
	 */
	public static final String STRATEGY_FACTORY_ERP = "STStrategyFactoryERP";

	private long rfctime = 0;

	private static final Log4JWrapper SAP_LOGGER = Log4JWrapper.getInstance(LrdActionsStrategyERP.class.getName());

	// Partner
	public static final String PARTY = "PARTY";

	// ITEM
	public static final String ITEM = "ITEM";

	@SuppressWarnings("squid:S1699")
	public LrdActionsStrategyERP()
	{
		super();
		// Fill the active Fields list only once
		fillActiveFields();
	}

	/**
	 * Filling active fields
	 */
	private void fillActiveFields()
	{
		setActiveFieldsListCreateChange(activeFieldsListCreateChange);
	}

	/**
	 * Field list which fields will be checked in create or change mode
	 */
	protected final List<SetActiveFieldsListEntry> activeFieldsListCreateChange = new ArrayList<SetActiveFieldsListEntry>();

	static final String EXC_NO_ACTION_WHEN_ERROR = "EXC_NO_ACTION_WHEN_ERROR";

	static final String EXC_NO_ACTION_WHEN_DISPLAY = "EXC_NO_ACTION_WHEN_DISPLAY";


	/**
	 * The condition type which should be used to determine the freight value
	 */
	protected String headerCondTypeFreight = "";

	/**
	 * The subtotal for the item freight value
	 */
	protected String subTotalItemFreight = "";

	/**
	 * Allows access to configuration settings
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess;

	/**
	 * @param moduleConfigurationAccess
	 *           Allows access to configuration settings
	 */
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * Standard constructor. <br>
	 */


	@Override
	public ReturnValue executeLrdDoActionsDelete(final TransactionConfiguration shop, final SalesDocument salesDoc,
			final JCoConnection cn, final String objectName, final TechKey[] itemsToDelete) throws BackendException
	{

		final String METHOD_NAME = "executeLrdDoActionsDelete()";
		SAP_LOGGER.entering(METHOD_NAME);

		ReturnValue retVal = new ReturnValue(ConstantsR3Lrd.BAPI_RETURN_ERROR);

		try
		{
			final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS);

			// getting import parameter
			final JCoParameterList importParams = function.getImportParameterList();

			// getting export parameters
			final JCoParameterList exportParams = function.getExportParameterList();
			final JCoTable itAction = importParams.getTable("IT_ACTION");

			// check, if items should be deleted:
			checkItemDeleted(salesDoc, cn, objectName, itemsToDelete, function, importParams, itAction);

			// error handling
			final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
			final JCoStructure esError = exportParams.getStructure("ES_ERROR");
			dispatchMessages(salesDoc, etMessages, esError);

			if (!(esError != null && "X".equals(esError.getString("ERRKZ"))))
			{
				retVal = new ReturnValue(ConstantsR3Lrd.BAPI_RETURN_INFO);
			}

		}
		catch (final BackendException ex)
		{
			handleException(salesDoc, ex);
		}
		finally
		{
			SAP_LOGGER.exiting();
		}

		SAP_LOGGER.exiting();
		return retVal;
	}

	/**
	 * @param salesDoc
	 * @param ex
	 * @throws BackendException
	 */
	private void handleException(final SalesDocument salesDoc, final BackendException ex) throws BackendException
	{
		final String abapExc = ex.getCause().getMessage();
		String resourceKey = "";
		if (abapExc.equals(EXC_NO_ACTION_WHEN_ERROR))
		{
			resourceKey = "sapsalestransactions.bo.sales.erp.actions.error";
		}
		else if (abapExc.equals(EXC_NO_ACTION_WHEN_DISPLAY))
		{
			resourceKey = "sapsalestransactions.bo.sales.erp.actions.display";
		}
		else
		{
			invalidateSalesDocument(salesDoc);

			throw ex;
		}
		final Message message = new Message(Message.ERROR, resourceKey);
		salesDoc.addMessage(message);
	}

	/**
	 * @param salesDoc
	 * @param cn
	 * @param objectName
	 * @param itemsToDelete
	 * @param function
	 * @param importParams
	 * @param itAction
	 * @throws BackendException
	 */
	private void checkItemDeleted(final SalesDocument salesDoc, final JCoConnection cn, final String objectName,
			final TechKey[] itemsToDelete, final JCoFunction function, final JCoParameterList importParams, final JCoTable itAction)
			throws BackendException
	{
		if (objectName.equals(LrdActionsStrategy.ITEMS))
		{
			deleteItems(itAction, itemsToDelete);
		}

		// if there is something to delete
		if (itAction.getNumRows() > 0)
		{
			final ERPLO_APICustomerExits custExit = getCustExit();
			if (custExit != null)
			{
				custExit.customerExitBeforeLoad(salesDoc, function, cn, SAP_LOGGER);
			}

			cn.execute(function);

			if (custExit != null)
			{
				custExit.customerExitAfterLoad(salesDoc, function, cn, SAP_LOGGER);
			}
		}

		if (SAP_LOGGER.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS, importParams, null);
		}
	}

	/**
	 * Registers the items for deletion. This is done by generating entries in the <code>itAction</code> table.
	 *
	 * @param itAction
	 *           the JCoTable, that is filled with the items to be deleted.
	 * @param itemsToDelete
	 *           the <code>TechKey</code>s of the items to be deleted.
	 */
	protected static void deleteItems(final JCoTable itAction, final TechKey[] itemsToDelete)
	{

		if (itemsToDelete == null)
		{
			return;
		}

		for (int i = 0; i < itemsToDelete.length; i++)
		{

			itAction.appendRow();
			itAction.setValue("HANDLE", itemsToDelete[i].getIdAsString());
			itAction.setValue("ACTION", "DELETE");

		} // for

	} // deleteItems

	@Override
	public BackendCallResult executeLrdSave(final SalesDocument posd, final boolean commit, final JCoConnection cn)
			throws BackendException
	{
		try
		{


			final String METHOD_NAME = "executeLrdSave()";
			SAP_LOGGER.entering(METHOD_NAME);

			BackendCallResult retVal = new BackendCallResult();

			final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SAVE);

			// getting import parameter
			final JCoParameterList importParams = function.getImportParameterList();

			// getting export parameters
			final JCoParameterList exportParams = function.getExportParameterList();

			if (!commit)
			{
				JCoHelper.setValue(importParams, "X", "IF_NO_COMMIT");
			}
			else
			{
				// Makes only sence after a commit
				JCoHelper.setValue(importParams, "X", "IF_SYNCHRON");
			}
			final ERPLO_APICustomerExits custExit = getCustExit();
			isCustExit(commit, cn, function, exportParams, custExit);

			if ((!(exportParams.getString("EF_SAVED").isEmpty())) && (!exportParams.getString("EV_VBELN_SAVED").trim().isEmpty()))
			{
				// Only set Techkey in case it is really new - when creating a
				// document,
				// otherwise several issues will occur:
				// - document is read multiple time afterwards
				// -exceptions can occur, see int.msg. 4340020 /2009
				if (posd.getHeader().getSalesDocNumber() == null || "".equals(posd.getHeader().getSalesDocNumber()))
				{
					posd.setTechKey(JCoHelper.getTechKey(exportParams, "EV_VBELN_SAVED"));
					posd.getHeader().setTechKey(JCoHelper.getTechKey(exportParams, "EV_VBELN_SAVED"));
					posd.getHeader()
							.setSalesDocNumber(ConversionTools.cutOffZeros(JCoHelper.getString(exportParams, "EV_VBELN_SAVED")));
				}
				SAP_LOGGER.debug("Transaction with ID" + exportParams.getString("EV_VBELN_SAVED") + "was saved successfully");

				// Now we need to tell the BO layer and UI that the order
				// is not in editing anymore!
				posd.getHeader().setChangeable(false);

			}
			// No data was changed
			else if (hasMessage("W", "V1", "041", ConstantsR3Lrd.MESSAGE_IGNORE_VARS, "", "", "",
					exportParams.getTable("ET_MESSAGES"), null))
			{
				if (SAP_LOGGER.isDebugEnabled())
				{
					SAP_LOGGER.debug("No data changed");
				}
				final TechKey techKey = new TechKey(posd.getHeader().getSalesDocNumber());
				posd.setTechKey(techKey);
				posd.getHeader().setTechKey(techKey);
			}
			else
			{
				retVal = new BackendCallResult(BackendCallResult.Result.failure);
				SAP_LOGGER.debug("Transaction could not be saved.");
			}

			if (SAP_LOGGER.isDebugEnabled())
			{
				logCall(ConstantsR3Lrd.FM_LO_API_SAVE, importParams, null);
			}

			dispatchMessages(posd, exportParams.getTable("ET_MESSAGES"), exportParams.getStructure("ES_ERROR"));

			SAP_LOGGER.exiting();
			return retVal;
		}
		catch (final BackendException e)
		{
			invalidateSalesDocument(posd);
			throw e;
		}
	}

	/**
	 * @param commit
	 * @param cn
	 * @param function
	 * @param exportParams
	 * @param custExit
	 * @throws BackendException
	 */
	private void isCustExit(final boolean commit, final JCoConnection cn, final JCoFunction function,
			final JCoParameterList exportParams, final ERPLO_APICustomerExits custExit) throws BackendException
	{
		if (custExit != null)
		{
			custExit.customerExitBeforeSave(commit, function, cn, SAP_LOGGER);
		}
		cn.execute(function);
		if (custExit != null)
		{
			custExit.customerExitAfterSave(commit, function, cn, SAP_LOGGER);
		}
		if (SAP_LOGGER.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder(60);
			debugOutput.append("Result of ERP_LORD_SAVE: ");
			debugOutput.append("\n EF_SAVED      : ").append(exportParams.getString("EF_SAVED"));
			debugOutput.append("\n EV_VBELN_SAVED: ").append(exportParams.getString("EV_VBELN_SAVED"));
			SAP_LOGGER.debug(debugOutput);
		}
	}

	/**
	 * Checks if following attributes have been provided: process type, soldTo, sales organisation, distribution channel,
	 * division (Mandatory fields for LOAD-call).
	 *
	 * @param posd
	 * @throws BackendExceptionECOERP
	 */
	protected void checkAttributesLrdLoad(final SalesDocument posd) throws BackendExceptionECOERP
	{

		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE),
				"Transaction Type");
		checkAttributeEmpty(posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "SoldTo");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "SalesOrg");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
				"DistrChan");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "Division");

	}

	@Override
	public BackendCallResult executeLrdLoad(final SalesDocument posd, final BackendState erpDocument, final JCoConnection cn,
			final LoadOperation loadState) throws BackendException
	{

		final String METHOD_NAME = "executeLrdLoad()";

		SAP_LOGGER.entering(METHOD_NAME);

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_LOAD);

		// getting import parameter
		final JCoParameterList importParams = function.getImportParameterList();

		// header structure
		final JCoStructure headComv = importParams.getStructure("IS_HEAD_COMV");
		final JCoStructure headComx = importParams.getStructure("IS_HEAD_COMX");

		// setting the import table basket_item


		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();

		// Structure for switches
		final JCoStructure logicSwitch = importParams.getStructure("IS_LOGIC_SWITCH");
		fillControlAttributes(logicSwitch);

		//Determine process type
		final String convProcessType = determineProcessType(posd, cn);



		// Scenario
		importParams.setValue("IV_SCENARIO_ID", scenario_LO_API_WEC);

		if (loadState.getLoadOperation().equals(LoadOperation.display))
		{

			JCoHelper.setValue(importParams, LoadOperation.display, "IV_TRTYP");
			JCoHelper.setValue(importParams, posd.getHeader().getTechKey().toString(), "IV_VBELN");
		}
		isCreateLoadOperation(posd, erpDocument, loadState, importParams, headComv, headComx, convProcessType);
		isEditLoadOperation(posd, loadState, importParams);
		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null)
		{
			custExit.customerExitBeforeLoad(posd, function, cn, SAP_LOGGER);
		}

		executeRfc(cn, function);

		if (custExit != null)
		{
			custExit.customerExitAfterLoad(posd, function, cn, SAP_LOGGER);
		}

		final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
		final JCoStructure esError = exportParams.getStructure("ES_ERROR");

		if (loadState.getLoadOperation().equals(LoadOperation.create))
		{
			final JCoStructure headerV = exportParams.getStructure("ES_HEAD_COMV");
			posd.setTechKey(JCoHelper.getTechKey(headerV, "HANDLE"));
		}

		final JCoStructure headerComV = exportParams.getStructure("ES_HEAD_COMV");
		posd.getHeader().setHandle(JCoHelper.getString(headerComV, "HANDLE"));

		logParameters(importParams, exportParams);

		// error during load cannot be corrected
		// this we need to raise as an exception
		final String messageType = esError.getString("MSGTY");

		if ("E".equals(messageType) || "A".equals(messageType))
		{

			if (loadState.getLoadOperation().equals(LoadOperation.create))
			{
				if (!isRecoverableHeaderError(esError))
				{
					logErrorMessage(esError);

					posd.setInitialized(false);

					return new BackendCallResult(BackendCallResult.Result.failure);
				}
			}
			else if (loadState.getLoadOperation().equals(LoadOperation.edit))
			{
				loadState.setLoadOperation(LoadOperation.display);
			}
		}

		dispatchMessages(posd, etMessages, esError);

		return isPosdInitialized(posd, METHOD_NAME, function, exportParams, esError);
	}

	/**
	 * Method to log import and export parameters
	 *
	 * @param importParams
	 * @param exportParams
	 */
	private void logParameters(final JCoParameterList importParams, final JCoParameterList exportParams)
	{
		if (SAP_LOGGER.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, importParams, null);
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, null, exportParams.getStructure("ES_HEAD_COMV"));
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, null, exportParams.getStructure("ES_HEAD_COMR"));
		}
	}

	/**
	 * @param posd
	 * @param METHOD_NAME
	 * @param function
	 * @param exportParams
	 * @param esError
	 * @return
	 */
	private BackendCallResult isPosdInitialized(final SalesDocument posd, final String METHOD_NAME, final JCoFunction function,
			final JCoParameterList exportParams, final JCoStructure esError)
	{
		if (posd.isInitialized())
		{
			readAlternativePartners(posd, function.getTableParameterList().getTable("ET_ALTERNATIVE_PARTNERS"));
		}

		if ("".equals(JCoHelper.getString(exportParams.getStructure("ES_HEAD_COMV"), "HANDLE")))
		{
			SAP_LOGGER.debug("No handle");
		}

		BackendCallResult result = null;
		if (!esError.getString("ERRKZ").isEmpty())
		{
			result = new BackendCallResult(BackendCallResult.Result.failure);
			SAP_LOGGER.debug("Error in " + METHOD_NAME + ": " + esError);
		}
		else
		{
			result = new BackendCallResult();
		}

		SAP_LOGGER.exiting();
		return result;
	}

	/**
	 * @param posd
	 * @param loadState
	 * @param importParams
	 */
	private void isEditLoadOperation(final SalesDocument posd, final LoadOperation loadState, final JCoParameterList importParams)
	{
		if (loadState.getLoadOperation().equals(LoadOperation.edit))
		{

			JCoHelper.setValue(importParams, LoadOperation.edit, "IV_TRTYP");
			JCoHelper.setValue(importParams, posd.getHeader().getTechKey().toString(), "IV_VBELN");
			JCoHelper.setValue(importParams, posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "IV_KUNAG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "IV_VKORG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
					"IV_VTWEG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "IV_SPART");

		}
	}

	/**
	 * @param posd
	 * @param erpDocument
	 * @param loadState
	 * @param importParams
	 * @param headComv
	 * @param headComx
	 * @param convProcessType
	 * @throws BackendExceptionECOERP
	 */
	private void isCreateLoadOperation(final SalesDocument posd, final BackendState erpDocument, final LoadOperation loadState,
			final JCoParameterList importParams, final JCoStructure headComv, final JCoStructure headComx,
			final String convProcessType) throws BackendExceptionECOERP
	{
		if (loadState.getLoadOperation().equals(LoadOperation.create))
		{

			// we need to know that first update might need to do specific
			// shipto handling
			erpDocument.setDocumentInitial(true);

			// First check on essential attributes
			checkAttributesLrdLoad(posd);

			JCoHelper.setValue(importParams, LoadOperation.create, "IV_TRTYP");
			JCoHelper.setValue(importParams, convProcessType, "IV_AUART");
			JCoHelper.setValue(importParams, posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "IV_KUNAG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "IV_VKORG");
			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
					"IV_VTWEG");
			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "IV_SPART");


			headComv.setValue("BSTKD", posd.getHeader().getPurchaseOrderExt());
			headComx.setValue("BSTKD", "X");

			final Date reqDelvDate = posd.getHeader().getReqDeliveryDate();
			if (reqDelvDate != null)
			{
				// String reqDelDate =

				headComv.setValue("VDATU", reqDelvDate);
				headComx.setValue("VDATU", "X");
			}

		}
	}

	/**
	 * @param posd
	 * @param cn
	 * @return
	 * @throws BackendException
	 */
	private String determineProcessType(final SalesDocument posd, final JCoConnection cn) throws BackendException
	{
		String processType = posd.getHeader().getProcessType();
		if (processType == null || processType.length() == 0)
		{
			processType = (String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);
		}
		final ProcessTypeConverter ptc = new ProcessTypeConverter();
		final String convProcessType = ptc.convertProcessTypeToLanguageDependent(processType, cn);
		return convProcessType;
	}



	private void readAlternativePartners(final SalesDocument salesDocument, final JCoTable alternativePartnerTable)
	{

		final List<ShipTo> shipToList = salesDocument.getAlternativeShipTos();
		final List<BillTo> billToList = salesDocument.getAlternativeBillTos();
		shipToList.clear();
		billToList.clear();

		final int numRows = alternativePartnerTable.getNumRows();

		for (int i = 0; i < numRows; i++)
		{
			alternativePartnerTable.setRow(i);

			final Address address = (Address) genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);

			address.setCompanyName(alternativePartnerTable.getString("NAME"));
			address.setName1(alternativePartnerTable.getString("NAME"));
			address.setName2(alternativePartnerTable.getString("NAME_2"));
			address.setCity(alternativePartnerTable.getString("CITY"));
			address.setStreet(alternativePartnerTable.getString("STREET"));
			address.setPostlCod1(alternativePartnerTable.getString("POSTL_COD1"));
			address.setHouseNo(alternativePartnerTable.getString("HOUSE_NO"));
			address.setCountry(alternativePartnerTable.getString("COUNTRY"));
			address.setRegion(alternativePartnerTable.getString("REGION"));
			address.setAddressStringC(alternativePartnerTable.getString("ADDRESS_SHORT_FORM_S"));
			address.setAddressPartner(alternativePartnerTable.getString("KUNNR"));
			address.setTel1Numbr(alternativePartnerTable.getString("TEL1_NUMBR"));
			address.setTel1Ext(alternativePartnerTable.getString("TEL1_EXT"));
			address.setFaxNumber(alternativePartnerTable.getString("FAX_NUMBER"));
			address.setFaxExtens(alternativePartnerTable.getString("FAX_EXTENS"));
			address.setAddrnum(alternativePartnerTable.getString("KUNNR"));
			address.clearX();

			if ("WE".equals(alternativePartnerTable.getString("PARVW")))
			{
				final PartnerBase partner = salesDocument.createShipTo();
				partner.setId(alternativePartnerTable.getString("KUNNR"));
				partner.setShortAddress(alternativePartnerTable.getString("ADDRESS_SHORT_FORM_S"));
				partner.setAddress(address);

				shipToList.add((ShipTo) partner);
			}
			if ("RE".equals(alternativePartnerTable.getString("PARVW")))
			{
				final PartnerBase partner = salesDocument.createBillTo();
				partner.setId(alternativePartnerTable.getString("KUNNR"));
				partner.setShortAddress(alternativePartnerTable.getString("ADDRESS_SHORT_FORM_S"));
				partner.setAddress(address);

				billToList.add((BillTo) partner);
			}

		}

	}

	@Override
	public ReturnValue executeSetActiveFields(final JCoConnection cn) throws BackendException
	{

		final String METHOD_NAME = "executeSetActiveFields()";
		SAP_LOGGER.entering(METHOD_NAME);

		ReturnValue retVal = null;

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SET_ACTIVE_FIELDS);

		// getting import parameters
		final JCoParameterList importParams = function.getImportParameterList();
		final JCoTable itActiveFields = importParams.getTable("IT_ACTIVE_FIELD");

		final Iterator<SetActiveFieldsListEntry> aflIT = activeFieldsListCreateChange.iterator();
		while (aflIT.hasNext())
		{
			final SetActiveFieldsListEntry aflEntry = aflIT.next();

			// New row
			itActiveFields.appendRow();
			// Set values
			itActiveFields.setValue("OBJECT", aflEntry.getObjectName());
			itActiveFields.setValue("FIELD", aflEntry.getFieldName());
		}

		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null)
		{
			custExit.customerExitBeforeSetActiveFields(function, cn, SAP_LOGGER);
		}
		cn.execute(function);
		if (custExit != null)
		{
			custExit.customerExitAfterSetActiveFields(function, cn, SAP_LOGGER);
		}

		retVal = new ReturnValue("000");

		SAP_LOGGER.exiting();
		return retVal;
	}

	/**
	 * Fill active fields list
	 *
	 * @param activeFieldsListCreateChange
	 *           List of active fields which we setup with this call
	 */
	protected void setActiveFieldsListCreateChange(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		// HEAD
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "BSTKD"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VSBED"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VDATU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "INCO1"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "INCO2"));

		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VKORG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VTWEG"));

		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "KUNAG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "BSARK"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "LIFSK"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "WAERK"));

		// in case of no guestUserMode those fields are excluded at
		// executeSetActiveFields
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_STREET"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_HNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_PCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_CITY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_COUNTRY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_LANGU_EXT"));

		final ERPLO_APICustomerExits custExit = getCustExit();

		// Header Extension Data
		if (custExit != null)
		{

			final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

			final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.HeadComV);

			checkHeadLrdFieldextension(activeFieldsListCreateChange, lrdFieldExtension);

		}


		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "MABNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "POSNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "KWMENG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "ABGRU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "EDATU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "PSTYV"));

		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM",

		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(ITEM, "VRKME"));


		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "KUNNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "PARVW"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "PCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "CITY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "CITY2"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "STREET"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "HNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "COUNTRY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "REGION"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "NAME"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "NAME2"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "TELNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "TELEXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "FAXNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "FAXEXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "MOBNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "EMAIL"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "TAXJURCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "TITLE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "LANGU_EXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "PBOX"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(PARTY, "PBOX_PCODE"));

		// Item Extension Data
		if (custExit != null)
		{

			final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

			final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.ItemComV);

			checkItemLrdFieldextension(activeFieldsListCreateChange, lrdFieldExtension);

		}

		// Text
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "TEXT_STRING"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "ID"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "SPRAS_ISO"));

	}

	/**
	 * @param activeFieldsListCreateChange
	 * @param lrdFieldExtension
	 */
	private void checkItemLrdFieldextension(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange,
			final LrdFieldExtension lrdFieldExtension)
	{
		if (lrdFieldExtension != null)
		{
			final List<String> fields = lrdFieldExtension.getFieldnames();

			if (fields != null)
			{
				for (final String field : fields)
				{
					activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(LrdFieldExtension.objectItem, field));
				}
			}
		}
	}

	/**
	 * @param activeFieldsListCreateChange
	 * @param lrdFieldExtension
	 */
	private void checkHeadLrdFieldextension(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange,
			final LrdFieldExtension lrdFieldExtension)
	{
		if (lrdFieldExtension != null)
		{

			final List<String> fields = lrdFieldExtension.getFieldnames();

			if (fields != null)
			{
				for (final String field : fields)
				{
					activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(LrdFieldExtension.objectHead, field));
				}
			}
		}
	}

	/**
	 * Wrapper for the remote function call. This can be used for performance measurement instrumentation, additional
	 * logging a.o. as well as for unit tests to get independent from the ERP backend
	 *
	 * @param theConnection
	 *           JCO connection
	 * @param theFunction
	 *           JCO Function
	 * @throws BackendException
	 */
	protected void executeRfc(final JCoConnection theConnection, final JCoFunction theFunction) throws BackendException
	{

		SAP_LOGGER.entering("executeRfc");
		try
		{

			final long start = System.currentTimeMillis();
			theConnection.execute(theFunction);
			final long end = System.currentTimeMillis();

			final long millis = end - start;
			rfctime = rfctime + millis;
		}
		finally
		{
			SAP_LOGGER.exiting();
		}
	}

	@Override
	public void executeLrdDoActionsDocumentPricing(final SalesDocument salesDocument, final JCoConnection cn,
			final TransactionConfiguration transConf) throws BackendException
	{
		executeLrdDoActionsDocumentPricing(salesDocument, ACTION_PARAM_NEW_FREIGHT_DETERMINATION, cn, transConf);
	}

	@Override
	public void executeLrdDoActionsDocumentPricing(final SalesDocument salesDocument, final String pricingType,
			final JCoConnection cn, final TransactionConfiguration transConf) throws BackendException
	{

		final String METHOD_NAME = "executeLrdDoActionsDocumentPricing()";
		SAP_LOGGER.entering(METHOD_NAME);

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS);

		// getting import parameter
		final JCoParameterList importParams = function.getImportParameterList();

		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();
		final JCoTable itAction = importParams.getTable("IT_ACTION");
		itAction.appendRow();
		itAction.setValue("HANDLE", salesDocument.getHeader().getHandle());
		itAction.setValue("ACTION", ACTION_ID_PRICING);
		itAction.setValue("PARAM", pricingType);
		cn.execute(function);

		if (SAP_LOGGER.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS, importParams, null);
		}

		// error handling
		final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
		final JCoStructure esError = exportParams.getStructure("ES_ERROR");
		dispatchMessages(salesDocument, etMessages, esError);

		SAP_LOGGER.exiting();
	}

	/**
	 * @param isLogicSwitch
	 */
	protected void fillControlAttributes(final JCoStructure isLogicSwitch)
	{
		// deactivating of switch for check of open documents
		// (contracts, quotations): no error message
		// should be returned when a open contract or quotation exists
		// for the entered product of the order
		isLogicSwitch.setValue(FIELD_NO_MESSAGES_DOC, "X");
		isLogicSwitch.setValue(FIELD_NO_CONVERSION, "X");

	}

}
