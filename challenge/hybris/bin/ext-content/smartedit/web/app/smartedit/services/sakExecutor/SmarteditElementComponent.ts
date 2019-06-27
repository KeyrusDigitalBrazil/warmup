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
import {CrossFrameEventService, SeComponent, TypedMap} from "smarteditcommons";
import {SakExecutorService} from "./SakExecutorService";

/* @internal */
export interface ISakExecutorDirectiveScope extends angular.IScope {
	active: boolean;
	componentDecoratorEnabled: boolean;
	componentAttributes?: TypedMap<string>;
}

/* @internal */
@SeComponent({
	inputs: [
		'smarteditComponentId:@',
		'smarteditComponentType:@',
		'smarteditContainerId:@',
		'smarteditContainerType:@'
	]
})
export class SmarteditElementComponent {

	public smarteditComponentId: string;
	public smarteditComponentType: string;
	public smarteditContainerId: string;
	public smarteditContainerType: string;

	private unregisterPerspectiveChangeEvent: () => void;
	private unregisterPerspectiveRefreshedEvent: () => void;
	private compiledElement: JQLite;
	private elementScope: angular.IScope;

	constructor(
		private $element: angular.IAugmentedJQuery,
		private $scope: ISakExecutorDirectiveScope,
		private $transclude: angular.ITranscludeFunction,
		private $q: angular.IQService,
		private $rootScope: angular.IRootScopeService,
		private sakExecutorService: SakExecutorService,
		private crossFrameEventService: CrossFrameEventService,
		private EVENT_PERSPECTIVE_CHANGED: string,
		private EVENT_PERSPECTIVE_REFRESHED: string,
	) {
		this.$scope.active = false;
		this.$scope.componentDecoratorEnabled = this.sakExecutorService.isDecoratorEnabled();
	}

	$postLink() {
		this.unregisterPerspectiveChangeEvent = this.crossFrameEventService.subscribe(this.EVENT_PERSPECTIVE_CHANGED, this.replayDecorators.bind(this));
		this.unregisterPerspectiveRefreshedEvent = this.crossFrameEventService.subscribe(this.EVENT_PERSPECTIVE_REFRESHED, this.replayDecorators.bind(this));

		this.$transclude(this.$scope, this.cloneAttach.bind(this));
	}

	$onDestroy() {
		this.unregisterPerspectiveChangeEvent();
		this.unregisterPerspectiveRefreshedEvent();
	}

	private replayDecorators() {
		this.elementScope.$destroy();
		this.$element.get(0).removeChild(this.compiledElement.get(0));

		this.sakExecutorService.wrapDecorators(this.$transclude, this.smarteditComponentId, this.smarteditComponentType)
			.then((compiled) => {
				this.elementScope = this.$scope.$new(false);
				this.compiledElement = compiled(this.elementScope);
				this.$element.append(this.compiledElement);
			});

		return this.$q.when();
	}

	private cloneAttach() {
		this.sakExecutorService.wrapDecorators(this.$transclude, this.smarteditComponentId, this.smarteditComponentType).then((compiled) => {
			this.elementScope = this.$scope.$new(false);
			this.compiledElement = compiled(this.elementScope);
			this.$element.append(this.compiledElement);

			this.sakExecutorService.prepareScope(this.$scope, this.$element);

			const inactivateDecorator = () => {
				this.$scope.active = false;
			};

			const activateDecorator = () => {
				this.$scope.active = true;
			};

			// Register Event Listeners
			this.$element.on('mouseenter', () => {
				if (!this.sakExecutorService.isDecoratorEnabled()) {
					this.$rootScope.$apply(inactivateDecorator);
					return;
				}
				this.$rootScope.$apply(activateDecorator);
			});
			this.$element.on('mouseleave', () => {
				this.$rootScope.$apply(inactivateDecorator);
			});
		});
	}

}
