function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	sh.getAllNodes(sh, '');
	print('Idnd2 = ' + sh);
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0');
	var t = sh.getNodeById('0/1');

	if ([id] == 1) {
		sh.setTextValue(t, 'Ток');
		//n.setFill(sh.getColorByName('LightGreen'));
	}
	if ([id] == 2) {
		sh.setTextValue(t, 'Напряжение');
		//n.setFill(sh.getColorByName('Yellow'));
	}
	if ([id] == 3) {
		sh.setTextValue(t, 'Потенциал');
		//n.setFill(sh.getColorByName('LightGreen'));
	}
}