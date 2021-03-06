package service;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;

import service.db.impl.PersonDaoImpl;

@SpringBootApplication
public class Application {

  final static Logger LOG = Logger.getLogger(Application.class);
  final static private String username = "spatialdb";
  final static private String password = "spatialad";
  final static private String
      connection =
       //"jdbc:postgresql://ec2-35-165-161-51.us-west-2.compute.amazonaws.com:5432/snowplow?user=power_user&password=$poweruserpassword";
      "jdbc:postgresql://ip-172-31-28-200.us-west-2.compute.internal:5432/snowplow?user=power_user&password=$poweruserpassword";
      // "jdbc:postgresql://geospatialdb.cqeod3cq54hm.us-west-2.rds.amazonaws.com:5432/geospatialdb?user=spatialdb&password=spatialad";
  public static Connection conn;
  //"jdbc:postgresql://shudhgvf:NPXQ1d2eVU32ouq7SfyGwcu9-C5m0Iug@elmer.db.elephantsql.com:5432/shudhgvf";

 /*
  final static private String username = "spatialdb";
  final static private String password = "spatialad";
  final static private String
      connection =
      "geospatialdb.cqeod3cq54hm.us-west-2.rds.amazonaws.com:5432";
       */

  public static Connection getConnection() {
    if (conn == null) {
      try {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(connection, username, password);
      } catch (Exception e) {
        LOG.error("error connecting to db", e);
      }
      return null;
    } else {
      return conn;
    }
  }

  public static void main(String[] args) {
    conn = getConnection();
    if (conn != null) {
      ApplicationContext ctx = SpringApplication.run(Application.class, args);
      PersonDaoImpl ob = new PersonDaoImpl();
      ob.getAllPersons();
      LOG.info("All users fetched");
    }
    LOG.debug("Let's inspect the beans provided by Spring Boot:");
    LOG.info("Application online");
        /*
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            LOG.debug(beanName);
        }
        */
  }

}
