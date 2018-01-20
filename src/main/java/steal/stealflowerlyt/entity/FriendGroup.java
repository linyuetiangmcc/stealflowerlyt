package steal.stealflowerlyt.entity;

import java.util.ArrayList;

public class FriendGroup {
	private int lean;
	private boolean success;
	private int pageNo;
	private int pageSum;
	private String openId = "oggN1jiZF8AthajXkotabFE6lbmk";
	private int totals;
	private ArrayList<Friend> list;
	public int getLean() {
		return lean;
	}
	public void setLean(int lean) {
		this.lean = lean;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSum() {
		return pageSum;
	}
	public void setPageSum(int pageSum) {
		this.pageSum = pageSum;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public int getTotals() {
		return totals;
	}
	public void setTotals(int totals) {
		this.totals = totals;
	}
	public ArrayList<Friend> getList() {
		return list;
	}
	public void setList(ArrayList<Friend> list) {
		this.list = list;
	}
}
