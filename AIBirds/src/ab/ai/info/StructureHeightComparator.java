package ab.ai.info;

import java.util.Comparator;

public class StructureHeightComparator implements Comparator<Structure> {

	@Override
	public int compare(Structure arg0, Structure arg1) {
		if (arg0.pos.getCenterY() > arg1.pos.getCenterY()) {
			return 1;
		} else if (arg0.pos.getCenterY() < arg1.pos.getCenterY()) {
			return -1;
		} else {
			return 0;
		}
	}

}
