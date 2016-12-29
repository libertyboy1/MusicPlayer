package com.view.media.db;

import android.os.Environment;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

public class DbManage {
	public static DbManager manager;

	public static void init() {

		DbManager.DaoConfig daoConfig = new DaoConfig()
		// 数据库的名字
				.setDbName("Media.db")
				// 保存到指定路径
//				.setDbDir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.view.media/"))
				// 数据库的版本号
				.setDbVersion(1)
				// 数据库版本更新监听
				.setDbUpgradeListener(new DbUpgradeListener() {
					@Override
					public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//						Logger.myLog().e("数据库版本更新了！");
					}
				});
		manager = x.getDb(daoConfig);

	}

	public static void DropTable(Class<?> name) {
		try {
			manager.dropTable(name);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

}
