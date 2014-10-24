function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	//sh.getAllNodes(sh, '');
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0'); 
	// if (sh.getStatus() == 1) 
	// {
	// 	n.setFill(sh.getColorByName([idTS] == [ON] ? 'red' : 'Lime'));
	// }
	// else
	// {
	// 	n.setFill(sh.getColorByName('yellow', [idTS] == [ON] ? 'red' : 'Lime'));	
	// }
	sh.setShapeFill(n, 'red', 'Lime');
}

function onSignalUpdate(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'Lime');
}