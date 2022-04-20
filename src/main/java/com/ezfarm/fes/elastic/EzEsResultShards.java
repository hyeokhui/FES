/**
 * 
 */
package com.ezfarm.fes.elastic;

/**
 * @Class Name : EzEsResultShards.java
 * @Description : 
 * @Modification Information
 *
 * @author 
 * @since 
 * @version 1.0
 * @see
 *
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -----------------------------------------
 */

public class EzEsResultShards {
	private long total;
	private long successful;
	private long skipped;
	private long failed;
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getSuccessful() {
		return successful;
	}
	public void setSuccessful(long successful) {
		this.successful = successful;
	}
	public long getSkipped() {
		return skipped;
	}
	public void setSkipped(long skipped) {
		this.skipped = skipped;
	}
	public long getFailed() {
		return failed;
	}
	public void setFailed(long failed) {
		this.failed = failed;
	}
}
