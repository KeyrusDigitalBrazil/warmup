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
package de.hybris.platform.mediaconversion.integrationtest;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static junit.framework.TestCase.fail;


/**
 * @author pohl
 */
@ManualTest
public class MediaConversionJobUpdateIntegrationTest extends AbstractMediaConversionJobIntegrationTest
{

	private void checkParent(final String prefix, final MediaModel parent, final MediaModel child)
	{
		// check linkage correct:
		Assert.assertNotNull(prefix + ": Parent not null.", parent);
		Assert.assertNotNull(prefix + ": Child not null.", child);
		Assert.assertEquals(prefix + ": Original of child set to parent.", parent, child.getOriginal());
		Assert.assertEquals(prefix + ": Original Data PK of child set to parent data pk.", parent.getDataPK(),
				child.getOriginalDataPK());
	}

	@Test
	public void testUpdateJobRun()
	{
		// run the job:
		this.cronJobService.performCronJob(generate, true);

		final MediaContainerModel conti = this.containers.get(0);
		final MediaModel normalMedia = this.mediaService.getMediaByFormat(conti, this.normal);
		this.checkParent("Normal", conti.getMaster(), normalMedia);
		final Long normalMediaOldDataPK = normalMedia.getDataPK();
		Assert.assertNotNull("Normal data pk available.", normalMediaOldDataPK);

		final MediaModel channelMedia = this.mediaService.getMediaByFormat(conti, this.channel);
		// check linkage correct:
		this.checkParent("Channel", normalMedia, channelMedia);
		final Long channelMediaOldDataPK = channelMedia.getDataPK();
		Assert.assertNotNull("Channel data pk available.", channelMediaOldDataPK);

		// now update the master:
		{
			final MediaModel master = conti.getMaster();
			Assert.assertNotNull("Master present.", master);
			InputStream inputStream = null;
			try
			{
				inputStream = TestMedia.KING.openStream();
				this.mediaService.setStreamForMedia(master, inputStream);
			}
			catch (final Exception e)
			{
				fail(e.getMessage());
			}
			finally
			{
				IOUtils.closeQuietly(inputStream);
			}
			this.modelService.save(master);
			this.modelService.refresh(master);
			this.modelService.refresh(conti);
		}
		// verify that the data pk of the master has changed
		Assert.assertEquals("Original still the same.", conti.getMaster(), normalMedia.getOriginal());
		Assert.assertFalse("Original Data PK are different now.",
				conti.getMaster().getDataPK().equals(normalMedia.getOriginalDataPK()));
				// not checking the channel media as there are no changes yet...

		// run the job:
		this.cronJobService.performCronJob(generate, true);
		this.modelService.refresh(normalMedia);
		this.modelService.refresh(channelMedia);

		this.checkParent("Normal after update", conti.getMaster(), normalMedia);
		this.checkParent("Channel after update", conti.getMaster(), normalMedia);

		// check datapk changes
		Assert.assertFalse("Normal data pk changed.", normalMediaOldDataPK.equals(normalMedia.getDataPK()));
		Assert.assertFalse("Channel data pk changed.", channelMediaOldDataPK.equals(channelMedia.getDataPK()));
	}

	@Test
	public void testUpdateOfRemovedJobRun()
	{
		// run the job:
		this.cronJobService.performCronJob(generate, true);

		final MediaContainerModel conti = this.containers.get(1);

		MediaModel normalMedia = this.mediaService.getMediaByFormat(conti, this.normal);
		this.checkParent("Normal", conti.getMaster(), normalMedia);
		final Long normalMediaOldDataPK = normalMedia.getDataPK();
		Assert.assertNotNull("Normal data pk available.", normalMediaOldDataPK);

		final MediaModel channelMedia = this.mediaService.getMediaByFormat(conti, this.channel);
		final MediaModel thumbnailMedia = this.mediaService.getMediaByFormat(conti, this.thumbnail);
		// check linkage correct:
		this.checkParent("Channel", normalMedia, channelMedia);
		this.checkParent("Thumbnail", normalMedia, thumbnailMedia);
		final Long channelMediaOldDataPK = channelMedia.getDataPK();
		final Long thumbnailMediaOldDataPK = thumbnailMedia.getDataPK();
		Assert.assertNotNull("Channel data pk available.", channelMediaOldDataPK);
		Assert.assertNotNull("Thumbnail data pk available.", thumbnailMediaOldDataPK);

		// now delete the normal media:
		this.modelService.remove(normalMedia);
		Assert.assertNotNull("Master still set.", conti.getMaster());

		// check if available:
		try
		{
			normalMedia = this.mediaService.getMediaByFormat(conti, this.normal);
			Assert.fail("Normal media must be gone but isn't: " + normalMedia);
		}
		catch (final ModelNotFoundException e)
		{
			// as expected...
		}
		this.modelService.refresh(channelMedia);
		this.modelService.refresh(thumbnailMedia);
		Assert.assertNotNull("Master still set.", conti.getMaster());

		// run the job:
		this.cronJobService.performCronJob(generate, true);
		Assert.assertNotNull("Master still set.", conti.getMaster());
		this.modelService.refresh(channelMedia);
		this.modelService.refresh(thumbnailMedia);
		Assert.assertNotNull("Master still set.", conti.getMaster());

		// check whether normal media was correctly generated:
		normalMedia = this.mediaService.getMediaByFormat(conti, this.normal);
		Assert.assertNotNull("Normal media recreated.", normalMedia);
		this.checkParent("normal recreated.", conti.getMaster(), normalMedia);

		Assert.assertFalse("Original Data PK of normal are different now.", normalMedia.getDataPK().equals(normalMediaOldDataPK));
		Assert.assertFalse("Original Data PK of channel are different now.",
				channelMedia.getDataPK().equals(channelMediaOldDataPK));
		Assert.assertFalse("Original Data PK of thumbnail are different now.",
				thumbnailMedia.getDataPK().equals(thumbnailMediaOldDataPK));

		// the sub converted medias:
		this.checkParent("Channel after update", normalMedia, channelMedia);
		this.checkParent("Thumbnail after update", normalMedia, thumbnailMedia);

	}
}
