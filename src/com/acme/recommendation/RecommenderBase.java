package com.acme.recommendation;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;

public class RecommenderBase {

    public static DataModel getDataModel() throws TasteException {
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setURL("jdbc:mysql://localhost/drupal7?user=root&password=root");

        JDBCDataModel dm = new MySQLJDBCDataModel(dataSource, "rec_vote", "uid", "nid", "vote", null);
        DataModel reloadFromJDBCDataModel = new ReloadFromJDBCDataModel(dm);

        return reloadFromJDBCDataModel;
    }

}
