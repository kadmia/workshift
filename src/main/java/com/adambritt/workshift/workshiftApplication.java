package com.adambritt.workshift;

import org.jdbi.v3.core.Jdbi;

import com.adambritt.workshift.db.InitializeData;
import com.adambritt.workshift.db.ShiftDao;
import com.adambritt.workshift.db.ShopDao;
import com.adambritt.workshift.db.TableRelationships;
import com.adambritt.workshift.db.UserDao;
import com.adambritt.workshift.resources.ShiftResource;
import com.adambritt.workshift.resources.ShopResource;
import com.adambritt.workshift.resources.UserResource;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;

public class workshiftApplication extends Application<workshiftConfiguration> {

    public static void main(final String[] args) throws Exception {
        new workshiftApplication().run(args);
    }

    @Override
    public String getName() {
        return "workshift";
    }

    @Override
    public void initialize(final Bootstrap<workshiftConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final workshiftConfiguration configuration, final Environment environment) {
    	final JdbiFactory jdbiFactory = new JdbiFactory();
    	final Jdbi jdbi = jdbiFactory.build(environment, configuration.getDataSourceFactory(), "h2");
    	
    	final UserDao userDao = jdbi.onDemand(UserDao.class);
    	userDao.createUserTable();
    	environment.jersey().register(new UserResource(userDao));

    	final ShopDao shopDao = jdbi.onDemand(ShopDao.class);
    	shopDao.createShopTable();
    	environment.jersey().register(new ShopResource(shopDao));

    	final ShiftDao shiftDao = jdbi.onDemand(ShiftDao.class);
    	shiftDao.createShiftTable();
    	environment.jersey().register(new ShiftResource(shiftDao));
    	
    	final TableRelationships tableRelationshipsDao = jdbi.onDemand(TableRelationships.class);
    	tableRelationshipsDao.createShiftUserTable();
    	tableRelationshipsDao.createUserShopTable();
    	
    	//Note: this will either insert or overwrite data
    	final InitializeData initializeData = jdbi.onDemand(InitializeData.class);
    	initializeData.initializeData();
    }

}
