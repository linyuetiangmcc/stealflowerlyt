package steal.stealflowerlyt;

import steal.stealflowerlyt.controller.HandleSteal;
import steal.stealflowerlyt.controller.Worker;

/**
 * Hello world!
 *
 */
public class Main 
{
	public void test(int before,int beforeStep,int afterStep,int resetValue,int maxBefore,int maxFailcount){   
        HandleSteal handleSteal = new HandleSteal(before, beforeStep, afterStep, resetValue, maxBefore,maxFailcount);
        Worker worker= new Worker(handleSteal);
        worker.run();
    }
	
    public static void main( String[] args )
    {
    	int before = 600;
    	int beforeStep = 30;
    	int afterStep = 200;
    	int resetValue = 400;
    	int maxBefore = 1200;
    	int maxFailcount = 4;
    	if(args.length >= 5){
    		before  = Integer.parseInt(args[0]); 
    		beforeStep = Integer.parseInt(args[1]); 
    		afterStep = Integer.parseInt(args[2]);
    		resetValue = Integer.parseInt(args[3]); 
    		maxBefore = Integer.parseInt(args[4]);
    		maxFailcount = Integer.parseInt(args[5]);
    	}
    	System.out.println("before=" + before + ",beforeStep=" + beforeStep +",afterStep="
    	+ afterStep + ",resetValue=" + resetValue + ",maxBefore=" + maxBefore
    	+ ",maxFailcount=" + maxFailcount);
        Main main = new Main();
        main.test(before,beforeStep,afterStep,resetValue,maxBefore,maxFailcount);
    }
}
