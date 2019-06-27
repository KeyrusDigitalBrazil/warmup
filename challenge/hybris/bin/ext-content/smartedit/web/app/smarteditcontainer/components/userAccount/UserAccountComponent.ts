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

import {ISeComponent, SeComponent} from 'smarteditcommons/services/dependencyInjection/di';
import {IframeManagerService} from 'smarteditcontainer/services';
import {CrossFrameEventService, ISessionService} from 'smarteditcommons/services';

@SeComponent({
	templateUrl: 'userAccountTemplate.html'
})
export class UserAccountComponent implements ISeComponent {
	public username: string;
	private unregUserChanged: any;

	constructor(
		private authenticationService: any,
		private iframeManagerService: IframeManagerService,
		private crossFrameEventService: CrossFrameEventService,
		private sessionService: ISessionService,
		private EVENTS: any
	) {}

	$onInit() {
		this.unregUserChanged = this.crossFrameEventService.subscribe(this.EVENTS.USER_HAS_CHANGED, this.getUsername.bind(this));
	}

	signOut() {
		this.authenticationService.logout();
		this.iframeManagerService.setCurrentLocation(null);
	}

	$onDestroy() {
		this.unregUserChanged();
	}

	getUsername() {
		this.sessionService.getCurrentUserDisplayName().then((displayName) => {
			this.username = displayName;
		});
	}
}