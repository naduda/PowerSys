function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	sh.getAllNodes(sh, '');
	print('Idnd = ' + sh);
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/1');
	if ([id] == 1) {sh.setTextValue(n, ' Силовой модуль включен');}
	if ([id] == 0) {sh.setTextValue(n, ' Силовой модуль отключен');}
}

function onSignalUpdate(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'gold');
}