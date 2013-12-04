package ab.ai;

import ab.demo.NaiveAgent;
import ab.vision.TestVision;

/**
 * This will be our entry point to our AIBirds.
 * Currently supports these kind of arguments:
 * none (Will just run as if inputed -ai)
 * -ai [1-21](level select: optional) -showSeg(debug window: optional)
 *
 */
public class AIEntry {
	//Currently is just using NaiveAgent as a placeholder
	public static void main(String[] args) {
		switch(args.length){
		case 0:
			{
				StructureAgent sa = new StructureAgent();
				sa.run();
			}
			break;
		case 1:
			if(args[0].equals("-ai")){
				StructureAgent sa = new StructureAgent();
				sa.run();
			}else{
				System.out.println("Unrecognized Parameters");
			}
			break;
		case 2:
			if(args[0].equals("-ai") && args[1].equals("-showSeg")){
				StructureAgent sa = new StructureAgent();
				Thread sathre = new Thread(sa);
				sathre.start();
				Thread thre = new Thread(new TestVision());
				thre.start();
			}else if(args[0].equals("-ai")){
				int level = 1;
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.out.println("Unrecognized Command, Best Fit Was:\nClient.jar -ai [1-21]");
					return;
				}
				NaiveAgent na = new NaiveAgent();
				na.currentLevel = level;
				na.run();
			}else{
				System.out.println("Unrecognized Parameters");
			}
			break;
		case 3:
			if(args[0].equals("-ai") && args[2].equals("-showSeg")){
				NaiveAgent na = new NaiveAgent();
				int level = 1;
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.out.println("Unrecognized Command, Best Fit Was:\nClient.jar -ai [1-21] -showSeg");
					return;
				}
				na.currentLevel = level;
				Thread nathre = new Thread(na);
				nathre.start();
				Thread thre = new Thread(new TestVision());
				thre.start();
			}else{
				System.out.println("Unrecognized Parameters");
			}
		default:
			System.out.println("Unrecognized Parameters");
		}

	}

}
