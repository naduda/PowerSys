package pr;

import pr.svgObjects.SVG;

public class Test {

	public static void main(String[] args) {
		String filePath = "d:/GIT/PowerSys/psARM/schemes/ÏÑ-110 êÂ 'Áëîê-4'";
		SVGModel svgModel = SVGModel.getInstance();
		SVG svg = svgModel.getSVG(filePath + ".svg");
		svg.setTitle("ttt");
		svgModel.setObject(filePath + "_2.svg", svg);
		System.out.println(filePath + "_2.svg");
	}

}
