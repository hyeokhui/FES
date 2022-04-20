/**
 * 
 */
package com.ezfarm.fes.elastic;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @Class Name : EzEsResultHits.java
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


public class EzEsResultHits {
	private String _index;
	private String _type;
	private String _id;
	private String _score;
	private String[] sort;
	private JsonObject _source;
	private JsonObject highlight;
	
	public String get_index() {
		return _index;
	}
	public void set_index(String _index) {
		this._index = _index;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_score() {
		return _score;
	}
	public void set_score(String _score) {
		this._score = _score;
	}
	public String[] getSort() {
		return sort;
	}
	public void setSort(String[] sort) {
		this.sort = sort;
	}
	public JsonObject get_source() {
		return _source;
	}
	public void set_source(JsonObject _source) {
		this._source = _source;
	}
	public <T> T getSource(int index, Class<T> clazz) throws Exception {
		Gson gson = new Gson();
		return gson.fromJson(_source.toString(), clazz);
	}
	public JsonObject getHighlight() {
		return highlight;
	}
	public void setHightlight(JsonObject highlight) {
		this.highlight = highlight;
	}
}
