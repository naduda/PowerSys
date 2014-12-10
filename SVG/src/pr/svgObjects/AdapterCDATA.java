package pr.svgObjects;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AdapterCDATA extends XmlAdapter<String, String> {
	
    @Override
    public String marshal(String arg0) throws Exception {
        return "\n\t\t<![CDATA[\n\t\t" + arg0.trim() + "\n\t\t]]>\n\t";
    }
    
    @Override
    public String unmarshal(String arg0) throws Exception {
        return arg0;
    }

}
