//function onDoubleClick(sh)
function onMouseReleased(sh)
{
	//sh.setTS([idTS] == 0 ? 1 : 0);
	//sh.getAllNodes(sh, '');
	//print('B = ' + sh);
	var StageLoader = Java.type("controllers.interfaces.StageLoader");
	var SingleObject = Java.type("single.SingleObject");

	var frmTU = new StageLoader("FormTU.xml", SingleObject.getResourceBundle().getString("keyFormTUTitle"), true);
	var controller = frmTU.getController();
	controller.setTuSignal(sh.gettSignalID());
	var typeSignal = 3; //TU
	if (controller.isOkPressed(typeSignal)) {
		var status = controller.getTuSignal().getStatus(); //work or manual
		if (status == 1) {
			var value = controller.getValue();
			sh.setTU(sh.getIdSignal(), value);
		} else {
			print('status = ' + status);
		}
	}
}

function onValueChange(sh) 
{
	var n = sh.getNodeById('0/0'); 
	sh.setShapeFill(n, 'green', 'red');
}