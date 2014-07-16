package owuor.f8th.types;

import java.util.ArrayList;
import java.util.List;

public class EListCategory {

	public String title;
	public final List<String> children = new ArrayList<String>();

	public EListCategory(String title) {
		this.title = title;
	}

}