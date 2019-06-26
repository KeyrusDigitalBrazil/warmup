/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * 
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.yacceleratorordermanagement.actions.email;

import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.springframework.util.Assert.isTrue;


/**
 * A process action which generates email with attachment.
 */
public class GenerateEmailWithAttachmentAction extends AbstractSimpleDecisionAction
{
	private static final Logger LOG = LoggerFactory.getLogger(GenerateEmailWithAttachmentAction.class);

	private CMSEmailPageService cmsEmailPageService;
	private String frontendTemplateName;
	private String returnLabelDocumentTemplateName;
	private String returnFormDocumentTemplateName;
	private ProcessContextResolutionStrategy contextResolutionStrategy;
	private EmailGenerationService emailGenerationService;
	private EmailService emailService;
	private PrintMediaService printMediaService;
	private MediaService mediaService;
	private String returnLabelAttachmentName;
	private String returnFormAttachmentName;
	private KeyGenerator guidKeyGenerator;

	@Override
	public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
	{
		Transition transition = Transition.NOK;
		getContextResolutionStrategy().initializeContext(businessProcessModel);
		final CatalogVersionModel contentCatalogVersion = getContextResolutionStrategy()
				.getContentCatalogVersion(businessProcessModel);
		if (contentCatalogVersion != null)
		{
			transition = generateEmailContent(businessProcessModel, contentCatalogVersion);
		}
		else
		{
			LOG.warn("Could not resolve the content catalog version, cannot generate email content");
		}

		return transition;
	}

	/**
	 * Checks whether or not the email content is to be generated
	 *
	 * @param businessProcessModel
	 * 		the {@link BusinessProcessModel} for which to check if an email has to be generated
	 * @param contentCatalogVersion
	 * 		the {@link CatalogVersionModel} for which to check if an email has to be generated
	 * @return transition for the business process action
	 */
	protected Transition generateEmailContent(final BusinessProcessModel businessProcessModel,
			final CatalogVersionModel contentCatalogVersion)
	{
		final EmailPageModel emailPageModel = getCmsEmailPageService()
				.getEmailPageForFrontendTemplate(getFrontendTemplateName(), contentCatalogVersion);
		Transition transition = Transition.NOK;

		if (emailPageModel != null)
		{
			final EmailMessageModel emailMessageModel = getEmailGenerationService().generate(businessProcessModel, emailPageModel);
			if (generateEmail(businessProcessModel, emailMessageModel))
			{
				transition = Transition.OK;
			}
		}
		else
		{
			LOG.warn("Could not retrieve email page model for " + getFrontendTemplateName() + " and " + contentCatalogVersion
					.getCatalog().getName() + ":" + contentCatalogVersion.getVersion() + ", cannot generate email content");
		}
		return transition;
	}

	/**
	 * Generates the email or sends warning to the console
	 *
	 * @param businessProcessModel
	 * 		the {@link BusinessProcessModel} to interact with
	 * @param emailMessageModel
	 * 		the {@link EmailMessageModel} to be generated
	 * @return true if the email has been generated. False otherwise.
	 */
	protected boolean generateEmail(final BusinessProcessModel businessProcessModel, final EmailMessageModel emailMessageModel)
	{
		if (emailMessageModel != null)
		{
			isTrue(businessProcessModel instanceof ReturnProcessModel, "Business Process is not a return process type ");

			final EmailAttachmentModel returnLabelAttachmentModel = createReturnMediaAttachment(businessProcessModel,
					ReturnRequestModel.RETURNLABEL);
			final EmailAttachmentModel returnFormAttachmentModel = createReturnMediaAttachment(businessProcessModel,
					ReturnRequestModel.RETURNFORM);

			if (returnLabelAttachmentModel != null && returnFormAttachmentModel != null)
			{
				saveReturnLabelAndReturnFormAttachments(returnLabelAttachmentModel, returnFormAttachmentModel, emailMessageModel,
						businessProcessModel);

				LOG.info("Email message generated");
				return true;
			}
			else
			{
				LOG.warn("Could not generate either Return Label and/or Return Form for the ReturnRequest");
			}
		}
		else
		{
			LOG.warn("Failed to generate email message");
		}
		return false;
	}

	/**
	 * Creates {@link EmailAttachmentModel}.
	 *
	 * @param businessProcessModel
	 * 		{@link BusinessProcessModel}, for which attachment will be created and saved
	 * @param propertyName
	 * 		property of {@link ReturnRequestModel}, for which {@link MediaModel} needs to be found
	 * @return {@link EmailAttachmentModel} with with proper media saved
	 */
	protected EmailAttachmentModel createReturnMediaAttachment(final BusinessProcessModel businessProcessModel,
			final String propertyName)
	{
		validatePropertyName(propertyName);

		final MediaModel media = getOrCreateMediaModelForProcessModel((ReturnProcessModel) businessProcessModel, propertyName);

		return castMedialModelToEmailAttachmentModel(media, getEmailAttachmentName(propertyName));
	}

	/**
	 * Saves {@EmailAttachmentModel} for given {@link BusinessProcessModel}.
	 *
	 * @param returnLabelAttachmentModel
	 * 		generated {@EmailAttachmentModel} as {@link ReturnRequestModel#RETURNLABEL}, with proper media file
	 * @param returnFormAttachmentModel
	 * 		generated {@EmailAttachmentModel} as {@link ReturnRequestModel#RETURNFORM} with proper media file
	 * @param emailMessageModel
	 * 		{@link EmailMessageModel} which will be used to send email to user
	 * @param businessProcessModel
	 * 		{@link BusinessProcessModel} for which attachments should be saved
	 */
	protected void saveReturnLabelAndReturnFormAttachments(final EmailAttachmentModel returnLabelAttachmentModel,
			final EmailAttachmentModel returnFormAttachmentModel, final EmailMessageModel emailMessageModel,
			final BusinessProcessModel businessProcessModel)
	{
		final List<EmailAttachmentModel> emailAttachmentModelList = new ArrayList<>();
		emailAttachmentModelList.add(returnLabelAttachmentModel);
		emailAttachmentModelList.add(returnFormAttachmentModel);
		emailMessageModel.setAttachments(emailAttachmentModelList);
		getModelService().save(emailMessageModel);

		final List<EmailMessageModel> emails = new ArrayList<EmailMessageModel>();
		emails.addAll(businessProcessModel.getEmails());
		emails.add(emailMessageModel);
		businessProcessModel.setEmails(emails);
		getModelService().save(businessProcessModel);
	}

	/**
	 * Retrieves {@link MediaModel} from {@link ReturnRequestModel#RETURNFORM} or {@link ReturnRequestModel#RETURNLABEL} for the given {@link ReturnProcessModel} <br>
	 * Or generates one if property is empty.
	 *
	 * @param returnProcessModel
	 * 		{@link ReturnProcessModel}
	 * @param propertyName
	 * 		Describes which {@link MediaModel} should be found. Must be either {@link ReturnRequestModel#RETURNFORM} or {@link ReturnRequestModel#RETURNLABEL}
	 * @return {@link MediaModel} property if exists, generate one otherwise
	 */
	protected MediaModel getOrCreateMediaModelForProcessModel(final ReturnProcessModel returnProcessModel,
			final String propertyName)
	{
		validateParameterNotNullStandardMessage("returnProcessModel", returnProcessModel);
		validateParameterNotNullStandardMessage("returnRequest", returnProcessModel.getReturnRequest());
		validatePropertyName(propertyName);

		final ReturnRequestModel returnRequestModel = returnProcessModel.getReturnRequest();
		MediaModel returnMedia = returnRequestModel.getProperty(propertyName);
		if (returnMedia == null)
		{
			returnMedia = getPrintMediaService()
					.getMediaForTemplate(getProperDocumentTemplateName(propertyName), returnProcessModel);
			returnRequestModel.setProperty(propertyName, returnMedia);
			getModelService().save(returnRequestModel);
		}

		LOG.debug("Found [{}] documents for return process to be sent to customers", returnMedia.getCode());
		return returnMedia;
	}

	/**
	 * Gets proper document template name depends on given parameter.
	 *
	 * @param documentTemplateName
	 * 		should be returnLabel or returnForm - static fields of {@link ReturnRequestModel} class
	 * @return returnLabelDocumentTemplateName or returnFormDocumentTemplateName field of {@link GenerateEmailWithAttachmentAction}
	 * class
	 */
	protected String getProperDocumentTemplateName(final String documentTemplateName)
	{
		validatePropertyName(documentTemplateName);

		return (ReturnRequestModel.RETURNLABEL.equals(documentTemplateName)) ?
				getReturnLabelDocumentTemplateName() :
				getReturnFormDocumentTemplateName();
	}

	/**
	 * Validates if passed parameter is either returnLabel or returnForm property.
	 *
	 * @param propertyName
	 * 		should be returnLabel or returnForm - static field of {@link ReturnRequestModel} class
	 */
	protected void validatePropertyName(final String propertyName)
	{
		isTrue(ReturnRequestModel.RETURNLABEL.equals(propertyName) || ReturnRequestModel.RETURNFORM.equals(propertyName),
				"Expected returnLabel or returnForm");
	}

	/**
	 * Creates {@link EmailAttachmentModel} with proper name and {@link MediaModel}
	 *
	 * @param mediaModel
	 * 		{@link MediaModel} which will be attached
	 * @param attachmentName
	 * 		the name for the attached {@link MediaModel}
	 * @return created {@link EmailAttachmentModel} with proper media file and name
	 */
	protected EmailAttachmentModel castMedialModelToEmailAttachmentModel(final MediaModel mediaModel, final String attachmentName)
	{
		final EmailAttachmentModel emailAttachmentModel = getEmailService()
				.createEmailAttachment(new DataInputStream(getMediaService().getStreamFromMedia(mediaModel)),
						attachmentName + "-" + getGuidKeyGenerator().generate().toString(), mediaModel.getMime());
		return emailAttachmentModel;
	}

	/**
	 * Gets proper attachment name depending on given {@link ReturnRequestModel} static field.
	 *
	 * @param propertyName
	 * 		should be one of {@link ReturnRequestModel} static field - returnLabel or returnForm
	 * @return proper name for attachment
	 */
	protected String getEmailAttachmentName(final String propertyName)
	{
		validatePropertyName(propertyName);

		return (ReturnRequestModel.RETURNLABEL.equals(propertyName)) ?
				getReturnLabelAttachmentName() :
				getReturnFormAttachmentName();
	}

	protected CMSEmailPageService getCmsEmailPageService()
	{
		return cmsEmailPageService;
	}

	@Required
	public void setCmsEmailPageService(final CMSEmailPageService cmsEmailPageService)
	{
		this.cmsEmailPageService = cmsEmailPageService;
	}

	protected String getFrontendTemplateName()
	{
		return frontendTemplateName;
	}

	@Required
	public void setFrontendTemplateName(final String frontendTemplateName)
	{
		this.frontendTemplateName = frontendTemplateName;
	}

	protected ProcessContextResolutionStrategy getContextResolutionStrategy()
	{
		return contextResolutionStrategy;
	}

	@Required
	public void setContextResolutionStrategy(final ProcessContextResolutionStrategy contextResolutionStrategy)
	{
		this.contextResolutionStrategy = contextResolutionStrategy;
	}

	protected EmailGenerationService getEmailGenerationService()
	{
		return emailGenerationService;
	}

	@Required
	public void setEmailGenerationService(final EmailGenerationService emailGenerationService)
	{
		this.emailGenerationService = emailGenerationService;
	}

	protected EmailService getEmailService()
	{
		return emailService;
	}

	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}

	protected PrintMediaService getPrintMediaService()
	{
		return printMediaService;
	}

	@Required
	public void setPrintMediaService(final PrintMediaService printMediaService)
	{
		this.printMediaService = printMediaService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected String getReturnLabelDocumentTemplateName()
	{
		return returnLabelDocumentTemplateName;
	}

	@Required
	public void setReturnLabelDocumentTemplateName(final String returnLabelDocumentTemplateName)
	{
		this.returnLabelDocumentTemplateName = returnLabelDocumentTemplateName;
	}

	protected String getReturnFormDocumentTemplateName()
	{
		return this.returnFormDocumentTemplateName;
	}

	@Required
	public void setReturnFormDocumentTemplateName(String returnFormDocumentTemplateName)
	{
		this.returnFormDocumentTemplateName = returnFormDocumentTemplateName;
	}

	protected KeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	protected String getReturnFormAttachmentName()
	{
		return returnFormAttachmentName;
	}

	@Required
	public void setReturnFormAttachmentName(final String returnFormAttachmentName)
	{
		this.returnFormAttachmentName = returnFormAttachmentName;
	}

	protected String getReturnLabelAttachmentName()
	{
		return returnLabelAttachmentName;
	}

	@Required
	public void setReturnLabelAttachmentName(final String returnLabelAttachmentName)
	{
		this.returnLabelAttachmentName = returnLabelAttachmentName;
	}
}
