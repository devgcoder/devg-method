package com.github.devgcoder.devgmethod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author duheng
 * @Date 2021/4/27 19:14
 */
@JsonIgnoreProperties
public class DevgMethodValue {
	// 任务名称,对应methodAnnotatedName
	private String name;

	// 描述
	private String desc;

	// bean名称,不需要存redis
	@JsonIgnore
	private String beanName;

	// 是否正在执行  (0-闲置，1-正在运行,2=运行失败)
	private byte runningState;
  // 上一次开始时间
	private String lastStartTime;
  // 上一次结束时间
	private String lastEndTime;
	// 上次运行时长
	private long lastDuration;
	// 本次运行开始时间
	private String theRunningTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(String lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public String getLastEndTime() {
		return lastEndTime;
	}

	public void setLastEndTime(String lastEndTime) {
		this.lastEndTime = lastEndTime;
	}

	public String getTheRunningTime() {
		return theRunningTime;
	}

	public void setTheRunningTime(String theRunningTime) {
		this.theRunningTime = theRunningTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public byte getRunningState() {
		return runningState;
	}

	public void setRunningState(byte runningState) {
		this.runningState = runningState;
	}

	public long getLastDuration() {
		return lastDuration;
	}

	public void setLastDuration(long lastDuration) {
		this.lastDuration = lastDuration;
	}
}
