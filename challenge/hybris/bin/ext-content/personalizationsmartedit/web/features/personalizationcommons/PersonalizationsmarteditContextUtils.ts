import {SeInjectable} from 'smarteditcommons';
import {Personalization} from "personalizationcommons/dtos/Personalization";
import {Customize} from "personalizationcommons/dtos/Customize";
import {CombinedView} from "personalizationcommons/dtos/CombinedView";
import {SeData} from "personalizationcommons/dtos/SeData";

import * as angular from 'angular';

@SeInjectable()
export class PersonalizationsmarteditContextUtils {

	getContextObject(): any {
		return {
			personalization: new Personalization(),
			customize: new Customize(),
			combinedView: new CombinedView(),
			seData: new SeData()
		};
	}

	clearCustomizeContext(contextService: any): void {
		const customize = contextService.getCustomize();
		customize.enabled = false;
		customize.selectedCustomization = null;
		customize.selectedVariations = null;
		customize.selectedComponents = null;
		contextService.setCustomize(customize);
	}

	clearCustomizeContextAndReloadPreview(previewService: any, contexService: any): void {
		const selectedVariations = angular.copy(contexService.getCustomize().selectedVariations);
		this.clearCustomizeContext(contexService);
		if (angular.isObject(selectedVariations) && !angular.isArray(selectedVariations)) {
			previewService.removePersonalizationDataFromPreview();
		}
	}

	clearCombinedViewCustomizeContext(contextService: any): void {
		const combinedView = contextService.getCombinedView();
		combinedView.customize.enabled = false;
		combinedView.customize.selectedCustomization = null;
		combinedView.customize.selectedVariations = null;
		combinedView.customize.selectedComponents = null;
		(combinedView.selectedItems || []).forEach(function(item: any) {
			delete item.highlighted;
		});
		contextService.setCombinedView(combinedView);
	}

	clearCombinedViewContext(contextService: any): void {
		const combinedView = contextService.getCombinedView();
		combinedView.enabled = false;
		combinedView.selectedItems = null;
		contextService.setCombinedView(combinedView);
	}

	clearCombinedViewContextAndReloadPreview(previewService: any, contextService: any): void {
		const cvEnabled = angular.copy(contextService.getCombinedView().enabled);
		const cvSelectedItems = angular.copy(contextService.getCombinedView().selectedItems);
		this.clearCombinedViewContext(contextService);
		if (cvEnabled && cvSelectedItems) {
			previewService.removePersonalizationDataFromPreview();
		}
	}
}
