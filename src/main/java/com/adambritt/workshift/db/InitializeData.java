package com.adambritt.workshift.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface InitializeData {

	@SqlUpdate(""
			+ "merge into users (id, name) values(1, 'Adam');"
			+ "merge into users (id, name) values(2, 'Joe');"
			+ "merge into users (id, name) values(3, 'Maria');"
			+ ""
			+ "merge into shops (id, name) values(1, 'Bakery');"
			+ "merge into shops (id, name) values(2, 'Butcher');"
			+ "merge into shops (id, name) values(3, 'Clothing');"
			+ ""
			+ "merge into shifts (id, shopId, startTS, endTS) values (1, 1, '2023-09-23T10:00:00.000+02:00', '2023-09-23T18:00:00.000+02:00');"
			+ "merge into shifts (id, shopId, startTS, endTS) values (2, 1, '2023-09-24T10:00:00.000+02:00', '2023-09-24T18:00:00.000+02:00');"
			+ "merge into shifts (id, shopId, startTS, endTS) values (3, 2, '2023-09-23T10:00:00.000+02:00', '2023-09-23T18:00:00.000+02:00');"
			+ "merge into shifts (id, shopId, startTS, endTS) values (4, 2, '2023-09-24T10:00:00.000+02:00', '2023-09-24T18:00:00.000+02:00');"
			+ "merge into shifts (id, shopId, startTS, endTS) values (5, 3, '2023-09-23T10:00:00.000+02:00', '2023-09-23T18:00:00.000+02:00');"
			+ "merge into shifts (id, shopId, startTS, endTS) values (6, 3, '2023-09-24T10:00:00.000+02:00', '2023-09-24T18:00:00.000+02:00');"
			+ "")
	public void initializeData();
}
