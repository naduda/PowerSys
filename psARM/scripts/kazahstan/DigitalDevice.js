function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	//sh.getAllNodes(sh, '');
	//print(sh.getId());
	var StageLoader = Java.type("controllers.interfaces.StageLoader");
	var SingleObject = Java.type("single.SingleObject");

	var frmTU = new StageLoader("FormTU.xml", SingleObject.getResourceBundle().getString("keyFormTUTitle"), true);
	var controller = frmTU.getController();
	controller.setTuSignal(sh.gettSignalIDTS());
	
	if (controller.isOkPressed(1)) {
		var status = controller.getTuSignal().getStatus(); //work or manual
		if (status == 1) {
			var value = controller.getValue();
			sh.setTU(sh.getIdTS(), value);
		} else {
			print('not work status => ' + status);
		}
	}
}

function onValueChange(sh)
{
	//sh.setDeadZone(2);
	// print('a');
	//var n = sh.getNodeById('0/0'); 

	//n.setFill(sh.getColorByName([idTS] == 0 ? 'red' : 'GREEN'));
}