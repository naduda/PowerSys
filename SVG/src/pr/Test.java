package pr;

import pr.svgObjects.SVG;

public class Test {

	public static void main(String[] args) {
		String filePath = "d:/GIT/PowerSys/psARM/schemes/ÏÑ-110 êÂ 'Áëîê-4'";
		SVGModel svgModel = SVGModel.getInstance();
		SVG svg = (SVG)svgModel.getObject(filePath + ".svg", SVG.class);
		svg.setTitle("ttt");
		svgModel.setObject(filePath + "_2.svg", svg);
	}

}
