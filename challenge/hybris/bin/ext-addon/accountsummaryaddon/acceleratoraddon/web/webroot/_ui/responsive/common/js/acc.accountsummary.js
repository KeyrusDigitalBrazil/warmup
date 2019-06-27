ACC.accountsummary = {

	_autoload: [
	    "backAccountSummaryUnit",
	    "filterByCriteria"
	],

	defaultFilterByKey : "documentNumber",
	filterByKey : $('#filterByKey').data("filterByKey"),
	startRange : $('#rangeCriteria').data("startRange"),
	endRange : $('#rangeCriteria').data("endRange"),
	documentTypeCode : $('#documentTypeCriteria').data("documentTypeCode"),
	filterByValue : $('#singleValueCriteria').data("filterByValue"),

	backAccountSummaryUnit: function(){
		$(".accountSummaryUnitBackBtn, .accountSummaryUnitTopBackBtn").on("click", function(){
			var sUrl = $(this).data("backToAccountSummary");
			window.location = sUrl;
		});
	},
	
	filterByCriteria: function(){
		$(document).on("change", "select#filterByKey", function(){
			ACC.accountsummary.defaultFilterByKey = $(this).find("option:selected").attr('id');
			ACC.accountsummary.addRemoveDatePickerClass();
			// clean all the field values
			ACC.accountsummary.cleanFilterKeyFields();
			ACC.accountsummary.showHideFilterKey();
			if (ACC.accountsummary.filterByKey === ACC.accountsummary.defaultFilterByKey) {
				if (ACC.accountsummary.filterByKey === "documentType") {
					$("#typeCriteria").val(ACC.accountsummary.documentTypeCode);
				}
				else if (ACC.accountsummary.defaultFilterByKey.match(/range/i)) {
					$("#startRange").val(ACC.accountsummary.startRange);
					$("#endRange").val(ACC.accountsummary.endRange);
				}
				else {
					$("#filterByValue").val(ACC.accountsummary.filterByValue);
				}
			}
			else {
				ACC.accountsummary.cleanFilterKeyFields();
			}
		});
	},
	
	
	
	addRemoveDatePickerClass: function() {
		if (ACC.accountsummary.defaultFilterByKey.match(/date/i)) {
			var dateForDatePicker = $('#rangeCriteria').data("dateForDatePicker");
			$("i").addClass("glyphicon glyphicon-calendar js-open-datepicker");
			
			$(document).on("click",'#startRangeCriteria .js-open-datepicker', function (){
				$('#startRangeCriteria .hasDatepicker').datepicker('show');
			});
			
			$(document).on("click",'#endRangeCriteria .js-open-datepicker', function (){
				$('#endRangeCriteria .hasDatepicker').datepicker('show');
			});

			$("#startRange, #endRange").datepicker({dateFormat: dateForDatePicker});
		}
		else {
			$("i").removeClass("glyphicon glyphicon-calendar js-open-datepicker");
			$("#startRange, #endRange").datepicker("destroy");
		}
	},
	
	showHideFilterKey: function() {
		// hide all fields
		$('.criterias').hide();
		
		if (ACC.accountsummary.defaultFilterByKey === "documentType") {
			$('.documentTypeCriteria').show();
		}
		else if (ACC.accountsummary.defaultFilterByKey.match(/range/i)) {
			$('.rangeCriteria').show();
		}
		else {
			$('.singleValueCriteria').show();
		}
	},
	
	cleanFilterKeyFields: function(){
		$('.filterCriteria').val('');
	}
};

$(document).ready(function (){
	ACC.accountsummary.defaultFilterByKey = $("#filterByKey").find("option:selected").attr('id');
	if (ACC.accountsummary.defaultFilterByKey !== undefined) {
		ACC.accountsummary.showHideFilterKey();
		ACC.accountsummary.addRemoveDatePickerClass();
	}
});
