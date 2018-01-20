package steal.stealflowerlyt.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Consts;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import steal.stealflowerlyt.constant.Constant;

public class Worker {
	private HandleSteal handleSteal;
	private static Logger logger = Logger.getLogger(Worker.class);
	public Worker(HandleSteal handleSteal) {
		super();
		this.handleSteal = handleSteal;
	}

	private long var_watch = 0; //20s 
	
	public void reloadHandleStealCounter() {
		Jedis jedis = new Jedis(Constant.REDISHOST,6379);
		jedis.select(Constant.REDISINDEX);
		//TODO
		
		

	}
	
	public void run() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss SSS");
		String nowStr = "";
		String printStr = "";
		
		while(true){
			if(var_watch >= 2000){
				//reloadHandleStealCounter();
				nowStr = format.format(new Date(System.currentTimeMillis()));
				printStr = nowStr + "->before={" + handleSteal.getBefore() +
						 "},failcount={" + handleSteal.getFailCount() +
						 "},flowerSize={" + handleSteal.getPersonalPackets().size() + "}";
				logger.info(printStr);
				var_watch = 0;
			}
			else { 
				var_watch = var_watch + 1;
			}
			
			handleSteal.run();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//logger.info(e.getMessage());
				System.out.println(e.getMessage());
			}
		}
	}

}
