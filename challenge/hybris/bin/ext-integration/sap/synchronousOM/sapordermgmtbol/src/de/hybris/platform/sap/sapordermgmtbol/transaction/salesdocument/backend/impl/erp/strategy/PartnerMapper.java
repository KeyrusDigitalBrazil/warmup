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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PartnerBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;

import com.sap.conn.jco.JCoTable;


/**
 * Class is responsible to map partner information between LO-API and the BOL layer
 */
public class PartnerMapper extends BaseMapper
{

	/**
	 * ID of LO-API segment which deals with partners
	 */
	public static final String OBJECT_ID_PARTY = "PARTY";
	/**
	 * Logging instance
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(PartnerMapper.class.getName());

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory = null;

	/**
	 * Injected generic factory.
	 *
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	@Override
	public void init()
	{
		/* nothing to initialize */
	}

	/**
	 * Sets the partner data for the sales document, with the input provided from LO-API
	 *
	 * @param ttHeadPartyComV
	 *           JCO table with partner data (ABAP type TDT_RFC_PARTY_COMV)
	 * @param ttHeadPartyComR
	 *           JCO table with partner read-only data (ABAP table type of TDS_RFC_WEC_PARTY_COMR)
	 * @param salesDoc
	 *           BOL sales document
	 * @param baseR3Lrd
	 * @param header
	 *           BOL header
	 */
	public void read(final JCoTable ttHeadPartyComV, //
			final JCoTable ttHeadPartyComR, //
			final SalesDocument salesDoc, //
			final BackendState baseR3Lrd, //
			final Header header)
	{

		if ((ttHeadPartyComV == null) || (ttHeadPartyComV.getNumRows() <= 0))
		{
			return;
		}

		header.setBillTo(salesDoc.createBillTo());

		final ShipTo shipTo = salesDoc.createShipTo();

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("handleTtHeadPartyComV()");
		}

		if (ttHeadPartyComV.getNumRows() > 0)
		{

			for (int i = 0; i < ttHeadPartyComV.getNumRows(); i++)
			{
				final String partnerFunction = mapPartnerFunction(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE),
						ttHeadPartyComR);
				if (partnerFunction != null)
				{
					populatePartnerData(ttHeadPartyComV, ttHeadPartyComR, baseR3Lrd, header, shipTo, partnerFunction);
				}
				ttHeadPartyComV.nextRow();
			}

		}
		sapLogger.exiting();
	}

	/**
	 * @param ttHeadPartyComV
	 * @param ttHeadPartyComR
	 * @param baseR3Lrd
	 * @param header
	 * @param shipTo
	 * @param partnerFunction
	 */
	protected void populatePartnerData(final JCoTable ttHeadPartyComV, final JCoTable ttHeadPartyComR,
			final BackendState baseR3Lrd, final Header header, final ShipTo shipTo, final String partnerFunction)
	{
		final String partnerId = ConversionTools.addLeadingZerosToNumericID(ttHeadPartyComV.getString("KUNNR"), 10);

		final PartnerListEntry partner = (PartnerListEntry) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
		partner.setPartnerId(partnerId);
		partner.setPartnerTechKey(new TechKey(partnerId));
		partner.setHandle(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE));

		setPartnerData(header, partnerFunction, partnerId, partner);

		if (partnerFunction.equals(ConstantsR3Lrd.ROLE_BILLPARTY))
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("header level billto found");
			}
			final BillTo billTo = header.getBillTo();
			billTo.setId(partnerId);
			billTo.setHandle(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("fillBillToAdress start for: " + billTo.getTechKey());
			}
			final Address address = readAddress(ttHeadPartyComV, ttHeadPartyComR, partnerId);
			billTo.setAddress(address);

		}

		else if (partnerFunction.equals(ConstantsR3Lrd.ROLE_SHIPTO))
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("header level shipto found");
			}
			shipTo.setId(partnerId);
			shipTo.setHandle(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("fillShipToAdress start for: " + shipTo.getTechKey());
			}
			final Address address = readAddress(ttHeadPartyComV, ttHeadPartyComR, partnerId);

			shipTo.setAddress(address);

			header.setShipTo(shipTo);
		}
		else if (partnerFunction.equals(ConstantsR3Lrd.ROLE_SOLDTO))
		{
			// store soldTo handle
			baseR3Lrd.setSoldToHandle(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
		}
		else if (partnerFunction.equals(ConstantsR3Lrd.ROLE_PAYER))
		{
			// store soldTo handle
			baseR3Lrd.setPayerHandle(ttHeadPartyComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
		}
	}

	/**
	 * @param header
	 * @param partnerFunction
	 * @param partnerId
	 * @param partner
	 */
	protected void setPartnerData(final Header header, final String partnerFunction, final String partnerId,
			final PartnerListEntry partner)
	{
		final String bpRole = mapPartnerFunctionToRole(partnerFunction);
		if (bpRole != null)
		{
			header.getPartnerList().setPartnerData(bpRole, partner);
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("added partner: " + partnerId + partnerFunction);
			}
		}
	}

	protected Address readAddress(final JCoTable ttHeadPartyComV, final JCoTable ttHeadPartyComR, final String partnerId)
	{

		final Address address = (Address) genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);
		address.setType(Address.TYPE_ORGANISATION);
		// address.setType(Address.TYPE_PERSON); // B2C

		address.setAddrguid(TechKey.generateKey().toString());
		final String name2 = ttHeadPartyComV.getString("NAME2");
		final String name = ttHeadPartyComV.getString("NAME");
		address.setFirstName(name2);
		address.setLastName(name);
		address.setName1(name);
		address.setName2(name2);
		address.setCompanyName(name);
		address.setStreet(ttHeadPartyComV.getString("STREET"));
		String houseNumber = ttHeadPartyComV.getString("HNUM");
		if ("000000".equals(houseNumber))
		{
			houseNumber = "";
		}
		address.setHouseNo(houseNumber);
		address.setPostlCod1(ttHeadPartyComV.getString("PCODE"));
		address.setCity(ttHeadPartyComV.getString("CITY"));
		address.setDistrict(ttHeadPartyComV.getString("CITY2"));
		address.setCountry(ttHeadPartyComV.getString("COUNTRY").trim());
		address.setRegion(ttHeadPartyComV.getString("REGION").trim());
		address.setTaxJurCode(ttHeadPartyComV.getString("TAXJURCODE").trim());
		address.setEmail(ttHeadPartyComV.getString("EMAIL"));
		address.setTel1Numbr(ttHeadPartyComV.getString("TELNUM"));
		address.setTel1Ext(ttHeadPartyComV.getString("TELEXT"));
		address.setFaxNumber(ttHeadPartyComV.getString("FAXNUM"));
		address.setFaxExtens(ttHeadPartyComV.getString("FAXEXT"));
		address.setTelmob1(ttHeadPartyComV.getString("MOBNUM"));
		address.setTitleKey(ttHeadPartyComV.getString("TITLE"));
		address.setAddressPartner(partnerId);
		address.setAddrnum(partnerId); // As we do not have a
		// native address number
		// from LO-API

		address.setAddressStringC(ttHeadPartyComR.getString("ADDRESS_SHORT"));

		address.clearX();
		return address;
	}

	protected String mapPartnerFunctionToRole(final String partnerFunction)
	{
		switch (partnerFunction)
		{
			case ConstantsR3Lrd.ROLE_SOLDTO:
				return PartnerFunctionData.SOLDTO;

			case ConstantsR3Lrd.ROLE_SHIPTO:
				return PartnerFunctionData.SHIPTO;

			case ConstantsR3Lrd.ROLE_CONTACT:
				return PartnerFunctionData.CONTACT;

			case ConstantsR3Lrd.ROLE_PAYER:
				return PartnerFunctionData.PAYER;

			case ConstantsR3Lrd.ROLE_BILLPARTY:
				return PartnerFunctionData.BILLTO;

			default:
				return null;
		}
	}

	protected String mapPartnerFunction(final String handle, final JCoTable ttPartyComR)
	{
		for (int i = 0; i < ttPartyComR.getNumRows(); i++)
		{
			ttPartyComR.setRow(i);
			if (handle.equals(ttPartyComR.getString(ConstantsR3Lrd.FIELD_HANDLE)))
			{
				return ttPartyComR.getString("PARVW_INT_R");
			}
		}
		return null;
	}

	/**
	 * Write JCO partner related tables before the LO-API update call
	 *
	 * @param salesDoc
	 * @param PartnerComV
	 *           JCO table with partner data (ABAP type TDT_RFC_PARTY_COMV)
	 * @param PartnerComX
	 *           JCO table with change indicators for partner data (ABAP type TDT_RFC_PARTY_COMC)
	 *
	 * @param tc
	 *           Configuration settings
	 * @param paytypeCOD
	 *           indicates that selected paytype was COD
	 * @param objInst
	 */
	public void write(final SalesDocument salesDoc, final JCoTable PartnerComV, final JCoTable PartnerComX,
			final TransactionConfiguration tc, final JCoTable objInst)
	{

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("fillPartner start");
		}
		// unit test enabling
		if (tc == null)
		{
			sapLogger.debug("No shop object provided");
			return;
		}
		final Header header = salesDoc.getHeader();

		// BillTo on header level
		final String headerTechKey = header.getTechKey().getIdAsString();
		populatePartnerTableParams(PartnerComV, PartnerComX, objInst, header, headerTechKey);

		final String role = mapPartnerFunctionToRole(ConstantsR3Lrd.ROLE_CONTACT);
		final PartnerListEntry contact = header.getPartnerList().getPartnerData(role);
		if ((contact != null) && contact.getHandle().isEmpty())
		{
			PartnerComV.appendRow();
			PartnerComX.appendRow();

			final String handle = TechKey.generateKey().getIdAsString();

			PartnerComV.setValue(ConstantsR3Lrd.FIELD_HANDLE, handle);
			PartnerComV.setValue("KUNNR", contact.getPartnerId());
			PartnerComV.setValue("PARVW", ConstantsR3Lrd.ROLE_CONTACT);

			PartnerComX.setValue(ConstantsR3Lrd.FIELD_HANDLE, handle);
			PartnerComX.setValue("KUNNR", ConstantsR3Lrd.ABAP_TRUE);
			PartnerComX.setValue("PARVW", ConstantsR3Lrd.ABAP_TRUE);

			addToObjInst(objInst, handle, headerTechKey, OBJECT_ID_PARTY);
		}

	}

	/**
	 * @param PartnerComV
	 * @param PartnerComX
	 * @param objInst
	 * @param header
	 * @param headerTechKey
	 */
	protected void populatePartnerTableParams(final JCoTable PartnerComV, final JCoTable PartnerComX, final JCoTable objInst,
			final Header header, final String headerTechKey)
	{
		final BillTo billTo = header.getBillTo();
		if (billTo != null && (billTo.isIdX() || isAddressChanged(billTo.getAddress())))
		{
			// don't set address from billto in case it's a guest user scenario,
			// then the billto adress will be set directly from common address

			setPartnerRFCTables(PartnerComV, PartnerComX, objInst, billTo.getHandle(), headerTechKey, // parentHandle
					billTo.getId(), billTo);
		}

		// ShipTo on Header level
		final ShipTo shipTo = header.getShipTo();
		if (shipTo != null && (shipTo.isIdX() || isAddressChanged(shipTo.getAddress())))
		{
			setPartnerRFCTables(PartnerComV, PartnerComX, objInst, shipTo.getHandle(), headerTechKey, // parentHandle
					shipTo.getId(), shipTo);
		}
	}

	protected void setPartnerRFCTables(final JCoTable PartnerComV, final JCoTable PartnerComX, final JCoTable ObjInst,
			final String handle, final String parentHandle, final String partnerNumber, final PartnerBase partner)
	{

		PartnerComV.appendRow();
		PartnerComV.setValue(ConstantsR3Lrd.FIELD_HANDLE, handle);

		if (partner != null)
		{
			if (partner.isIdX())
			{
				PartnerComV.setValue("KUNNR", partnerNumber);
			}

			PartnerComX.appendRow();
			PartnerComX.setValue(ConstantsR3Lrd.FIELD_HANDLE, handle);
			if (partner.isIdX())
			{
				PartnerComX.setValue("KUNNR", ConstantsR3Lrd.ABAP_TRUE);
			}

			ObjInst.appendRow();
			ObjInst.setValue(ConstantsR3Lrd.FIELD_HANDLE, handle);
			ObjInst.setValue(ConstantsR3Lrd.FIELD_HANDLE_PARENT, parentHandle);
			ObjInst.setValue(ConstantsR3Lrd.FIELD_OBJECT_ID, OBJECT_ID_PARTY);

			Address address = null;
			address = partner.getAddress();

			if (address != null && isAddressChanged(address))
			{
				PartnerComV.setValue("NAME", address.getLastName());
				PartnerComX.setValue("NAME", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("NAME2", address.getFirstName());
				PartnerComX.setValue("NAME2", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("CITY", address.getCity());
				PartnerComX.setValue("CITY", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("STREET", address.getStreet());
				PartnerComX.setValue("STREET", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("HNUM", address.getHouseNo());
				PartnerComX.setValue("HNUM", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("COUNTRY", address.getCountry());
				PartnerComX.setValue("COUNTRY", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("CITY2", address.getDistrict());
				PartnerComX.setValue("CITY2", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("PCODE", address.getPostlCod1());
				PartnerComX.setValue("PCODE", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("PBOX_PCODE", address.getPostlCod2());
				PartnerComX.setValue("PCODE", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("REGION", address.getRegion());
				PartnerComX.setValue("REGION", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("TELNUM", address.getTel1Numbr());
				PartnerComX.setValue("TELNUM", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("TELEXT", address.getTel1Ext());
				PartnerComX.setValue("TELEXT", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("FAXNUM", address.getFaxNumber());
				PartnerComX.setValue("FAXNUM", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("FAXEXT", address.getFaxExtens());
				PartnerComX.setValue("FAXEXT", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("EMAIL", address.getEmail());
				PartnerComX.setValue("EMAIL", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("TAXJURCODE", address.getTaxJurCode());
				PartnerComX.setValue("TAXJURCODE", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("MOBNUM", address.getTelmob1());
				PartnerComX.setValue("MOBNUM", ConstantsR3Lrd.ABAP_TRUE);
				PartnerComV.setValue("TITLE", address.getTitleKey());
				PartnerComX.setValue("TITLE", ConstantsR3Lrd.ABAP_TRUE);
			}
		}

	}

	@SuppressWarnings("squid:MethodCyclomaticComplexity")
	protected boolean isAddressChanged(final Address address)
	{
		if (address == null)
		{
			return false;
		}

		boolean result = address.getStreetX() || address.getCityX() || address.getHouseNoX();
		result = result || address.getCountryX() || address.getPostlCod1X() || address.getPostlCod2X();
		result = result || address.getRegionX() || address.getFirstNameX() || address.getLastNameX();
		result = result || address.getTel1NumbrX() || address.getTel1ExtX() || address.getFaxNumberX();
		result = result || address.getFaxExtensX() || address.getEmailX() || address.getTaxJurCodeX();
		result = result || address.getTitleKeyX() || address.getCompanyNameX() || address.getTelmob1X();
		result = result || address.getDistrictX();

		return result;
	}
}
