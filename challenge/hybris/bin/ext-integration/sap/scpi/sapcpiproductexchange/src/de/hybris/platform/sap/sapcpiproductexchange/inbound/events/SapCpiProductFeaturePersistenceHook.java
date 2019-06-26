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
package de.hybris.platform.sap.sapcpiproductexchange.inbound.events;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationClassesResolverStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

public class SapCpiProductFeaturePersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiProductFeaturePersistenceHook.class);

	private static final String IMPEX_NONEXISTEND_CLSATTRVALUE_FALLBACK_KEY = "impex.nonexistend.clsattrvalue.fallback.enabled";

	private FormatFactory formatFactory;
	private ClassificationClassesResolverStrategy classResolverStrategy;
	private ModelService modelService;
 	private ClassificationSystemService classificationSystemService;

	private char collectionDelimiter = ImpExConstants.Syntax.DEFAULT_COLLECTION_VALUE_DELIMITER;

	private static final String QUALIFIER_SPLITTER = "/";

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if ( item instanceof ProductModel)
		{
			LOG.info("The persistence hook sapCpiProductFeaturePersistenceHook is called!");

			final ProductModel productModel = (ProductModel) item;

			final List<ProductFeatureModel> productFeatures = productModel.getFeatures();

			setProductFeatureProduct(productFeatures, productModel);

			final Set<ClassificationClassModel> classificationClasses = getClassificationClassModel(productModel);

			if ( !isValidProductFeatures(productFeatures) || classificationClasses.isEmpty())
			{
				cleanProductFeatureProduct(productFeatures);
				return Optional.of(item);
			}

			final Set<PK> singleAttributeValue = new HashSet();

			final Map<PK, Integer> multipleAttributeValuePosition = new HashMap();

			final List<ProductFeatureModel> extraProductFeatures = new ArrayList();

			for (final Iterator<ProductFeatureModel> i = productFeatures.iterator(); i.hasNext();)
			{
				final ProductFeatureModel productFeature = i.next();

				final List<String> qualifierInfo = processFeatureQualifier(productFeature);

				final ClassAttributeAssignmentModel assignmentModel = retrieveClassAttributeAssignmentModel(classificationClasses,qualifierInfo);

				if( assignmentModel == null || !assignmentModel.getMultiValued() && !singleAttributeValue.add(assignmentModel.getPk()))
				{
					i.remove();
					continue;
				}
				else if(assignmentModel.getMultiValued())
				{
					multipleAttributeValuePosition.put(assignmentModel.getPk(), multipleAttributeValuePosition.getOrDefault(assignmentModel.getPk(), 0) + 1);
				}

				final  List<Object>  values = translateAwareValue(assignmentModel,(String) productFeature.getValue(),qualifierInfo.get(2));
				final ClassificationClassModel classificationClass = assignmentModel.getClassificationClass();

				productFeature.setValue(values.get(0));
				productFeature.setUnit(assignmentModel.getUnit());
				productFeature.setClassificationAttributeAssignment(assignmentModel);
				productFeature.setQualifier(buildQualifier(classificationClass,qualifierInfo));
				productFeature.setValuePosition(multipleAttributeValuePosition.get(assignmentModel.getPk()));

				//if attribute is range type, which has two numeric values.
				if(values.size() == 2)
				{
					final ProductFeatureModel newProductFeature = cloneProductFeature(productFeature);
					newProductFeature.setValue(values.get(1));
					newProductFeature.setUnit(assignmentModel.getUnit());
					//if attribute is multiple value
					multipleAttributeValuePosition.put(assignmentModel.getPk(), multipleAttributeValuePosition.get(assignmentModel.getPk()) + 1); //if attribute is multiple value
					newProductFeature.setValuePosition(multipleAttributeValuePosition.get(assignmentModel.getPk()));
					extraProductFeatures.add(newProductFeature);
				}
			}

			productFeatures.addAll(extraProductFeatures);
			return Optional.of(item);
		}

		return Optional.of(item);
	}

	private List processFeatureQualifier(final ProductFeatureModel productFeature)
	{
		return Arrays.asList(productFeature.getQualifier().split(QUALIFIER_SPLITTER));
	}

	private List<Object> translateAwareValue(final ClassAttributeAssignmentModel assignment, final Object value, final String qualifier)
	{

		final List <Object>  values =  new ArrayList();

		for (final Iterator iter = splitFeatureValues(assignment, (String)value,qualifier).iterator(); iter.hasNext();)
		{
			final String singleStr = (String) iter.next();
			if (Optional.ofNullable(singleStr).isPresent())
			{
				Object transValue = null;
				try
				{
					transValue = getSingleProductFeatureValue(assignment, singleStr,qualifier);
				}
				catch (final JaloInvalidParameterException e)
				{
					if (Config.getBoolean(IMPEX_NONEXISTEND_CLSATTRVALUE_FALLBACK_KEY, false))
					{
						LOG.debug("Fallback ENABLED");
						LOG.warn(String.format(
								"Value %s is not of type %s will use type string as fallback (%s)",value ,assignment.getAttributeType().getCode(),e.getMessage()),e);
					}
					else
					{
						LOG.debug("Fallback DISABLED. Marking line as unresolved. Will try to import value in another pass",e);
					}


				}
				values.add(transValue);
			}
		}

		return values;
	}

	private void setProductFeatureProduct(final List<ProductFeatureModel> productFeatures, final ProductModel product)
	{
		if(productFeatures != null)
		{
			productFeatures.forEach((feature) ->
			{
				feature.setProduct(product);
			});
		}
	}

	private void cleanProductFeatureProduct(final List<ProductFeatureModel> productFeatures)
	{
		if(productFeatures != null)
		{
			productFeatures.removeIf(t -> t.getPk() == null);
		}
	}

	private boolean isValidProductFeatures(final List<ProductFeatureModel> productFeatures)
	{
		return Optional.ofNullable(productFeatures).isPresent() && !productFeatures.isEmpty() && productFeatures.stream().filter(p->p.getPk() != null).collect(Collectors.toList()).isEmpty();
	}

	private Set<ClassificationClassModel> getClassificationClassModel(final ProductModel product)
	{
		return classResolverStrategy.resolve(product);
	}

	private ClassAttributeAssignmentModel retrieveClassAttributeAssignmentModel(
			final Set<ClassificationClassModel> classificationClasses,final List<String> qualifierInfo)
	{
		if(qualifierInfo == null || qualifierInfo.size()<3)
		{
			return null;
		}

		final List<ClassAttributeAssignmentModel> assignments = classResolverStrategy
				.getAllClassAttributeAssignments(classificationClasses);

		return findAssignmentWithCode(assignments, qualifierInfo);
	}

	private ClassAttributeAssignmentModel findAssignmentWithCode(final Collection<ClassAttributeAssignmentModel> assignments,
																 final List<String> qualifierInfo)
	{
		if(Optional.ofNullable(assignments).isPresent()) {
			for (final ClassAttributeAssignmentModel assignment : assignments) {
				if (assignment!= null && assignment.getClassificationAttribute().getCode().equals(qualifierInfo.get(2))
						&& assignment.getSystemVersion().getCatalog().getId().equals(qualifierInfo.get(0))) {
					return assignment;
				}
			}
		}
		return null;
	}

	private ClassificationAttributeValueModel findAttributeValue(final String code, final ClassificationSystemVersionModel classificationSystemVersion)
	{
		return classificationSystemService.getAttributeValueForCode(classificationSystemVersion,code);
	}

	private Object getSingleProductFeatureValue(final ClassAttributeAssignmentModel assignment, final Object featureValue, final String qualifier)
	{
		Object returnValue = featureValue;
		if (featureValue instanceof String)
		{
			final ClassificationAttributeTypeEnum type = assignment.getAttributeType();
			switch (type)
			{
				case BOOLEAN:
					returnValue = Boolean.valueOf( formatValue(featureValue,qualifier));
					break;
				case ENUM:
					returnValue = findAttributeValue((String) featureValue,assignment.getSystemVersion());
					validateParameterNotNull(returnValue, "No such attribute value: null");
					break;
				case NUMBER:
					returnValue = Double.valueOf(convertNumeric(featureValue,qualifier));
					break;
				case STRING:
					returnValue = formatValue(featureValue,qualifier);
					break;
				case DATE:
					final DateFormat dateFormat = formatFactory.createDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT);
					try
					{
						returnValue = dateFormat.parse(formatValue(featureValue,qualifier));
					}
					catch (final ParseException e)
					{
						throw new IllegalArgumentException("Invalid date: " + formatValue(featureValue,qualifier));
					}
					break;
				case REFERENCE:
					break;
			}
		}
		return returnValue;
	}

	private String formatValue(final Object value, final String qualifier)
	{
		if(value == null)
		{
			return null;
		}
		return ((String)value).replace(qualifier.toUpperCase()+"_", "");
	}

	private String buildQualifier(final ClassificationClassModel superCategory, final List<String> qualifierInfo)
	{
		final StringBuilder key = new StringBuilder();
		return key.append(qualifierInfo.get(0)).append(QUALIFIER_SPLITTER).append(qualifierInfo.get(1)).append(QUALIFIER_SPLITTER)
				.append(superCategory.getCode()).append(".").append(qualifierInfo.get(2).toLowerCase()).toString();
	}

	private String convertNumeric (final Object value, final String qualifier )
	{
		final String numericValue = formatValue (value,qualifier);
		final BigDecimal number = new BigDecimal(numericValue);
		final StringBuilder sb = new StringBuilder(number.stripTrailingZeros().toPlainString());
		return sb.toString();
	}

	private ProductFeatureModel cloneProductFeature(final ProductFeatureModel productFeature )
	{
		return modelService.clone(productFeature);
	}

	/**
	 * For attribute is Range type.
	 * @param assignment
	 * @param valueCollection
	 * @return
	 */
	private List <String> splitFeatureValues(final ClassAttributeAssignmentModel assignment, final Object valueCollection, final String qualifier)
	{
		if(valueCollection == null)
		{
			return Collections.emptyList();
		}

		final String[] values = ((String)valueCollection).split(String.valueOf(collectionDelimiter));
		final List<String> returnValues = new ArrayList();
		returnValues.add(values[0]);
		if (assignment.getRange() && values.length ==2)
		{
			if(new BigDecimal(formatValue(values[0],qualifier)).compareTo(new BigDecimal(values[1]))>0)
			{
				returnValues.add(convertMaxNumeric(assignment.getFormatDefinition()));
			}
			else
			{
				returnValues.add(values[1]);
			}
		}
		return returnValues;
	}

	private String convertMaxNumeric(String format)
	{
		return format == null?"9999":format.split(";")[0].replace(",", "").replace('#', '9').replace('0', '9');
	}

	protected ClassificationClassesResolverStrategy getClassResolverStrategy()
	{
		return classResolverStrategy;
	}

	@Required
	public void setClassResolverStrategy(ClassificationClassesResolverStrategy classResolverStrategy)
	{
		this.classResolverStrategy = classResolverStrategy;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

	@Required
	public void setFormatFactory(FormatFactory formatFactory)
	{
		this.formatFactory = formatFactory;
	}

	protected ClassificationSystemService getClassificationSystemService()
	{
		return classificationSystemService;
	}

	@Required
	public void setClassificationSystemService(ClassificationSystemService classificationSystemService)
	{
		this.classificationSystemService = classificationSystemService;
	}
}
