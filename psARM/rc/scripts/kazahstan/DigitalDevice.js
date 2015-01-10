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
	var typeSignal = 1; //TI
	if (controller.isOkPressed(typeSignal)) {
		var status = controller.getTuSignal().getStatus(); //work or manual
		if (status == 1) {
			var value = controller.getValue();
			print(value);
		} else {
			print('status = ' + status);
		}
	}
}

function onValueChange(sh)
{
	sh.setDeadZone(2);
	// print('a');
	//var n = sh.getNodeById('0/0'); 

	//n.setFill(sh.getColorByName([idTS] == 0 ? 'red' : 'GREEN'));
}