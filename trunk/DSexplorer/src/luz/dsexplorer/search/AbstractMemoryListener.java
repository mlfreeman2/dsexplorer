package luz.dsexplorer.search;

import luz.dsexplorer.winapi.api.MemoryListener;
import luz.dsexplorer.winapi.api.Result;
import luz.dsexplorer.winapi.api.ResultList;

import com.sun.jna.Memory;


public abstract class AbstractMemoryListener implements MemoryListener {
	protected ResultList results;
	protected int overlapping;

	public void add(Result result) {
		results.add(result);
	}
	
	public ResultList getResultList() {
		return results;
	}
	
	/**
	 * To optimize memory reading, the reading algorithm divides large memory areas into
	 * smaller ones. This value forces the reading algorithm to overlap these memory areas by
	 * the given value. This is usefull if the seach pattern is longer than one Byte. In this case, 
	 * the MemoryListener has only to worry about one area at each time. Values at the end of an 
	 * area will repeat at the beginning of the next area. In general this value should be 
	 * 'size of the search pattern -1'.
	 * @param overlapping
	 */
	public int getOverlapping(){
		return overlapping;
	}
		
	public abstract void init(ResultList results, String value);
	
	public abstract void mem(Memory outputBuffer, long address, long size);

}
