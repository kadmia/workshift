package com.adambritt.workshift.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface TableRelationships {

	@SqlUpdate("create table if not exists shifts_users("
			+ "  shiftId bigint not null"
			+ ", userId bigint not null"
			+ ", constraint fk_shiftId_userId foreign key(shiftId) references shifts(id)"
			+ ", constraint fk_userId_shiftId foreign key(userId) references users(id)"
			+ ", constraint uq_shifts_users unique(shiftId, userId)"
			+ ");")
	void createShiftUserTable();

	@SqlUpdate("create table if not exists users_shops("
			+ "  userId bigint not null"
			+ ", shopId bigint not null"
			+ ", constraint fk_userId_shopId foreign key(userId) references users(id)"
			+ ", constraint fk_shopId_userId foreign key(shopId) references shops(id)"
			+ ", constraint uq_users_shops unique(userId, shopId)"
			+ ");")
	void createUserShopTable();
}
