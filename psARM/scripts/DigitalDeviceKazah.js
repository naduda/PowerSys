function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	sh.getAllNodes(sh, '');
	print(sh.getId());
}

function onValueChange(sh) 
{
	//sh.setDeadZone(2);
	var t = sh.getNodeById('0/1'); 

	sh.setTextValue(t, [id], '0.0');
}