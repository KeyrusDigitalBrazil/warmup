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
package de.hybris.platform.b2b.mail;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import java.io.Writer;


/**
 * An interface for rendering {@link RendererTemplateModel}'s.
 * 
 * @deprecated Since 4.4. User {@link de.hybris.platform.commons.renderer.Renderer} and its vlocity impl
 *             {@link de.hybris.platform.commons.renderer.impl.VelocityTemplateRenderer}
 */
@Deprecated
public interface TemplateRenderer
{

	/**
	 * Render templates via velocity by default can be overwritten to use any other templating engine.
	 * 
	 * @param template
	 *           the Renderer template define in hybris
	 * @param context
	 *           A POJO holding data to populate the template.
	 * @param writer
	 *           where to write the result of rendering.
	 */
	public abstract void render(RendererTemplateModel template, Object context, Writer writer);
}
