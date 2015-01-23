function onDoubleClick(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	//sh.getAllNodes(sh, '');
	//print('Id = ' + sh.getId());

	var StageLoader = Java.type("controllers.interfaces.StageLoader");
	var SingleObject = Java.type("single.SingleObject");

	var frmTU = new StageLoader("FormTU.xml", SingleObject.getResourceBundle().getString("keyFormTUTitle"), true);
	var controller = frmTU.getController();
	controller.setTuSignal(sh.gettSignalID());
	
	if (controller.isOkPressed(3)) {
		var status = controller.getTuSignal().getStatus(); //work or manual
		if (status == 1) {
			var value = controller.getValue();
			//sh.setTU(sh.getIdTS(), value);
			print(value);
		} else {
			print('not work status => ' + status);
		}
	}

}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'Lime');
}

function onSignalUpdate(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'red', 'Lime');
}