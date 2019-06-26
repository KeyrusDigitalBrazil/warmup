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
package de.hybris.platform.sap.sapmodel.authors;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeValue;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator;
import de.hybris.platform.catalog.jalo.classification.impex.UnitAwareValue;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.ColumnParams;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.impex.jalo.translators.AtomicValueTranslator;
import de.hybris.platform.impex.jalo.translators.NotifiedSpecialValueTranslator;
import de.hybris.platform.impex.jalo.translators.SingleValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.CSVUtils;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;


public class SapClassificationAttributeAuthorTranslator extends ClassificationAttributeTranslator
		implements NotifiedSpecialValueTranslator
{
	public static final String IMPEX_NONEXISTEND_CLSATTRVALUE_FALLBACK_KEY = "impex.nonexistend.clsattrvalue.fallback.enabled";
	/** Used logger instance. */
	private static final Logger LOG = Logger.getLogger(SapClassificationAttributeAuthorTranslator.class);

	private final String VALUEAUTHORSEPARATOR = "#";

	private Collection<UnitAwareValueAuthor> classificationAttributeAuthor;

	final private static String BEAN_NAME = "sapClassificationAttributeAuthorHelper";
	private SapClassificationAttributeAuthorHelper sapClassificationAttributeAuthorHelper;

	private ClassificationService classificationService;
	private ModelService modelService;
	private ProductService productService;

	private SpecialColumnDescriptor columnDescriptor;

	/**
	 * General no-arg constructor
	 */
	public SapClassificationAttributeAuthorTranslator()
	{
		super();
	}

	/**
	 * Testing purpose constructor.
	 */
	public SapClassificationAttributeAuthorTranslator(final ClassificationSystemVersion sysVer, final ClassificationAttribute attr,
			final char delimiter, final Language lang)
	{
		super(sysVer, attr, delimiter, lang);
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		super.init(columnDescriptor);

		if (sapClassificationAttributeAuthorHelper == null)
		{
			sapClassificationAttributeAuthorHelper = (SapClassificationAttributeAuthorHelper) Registry.getApplicationContext()
					.getBean(BEAN_NAME);
			classificationService = sapClassificationAttributeAuthorHelper.getClassificationService();
			modelService = sapClassificationAttributeAuthorHelper.getModelService();
			productService = sapClassificationAttributeAuthorHelper.getProductService();
		}
		this.columnDescriptor = columnDescriptor;
	}

	private SingleValueTranslator fallbackValueTranslator = null;

	@Override
	protected AbstractValueTranslator getFallbackValueTranslator()
	{
		if (fallbackValueTranslator == null)
		{
			fallbackValueTranslator = new AtomicValueTranslator(null, String.class);
		}
		return fallbackValueTranslator;
	}

	@Override
	public Collection<UnitAwareValue> translateCurrentUnitAwareValues(final ValueLine line,
			final ClassAttributeAssignment assignment, final Product processedItem) throws HeaderValidationException
	{
		if (assignment == null)
		{
			return Collections.emptyList();
		}

		if (this.classAttrAssignment == null)
		{
			this.classAttrAssignment = assignment;
		}

		final AbstractValueTranslator trans = getSingleCellValueTranslator(assignment);
		// skip classification attribute if cell is marked as IGNORE
		Collection values = null;
		if (currentCellValue != null && currentCellValue.length() > 0)
		{
			for (final Iterator iter = splitValues(assignment, currentCellValue).iterator(); iter.hasNext();)
			{
				final String singleStr = (String) iter.next();
				values = populateValues(line, assignment, processedItem, trans, values, singleStr);
			}
		}
		this.classificationAttributeAuthor = values;

		return values;
	}

	private Collection populateValues(final ValueLine line, final ClassAttributeAssignment assignment,
									  final Product processedItem, final AbstractValueTranslator trans,
									  Collection values, final String singleStr) {
		if (singleStr != null && singleStr.length() > 0)
		{
			if (values == null)
			{
				values = new LinkedList();
			}

			final String singleValueString = getValueWithoutAuthor(singleStr);

			try
			{
				final Object transValue = trans.importValue(CSVUtils.unescapeString(singleValueString, TO_ESCAPE, true), processedItem);

				final String author = getValueAuthor(singleStr);

				final ClassificationAttributeUnit classificationAttributeUnit = getAttributeUnit(assignment, singleValueString,
						this.systemName, this.versionName);
				values.add(new UnitAwareValueAuthor(transValue, classificationAttributeUnit, author));
			}
			catch (final JaloInvalidParameterException e)
			{
				handleJaloInvalidParameterException(line, assignment, processedItem, values, singleStr, singleValueString, e);
			}
		}
		return values;
	}

	private void handleJaloInvalidParameterException(final ValueLine line, final ClassAttributeAssignment assignment, final Product processedItem,
													 final Collection values, final String singleStr, final String singleValueString,
													 final JaloInvalidParameterException e) {
		if (Config.getBoolean(IMPEX_NONEXISTEND_CLSATTRVALUE_FALLBACK_KEY, false))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Fallback ENABLED");
			}
			LOG.warn("Value " + singleStr + " is not of type " + assignment.getAttributeType().getCode()
					+ " will use type string as fallback (" + e.getMessage() + ")");
			final Object transValue = getFallbackValueTranslator()
					.importValue(CSVUtils.unescapeString(singleValueString, TO_ESCAPE, true), processedItem);
			values.add(new UnitAwareValue(transValue, null));
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Fallback DISABLED. Marking line as unresolved. Will try to import value in another pass");
			}
			line.getValueEntry(this.columnDescriptor.getValuePosition()).markUnresolved(e.getMessage());
			LOG.warn(e);
			LOG.warn(e.getMessage());
		}
	}

	private String extractAttributeUnitName(final ClassAttributeAssignment assignment, final String singleStr)
	{
		final String unitName = null;
		boolean warn = false;
		if (assignment.getUnit() != null && singleStr != null && singleStr.indexOf(attributeSeparator) != -1)
		{
			if (singleStr.lastIndexOf(attributeSeparator) == singleStr.indexOf(attributeSeparator))
			{
				return singleStr.split(String.valueOf(attributeSeparator))[1];
			}
			else
			{
				warn = true;
			}

		}
		final String msg = "Invalid classification attribute unit syntax - was: [" + singleStr + "], expected format [value"
				+ attributeSeparator + "unit].";

		if (((ColumnParams) this.columnDescriptor.getDescriptorData()).hasItemPattern())
		{
			final String defaultAttributeUnitName = findDefaultAttributeUnitName(((ColumnParams) this.columnDescriptor.getDescriptorData()).getItemPatternLists());
			if (defaultAttributeUnitName != null && warn)
			{
				LOG.warn(msg + " Classification attribute unit from script header [" + defaultAttributeUnitName + "] will be used instead.");
				return defaultAttributeUnitName;
			}
		}
		if (warn && assignment.getUnit() != null)
		{
			LOG.warn(msg + " Classification attribute unit from attribute assignment [" + assignment.getUnit().getCode()
					+ "] will be used instead.");
		}
		return unitName;
	}

	private String findDefaultAttributeUnitName(final List<ColumnParams>[] itemPatternLists) {
		for (final List<ColumnParams> columnParamsList : itemPatternLists)
		{
			for (final ColumnParams columnParams : columnParamsList)
			{
				if (("unit").equals(columnParams.getQualifier())
						&& columnParams.getModifier(ImpExConstants.Syntax.Modifier.DEFAULT) != null)
				{
					return columnParams.getModifier(ImpExConstants.Syntax.Modifier.DEFAULT);
				}
			}
		}
		return null;
	}

	@Override
	public void notifyTranslationEnd(final ValueLine line, final HeaderDescriptor header, final Item processedItem)
			throws ImpExException
	{
		super.notifyTranslationEnd(line, header, processedItem);

		String productCode = null;
		try
		{
			productCode = (String) processedItem.getAttribute("code");
			final CatalogVersion catalogVersion = (CatalogVersion) processedItem.getAttribute("catalogVersion");
			final CatalogVersionModel catalogVersionModel = modelService.get(catalogVersion.getPK());

			final ProductModel productModel = productService.getProductForCode(catalogVersionModel, productCode);
			final FeatureList featureList = classificationService.getFeatures(productModel);

			updateProductFeatures(productModel, featureList);

			LOG.info(String.format(
					"The current classification system attribute value/author for the product [%s] have been udpated.", productCode));

		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.info("The product value/author has not been imported yet!" + ex.getMessage());
		}
		catch (final Exception ex)
		{
			LOG.error(ex);
			LOG.error(String.format(
					"Something went wrong while importingg classification system attribute value/author for the product [%s]!",
					productCode) + ex.getMessage());
		}
	}

	private ClassificationAttributeUnit getAttributeUnit(final ClassAttributeAssignment assignment, final String singleStr,
														 final String systemName, final String versionName)
	{
		final String unitName = extractAttributeUnitName(assignment, singleStr);
		if (unitName == null)
		{
			return null;
		}
		try
		{
			final ClassificationSystem classificationSystem = CatalogManager.getInstance().getClassificationSystem(systemName);
			final ClassificationSystemVersion version;
			if (classificationSystem == null)
			{
				LOG.warn("Classification system with id [" + systemName + "] not found.");
				return null;
			}
			else
			{
				version = classificationSystem.getSystemVersion(versionName);
			}
			if (version == null)
			{
				LOG.warn("Classification system version with name [" + versionName + "] not found in classification system ["
						+ systemName + "].");
				return null;
			}
			else
			{
				return version.getAttributeUnit(unitName);
			}
		}
		catch (final JaloItemNotFoundException e)
		{
			LOG.warn(e);
			LOG.warn("Classification attribute unit with code [" + unitName + "] not found in classification system [" + systemName
					+ ":" + versionName + "].");
			return null;
		}
	}

	private String getValueAuthor(final String singleStr)
	{
		if (singleStr != null)
		{
			final int position = singleStr.lastIndexOf(VALUEAUTHORSEPARATOR);
			return position > 0 ? singleStr.substring(position + 1) : null;
		}
		return null;
	}

	private String getValueWithoutAuthor(final String singleStr)
	{
		if (singleStr != null)
		{
			final int position = singleStr.lastIndexOf(VALUEAUTHORSEPARATOR);
			return position > 0 ? singleStr.substring(0, position) : singleStr;
		}
		return singleStr;
	}

	private void updateProductFeatures(final ProductModel product, final FeatureList features)
	{

		final ClassAttributeAssignmentModel assignment = getClassAttributeAssignmentModel(features);
		if (assignment == null)
		{
			return;
		}

		if (classificationAttributeAuthor == null)
		{
			return;
		}

		if (product == null)
		{
			return;
		}

		final List<ProductFeatureModel> productFeature = product.getFeatures().stream()
				.filter(p -> assignment.equals(p.getClassificationAttributeAssignment())).collect(Collectors.toList());

		if (productFeature != null)
		{
			productFeature.forEach(p -> {
				classificationAttributeAuthor.forEach(c -> {
					if (p.getValue().equals(c.getValue()) || (p.getValue() instanceof ClassificationAttributeValueModel
							&& c.getValue() instanceof ClassificationAttributeValue && ((ClassificationAttributeValueModel) p.getValue())
									.getPk().equals(((ClassificationAttributeValue) c.getValue()).getPK())))
					{
						p.setAuthor(c.getValueAuthor());
						this.modelService.save(p);
					}
				});
			});
		}
	}

	private ClassAttributeAssignmentModel getClassAttributeAssignmentModel(final FeatureList features)
	{
		final String attributeAuthorCode = buildClassificationAttributeCode();

		for (final Feature feature : features)
		{
			if (feature.getValues() != null && !feature.getValues().isEmpty())
			{
				if (!feature.equals(features.getFeatureByCode(attributeAuthorCode)))
				{
					continue;
				}
				return feature.getClassAttributeAssignment();
			}
		}
		return null;
	}

	private String buildClassificationAttributeCode()
	{
		return this.classSystemVersion.getFullVersionName() + "/" + this.classAttrAssignment.getClassificationClass().getCode()
				+ "." + this.classAttr.getCode().toLowerCase();
	}
}
