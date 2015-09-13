function fillOptions(ctrlId) {
	var values = charspecvalues[ctrlId];
	if (values != null) {
		var optionhtml = '';
		for ( var item in values) {
			optionhtml += '<option value="' + item + '"';
			if (values[item].value == 'yes') {
				optionhtml += ' selected';
			}
			optionhtml += '>' + values[item].value + '</option>';
		}
	}
	var ctrl = $("#" + ctrlId);
	ctrl.html(optionhtml);
	ctrl.selectmenu();
	ctrl.selectmenu('refresh', true);
}

Array.prototype.contains = function(item){
    return RegExp(item).test(this);
};
function selectOptions(ctrlId,realValues) {
	if (realValues==null){
		return;
	}
	var values = charspecvalues[ctrlId];
	if (values != null) {
		var realValueArr = [];
		realValueArr = realValues.split(',');
		
		var optionhtml = '';
		for ( var item in values) {
			optionhtml += '<option value="' + item + '"';
			if (realValueArr.contains(item)) {
				optionhtml += ' selected';
			}
			optionhtml += '>' + values[item].value + '</option>';
		}
	}
	var ctrl = $("#" + ctrlId);
	ctrl.html(optionhtml);
	ctrl.selectmenu();
	ctrl.selectmenu('refresh', true);
}
function getDisplayEnumValue(charCode,realValue){
	if (realValue==null)
		return '';
	var realValueArr = [];
	realValueArr = realValue.split(',');
	var values = charspecvalues[charCode];
	if (values != null) {
		var displayValues = '';
		for ( var item in values) {
			if (realValueArr.contains(item)) {
				if (displayValues!=''){
					displayValues += ',';
				}
				displayValues += values[item].value;
			}
		}
		if (displayValues!=''){
			return displayValues;
		}
	}
	return realValue;
}

