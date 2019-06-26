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
package de.hybris.platform.personalizationservices.task;

import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.CONTEXT_BASESITE_KEY;
import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.CONTEXT_SEGMENTS_KEY;
import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.CONTEXT_USER_KEY;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.data.UserToSegmentData;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CxRegisteredUserSessionCloseTaskRunnerTest extends ServicelayerTransactionalTest
{
	private static String USER_ID = "userId";
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String NOT_EXISTING_SEGMENT = "notExisitng";
	private static final String BASE_SITE_ID = "testBaseSite";
	private static final String BASE_SITE1_ID = "testBaseSite1";

	@Resource
	private CxRegisteredUserSessionCloseTaskRunner cxRegisteredUserSessionCloseTaskRunner;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private TaskService taskService;
	@Resource
	private BaseSiteService baseSiteService;

	private UserModel user;

	@Before
	public void setup() throws Exception
	{
		createBaseSite(BASE_SITE_ID);
		createUser();
		createSegment(SEGMENT1);
		createSegment(SEGMENT2);
	}

	@Test
	public void testSaveUserSegments() throws Exception
	{
		//given
		final Collection<UserToSegmentData> expected = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT2);
		final Map<String, Object> contextMap = createContext(user.getUid(), expected, BASE_SITE_ID);

		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(contextMap);
		cxRegisteredUserSessionCloseTaskRunner.run(taskService, task);

		//then
		verifySegments(expected, user.getUserToSegments(), BASE_SITE_ID);
	}

	@Test
	public void testSaveUserSegmentsForTwoBaseSites() throws Exception
	{
		//given
		final Collection<UserToSegmentData> expected = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT2);
		final Map<String, Object> contextMapForBaseSite = createContext(user.getUid(), expected, BASE_SITE_ID);
		createBaseSite(BASE_SITE1_ID);
		final Map<String, Object> contextMapForBaseSite1 = createContext(user.getUid(), expected, BASE_SITE1_ID);

		//when
		TaskModel task = modelService.create(TaskModel.class);
		task.setContext(contextMapForBaseSite);
		cxRegisteredUserSessionCloseTaskRunner.run(taskService, task);

		task = modelService.create(TaskModel.class);
		task.setContext(contextMapForBaseSite1);
		cxRegisteredUserSessionCloseTaskRunner.run(taskService, task);

		//then
		final Collection<CxUserToSegmentModel> userSegments = user.getUserToSegments();
		Assert.assertEquals(4, userSegments.size());

		Collection<CxUserToSegmentModel> userSegmentsForBaseSite = userSegments.stream()//
				.filter(us -> BASE_SITE_ID.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expected, userSegmentsForBaseSite, BASE_SITE_ID);

		userSegmentsForBaseSite = userSegments.stream()//
				.filter(us -> BASE_SITE1_ID.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expected, userSegmentsForBaseSite, BASE_SITE1_ID);
	}

	@Test
	public void testSaveUserSegmentsWithNotExistingSegment() throws Exception
	{
		//given
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, BigDecimal.ONE, NOT_EXISTING_SEGMENT);
		final Map<String, Object> contextMap = createContext(user.getUid(), userSegments, BASE_SITE_ID);

		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(contextMap);
		cxRegisteredUserSessionCloseTaskRunner.run(taskService, task);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
	}


	protected Map<String, Object> createContext(final String userId, final Collection<UserToSegmentData> userSegments,
			final String baseSiteUid)
	{
		final Map<String, Object> contextMap = new HashMap<>();
		contextMap.put(CONTEXT_USER_KEY, user.getUid());
		contextMap.put(CONTEXT_SEGMENTS_KEY, userSegments);
		contextMap.put(CONTEXT_BASESITE_KEY, baseSiteUid);

		return contextMap;
	}

	protected BaseSiteModel createBaseSite(final String baseSiteId)
	{
		final BaseSiteModel baseSite = modelService.create(BaseSiteModel.class);
		baseSite.setUid(baseSiteId);
		modelService.save(baseSite);
		return baseSite;
	}

	protected void createUser()
	{
		user = modelService.create(UserModel.class);
		user.setUid(USER_ID);
		modelService.save(user);
	}

	protected void createSegment(final String code)
	{
		final CxSegmentModel segment = modelService.create(CxSegmentModel.class);
		segment.setCode(code);
		modelService.save(segment);
	}

	protected Collection<UserToSegmentData> createUserSegments(final UserModel user, final BigDecimal affinity,
			final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegment(affinity, s)).collect(Collectors.toList());
	}

	protected UserToSegmentData createUserSegment(final BigDecimal affinity, final String segmentCode)
	{
		final UserToSegmentData data = new UserToSegmentData();
		data.setAffinity(affinity);
		data.setCode(segmentCode);
		return data;
	}

	protected void verifySegments(final Collection<UserToSegmentData> expected, final Collection<CxUserToSegmentModel> current,
			final String baseSiteId)
	{
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedDataMap = expected.stream().collect(//
				Collectors.toMap(//
						UserToSegmentData::getCode, //
						UserToSegmentData::getAffinity));

		Assert.assertTrue(current.stream().allMatch(us -> baseSiteId.equals(us.getBaseSite().getUid())//
				&& expectedDataMap.containsKey(us.getSegment().getCode())//
				&& expectedDataMap.get(us.getSegment().getCode()).equals(us.getAffinity())));
	}
}
