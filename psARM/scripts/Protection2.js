function onDoubleClick(sh) 
{
	//sh.setTS(sh.getIdSignal(), [id] == 0 ? 1 : 0);
	//print(sh.getStateId('on'));
	//print(sh.getStateId('off'));
}

function onValueChange(sh) 
{
	//sh.getAllNodes(sh, '');
	var n = sh.getNodeById('0/0');
	n.setFill(sh.getColorByName([id] == 0 ? 'ORANGE' : 'red'));
}