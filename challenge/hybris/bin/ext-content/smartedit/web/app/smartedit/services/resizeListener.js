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
angular.module("resizeListenerModule", ['yjqueryModule'])
    /*
     * interval at which manual resizeListener will try to get a reference on resize containers and clone them for clean unregistering
     */
    .constant("RESIZE_LISTENER_REPROCESS_TIMEOUT", 500)
    /*
     * Service that wraps element-resize-detector from git://github.com/wnr/element-resize-detector.git#v1.1.12
     */
    .service("resizeListener", function($window, $document, $interval, yjQuery, RESIZE_LISTENER_REPROCESS_TIMEOUT) {

        var resizeListenersRegistry = [];

        // default strategy is object. using 'scroll' strategy that is more performant.
        // see https://github.com/wnr/element-resize-detector
        var erd = $window.elementResizeDetectorMaker({
            strategy: "scroll"
        });

        var internalStatePropertyName = "_erd";
        /*
         * unregisters the resize listener of a given node
         */
        this.unregister = function(element) {

            /*
             * at this stage, a DOM manipulation driven by the storefront may have removed
             * the erd_scroll_detection_container added by erd library at register time, which will cause uninstall to fail
             * we then re-add it before uninstalling
             * 
             * Note: 
             *  It is necessary to check whether the document contains the container retrieved. However, 
             *  IE11 does not support the method contains for nodes, only for elements. Thus, it would throw an exception. 
             *  It is necessary to use yjQuery to be consistent with all browsers.
             */
            this.fix(element);

            var entry = resizeListenersRegistry.find(function(entry) {
                return entry.element === element;
            });
            if (entry) {
                erd.uninstall(element);

                resizeListenersRegistry.splice(resizeListenersRegistry.indexOf(entry), 1);
            }
        }.bind(this);

        this.fix = function(element) {

            /*
             * at this stage, a DOM manipulation driven by the storefront may have removed
             * the erd_scroll_detection_container added by erd library at register time, which will cause uninstall to fail
             * we then re-add it before uninstalling
             * 
             * Note: 
             *  It is necessary to check whether the document contains the container retrieved. However, 
             *  IE11 does not support the method contains for nodes, only for elements. Thus, it would throw an exception. 
             *  It is necessary to use yjQuery to be consistent with all browsers.
             */
            var entry = resizeListenersRegistry.find(function(entry) {
                return entry.element === element;
            });
            if (entry) {
                var container = element[internalStatePropertyName] ? element[internalStatePropertyName].container : undefined;
                if (container && !yjQuery.contains($document[0], container)) {
                    /*
                     *  in IE11, the container returns with no children, we then need to restore its children before reappending it to the element
                     */
                    if (!container.hasChildNodes() && entry.containerClone && entry.containerClone.hasChildNodes()) {
                        Array.prototype.slice.call(entry.containerClone.childNodes).forEach(function(child) {
                            container.appendChild(child);
                        });
                    }

                    element.appendChild(container);
                }
            }
        }.bind(this);


        /*
         * registers a resize listener of a given node, at this stage the containers are not created yet by the underlying library
         */
        this.register = function(element, listener) {
            var index = resizeListenersRegistry.findIndex(function(obj) {
                return element === obj.element;
            });
            if (index === -1) {
                erd.listenTo(element, listener);
                resizeListenersRegistry.push({
                    element: element,
                    containerClone: null
                });
            }
        };

        this.init = function() {
            this._missingClonesListener = $interval(function() {
                resizeListenersRegistry.forEach(function(entry) {
                    var container = entry.element[internalStatePropertyName].container;
                    if (container && !entry.containerClone) {
                        entry.containerClone = container.cloneNode(true);
                    }
                });
            }, RESIZE_LISTENER_REPROCESS_TIMEOUT);
        };
        /*
         * unregisters listeners on all nodes and cleans up
         */
        this.dispose = function() {
            $interval.cancel(this._missingClonesListener);
            var registryCopy = resizeListenersRegistry.slice();
            registryCopy.forEach(function(entry) {
                this.unregister(entry.element);
            }.bind(this));
        };

        /*
         * for e2e test purposes
         */
        this._listenerCount = function() {
            return resizeListenersRegistry.length;
        };
    });
