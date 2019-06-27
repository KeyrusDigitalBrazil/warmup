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
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;

import com.sap.conn.jco.JCoTable;


/**
 * Responsible to map texts between LO-API and BOL layer
 */
public class HeadTextMapper extends TextMapper
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(HeadTextMapper.class.getName());


	@Override
	public void init()
	{
		/* nothing to initialize */
	}


	/**
	 * Writes header text information from BOL to JCO tables used to update the sales document in LO-API
	 *
	 * @param header
	 *           BOL representation of header
	 * @param textV
	 * @param textX
	 * @param objInst
	 */
	public void write(final Header header, //
			final JCoTable textV, //
			final JCoTable textX, //
			final JCoTable objInst)
	{

		final Text text = header.getText();

		if (text == null)
		{
			return;
		}

		boolean sendKeyFields = false;
		boolean sendHeaderText = true;

		String handle = text.getHandle();

		// If text is empty, do not create it. This check does only apply
		// if we have no ERP handle yet, because otherwise we need to
		// update the text in any case

		if ((handle == null) || handle.isEmpty())
		{
			final String content = text.getText();
			if ((content == null) || content.isEmpty())
			{
				sendHeaderText = false;
			}
			else
			{
				sendKeyFields = true;
				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("We need to create a new handle and set additional fields");
				}
				handle = TechKey.generateKey().getIdAsString();
			}
		}

		final String headHandle = header.getHandle();

		isSendHeaderText(header, textV, textX, objInst, text, sendKeyFields, sendHeaderText, handle, headHandle);
	}


	/**
	 * @param header
	 * @param textV
	 * @param textX
	 * @param objInst
	 * @param text
	 * @param sendKeyFields
	 * @param sendHeaderText
	 * @param handle
	 * @param headHandle
	 */
	@SuppressWarnings("squid:S00107")
	private void isSendHeaderText(final Header header, final JCoTable textV, final JCoTable textX, final JCoTable objInst,
			final Text text, final boolean sendKeyFields, final boolean sendHeaderText, final String handle, final String headHandle)
	{
		if (sendHeaderText)
		{

			textV.appendRow();
			JCoHelper.setValue(textV, handle, ConstantsR3Lrd.FIELD_HANDLE);
			if (sendKeyFields)
			{
				JCoHelper.setValue(textV, configTextId, ConstantsR3Lrd.FIELD_ID);
				JCoHelper.setValue(textV, configLangIso, ConstantsR3Lrd.FIELD_SPRAS_ISO);
			}
			if (header.getText() != null)
			{
				JCoHelper.setValue(textV, text.getText(), ConstantsR3Lrd.FIELD_TEXT_STRING);
			}

			if (sapLogger.isDebugEnabled())
			{
				traceText("Header text will be sent to ERP", configTextId, configLangIso, text, handle, headHandle, sapLogger);
			}

			textX.appendRow();
			JCoHelper.setValue(textX, handle, ConstantsR3Lrd.FIELD_HANDLE);
			if (sendKeyFields)
			{
				JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_ID);
				JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_SPRAS_ISO);
			}
			JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_TEXT_STRING);

			addToObjInst(objInst, handle, headHandle, OBJECT_ID_TEXT);
		}
	}

	/**
	 * Reads header text from JCO table provided by LO-API
	 *
	 * @param etHeadTextV
	 * @param head
	 *           BOL representation of header
	 */
	public void read(final JCoTable etHeadTextV, //
			final Header head)
	{

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("handle texts for header id: " + configTextId);
		}

		// Loop over all existing texts to find the right one with given TEXT_ID

		etHeadTextV.firstRow();
		if (etHeadTextV.getNumRows() > 0)
		{
			do
			{

				final String id = JCoHelper.getString(etHeadTextV, ConstantsR3Lrd.FIELD_ID);
				checkCurrentTextId(id);

				if (configTextId.equals(id))
				{

					// text found
					final Text text = head.createText();
					text.setId(id);
					text.setText(JCoHelper.getString(etHeadTextV, ConstantsR3Lrd.FIELD_TEXT_STRING));
					text.setHandle(JCoHelper.getString(etHeadTextV, ConstantsR3Lrd.FIELD_HANDLE));

					head.setText(text);
					isDebugEnabled(text);
					return;
				}
			}
			while (etHeadTextV.nextRow());
		}
	}


	/**
	 * @param id
	 */
	private void checkCurrentTextId(final String id)
	{
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("Current Text ID:" + id);
		}
	}


	/**
	 * @param text
	 */
	private void isDebugEnabled(final Text text)
	{
		if (sapLogger.isDebugEnabled())
		{
			traceReadTexts(text, "Standard text found", sapLogger);
		}
	}
}
