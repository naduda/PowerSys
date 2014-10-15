function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	//sh.getAllNodes(sh, '');
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0'); 

	n.setFill(sh.getColorByName([idTS] == [ON] ? 'red' : 'Lime'));
}