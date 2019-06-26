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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator;
import de.hybris.platform.catalog.jalo.classification.impex.UnitAwareValue;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.jalo.classification.util.FeatureValue;
import de.hybris.platform.catalog.jalo.classification.util.TypedFeature;
import de.hybris.platform.core.PK;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.util.CSVUtils;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


public class MarketplaceClassificationAttributeTranslator extends ClassificationAttributeTranslator
{
	private SpecialColumnDescriptor columnDescriptor;

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		basicInit(columnDescriptor);
		i18nInit(columnDescriptor);
	}

	@Override
	public void performImport(final String cellValue, final Item processedItem) throws ImpExException
	{
		final String[] cellValues = cellValue.split("\\*");
		this.currentCellValue = cellValues[cellValues.length - 1];
		this.qualfier = cellValues[0];
		this.qualfier = qualfier.substring("@".length()).trim();

		setUpClassAttr(cellValue);
		setUpClassAttrAssignment(cellValue);
		this.allDoneFor = null;

	}

	@Override
	public void notifyTranslationEnd(final ValueLine line, final HeaderDescriptor header, final Item processedItem)
			throws ImpExException
	{
		importFeatures(line, (Product) processedItem);
	}

	@Override
	public Collection<UnitAwareValue> translateCurrentUnitAwareValues(final ValueLine line,
			final ClassAttributeAssignment assignment, @SuppressWarnings("deprecation") final Product processedItem) throws HeaderValidationException
	{
		if (assignment == null)
		{
			return CollectionUtils.emptyCollection();
		}

		final AbstractValueTranslator trans = getSingleCellValueTranslator(assignment);
		// skip classification attribute if cell is marked as IGNORE
		final Collection<UnitAwareValue> values = new LinkedList<>();
		if (!currentCellValue.isEmpty())
		{
			for (final Iterator<?> iter = splitValues(assignment, currentCellValue).iterator(); iter.hasNext();)
			{
				final String singleStr = (String) iter.next();
				translateEachValues(singleStr, values, trans, processedItem, line);
			}
		}
		return values;
	}

	protected void translateEachValues(final String singleStr, final Collection<UnitAwareValue> values,
			final AbstractValueTranslator trans, @SuppressWarnings("deprecation") final Product processedItem, final ValueLine line)
	{

		if (singleStr != null && singleStr.length() > 0)
		{
			Object transValue;
			try
			{
				transValue = trans.importValue(CSVUtils.unescapeString(singleStr, TO_ESCAPE, true), processedItem);
				final ClassificationAttributeUnit classificationAttributeUnit = null;
				values.add(new UnitAwareValue(transValue, classificationAttributeUnit));

			}
			catch (final JaloInvalidParameterException e)//NOSONAR
			{
				if (Config.getBoolean(IMPEX_NONEXISTEND_CLSATTRVALUE_FALLBACK_KEY, false))
				{
					transValue = getFallbackValueTranslator().importValue(CSVUtils.unescapeString(singleStr, TO_ESCAPE, true),
							processedItem);
					values.add(new UnitAwareValue(transValue, null));
				}
				else
				{
					line.getValueEntry(this.columnDescriptor.getValuePosition()).markUnresolved(e.getMessage());
				}
			}
		}
	}

	@Override
	protected ClassAttributeAssignment matchAssignment(final Collection<ClassificationClass> classes)
	{
		for (final ClassificationClass myClass : classes)
		{
			ClassAttributeAssignment match = null;
			// if not cached calculate match
			try
			{
				if (this.classAttr != null)
				{
					match = myClass.getAttributeAssignment(this.classAttr);
				}
			}
			catch (final JaloItemNotFoundException e)//NOSONAR
			{
				// fine here
			}

			// now test match
			if (match != null)
			{
				return match;
			}
		}
		return null;
	}

	protected void basicInit(final SpecialColumnDescriptor columnDescriptor)
	{
		this.setColumnDescriptor(columnDescriptor);
		this.systemName = columnDescriptor.getDescriptorData().getModifier("system");
		this.versionName = columnDescriptor.getDescriptorData().getModifier("version");
		this.className = columnDescriptor.getDescriptorData().getModifier("class");
	}

	protected void i18nInit(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		if (columnDescriptor.getDescriptorData().getModifier("lang") != null)
		{
			this.lang = StandardColumnDescriptor.findLanguage(columnDescriptor.getHeader(),
					columnDescriptor.getDescriptorData().getModifier("lang"));
		}
		this.locale = columnDescriptor.getHeader().getReader().getLocale();
		if (lang != null)
		{
			this.locale = new Locale(this.lang.toString());
		}
	}

	protected void setUpClassAttrAssignment(final String cellValue) throws HeaderValidationException
	{
		if (StringUtils.isNotBlank(className))
		{
			ClassificationClass classificationClass = null;
			try
			{
				classificationClass = this.classSystemVersion.getClassificationClass(className);
			}
			catch (final JaloItemNotFoundException e)//NOSONAR
			{
				throw new HeaderValidationException(columnDescriptor.getHeader(),
						"unknown classification class " + className + " within system version '" + systemName + "." + versionName + "' "
								+ "in column " + columnDescriptor.getValuePosition() + ":" + columnDescriptor.getQualifier(),
						HeaderValidationException.UNKNOWN);
			}
			try
			{
				this.classAttrAssignment = classificationClass.getAttributeAssignment(this.classAttr);
			}
			catch (final JaloItemNotFoundException e)//NOSONAR
			{
				throw new HeaderValidationException(columnDescriptor.getHeader(),
						"unknown attribute assignment " + className + "." + cellValue + " within system version '" + systemName + "."
								+ versionName + "' " + "in column " + columnDescriptor.getValuePosition() + ":"
								+ columnDescriptor.getQualifier(),
						HeaderValidationException.UNKNOWN);
			}
		}
	}

	protected void setUpClassAttr(final String cellValue) throws HeaderValidationException
	{
		final ClassificationSystem sys = CatalogManager.getInstance().getClassificationSystem(systemName);
		if (sys == null)
		{
			throw new HeaderValidationException(
					columnDescriptor.getHeader(), "unknown classification system '" + systemName + "' in column "
							+ columnDescriptor.getValuePosition() + ":" + columnDescriptor.getQualifier(),
					HeaderValidationException.UNKNOWN);
		}
		this.classSystemVersion = (ClassificationSystemVersion) sys.getCatalogVersion(versionName);
		if (this.classSystemVersion == null)
		{
			throw new HeaderValidationException(columnDescriptor.getHeader(),
					"unknown classification system version '" + systemName + "." + versionName + "' in column "
							+ columnDescriptor.getValuePosition() + ":" + columnDescriptor.getQualifier(),
					HeaderValidationException.UNKNOWN);
		}
		try
		{
			this.classAttr = this.classSystemVersion.getClassificationAttribute(qualfier);
		}
		catch (final JaloItemNotFoundException e)//NOSONAR
		{
			throw new HeaderValidationException(columnDescriptor.getHeader(),
					"unknown classification attribute " + cellValue + " within system version '" + systemName + "." + versionName
							+ "' " + "in column " + columnDescriptor.getValuePosition() + ":" + columnDescriptor.getQualifier(),
					HeaderValidationException.UNKNOWN);
		}
	}

	protected void importFeatures(final ValueLine line, final Product product) throws ImpExException
	{
		final Map<ClassificationAttributeTranslator, ClassAttributeAssignment> assignmentsFromTranslators = collectAssignmentsFromTranslators(
				product);

		if (MapUtils.isNotEmpty(assignmentsFromTranslators))
		{
			extractAndStoreValues(line, product, assignmentsFromTranslators);
		}
	}

	protected Map<ClassificationAttributeTranslator, ClassAttributeAssignment> collectAssignmentsFromTranslators(
			final Product product)
	{

		Map<ClassificationAttributeTranslator, ClassAttributeAssignment> ret = null; // lazy
		List<ClassificationClass> productClasses = null; // lazy
		// get fixed assignment (if defined)
		ClassAttributeAssignment assignment = this.getAssignment();
		if (assignment == null)
		{
			// load all assigned classes - only once !!!
			productClasses = CatalogManager.getInstance().getClassificationClasses(product);
			// lookup assignment dynamically
			assignment = this.matchAssignment(productClasses);
		}

		if (assignment != null)
		{
			ret = new LinkedHashMap<ClassificationAttributeTranslator, ClassAttributeAssignment>(2);
			// register translator and assignment
			ret.put(this, assignment);
		}
		return ret != null ? ret : Collections.emptyMap();
	}

	protected void extractAndStoreValues(final ValueLine line, final Product product,
			final Map<ClassificationAttributeTranslator, ClassAttributeAssignment> assignmentsFromTranslators) throws ImpExException
	{
		final List<ClassAttributeAssignment> assignments = new ArrayList<ClassAttributeAssignment>(
				assignmentsFromTranslators.values());

		// changed from create to load because of localized values loss (see PLA-10283)
		final FeatureContainer cont = FeatureContainer.loadTyped(product, assignments);

		// process all single cell values
		for (final Map.Entry<ClassificationAttributeTranslator, ClassAttributeAssignment> e : assignmentsFromTranslators.entrySet())
		{
			extractAndStoreValue(line, product, cont, e.getValue());
		}
		try
		{
			cont.store();
		}
		catch (final ConsistencyCheckException e1)//NOSONAR
		{
			throw new ImpExException("error writing classification features " + cont + " : " + e1.getMessage());
		}
		finally
		{
			// mark all translators as done - since _each one_ is being notified !!!
			markTranslatorsAsDone(product);
		}
	}

	protected void markTranslatorsAsDone(final Product product)
	{
		final PK myPK = product.getPK();
		this.allDoneFor = myPK;
		this.currentCellValue = null;
	}

	protected void extractAndStoreValue(final ValueLine line, final Product product, final FeatureContainer cont,
			final ClassAttributeAssignment assignment) throws HeaderValidationException
	{
		final Collection<UnitAwareValue> actualValues = this.translateCurrentUnitAwareValues(line, assignment, product);
		final boolean localized = assignment.isLocalizedAsPrimitive();
		final SessionContext ctx = this.getValueCtx(localized);
		final TypedFeature feature = cont.getFeature(assignment);
		if (actualValues == null)
		{
			feature.clear(ctx);
		}
		else if (!actualValues.isEmpty())
		{
			feature.clear(ctx);
			for (final UnitAwareValue value : actualValues)
			{
				final FeatureValue featureValue = feature.createValue(ctx, -1, value.getValue(), false);
				if (value.hasUnit())
				{
					featureValue.setUnit(value.getUnit());
				}
			}
		}
	}

	public SpecialColumnDescriptor getColumnDescriptor()
	{
		return columnDescriptor;
	}

	public void setColumnDescriptor(final SpecialColumnDescriptor columnDescriptor)
	{
		this.columnDescriptor = columnDescriptor;
	}
}
