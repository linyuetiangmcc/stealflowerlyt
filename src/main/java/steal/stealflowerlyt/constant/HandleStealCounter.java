package steal.stealflowerlyt.constant;

public class HandleStealCounter {
	public static int before = 1258;  //提前多少ms发包
	public static int beforeStep = 20; //失败maxFailcount（没有保护套）提前时间ms
	public static int afterStep = 25;  //有保护套，就延后的时间ms
	public static int resetValue = 800; //before设置为默认值ms
	public static int maxBefore = 1450; //before最大值ms
	public static int maxFailcount = 3; //失败次数，达到就往前调整
	public static int successStep = 1;  //成功时默认往前时间，小调整ms
}
