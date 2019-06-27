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

export interface IChangesObject<T> {
	currentValue: T;
	previousValue: T;
	isFirstChange(): boolean;
}

export interface IOnChangesObject {
	[property: string]: IChangesObject<any>;
}

export interface ISeComponent {

    /**
     * Called on each controller after all the controllers on an element have been constructed and had their bindings
     * initialized (and before the pre & post linking functions for the directives on this element). This is a good
     * place to put initialization code for your controller.
     */
	$onInit?(): void;
    /**
     * Called on each turn of the digest cycle. Provides an opportunity to detect and act on changes.
     * Any actions that you wish to take in response to the changes that you detect must be invoked from this hook;
     * implementing this has no effect on when `$onChanges` is called. For example, this hook could be useful if you wish
     * to perform a deep equality check, or to check a `Date object, changes to which would not be detected by Angular's
     * change detector and thus not trigger `$onChanges`. This hook is invoked with no arguments; if detecting changes,
     * you must store the previous value(s) for comparison to the current values.
     */
	$doCheck?(): void;
    /**
     * Called whenever one-way bindings are updated. The onChangesObj is a hash whose keys are the names of the bound
     * properties that have changed, and the values are an {@link IChangesObject} object  of the form
     * { currentValue, previousValue, isFirstChange() }. Use this hook to trigger updates within a component such as
     * cloning the bound value to prevent accidental mutation of the outer value.
     */
	$onChanges?(onChangesObj: IOnChangesObject): void;
    /**
     * Called on a controller when its containing scope is destroyed. Use this hook for releasing external resources,
     * watches and event handlers.
     */
	$onDestroy?(): void;
    /**
     * Called after this controller's element and its children have been linked. Similar to the post-link function this
     * hook can be used to set up DOM event handlers and do direct DOM manipulation. Note that child elements that contain
     * templateUrl directives will not have been compiled and linked since they are waiting for their template to load
     * asynchronously and their own compilation and linking has been suspended until that occurs.
     */
	$postLink?(): void;

	// IController implementations frequently do not implement any of its methods.
	// A string indexer indicates to TypeScript not to issue a weak type error in this case.
	[index: string]: any;

}