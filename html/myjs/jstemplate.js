function fillOptions(ctrlId,effCond) {
	var values = charspecvalues[ctrlId];
	if (values != null) {
		var optionhtml = '';
		for ( var item in values) {
			if ((effCond!=null&&values[item].effCond==effCond)||effCond==null){
				optionhtml += '<option value="' + item + '"';
				if (values[item].selected == 'yes') {
					optionhtml += ' selected';
				}
				optionhtml += '>' + values[item].value + '</option>';
			}
			
		}
	}
	var ctrl = $("#" + ctrlId);
	ctrl.html(optionhtml);
	ctrl.selectmenu();
	ctrl.selectmenu('refresh', true);
}

Array.prototype.contains = function(item){
    return RegExp('\\b'+item+'\\b').test(this);
};
function selectOptions(ctrlId,realValues,effCond) {
	if (realValues==null){
		return;
	}
	var values = charspecvalues[ctrlId];
	if (values != null) {
		var realValueArr = [];
		realValueArr = realValues.split(',');
		
		var optionhtml = '';
		for ( var item in values) {
			if ((effCond!=null&&values[item].effCond==effCond)||effCond==null){
				optionhtml += '<option value="' + item + '"';
				if (realValueArr.contains(item)) {
					optionhtml += ' selected';
				}
				optionhtml += '>' + values[item].value + '</option>';
			}
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
	realValueArr = (''+realValue).split(',');
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

function getEnumValues(charCode){
	var values = charspecvalues[charCode];
	return values;
}

