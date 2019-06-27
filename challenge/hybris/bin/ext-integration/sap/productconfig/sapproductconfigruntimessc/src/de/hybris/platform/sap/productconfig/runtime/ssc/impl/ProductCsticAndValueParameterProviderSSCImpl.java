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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;
import com.sap.custdev.projects.fbs.slc.helper.ConfigSessionManager;
import com.sap.custdev.projects.fbs.slc.pricing.spc.api.SPCConstants.DataModel;
import com.sap.sce.front.base.KnowledgeBase;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_cstic_seq;
import com.sap.sce.kbrt.oo_class_seq;
import com.sap.sce.kbrt.imp.kb_cstic_imp;
import com.sap.sce.kbrt.imp.kb_product_imp;
import com.sap.sxe.sys.language;
import com.sap.sxe.sys.read_only_sequence;
import com.sap.sxe.util.domain;
import com.sap.sxe.util.imp.symbol_type_imp;
import com.sap.sxe.util.imp.symbol_value_imp;


/**
 * Default SSC implementation of {@link ProductCsticAndValueParameterProvider}
 */
public class ProductCsticAndValueParameterProviderSSCImpl implements ProductCsticAndValueParameterProvider
{
	private static final Logger LOG = Logger.getLogger(ProductCsticAndValueParameterProviderSSCImpl.class);

	private static final String PRODUCT_TYPE = "MARA";
	private static final String CANNOT_FIND_KNOWLEDGE_BASE_FOR_PRODUCT = "Cannot find knowledge base for product";
	private static final String MORE_THAN_ONE_KNOWLEDGE_BASE_FOR_PRODUCT = "More than one knowledgebase was found for product";

	private I18NService i18NService;

	@Override
	public Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode)
	{
		Map<String, CsticParameterWithValues> csticParametersWithValues = new HashMap<>();

		try
		{
			final IConfigSession session = createSession();
			final KnowledgeBase knowledgeBase = retrieveKnowledgeBase(productCode, session);
			if (knowledgeBase != null)
			{
				csticParametersWithValues = processKnowledgeBase(knowledgeBase);
			}
			session.closeSession();
		}
		catch (final IpcCommandException e)
		{
			LOG.debug(CANNOT_FIND_KNOWLEDGE_BASE_FOR_PRODUCT + " " + productCode, e);
		}

		return csticParametersWithValues;
	}

	protected IConfigSession createSession() throws IpcCommandException
	{
		final String runtimEnv = System.getProperty(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT);
		if (runtimEnv == null)
		{
			System.getProperties().put(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT,
					SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT_HYBRIS);
		}

		final IConfigSession session = new ConfigSessionImpl();

		final String language = i18NService.getCurrentLocale().getLanguage().toUpperCase(Locale.ENGLISH);
		final String sessionId = UUID.randomUUID().toString();

		session.createSession("true", sessionId, null, false, false, language);
		session.setPricingDatamodel(DataModel.CRM);

		return session;
	}

	protected KnowledgeBase retrieveKnowledgeBase(final String productCode, final IConfigSession session)
			throws IpcCommandException
	{
		KnowledgeBase knowledgeBase = null;

		final IKnowledgeBaseData[] kbData = session.findKnowledgeBases(PRODUCT_TYPE, productCode, null, null, null, null, null,
				null, false);

		if (kbData == null || kbData.length == 0)
		{
			LOG.error(CANNOT_FIND_KNOWLEDGE_BASE_FOR_PRODUCT + " " + productCode);
			return null;
		}

		if (kbData.length > 1)
		{
			LOG.warn(MORE_THAN_ONE_KNOWLEDGE_BASE_FOR_PRODUCT + " " + productCode);
		}
		final String logsys = kbData[0].getKbLogsys();
		final ConfigSessionManager configSessionManager = session.getConfigSessionManager();
		knowledgeBase = configSessionManager.getKB(null, logsys, null, null, productCode, PRODUCT_TYPE, null, null, null);

		if (knowledgeBase == null)
		{
			LOG.error(CANNOT_FIND_KNOWLEDGE_BASE_FOR_PRODUCT + " " + productCode);
		}
		return knowledgeBase;
	}

	protected Map<String, CsticParameterWithValues> processKnowledgeBase(final KnowledgeBase knowledgeBase)
	{
		final Map<String, CsticParameterWithValues> csticParametersWithValues = new HashMap<>();

		final kb kb = knowledgeBase.getKb();
		final oo_class_seq classes = kb.kb_get_oo_classes();

		final Enumeration enumClasses = classes.elements();
		for (final Object obj : Collections.list(enumClasses))
		{
			if (obj instanceof kb_product_imp)
			{
				final kb_product_imp productImp = (kb_product_imp) obj;
				processCstics(csticParametersWithValues, productImp);
			}
		}
		return csticParametersWithValues;
	}

	protected void processCstics(final Map<String, CsticParameterWithValues> csticsMap, final kb_product_imp productImp)
	{
		final kb_cstic_seq cstics = productImp.kb_get_cstics();

		final Enumeration enumCstic = cstics.elements();
		for (final Object obj : Collections.list(enumCstic))
		{
			final kb_cstic_imp cstic = (kb_cstic_imp) obj;
			// Ignore pricing cstics
			if (!cstic.kb_is_pricing_cstic_p())
			{
				processCstic(cstic, csticsMap);
			}
		}
	}

	protected void processCstic(final kb_cstic_imp cstic, final Map<String, CsticParameterWithValues> csticsMap)
	{
		final String csticName = retrieveCsticName(cstic);

		// Cumulate values if several (sub-)instances contain the same cstic
		CsticParameterWithValues csticParameterWithValues = csticsMap.get(csticName);
		if (csticParameterWithValues == null)
		{
			csticParameterWithValues = new CsticParameterWithValues();
			final CsticParameter csticParameter = new CsticParameter();
			csticParameter.setCsticName(csticName);
			csticParameter.setCsticDescription(retrieveCsticDescription(cstic));
			csticParameterWithValues.setCstic(csticParameter);
			csticParameterWithValues.setValues(new ArrayList<ValueParameter>());
			csticsMap.put(csticName, csticParameterWithValues);
		}

		final List<ValueParameter> values = csticParameterWithValues.getValues();

		if (isStringCstic(cstic))
		{
			final List<String> csticValues = retrieveCsticValues(cstic);
			fillValueListFromValueSequence(values, csticValues);
		}
		else
		{
			final String domainAsString = retrieveCsticDomainAsSting(cstic);
			fillValueList(values, domainAsString);
		}
	}

	protected boolean isStringCstic(final kb_cstic_imp cstic)
	{
		return cstic.kb_get_type_descriptor() instanceof symbol_type_imp;
	}

	protected String retrieveCsticName(final kb_cstic_imp cstic)
	{
		return cstic.kb_get_cstic_ext_form();
	}

	protected String retrieveCsticDescription(final kb_cstic_imp cstic)
	{
		final String languageId = i18NService.getCurrentLocale().getLanguage().toUpperCase(Locale.ENGLISH);
		return cstic.expl_get_text(language.find_language_for_id2(languageId));
	}

	protected String retrieveCsticDomainAsSting(final kb_cstic_imp cstic)
	{
		String domainAsString = null;
		// Domain can be determined only for BDT
		if (hasKbBdtType(cstic))
		{
			final domain domain = cstic.kb_get_domain();
			if (!domain.util_unconstrained_domain_p())
			{
				// No values for unconstrained domains
				domainAsString = domain.toString();
			}
		}
		return domainAsString;
	}

	public boolean hasKbBdtType(final kb_cstic_imp cstic)
	{
		return cstic.kb_has_bdt_type_descriptor_p();
	}

	protected void fillValueList(final List<ValueParameter> values, final String domainAsString)
	{
		if (domainAsString == null || domainAsString.isEmpty())
		{
			return;
		}
		final String[] valueArray = domainAsString.split(",");

		if (valueArray != null && valueArray.length > 0)
		{
			for (int i = 0; i < valueArray.length; i++)
			{
				final String value = valueArray[i].trim();


				if (!isValueAlreadyExists(value, values))
				{
					final ValueParameter valueParameter = new ValueParameter();
					valueParameter.setValueName(value);
					valueParameter.setValueDescription("");
					values.add(valueParameter);
				}
			}
		}
	}

	protected List<String> retrieveCsticValues(final kb_cstic_imp cstic)
	{
		final List valueList = new ArrayList<>();
		final read_only_sequence valueSequence = cstic.kb_get_val_sequence();
		final Enumeration enumValue = valueSequence.elements();
		for (final Object obj : Collections.list(enumValue))
		{
			final symbol_value_imp value = (symbol_value_imp) obj;
			valueList.add(value.toString());
		}
		return valueList;
	}

	protected void fillValueListFromValueSequence(final List<ValueParameter> values, final List<String> csticValues)
	{
		if (csticValues != null && !csticValues.isEmpty())
		{
			for (final String value : csticValues)
			{
				if (!isValueAlreadyExists(value, values))
				{
					final ValueParameter valueParameter = new ValueParameter();
					valueParameter.setValueName(value);
					valueParameter.setValueDescription("");
					values.add(valueParameter);
				}
			}
		}
	}

	protected boolean isValueAlreadyExists(final String value, final List<ValueParameter> values)
	{
		boolean exists = false;
		for (final ValueParameter valueParameter : values)
		{
			if (valueParameter.getValueName().equals(value))
			{
				exists = true;
				break;
			}
		}
		return exists;
	}

	/**
	 * @return the i18NService
	 */
	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}
}
