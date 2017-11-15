package com.beifengorm.dao;

import java.util.List;

public interface BeifengDAO {

	public void save(Object o);
	public void update(Object o);
	public void del(Object o);
	public Object query(String bql);
	@SuppressWarnings("rawtypes")
	public List queryList(String bql);
}
