function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	sh.getAllNodes(sh, '');
	print('Idnd2 = ' + sh);
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/1');

	if ([id] == 1) {sh.setTextValue(n, ' A');}
	if ([id] == 2) {sh.setTextValue(n, ' B');}
	if ([id] == 3) {sh.setTextValue(n, ' B');}
}