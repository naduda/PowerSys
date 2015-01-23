function onDoubleClick(sh) 
{
	sh.setTS(sh.getIdSignal(), [id] == 0 ? 1 : 0);
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('1/2/0/0');
	
	sh.rotate(n, [id] == 1 ? -30 : 0); 
}