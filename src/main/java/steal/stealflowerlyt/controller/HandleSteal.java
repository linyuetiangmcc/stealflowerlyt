package steal.stealflowerlyt.controller;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import steal.stealflowerlyt.entity.PersonalPacket;
import steal.stealflowerlyt.net.HttpClientUtil;

public class HandleSteal {
	private String url_steal = "https://weixin.spdbccc.com.cn/wxrp-page-steal/stealFlover";
	private String charset = "utf-8";
	private Map<String, String> httpHeadMap = new HashMap<String, String>();
	private ArrayList<PersonalPacket> personalPackets = new ArrayList<PersonalPacket>();
	private HttpClientUtil httpClientUtil = null;
	private static Logger logger = Logger.getLogger(HandleSteal.class);
	
	private long before = 1220; // 提前多少ms开始发包0-1200ms
	private int failCount = 0;
	private int beforeStep = 20; // 提前步长
	private int afterStep = 15; // 调慢步长
	private int resetValue = 800; // 初始化值
	private int maxBefore = 1400; // 最大before值
	private int maxFailcount = 4;

	public ArrayList<PersonalPacket> getPersonalPackets() {
		return personalPackets;
	}

	public void setPersonalPackets(ArrayList<PersonalPacket> personalPackets) {
		this.personalPackets = personalPackets;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public long getBefore() {
		return before;
	}

	public void setBefore(long before) {
		this.before = before;
	}

	public int getBeforeStep() {
		return beforeStep;
	}

	public void setBeforeStep(int beforeStep) {
		this.beforeStep = beforeStep;
	}

	public int getAfterStep() {
		return afterStep;
	}

	public void setAfterStep(int afterStep) {
		this.afterStep = afterStep;
	}

	public int getResetValue() {
		return resetValue;
	}

	public void setResetValue(int resetValue) {
		this.resetValue = resetValue;
	}

	public int getMaxBefore() {
		return maxBefore;
	}

	public void setMaxBefore(int maxBefore) {
		this.maxBefore = maxBefore;
	}

	public HandleSteal(int before, int beforeStep, int afterStep, int resetValue, int maxBefore,int maxFailcount) {
		super();
		this.before = before;
		this.beforeStep = beforeStep;
		this.afterStep = afterStep;
		this.resetValue = resetValue;
		this.maxBefore = maxBefore;
		this.maxFailcount = maxFailcount;
		httpClientUtil = new HttpClientUtil();
		initHttpHead(); // 初始化 http 请求头
		readPostFlowers();  //读取最新花朵信息
	}
	
	public void readPostFlowers() {
		personalPackets.clear(); //清空
		//readLineFile("personredpacket_time.txt"); // 读取花信息，来源于维健结果
		readLineFile("//root//steal//getflowers//flowers_to_post.txt");
		deletePast(); // 删除过期时间的花
		deleteConflict(); // 删除可能冲突的花，消除频繁
	}

	public void run() {
		Date now = new Date();// 当前时间，用于判断花朵是否可以收获
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss SSS");
		String exeStr = "";
		String httpPostResult = "";
		String endDateStr = "";
		String content = "";
		String printStr = "";

		now.setTime(System.currentTimeMillis());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		if ((calendar.get(Calendar.HOUR_OF_DAY) % 4) == 0 && calendar.get(Calendar.MINUTE) == 20
				&& calendar.get(Calendar.SECOND) == 0) { //每4小时，20分钟，00秒执行更新
			System.out.println(now + "--reload flowers started!!");
			readPostFlowers();
			System.out.println("reload flowers ended!!");
			try{ //延迟1s，此后时间对不上，不会再执行
				Thread.sleep(1000);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (personalPackets.size() > 0) {
			// 新逻辑，只取第一个
			PersonalPacket personalPacket = personalPackets.get(0);
			if (now.getTime() < personalPacket.getEnDate().getTime()
					&& (personalPacket.getEnDate().getTime() - now.getTime()) < before) {
				content = personalPacket.getUrlParameter();
				exeStr = format.format(now);
				try{
					httpPostResult = httpClientUtil.doPost(url_steal, httpHeadMap, charset, content);
				}catch (Exception e) {
					e.printStackTrace();
					return ;
				}
				endDateStr = format.format(personalPacket.getEnDate());
				printStr = "result=" + httpPostResult + ",nickname={" + personalPacket.getNickName() + "},exeDate={"
						+ exeStr + "},endDate={" + endDateStr + "},before={" + before + "},failcount={" + failCount
						+ "},flowerSizeNow={" + personalPackets.size() + "}";

				logger.info(printStr);

				if (httpPostResult == null) {
					personalPackets.remove(0);
					return;
				}

				// 调整时间，有保护的或没成熟，提前了，证明before要调整小
				if (httpPostResult.contains("这朵花有保护罩") || httpPostResult.contains("不足24小时")) {
					printStr = "Adjust after " + afterStep + "ms, too quick!";
					logger.info(printStr);
					adjust_after();
				}
				
				// 失败就计数，需要调整，连续maxFailcount次以上失败，证明慢了，要调整快点执行，只有没有保护套的才计数调整
				if (httpPostResult.contains("花已经被领取哟") && personalPacket.getNickName().contains("N-")) {
					failCount++;
					if (failCount >= maxFailcount) {
						printStr = "fail count >=" + maxFailcount +",Adjust before at least " + beforeStep + "ms, too slow! ";
						logger.info(printStr);
						adjust_before();
					}
				}

				if (httpPostResult.contains("{\"message\":\"1\"}")) {
					failCount = 0;
					if (before + 1 <= maxBefore) // 成功默认再提前1ms，小调整
						before = before + 1;
				}
				
				personalPackets.remove(0);
			}

			else {
				if (now.getTime() >= personalPacket.getEnDate().getTime())
					personalPackets.remove(0);
			}
		}
	}

	public void initHttpHead() {
		httpHeadMap.put("Host", "weixin.spdbccc.com.cn");
		httpHeadMap.put("Connection", "keep-alive");
		httpHeadMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		httpHeadMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpHeadMap.put("Origin", "https://weixin.spdbccc.com.cn");
		httpHeadMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat QBCore/3.43.691.400 QQBrowser/9.0.2524.400");
		httpHeadMap.put("X-Requested-With", "XMLHttpRequest");
		httpHeadMap.put("Referer", "Referer: https://weixin.spdbccc.com.cn/wxrp-page-steal/myRedPacket");
		httpHeadMap.put("Accept-Encoding", "gzip, deflate");
		httpHeadMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-us;q=0.6,en;q=0.5;q=0.4");
		// cookie init
		try {
			FileInputStream in = new FileInputStream("cookie.txt");
			InputStreamReader inReader;
			inReader = new InputStreamReader(in, "UTF-8");
			BufferedReader bufReader = new BufferedReader(inReader);
			String line = bufReader.readLine();
			httpHeadMap.put("Cookie", line);
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readLineFile(String filename) {
		try {
			FileInputStream in = new FileInputStream(filename);
			InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
			BufferedReader bufReader = new BufferedReader(inReader);
			String line = null;
			String[] strings = new String[4];
			while ((line = bufReader.readLine()) != null) {
				strings = line.split("\\|");
				PersonalPacket personalPacket = new PersonalPacket();
				personalPacket.setOpenid(strings[0]);
				// personalPacket.setEnDate(new Date(strings[1]));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date endDate = formatter.parse(strings[1]);
				personalPacket.setEnDate(endDate);
				personalPacket.setNickName(strings[2]);
				personalPacket.setUrlParameter(strings[3]);
				personalPackets.add(personalPacket);
			}
			bufReader.close();
			inReader.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("读取" + filename + "出错！");
		}
	}

	public void deletePast() {
		System.out.println("flower before delete past:" + personalPackets.size());
		ArrayList<PersonalPacket> personalPacketsTemp = new ArrayList<PersonalPacket>();
		for (PersonalPacket personalPacket : personalPackets) {
			personalPacketsTemp.add(personalPacket);
		}

		Date now = new Date();

		for (PersonalPacket personalPacket : personalPacketsTemp) {
			if (personalPacket.getEnDate().getTime() < now.getTime()) {
				personalPackets.remove(personalPacket);
			}
		}

		System.out.println("flower after delete past:" + personalPackets.size());
	}

	public void deleteConflict() {
		System.out.println("flower before delete conflic:" + personalPackets.size());

		for (int i = 1; i < personalPackets.size() - 1; ) {
			long pre_one = personalPackets.get(i - 1).getEnDate().getTime();
			long next_one = personalPackets.get(i + 1).getEnDate().getTime();
			long middle = personalPackets.get(i).getEnDate().getTime();
			if ((middle - pre_one) <= 2000 && (next_one - middle) > 2000) { //删除前者
				System.out.println("remove conflic flower:" + personalPackets.get(i - 1).getEnDate());
				personalPackets.remove(i - 1);
				//i不变，由于删除一个元素，i是往后走一位
			}
			else
			if ((middle - pre_one) <= 2000 && (next_one - middle) <= 2000) { //删除中间
				System.out.println("remove conflic flower:" + personalPackets.get(i).getEnDate());
				personalPackets.remove(i);
			}
			else { //不删除
				i++;
			}
		}

		System.out.println("flower after delete conflic:" + personalPackets.size());
	}

	public void decreaseBefore() {
		if ((before - afterStep) > 0)
			before = before - afterStep;
	}

	public void increaseBefore() {
		if ((before + beforeStep) <= maxBefore)
			before = before + beforeStep;
		else { // 超过maxBefore，重新设置为较小的值，即有可能太提前了，永远抢不到，防止before增加到最大，永远提前。
			before = resetValue;
		}
	}

	public void adjust_before() {
		// 先复位
		failCount = 0;
		increaseBefore();
	}

	public void adjust_after() {
		// 先复位
		failCount = 0;
		decreaseBefore();
	}

}
