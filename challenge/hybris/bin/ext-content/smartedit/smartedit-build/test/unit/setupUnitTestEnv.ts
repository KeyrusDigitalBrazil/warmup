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
// necessary for registration of decorators and hence required by downstream teams that flag smarteditcommons as external
// without smarteditcommons flagged as external, import of functions, like decorators, in downstream extensions fail
import * as angular from 'angular';

beforeEach((angular as any).mock.module(function($provide: any) {
	$provide.value('$log', console);
}));
