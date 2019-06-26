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
package de.hybris.platform.acceleratorcms.tags2;

import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSDynamicAttributeService;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.util.HtmlElementHelper;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang.StringUtils;


public class CMSContentSlotTag extends BodyTagSupport implements DynamicAttributes
{
	// Tag Attributes
	protected ContentSlotModel contentSlotAttribute;
	protected String uidAttribute;
	private String positionAttribute;
	protected String varAttribute;
	protected Integer limitAttribute;
	protected String elementAttribute = "";
	protected Map<String, String> dynamicAttributes; // NOSONAR

	// Services
	protected HtmlElementHelper htmlElementHelper; // NOSONAR
	protected CMSPageSlotComponentService cmsPageSlotComponentService; // NOSONAR
	protected CMSPageContextService cmsPageContextService; // NOSONAR
	protected Collection<CMSDynamicAttributeService> cmsDynamicAttributeServices; // NOSONAR

	// State
	protected int currentIndex;
	protected boolean currentContentSlotFromMaster;
	protected String currentContentSlotPosition;
	protected List<AbstractCMSComponentModel> currentComponents; // NOSONAR
	protected AbstractCMSComponentModel currentComponent;
	protected ContentSlotModel currentContentSlot;
	protected CmsPageRequestContextData currentCmsPageRequestContextData;

	public CMSContentSlotTag()
	{
		super();
		resetAttributes();
		resetState();
	}

	public void setContentSlot(final ContentSlotModel contentSlot)
	{
		this.contentSlotAttribute = contentSlot;
	}

	public void setUid(final String uid)
	{
		this.uidAttribute = uid;
	}

	public void setPosition(final String position)
	{
		this.positionAttribute = position;
	}

	public void setVar(final String var)
	{
		this.varAttribute = var;
	}

	public void setLimit(final Integer limit)
	{
		this.limitAttribute = limit;
	}

	public void setElement(final String element)
	{
		this.elementAttribute = element;
	}

	@Override
	public void setDynamicAttribute(final String uri, final String localName, final Object value) throws JspException
	{
		final String attributeValue = attributeToString(value);
		if (attributeValue != null)
		{
			// Store the dynamic attributes
			dynamicAttributes.put(localName, attributeValue);
		}
	}

	protected String attributeToString(final Object value)
	{
		if (value instanceof String)
		{
			return (String) value;
		}
		if (value == null)
		{
			return null;
		}
		return value.toString();
	}

	@Override
	public void release()
	{
		super.release();
		resetState();
		resetAttributes();
	}

	protected final void resetAttributes()
	{
		contentSlotAttribute = null;
		uidAttribute = null;
		positionAttribute = null;
		varAttribute = null;
		limitAttribute = null;
		elementAttribute = "";
		dynamicAttributes = new HashMap<>();
	}

	protected final void resetState()
	{
		currentIndex = 0;
		currentContentSlotFromMaster = false;
		currentContentSlotPosition = null;
		currentComponents = null;
		currentComponent = null;
		currentContentSlot = null;
		currentCmsPageRequestContextData = null;
	}

	protected CMSPageContextService lookupCMSPageContextService()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "cmsPageContextService", CMSPageContextService.class, true);
	}

	protected CMSPageSlotComponentService lookupCMSPageSlotComponentService()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "cmsPageSlotComponentService",
				CMSPageSlotComponentService.class, true);
	}

	protected HtmlElementHelper lookupHtmlElementHelper()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "htmlElementHelper", HtmlElementHelper.class, true);
	}

	protected Collection<CMSDynamicAttributeService> lookupCMSDynamicAttributeServices()
	{
		return SpringHelper.getSpringBeansIncludingAncestors(pageContext.getRequest(), CMSDynamicAttributeService.class, true);
	}

	protected void loadServices()
	{
		if (htmlElementHelper == null)
		{
			htmlElementHelper = lookupHtmlElementHelper();
		}
		if (cmsPageSlotComponentService == null)
		{
			cmsPageSlotComponentService = lookupCMSPageSlotComponentService();
		}
		if (cmsPageContextService == null)
		{
			cmsPageContextService = lookupCMSPageContextService();
		}
		if (cmsDynamicAttributeServices == null)
		{
			cmsDynamicAttributeServices = lookupCMSDynamicAttributeServices();
		}
	}

	protected void prepare()
	{
		// Lookup the CMS Page Context for the current request
		final ServletRequest request = pageContext.getRequest();

		currentCmsPageRequestContextData = cmsPageContextService.getCmsPageRequestContextData(request);

		ContentSlotModel foundContentSlot = null;

		// Resolve the Content Slot, if needed
		if (contentSlotAttribute != null)
		{
			foundContentSlot = contentSlotAttribute;
		}
		else
		{
			if (!StringUtils.isEmpty(uidAttribute))
			{
				// Lookup by UID
				foundContentSlot = cmsPageSlotComponentService.getContentSlotForId(uidAttribute);
			}
			else if (!StringUtils.isEmpty(positionAttribute))
			{
				final ContentSlotData contentSlotForPosition = cmsPageSlotComponentService
						.getContentSlotForPosition(currentCmsPageRequestContextData, positionAttribute);
				if (contentSlotForPosition != null)
				{
					foundContentSlot = contentSlotForPosition.getContentSlot();
					foundContentSlot.setCurrentPosition(positionAttribute);

					currentContentSlotPosition = contentSlotForPosition.getPosition();
					currentContentSlotFromMaster = contentSlotForPosition.isFromMaster();
				}
			}
		}

		if (foundContentSlot != null)
		{
			final int limit = limitAttribute == null ? -1 : limitAttribute.intValue();

			currentContentSlot = foundContentSlot;
			currentComponents = cmsPageSlotComponentService.getCMSComponentsForContentSlot(currentCmsPageRequestContextData,
					foundContentSlot, true, limit);
		}

		currentIndex = 0;

		if (currentComponents != null && !currentComponents.isEmpty())
		{
			currentComponent = currentComponents.get(0);
		}

	}

	protected Map<String, String> createLiveEditAttributes()
	{
		final Map<String, String> data = new HashMap<>();

		if (currentContentSlot != null)
		{
			data.put("data-cms-content-slot", currentContentSlot.getUid());
			data.put("data-cms-content-slot-name", currentContentSlot.getName());
			data.put("data-cms-content-slot-from-master", String.valueOf(currentContentSlotFromMaster));

			if (!StringUtils.isEmpty(currentContentSlotPosition))
			{
				data.put("data-cms-content-slot-position", currentContentSlotPosition);
			}
		}

		return data;
	}

	@Override
	public int doStartTag() throws JspException
	{
		resetState();

		loadServices();
		prepare();
		if (hasSlot())
		{
			if (hasItem())
			{
				beforeAllItems();

				// Prepare for the first component
				beforeItem();

				// Ask for the body to be evaluated
				return EVAL_BODY_INCLUDE;
			}

			// There were no components
			noItems();
		}
		else
		{
			// The slot was not found
			noSlot();
		}

		return SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException
	{
		afterItem();

		increment();
		if (hasItem())
		{
			// Prepare for the next component
			beforeItem();

			// Ask for the body to be evaluated again
			return EVAL_BODY_AGAIN;
		}

		// We have finished iterating over the components
		afterAllItems();
		resetState();
		resetAttributes();
		return SKIP_BODY;
	}

	@Override
	public void setBodyContent(final BodyContent bodyContent)
	{
		this.bodyContent = bodyContent;
	}

	protected void increment()
	{
		if (currentComponents != null)
		{
			final int nextIndex = currentIndex + 1;
			if (nextIndex < currentComponents.size())
			{
				// Update the index and update the current component
				currentIndex = nextIndex;
				currentComponent = currentComponents.get(nextIndex);

				return;
			}
		}

		// No next item, set current component to null
		currentIndex = -1;
		currentComponent = null;
	}

	protected boolean hasSlot()
	{
		return currentContentSlot != null;
	}

	protected boolean hasItem()
	{
		return currentComponent != null;
	}

	protected void beforeAllItems()
	{

		writeOpenElement();
	}

	protected void afterAllItems()
	{
		cmsDynamicAttributeServices.forEach(e -> e.afterAllItems(pageContext));
		writeEndElement();
		unExposeVariables();

	}

	protected void noSlot()
	{
		// template method hook
	}

	protected void noItems()
	{
		writeOpenElement();
		writeEndElement();
	}

	protected void beforeItem()
	{
		exposeVariables();
	}

	protected void afterItem()
	{
		// template method hook
	}

	protected void exposeVariables()
	{
		pageContext.setAttribute(varAttribute, currentComponent, PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("contentSlot", currentContentSlot, PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("elementPos", Integer.valueOf(currentIndex), PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("isFirstElement", Boolean.valueOf(currentIndex == 0), PageContext.REQUEST_SCOPE);
		final int size = currentComponents.size();
		pageContext.setAttribute("isLastElement", Boolean.valueOf(currentIndex == size - 1), PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("numberOfElements", Integer.valueOf(size), PageContext.REQUEST_SCOPE);
	}

	protected void unExposeVariables()
	{
		pageContext.removeAttribute(varAttribute, PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("contentSlot", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("elementPos", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("isFirstElement", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("isLastElement", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("numberOfElements", PageContext.REQUEST_SCOPE);
	}

	protected void writeOpenElement()
	{
		pageContext.setAttribute("positionAttribute", positionAttribute, PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("page", currentCmsPageRequestContextData.getPage(), PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("catalogVersion", currentCmsPageRequestContextData.getPage().getCatalogVersion(),
				PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("isLiveEdit", Boolean.valueOf(currentCmsPageRequestContextData.isLiveEdit()),
				PageContext.REQUEST_SCOPE);
		pageContext.setAttribute("currentContentSlotPosition", currentContentSlotPosition, PageContext.REQUEST_SCOPE);


		final String elementName = getElementName();
		if (elementName != null)
		{
			// Write the open element
			htmlElementHelper.writeOpenElement(pageContext, elementName, getElementAttributes());
		}
	}

	protected Map<String, String> getElementAttributes()
	{
		Map<String, String> mergedAttributes = new HashMap<>(Collections.singletonMap("class", getElementCssClass()));

		if (dynamicAttributes != null)
		{
			mergedAttributes = htmlElementHelper.mergeAttributeMaps(mergedAttributes, dynamicAttributes);
		}
		if (currentCmsPageRequestContextData.isLiveEdit())
		{
			mergedAttributes = htmlElementHelper.mergeAttributeMaps(mergedAttributes, createLiveEditAttributes());
		}

		if (cmsDynamicAttributeServices != null)
		{
			for (final CMSDynamicAttributeService attributeService : cmsDynamicAttributeServices)
			{
				mergedAttributes = htmlElementHelper.mergeAttributeMaps(mergedAttributes,
						attributeService.getDynamicContentSlotAttributes(currentContentSlot, pageContext, mergedAttributes));
			}


		}

		return mergedAttributes;
	}

	protected void writeEndElement()
	{
		final String elementName = getElementName();
		if (elementName != null)
		{
			htmlElementHelper.writeEndElement(pageContext, elementName);
		}

		pageContext.removeAttribute("positionAttribute", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("page", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("catalogVersion", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("isLiveEdit", PageContext.REQUEST_SCOPE);
		pageContext.removeAttribute("currentContentSlotPosition", PageContext.REQUEST_SCOPE);

	}

	protected String getElementCssClass()
	{
		return "yCmsContentSlot";
	}

	protected String getElementName()
	{
		// Work out the element name to use
		if (elementAttribute != null && !elementAttribute.isEmpty())
		{
			return elementAttribute;
		}
		else if (currentCmsPageRequestContextData.isLiveEdit())
		{
			return "div";
		}
		else
		{

			/*
			 * will return the first non null computed element from the list fo attribute services
			 */
			final Optional<String> optional = cmsDynamicAttributeServices.stream()
					.filter(e -> e != null && e.getFallbackElement(currentContentSlot) != null)
					.map(e -> e.getFallbackElement(currentContentSlot))
					.findFirst();
			return optional.isPresent() ? optional.get() : null;
		}
	}
}
