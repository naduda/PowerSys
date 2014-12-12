function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	sh.getAllNodes(sh, '');
	print('Idnd = ' + sh);
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'yellow');
}

function onSignalUpdate(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'yellow');
}