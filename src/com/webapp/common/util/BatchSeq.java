package com.webapp.common.util;

import com.webapp.common.dao.SysSeqDAO;

public class BatchSeq {
	private String seqName = null;
	private long nowValue = 0;
	private long maxValue = 0;
	private long step = 1;//seq预占用的步长
	private SysSeqDAO seqDAO = null;
	
	public BatchSeq(){
	}
	public BatchSeq(SysSeqDAO seqDAO){
		this.seqDAO = seqDAO;
	}
	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public long getNowValue() {
		return nowValue;
	}

	public void setNowValue(long nowValue) {
		this.nowValue = nowValue;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}

	
	
	public synchronized long getNewId() throws Exception{
		if (nowValue==0){
			BatchSeq newseq = seqDAO.getBatchSeq(seqName);
			nowValue = newseq.getNowValue();
			maxValue = newseq.getMaxValue();
			
			return nowValue;
		}else if (nowValue==maxValue){
			BatchSeq newseq = seqDAO.getBatchSeq(seqName);
			nowValue = newseq.getNowValue();
			maxValue = newseq.getMaxValue();
			
			return nowValue;
		}else{
			nowValue ++;
			return nowValue;
		}
	}
}
